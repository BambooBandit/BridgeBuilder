package com.bamboo.bridgebuilder.commands;

import com.bamboo.bridgebuilder.map.MapPolygon;

public class MovePolygonVertice implements Command
{
    public MapPolygon mapPolygon;
    public float oldX, oldY, newX, newY;

    public MovePolygonVertice(MapPolygon mapPolygon, float oldX, float oldY)
    {
        this.mapPolygon = mapPolygon;
        this.oldX = oldX;
        this.oldY = oldY;
    }

    public void update(float currentDragX, float currentDragY)
    {
        this.newX = currentDragX;
        this.newY = currentDragY;
        this.mapPolygon.moveVertice(newX, newY);
    }

    @Override
    public void execute()
    {
        this.mapPolygon.moveVertice(newX, newY);
    }

    @Override
    public void undo()
    {
        this.mapPolygon.moveVertice(oldX, oldY);
    }
}
