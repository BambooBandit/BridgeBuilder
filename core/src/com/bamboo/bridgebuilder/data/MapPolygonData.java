package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.MapPolygon;

public class MapPolygonData extends MapObjectData
{
    public float[] verts;
    public MapPolygonData(){}
    public MapPolygonData(MapPolygon mapPolygon)
    {
        super(mapPolygon);
        this.verts = mapPolygon.polygon.getVertices();
    }
}
