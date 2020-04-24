package com.bamboo.bridgebuilder.commands;

import com.bamboo.bridgebuilder.map.Layer;
import com.bamboo.bridgebuilder.map.Map;

public class RemoveLayer implements Command
{
    private Map map;
    private Layer layer;
    private int layerIndex;

    public RemoveLayer(Map map, Layer layer)
    {
        this.map = map;
        this.layer = layer;

        for(int i = 0; i < this.map.layerMenu.layers.size; i ++)
        {
            if(this.map.layerMenu.layers.get(i) == layer.layerField)
            {
                this.layerIndex = i;
                return;
            }
        }
    }

    @Override
    public void execute()
    {
        this.map.layerMenu.removeLayer(this.layer.layerField);
    }

    @Override
    public void undo()
    {
        this.map.layerMenu.addLayer(this.layer, this.layerIndex);
    }
}
