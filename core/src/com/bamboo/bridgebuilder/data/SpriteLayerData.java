package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.MapObject;
import com.bamboo.bridgebuilder.map.MapSprite;
import com.bamboo.bridgebuilder.map.SpriteLayer;

import java.util.ArrayList;

import static com.bamboo.bridgebuilder.map.LayerChild.getAndIncrementId;

public class SpriteLayerData extends LayerData
{
    public ArrayList<LayerChildData> children;
    public SpriteLayerData(){}
    public SpriteLayerData(SpriteLayer layer)
    {
        super(layer);
        this.children = new ArrayList<>();
        for(int i = 0; i < layer.children.size; i ++)
        {
            MapSprite mapSprite = layer.children.get(i);
            if(mapSprite.attachedSprites != null && mapSprite.attachedSprites.children.size > 0)
            {
                for(int k = 0; k < mapSprite.attachedSprites.children.size; k ++)
                {
                    MapSprite child = mapSprite.attachedSprites.children.get(k);
                    child.setID(getAndIncrementId());
                }
            }
            else
                mapSprite.setID(getAndIncrementId());
            if(mapSprite.attachedMapObjects != null)
            {
                for(int k = 0; k < mapSprite.attachedMapObjects.size; k ++)
                {
                    MapObject child = mapSprite.attachedMapObjects.get(k);
                    if(child.attachedId != -1)
                        child.setID(getAndIncrementId());
                }
                for(int k = 0; k < mapSprite.attachedMapObjects.size; k ++)
                {
                    MapObject child = mapSprite.attachedMapObjects.get(k);
                    if(child.attachedId == -1)
                        child.setID(getAndIncrementId());
                }
            }
        }
        for(int i = 0; i < layer.children.size; i ++)
        {
            MapSprite mapSprite = layer.children.get(i);
            if(mapSprite.attachedSprites != null && mapSprite.attachedSprites.children.size > 0)
                this.children.add(new AttachedMapSpriteData(mapSprite));
            else
                this.children.add(new MapSpriteData(mapSprite));
        }
    }
}
