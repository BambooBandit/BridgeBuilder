package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.*;

import java.util.ArrayList;

public class ObjectLayerData extends LayerData
{
    public ArrayList<MapObjectData> children;
    public ArrayList<CellData> grid;
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
        ObjectLayer objectLayer = (ObjectLayer) layer;
        if(objectLayer.spriteGrid != null)
        {
            this.grid = new ArrayList<>(objectLayer.spriteGrid.grid.size);
            for(int i = 0; i < objectLayer.spriteGrid.grid.size; i ++)
                this.grid.add(new CellData(objectLayer.spriteGrid.grid.get(i)));
        }
    }
}
