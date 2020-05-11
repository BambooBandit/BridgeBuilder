package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.map.MapObject;
import com.bamboo.bridgebuilder.map.MapSprite;

public class ScaleMapSprites implements Command
{
    private ObjectMap<MapSprite, Float> originalMapSpriteScale;
    private Array<MapSprite> selectedMapSprites;
    private float resultingScaleAddition;

    public ScaleMapSprites(Array<MapSprite> selectedMapSprites)
    {
        this.selectedMapSprites = new Array<>(selectedMapSprites);
        this.originalMapSpriteScale = new ObjectMap<>(selectedMapSprites.size);
        for(int i = 0; i < selectedMapSprites.size; i ++)
        {
            MapSprite mapSprite = selectedMapSprites.get(i);
            this.originalMapSpriteScale.put(mapSprite, mapSprite.scale);
        }

        setOrigin(selectedMapSprites);
    }

    public void update(float scale)
    {
        ObjectMap.Entries<MapSprite, Float> iterator = this.originalMapSpriteScale.iterator();
        while(iterator.hasNext)
        {
            ObjectMap.Entry<MapSprite, Float> entry = iterator.next();
            MapSprite mapSprite = entry.key;
            float originalScale = entry.value;
            this.resultingScaleAddition = scale;
            mapSprite.setScale(originalScale + this.resultingScaleAddition);
            if(mapSprite.tool.hasAttachedMapObjects()) //TODO should be part of the mapSprite.setScale() method
            {
                for(int i = 0; i < mapSprite.attachedMapObjects.size; i ++)
                {
                    MapObject mapObject = mapSprite.attachedMapObjects.get(i);
                    mapObject.setScale(originalScale + this.resultingScaleAddition);
                }
            }
        }
    }

    @Override
    public void execute()
    {
        setOrigin(selectedMapSprites);
        ObjectMap.Entries<MapSprite, Float> iterator = this.originalMapSpriteScale.iterator();
        while(iterator.hasNext)
        {
            ObjectMap.Entry<MapSprite, Float> entry = iterator.next();
            MapSprite mapSprite = entry.key;
            float originalScale = entry.value;
            mapSprite.setScale(originalScale + this.resultingScaleAddition);
            if(mapSprite.tool.hasAttachedMapObjects())
            {
                for(int i = 0; i < mapSprite.attachedMapObjects.size; i ++)
                {
                    MapObject mapObject = mapSprite.attachedMapObjects.get(i);
                    mapObject.setScale(originalScale + this.resultingScaleAddition);
                }
            }
        }
    }

    @Override
    public void undo()
    {
        setOrigin(selectedMapSprites);
        ObjectMap.Entries<MapSprite, Float> iterator = this.originalMapSpriteScale.iterator();
        while(iterator.hasNext)
        {
            ObjectMap.Entry<MapSprite, Float> entry = iterator.next();
            MapSprite mapSprite = entry.key;
            Float originalScale = entry.value;
            mapSprite.setScale(originalScale);
            if(mapSprite.tool.hasAttachedMapObjects())
            {
                for(int i = 0; i < mapSprite.attachedMapObjects.size; i ++)
                {
                    MapObject mapObject = mapSprite.attachedMapObjects.get(i);
                    mapObject.setScale(originalScale);
                }
            }
        }
    }

    private void setOrigin(Array<MapSprite> selectedMapSprites)
    {
        float xSum = 0, ySum = 0;
        for(int i = 0; i < selectedMapSprites.size; i ++)
        {
            MapSprite mapSprite = selectedMapSprites.get(i);
            xSum += mapSprite.getX();
            ySum += mapSprite.getY();
        }
        float xAverage = xSum / selectedMapSprites.size;
        float yAverage = ySum / selectedMapSprites.size;
        Utils.setCenterOrigin(xAverage, yAverage);
    }
}
