package com.bamboo.bridgebuilder.commands;

import com.bamboo.bridgebuilder.map.Layer;
import com.bamboo.bridgebuilder.map.Map;

public class SelectLayer implements Command
{
    private Map map;
    private Layer oldLayer;
    private Layer newLayer;
    private boolean ctrlHeld;

    public SelectLayer(Map map, Layer oldLayer, Layer newLayer, boolean ctrlHeld)
    {
        this.map = map;
        this.oldLayer = oldLayer;
        this.newLayer = newLayer;
        this.ctrlHeld = ctrlHeld;
    }

    @Override
    public void execute()
    {
        this.map.layerMenu.unselectAll();
        if (!ctrlHeld)
        {
            this.newLayer.layerField.select();
            map.selectedLayer = this.newLayer;
        }
        map.propertyMenu.rebuild();
    }

    @Override
    public void undo()
    {
        this.map.layerMenu.unselectAll();
        if (!ctrlHeld)
        {
            if(this.oldLayer != null)
            {
                this.oldLayer.layerField.select();
                map.selectedLayer = this.oldLayer;
            }
        }
        map.propertyMenu.rebuild();
    }
}
