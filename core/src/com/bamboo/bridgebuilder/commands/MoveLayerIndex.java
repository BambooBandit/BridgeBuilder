package com.bamboo.bridgebuilder.commands;

import com.bamboo.bridgebuilder.map.Layer;
import com.bamboo.bridgebuilder.map.Map;

public class MoveLayerIndex implements Command
{
    private Map map;
    private Layer layer;
    private int oldIndex;
    private int newIndex;

    public MoveLayerIndex(Map map, Layer layer, int oldIndex, int newIndex)
    {
        this.map = map;
        this.layer = layer;
        this.oldIndex = oldIndex;
        this.newIndex = newIndex;
    }

    @Override
    public void execute()
    {
        this.map.layerMenu.moveLayer(this.newIndex, this.layer.layerField);
    }

    @Override
    public void undo()
    {
        this.map.layerMenu.moveLayer(this.oldIndex, this.layer.layerField);
    }
}
