package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.map.MapSprite;

public class RotateMapSprites implements Command
{
    private ObjectMap<MapSprite, Float> originalMapSpriteRotation;
    private Array<MapSprite> selectedMapSprites;
    private float resultingAngleAddition;

    public RotateMapSprites(Array<MapSprite> selectedMapSprites)
    {
        this.selectedMapSprites = new Array<>(selectedMapSprites);
        this.originalMapSpriteRotation = new ObjectMap<>(selectedMapSprites.size);
        for(int i = 0; i < selectedMapSprites.size; i ++)
        {
            MapSprite mapSprite = selectedMapSprites.get(i);
            this.originalMapSpriteRotation.put(mapSprite, mapSprite.rotation);
        }

        setOrigin(this.selectedMapSprites);
    }

    public void update(float angle)
    {
        this.resultingAngleAddition = angle;
        ObjectMap.Entries<MapSprite, Float> iterator = this.originalMapSpriteRotation.iterator();
        while(iterator.hasNext)
        {
            ObjectMap.Entry<MapSprite, Float> entry = iterator.next();
            MapSprite mapSprite = entry.key;
            float originalRotation = entry.value;
            mapSprite.setRotation(originalRotation + this.resultingAngleAddition);
        }
    }

    @Override
    public void execute()
    {
        setOrigin(selectedMapSprites);
        ObjectMap.Entries<MapSprite, Float> iterator = this.originalMapSpriteRotation.iterator();
        while(iterator.hasNext)
        {
            ObjectMap.Entry<MapSprite, Float> entry = iterator.next();
            MapSprite mapSprite = entry.key;
            float originalRotation = entry.value;
            mapSprite.setRotation(originalRotation + this.resultingAngleAddition);
        }
    }

    @Override
    public void undo()
    {
        setOrigin(selectedMapSprites);
        ObjectMap.Entries<MapSprite, Float> iterator = this.originalMapSpriteRotation.iterator();
        while(iterator.hasNext)
        {
            ObjectMap.Entry<MapSprite, Float> entry = iterator.next();
            MapSprite mapSprite = entry.key;
            Float originalRotation = entry.value;
            mapSprite.setRotation(originalRotation);
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
