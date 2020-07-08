package com.bamboo.bridgebuilder.ui.manipulators;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class RotationBox extends ManipulatorBox
{
    public RotationBox()
    {
        this.sprite = new Sprite(new Texture("ui/rotate.png")); // TODO pack this
        this.sprite.setSize(width, height);
        hover(false);
    }

    @Override
    public void setPosition(float x, float y)
    {
        super.setPosition(x + width * scale, y);
        this.x = x;
        this.y = y;
    }
}
