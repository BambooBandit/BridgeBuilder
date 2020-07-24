package com.bamboo.bridgebuilder.ui.manipulators;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class MoveBox extends ManipulatorBox
{
    public MoveBox()
    {
        this.sprite = new Sprite(new Texture("ui/move.png")); // TODO pack this
        this.sprite.setOriginCenter();
        this.sprite.setSize(this.width, height);
        hover(false);
    }
}
