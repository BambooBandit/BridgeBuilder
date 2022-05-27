package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.FloatArray;
import com.bamboo.bridgebuilder.BoxSelect;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.commands.*;
import com.bamboo.bridgebuilder.ui.InstanceOrSpriteToolDialog;
import com.bamboo.bridgebuilder.ui.SnapSpriteDialog;
import com.bamboo.bridgebuilder.ui.fileMenu.Tools;
import com.bamboo.bridgebuilder.ui.propertyMenu.PropertyToolPane;
import com.bamboo.bridgebuilder.ui.spriteMenu.LayerOverrideDialog;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;


public class MapInput implements InputProcessor
{
    public Map map;
    public BridgeBuilder editor;

    public Vector2 dragOriginPos;
    public Vector2 dragDifferencePos; // Used to retrieve position difference of mouse drag
    public Vector2 currentPos;

    public FloatArray mapPolygonVertices; // allows for seeing where you are clicking when constructing a new MapObject polygon
    public FloatArray stairVertices; // allows for seeing where you are clicking when constructing a new set of stairs
    public Vector2 objectVerticePosition;
    public Vector2 stairVerticePosition;

    // Null if not currently drag/moving any layer child
    public MoveMapSpriteOffset moveMapSpriteOffset;
    public MoveMapSprites moveMapSprites;
    public RotateMapSprites rotateMapSprites;
    public ScaleMapSprites scaleMapSprites;
    public MoveMapObjects moveMapObjects;
    public MovePolygonVertice movePolygonVertice;

    public float gradientX, gradientY; // Used for gradient placement
    public boolean draggingGradient = false;

    public LayerChild snapFromThisObject = null; // Hold alt while having one object selected, you can snap to another object

    public SpriteLayer overrideLayer; // Hold alt while having one layer selected, you can snap to a mapsprite in a secondary selected layer.

    public BoxSelect boxSelect;

    public MapInput(BridgeBuilder editor, Map map)
    {
        this.editor = editor;
        this.map = map;

        this.dragOriginPos = new Vector2();
        this.dragDifferencePos = new Vector2();
        this.currentPos = new Vector2();

        this.mapPolygonVertices = new FloatArray();
        this.objectVerticePosition = new Vector2();

        this.stairVertices = new FloatArray();
        this.stairVerticePosition = new Vector2();

        this.boxSelect = new BoxSelect(map);
    }

    @Override
    public boolean keyDown(int keycode)
    {
        handleEdgeSnapKeyDown(keycode);
        handleLayerOverrideKeyDown(keycode);
        return false;
    }

    private void handleEdgeSnapKeyDown(int keycode)
    {
        if(map.selectedObjects.size > 0 && map.selectedSprites.size > 0)
            return;
        if(!(keycode == Input.Keys.ALT_LEFT && (map.selectedSprites.size == 1 || map.selectedObjects.size == 1) && map.editor.fileMenu.toolPane.select.selected))
            return;
        if(this.map.selectedSprites.size == 1)
            snapFromThisObject = map.selectedSprites.first();
        else
            snapFromThisObject = map.selectedObjects.first();
    }

    private void handleLayerOverrideKeyDown(int keycode)
    {
        if(map.selectedObjects.size > 0 || map.selectedSprites.size > 0)
            return;
        if(!(keycode == Input.Keys.ALT_LEFT && map.editor.fileMenu.toolPane.select.selected))
            return;
        if(!(map.selectedLayer instanceof SpriteLayer))
            return;
        if(map.secondarySelectedLayer == null)
            return;

        overrideLayer = (SpriteLayer) map.selectedLayer;
    }

    private void handleEdgeSnapKeyUp(int keycode)
    {
        if(keycode != Input.Keys.ALT_LEFT)
            return;
        snapFromThisObject = null;
    }

    private void handleLayerOverrideKeyUp(int keycode)
    {
        if(keycode != Input.Keys.ALT_LEFT)
            return;

        overrideLayer = null;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        handleEdgeSnapKeyUp(keycode);
        handleLayerOverrideKeyUp(keycode);
        return false;
    }

