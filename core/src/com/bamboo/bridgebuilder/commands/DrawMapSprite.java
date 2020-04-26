package com.bamboo.bridgebuilder.commands;

import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapSprite;
import com.bamboo.bridgebuilder.map.SpriteLayer;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

public class DrawMapSprite implements Command
{
    private Map map;
    private SpriteLayer layer;
    private MapSprite mapSprite = null;
    private float x;
    private float y;

    public DrawMapSprite(Map map, SpriteLayer layer, float x, float y)
    {
        this.map = map;
        this.layer = layer;
        this.x = x;
        this.y = y;
    }

    @Override
    public void execute()
    {
        if(mapSprite == null)
        {
            SpriteLayer layer = (SpriteLayer) map.selectedLayer;
            SpriteTool spriteTool = map.getSpriteToolFromSelectedTools();
            this.mapSprite = new MapSprite(map, layer, spriteTool, x, y);
        }
        layer.addMapSprite(this.mapSprite);
    }

    @Override
    public void undo()
    {
        layer.children.removeValue(this.mapSprite, true);
    }
}
