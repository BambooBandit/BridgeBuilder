package com.bamboo.bridgebuilder.ui.spriteMenu;

public enum SpriteMenuTools
{
    SPRITE("sprite"), SPRITESELECT(null, "spriteLayer"), LINES(null, "lines"), DARK_MODE(null, "darkMode"), NEW_SPRITESHEET(null, "newSpriteLayer");

    public String name;
    public String type;
    SpriteMenuTools(String type, String name)
    {
        this.type = type;
        this.name = name;
    }
    SpriteMenuTools(String type)
    {
        this.type = type;
    }
}
