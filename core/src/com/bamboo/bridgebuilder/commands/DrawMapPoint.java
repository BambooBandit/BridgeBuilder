package com.bamboo.bridgebuilder.commands;

import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapPoint;
import com.bamboo.bridgebuilder.map.ObjectLayer;

public class DrawMapPoint implements Command
{
    private Map map;
    private ObjectLayer layer;
    private MapPoint mapPoint = null;
    private float x;
    private float y;

    public DrawMapPoint(Map map, ObjectLayer layer, float x, float y)
    {
        this.map = map;
        this.layer = layer;
        this.x = x;
        this.y = y;
    }

    @Override
    public void execute()
    {
        if(mapPoint == null)
        {
            ObjectLayer layer = (ObjectLayer) map.selectedLayer;
            this.mapPoint = new MapPoint(map, layer, x, y);
        }
        layer.addMapObject(this.mapPoint);
    }

    @Override
    public void undo()
    {
        layer.children.removeValue(this.mapPoint, true);
    }
}
