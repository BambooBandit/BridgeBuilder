package com.bamboo.bridgebuilder.commands;

import com.bamboo.bridgebuilder.map.Layer;
import com.bamboo.bridgebuilder.map.Map;

public class SelectLayer implements Command
{
    private Map map;
    private Layer oldLayer;
    private Layer newLayer;
    private boolean shiftHeld;

    public SelectLayer(Map map, Layer oldLayer, Layer newLayer, boolean shiftHeld)
    {
        this.map = map;
        this.oldLayer = oldLayer;
        this.newLayer = newLayer;
        this.shiftHeld = shiftHeld;
    }

    @Override
    public void execute()
    {
        boolean wasSelected = this.newLayer.layerField.isSelected;
        this.map.layerMenu.unselectAll();
        if (!shiftHeld || (shiftHeld && !wasSelected))
        {
            this.newLayer.layerField.select();
            map.selectedLayer = this.newLayer;
        }
        map.propertyMenu.rebuild();

        if(map.editor.fileMenu.toolPane.depth.selected)
            map.colorizeDepth();
    }

    @Override
    public void undo()
    {
        boolean wasSelected = false;
        if(this.oldLayer != null)
            wasSelected = this.oldLayer.layerField.isSelected;
        this.map.layerMenu.unselectAll();
        if (!shiftHeld || (shiftHeld && !wasSelected))
        {
            if(this.oldLayer != null)
            {
                this.oldLayer.layerField.select();
                map.selectedLayer = this.oldLayer;
            }
        }
        map.propertyMenu.rebuild();

        if(map.editor.fileMenu.toolPane.depth.selected)
            map.colorizeDepth();
    }
}
