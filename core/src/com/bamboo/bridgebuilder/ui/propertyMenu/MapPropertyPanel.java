package com.bamboo.bridgebuilder.ui.propertyMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.EditorAssets;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.ColorPropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;

public class MapPropertyPanel extends Group
{
    public static int textFieldHeight = 32;

    private BridgeBuilder editor;
    private PropertyMenu menu;

    private Image background;
    private Stack stack;
    public Table table; // Holds all the text fields

    public Array<PropertyField> lockedProperties;
    public Array<PropertyField> properties;

    public TextButton apply;


    public MapPropertyPanel(Skin skin, PropertyMenu menu, BridgeBuilder editor)
    {
        this.editor = editor;
        this.menu = menu;

        this.background = new Image(EditorAssets.getUIAtlas().createPatch("load-background"));
        this.stack = new Stack();
        this.table = new Table();
        this.table.left().top();

        this.lockedProperties = new Array<>();
        ColorPropertyField mapRGBAProperty = new ColorPropertyField(skin, menu, false, 0, 0, 0, 1);
        this.lockedProperties.add(mapRGBAProperty);
        this.properties = new Array<>();

        this.apply = new TextButton("Apply", skin);
        this.apply.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                ColorPropertyField mapRGBAProperty = Utils.getLockedColorField(lockedProperties);
                menu.map.rayHandler.setAmbientLight(Float.parseFloat(mapRGBAProperty.rValue.getText()), Float.parseFloat(mapRGBAProperty.gValue.getText()), Float.parseFloat(mapRGBAProperty.bValue.getText()), Float.parseFloat(mapRGBAProperty.aValue.getText()));
            }
        });

        this.table.add(mapRGBAProperty).padBottom(1).row();
        this.table.add(this.apply).padBottom(1).row();

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

        float newHeight = textFieldHeight * 2;

        this.background.setBounds(0, 0, width, newHeight);
        this.stack.setSize(width, newHeight);
        this.stack.invalidateHierarchy();

        if(height == 0)
            super.setSize(0, 0);
        else
            super.setSize(width, newHeight);
    }
}