package com.bamboo.bridgebuilder.data;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
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
        if(spriteSheet.name.startsWith("editor"))
        {
            this.name = spriteSheet.name.substring(6);
            this.name = Character.toLowerCase(this.name.charAt(0)) + this.name.substring(1);
        }
        else
            this.name = spriteSheet.name;

        for(int i = 0; i < map.spriteMenu.spriteTable.getChildren().size; i ++)
        {
            if(map.spriteMenu.spriteTable.getChildren().get(i) instanceof Table)
            {
                SpriteTool spriteTool = ((Table) map.spriteMenu.spriteTable.getChildren().get(i)).findActor("spriteTool");
                if(spriteTool.sheet.name == spriteSheet.name)
                {
                    if(this.tools == null)
                        this.tools = new ArrayList<>();
                    this.tools.add(new ToolData(spriteTool));
                }
            }
        }
    }
}
