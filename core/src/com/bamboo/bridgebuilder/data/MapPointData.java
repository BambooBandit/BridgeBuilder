package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.MapPoint;

public class MapPointData extends MapObjectData
{
    public MapPointData(){}
    public MapPointData(MapPoint mapPoint, float offsetX, float offsetY)
    {
        super(mapPoint, offsetX, offsetY);
    }
}
