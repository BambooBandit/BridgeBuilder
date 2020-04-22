package com.bamboo.bridgebuilder.ui.manipulators;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapSprite;

public class ScaleBox extends ManipulatorBox
{
    public ScaleBox()
    {
        this.sprite = new Sprite(new Texture("ui/scale.png")); // TODO pack this
        this.sprite.setSize(width, height);
    }
}
