package com.bamboo.bridgebuilder.ui.manipulators;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.bamboo.bridgebuilder.BBColors;

public abstract class ManipulatorBox
{
    public float x, y;
    public Sprite sprite;
    public Rectangle rectangle;
    public float width, height, scale = 1;
    public ManipulatorBox()
    {
        this.width = .625f;
        this.height = .625f;
        this.rectangle = new Rectangle(0, 0, width, height);
    }

    public void setPosition(float x, float y)
    {
        this.x = x;
        this.y = y;
        this.sprite.setPosition(x, y);
        this.rectangle.setPosition(x, y);
    }

    public void setScale(float scale)
    {
        this.scale = scale;
        this.rectangle.setSize(width * scale, height * scale);
        this.sprite.setSize(width * scale, width * scale);
        setPosition(x, y);
    }

    public boolean contains(float x, float y)
    {
        return this.rectangle.contains(x, y);
    }

    public void hover(boolean on)
    {
        if(on)
            this.sprite.setColor(Color.WHITE);
        else
            this.sprite.setColor(BBColors.lightGray);
    }
}
