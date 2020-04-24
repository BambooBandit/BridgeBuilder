package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.ui.fileMenu.Tools;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

import java.util.Stack;


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
        Vector3 coords = Utils.unproject(map.camera, screenX, screenY);
        this.dragOrigin.set(coords.x, coords.y);

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
        return false;
    }

    @Override
    public boolean scrolled(int amount)
    {
        handleCameraZoom(amount);
        return false;
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
        if(editor.getFileTool() != null && editor.getFileTool().tool == Tools.GRAB)
        {
            this.map.camera.position.x -= this.pos.x;
            this.map.camera.position.y -= this.pos.y;
            this.map.camera.update();
        }
    }

    private int sign (int x) {
        return (x > 0) ? 1 : (x < 0) ? -1 : 0;
    }
}
