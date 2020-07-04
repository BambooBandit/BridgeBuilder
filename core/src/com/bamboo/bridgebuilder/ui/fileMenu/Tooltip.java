package com.bamboo.bridgebuilder.ui.fileMenu;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.bamboo.bridgebuilder.BridgeBuilder;

public class Tooltip extends Stack
{
    public BridgeBuilder editor;
    public Label label;

    public Tooltip(BridgeBuilder editor, String tooltip, String shortcut, Skin skin)
    {
        this.editor = editor;
        this.label = new Label(tooltip + " " + shortcut, skin);
    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        if(editor.activeMap != null)
            this.label.setPosition(editor.activeMap.propertyMenu.getWidth() + 10, 10);
//            this.label.setPosition((Gdx.graphics.getWidth() / 6f) + 10, 10);
        else
            this.label.setPosition(10, 10);
        this.label.draw(batch, parentAlpha);
    }
}