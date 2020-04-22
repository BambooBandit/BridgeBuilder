package com.bamboo.bridgebuilder.ui.manipulators;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapSprite;

public abstract class ManipulatorBox
{
    public Sprite sprite;
    protected Rectangle rectangle;
    protected int width, height;
    public ManipulatorBox()
    {
        this.width = 25;
        this.height = 25;
        this.rectangle = new Rectangle(0, 0, width, height);
    }

    public void setPosition(float x, float y)
    {
        this.sprite.setPosition(x, y);
        this.rectangle.setPosition(x, y);
    }

    public boolean contains(float x, float y)
    {
        return this.rectangle.contains(x, y);
    }
}
