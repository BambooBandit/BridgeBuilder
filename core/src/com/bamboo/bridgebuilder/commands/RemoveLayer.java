package com.bamboo.bridgebuilder.commands;

import com.bamboo.bridgebuilder.map.Layer;
import com.bamboo.bridgebuilder.map.Map;

public class RemoveLayer implements Command
{
    private Map map;
    private Layer layer;
    private int layerIndex;
    private boolean selected;

    public RemoveLayer(Map map, Layer layer)
    {
        this.map = map;
        this.layer = layer;
        this.selected = layer.layerField.isSelected;

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
        if(this.selected)
        {
            this.layer.layerField.unselect();
            this.map.selectedLayer = null;
        }
        this.map.layerMenu.removeLayer(this.layer.layerField);
        this.map.propertyMenu.rebuild();
    }

    @Override
    public void undo()
    {
        this.map.layerMenu.addLayer(this.layer, this.layerIndex);
        if(this.selected)
        {
            this.layer.layerField.select();
            this.map.selectedLayer = this.layer;
        }
        this.map.propertyMenu.rebuild();
    }
}
