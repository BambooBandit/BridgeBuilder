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
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.ColorPropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.LabelFieldPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.OpaqueColorPropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;

import static com.bamboo.bridgebuilder.BridgeBuilder.toolHeight;

public class MapPropertyPanel extends Group
{
    private BridgeBuilder editor;
    private PropertyMenu menu;

    private Image background;
    private Stack stack;
    public Table table; // Holds all the text fields

    public Array<PropertyField> lockedProperties;
    public Array<PropertyField> properties;

    public TextButton apply;


    public MapPropertyPanel(Skin skin, PropertyMenu menu, BridgeBuilder editor, Map map)
    {
        this.editor = editor;
        this.menu = menu;

        this.background = new Image(EditorAssets.getUIAtlas().createPatch("load-background"));
        this.stack = new Stack();
        this.table = new Table();
        this.table.left().top();

        OpaqueColorPropertyField mapBackgroundColorProperty = new OpaqueColorPropertyField(skin, menu, this.properties, false, "Background", map.r, map.g, map.b);
        ColorPropertyField mapAmbientColorProperty = new ColorPropertyField(skin, menu, this.properties, false, "Ambient", 0, 0, 0, 1);
        OpaqueColorPropertyField shadowColorProperty = new OpaqueColorPropertyField(skin, menu, this.properties, false, "Shadows", .2f, .2f, .2f);
        OpaqueColorPropertyField fogColorProperty = new OpaqueColorPropertyField(skin, menu, this.properties, false, "Fog", .35f, .5f, .7f);

        LabelFieldPropertyValuePropertyField mapVirtualHeightProperty = new LabelFieldPropertyValuePropertyField("Virtual Height", "20", skin, menu, properties, false);

        TextField.TextFieldFilter valueFilter = new TextField.TextFieldFilter()
        {
            @Override
            public boolean acceptChar(TextField textField, char c)
            {
                return c == '.' || Character.isDigit(c);
            }
        };

        mapVirtualHeightProperty.value.setTextFieldFilter(valueFilter);

        this.lockedProperties = new Array<>();
        this.lockedProperties.add(mapBackgroundColorProperty);
        this.lockedProperties.add(mapAmbientColorProperty);
        this.lockedProperties.add(shadowColorProperty);
        this.lockedProperties.add(fogColorProperty);
        this.lockedProperties.add(mapVirtualHeightProperty);
        this.properties = new Array<>();

        this.apply = new TextButton("Apply", skin);
        this.apply.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                apply();
            }
        });

        this.table.add(mapBackgroundColorProperty).padBottom(1).row();
        this.table.add(mapAmbientColorProperty).padBottom(1).row();
        this.table.add(shadowColorProperty).padBottom(1).row();
        this.table.add(fogColorProperty).padBottom(1).row();
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
            this.table.getChildren().get(i).setSize(width, toolHeight);
            this.table.getCell(this.table.getChildren().get(i)).size(width, toolHeight);
        }

        float newHeight = toolHeight * 6;

        this.background.setBounds(0, 0, width, newHeight);
        this.stack.setSize(width, newHeight);
        this.stack.invalidateHierarchy();

        if(height == 0)
            super.setSize(0, 0);
        else
            super.setSize(width, newHeight);
    }

    public void apply()
    {
        OpaqueColorPropertyField mapBackgroundColorProperty = Utils.getLockedOpaqueColorField("Background", lockedProperties);
        ColorPropertyField mapAmbientColorProperty = Utils.getLockedColorField("Ambient", lockedProperties);

        menu.map.rayHandler.setAmbientLight(Float.parseFloat(mapAmbientColorProperty.rValue.getText()), Float.parseFloat(mapAmbientColorProperty.gValue.getText()), Float.parseFloat(mapAmbientColorProperty.bValue.getText()), Float.parseFloat(mapAmbientColorProperty.aValue.getText()));
        menu.map.r = mapBackgroundColorProperty.getR();
        menu.map.g = mapBackgroundColorProperty.getG();
        menu.map.b = mapBackgroundColorProperty.getB();

        LabelFieldPropertyValuePropertyField mapVirtualHeightProperty = Utils.getLockedPropertyField(lockedProperties, "Virtual Height");
        menu.map.virtualHeight = Float.parseFloat(mapVirtualHeightProperty.value.getText());
        menu.map.virtualWidth = menu.map.virtualHeight * Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
        menu.map.camera.viewportWidth = menu.map.virtualWidth;
        menu.map.camera.viewportHeight = menu.map.virtualHeight;
    }
}