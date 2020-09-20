package com.bamboo.bridgebuilder.ui.manipulators;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class MoveBox extends ManipulatorBox
{
    public static Texture moveTexture = null;

    public MoveBox()
    {
        if(moveTexture == null)
            moveTexture = new Texture("ui/move.png");
        this.sprite = new Sprite(moveTexture); // TODO pack this
        this.sprite.setOriginCenter();
        this.sprite.setSize(this.width, height);
        hover(false);
    }
}
