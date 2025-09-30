package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.map.Layer;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.SpriteLayer;

public class RemoveLayer implements Command
{
    private Map map;
    private Layer layer;
    private int layerIndex;
    private boolean selected;
    private boolean secondarySelected;

    private Array<LayerOverride> chainedCommands; // Used for keeping track of layer overrides

    public RemoveLayer(Map map, Layer layer)
    {
        this.map = map;
        this.layer = layer;
        this.selected = layer.layerField.isSelected;
        this.secondarySelected = layer.layerField.isSecondarySelected;

        for(int i = 0; i < this.map.layerMenu.layers.size; i ++)
        {
            if(this.map.layerMenu.layers.get(i) == layer.layerField)
            {
                this.layerIndex = i;
                break;
            }
        }

        if(layer instanceof SpriteLayer)
        {
            if (layer.overrideSprite != null)
            {
                LayerOverride layerOverride = new LayerOverride((SpriteLayer) layer, null, true);
                addCommandToChain(layerOverride);
            }
            if (layer.overrideSpriteBack != null)
            {
                LayerOverride layerOverride = new LayerOverride((SpriteLayer) layer, null, false);
                addCommandToChain(layerOverride);
            }
        }
    }

    @Override
    public void execute()
    {
        if (this.chainedCommands != null)
            for (int i = 0; i < this.chainedCommands.size; i++)
                this.chainedCommands.get(i).execute();

        if(this.selected)
        {
            this.layer.layerField.unselect();
            this.map.selectedLayer = null;
        }
        if(this.secondarySelected)
        {
            this.layer.layerField.secondaryUnselect();
            this.map.secondarySelectedLayer = null;
        }
        this.map.layerMenu.removeLayer(this.layer.layerField);
        this.map.propertyMenu.rebuild();
        this.map.updateLayerZColor();
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
        if(this.secondarySelected)
        {
            this.layer.layerField.secondarySelect();
            this.map.secondarySelectedLayer = this.layer;
        }
        this.map.propertyMenu.rebuild();

        if (this.chainedCommands != null)
            for (int i = 0; i < this.chainedCommands.size; i++)
                this.chainedCommands.get(i).undo();
        this.map.updateLayerZColor();
    }

    public void addCommandToChain(LayerOverride command)
    {
        if (this.chainedCommands == null)
            this.chainedCommands = new Array<>();
        this.chainedCommands.add(command);
    }
}
