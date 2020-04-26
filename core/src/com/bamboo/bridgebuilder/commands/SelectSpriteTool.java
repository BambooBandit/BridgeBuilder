package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteMenuTools;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

public class SelectSpriteTool implements Command
{
    private Map map;
    private boolean ctrlHeld;

    private SpriteTool clickedSpriteTool;
    private Array<SpriteTool> oldSelectedSpriteTools;

    public SelectSpriteTool(Map map, SpriteTool clickedSpriteTool, boolean ctrlHeld)
    {
        this.map = map;
        this.ctrlHeld = ctrlHeld;
        this.clickedSpriteTool = clickedSpriteTool;
    }

    @Override
    public void execute()
    {
        if(this.oldSelectedSpriteTools == null)
            this.oldSelectedSpriteTools = new Array<>(map.spriteMenu.selectedSpriteTools);
        else
        {
            this.oldSelectedSpriteTools.clear();
            this.oldSelectedSpriteTools.addAll(map.spriteMenu.selectedSpriteTools);
        }

        for(int i = 0; i < map.spriteMenu.spriteTable.getChildren().size; i ++)
        {
            if(map.spriteMenu.spriteTable.getChildren().get(i) instanceof SpriteTool)
            {
                SpriteTool tool = (SpriteTool) map.spriteMenu.spriteTable.getChildren().get(i);
                if (tool == clickedSpriteTool)
                {
                    if (this.ctrlHeld)
                    {
                        if (tool.isSelected)
                        {
                            map.spriteMenu.selectedSpriteTools.removeValue(tool, false);
                            this.map.propertyMenu.rebuild();
                            tool.unselect();
                        }
                        else
                        {
                            map.spriteMenu.selectedSpriteTools.add(tool);
                            this.map.propertyMenu.rebuild();
                            tool.select();
                        }
                    }
                    else
                    {
                        map.spriteMenu.selectedSpriteTools.clear();
                        map.spriteMenu.selectedSpriteTools.add(tool);
                        this.map.propertyMenu.rebuild();
                        tool.select();
                    }
                }
                else if (tool.tool == SpriteMenuTools.SPRITE)
                {
                    if (!this.ctrlHeld)
                    {
                        map.spriteMenu.selectedSpriteTools.removeValue(tool, false);
                        this.map.propertyMenu.rebuild();
                        tool.unselect();
                    }
                }
            }
        }
    }

    @Override
    public void undo()
    {
        for(int i = 0; i < this.map.spriteMenu.selectedSpriteTools.size; i ++)
            this.map.spriteMenu.selectedSpriteTools.get(i).unselect();

        this.map.spriteMenu.selectedSpriteTools.clear();
        this.map.spriteMenu.selectedSpriteTools.addAll(this.oldSelectedSpriteTools);

        for(int i = 0; i < this.map.spriteMenu.selectedSpriteTools.size; i ++)
            this.map.spriteMenu.selectedSpriteTools.get(i).select();

        this.map.propertyMenu.rebuild();
    }
}
