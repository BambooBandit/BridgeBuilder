package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.bamboo.bridgebuilder.EditorAssets;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteMenuTools;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteSheet;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

public class CreateSpriteSheet implements Command
{
    public Map map;
    public SpriteSheet spriteSheet;
    public String name;

    public CreateSpriteSheet(Map map, String name)
    {
        this.map = map;
        this.name = name;
    }

    @Override
    public void execute()
    {
        if(this.spriteSheet == null)
        {
            this.spriteSheet = new SpriteSheet(name);
            this.map.spriteMenu.spriteSheets.add(this.spriteSheet);

            EditorAssets.assets.load(name + ".atlas", TextureAtlas.class);
            EditorAssets.assets.finishLoading();
            EditorAssets.setMapAtlas(name, EditorAssets.getAssets().get(name + ".atlas"));

            // Add all the sprites to the spriteTable as Images
            this.map.spriteMenu.spriteTable.padLeft(1);
            this.map.spriteMenu.spriteTable.padTop(1);
            Label label = new Label(name, EditorAssets.getUISkin());
            this.spriteSheet.label = label;
            this.map.spriteMenu.spriteTable.add(label).width(0).row();
            boolean checkerDark = false;
            boolean rowOdd = true;
            for(int i = 0; i < EditorAssets.getMapAtlas(name).getRegions().size; i ++)
            {
                TextureAtlas.AtlasRegion spriteRegion = EditorAssets.getMapAtlas(name).getRegions().get(i);

                SpriteTool spriteTool = new SpriteTool(SpriteMenuTools.SPRITE, this.spriteSheet, new Image(spriteRegion), spriteRegion, spriteRegion.name, 0, 0, this.map.spriteMenu.toolPane, EditorAssets.getUISkin());
                spriteTool.setName("spriteTool");
                this.map.propertyMenu.setSpriteProperties(spriteTool);
                SpriteDrawable backgroundDrawable = new SpriteDrawable(new Sprite(new Texture("ui/whitePixel.png")));
                if(checkerDark)
                    backgroundDrawable.getSprite().setColor(Color.WHITE);
                else
                    backgroundDrawable.getSprite().setColor(Color.LIGHT_GRAY);
                Table cellTable = new Table(EditorAssets.getUISkin());
                cellTable.setName(this.spriteSheet.name);
                cellTable.setTouchable(Touchable.enabled);
                cellTable.addListener(new ClickListener()
                {
                    @Override
                    public void clicked(InputEvent event, float x, float y)
                    {
                        map.spriteMenu.toolPane.selectTool(spriteTool);
                    }
                });
                cellTable.background(backgroundDrawable);
                cellTable.add(spriteTool).grow();
                this.spriteSheet.children.add(cellTable);

                float minimumArea = 300;
                float maximumArea = 1000;
                float newWidth = spriteTool.image.getWidth() / 25;
                float newHeight = spriteTool.image.getHeight() / 25;
                float multiplier = 1;
                if(newWidth * newHeight < minimumArea)
                    multiplier = (float) Math.sqrt(minimumArea / (newWidth * newHeight));
                else if(newWidth * newHeight > maximumArea)
                    multiplier = (float) Math.sqrt(maximumArea/ (newWidth * newHeight));
                newWidth *= multiplier;
                newHeight *= multiplier;
                spriteTool.image.setSize(newWidth, newHeight);
                spriteTool.setSize(newWidth, newHeight);
                checkerDark = !checkerDark;
                this.map.spriteMenu.spriteTable.add(cellTable).center().grow();
                if((i + 1) % 3 == 0)
                {
                    rowOdd = !rowOdd;
                    checkerDark = !rowOdd;
                    this.map.spriteMenu.spriteTable.row();
                }
            }

            this.map.spriteMenu.spriteTable.row();
            this.map.spriteMenu.spriteTable.padBottom(500).row();
        }
        else
        {
            // Re-add sprite sheet ui
            this.map.spriteMenu.spriteTable.add(this.spriteSheet.label).width(0).row();
            for (int i = 0; i < this.spriteSheet.children.size; i++)
            {
                Table child = this.spriteSheet.children.get(i);
                this.map.spriteMenu.spriteTable.add(child).grow();
                if((i + 1) % 3 == 0)
                    this.map.spriteMenu.spriteTable.row();
            }
            this.map.spriteMenu.spriteSheets.add(this.spriteSheet);

            map.spriteMenu.spriteTable.row();
            map.spriteMenu.spriteTable.padBottom(500).row();
        }
    }

    @Override
    public void undo()
    {
        // Delete sprite sheet ui
        this.map.spriteMenu.spriteTable.removeActor(this.spriteSheet.label);
        for(int k = 0; k < this.spriteSheet.children.size; k ++)
        {
            Table child = this.spriteSheet.children.get(k);
            SpriteTool spriteTool = child.findActor("spriteTool");
            if(spriteTool.isSelected)
                spriteTool.unselect();
            this.map.spriteMenu.selectedSpriteTools.removeValue(spriteTool, true);
            this.map.spriteMenu.spriteTable.removeActor(child);
        }
        this.map.spriteMenu.spriteSheets.removeValue(this.spriteSheet, true);
    }
}
