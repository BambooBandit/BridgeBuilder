package com.bamboo.bridgebuilder;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.bamboo.bridgebuilder.map.Map;

public class BoxSelect
{
    public Rectangle rectangle;
    public boolean isDragging = false;
    public Map map;
    private Vector2 dragStartPosition;
    private Vector2 dragCurrentPosition;
    private float[] vertices;
    public BoxSelect(Map map)
    {
        this.map = map;
        this.rectangle = new Rectangle();
        this.dragStartPosition = new Vector2();
        this.dragCurrentPosition = new Vector2();
        this.vertices = new float[8];
    }

    public void startDrag(float x, float y)
    {
        this.dragStartPosition.set(x, y);
        this.rectangle.setPosition(x, y);
        this.vertices[0] = x;
        this.vertices[1] = y;
        this.continueDrag(x, y);
        this.isDragging = true;
    }

    public void continueDrag(float x, float y)
    {
        this.dragCurrentPosition.set(x, y);
        this.rectangle.setSize(this.dragCurrentPosition.x - this.dragStartPosition.x, this.dragCurrentPosition.y - this.dragStartPosition.y);
        this.vertices[2] = rectangle.x;
        this.vertices[3] = y;
        this.vertices[4] = x;
        this.vertices[5] = y;
        this.vertices[6] = x;
        this.vertices[7] = rectangle.y;
    }

    public float[] getVertices()
    {
        return vertices;
    }
}
