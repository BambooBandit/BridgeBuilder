package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.utils.FloatArray;
import com.bamboo.bridgebuilder.map.Map;

public class ClearMapPolygonVertices implements Command
{
    private Map map;
    private FloatArray oldVertices;

    public ClearMapPolygonVertices(Map map, FloatArray oldVertices)
    {
        this.map = map;
        this.oldVertices = new FloatArray(oldVertices);
    }

    @Override
    public void execute()
    {
        this.map.input.mapPolygonVertices.clear();
    }

    @Override
    public void undo()
    {
        this.map.input.mapPolygonVertices.addAll(this.oldVertices);
    }
}
