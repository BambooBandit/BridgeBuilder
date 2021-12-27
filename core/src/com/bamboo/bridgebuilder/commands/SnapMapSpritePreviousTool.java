package com.bamboo.bridgebuilder.commands;

import com.bamboo.bridgebuilder.map.MapSprite;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

public class SnapMapSpritePreviousTool implements Command
{
    private MapSprite from;
    private SpriteTool oldTo;
    private MapSprite to;

    public SnapMapSpritePreviousTool(MapSprite from, MapSprite to)
    {
        this.from = from;
        this.oldTo = from.tool.previousTool;
        this.to = to;
    }

    @Override
    public void execute()
    {
        this.from.tool.previousTool = this.to.tool;
        {
            if(this.to != null)
            {
                this.to.tool.nextTool = this.from.tool;
            }
        }
    }

    @Override
    public void undo()
    {
        this.from.tool.previousTool = this.oldTo;
        if(this.to != null)
            this.to.tool.nextTool = null;
    }
}
