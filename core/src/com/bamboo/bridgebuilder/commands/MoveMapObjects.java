package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.bamboo.bridgebuilder.map.MapObject;

public class MoveMapObjects implements Command
{
    private ObjectMap<MapObject, Vector2> originalMapObjectPosition;
    private float resultingOffsetX;
    private float resultingOffsetY;

    public MoveMapObjects(Array<MapObject> selectedMapObjects)
    {
        this.originalMapObjectPosition = new ObjectMap<>(selectedMapObjects.size);
        for(int i = 0; i < selectedMapObjects.size; i ++)
        {
            MapObject mapObject = selectedMapObjects.get(i);
            this.originalMapObjectPosition.put(mapObject, new Vector2(mapObject.position));
        }
    }

    public void update(float currentDragX, float currentDragY)
    {
        float offsetDifferenceX = currentDragX - this.resultingOffsetX;
        float offsetDifferenceY = currentDragY - this.resultingOffsetY;
        this.resultingOffsetX = currentDragX;
        this.resultingOffsetY = currentDragY;

        ObjectMap.Entries<MapObject, Vector2> iterator = this.originalMapObjectPosition.iterator();
        while(iterator.hasNext)
        {
            ObjectMap.Entry<MapObject, Vector2> entry = iterator.next();
            MapObject mapObject = entry.key;
            Vector2 originalPosition = entry.value;
            if(mapObject.attachedSprite == null)
                mapObject.setPosition(originalPosition.x + this.resultingOffsetX, originalPosition.y + this.resultingOffsetY);
            else
                mapObject.attachedMapObjectManager.moveBy(offsetDifferenceX, offsetDifferenceY);
        }
    }

    @Override
    public void execute()
    {
        ObjectMap.Entries<MapObject, Vector2> iterator = this.originalMapObjectPosition.iterator();
        while(iterator.hasNext)
        {
            ObjectMap.Entry<MapObject, Vector2> entry = iterator.next();
            MapObject mapObject = entry.key;
            Vector2 originalPosition = entry.value;
            if(mapObject.attachedSprite == null)
                mapObject.setPosition(originalPosition.x + this.resultingOffsetX, originalPosition.y + this.resultingOffsetY);
            else
                mapObject.attachedMapObjectManager.moveBy(this.resultingOffsetX, this.resultingOffsetY);
        }
    }

    @Override
    public void undo()
    {
        ObjectMap.Entries<MapObject, Vector2> iterator = this.originalMapObjectPosition.iterator();
        while(iterator.hasNext)
        {
            ObjectMap.Entry<MapObject, Vector2> entry = iterator.next();
            MapObject mapObject = entry.key;
            Vector2 originalPosition = entry.value;
            if(mapObject.attachedSprite == null)
                mapObject.setPosition(originalPosition.x, originalPosition.y);
            else
                mapObject.attachedMapObjectManager.moveBy(-this.resultingOffsetX, -this.resultingOffsetY);
        }
    }
}
