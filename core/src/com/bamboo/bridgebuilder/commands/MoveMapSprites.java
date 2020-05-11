package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.bamboo.bridgebuilder.map.MapObject;
import com.bamboo.bridgebuilder.map.MapSprite;

public class MoveMapSprites implements Command
{
    private ObjectMap<MapSprite, Vector2> originalMapSpritePosition;
    private float resultingOffsetX;
    private float resultingOffsetY;

    public MoveMapSprites(Array<MapSprite> selectedMapSprites)
    {
        this.originalMapSpritePosition = new ObjectMap<>(selectedMapSprites.size);
        for(int i = 0; i < selectedMapSprites.size; i ++)
        {
            MapSprite mapSprite = selectedMapSprites.get(i);
            this.originalMapSpritePosition.put(mapSprite, new Vector2(mapSprite.getX(), mapSprite.getY()));
        }
    }

    public void update(float currentDragX, float currentDragY)
    {
        float offsetDifferenceX = currentDragX - this.resultingOffsetX;
        float offsetDifferenceY = currentDragY - this.resultingOffsetY;
        this.resultingOffsetX = currentDragX;
        this.resultingOffsetY = currentDragY;

        ObjectMap.Entries<MapSprite, Vector2> iterator = this.originalMapSpritePosition.iterator();
        while(iterator.hasNext)
        {
            ObjectMap.Entry<MapSprite, Vector2> entry = iterator.next();
            MapSprite mapSprite = entry.key;
            Vector2 originalPosition = entry.value;
            mapSprite.setPosition(originalPosition.x + this.resultingOffsetX, originalPosition.y + this.resultingOffsetY);
            if(mapSprite.tool.hasAttachedMapObjects())
            {
                for(int i = 0; i < mapSprite.attachedMapObjects.size; i ++)
                {
                    MapObject mapObject = mapSprite.attachedMapObjects.get(i);
                    mapObject.setPosition(mapObject.getX() + offsetDifferenceX, mapObject.getY() + offsetDifferenceY);
                }
            }
        }
    }

    @Override
    public void execute()
    {
        ObjectMap.Entries<MapSprite, Vector2> iterator = this.originalMapSpritePosition.iterator();
        while(iterator.hasNext)
        {
            ObjectMap.Entry<MapSprite, Vector2> entry = iterator.next();
            MapSprite mapSprite = entry.key;
            Vector2 originalPosition = entry.value;
            mapSprite.setPosition(originalPosition.x + this.resultingOffsetX, originalPosition.y + this.resultingOffsetY);
            if(mapSprite.tool.hasAttachedMapObjects())
            {
                for(int i = 0; i < mapSprite.attachedMapObjects.size; i ++)
                {
                    MapObject mapObject = mapSprite.attachedMapObjects.get(i);
                    mapObject.setPosition(mapObject.getX() + this.resultingOffsetX, mapObject.getY() + this.resultingOffsetY);
                }
            }
        }
    }

    @Override
    public void undo()
    {
        ObjectMap.Entries<MapSprite, Vector2> iterator = this.originalMapSpritePosition.iterator();
        while(iterator.hasNext)
        {
            ObjectMap.Entry<MapSprite, Vector2> entry = iterator.next();
            MapSprite mapSprite = entry.key;
            Vector2 originalPosition = entry.value;
            mapSprite.setPosition(originalPosition.x, originalPosition.y);
            if(mapSprite.tool.hasAttachedMapObjects())
            {
                for(int i = 0; i < mapSprite.attachedMapObjects.size; i ++)
                {
                    MapObject mapObject = mapSprite.attachedMapObjects.get(i);
                    mapObject.setPosition(mapObject.getX() - this.resultingOffsetX, mapObject.getY() - this.resultingOffsetY);
                }
            }
        }
    }
}
