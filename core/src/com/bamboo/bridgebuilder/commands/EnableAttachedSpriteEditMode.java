package com.bamboo.bridgebuilder.commands;

import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapSprite;
import com.bamboo.bridgebuilder.map.SpriteLayer;

public class EnableAttachedSpriteEditMode implements Command
{
    public Map map;
    public MapSprite parentSprite;

    public EnableAttachedSpriteEditMode(Map map, MapSprite parentSprite)
    {
        this.map = map;
        this.parentSprite = parentSprite;
    }

    @Override
    public void execute()
    {
        if(this.parentSprite.attachedSprites == null)
        {
            this.parentSprite.attachedSprites = new SpriteLayer(map.editor, map, null);
            this.parentSprite.attachedSprites.addMapSprite(this.parentSprite, -1);
        }
        map.selectedLayerPriorToAttachedSpriteEditMode = map.selectedLayer;
        map.selectedLayer.layerField.unselect();
        map.selectedLayer = this.parentSprite.attachedSprites;
        map.editAttachedMapSprite = parentSprite;
        map.editor.fileMenu.toolPane.attachedSprites.select();
    }

    @Override
    public void undo()
    {
        map.selectedLayer = map.selectedLayerPriorToAttachedSpriteEditMode;
        map.selectedLayer.layerField.select();
        map.editAttachedMapSprite = null;
        map.editor.fileMenu.toolPane.attachedSprites.unselect();
    }
}
