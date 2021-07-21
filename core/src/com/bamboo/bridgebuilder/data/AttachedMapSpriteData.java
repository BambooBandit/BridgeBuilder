package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.MapSprite;

import java.util.ArrayList;

public class AttachedMapSpriteData extends LayerChildData
{
    public ArrayList<MapSpriteData> s; // sprites

    public AttachedMapSpriteData() {}
    public AttachedMapSpriteData(MapSprite mapSprite)
    {
        super(mapSprite);
        if(mapSprite.attachedSprites.children.size > 0)
            this.s = new ArrayList<>();

        for(int i = 0; i < mapSprite.attachedSprites.children.size; i ++)
        {
            MapSprite child = mapSprite.attachedSprites.children.get(i);
            MapSpriteData mapSpriteData = new MapSpriteData(child);
            s.add(mapSpriteData);
        }
    }
}
