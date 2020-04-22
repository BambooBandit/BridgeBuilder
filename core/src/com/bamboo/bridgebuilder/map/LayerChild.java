package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

public abstract class LayerChild
{
    protected Map map;
    public Vector2 position;
    public Layer layer;

    public LayerChild(Map map, Layer layer, float x, float y)
    {
        this.map = map;
        this.layer = layer;
        this.position = new Vector2(x, y);
    }

    public abstract void draw();

    public void setPosition(float x, float y)
    {
        this.position.set(x, y);
    }
}
