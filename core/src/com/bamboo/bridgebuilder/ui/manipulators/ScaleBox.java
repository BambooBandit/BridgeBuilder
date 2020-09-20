package com.bamboo.bridgebuilder.ui.manipulators;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class ScaleBox extends ManipulatorBox
{
    public static Texture scaleTexture = null;
    public ScaleBox()
    {
        if(scaleTexture == null)
            scaleTexture = new Texture("ui/scale.png");
        this.sprite = new Sprite(scaleTexture); // TODO pack this
        this.sprite.setSize(width, height);
        hover(false);
    }

    @Override
    public void setPosition(float x, float y)
    {
        super.setPosition(x + (2f * width * scale), y);
        this.x = x;
        this.y = y;
    }
}
