package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.ui.fileMenu.Tools;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;


public class MapInput implements InputProcessor
{
    public Map map;
    public BridgeBuilder editor;

    private Vector2 dragOrigin;
    private Vector3 pos; // Used to retrieve position difference of mouse drag

    public MapInput(BridgeBuilder editor, Map map)
    {
        this.editor = editor;
        this.map = map;

        this.dragOrigin = new Vector2();
        this.pos = new Vector3();
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

        handleBrushClick(coords.x, coords.y);
        handleSelect();

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        Vector3 coords = Utils.unproject(map.camera, screenX, screenY);
        this.pos.set(coords);
        this.pos = this.pos.sub(dragOrigin.x, dragOrigin.y, 0);

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

    private void handleBrushClick(float x, float y)
    {
        if(!Utils.isFileToolThisType(editor, Tools.BRUSH) || map.selectedLayer == null || !(map.selectedLayer instanceof SpriteLayer))
            return;

        SpriteLayer layer = (SpriteLayer) map.selectedLayer;
        SpriteTool spriteTool = map.getSpriteToolFromSelectedTools();
        MapSprite mapSprite = new MapSprite(map, layer, spriteTool, x, y);
        layer.addMapSprite(mapSprite);
    }

    private void handleSelect()
    {
        if(!Utils.isFileToolThisType(editor, Tools.SELECT) || map.selectedLayer == null)
            return;
        if(map.hoveredChild instanceof MapSprite)
        {
            MapSprite hoveredMapSprite = (MapSprite) map.hoveredChild;
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
            {
                if(map.selectedSprites.contains(hoveredMapSprite, true))
                    map.selectedSprites.removeValue(hoveredMapSprite, true);
                else
                    map.selectedSprites.add(hoveredMapSprite);
            }
            else
            {
                map.selectedSprites.clear();
                map.selectedSprites.add(hoveredMapSprite);
            }
        }
        else if(map.hoveredChild instanceof MapObject)
        {
            MapObject hoveredMapObject = (MapObject) map.hoveredChild;
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
            {
                if(map.selectedObjects.contains(hoveredMapObject, true))
                    map.selectedObjects.removeValue(hoveredMapObject, true);
                else
                    map.selectedObjects.add(hoveredMapObject);
            }
            else
            {
                map.selectedObjects.clear();
                map.selectedObjects.add(hoveredMapObject);
            }
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
