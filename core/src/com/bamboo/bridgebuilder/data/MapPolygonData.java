package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.MapPolygon;

public class MapPolygonData extends MapObjectData
{
    public float[] v; // verts
    public MapPolygonData(){}
    public MapPolygonData(MapPolygon mapPolygon, float offsetX, float offsetY)
    {
        super(mapPolygon, offsetX, offsetY);
        this.v = mapPolygon.polygon.getVertices();
    }
}
