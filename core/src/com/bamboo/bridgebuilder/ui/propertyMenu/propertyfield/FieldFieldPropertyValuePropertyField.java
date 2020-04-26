package com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.commands.RemoveProperty;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.ui.propertyMenu.PropertyMenu;

public class FieldFieldPropertyValuePropertyField extends PropertyField
{
    public TextField property;
    public TextField value;

    public FieldFieldPropertyValuePropertyField(String property, String value, Skin skin, final PropertyMenu menu, Array<PropertyField> properties, boolean removeable)
    {
        super(menu, properties, removeable);


        if (removeable)
            this.property = new TextField(property, skin);
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

        final FieldFieldPropertyValuePropertyField thisProperty = this;

        this.property.getListeners().clear();
        this.value.getListeners().clear();

        TextField.TextFieldClickListener propertyClickListener = property.new TextFieldClickListener()
        {
            @Override
            public boolean keyTyped(InputEvent event, char character)
            {
                for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                {
                    if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(thisProperty, true))
                        continue;
                    FieldFieldPropertyValuePropertyField propertyField = null;
                    if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(thisProperty, false))
                        propertyField = (FieldFieldPropertyValuePropertyField) map.spriteMenu.selectedSpriteTools.get(i).properties.get(map.spriteMenu.selectedSpriteTools.get(i).properties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final FieldFieldPropertyValuePropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {
                            finalPropertyField.property.setText(thisProperty.property.getText());
                        });
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, true))
                        continue;
                    FieldFieldPropertyValuePropertyField propertyField = null;
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, false))
                        propertyField = (FieldFieldPropertyValuePropertyField) map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final FieldFieldPropertyValuePropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {
                            finalPropertyField.property.setText(thisProperty.property.getText());
                        });
                    }
                }
                super.keyTyped(event, character);
                for (int i = 0; i < textFieldActions.size; i++)
                    textFieldActions.get(i).action();
                textFieldActions.clear();
                return false;
            }
        };
        this.property.addListener(propertyClickListener);

        TextField.TextFieldClickListener valueClickListener = value.new TextFieldClickListener()
        {
            @Override
            public boolean keyTyped(InputEvent event, char character)
            {
                for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                {
                    if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(thisProperty, true))
                        continue;
                    FieldFieldPropertyValuePropertyField propertyField = null;
                    if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(thisProperty, false))
                        propertyField = (FieldFieldPropertyValuePropertyField) map.spriteMenu.selectedSpriteTools.get(i).properties.get(map.spriteMenu.selectedSpriteTools.get(i).properties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final FieldFieldPropertyValuePropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.value.setText(thisProperty.value.getText());});
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, true))
                        continue;
                    FieldFieldPropertyValuePropertyField propertyField = null;
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, false))
                        propertyField = (FieldFieldPropertyValuePropertyField) map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final FieldFieldPropertyValuePropertyField finalPropertyField = propertyField;
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

        final FieldFieldPropertyValuePropertyField thisProperty = this;

        this.value.getListeners().clear();
        this.property.getListeners().clear();

        TextField.TextFieldClickListener valueClickListener = value.new TextFieldClickListener()
        {
            @Override
            public boolean keyTyped(InputEvent event, char character)
            {
                for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                {
                    if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(thisProperty, true))
                        continue;
                    FieldFieldPropertyValuePropertyField propertyField = null;
                    if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(thisProperty, false))
                        propertyField = (FieldFieldPropertyValuePropertyField) map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.get(map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final FieldFieldPropertyValuePropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.value.setText(thisProperty.value.getText());});
                    }
                }
                for (int i = 0; i < map.selectedSprites.size; i ++)
                {
                    if (map.selectedSprites.get(i).lockedProperties.contains(thisProperty, true))
                        continue;
                    FieldFieldPropertyValuePropertyField propertyField = null;
                    if (map.selectedSprites.get(i).lockedProperties.contains(thisProperty, false))
                        propertyField = (FieldFieldPropertyValuePropertyField) map.selectedSprites.get(i).lockedProperties.get(map.selectedSprites.get(i).lockedProperties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final FieldFieldPropertyValuePropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.value.setText(thisProperty.value.getText());});
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, true))
                        continue;
                    FieldFieldPropertyValuePropertyField propertyField = null;
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, false))
                        propertyField = (FieldFieldPropertyValuePropertyField) map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final FieldFieldPropertyValuePropertyField finalPropertyField = propertyField;
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

        TextField.TextFieldClickListener propertyClickListener = property.new TextFieldClickListener()
        {
            @Override
            public boolean keyTyped(InputEvent event, char character)
            {
                for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                {
                    if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(thisProperty, true))
                        continue;
                    FieldFieldPropertyValuePropertyField propertyField = null;
                    if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(thisProperty, false))
                        propertyField = (FieldFieldPropertyValuePropertyField) map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.get(map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final FieldFieldPropertyValuePropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.property.setText(thisProperty.property.getText());});
                    }
                }
                for (int i = 0; i < map.selectedSprites.size; i ++)
                {
                    if (map.selectedSprites.get(i).lockedProperties.contains(thisProperty, true))
                        continue;
                    FieldFieldPropertyValuePropertyField propertyField = null;
                    if (map.selectedSprites.get(i).lockedProperties.contains(thisProperty, false))
                        propertyField = (FieldFieldPropertyValuePropertyField) map.selectedSprites.get(i).lockedProperties.get(map.selectedSprites.get(i).lockedProperties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final FieldFieldPropertyValuePropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.property.setText(thisProperty.property.getText());});
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, true))
                        continue;
                    FieldFieldPropertyValuePropertyField propertyField = null;
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, false))
                        propertyField = (FieldFieldPropertyValuePropertyField) map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final FieldFieldPropertyValuePropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.property.setText(thisProperty.property.getText());});
                    }
                }
                super.keyTyped(event, character);
                for (int i = 0; i < textFieldActions.size; i++)
                    textFieldActions.get(i).action();
                textFieldActions.clear();
                return false;
            }
        };
        this.property.addListener(propertyClickListener);
    }

    public String getProperty()
    {
        return this.property.getText();
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

    //TODO commenting below fixes some issues but stops common property panel from working
//    @Override
//    public boolean equals(Object o)
//    {
//        if(o instanceof FieldFieldPropertyValuePropertyField)
//        {
//            FieldFieldPropertyValuePropertyField toCompare = (FieldFieldPropertyValuePropertyField) o;
//            return this.property.getText().equals(toCompare.property.getText()) && this.value.getText().equals(toCompare.value.getText());
//        }
//        return false;
//    }
//
//    @Override
//    public int hashCode() {
//        return this.property.getText().hashCode() +
//                this.property.getClass().hashCode() +
//                this.value.getText().hashCode() +
//                (this.removeable ? 0 : 1);
//    }
}
