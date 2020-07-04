package com.bamboo.bridgebuilder.commands;

import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapSprite;

public class DisableAttachedSpriteEditMode implements Command
{
    public Map map;
    public MapSprite parentSprite;

    public DisableAttachedSpriteEditMode(Map map, MapSprite parentSprite)
    {
        this.map = map;
        this.parentSprite = parentSprite;
    }

    @Override
    public void execute()
    {
        map.selectedLayer = map.selectedLayerPriorToAttachedSpriteEditMode;
        map.selectedLayer.layerField.select();
        map.editAttachedMapSprite = null;
        map.editor.fileMenu.toolPane.attachedSprites.unselect();
    }

    @Override
    public void undo()
    {
        map.selectedLayerPriorToAttachedSpriteEditMode = map.selectedLayer;
        map.selectedLayer.layerField.unselect();
        map.selectedLayer = this.parentSprite.attachedSprites;
        map.editAttachedMapSprite = parentSprite;
        map.editor.fileMenu.toolPane.attachedSprites.select();
    }
}
