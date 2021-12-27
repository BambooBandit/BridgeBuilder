package com.bamboo.bridgebuilder.ui.spriteMenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.BBColors;
import com.bamboo.bridgebuilder.EditorAssets;
import com.bamboo.bridgebuilder.map.AttachedMapObjectManager;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapObject;
import com.bamboo.bridgebuilder.map.MapSprite;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;

import static com.bamboo.bridgebuilder.BridgeBuilder.toolHeight;

/** The sprite buttons in the SpriteMenu. Holds data that belongs with the sprite, such as locked properties.*/
public class SpriteTool extends SpriteMenuTool
{
    public Array<PropertyField> lockedProperties; // properties such as probability. They belong to all MapSprites and cannot be removed
    public Array<PropertyField> properties;
    public Label toolNameLabel;
    public Label spriteNameLabel;

    public int x, y;
    public String name;

    public TextureRegion textureRegion;

    public Array<Sprite> previewSprites;
    public Array<TextureAtlas.AtlasSprite> topSprites;

    public Array<AttachedMapObjectManager> attachedMapObjectManagers;

    public SpriteTool nextTool;
    public SpriteTool previousTool;

    public SpriteTool(SpriteMenuTools tool, SpriteSheet sheet, Image image, TextureRegion textureRegion, String name, int x, int y, SpriteMenuToolPane spriteMenuToolPane, Skin skin)
    {
        super(tool, sheet, image, spriteMenuToolPane, skin);
        this.textureRegion = textureRegion;
        this.previewSprites = new Array();
        Sprite sprite;
        if(textureRegion instanceof TextureAtlas.AtlasRegion)
            sprite = new TextureAtlas.AtlasSprite((TextureAtlas.AtlasRegion) textureRegion);
        else
            sprite = new Sprite(textureRegion);
        sprite.setSize(sprite.getWidth() / 64, sprite.getHeight() / 64);
        sprite.setOriginCenter();
        this.previewSprites.add(sprite);

        this.lockedProperties = new Array<>();
        this.properties = new Array<>();
        this.name = name;
        this.x = x;
        this.y = y;

        this.toolNameLabel = new Label(name, skin);
        this.toolNameLabel.setHeight(toolHeight);
        this.spriteNameLabel = new Label(name + ", " + sheet.name, skin);
        this.spriteNameLabel.setHeight(toolHeight);
    }

    @Override
    public void select()
    {
        this.image.setColor(Color.GREEN);

        Table cellTable = (Table) this.image.getParent().getParent();
        SpriteDrawable background = (SpriteDrawable) cellTable.getBackground();
        Color backgroundColor = background.getSprite().getColor();
        if(backgroundColor.equals(Color.WHITE) || backgroundColor.equals(BBColors.darkDarkGrey))
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
                background.getSprite().setColor(BBColors.darkDarkGrey);
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

    public void setTopSprites(String topSpriteName)
    {
        if(this.topSprites == null)
            this.topSprites = new Array();
        this.topSprites.clear();
        if(this.previewSprites.size > 1)
            this.previewSprites.removeRange(1, this.previewSprites.size - 1);
        int digits = 0;
        for(int i = 0; i < topSpriteName.length() - 1; i ++)
        {
            if(Character.isDigit(topSpriteName.charAt(i)))
                digits++;
            else
                break;
        }
        if(digits == 0)
        {
            TextureRegion textureRegion = EditorAssets.getTextureRegion(sheet.name, topSpriteName);
            if (textureRegion == null)
                return;
            TextureAtlas.AtlasSprite sprite = new TextureAtlas.AtlasSprite((TextureAtlas.AtlasRegion) textureRegion);
            sprite.setSize(sprite.getWidth() / 64, sprite.getHeight() / 64);
            sprite.setOriginCenter();
            this.topSprites.add(sprite);
            this.previewSprites.add(sprite);
        }
        else
        {
            String topSpriteNoDigits = topSpriteName.substring(digits, topSpriteName.length());
            int number = Integer.parseInt(topSpriteName.substring(0, digits));
            TextureRegion textureRegion = EditorAssets.getTextureRegion(sheet.name, topSpriteName);
            while(textureRegion != null)
            {
                TextureAtlas.AtlasSprite sprite = new TextureAtlas.AtlasSprite((TextureAtlas.AtlasRegion) textureRegion);
                sprite.setSize(sprite.getWidth() / 64, sprite.getHeight() / 64);
                this.topSprites.add(sprite);
                sprite = new TextureAtlas.AtlasSprite((TextureAtlas.AtlasRegion) textureRegion);
                sprite.setSize(sprite.getWidth() / 64, sprite.getHeight() / 64);
                sprite.setOriginCenter();
                this.previewSprites.add(sprite);
                number ++;
                textureRegion = EditorAssets.getTextureRegion(sheet.name, (number + topSpriteNoDigits));
            }
        }
    }

    public void createAttachedMapObject(Map map, MapObject mapObject, MapSprite mapSprite)
    {
        if(this.attachedMapObjectManagers == null)
            this.attachedMapObjectManagers = new Array<>();
        this.attachedMapObjectManagers.add(new AttachedMapObjectManager(map, this, mapObject, mapSprite));
    }

    public void createAttachedMapObject(Map map, MapObject mapObject, float offsetX, float offsetY)
    {
        if(this.attachedMapObjectManagers == null)
            this.attachedMapObjectManagers = new Array<>();
        this.attachedMapObjectManagers.add(new AttachedMapObjectManager(map, this, mapObject, offsetX, offsetY));
    }

    public boolean removeAttachedMapObject(MapObject mapObject)
    {
        if(this.attachedMapObjectManagers == null)
            return false;
        for(int i = 0; i < this.attachedMapObjectManagers.size; i ++)
        {
            if (this.attachedMapObjectManagers.get(i).deleteAttachedMapObjectFromAll(mapObject))
            {
                this.attachedMapObjectManagers.removeIndex(i);
                return true;
            }
        }
        return false;
    }

    public boolean hasAttachedMapObjects()
    {
        if(this.attachedMapObjectManagers == null)
            return false;
        if(this.attachedMapObjectManagers.size > 0)
            return true;
        return false;
    }
}
