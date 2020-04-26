package com.bamboo.bridgebuilder.ui.propertyMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.EditorAssets;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.ColorPropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.LabelFieldPropertyValuePropertyField;
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

        ColorPropertyField mapRGBAProperty = new ColorPropertyField(skin, menu, this.properties, false, 0, 0, 0, 1);
        LabelFieldPropertyValuePropertyField mapVirtualHeightProperty = new LabelFieldPropertyValuePropertyField("Virtual Height", "20", skin, menu, properties, false);
        this.lockedProperties = new Array<>();
        this.lockedProperties.add(mapRGBAProperty);
        this.lockedProperties.add(mapVirtualHeightProperty);
        this.properties = new Array<>();

        this.apply = new TextButton("Apply", skin);
        this.apply.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                ColorPropertyField mapRGBAProperty = Utils.getLockedColorField(lockedProperties);
                menu.map.rayHandler.setAmbientLight(Float.parseFloat(mapRGBAProperty.rValue.getText()), Float.parseFloat(mapRGBAProperty.gValue.getText()), Float.parseFloat(mapRGBAProperty.bValue.getText()), Float.parseFloat(mapRGBAProperty.aValue.getText()));

                LabelFieldPropertyValuePropertyField mapVirtualHeightProperty = Utils.getLockedPropertyField(lockedProperties, "Virtual Height");
                menu.map.virtualHeight = Float.parseFloat(mapVirtualHeightProperty.value.getText());
                menu.map.virtualWidth = menu.map.virtualHeight * Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
                menu.map.camera.viewportWidth = menu.map.virtualWidth;
                menu.map.camera.viewportHeight = menu.map.virtualHeight;
            }
        });

        this.table.add(mapRGBAProperty).padBottom(1).row();
        this.table.add(mapVirtualHeightProperty).padBottom(1).row();
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

        float newHeight = textFieldHeight * 3;

        this.background.setBounds(0, 0, width, newHeight);
        this.stack.setSize(width, newHeight);
        this.stack.invalidateHierarchy();

        if(height == 0)
            super.setSize(0, 0);
        else
            super.setSize(width, newHeight);
    }
}