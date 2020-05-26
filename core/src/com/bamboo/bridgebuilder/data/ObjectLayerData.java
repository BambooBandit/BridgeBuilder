package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.Layer;
import com.bamboo.bridgebuilder.map.MapObject;
import com.bamboo.bridgebuilder.map.MapPoint;
import com.bamboo.bridgebuilder.map.MapPolygon;

import java.util.ArrayList;

public class ObjectLayerData extends LayerData
{
    public ArrayList<MapObjectData> children;
    public ObjectLayerData(){}
    public ObjectLayerData(Layer layer)
    {
        super(layer);
        this.children = new ArrayList<>();
        for(int i = 0; i < layer.children.size; i ++)
        {
            MapObject mapObject = (MapObject) layer.children.get(i);
            if(mapObject instanceof MapPoint)
                this.children.add(new MapPointData((MapPoint) mapObject));
            else
                this.children.add(new MapPolygonData((MapPolygon) mapObject));
        }
    }
}
