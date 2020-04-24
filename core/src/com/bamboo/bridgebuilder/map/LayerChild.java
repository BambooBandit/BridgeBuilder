package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.math.Vector2;

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
    public abstract void drawHoverOutline();
    public abstract void drawSelectedOutline();
    public abstract void drawSelectedHoveredOutline();

    public void setPosition(float x, float y)
    {
        this.position.set(x, y);
    }

    public abstract boolean isHoveredOver(float x, float y);
}
