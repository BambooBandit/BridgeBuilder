package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.FloatArray;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.commands.*;
import com.bamboo.bridgebuilder.ui.fileMenu.Tools;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;


public class MapInput implements InputProcessor
{
    public Map map;
    public BridgeBuilder editor;

    private Vector2 dragOrigin;
    private Vector2 pos; // Used to retrieve position difference of mouse drag

    public FloatArray mapPolygonVertices; // allows for seeing where you are clicking when constructing a new MapObject polygon
    public Vector2 objectVerticePosition;

    // Null if not currently drag/moving any layer child
    public MoveMapSprites moveMapSprites;
    public RotateMapSprites rotateMapSprites;
    public MoveMapObjects moveMapObjects;


    public MapInput(BridgeBuilder editor, Map map)
    {
        this.editor = editor;
        this.map = map;

        this.dragOrigin = new Vector2();
        this.pos = new Vector2();

        this.mapPolygonVertices = new FloatArray();
        this.objectVerticePosition = new Vector2();
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
        map.stage.unfocusAll();
        editor.stage.unfocusAll();

        Vector3 coords = Utils.unproject(map.camera, screenX, screenY);
        this.dragOrigin.set(coords.x, coords.y);

        if(handleMapSpriteCreation(coords.x, coords.y, button))
            return false;
        if(handleManipulatorBoxTouchDown(coords.x, coords.y, button))
            return false;
        if(handleSelect(button))
            return false;
        if(handleMapPointCreation(coords.x, coords.y, button))
            return false;
        if(handleMapPolygonVerticeCreation(coords.x, coords.y, button))
            return false;
        if(handleMapPolygonCreation(button))
            return false;

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        Vector3 coords = Utils.unproject(map.camera, screenX, screenY);

        handleManipulatorBoxTouchUp();
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        Vector3 coords = Utils.unproject(map.camera, screenX, screenY);
        this.pos.set(coords.x, coords.y);
        handleManipulatorBoxDrag(this.pos);

        this.pos = this.pos.sub(dragOrigin.x, dragOrigin.y);

        handleCameraDrag();

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
        Vector3 coords = Utils.unproject(map.camera, screenX, screenY);
        handlePreviewSpritePositionUpdate(coords.x, coords.y);
        handleHoveredLayerChildUpdate(coords.x, coords.y);
        return false;
    }

    @Override
    public boolean scrolled(int amount)
    {
        handleCameraZoom(amount);
        return false;
    }

    private boolean handleManipulatorBoxTouchDown(float x, float y, int button)
    {
        if(button != Input.Buttons.LEFT)
            return false;

        for(int i = 0; i < map.selectedSprites.size; i++)
        {
            MapSprite selectedSprite = map.selectedSprites.get(i);
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
                return true;
            }
        }

        for(int i = 0; i < this.map.selectedObjects.size; i++)
        {
            MapObject selectedObjects = map.selectedObjects.get(i);
            if(selectedObjects.moveBox.contains(x, y))
            {
                this.moveMapObjects = new MoveMapObjects(this.map.selectedObjects);
                this.map.pushCommand(this.moveMapObjects);
                return true;
            }
        }

