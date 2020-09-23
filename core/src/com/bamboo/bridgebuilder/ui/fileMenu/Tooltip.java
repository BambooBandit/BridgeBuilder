package com.bamboo.bridgebuilder.ui.fileMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.Align;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.EditorAssets;

public class Tooltip extends Stack
{
    public BridgeBuilder editor;
    public Label label;
    private boolean left;

    public Tooltip(BridgeBuilder editor, String tooltip, String shortcut, Skin skin, boolean left)
    {
        this.left = left;
        this.editor = editor;
        this.label = new Label(tooltip + " " + shortcut, skin);
        if(!left)
            this.label.setAlignment(Align.right);
    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        if(left)
        {
            if (editor.activeMap != null)
                this.label.setPosition(editor.activeMap.propertyMenu.getWidth() + 10, 10);
            else
                this.label.setPosition(10, 10);
        }
        else // right
        {
            EditorAssets.getGlyph().setText(EditorAssets.getFont(), label.getText());
            float width = EditorAssets.getGlyph().width / 2f;
            if (editor.activeMap != null)
                this.label.setPosition(Gdx.graphics.getWidth() - editor.activeMap.spriteMenu.getWidth() - 10 - width, 10);
            else
                this.label.setPosition(Gdx.graphics.getWidth() - 10 - width, 10);
        }
        this.label.draw(batch, parentAlpha);
    }
}