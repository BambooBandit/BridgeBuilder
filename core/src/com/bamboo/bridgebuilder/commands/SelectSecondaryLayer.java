package com.bamboo.bridgebuilder.commands;

import com.bamboo.bridgebuilder.map.Layer;
import com.bamboo.bridgebuilder.map.Map;

public class SelectSecondaryLayer implements Command
{
    private Map map;
    private Layer oldLayer;
    private Layer newLayer;
    private boolean altHeld;

    public SelectSecondaryLayer(Map map, Layer oldLayer, Layer newLayer, boolean altHeld)
    {
        this.map = map;
        this.oldLayer = oldLayer;
        this.newLayer = newLayer;
        this.altHeld = altHeld;
    }

    @Override
    public void execute()
    {
        boolean wasSelected = this.newLayer.layerField.isSecondarySelected;
        this.map.layerMenu.secondaryUnselect();
        if (!altHeld || (altHeld && !wasSelected))
        {
            this.newLayer.layerField.secondarySelect();
            map.secondarySelectedLayer = this.newLayer;
        }
        map.propertyMenu.rebuild();

        if(map.editor.fileMenu.toolPane.depth.selected)
            map.colorizeDepth();
        map.colorizeGroup();
    }

    @Override
    public void undo()
    {
        boolean wasSelected = false;
        if(this.oldLayer != null)
            wasSelected = this.oldLayer.layerField.isSecondarySelected;
        this.map.layerMenu.secondaryUnselect();
        if (!altHeld || (altHeld && !wasSelected))
        {
            if(this.oldLayer != null)
            {
                this.oldLayer.layerField.secondarySelect();
                map.secondarySelectedLayer = this.oldLayer;
            }
        }
        map.propertyMenu.rebuild();

        if(map.editor.fileMenu.toolPane.depth.selected)
            map.colorizeDepth();
        map.colorizeGroup();
    }
}
