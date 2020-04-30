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
    private Vector3 pos; // Used to retrieve position difference of mouse drag

    public FloatArray mapPolygonVertices; // allows for seeing where you are clicking when constructing a new MapObject polygon
    public Vector2 objectVerticePosition;

    public MoveMapSprites moveMapSprites; // Null if not currently drag/moving any layer child

    public MapInput(BridgeBuilder editor, Map map)
    {
        this.editor = editor;
        this.map = map;

        this.dragOrigin = new Vector2();
        this.pos = new Vector3();

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

        handleMapSpriteCreation(coords.x, coords.y, button);
        handleSelect(button);
        handleMapPointCreation(coords.x, coords.y, button);
        handleMapPolygonVerticeCreation(coords.x, coords.y, button);
        handleMapPolygonCreation(button);
        handleManipulatorBoxTouchDown(coords.x, coords.y, button);

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
        this.pos.set(coords);
        this.pos = this.pos.sub(dragOrigin.x, dragOrigin.y, 0);

        handleManipulatorBoxDrag(pos.x, pos.y);
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

    private void handleManipulatorBoxTouchDown(float x, float y, int button)
    {
        if(button != Input.Buttons.LEFT)
            return;

        for(int i = 0; i < map.selectedSprites.size; i++)
        {
            MapSprite selectedSprite = map.selectedSprites.get(i);
            if(selectedSprite.moveBox.contains(x, y))
            {
                this.moveMapSprites = new MoveMapSprites(this.map.selectedSprites);
                this.map.pushCommand(this.moveMapSprites);
                return;
            }
            else if(selectedSprite.rotationBox.contains(x, y))
            {
                return;
            }
            else if(selectedSprite.scaleBox.contains(x, y))
            {
                return;
            }
        }

        for(int i = 0; i < this.map.selectedObjects.size; i++)
        {
            MapObject selectedObjects = map.selectedObjects.get(i);
            if(selectedObjects.moveBox.contains(x, y))
            {
                return;
            }
        }
    }

    private void handleManipulatorBoxTouchUp()
    {
        this.moveMapSprites = null;
    }

    private void handleManipulatorBoxDrag(float x, float y)
    {
        if(this.moveMapSprites == null)
            return;
        this.moveMapSprites.update(x, y);
    }

    private void handleHoveredLayerChildUpdate(float x, float y)
    {
        if(map.selectedLayer != null && Utils.isFileToolThisType(editor, Tools.SELECT))
        {
            for (int i = map.selectedLayer.children.size - 1; i >= 0; i--)
            {
                LayerChild layerChild = (LayerChild) map.selectedLayer.children.get(i);
                if (layerChild.isHoveredOver(x, y))
                {
                    map.hoveredChild = layerChild;
                    return;
                }
            }
        }
        map.hoveredChild = null;
    }

    private void handleMapSpriteCreation(float x, float y, int button)
    {
        if(!Utils.isFileToolThisType(editor, Tools.BRUSH) || map.selectedLayer == null || !(map.selectedLayer instanceof SpriteLayer) || button != Input.Buttons.LEFT)
            return;

        DrawMapSprite drawMapSprite = new DrawMapSprite(map, (SpriteLayer) map.selectedLayer, x, y);
        map.executeCommand(drawMapSprite);
    }

    private void handleSelect(int button)
    {
        if(!Utils.isFileToolThisType(editor, Tools.SELECT) || map.selectedLayer == null || button != Input.Buttons.LEFT)
            return;
        if(map.hoveredChild == null)
            return;

        SelectMapSprite selectMapSprite = new SelectMapSprite(map, map.hoveredChild, Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT));
        map.executeCommand(selectMapSprite);
    }

    private void handleMapPointCreation(float x, float y, int button)
    {
        if(!Utils.isFileToolThisType(editor, Tools.DRAWPOINT) || map.selectedLayer == null || !(map.selectedLayer instanceof ObjectLayer) || button != Input.Buttons.LEFT)
            return;

        DrawMapPoint drawMapPoint = new DrawMapPoint(map, (ObjectLayer) map.selectedLayer, x, y);
        map.executeCommand(drawMapPoint);
    }

    private void handleMapPolygonVerticeCreation(float x, float y, int button)
    {
        if(!Utils.isFileToolThisType(editor, Tools.DRAWOBJECT) || map.selectedLayer == null || !(map.selectedLayer instanceof ObjectLayer) || button != Input.Buttons.LEFT)
            return;

        DrawMapPolygonVertice drawMapPolygonVertice = new DrawMapPolygonVertice(map, x, y, objectVerticePosition.x, objectVerticePosition.y);
        map.executeCommand(drawMapPolygonVertice);
    }

    private void handleMapPolygonCreation(int button)
    {
        if(!Utils.isFileToolThisType(editor, Tools.DRAWOBJECT) || map.selectedLayer == null || !(map.selectedLayer instanceof ObjectLayer) || button != Input.Buttons.RIGHT)
        {
            if(button == Input.Buttons.RIGHT && map.input.mapPolygonVertices.size < 6)
                clearMapPolygonVertices(button);
            return;
        }
        if(map.input.mapPolygonVertices.size < 6)
        {
            clearMapPolygonVertices(button);
            return;
        }
            DrawMapPolygon drawMapPolygon = new DrawMapPolygon(map, (ObjectLayer) map.selectedLayer, map.input.mapPolygonVertices, objectVerticePosition.x, objectVerticePosition.y);
        clearMapPolygonVertices(button);
        map.executeCommand(drawMapPolygon);
    }

    private void clearMapPolygonVertices(int button)
    {
        if(button == Input.Buttons.RIGHT)
        {
            ClearMapPolygonVertices clearMapPolygonVertices = new ClearMapPolygonVertices(this.map, this.map.input.mapPolygonVertices);
            this.map.executeCommand(clearMapPolygonVertices);
        }
    }

    private void handlePreviewSpritePositionUpdate(float x, float y)
    {
        SpriteTool spriteTool = map.getSpriteToolFromSelectedTools();
        if(spriteTool == null)
            return;
        for(int i = 0; i < spriteTool.previewSprites.size; i ++)
        {
            Sprite previewSprite = spriteTool.previewSprites.get(i);
            previewSprite.setPosition(x - previewSprite.getWidth() / 2, y - previewSprite.getHeight() / 2);
        }
    }

    private void handleCameraZoom(int amount)
    {
        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
            amount *= 10;
        this.map.zoom += amount / 3f;
        if(this.map.zoom < .1f)
            this.map.zoom = .1f;
    }

    private void handleCameraDrag()
    {
        if(!Utils.isFileToolThisType(editor, Tools.GRAB))
            return;
        this.map.camera.position.x -= this.pos.x;
        this.map.camera.position.y -= this.pos.y;
        this.map.camera.update();
    }

    private int sign (int x) {
        return (x > 0) ? 1 : (x < 0) ? -1 : 0;
    }
}
