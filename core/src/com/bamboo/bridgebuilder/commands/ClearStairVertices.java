package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.utils.FloatArray;
import com.bamboo.bridgebuilder.map.Map;

public class ClearStairVertices implements Command
{
    private Map map;
    private FloatArray oldVertices;

    public ClearStairVertices(Map map, FloatArray oldVertices)
    {
        this.map = map;
        this.oldVertices = new FloatArray(oldVertices);
    }

    @Override
    public void execute()
    {
        this.map.input.stairVertices.clear();
    }

    @Override
    public void undo()
    {
        this.map.input.stairVertices.addAll(this.oldVertices);
    }
}
