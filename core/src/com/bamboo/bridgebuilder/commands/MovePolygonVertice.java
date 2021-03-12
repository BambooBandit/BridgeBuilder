package com.bamboo.bridgebuilder.commands;

import com.bamboo.bridgebuilder.map.MapPolygon;

public class MovePolygonVertice implements Command
{
    public MapPolygon mapPolygon;
    public float firstX, firstY; // dragging
    public float oldX, oldY, offsetX, offsetY;

    public MovePolygonVertice(MapPolygon mapPolygon, float firstX, float firstY, float oldX, float oldY)
    {
        this.mapPolygon = mapPolygon;
        this.firstX = firstX - mapPolygon.map.cameraX;
        this.firstY = firstY - mapPolygon.map.cameraY;
        this.oldX = oldX;
        this.oldY = oldY;
    }

    public void update(float currentDragX, float currentDragY)
    {
        this.offsetX = (currentDragX - mapPolygon.map.cameraX) - firstX;
        this.offsetY = (currentDragY - mapPolygon.map.cameraY) - firstY;

        if(this.mapPolygon.attachedSprite == null)
        {
            this.mapPolygon.moveVertice(oldX + offsetX, oldY + offsetY);
        }
        else
        {
            float offsetDifferenceX = (oldX + this.offsetX) - this.mapPolygon.getVerticeX();
            float offsetDifferenceY = (oldY + this.offsetY) - this.mapPolygon.getVerticeY();
            this.mapPolygon.attachedMapObjectManager.moveVerticeBy(mapPolygon.indexOfSelectedVertice, offsetDifferenceX, offsetDifferenceY);
        }
    }

    @Override
    public void execute()
    {
        float offsetDifferenceX = (oldX + this.offsetX) - this.mapPolygon.getVerticeX();
        float offsetDifferenceY = (oldY + this.offsetY) - this.mapPolygon.getVerticeY();
        if(this.mapPolygon.attachedSprite != null)
            this.mapPolygon.attachedMapObjectManager.moveVerticeBy(mapPolygon.indexOfSelectedVertice, offsetDifferenceX, offsetDifferenceY);
        else
            this.mapPolygon.moveVertice(oldX + offsetX, oldY + offsetY);
    }

    @Override
    public void undo()
    {
        float offsetDifferenceX = this.oldX - this.mapPolygon.getVerticeX();
        float offsetDifferenceY = this.oldY - this.mapPolygon.getVerticeY();
        if(this.mapPolygon.attachedSprite != null)
            this.mapPolygon.attachedMapObjectManager.moveVerticeBy(mapPolygon.indexOfSelectedVertice, offsetDifferenceX, offsetDifferenceY);
        else
            this.mapPolygon.moveVertice(oldX, oldY);
    }
}