    @Override
    public boolean keyTyped(char character)
    {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        try
        {
            this.map.stage.unfocusAll();
            this.editor.stage.unfocusAll();

            Vector3 coords = Utils.unproject(this.map.camera, screenX, screenY);
            coords.x += map.cameraX;
            coords.y += map.cameraY;
            this.currentPos.set(coords.x, coords.y);
            this.dragOriginPos.set(coords.x, coords.y);


            if(handleMapSpriteCreation(coords.x, coords.y, button))
                return false;
            if(handleManipulatorBoxMoveLayerChildTouchDown(coords.x, coords.y, button))
                return false;
            if(handleManipulatorBoxMoveVerticeTouchDown(coords.x, coords.y, button))
                return false;
            if(handleSelect(button))
                return false;
            if(handleBoxSelectTouchDown(coords.x, coords.y, button))
                return false;
            if(handleMapPointCreation(coords.x, coords.y, button))
                return false;
            if(handleMapPolygonVerticeCreation(coords.x, coords.y, button))
                return false;
            if(handleStairVerticeCreation(coords.x, coords.y, button))
                return false;
            if(handleMapPolygonRectangleCreation(button))
                return false;
            if(handleMapPolygonCreation(button))
                return false;
            if(handleStairsCreation(button))
                return false;
            if(handlePolygonVertexSelection(button))
                return false;
            if(handleGradientStart(coords.x, coords.y, button))
                return false;
            if(applyGradient(coords.x, coords.y, button))
                return false;
        } catch(Exception e){
            this.editor.crashRecovery(e);
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        try
        {
            handleManipulatorBoxTouchUp();
            handleBoxSelectTouchUp();
        } catch(Exception e){
            this.editor.crashRecovery(e);
        }

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        try
        {

            Vector3 coords = Utils.unproject(this.map.camera, screenX, screenY);
            coords.x += map.cameraX;
            coords.y += map.cameraY;
            this.currentPos.set(coords.x, coords.y);
            this.dragDifferencePos.set(coords.x, coords.y);
            this.dragDifferencePos = this.dragDifferencePos.sub(this.dragOriginPos.x, this.dragOriginPos.y);
            handleManipulatorBoxDrag(this.dragDifferencePos, this.currentPos);
            handleBoxSelectDrag(coords.x, coords.y);
            handleCameraDrag();
            this.dragOriginPos.set(coords.x - dragDifferencePos.x, coords.y - dragDifferencePos.y);

        } catch(Exception e){
            this.editor.crashRecovery(e);
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
        try
        {
            Vector3 coords = Utils.unproject(this.map.camera, screenX, screenY);
            float coordsX = coords.x + map.cameraX;
            float coordsY = coords.y + map.cameraY;
            String xCoord = String.format("%.2f", coordsX);
            String yCoord = String.format("%.2f", coordsY);
            editor.mouseCoordTooltip.label.setText("(" + xCoord + ", " + yCoord  +")   (" + screenX + ", " + screenY + ")");
            if(editor.fileMenu.toolPane.fence.selected && map.lastFencePlaced != null)
            {
                float currentDistance = Utils.getDistance(map.lastFencePlaced.x + map.lastFencePlaced.width / 2f, coordsX, (map.lastFencePlaced.y + map.lastFencePlaced.height / 2f), coordsY);
                editor.fenceDistanceTooltip.label.setText("(Fence Distance) last: " + (Math.round(map.lastFencePlacedDistance * 100.0) / 100.0) + ". current: " + (Math.round(currentDistance * 100.0) / 100.0));
            }
            this.currentPos.set(coordsX, coordsY);
            handlePreviewSpritePositionUpdate(coordsX, coordsY);
            handleHoveredLayerChildUpdate(coordsX, coordsY);
            handleManipulatorBoxHover(coordsX, coordsY);
            handleSelectedPolygonVerticeHover(coordsX, coordsY);
            updateGradientPreview(coordsX, coordsY);
        } catch(Exception e){
            this.editor.crashRecovery(e);
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount)
    {
        try
        {
            handleCameraZoom(amount);
        } catch(Exception e){
            this.editor.crashRecovery(e);
        }
        return false;
    }

    private boolean handleManipulatorBoxMoveLayerChildTouchDown(float x, float y, int button)
    {
        if((button != Input.Buttons.LEFT || !Utils.isFileToolThisType(this.editor, Tools.SELECT)))
            return false;

        for(int i = 0; i < this.map.selectedSprites.size; i++)
        {
            MapSprite selectedSprite = this.map.selectedSprites.get(i);
            if(selectedSprite.moveBox.contains(x, y))
            {
                this.moveMapSprites = new MoveMapSprites(x, y, this.map.selectedSprites);
                this.map.pushCommand(this.moveMapSprites);
                return true;
            }
            else if(selectedSprite.rotationBox.contains(x, y))
            {
                this.rotateMapSprites = new RotateMapSprites(this.map.selectedSprites);
                this.map.pushCommand(this.rotateMapSprites);
                return true;
            }
            else if(selectedSprite.scaleBox.contains(x, y))
            {
                this.scaleMapSprites = new ScaleMapSprites(this.map.selectedSprites);
                this.map.pushCommand(this.scaleMapSprites);
                return true;
            }
            else if(map.selectedSprites.size == 1)
            {
                if(selectedSprite.offsetMovebox1.contains(x, y))
                {
                    this.moveMapSpriteOffset = new MoveMapSpriteOffset(this.map.selectedSprites.first(), MoveMapSpriteOffset.Location.ONE);
                    this.map.pushCommand(this.moveMapSpriteOffset);
                    return true;
                }
                else if(selectedSprite.offsetMovebox2.contains(x, y))
                {
                    this.moveMapSpriteOffset = new MoveMapSpriteOffset(this.map.selectedSprites.first(), MoveMapSpriteOffset.Location.TWO);
                    this.map.pushCommand(this.moveMapSpriteOffset);
                    return true;
                }
                else if(selectedSprite.offsetMovebox3.contains(x, y))
                {
                    this.moveMapSpriteOffset = new MoveMapSpriteOffset(this.map.selectedSprites.first(), MoveMapSpriteOffset.Location.THREE);
                    this.map.pushCommand(this.moveMapSpriteOffset);
                    return true;
                }
                else if(selectedSprite.offsetMovebox4.contains(x, y))
                {
                    this.moveMapSpriteOffset = new MoveMapSpriteOffset(this.map.selectedSprites.first(), MoveMapSpriteOffset.Location.FOUR);
                    this.map.pushCommand(this.moveMapSpriteOffset);
                    return true;
                }
            }
        }

        for(int i = 0; i < this.map.selectedObjects.size; i++)
        {
            MapObject selectedObjects = this.map.selectedObjects.get(i);
            if(selectedObjects.moveBox.contains(x, y))
            {
                this.moveMapObjects = new MoveMapObjects(this.map.selectedObjects);
                this.map.pushCommand(this.moveMapObjects);
                return true;
            }
        }

        return false;
    }

    private boolean handleManipulatorBoxMoveVerticeTouchDown(float x, float y, int button)
    {
        if(button != Input.Buttons.LEFT && !Utils.isFileToolThisType(this.editor, Tools.OBJECTVERTICESELECT))
            return false;

        for(int i = 0; i < this.map.selectedObjects.size; i++)
        {
            MapObject mapObject = map.selectedObjects.get(i);
            if(mapObject instanceof MapPolygon)
            {
                MapPolygon mapPolygon = (MapPolygon) mapObject;
                if (mapPolygon.moveBox.contains(x, y) && mapPolygon.indexOfSelectedVertice != -1)
                {
                    this.movePolygonVertice = new MovePolygonVertice(mapPolygon, x, y, mapPolygon.polygon.getTransformedVertices()[mapPolygon.indexOfSelectedVertice] + map.cameraX, mapPolygon.polygon.getTransformedVertices()[mapPolygon.indexOfSelectedVertice + 1] + map.cameraY);
                    this.map.pushCommand(this.movePolygonVertice);
                    return true;
                }
            }
        }

        return false;
    }

    private boolean handleManipulatorBoxTouchUp()
    {
        this.moveMapSprites = null;
        this.moveMapSpriteOffset = null;
        this.rotateMapSprites = null;
        this.scaleMapSprites = null;
        this.moveMapObjects = null;
        this.movePolygonVertice = null;
        return false;
    }

    private boolean handleManipulatorBoxDrag(Vector2 dragAmount, Vector2 dragCurrentPos)
    {
        if(this.moveMapSprites != null)
            this.moveMapSprites.update(dragCurrentPos.x, dragCurrentPos.y);
        else if(this.moveMapObjects != null)
        {
            MapSprite parent = this.moveMapObjects.originalMapObjectPosition.iterator().next().key.attachedSprite;
            if(parent != null)
            {
                float scale = parent.scale;
                this.moveMapObjects.update(dragAmount.x / scale, dragAmount.y / scale);
            }
            else
                this.moveMapObjects.update(dragAmount.x, dragAmount.y);
        }
        else if(this.moveMapSpriteOffset != null)
            this.moveMapSpriteOffset.update(dragAmount.x, dragAmount.y);
        else if(this.movePolygonVertice != null)
        {
            // Magnet. Snap the vertice next to the nearest vertice less than .35 units
            if(Gdx.input.isKeyPressed(Input.Keys.S))
            {
                float units = .35f;
                float smallestDistance = units;
                float smallestDistanceX = 0;
                float smallestDistanceY = 0;
                for(int i = 0; i < map.layers.size; i ++)
                {
                    Layer layer = map.layers.get(i);
                    if(layer instanceof ObjectLayer)
                    {
                        ObjectLayer objectLayer = (ObjectLayer) layer;
                        for(int k = 0; k < objectLayer.children.size; k ++)
                        {
                            MapObject object = objectLayer.children.get(k);
                            if(object instanceof MapPoint)
                                continue;
                            MapPolygon mapPolygon = (MapPolygon) object;
                            float[] vertices = mapPolygon.polygon.getTransformedVertices();
                            for(int s = 0; s < vertices.length; s += 2)
                            {
                                float verticeX = vertices[s];
                                float verticeY = vertices[s + 1];
                                float distance = Utils.getDistance(verticeX, dragCurrentPos.x, verticeY, dragCurrentPos.y);
                                if(distance < smallestDistance)
                                {
                                    smallestDistance = distance;
                                    smallestDistanceX = verticeX;
                                    smallestDistanceY = verticeY;
                                }
                            }
                        }
                    }
                    else if(layer instanceof SpriteLayer)
                    {
                        SpriteLayer spriteLayer = (SpriteLayer) layer;
                        for(int k = 0; k < spriteLayer.children.size; k ++)
                        {
                            MapSprite mapSprite = spriteLayer.children.get(k);
                            if(mapSprite.attachedMapObjects == null)
                                continue;
                            for(int q = 0; q < mapSprite.attachedMapObjects.size; q++)
                            {
                                MapObject object = mapSprite.attachedMapObjects.get(q);
                                if(object instanceof MapPoint)
                                    continue;
                                MapPolygon mapPolygon = (MapPolygon) object;
                                float[] vertices = mapPolygon.polygon.getTransformedVertices();
                                for (int s = 0; s < vertices.length; s += 2)
                                {
                                    float verticeX = vertices[s];
                                    float verticeY = vertices[s + 1];
                                    float distance = Utils.getDistance(verticeX, dragCurrentPos.x, verticeY, dragCurrentPos.y);
                                    if (distance < smallestDistance)
                                    {
                                        smallestDistance = distance;
                                        smallestDistanceX = verticeX;
                                        smallestDistanceY = verticeY;
                                    }
                                }
                            }
                        }
                    }
                }
                if(smallestDistance != units)
                {
                    dragCurrentPos.x = smallestDistanceX;
                    dragCurrentPos.y = smallestDistanceY;
                }
            }

            this.movePolygonVertice.update(dragCurrentPos.x, dragCurrentPos.y);
        }
        else if(this.rotateMapSprites != null)
            this.rotateMapSprites.update((this.dragOriginPos.angle(dragCurrentPos) / map.camera.zoom) * 10);
        else if(this.scaleMapSprites != null)
        {
            float amountUp = dragCurrentPos.y - this.dragOriginPos.y;
            float amountRight = dragCurrentPos.x - this.dragOriginPos.x;
            this.scaleMapSprites.update(amountUp + amountRight);
        }
        return false;
    }

    private boolean handleHoveredLayerChildUpdate(float x, float y)
    {
        if(Utils.isFileToolThisType(this.editor, Tools.SELECT))
        {
            for (int i = 0; i < map.selectedSprites.size; i++)
            {
                MapSprite mapSprite = map.selectedSprites.get(i);
                if (mapSprite.attachedMapObjects != null)
                {
                    for (int k = 0; k < mapSprite.attachedMapObjects.size; k++)
                    {
                        MapObject mapObject = mapSprite.attachedMapObjects.get(k);
                        if (mapObject.isHoveredOver(x, y))
                        {
                            this.map.hoveredChild = mapObject;
                            return false;
                        }
                    }
                }
            }
            if ((this.map.secondarySelectedLayer == null || !Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) && this.map.selectedLayer != null)
            {
                for (int i = this.map.selectedLayer.children.size - 1; i >= 0; i--)
                {
                    LayerChild layerChild = (LayerChild) this.map.selectedLayer.children.get(i);
                    if (layerChild.isHoveredOver(x, y))
                    {
                        this.map.hoveredChild = layerChild;
                        return false;
                    }
                    if (map.editor.fileMenu.toolPane.selectAttachedSprites.selected && layerChild instanceof MapSprite)
                    {
                        MapSprite mapSprite = (MapSprite) layerChild;
                        if (mapSprite.attachedSprites != null)
                        {
                            for (int k = mapSprite.attachedSprites.children.size - 1; k >= 0; k--)
                            {
                                MapSprite attachedSprite = mapSprite.attachedSprites.children.get(k);
                                if (attachedSprite.isHoveredOver(x, y))
                                {
                                    this.map.hoveredChild = attachedSprite;
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
            if (this.map.secondarySelectedLayer != null)
            {
                for (int i = this.map.secondarySelectedLayer.children.size - 1; i >= 0; i--)
                {
                    LayerChild layerChild = (LayerChild) this.map.secondarySelectedLayer.children.get(i);
                    if (layerChild.isHoveredOver(x, y))
                    {
                        this.map.hoveredChild = layerChild;
                        return false;
                    }
                    if (map.editor.fileMenu.toolPane.selectAttachedSprites.selected && layerChild instanceof MapSprite)
                    {
                        MapSprite mapSprite = (MapSprite) layerChild;
                        if (mapSprite.attachedSprites != null)
                        {
                            for (int k = mapSprite.attachedSprites.children.size - 1; k >= 0; k--)
                            {
                                MapSprite attachedSprite = mapSprite.attachedSprites.children.get(k);
                                if (attachedSprite.isHoveredOver(x, y))
                                {
                                    this.map.hoveredChild = attachedSprite;
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        this.map.hoveredChild = null;
        return false;
    }

    private void handleManipulatorBoxHover(float x, float y)
    {
        for(int i = 0; i < this.map.selectedSprites.size; i ++)
        {
            MapSprite mapSprite = this.map.selectedSprites.get(i);
            mapSprite.moveBox.hover(mapSprite.moveBox.contains(x, y));
            mapSprite.rotationBox.hover(mapSprite.rotationBox.contains(x, y));
            mapSprite.scaleBox.hover(mapSprite.scaleBox.contains(x, y));
        }
        if(this.map.selectedSprites.size == 1)
        {
            MapSprite mapSprite = this.map.selectedSprites.first();
            mapSprite.offsetMovebox1.hover(mapSprite.offsetMovebox1.contains(x, y));
            mapSprite.offsetMovebox2.hover(mapSprite.offsetMovebox2.contains(x, y));
            mapSprite.offsetMovebox3.hover(mapSprite.offsetMovebox3.contains(x, y));
            mapSprite.offsetMovebox4.hover(mapSprite.offsetMovebox4.contains(x, y));

        }
        for(int i = 0; i < this.map.selectedObjects.size; i ++)
        {
            MapObject mapObject = this.map.selectedObjects.get(i);
            mapObject.moveBox.hover(mapObject.moveBox.contains(x, y));
        }
    }

    private void handleSelectedPolygonVerticeHover(float x, float y)
    {
        if(!Utils.isFileToolThisType(this.editor, Tools.OBJECTVERTICESELECT))
            return;
        x -= map.cameraX;
        y -= map.cameraY;
        boolean vertexFound = false;
        for(int i = 0; i < this.map.selectedObjects.size; i ++)
        {
            MapObject mapObject = this.map.selectedObjects.get(i);
            if(mapObject instanceof MapPolygon)
            {
                MapPolygon mapPolygon = (MapPolygon) mapObject;
                for(int k = 0; k < mapPolygon.polygon.getTransformedVertices().length; k += 2)
                {
                    float verticeX = mapPolygon.polygon.getTransformedVertices()[k];
                    float verticeY = mapPolygon.polygon.getTransformedVertices()[k + 1];
                    double distance = Math.sqrt(Math.pow((x - verticeX), 2) + Math.pow((y - verticeY), 2));
                    if (distance <= .25f * map.camera.zoom && !vertexFound)
                    {
                        vertexFound = true;
                        mapPolygon.indexOfHoveredVertice = k;
                        break;
                    }
                    else
                        mapPolygon.indexOfHoveredVertice = -1;
                }
            }
        }
    }

    private void updateGradientPreview(float x, float y)
    {
        if(!draggingGradient)
            return;
        float fromR = map.editor.fileMenu.toolPane.gradientDialog.getFromR();
        float fromG = map.editor.fileMenu.toolPane.gradientDialog.getFromG();
        float fromB = map.editor.fileMenu.toolPane.gradientDialog.getFromB();
        float fromA = map.editor.fileMenu.toolPane.gradientDialog.getFromA();
        float toR = map.editor.fileMenu.toolPane.gradientDialog.getToR();
        float toG = map.editor.fileMenu.toolPane.gradientDialog.getToG();
        float toB = map.editor.fileMenu.toolPane.gradientDialog.getToB();
        float toA = map.editor.fileMenu.toolPane.gradientDialog.getToA();
        float fromX = this.gradientX;
        float fromY = this.gradientY;

        float toX = x;
        float toY = y;
        float angle = Utils.degreeAngleFix(90 - Utils.getAngleDegree(fromX, fromY, toX, toY));
        this.map.camera.rotate(angle);
        this.map.camera.update();

        float projectedFromY = Utils.project(this.map.camera, fromX, fromY).y;
        float projectedToY = Utils.project(this.map.camera, toX, toY).y;

        for(int i = 0; i < map.selectedSprites.size; i ++)
        {
            MapSprite mapSprite = map.selectedSprites.get(i);
            float projY = Utils.project(this.map.camera, mapSprite.getX(), mapSprite.getY()).y;
            float norm = MathUtils.norm(projectedFromY, projectedToY, projY);
            float newR = MathUtils.lerp(fromR, toR, norm);
            float newG = MathUtils.lerp(fromG, toG, norm);
            float newB = MathUtils.lerp(fromB, toB, norm);
            float newA = MathUtils.lerp(fromA, toA, norm);
            if(fromR < 0 || toR < 0)
                newR = mapSprite.sprite.getColor().r;
            if(fromG < 0 || toG < 0)
                newG = mapSprite.sprite.getColor().g;
            if(fromB < 0 || toB < 0)
                newB = mapSprite.sprite.getColor().b;
            if(fromA < 0 || toA < 0)
                newA = mapSprite.sprite.getColor().a;
            mapSprite.sprite.setColor(newR, newG, newB, newA);
        }

        this.map.camera.rotate(-angle);
        this.map.camera.update();
    }

    private boolean handleMapSpriteCreation(float x, float y, int button)
    {
        if(!Utils.isFileToolThisType(this.editor, Tools.BRUSH) || this.map.selectedLayer == null || !(this.map.selectedLayer instanceof SpriteLayer) || button != Input.Buttons.LEFT)
            return false;
        if(this.map.spriteMenu.selectedSpriteTools.size == 0)
            return false;

        if(Command.shouldExecute(map, DrawFence.class))
        {
            if(!editor.fileMenu.toolPane.splat.selected)
            {
                DrawFence drawFence;
//                if(Utils.doesLayerHavePerspective(map, map.selectedLayer))
//                {
//                    Perspective perspective = ((SpriteLayer) map.selectedLayer).perspective;
//                    Vector3 project = perspective.projectWorldToPerspective(x, y);
//                    drawFence = new DrawFence(this.map, (SpriteLayer) this.map.selectedLayer, project.x, project.y);
//                }
//                else
                    drawFence = new DrawFence(this.map, (SpriteLayer) this.map.selectedLayer, x, y);
                this.map.executeCommand(drawFence);
                return true;
            }
            else
            {
                int randomSpawnAmount = Utils.randomInt(editor.fileMenu.toolPane.splatDialog.getMinSpawn(), editor.fileMenu.toolPane.splatDialog.getMaxSpawn());
                DrawFence drawFence = null;
                for(int i = 0; i < randomSpawnAmount; i ++)
                {
                    float randomX = Utils.randomFloat(x - editor.fileMenu.toolPane.splatDialog.getMaxXDisplacement(), x + editor.fileMenu.toolPane.splatDialog.getMaxXDisplacement());
                    float randomY = Utils.randomFloat(y - editor.fileMenu.toolPane.splatDialog.getMaxYDisplacement(), y + editor.fileMenu.toolPane.splatDialog.getMaxYDisplacement());
                    if(i == 0)
                        drawFence = new DrawFence(this.map, (SpriteLayer) this.map.selectedLayer, randomX, randomY);
                    else
                        drawFence.addCommandToChain(new DrawFence(this.map, (SpriteLayer) this.map.selectedLayer, randomX, randomY));
                }
                if(randomSpawnAmount > 0)
                {
                    this.map.executeCommand(drawFence);
                    return true;
                }
            }
        }
        else if(Command.shouldExecute(map, DrawMapSprite.class))
        {
            if(!editor.fileMenu.toolPane.splat.selected)
            {
                DrawMapSprite drawMapSprite;
                if(Utils.doesLayerHavePerspective(this.map, this.map.selectedLayer))
                {
                    Perspective perspective = this.map.selectedLayer.perspective;
                    Vector3 projector = Utils.project(map.camera, x - map.cameraX, y - map.cameraY);
                    Vector3 unprojector = perspective.projectScreenToPerspective(projector.x, Gdx.graphics.getHeight() - projector.y);
                    drawMapSprite = new DrawMapSprite(this.map, (SpriteLayer) this.map.selectedLayer, unprojector.x, unprojector.y);
                }
                else
                    drawMapSprite = new DrawMapSprite(this.map, (SpriteLayer) this.map.selectedLayer, x, y);
                this.map.executeCommand(drawMapSprite);
                return true;
            }
            else
            {
                int randomSpawnAmount = Utils.randomInt(editor.fileMenu.toolPane.splatDialog.getMinSpawn(), editor.fileMenu.toolPane.splatDialog.getMaxSpawn());
                DrawMapSprite drawMapSprite = null;
                for(int i = 0; i < randomSpawnAmount; i ++)
                {
                    float randomX = Utils.randomFloat(x - editor.fileMenu.toolPane.splatDialog.getMaxXDisplacement(), x + editor.fileMenu.toolPane.splatDialog.getMaxXDisplacement());
                    float randomY = Utils.randomFloat(y - editor.fileMenu.toolPane.splatDialog.getMaxYDisplacement(), y + editor.fileMenu.toolPane.splatDialog.getMaxYDisplacement());
                    if(i == 0)
                        drawMapSprite = new DrawMapSprite(this.map, (SpriteLayer) this.map.selectedLayer, randomX, randomY);
                    else
                        drawMapSprite.addCommandToChain(new DrawMapSprite(this.map, (SpriteLayer) this.map.selectedLayer, randomX, randomY));
                }
                if(randomSpawnAmount > 0)
                {
                    this.map.executeCommand(drawMapSprite);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean handleSelect(int button)
    {
        if(!Utils.isFileToolThisType(this.editor, Tools.SELECT) || this.map.selectedLayer == null || button != Input.Buttons.LEFT)
            return false;

        if(this.snapFromThisObject != null && this.map.hoveredChild != this.snapFromThisObject && !(this.map.hoveredChild instanceof MapObject))
        {
            new SnapSpriteDialog(editor.stage, map.skin, map, this.snapFromThisObject, this.map.hoveredChild);
            return true;
        }
        if(this.snapFromThisObject != null && this.map.hoveredChild != this.snapFromThisObject && this.snapFromThisObject instanceof MapPoint && this.map.hoveredChild instanceof MapPoint)
        {
            SnapMapPointBranch snapMapPointBranch = new SnapMapPointBranch((MapPoint) this.snapFromThisObject, (MapPoint) this.map.hoveredChild);
            this.map.input.snapFromThisObject = null;
            this.map.executeCommand(snapMapPointBranch);
            return true;
        }
        if(this.overrideLayer != null && !(this.map.hoveredChild instanceof MapObject))
        {
            if(this.map.hoveredChild != null && this.map.hoveredChild.layer == this.map.selectedLayer)
                return true;
            new LayerOverrideDialog(editor.stage, map.skin, map, this.overrideLayer, (MapSprite) this.map.hoveredChild);
            return true;
        }

        if(this.map.hoveredChild == null)
            return false;

        if(map.hoveredChild instanceof MapPolygon && editor.fileMenu.toolPane.groupDialog.shouldAdd())
        {
            AddMapSpritesToGroup addMapSpritesToGroup = new AddMapSpritesToGroup(map, (MapPolygon) map.hoveredChild);
            this.map.executeCommand(addMapSpritesToGroup);
        }
        else if(map.hoveredChild instanceof MapPolygon && editor.fileMenu.toolPane.groupDialog.shouldRemove())
        {
            RemoveMapSpritesFromGroup removeMapSpritesFromGroup = new RemoveMapSpritesFromGroup(map, (MapPolygon) map.hoveredChild);
            this.map.executeCommand(removeMapSpritesFromGroup);
        }
        else
        {
            SelectLayerChild selectLayerChild = new SelectLayerChild(this.map, this.map.hoveredChild, Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT));
            this.map.executeCommand(selectLayerChild);
        }

        return true;
    }

    private boolean handleBoxSelectTouchDown(float x, float y, int button)
    {
        if(!Utils.isFileToolThisType(this.editor, Tools.BOXSELECT) || this.map.selectedLayer == null || button != Input.Buttons.LEFT)
            return false;
        this.boxSelect.startDrag(x, y);
        return true;
    }
    private boolean handleBoxSelectDrag(float x, float y)
    {
        if(!Utils.isFileToolThisType(this.editor, Tools.BOXSELECT) || this.map.selectedLayer == null)
            return false;
        this.boxSelect.continueDrag(x, y);
        return false;
    }
    private boolean handleBoxSelectTouchUp()
    {
        if(!Utils.isFileToolThisType(this.editor, Tools.BOXSELECT) || this.map.selectedLayer == null || !this.boxSelect.isDragging)
            return false;

        SelectLayerChildren selectLayerChildren = new SelectLayerChildren(this.map, this.dragOriginPos.x, this.dragOriginPos.y, this.currentPos.x, this.currentPos.y, Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT));
        this.map.executeCommand(selectLayerChildren);

        this.map.propertyMenu.rebuild();
        this.map.input.boxSelect.isDragging = false;
        return false;
    }

    private boolean handlePolygonVertexSelection(int button)
    {
        if(button != Input.Buttons.LEFT && !Utils.isFileToolThisType(this.editor, Tools.OBJECTVERTICESELECT))
            return false;

        for(int i = 0; i < this.map.selectedObjects.size; i ++)
        {
            MapObject mapObject = this.map.selectedObjects.get(i);
            if(mapObject instanceof MapPolygon)
            {
                MapPolygon mapPolygon = (MapPolygon) mapObject;
                if(mapPolygon.indexOfHoveredVertice != -1 && mapPolygon.indexOfSelectedVertice != mapPolygon.indexOfHoveredVertice)
                {
                    SelectPolygonVertice selectPolygonVertice = new SelectPolygonVertice(this.map);
                    this.map.executeCommand(selectPolygonVertice);
                    return true;
                }
            }
        }
        if(Command.shouldExecute(map, SelectPolygonVertice.class))
        {
            SelectPolygonVertice selectPolygonVertice = new SelectPolygonVertice(this.map);
            this.map.executeCommand(selectPolygonVertice);
        }
        return false;
    }

    private boolean handleGradientStart(float x, float y, int button)
    {
        if((!Utils.isFileToolThisType(this.editor, Tools.GRADIENT) || this.map.selectedLayer == null || button != Input.Buttons.LEFT))
            return false;
        if(!(this.map.selectedLayer instanceof SpriteLayer))
            return false;
        if(this.map.selectedSprites.size <= 1)
            return false;
        if(this.draggingGradient)
            return false;

        this.gradientX = x;
        this.gradientY = y;
        this.draggingGradient = true;

        return true;
    }

    private boolean applyGradient(float x, float y, int button)
    {
        if((!Utils.isFileToolThisType(this.editor, Tools.GRADIENT) || this.map.selectedLayer == null))
            return false;
        if(!(this.map.selectedLayer instanceof SpriteLayer))
            return false;
        if(this.map.selectedSprites.size <= 1)
            return false;
        if(!this.draggingGradient)
            return false;

        if(button != Input.Buttons.LEFT)
        {
            this.draggingGradient = false;
            PropertyToolPane.apply(map);
            return true;
        }

        ApplyGradient applyGradient = new ApplyGradient(this.map, this.gradientX, this.gradientY, x, y);
        this.map.executeCommand(applyGradient);

        this.draggingGradient = false;

        return true;
    }

    private boolean handleMapPointCreation(float x, float y, int button)
    {
        if(!Utils.isFileToolThisType(this.editor, Tools.DRAWPOINT) || this.map.selectedLayer == null || button != Input.Buttons.LEFT)
            return false;
        if(this.map.selectedLayer instanceof SpriteLayer && this.map.selectedSprites.size != 1)
            return false;

        float perspectiveX = x;
        float perspectiveY = y;
        if(Utils.doesLayerHavePerspective(this.map, this.map.selectedLayer))
        {
            Perspective perspective = this.map.selectedLayer.perspective;
            Vector3 projector = Utils.project(map.camera, x - map.cameraX, y - map.cameraY);
            Vector3 unprojector = perspective.projectScreenToPerspective(projector.x, Gdx.graphics.getHeight() - projector.y);
            perspectiveX = unprojector.x;
            perspectiveY = unprojector.y;
        }

        if(Command.shouldExecute(map, DrawBranch.class))
        {
            DrawBranch drawBranch = new DrawBranch(map, (ObjectLayer) this.map.selectedLayer, perspectiveX, perspectiveY);
            map.executeCommand(drawBranch);
            return true;
        }

        DrawMapPoint drawMapPoint;
        if(this.map.selectedLayer instanceof ObjectLayer)
        {
            drawMapPoint = new DrawMapPoint(this.map, (ObjectLayer) this.map.selectedLayer, perspectiveX, perspectiveY);
            this.map.executeCommand(drawMapPoint);
        }
        else
        {
            new InstanceOrSpriteToolDialog(editor.stage, map.skin, map, this.map.selectedSprites.first(), null, perspectiveX, perspectiveY);
        }
        return true;
    }

    private boolean handleMapPolygonVerticeCreation(float x, float y, int button)
    {
        if((!Utils.isFileToolThisType(this.editor, Tools.DRAWOBJECT) && !Utils.isFileToolThisType(this.editor, Tools.DRAWRECTANGLE)) || this.map.selectedLayer == null || button != Input.Buttons.LEFT)
            return false;
        if(this.map.selectedLayer instanceof SpriteLayer && this.map.selectedSprites.size != 1)
            return false;

        if(Utils.isFileToolThisType(this.editor, Tools.DRAWRECTANGLE) && this.map.input.mapPolygonVertices.size >= 6)
            return false;

        DrawMapPolygonVertice drawMapPolygonVertice;
        if(Utils.doesLayerHavePerspective(this.map, this.map.selectedLayer))
        {
            Perspective perspective = this.map.selectedLayer.perspective;
            Vector3 projector = Utils.project(map.camera, x - map.cameraX, y - map.cameraY);
            Vector3 unprojector = perspective.projectScreenToPerspective(projector.x, Gdx.graphics.getHeight() - projector.y);
            drawMapPolygonVertice = new DrawMapPolygonVertice(this.map, unprojector.x, unprojector.y, this.objectVerticePosition.x, this.objectVerticePosition.y, Utils.isFileToolThisType(this.editor, Tools.DRAWRECTANGLE));
        }
        else
            drawMapPolygonVertice = new DrawMapPolygonVertice(this.map, x, y, this.objectVerticePosition.x, this.objectVerticePosition.y, Utils.isFileToolThisType(this.editor, Tools.DRAWRECTANGLE));

        this.map.executeCommand(drawMapPolygonVertice);
        return true;
    }

    private boolean handleStairVerticeCreation(float x, float y, int button)
    {
        if(!Utils.isFileToolThisType(this.editor, Tools.STAIRS) || this.map.selectedLayer == null || button != Input.Buttons.LEFT)
            return false;
        if(!(this.map.selectedLayer instanceof SpriteLayer))
            return false;
        if(this.map.input.stairVertices.size >= 8)
            return false;

        DrawStairVertice drawStairVertice = new DrawStairVertice(this.map, x, y, this.stairVerticePosition.x, this.stairVerticePosition.y);
        this.map.executeCommand(drawStairVertice);
        return true;
    }

    private boolean handleMapPolygonCreation(int button)
    {
        if(!Utils.isFileToolThisType(this.editor, Tools.DRAWOBJECT) || this.map.selectedLayer == null || button != Input.Buttons.RIGHT)
        {
            if(button == Input.Buttons.RIGHT && this.map.input.mapPolygonVertices.size < 6)
                clearMapPolygonVertices(button);
            return false;
        }
        if(this.map.input.mapPolygonVertices.size < 6)
        {
            clearMapPolygonVertices(button);
            return true;
        }
        if(this.map.selectedLayer instanceof SpriteLayer && this.map.selectedSprites.size != 1)
        {
            clearMapPolygonVertices(button);
            return true;
        }

        if(this.map.selectedLayer instanceof ObjectLayer)
        {
            DrawMapPolygon drawMapPolygon = new DrawMapPolygon(this.map, (ObjectLayer) this.map.selectedLayer, this.map.input.mapPolygonVertices, this.objectVerticePosition.x, this.objectVerticePosition.y);
            clearMapPolygonVertices(button);
            drawMapPolygon.execute();
            if(editor.fileMenu.toolPane.groupDialog.shouldCreate())
            {
                AddMapSpritesToGroup addMapSpritesToGroup = new AddMapSpritesToGroup(map, drawMapPolygon.mapPolygon);
                this.map.executeCommand(addMapSpritesToGroup);
                clearMapPolygonVertices(button);
                this.map.groupPolygons.children.sort();
            }
            this.map.pushCommand(drawMapPolygon);
        }
        else
        {
            new InstanceOrSpriteToolDialog(editor.stage, map.skin, map, this.map.selectedSprites.first(), new FloatArray(this.map.input.mapPolygonVertices), this.objectVerticePosition.x, this.objectVerticePosition.y);
//            DrawMapPolygon drawMapPolygon = new DrawMapPolygon(this.map, this.map.selectedSprites.first(), this.map.input.mapPolygonVertices, this.objectVerticePosition.x, this.objectVerticePosition.y);
//            clearMapPolygonVertices(button);
//            this.map.executeCommand(drawMapPolygon);
        }

        return true;
    }

    private boolean handleStairsCreation(int button)
    {
        if(!Utils.isFileToolThisType(this.editor, Tools.STAIRS) || this.map.selectedLayer == null || button != Input.Buttons.RIGHT)
        {
            if(button == Input.Buttons.RIGHT && this.map.input.mapPolygonVertices.size < 8)
                clearStairVertices(button);
            return false;
        }
        if(!Command.shouldExecute(map, CreateStairs.class))
        {
            return false;
        }
        if(this.map.input.stairVertices.size < 8)
        {
            clearStairVertices(button);
            return true;
        }
        if(!(this.map.selectedLayer instanceof SpriteLayer))
        {
            clearStairVertices(button);
            return true;
        }

        CreateStairs createStairs;
        createStairs = new CreateStairs(this.map, (SpriteLayer) this.map.selectedLayer, this.map.input.stairVertices, this.stairVerticePosition.x, this.stairVerticePosition.y);
        clearStairVertices(button);
        this.map.executeCommand(createStairs);
        return true;
    }

    private boolean handleMapPolygonRectangleCreation(int button)
    {
        if(!Utils.isFileToolThisType(this.editor, Tools.DRAWRECTANGLE) || this.map.selectedLayer == null || button != Input.Buttons.LEFT)
        {
            if(Utils.isFileToolThisType(this.editor, Tools.DRAWRECTANGLE) && button == Input.Buttons.RIGHT)
            {
                clearMapPolygonVertices(button);
                return true;
            }

            if(button == Input.Buttons.LEFT && this.map.input.mapPolygonVertices.size < 2)
                clearMapPolygonVertices(button);
            return false;
        }

        if(this.map.input.mapPolygonVertices.size < 2)
        {
            clearMapPolygonVertices(button);
            return true;
        }
        if(this.map.selectedLayer instanceof SpriteLayer && this.map.selectedSprites.size != 1)
        {
            clearMapPolygonVertices(button);
            return true;
        }

        DrawMapPolygon drawMapPolygon;
        if(this.map.selectedLayer instanceof ObjectLayer)
        {
            drawMapPolygon = new DrawMapPolygon(this.map, (ObjectLayer) this.map.selectedLayer, this.map.input.mapPolygonVertices, this.objectVerticePosition.x, this.objectVerticePosition.y);
            this.map.executeCommand(drawMapPolygon);
            clearMapPolygonVertices(button);
        }
        else
        {
            new InstanceOrSpriteToolDialog(editor.stage, map.skin, map, this.map.selectedSprites.first(), new FloatArray(this.map.input.mapPolygonVertices), this.objectVerticePosition.x, this.objectVerticePosition.y);
//            drawMapPolygon = new DrawMapPolygon(this.map, this.map.selectedSprites.first(), this.map.input.mapPolygonVertices, this.objectVerticePosition.x, this.objectVerticePosition.y);
        }
//        clearMapPolygonVertices(button);
//        this.map.executeCommand(drawMapPolygon);
        return true;
    }

    public boolean clearMapPolygonVertices(int button)
    {
        if(Utils.isFileToolThisType(this.editor, Tools.DRAWOBJECT) && button == Input.Buttons.RIGHT || Utils.isFileToolThisType(this.editor, Tools.DRAWRECTANGLE))
        {
            ClearMapPolygonVertices clearMapPolygonVertices = new ClearMapPolygonVertices(this.map, this.map.input.mapPolygonVertices);
            this.map.executeCommand(clearMapPolygonVertices);
        }
        return false;
    }

    private boolean clearStairVertices(int button)
    {
        if(Utils.isFileToolThisType(this.editor, Tools.STAIRS) && button == Input.Buttons.RIGHT)
        {
            ClearStairVertices clearStairVertices = new ClearStairVertices(this.map, this.map.input.stairVertices);
            this.map.executeCommand(clearStairVertices);
        }
        return false;
    }

    public boolean handlePreviewSpritePositionUpdate(float x, float y)
    {
        if(map.selectedLayer == null || !(map.selectedLayer instanceof SpriteLayer))
            return false;
        SpriteTool spriteTool = this.map.getSpriteToolFromSelectedTools();
        if(spriteTool == null)
            return false;

        float randomScale = this.editor.fileMenu.toolPane.minMaxDialog.randomSizeValue;

        for(int i = 0; i < spriteTool.previewSprites.size; i ++)
        {
            TextureAtlas.AtlasSprite previewSprite = (TextureAtlas.AtlasSprite) spriteTool.previewSprites.get(i);
            if(Utils.doesLayerHavePerspective(this.map, this.map.selectedLayer))
            {
                Perspective perspective = ((SpriteLayer) this.map.selectedLayer).perspective;
                Vector3 projector = Utils.project(map.camera, x - map.cameraX, y - map.cameraY);
                Vector3 unprojector = perspective.projectScreenToPerspective(projector.x, Gdx.graphics.getHeight() - projector.y);
                float mapSpriteX = unprojector.x;
                float mapSpriteY = unprojector.y;
                float width = previewSprite.getWidth();
                float height = previewSprite.getHeight();
                mapSpriteX -= width / 2;
                mapSpriteY -= height / 2;

                float scale = randomScale;

                float spriteX = mapSpriteX - ((width * scale) - width) / 2f;
                float spriteY = mapSpriteY - ((height * scale) - height) / 2f;
                float trimX = spriteX;
                float trimY = spriteY;
                float trimHeight = (previewSprite.getAtlasRegion().getRegionHeight() / 64f);

                float yScaleDisplacement = 0;
                float xScaleDisplacement = 0;

                float projectX = trimX;
                float projectY = trimY;

                Vector3 p = perspective.projectWorldToPerspective(projectX, projectY);
                projectX = p.x;
                projectY = p.y;

                float perspectiveScale = perspective.getScaleFactor(trimY) * scale;

                yScaleDisplacement += (((trimHeight * (perspectiveScale)) - trimHeight)) / 2f;
                xScaleDisplacement += (((width) * (perspectiveScale)) - width) / 2f;

                float perspectiveOffsetX = trimX - (projectX + (xScaleDisplacement));
                float perspectiveOffsetY = trimY - (projectY + (yScaleDisplacement));

                previewSprite.setPosition(spriteX - perspectiveOffsetX, spriteY - perspectiveOffsetY);
                previewSprite.setOriginCenter();
                previewSprite.setOrigin(width / 2f, previewSprite.getOriginY());
                previewSprite.setScale(perspectiveScale);
            }
            else
                previewSprite.setPosition(x - map.cameraX - previewSprite.getWidth() / 2, y - map.cameraY - previewSprite.getHeight() / 2);
        }
        return false;
    }

    private boolean handleCameraZoom(int amount)
    {
//        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
//            amount *= 10;
//        this.map.zoom += amount / 3f;
//        if(this.map.zoom < .1f)
//            this.map.zoom = .1f;


        if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
        {
            this.map.perspectiveZoom += amount / 500f;

            if (this.map.perspectiveZoom > .0075f)
                this.map.perspectiveZoom = .0075f;
            else if(this.map.perspectiveZoom < 0)
                this.map.perspectiveZoom = 0;
            return false;
        }


        this.map.zoom += amount / 4f;

        if (this.map.zoom < .1f)
            this.map.zoom = .1f;

        return false;
    }

    private boolean handleCameraDrag()
    {
        if(!Utils.isFileToolThisType(this.editor, Tools.GRAB))
            return false;
        this.map.cameraX -= this.dragDifferencePos.x;
        this.map.cameraY -= this.dragDifferencePos.y;
//        this.map.camera.position.x -= this.dragDifferencePos.x;
//        this.map.camera.position.y -= this.dragDifferencePos.y;
        this.map.camera.update();
        return false;
    }
}
