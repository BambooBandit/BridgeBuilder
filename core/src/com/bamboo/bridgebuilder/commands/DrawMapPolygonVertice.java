package com.bamboo.bridgebuilder.commands;

import com.bamboo.bridgebuilder.map.Map;

public class DrawMapPolygonVertice implements Command
{
    private Map map;
    private float xClick;
    private float yClick;
    private float objectVerticeX;
    private float objectVerticeY;
    private int index;

    public DrawMapPolygonVertice(Map map, float x, float y, float objectVerticeX, float objectVerticeY)
    {
        this.map = map;
        this.xClick = x;
        this.yClick = y;
        this.objectVerticeX = objectVerticeX;
        this.objectVerticeY = objectVerticeY;
    }

    @Override
    public void execute()
    {
        if(this.map.input.mapPolygonVertices.size == 0)
        {
            this.map.input.objectVerticePosition.set(this.xClick, this.yClick);
            this.objectVerticeX = this.map.input.objectVerticePosition.x;
            this.objectVerticeY = this.map.input.objectVerticePosition.y;
        }
        this.map.input.mapPolygonVertices.add(this.xClick - this.objectVerticeX);
        this.map.input.mapPolygonVertices.add(this.yClick - this.objectVerticeY);
        this.index = this.map.input.mapPolygonVertices.size - 2;
    }

    @Override
    public void undo()
    {
        this.map.input.mapPolygonVertices.removeRange(index, index + 1);
    }
}
