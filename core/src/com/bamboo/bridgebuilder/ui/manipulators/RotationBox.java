package com.bamboo.bridgebuilder.ui.manipulators;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class RotationBox extends ManipulatorBox
{
    public static Texture rotationTexture = null;
    public RotationBox()
    {
        if(rotationTexture == null)
            rotationTexture = new Texture("ui/rotate.png");
        this.sprite = new Sprite(rotationTexture); // TODO pack this
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
