package com.bamboo.bridgebuilder.commands;

import com.bamboo.bridgebuilder.map.Layer;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.ui.layerMenu.LayerTypes;

public class CreateLayer implements Command
{
    private Map map;
    private LayerTypes type;
    public Layer layer = null;
    public Layer selectedLayer;

    public CreateLayer(Map map, LayerTypes layerType)
    {
        this.map = map;
        this.type = layerType;
        this.selectedLayer = map.selectedLayer;
    }

    @Override
    public void execute()
    {
        if(this.layer == null)
            this.layer = this.map.layerMenu.newLayer(type);
        else
            this.map.layerMenu.addLayer(this.layer);

        if(selectedLayer != null)
        {
            map.layerMenu.moveLayer(map.layers.size - map.layers.indexOf(selectedLayer, true), layer.layerField);
        }
    }

    @Override
    public void undo()
    {
        this.map.layerMenu.removeLayer(this.layer.layerField);
    }
}
