package com.bamboo.bridgebuilder.ui.manipulators;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapSprite;

public class RotationBox extends ManipulatorBox
{
    public RotationBox()
    {
        this.sprite = new Sprite(new Texture("ui/rotate.png")); // TODO pack this
        this.sprite.setSize(width, height);
    }
}
