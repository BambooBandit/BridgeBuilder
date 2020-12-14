package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.MapPolygon;
import com.bamboo.bridgebuilder.map.MapSprite;

import java.util.ArrayList;

public class GroupMapPolygonData extends MapPolygonData
{
    public ArrayList<Integer> mapSpriteIDs;

    public GroupMapPolygonData(){}
    public GroupMapPolygonData(MapPolygon mapPolygon, float offsetX, float offsetY)
    {
        super(mapPolygon, offsetX, offsetY);
        if(mapPolygon.mapSprites != null && mapPolygon.mapSprites.size > 0)
        {
            this.mapSpriteIDs = new ArrayList<>();
            for(int i = 0; i < mapPolygon.mapSprites.size; i ++)
            {
                MapSprite mapSprite = mapPolygon.mapSprites.get(i);
                if(mapSprite.parentSprite != null && mapSprite.parentSprite.layer.children.contains(mapSprite.parentSprite, true))
                    this.mapSpriteIDs.add(mapSprite.id);
                else if(mapSprite.parentSprite == null && mapSprite.layer.children.contains(mapSprite, true))
                    this.mapSpriteIDs.add(mapSprite.id);
            }
        }
    }
}
