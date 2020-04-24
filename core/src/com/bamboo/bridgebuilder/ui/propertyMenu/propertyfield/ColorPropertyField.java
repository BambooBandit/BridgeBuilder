package com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.ui.propertyMenu.PropertyMenu;

public class ColorPropertyField extends PropertyField
{
    public TextField rValue; // Null if rgba and rgbaDistanceRayAmount is false
    public TextField gValue; // Null if rgba and rgbaDistanceRayAmount is false
    public TextField bValue; // Null if rgba and rgbaDistanceRayAmount is false
    public TextField aValue; // Null if rgba and rgbaDistanceRayAmount is false

    public ColorPropertyField(Skin skin, final PropertyMenu menu, boolean removeable, float r, float g, float b, float a)
    {
        super(menu, removeable);

        TextField.TextFieldFilter filter = new TextField.TextFieldFilter()
        {
            @Override
            public boolean acceptChar(TextField textField, char c)
            {
                return c == '.' || Character.isDigit(c);
            }
        };

        this.rValue = new TextField(Float.toString(r), skin);
        this.gValue = new TextField(Float.toString(g), skin);
        this.bValue = new TextField(Float.toString(b), skin);
        this.rValue.setColor(Color.RED);
        this.gValue.setColor(Color.GREEN);
        this.bValue.setColor(Color.BLUE);
        this.aValue = new TextField(Float.toString(a), skin);
        this.rValue.setTextFieldFilter(filter);
        this.gValue.setTextFieldFilter(filter);
        this.bValue.setTextFieldFilter(filter);
        this.aValue.setTextFieldFilter(filter);

        this.table = new Table();
        this.table.bottom().left();
        this.table.add(this.rValue);
        this.table.add(this.gValue);
        this.table.add(this.bValue);
        this.table.add(this.aValue);

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
                    menu.removeProperty(removeableField);
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

        final ColorPropertyField property = this;

        this.rValue.getListeners().clear();
        this.gValue.getListeners().clear();
        this.bValue.getListeners().clear();
        this.aValue.getListeners().clear();

        TextField.TextFieldClickListener rClickListener = rValue.new TextFieldClickListener()
        {
            @Override
            public boolean keyTyped(InputEvent event, char character)
            {
                for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                {
                    if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, true))
                        continue;
                    ColorPropertyField propertyField = null;
                    if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, false))
                        propertyField = (ColorPropertyField) map.spriteMenu.selectedSpriteTools.get(i).properties.get(map.spriteMenu.selectedSpriteTools.get(i).properties.indexOf(property, false));
                    if (propertyField != null)
                    {
                        final ColorPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.rValue.setText(property.rValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(property, true))
                        continue;
                    ColorPropertyField propertyField = null;
                    if (map.selectedObjects.get(i).properties.contains(property, false))
                        propertyField = (ColorPropertyField) map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(property, false));
                    if (propertyField != null)
                    {
                        final ColorPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.rValue.setText(property.rValue.getText());});
                    }
                }
                super.keyTyped(event, character);
                for (int i = 0; i < textFieldActions.size; i++)
                    textFieldActions.get(i).action();
                textFieldActions.clear();
                return false;
            }
        };
        this.rValue.addListener(rClickListener);

        TextField.TextFieldClickListener gClickListener = gValue.new TextFieldClickListener()
        {
            @Override
            public boolean keyTyped(InputEvent event, char character)
            {
                for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                {
                    if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, true))
                        continue;
                    ColorPropertyField propertyField = null;
                    if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, false))
                        propertyField = (ColorPropertyField) map.spriteMenu.selectedSpriteTools.get(i).properties.get(map.spriteMenu.selectedSpriteTools.get(i).properties.indexOf(property, false));
                    if (propertyField != null)
                    {
                        final ColorPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.gValue.setText(property.gValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(property, true))
                        continue;
                    ColorPropertyField propertyField = null;
                    if (map.selectedObjects.get(i).properties.contains(property, false))
                        propertyField = (ColorPropertyField) map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(property, false));
                    if (propertyField != null)
                    {
                        final ColorPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.gValue.setText(property.gValue.getText());});
                    }
                }
                super.keyTyped(event, character);
                for (int i = 0; i < textFieldActions.size; i++)
                    textFieldActions.get(i).action();
                textFieldActions.clear();
                return false;
            }
        };
        this.gValue.addListener(gClickListener);

        TextField.TextFieldClickListener bClickListener = bValue.new TextFieldClickListener()
        {
            @Override
            public boolean keyTyped(InputEvent event, char character)
            {
                for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                {
                    if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, true))
                        continue;
                    ColorPropertyField propertyField = null;
                    if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, false))
                        propertyField = (ColorPropertyField) map.spriteMenu.selectedSpriteTools.get(i).properties.get(map.spriteMenu.selectedSpriteTools.get(i).properties.indexOf(property, false));
                    if (propertyField != null)
                    {
                        final ColorPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.bValue.setText(property.bValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(property, true))
                        continue;
                    ColorPropertyField propertyField = null;
                    if (map.selectedObjects.get(i).properties.contains(property, false))
                        propertyField = (ColorPropertyField) map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(property, false));
                    if (propertyField != null)
                    {
                        final ColorPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.bValue.setText(property.bValue.getText());});
                    }
                }
                super.keyTyped(event, character);
                for (int i = 0; i < textFieldActions.size; i++)
                    textFieldActions.get(i).action();
                textFieldActions.clear();
                return false;
            }
        };
        this.bValue.addListener(bClickListener);

        TextField.TextFieldClickListener aClickListener = aValue.new TextFieldClickListener()
        {
            @Override
            public boolean keyTyped(InputEvent event, char character)
            {
                for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                {
                    if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, true))
                        continue;
                    ColorPropertyField propertyField = null;
                    if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, false))
                        propertyField = (ColorPropertyField) map.spriteMenu.selectedSpriteTools.get(i).properties.get(map.spriteMenu.selectedSpriteTools.get(i).properties.indexOf(property, false));
                    if (propertyField != null)
                    {
                        final ColorPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.aValue.setText(property.aValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(property, true))
                        continue;
                    ColorPropertyField propertyField = null;
                    if (map.selectedObjects.get(i).properties.contains(property, false))
                        propertyField = (ColorPropertyField) map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(property, false));
                    if (propertyField != null)
                    {
                        final ColorPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.aValue.setText(property.aValue.getText());});
                    }
                }
                super.keyTyped(event, character);
                for (int i = 0; i < textFieldActions.size; i++)
                    textFieldActions.get(i).action();
                textFieldActions.clear();
                return false;
            }
        };
        this.aValue.addListener(aClickListener);
    }

    @Override
    protected void addLockedListener()
    {
        final Map map = menu.map;

        final ColorPropertyField thisProperty = this;

        this.rValue.getListeners().clear();
        this.gValue.getListeners().clear();
        this.bValue.getListeners().clear();
        this.aValue.getListeners().clear();

        TextField.TextFieldClickListener rClickListener = rValue.new TextFieldClickListener()
        {
            @Override
            public boolean keyTyped(InputEvent event, char character)
            {
                for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                {
                    if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(thisProperty, true))
                        continue;
                    ColorPropertyField propertyField = null;
                    if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(thisProperty, false))
                        propertyField = (ColorPropertyField) map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.get(map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final ColorPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.rValue.setText(thisProperty.rValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedSprites.size; i ++)
                {
                    if (map.selectedSprites.get(i).lockedProperties.contains(thisProperty, true))
                        continue;
                    ColorPropertyField propertyField = null;
                    if (map.selectedSprites.get(i).lockedProperties.contains(thisProperty, false))
                        propertyField = (ColorPropertyField) map.selectedSprites.get(i).lockedProperties.get(map.selectedSprites.get(i).lockedProperties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final ColorPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() -> {finalPropertyField.rValue.setText(thisProperty.rValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, true))
                        continue;
                    ColorPropertyField propertyField = null;
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, false))
                        propertyField = (ColorPropertyField) map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final ColorPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.rValue.setText(thisProperty.rValue.getText());});
                    }
                }
                super.keyTyped(event, character);
                for (int i = 0; i < textFieldActions.size; i++)
                    textFieldActions.get(i).action();
                textFieldActions.clear();
                return false;
            }
        };
        this.rValue.addListener(rClickListener);

        TextField.TextFieldClickListener gClickListener = gValue.new TextFieldClickListener()
        {
            @Override
            public boolean keyTyped(InputEvent event, char character)
            {
                for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                {
                    if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(thisProperty, true))
                        continue;
                    ColorPropertyField propertyField = null;
                    if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(thisProperty, false))
                        propertyField = (ColorPropertyField) map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.get(map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final ColorPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.gValue.setText(thisProperty.gValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedSprites.size; i ++)
                {
                    if (map.selectedSprites.get(i).lockedProperties.contains(thisProperty, true))
                        continue;
                    ColorPropertyField propertyField = null;
                    if (map.selectedSprites.get(i).lockedProperties.contains(thisProperty, false))
                        propertyField = (ColorPropertyField) map.selectedSprites.get(i).lockedProperties.get(map.selectedSprites.get(i).lockedProperties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final ColorPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.gValue.setText(thisProperty.gValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, true))
                        continue;
                    ColorPropertyField propertyField = null;
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, false))
                        propertyField = (ColorPropertyField) map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final ColorPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.gValue.setText(thisProperty.gValue.getText());});
                    }
                }
                super.keyTyped(event, character);
                for (int i = 0; i < textFieldActions.size; i++)
                    textFieldActions.get(i).action();
                textFieldActions.clear();
                return false;
            }
        };
        this.gValue.addListener(gClickListener);

        TextField.TextFieldClickListener bClickListener = bValue.new TextFieldClickListener()
        {
            @Override
            public boolean keyTyped(InputEvent event, char character)
            {
                for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                {
                    if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(thisProperty, true))
                        continue;
                    ColorPropertyField propertyField = null;
                    if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(thisProperty, false))
                        propertyField = (ColorPropertyField) map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.get(map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final ColorPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.bValue.setText(thisProperty.bValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedSprites.size; i ++)
                {
                    if (map.selectedSprites.get(i).lockedProperties.contains(thisProperty, true))
                        continue;
                    ColorPropertyField propertyField = null;
                    if (map.selectedSprites.get(i).lockedProperties.contains(thisProperty, false))
                        propertyField = (ColorPropertyField) map.selectedSprites.get(i).lockedProperties.get(map.selectedSprites.get(i).lockedProperties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final ColorPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.bValue.setText(thisProperty.bValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, true))
                        continue;
                    ColorPropertyField propertyField = null;
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, false))
                        propertyField = (ColorPropertyField) map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final ColorPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.bValue.setText(thisProperty.bValue.getText());});
                    }
                }
                super.keyTyped(event, character);
                for (int i = 0; i < textFieldActions.size; i++)
                    textFieldActions.get(i).action();
                textFieldActions.clear();
                return false;
            }
        };
        this.bValue.addListener(bClickListener);

        TextField.TextFieldClickListener aClickListener = aValue.new TextFieldClickListener()
        {
            @Override
            public boolean keyTyped(InputEvent event, char character)
            {
                for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                {
                    if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(thisProperty, true))
                        continue;
                    ColorPropertyField propertyField = null;
                    if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(thisProperty, false))
                        propertyField = (ColorPropertyField) map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.get(map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final ColorPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.aValue.setText(thisProperty.aValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedSprites.size; i ++)
                {
                    if (map.selectedSprites.get(i).lockedProperties.contains(thisProperty, true))
                        continue;
                    ColorPropertyField propertyField = null;
                    if (map.selectedSprites.get(i).lockedProperties.contains(thisProperty, false))
                        propertyField = (ColorPropertyField) map.selectedSprites.get(i).lockedProperties.get(map.selectedSprites.get(i).lockedProperties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final ColorPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.aValue.setText(thisProperty.aValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, true))
                        continue;
                    ColorPropertyField propertyField = null;
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, false))
                        propertyField = (ColorPropertyField) map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final ColorPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.aValue.setText(thisProperty.aValue.getText());});
                    }
                }
                super.keyTyped(event, character);
                for (int i = 0; i < textFieldActions.size; i++)
                    textFieldActions.get(i).action();
                textFieldActions.clear();
                return false;
            }
        };
        this.aValue.addListener(aClickListener);
    }

    public float getR()
    {
        return Float.parseFloat(this.rValue.getText());
    }
    public float getG()
    {
        return Float.parseFloat(this.gValue.getText());
    }
    public float getB()
    {
        return Float.parseFloat(this.bValue.getText());
    }
    public float getA()
    {
        return Float.parseFloat(this.aValue.getText());
    }

    @Override
    public void setSize(float width, float height)
    {
        float removeable = 0;
        if(this.removeable)
            removeable = height;
        float valueWidth = ((width - removeable) / 4);
        this.table.getCell(this.rValue).size(valueWidth, height);
        this.table.getCell(this.gValue).size(valueWidth, height);
        this.table.getCell(this.bValue).size(valueWidth, height);
        this.table.getCell(this.aValue).size(valueWidth, height);
        if(this.removeable)
            this.table.getCell(this.remove).size(height, height);
        this.table.invalidateHierarchy();
        super.setSize(width, height);
    }

    public void setRGBA(float r, float g, float b, float a)
    {
        this.rValue.setText(Float.toString(r));
        this.gValue.setText(Float.toString(g));
        this.bValue.setText(Float.toString(b));
        this.aValue.setText(Float.toString(a));
    }

    @Override
    public boolean equals(Object o)
    {
        if(o instanceof ColorPropertyField)
        {
            ColorPropertyField toCompare = (ColorPropertyField) o;
            return this.rValue.getText().equals(toCompare.rValue.getText()) &&
                    this.gValue.getText().equals(toCompare.gValue.getText()) &&
                    this.bValue.getText().equals(toCompare.bValue.getText()) &&
                    this.aValue.getText().equals(toCompare.aValue.getText());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.rValue.getText().hashCode() +
                this.gValue.getText().hashCode() +
                this.bValue.getText().hashCode() +
                this.aValue.getText().hashCode() +
                (this.removeable ? 0 : 1);
    }
}
