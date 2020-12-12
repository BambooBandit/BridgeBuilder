package com.bamboo.bridgebuilder.commands;

import com.bamboo.bridgebuilder.map.Map;

public class DrawStairVertice implements Command
{
    private Map map;
    private float xClick;
    private float yClick;
    private float stairVerticeX;
    private float stairVerticeY;
    private int index;

    public DrawStairVertice(Map map, float x, float y, float stairVerticeX, float stairVerticeY)
    {
        this.map = map;
        this.xClick = x;
        this.yClick = y;
        this.stairVerticeX = stairVerticeX;
        this.stairVerticeY = stairVerticeY;
    }

    @Override
    public void execute()
    {
        if (this.map.input.stairVertices.size == 0)
        {
            this.map.input.stairVerticePosition.set(this.xClick, this.yClick);
            this.stairVerticeX = this.map.input.stairVerticePosition.x;
            this.stairVerticeY = this.map.input.stairVerticePosition.y;
        }
        this.map.input.stairVertices.add(this.xClick - this.stairVerticeX);
        this.map.input.stairVertices.add(this.yClick - this.stairVerticeY);
        this.index = this.map.input.stairVertices.size - 2;
    }

    @Override
    public void undo()
    {
        this.map.input.stairVertices.removeRange(index, index + 1);
    }
}