        return false;
    }

    private boolean handleManipulatorBoxTouchUp()
    {
        this.moveMapSprites = null;
        this.rotateMapSprites = null;
        this.moveMapObjects = null;
        return false;
    }

    private boolean handleManipulatorBoxDrag(Vector2 pos)
    {
        if(this.moveMapSprites != null)
            this.moveMapSprites.update(pos.x, pos.y);
        else if(this.moveMapObjects != null)
            this.moveMapObjects.update(pos.x, pos.y);
        else if(this.rotateMapSprites != null)
            this.rotateMapSprites.update(dragOrigin.angle(pos));
        return false;
    }

    private boolean handleHoveredLayerChildUpdate(float x, float y)
    {
        if(map.selectedLayer != null && Utils.isFileToolThisType(editor, Tools.SELECT))
        {
            for (int i = map.selectedLayer.children.size - 1; i >= 0; i--)
            {
                LayerChild layerChild = (LayerChild) map.selectedLayer.children.get(i);
                if (layerChild.isHoveredOver(x, y))
                {
                    map.hoveredChild = layerChild;
                    return false;
                }
            }
        }
        map.hoveredChild = null;
        return false;
    }

    private boolean handleMapSpriteCreation(float x, float y, int button)
    {
        if(!Utils.isFileToolThisType(editor, Tools.BRUSH) || map.selectedLayer == null || !(map.selectedLayer instanceof SpriteLayer) || button != Input.Buttons.LEFT)
            return false;

        DrawMapSprite drawMapSprite = new DrawMapSprite(map, (SpriteLayer) map.selectedLayer, x, y);
        map.executeCommand(drawMapSprite);
        return false;
    }

    private boolean handleSelect(int button)
    {
        if(!Utils.isFileToolThisType(editor, Tools.SELECT) || map.selectedLayer == null || button != Input.Buttons.LEFT)
            return false;
        if(map.hoveredChild == null)
            return false;

        SelectLayerChild selectLayerChild = new SelectLayerChild(map, map.hoveredChild, Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT));
        map.executeCommand(selectLayerChild);
        return false;
    }

    private boolean handleMapPointCreation(float x, float y, int button)
    {
        if(!Utils.isFileToolThisType(editor, Tools.DRAWPOINT) || map.selectedLayer == null || !(map.selectedLayer instanceof ObjectLayer) || button != Input.Buttons.LEFT)
            return false;

        DrawMapPoint drawMapPoint = new DrawMapPoint(map, (ObjectLayer) map.selectedLayer, x, y);
        map.executeCommand(drawMapPoint);
        return false;
    }

    private boolean handleMapPolygonVerticeCreation(float x, float y, int button)
    {
        if(!Utils.isFileToolThisType(editor, Tools.DRAWOBJECT) || map.selectedLayer == null || !(map.selectedLayer instanceof ObjectLayer) || button != Input.Buttons.LEFT)
            return false;

        DrawMapPolygonVertice drawMapPolygonVertice = new DrawMapPolygonVertice(map, x, y, objectVerticePosition.x, objectVerticePosition.y);
        map.executeCommand(drawMapPolygonVertice);
        return false;
    }

    private boolean handleMapPolygonCreation(int button)
    {
        if(!Utils.isFileToolThisType(editor, Tools.DRAWOBJECT) || map.selectedLayer == null || !(map.selectedLayer instanceof ObjectLayer) || button != Input.Buttons.RIGHT)
        {
            if(button == Input.Buttons.RIGHT && map.input.mapPolygonVertices.size < 6)
                clearMapPolygonVertices(button);
            return false;
        }
        if(map.input.mapPolygonVertices.size < 6)
        {
            clearMapPolygonVertices(button);
            return false;
        }
            DrawMapPolygon drawMapPolygon = new DrawMapPolygon(map, (ObjectLayer) map.selectedLayer, map.input.mapPolygonVertices, objectVerticePosition.x, objectVerticePosition.y);
        clearMapPolygonVertices(button);
        map.executeCommand(drawMapPolygon);
        return false;
    }

    private boolean clearMapPolygonVertices(int button)
    {
        if(button == Input.Buttons.RIGHT)
        {
            ClearMapPolygonVertices clearMapPolygonVertices = new ClearMapPolygonVertices(this.map, this.map.input.mapPolygonVertices);
            this.map.executeCommand(clearMapPolygonVertices);
        }
        return false;
    }

    private boolean handlePreviewSpritePositionUpdate(float x, float y)
    {
        SpriteTool spriteTool = map.getSpriteToolFromSelectedTools();
        if(spriteTool == null)
            return false;
        for(int i = 0; i < spriteTool.previewSprites.size; i ++)
        {
            Sprite previewSprite = spriteTool.previewSprites.get(i);
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
        if(!Utils.isFileToolThisType(editor, Tools.GRAB))
            return false;
        this.map.camera.position.x -= this.pos.x;
        this.map.camera.position.y -= this.pos.y;
        this.map.camera.update();
        return false;
    }

    private int sign (int x) {
        return (x > 0) ? 1 : (x < 0) ? -1 : 0;
    }
}
