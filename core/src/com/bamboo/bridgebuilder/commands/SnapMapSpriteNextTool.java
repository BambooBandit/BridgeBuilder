package com.bamboo.bridgebuilder.commands;

import com.bamboo.bridgebuilder.map.MapSprite;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

public class SnapMapSpriteNextTool implements Command
{
    private MapSprite from;
    private SpriteTool oldTo;
    private MapSprite to;

    public SnapMapSpriteNextTool(MapSprite from, MapSprite to)
    {
        this.from = from;
        this.oldTo = from.tool.nextTool;
        this.to = to;
    }

    @Override
    public void execute()
    {
        this.from.tool.nextTool = this.to.tool;
        {
            if(this.to != null)
            {
                this.to.tool.previousTool = this.from.tool;
            }
        }
    }

    @Override
    public void undo()
    {
        this.from.tool.nextTool = this.oldTo;
        if(this.to != null)
            this.to.tool.previousTool = null;
    }
}
