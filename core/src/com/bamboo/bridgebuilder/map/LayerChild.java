package com.bamboo.bridgebuilder.map;

public abstract class LayerChild
{
    protected Map map;
    public Layer layer;
    public boolean selected;

    public LayerChild(Map map, Layer layer, float x, float y)
    {
        this.map = map;
        this.layer = layer;
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
}
