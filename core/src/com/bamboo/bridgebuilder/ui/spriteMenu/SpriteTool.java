package com.bamboo.bridgebuilder.ui.spriteMenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.EditorAssets;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;

/** The sprite buttons in the SpriteMenu. Holds data that belongs with the sprite, such as locked properties.*/
public class SpriteTool extends SpriteMenuTool implements Comparable<SpriteTool>
{

    public Array<PropertyField> lockedProperties; // properties such as probability. They belong to all MapSprites and cannot be removed
    public Array<PropertyField> properties;

    public int id, x, y;
    public String name;

    public TextureRegion textureRegion;

    public Array<Sprite> previewSprites;
    public Array<TextureAtlas.AtlasSprite> topSprites;

    public SpriteTool(SpriteMenuTools tool, SheetTools sheetTool, Image image, TextureRegion textureRegion, String name, int id, int x, int y, SpriteMenuToolPane spriteMenuToolPane, Skin skin)
    {
        super(tool, sheetTool, image, spriteMenuToolPane, skin);
        this.textureRegion = textureRegion;
        this.previewSprites = new Array();
        Sprite sprite;
        if(textureRegion instanceof TextureAtlas.AtlasRegion)
            sprite = new TextureAtlas.AtlasSprite((TextureAtlas.AtlasRegion) textureRegion);
        else
            sprite = new Sprite(textureRegion);
        sprite.setSize(sprite.getWidth() / 64, sprite.getHeight() / 64);
        this.previewSprites.add(sprite);

        this.lockedProperties = new Array<>();
        this.properties = new Array<>();
        this.name = name;
        this.id = id;
        this.x = x;
        this.y = y;
    }

    @Override
    public void select()
    {
        this.image.setColor(Color.GREEN);

        Table cellTable = (Table) this.image.getParent().getParent();
        SpriteDrawable background = (SpriteDrawable) cellTable.getBackground();
        Color backgroundColor = background.getSprite().getColor();
        if(backgroundColor.equals(Color.WHITE) || backgroundColor.equals(Color.DARK_GRAY))
            background.getSprite().setColor(Color.LIME);
        else if(backgroundColor.equals(Color.LIGHT_GRAY) || backgroundColor.equals(Color.BLACK))
            background.getSprite().setColor(Color.FOREST);

        this.isSelected = true;

        if(this.tool == SpriteMenuTools.LINES)
        {
            spriteMenuToolPane.menu.spriteTable.setDebug(true);
        }
    }

    @Override
    public void unselect()
    {
        this.image.setColor(Color.WHITE);

        Table cellTable = (Table) this.image.getParent().getParent();
        SpriteDrawable background = (SpriteDrawable) cellTable.getBackground();
        Color backgroundColor = background.getSprite().getColor();
        if(backgroundColor.equals(Color.LIME))
        {
            if(spriteMenuToolPane.darkMode.isSelected)
                background.getSprite().setColor(Color.DARK_GRAY);
            else
                background.getSprite().setColor(Color.WHITE);
        }
        else if(backgroundColor.equals(Color.FOREST))
        {
            if(spriteMenuToolPane.darkMode.isSelected)
                background.getSprite().setColor(Color.BLACK);
            else
                background.getSprite().setColor(Color.LIGHT_GRAY);
        }

        this.isSelected = false;

        if(this.tool == SpriteMenuTools.LINES)
        {
            spriteMenuToolPane.menu.spriteTable.setDebug(false);
        }
    }

    @Override
    public int compareTo(SpriteTool o)
    {
        if(id > o.id)
            return 1;
        else if(id < o.id)
            return -1;
        return 0;
    }

    public void setTopSprites(String topSpriteName)
    {
        if(this.topSprites == null)
            this.topSprites = new Array();
        this.topSprites.clear();
        if(this.previewSprites.size > 1)
            this.previewSprites.removeRange(1, this.previewSprites.size - 1);
        int digits = 0;
        for(int i = topSpriteName.length() - 1; i >= 0; i --)
        {
            if(Character.isDigit(topSpriteName.charAt(i)))
                digits++;
            else
                break;
        }
        if(digits == 0)
        {
            TextureRegion textureRegion = EditorAssets.getTextureRegion(sheetTool.name, topSpriteName);
            if (textureRegion == null)
                return;
            TextureAtlas.AtlasSprite sprite = new TextureAtlas.AtlasSprite((TextureAtlas.AtlasRegion) textureRegion);
            sprite.setSize(sprite.getWidth() / 64, sprite.getHeight() / 64);
            this.topSprites.add(sprite);
            this.previewSprites.add(sprite);
        }
        else
        {
            String topSpriteNoDigits = topSpriteName.substring(0, topSpriteName.length() - digits);
            int number = Integer.parseInt(topSpriteName.substring(topSpriteName.length() - digits));
            TextureRegion textureRegion = EditorAssets.getTextureRegion(sheetTool.name, topSpriteName);
            while(textureRegion != null)
            {
                TextureAtlas.AtlasSprite sprite = new TextureAtlas.AtlasSprite((TextureAtlas.AtlasRegion) textureRegion);
                sprite.setSize(sprite.getWidth() / 64, sprite.getHeight() / 64);
                this.topSprites.add(sprite);
                sprite = new TextureAtlas.AtlasSprite((TextureAtlas.AtlasRegion) textureRegion);
                sprite.setSize(sprite.getWidth() / 64, sprite.getHeight() / 64);
                this.previewSprites.add(sprite);
                number ++;
                textureRegion = EditorAssets.getTextureRegion(sheetTool.name, (topSpriteNoDigits + number));
            }
        }
    }
}
