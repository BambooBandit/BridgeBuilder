package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.MapSprite;
import com.bamboo.bridgebuilder.map.SpriteLayer;

import java.util.ArrayList;

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
            mapSprite.setID(MapSprite.getAndIncrementId());
            if(mapSprite.attachedSprites != null && mapSprite.attachedSprites.children.size > 0)
                this.children.add(new AttachedMapSpriteData(mapSprite));
            else
                this.children.add(new MapSpriteData(mapSprite));
        }
    }
}
