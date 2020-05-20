package com.bamboo.bridgebuilder.ui.spriteMenu;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

public class SpriteSheet
{
    public String name;
    public Label label;
    public Array<Table> children;

    public SpriteSheet(String name)
    {
        this.name = name;
        this.children = new Array<>();
    }
}
