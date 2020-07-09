package com.bamboo.bridgebuilder.map;

public abstract class LayerChild
{
    public float x, y;
    public Map map;
    public Layer layer;
    public boolean selected;
    public float perspectiveScale = 0;

    public LayerChild(Map map, Layer layer, float x, float y)
    {
        this.map = map;
        this.layer = layer;
    }

    public LayerChild(Map map, float x, float y)
    {
        this.map = map;
    }

    public abstract void draw();
    public abstract void drawHoverOutline();
    public abstract void drawSelectedOutline();
    public abstract void drawSelectedHoveredOutline();

    public abstract boolean isHoveredOver(float x, float y);
    public abstract boolean isHoveredOver(float[] vertices);

    public abstract void select();
    public abstract void unselect();

    public abstract void setPosition(float x, float y);
    public abstract float getX();
    public abstract float getY();

    public abstract void setRotation(float degrees);
    public abstract void setScale(float scale);
    public abstract float getScale();
    public abstract float getRotation();
}
