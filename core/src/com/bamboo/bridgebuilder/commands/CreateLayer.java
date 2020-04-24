package com.bamboo.bridgebuilder.commands;

import com.bamboo.bridgebuilder.map.Layer;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.ui.layerMenu.LayerTypes;

public class CreateLayer implements Command
{
    private Map map;
    private LayerTypes type;
    public Layer layer = null;

    public CreateLayer(Map map, LayerTypes layerType)
    {
        this.map = map;
        this.type = layerType;
    }

    @Override
    public void execute()
    {
        if(this.layer == null)
            this.layer = this.map.layerMenu.newLayer(type);
        else
            this.map.layerMenu.addLayer(this.layer);
    }

    @Override
    public void undo()
    {
        this.map.layerMenu.removeLayer(this.layer.layerField);
    }
}
