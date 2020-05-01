package com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.commands.RemoveProperty;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.ui.propertyMenu.PropertyMenu;

public class LabelFieldPropertyValuePropertyField extends PropertyField
{
    private Label property;
    public TextField value;

    public LabelFieldPropertyValuePropertyField(String property, String value, Skin skin, final PropertyMenu menu, Array<PropertyField> properties, boolean removeable)
    {
        super(menu, properties, removeable);

        this.property = new Label(property, skin);
        this.value = new TextField(value, skin);

        this.table = new Table();
        this.table.bottom().left();
        this.table.add(this.property);
        this.table.add(this.value);

        if(removeable)
        {
            this.remove = new TextButton("X", skin);
            this.remove.setColor(Color.FIREBRICK);
            final PropertyField removeableField = this;
            this.remove.addListener(new ClickListener()
            {
                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    RemoveProperty removeProperty = new RemoveProperty(menu.map, removeableField, properties);
                    menu.map.executeCommand(removeProperty);
                }
            });

            this.table.add(this.remove);
            addRemoveableListener();
        }
        else
            addLockedListener();

        addActor(this.table);
    }

    @Override
    protected void addRemoveableListener()
    {
        final Map map = menu.map;

        final LabelFieldPropertyValuePropertyField thisProperty = this;

        this.value.getListeners().clear();

        TextField.TextFieldClickListener valueClickListener = value.new TextFieldClickListener()
        {
            @Override
            public boolean keyTyped(InputEvent event, char character)
            {
                for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                {
                    if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(thisProperty, true))
                        continue;
                    LabelFieldPropertyValuePropertyField propertyField = null;
                    if (Utils.containsEquivalentPropertyField(map.spriteMenu.selectedSpriteTools.get(i).properties, thisProperty))
                        propertyField = (LabelFieldPropertyValuePropertyField) map.spriteMenu.selectedSpriteTools.get(i).properties.get(Utils.indexOfEquivalentProperty(map.spriteMenu.selectedSpriteTools.get(i).properties, thisProperty));
                    if (propertyField != null)
                    {
                        final LabelFieldPropertyValuePropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.value.setText(thisProperty.value.getText());});
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, true))
                        continue;
                    LabelFieldPropertyValuePropertyField propertyField = null;
                    if (Utils.containsEquivalentPropertyField(map.selectedObjects.get(i).properties, thisProperty))
                        propertyField = (LabelFieldPropertyValuePropertyField) map.selectedObjects.get(i).properties.get(Utils.indexOfEquivalentProperty(map.selectedObjects.get(i).properties, thisProperty));
                    if (propertyField != null)
                    {
                        final LabelFieldPropertyValuePropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.value.setText(thisProperty.value.getText());});
                    }
                }
                super.keyTyped(event, character);
                for (int i = 0; i < textFieldActions.size; i++)
                    textFieldActions.get(i).action();
                textFieldActions.clear();
                return false;
            }
        };
        this.value.addListener(valueClickListener);
    }

    @Override
    protected void addLockedListener()
    {
        final Map map = menu.map;

        final LabelFieldPropertyValuePropertyField thisProperty = this;

        this.value.getListeners().clear();

        TextField.TextFieldClickListener valueClickListener = value.new TextFieldClickListener()
        {
            @Override
            public boolean keyTyped(InputEvent event, char character)
            {
                for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                {
                    if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(thisProperty, true))
                        continue;
                    LabelFieldPropertyValuePropertyField propertyField = null;
                    if (Utils.containsEquivalentPropertyField(map.spriteMenu.selectedSpriteTools.get(i).lockedProperties, thisProperty))
                        propertyField = (LabelFieldPropertyValuePropertyField) map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.get(Utils.indexOfEquivalentProperty(map.spriteMenu.selectedSpriteTools.get(i).lockedProperties, thisProperty));
                    if (propertyField != null)
                    {
                        final LabelFieldPropertyValuePropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.value.setText(thisProperty.value.getText());});
                    }
                }
                for (int i = 0; i < map.selectedSprites.size; i ++)
                {
                    if (map.selectedSprites.get(i).lockedProperties.contains(thisProperty, true))
                        continue;
                    LabelFieldPropertyValuePropertyField propertyField = null;
                    if (Utils.containsEquivalentPropertyField(map.selectedSprites.get(i).lockedProperties, thisProperty))
                        propertyField = (LabelFieldPropertyValuePropertyField) map.selectedSprites.get(i).lockedProperties.get(Utils.indexOfEquivalentProperty(map.selectedSprites.get(i).lockedProperties, thisProperty));
                    if (propertyField != null)
                    {
                        final LabelFieldPropertyValuePropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.value.setText(thisProperty.value.getText());});
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, true))
                        continue;
                    LabelFieldPropertyValuePropertyField propertyField = null;
                    if (Utils.containsEquivalentPropertyField(map.selectedObjects.get(i).properties, thisProperty))
                        propertyField = (LabelFieldPropertyValuePropertyField) map.selectedObjects.get(i).properties.get(Utils.indexOfEquivalentProperty(map.selectedObjects.get(i).properties, thisProperty));
                    if (propertyField != null)
                    {
                        final LabelFieldPropertyValuePropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.value.setText(thisProperty.value.getText());});
                    }
                }
                super.keyTyped(event, character);
                for (int i = 0; i < textFieldActions.size; i++)
                    textFieldActions.get(i).action();
                textFieldActions.clear();
                return false;
            }
        };
        this.value.addListener(valueClickListener);
    }

    public String getProperty()
    {
        return this.property.getText().toString();
    }
    public String getValue()
    {
        return this.value.getText();
    }

    @Override
    public void setSize(float width, float height)
    {
        this.value.setSize(width / 2, height);
        this.table.getCell(this.property).size(width / 2, height);
        if (this.removeable)
            this.table.getCell(this.value).size((width / 2) - height, height);
        else
            this.table.getCell(this.value).size(width / 2, height);
        if(this.removeable)
            this.table.getCell(this.remove).size(height, height);
        this.table.invalidateHierarchy();
        super.setSize(width, height);
    }

    @Override
    public boolean equals(PropertyField propertyField)
    {
        if(propertyField instanceof LabelFieldPropertyValuePropertyField)
        {
            LabelFieldPropertyValuePropertyField toCompare = (LabelFieldPropertyValuePropertyField) propertyField;
            return this.property.getText().equals(toCompare.property.getText()) && this.value.getText().equals(toCompare.value.getText());
        }
        return false;
    }
}
