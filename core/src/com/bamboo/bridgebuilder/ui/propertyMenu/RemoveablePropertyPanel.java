package com.bamboo.bridgebuilder.ui.propertyMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.EditorAssets;

public class RemoveablePropertyPanel extends Group
{
    public static int textFieldHeight = 35;

    private BridgeBuilder editor;
    private PropertyMenu menu;

    private Image background;
    private Stack stack;
    public Table table; // Holds all the text fields

    public RemoveablePropertyPanel(Skin skin, PropertyMenu menu, BridgeBuilder editor)
    {
        this.editor = editor;
        this.menu = menu;

        this.background = new Image(EditorAssets.getUIAtlas().createPatch("load-background"));
        this.stack = new Stack();
        this.table = new Table();
        this.table.left().bottom();

        this.stack.add(this.background);
        this.stack.add(this.table);

        this.addActor(this.stack);
    }

    @Override
    public void setSize(float width, float height)
    {
        for(int i = 0; i < this.table.getChildren().size; i ++)
        {
            this.table.getChildren().get(i).setSize(width, textFieldHeight);
            this.table.getCell(this.table.getChildren().get(i)).size(width, textFieldHeight);
        }

        this.table.invalidateHierarchy();
        float newHeight = textFieldHeight * this.table.getChildren().size;

        this.background.setBounds(0, 0, width, newHeight);
        this.stack.setSize(width, newHeight);
        this.stack.invalidateHierarchy();

        if(height == 0)
            super.setSize(0, 0);
        else
            super.setSize(width, newHeight);
    }
}