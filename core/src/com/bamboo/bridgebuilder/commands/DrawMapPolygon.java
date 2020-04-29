package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.utils.FloatArray;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapPolygon;
import com.bamboo.bridgebuilder.map.ObjectLayer;

public class DrawMapPolygon implements Command
{
    private Map map;
    private ObjectLayer layer;
    private FloatArray vertices;
    private float objectX;
    private float objectY;
    private MapPolygon mapPolygon;

    public DrawMapPolygon(Map map, ObjectLayer layer, FloatArray vertices, float objectX, float objectY)
    {
        this.map = map;
        this.layer = layer;
        this.vertices = new FloatArray(vertices);
        this.objectX = objectX;
        this.objectY = objectY;
    }

    @Override
    public void execute()
    {
        if(this.mapPolygon == null)
            this.mapPolygon = new MapPolygon(this.map, this.layer, vertices.toArray(), this.objectX, this.objectY);
        this.layer.addMapObject(mapPolygon);
    }

    @Override
    public void undo()
    {
        this.layer.children.removeValue(mapPolygon, true);
    }
}
