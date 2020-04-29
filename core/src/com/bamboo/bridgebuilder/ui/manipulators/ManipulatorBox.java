package com.bamboo.bridgebuilder.ui.manipulators;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public abstract class ManipulatorBox
{
    public Sprite sprite;
    protected Rectangle rectangle;
    public float width, height;
    public ManipulatorBox()
    {
        this.width = .625f;
        this.height = .625f;
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
