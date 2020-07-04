package com.bamboo.bridgebuilder.ui.fileMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;

public class Tooltip extends Stack
{
    public Label label;

    public Tooltip(String tooltip, String shortcut, Skin skin)
    {
        this.label = new Label(tooltip + " " + shortcut, skin);
    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        this.label.setPosition((Gdx.graphics.getWidth() / 6f) + 10, 10);
        this.label.draw(batch, parentAlpha);
    }
}