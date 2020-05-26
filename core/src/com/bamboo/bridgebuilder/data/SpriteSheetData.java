package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteSheet;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

import java.util.ArrayList;

public class SpriteSheetData
{
    public String name;
    public ArrayList<ToolData> tools;

    public SpriteSheetData(){}
    public SpriteSheetData(Map map, SpriteSheet spriteSheet)
    {
        this.name = spriteSheet.name;
        this.tools = new ArrayList<>();

        for(int i = 0; i < map.spriteMenu.spriteTable.getChildren().size; i ++)
        {
            if(map.spriteMenu.spriteTable.getChildren().get(i) instanceof SpriteTool)
            {
                SpriteTool spriteTool = ((SpriteTool) map.spriteMenu.spriteTable.getChildren().get(i));
                if(spriteTool.sheet.name == this.name)
                    this.tools.add(new ToolData(spriteTool));
            }
        }
    }
}
