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
        if(this.mapSprite == null)
        {
            SpriteLayer layer = (SpriteLayer) this.map.selectedLayer;
            SpriteTool spriteTool = this.map.getSpriteToolFromSelectedTools();
            this.mapSprite = new MapSprite(this.map, layer, spriteTool, this.x, this.y);
            this.map.shuffleRandomSpriteTool();
        }
        this.layer.addMapSprite(this.mapSprite);

        if(this.map.editor.fileMenu.toolPane.depth.selected)
            this.map.colorizeDepth();
    }

    @Override
    public void undo()
    {
        this.layer.children.removeValue(this.mapSprite, true);

        if(this.map.editor.fileMenu.toolPane.depth.selected)
            this.map.colorizeDepth();

        if(this.map.editor.fileMenu.toolPane.spriteGridColors.selected)
            this.map.updateLayerSpriteGrids();
    }
}
