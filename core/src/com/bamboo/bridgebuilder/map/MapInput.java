package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.FloatArray;
import com.bamboo.bridgebuilder.BoxSelect;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.commands.*;
import com.bamboo.bridgebuilder.ui.fileMenu.Tools;
import com.bamboo.bridgebuilder.ui.propertyMenu.PropertyToolPane;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.FieldFieldPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;


public class MapInput implements InputProcessor
{
    public Map map;
    public BridgeBuilder editor;

    public Vector2 dragOriginPos;
    public Vector2 dragDifferencePos; // Used to retrieve position difference of mouse drag
    public Vector2 currentPos;

    public FloatArray mapPolygonVertices; // allows for seeing where you are clicking when constructing a new MapObject polygon
    public Vector2 objectVerticePosition;

    // Null if not currently drag/moving any layer child
    public MoveMapSprites moveMapSprites;
    public RotateMapSprites rotateMapSprites;
    public ScaleMapSprites scaleMapSprites;
    public MoveMapObjects moveMapObjects;
    public MovePolygonVertice movePolygonVertice;

    public float gradientX, gradientY; // Used for gradient placement
    public boolean draggingGradient = false;

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

        this.boxSelect = new BoxSelect(map);
    }

    @Override
    public boolean keyDown(int keycode)
    {
        return false;
    }

    @Override
    public boolean keyUp(int keycode)
    {
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
            if(handleMapPolygonRectangleCreation(button))
                return false;
            if(handleMapPolygonCreation(button))
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
            this.currentPos.set(coords.x, coords.y);
            this.dragDifferencePos.set(coords.x, coords.y);
            this.dragDifferencePos = this.dragDifferencePos.sub(this.dragOriginPos.x, this.dragOriginPos.y);
            handleManipulatorBoxDrag(this.dragDifferencePos, this.currentPos);
            handleBoxSelectDrag(coords.x, coords.y);

            handleCameraDrag();
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
            this.currentPos.set(coords.x, coords.y);
            handlePreviewSpritePositionUpdate(coords.x, coords.y);
            handleHoveredLayerChildUpdate(coords.x, coords.y);
            handleManipulatorBoxHover(coords.x, coords.y);
            handleSelectedPolygonVerticeHover(coords.x, coords.y);
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
        if(button != Input.Buttons.LEFT || !Utils.isFileToolThisType(this.editor, Tools.SELECT))
            return false;

        for(int i = 0; i < this.map.selectedSprites.size; i++)
        {
            MapSprite selectedSprite = this.map.selectedSprites.get(i);
            if(selectedSprite.moveBox.contains(x, y))
            {
                this.moveMapSprites = new MoveMapSprites(this.map.selectedSprites);
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
                    this.movePolygonVertice = new MovePolygonVertice(mapPolygon, mapPolygon.polygon.getTransformedVertices()[mapPolygon.indexOfSelectedVertice], mapPolygon.polygon.getTransformedVertices()[mapPolygon.indexOfSelectedVertice + 1]);
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
        this.rotateMapSprites = null;
        this.scaleMapSprites = null;
        this.moveMapObjects = null;
        this.movePolygonVertice = null;
        return false;
    }

    private boolean handleManipulatorBoxDrag(Vector2 dragAmount, Vector2 dragCurrentPos)
    {
        if(this.moveMapSprites != null)
            this.moveMapSprites.update(dragAmount.x, dragAmount.y);
        else if(this.moveMapObjects != null)
            this.moveMapObjects.update(dragAmount.x, dragAmount.y);
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
            this.rotateMapSprites.update(this.dragOriginPos.angle(dragCurrentPos));
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
        if(this.map.selectedLayer != null && Utils.isFileToolThisType(this.editor, Tools.SELECT))
        {
            for(int i = 0; i < map.selectedSprites.size; i ++)
            {
                MapSprite mapSprite = map.selectedSprites.get(i);
                if(mapSprite.tool.hasAttachedMapObjects())
                {
                    for(int k = 0; k < mapSprite.attachedMapObjects.size; k ++)
                    {
                        MapObject mapObject = mapSprite.attachedMapObjects.get(k);
                        if(mapObject.isHoveredOver(x, y))
                        {
                            this.map.hoveredChild = mapObject;
                            return false;
                        }
                    }
                }
            }
            for (int i = this.map.selectedLayer.children.size - 1; i >= 0; i--)
            {
                LayerChild layerChild = (LayerChild) this.map.selectedLayer.children.get(i);
                if (layerChild.isHoveredOver(x, y))
                {
                    this.map.hoveredChild = layerChild;
                    return false;
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

    private boolean handleMapSpriteCreation(float x, float y, int button)
    {
        if(!Utils.isFileToolThisType(this.editor, Tools.BRUSH) || this.map.selectedLayer == null || !(this.map.selectedLayer instanceof SpriteLayer) || button != Input.Buttons.LEFT)
            return false;
        if(this.map.spriteMenu.selectedSpriteTools.size == 0)
            return false;

        if(Command.shouldExecute(map, DrawMapSprite.class))
        {
            DrawMapSprite drawMapSprite = new DrawMapSprite(this.map, (SpriteLayer) this.map.selectedLayer, x, y);
            this.map.executeCommand(drawMapSprite);
            return true;
        }
        return false;
    }

    private boolean handleSelect(int button)
    {
        if(!Utils.isFileToolThisType(this.editor, Tools.SELECT) || this.map.selectedLayer == null || button != Input.Buttons.LEFT)
            return false;
        if(this.map.hoveredChild == null)
            return false;

        SelectLayerChild selectLayerChild = new SelectLayerChild(this.map, this.map.hoveredChild, Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT));
        this.map.executeCommand(selectLayerChild);
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

        SelectLayerChildren selectLayerChildren = new SelectLayerChildren(this.map, this.dragOriginPos.x, this.dragOriginPos.y, this.currentPos.x, this.currentPos.y, Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT));
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

        DrawMapPoint drawMapPoint;
        if(this.map.selectedLayer instanceof ObjectLayer)
            drawMapPoint = new DrawMapPoint(this.map, (ObjectLayer) this.map.selectedLayer, x, y);
        else
            drawMapPoint = new DrawMapPoint(this.map, this.map.selectedSprites.first(), x, y);
        this.map.executeCommand(drawMapPoint);
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

        DrawMapPolygonVertice drawMapPolygonVertice = new DrawMapPolygonVertice(this.map, x, y, this.objectVerticePosition.x, this.objectVerticePosition.y, Utils.isFileToolThisType(this.editor, Tools.DRAWRECTANGLE));
        this.map.executeCommand(drawMapPolygonVertice);
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

        DrawMapPolygon drawMapPolygon;
        if(this.map.selectedLayer instanceof ObjectLayer)
            drawMapPolygon = new DrawMapPolygon(this.map, (ObjectLayer) this.map.selectedLayer, this.map.input.mapPolygonVertices, this.objectVerticePosition.x, this.objectVerticePosition.y);
        else
            drawMapPolygon = new DrawMapPolygon(this.map, this.map.selectedSprites.first(), this.map.input.mapPolygonVertices, this.objectVerticePosition.x, this.objectVerticePosition.y);
        clearMapPolygonVertices(button);
        this.map.executeCommand(drawMapPolygon);
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
            drawMapPolygon = new DrawMapPolygon(this.map, (ObjectLayer) this.map.selectedLayer, this.map.input.mapPolygonVertices, this.objectVerticePosition.x, this.objectVerticePosition.y);
        else
            drawMapPolygon = new DrawMapPolygon(this.map, this.map.selectedSprites.first(), this.map.input.mapPolygonVertices, this.objectVerticePosition.x, this.objectVerticePosition.y);
        clearMapPolygonVertices(button);
        this.map.executeCommand(drawMapPolygon);
        return true;
    }

    private boolean clearMapPolygonVertices(int button)
    {
        if(Utils.isFileToolThisType(this.editor, Tools.DRAWOBJECT) && button == Input.Buttons.RIGHT || Utils.isFileToolThisType(this.editor, Tools.DRAWRECTANGLE))
        {
            ClearMapPolygonVertices clearMapPolygonVertices = new ClearMapPolygonVertices(this.map, this.map.input.mapPolygonVertices);
            this.map.executeCommand(clearMapPolygonVertices);
        }
        return false;
    }

    private boolean handlePreviewSpritePositionUpdate(float x, float y)
    {
        if(map.selectedLayer == null || !(map.selectedLayer instanceof SpriteLayer))
            return false;
        SpriteTool spriteTool = this.map.getSpriteToolFromSelectedTools();
        if(spriteTool == null)
            return false;
        for(int i = 0; i < spriteTool.previewSprites.size; i ++)
        {
            Sprite previewSprite = spriteTool.previewSprites.get(i);
            if(this.editor.fileMenu.toolPane.perspective.selected && Utils.doesLayerHavePerspective(this.map, this.map.selectedLayer))
            {
                x -= previewSprite.getWidth() / 2;
                y -= previewSprite.getHeight() / 2;
                map.camera.update();
                float[] m = this.map.camera.combined.getValues();
                float skew = 0;
                float antiDepth = 0;
                float perspectiveTop = 0;
                float perspectiveBottom = 0;
                try
                {
                    PropertyField topProperty = Utils.getTopScalePerspectiveProperty(this.map, this.map.selectedLayer);
                    PropertyField bottomProperty = Utils.getBottomScalePerspectiveProperty(this.map, this.map.selectedLayer);
                    FieldFieldPropertyValuePropertyField property = Utils.getSkewPerspectiveProperty(this.map, this.map.selectedLayer);
                    skew = Float.parseFloat(property.value.getText());
                    property = Utils.getAntiDepthPerspectiveProperty(this.map, this.map.selectedLayer);
                    antiDepth = Float.parseFloat(property.value.getText());
                    if(topProperty != null)
                        perspectiveTop = Float.parseFloat(((FieldFieldPropertyValuePropertyField) topProperty).value.getText());
                    if(bottomProperty != null)
                        perspectiveBottom = Float.parseFloat(((FieldFieldPropertyValuePropertyField) bottomProperty).value.getText());
                }
                catch (NumberFormatException e){}
                if(antiDepth >= .1f)
                    skew /= antiDepth * 15;
                m[Matrix4.M31] += skew;
                m[Matrix4.M11] += this.map.camera.position.y / ((-10f * this.map.camera.zoom) / skew) - ((.097f * antiDepth) / (antiDepth + .086f));
                this.map.camera.invProjectionView.set(this.map.camera.combined);
                Matrix4.inv(this.map.camera.invProjectionView.val);
                this.map.camera.frustum.update(this.map.camera.invProjectionView);

                float yScaleDisplacement = 0;
                float xScaleDisplacement = 0;
                float spriteAtlasWidth = previewSprite.getRegionWidth() / 64;
                float spriteAtlasHeight = previewSprite.getRegionHeight() / 64;
                float whiteSpaceWidth = (previewSprite.getWidth() - spriteAtlasWidth);

                xScaleDisplacement = previewSprite.getWidth() / 2;

                Vector3 p = Utils.project(this.map.camera, x + xScaleDisplacement, y);
                x = p.x;
                y = Gdx.graphics.getHeight() - p.y;
                this.map.camera.update();
                p = Utils.unproject(this.map.camera, x, y);
                x = p.x;
                y = p.y;

                yScaleDisplacement = ((spriteAtlasHeight * previewSprite.getScaleY()) - spriteAtlasHeight) / 2f;
                xScaleDisplacement = -(spriteAtlasWidth / 2);
                xScaleDisplacement -= (whiteSpaceWidth * previewSprite.getScaleX() / 2);

                previewSprite.setPosition(x + xScaleDisplacement, y + yScaleDisplacement);

                float mapHeight = this.map.selectedLayer.height;
                float positionY = y;

                float coeff = positionY / mapHeight;
                float delta = perspectiveTop - perspectiveBottom;

                float perspectiveScale = (perspectiveBottom + coeff * delta);

                previewSprite.setScale(perspectiveScale);
            }
            else
                previewSprite.setPosition(x - previewSprite.getWidth() / 2, y - previewSprite.getHeight() / 2);
        }
        return false;
    }

    private boolean handleCameraZoom(int amount)
    {
        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
            amount *= 10;
        this.map.zoom += amount / 3f;
        if(this.map.zoom < .1f)
            this.map.zoom = .1f;
        return false;
    }

    private boolean handleCameraDrag()
    {
        if(!Utils.isFileToolThisType(this.editor, Tools.GRAB))
            return false;
        this.map.camera.position.x -= this.dragDifferencePos.x;
        this.map.camera.position.y -= this.dragDifferencePos.y;
        this.map.camera.update();
        PropertyToolPane.updatePerspective(this.map);
        return false;
    }
}
