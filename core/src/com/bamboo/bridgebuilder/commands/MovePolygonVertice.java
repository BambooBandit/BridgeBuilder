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

        if(this.mapPolygon.attachedSprite == null)
            this.mapPolygon.moveVertice(newX, newY);
        else
        {
            float offsetDifferenceX = this.newX - this.mapPolygon.getVerticeX();
            float offsetDifferenceY = this.newY - this.mapPolygon.getVerticeY();
            this.mapPolygon.attachedMapObjectManager.moveVerticeBy(mapPolygon.indexOfSelectedVertice, offsetDifferenceX, offsetDifferenceY);
        }
    }

    @Override
    public void execute()
    {
        float offsetDifferenceX = this.newX - this.mapPolygon.getVerticeX();
        float offsetDifferenceY = this.newY - this.mapPolygon.getVerticeY();
        this.mapPolygon.attachedMapObjectManager.moveVerticeBy(mapPolygon.indexOfSelectedVertice, offsetDifferenceX, offsetDifferenceY);
    }

    @Override
    public void undo()
    {
        float offsetDifferenceX = this.oldX - this.mapPolygon.getVerticeX();
        float offsetDifferenceY = this.oldY - this.mapPolygon.getVerticeY();
        this.mapPolygon.attachedMapObjectManager.moveVerticeBy(mapPolygon.indexOfSelectedVertice, offsetDifferenceX, offsetDifferenceY);
    }
}
