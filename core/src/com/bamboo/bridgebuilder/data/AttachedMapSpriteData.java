package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.MapSprite;

import java.util.ArrayList;

public class AttachedMapSpriteData extends MapSpriteData
{
    public ArrayList<MapSpriteData> sprites;

    public AttachedMapSpriteData() {}
    public AttachedMapSpriteData(MapSprite mapSprite)
    {
        super(mapSprite);
        this.sprites = new ArrayList<>();

        for(int i = 0; i < mapSprite.attachedSprites.children.size; i ++)
        {
            MapSprite child = mapSprite.attachedSprites.children.get(i);
            MapSpriteData mapSpriteData = new MapSpriteData(child);
            sprites.add(mapSpriteData);
        }
    }
}
