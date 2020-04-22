package com.bamboo.bridgebuilder.ui.manipulators;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class MoveBox extends ManipulatorBox
{
    public MoveBox()
    {
        this.sprite = new Sprite(new Texture("ui/move.png")); // TODO pack this
        this.sprite.setSize(this.width, height);
    }
}
