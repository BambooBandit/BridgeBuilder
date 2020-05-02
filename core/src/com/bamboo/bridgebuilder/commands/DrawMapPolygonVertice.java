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
    private boolean isRectangle;

    public DrawMapPolygonVertice(Map map, float x, float y, float objectVerticeX, float objectVerticeY, boolean isRectangle)
    {
        this.map = map;
        this.xClick = x;
        this.yClick = y;
        this.objectVerticeX = objectVerticeX;
        this.objectVerticeY = objectVerticeY;
        this.isRectangle = isRectangle;
    }

    @Override
    public void execute()
    {
        int amount = 1;
        if(this.isRectangle)
            amount = 4;
        for(int i = 0; i < amount; i ++)
        {
            if (this.map.input.mapPolygonVertices.size == 0)
            {
                this.map.input.objectVerticePosition.set(this.xClick, this.yClick);
                this.objectVerticeX = this.map.input.objectVerticePosition.x;
                this.objectVerticeY = this.map.input.objectVerticePosition.y;
            }
            this.map.input.mapPolygonVertices.add(this.xClick - this.objectVerticeX);
            this.map.input.mapPolygonVertices.add(this.yClick - this.objectVerticeY);
            this.index = this.map.input.mapPolygonVertices.size - 2;
        }
    }

    @Override
    public void undo()
    {
        if(this.isRectangle)
            this.map.input.mapPolygonVertices.clear();
        else
            this.map.input.mapPolygonVertices.removeRange(index, index + 1);
    }
}
