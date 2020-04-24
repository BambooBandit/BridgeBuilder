package com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.ui.propertyMenu.PropertyMenu;

public class LightPropertyField extends PropertyField
{
    private Label distanceLabel; // Null if rgbaDistanceRayAmount is false
    private Label rayAmountLabel; // Null if rgbaDistanceRayAmount is false
    public TextField rValue; // Null if rgba and rgbaDistanceRayAmount is false
    public TextField gValue; // Null if rgba and rgbaDistanceRayAmount is false
    public TextField bValue; // Null if rgba and rgbaDistanceRayAmount is false
    public TextField aValue; // Null if rgba and rgbaDistanceRayAmount is false
    public TextField distanceValue; // Null if rgbaDistanceRayAmount is false
    public TextField rayAmountValue; // Null if rgbaDistanceRayAmount is false

    public LightPropertyField(Skin skin, final PropertyMenu menu, boolean removeable, float r, float g, float b, float a, float distance, int rayAmount)
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
        this.distanceLabel = new Label("Dis", skin);
        this.distanceLabel.setAlignment(Align.right);
        this.rayAmountLabel = new Label("Cnt", skin);
        this.rayAmountLabel.setAlignment(Align.right);
        this.distanceValue = new TextField(Float.toString(distance), skin);
        this.distanceValue.setTextFieldFilter(filter);
        this.rayAmountValue = new TextField(Integer.toString(rayAmount), skin);
        this.rayAmountValue.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());

        this.table = new Table();
        this.table.bottom().left();
        this.table.add(this.rValue);
        this.table.add(this.gValue);
        this.table.add(this.bValue);
        this.table.add(this.aValue);
        this.table.add(this.distanceLabel);
        this.table.add(this.distanceValue);
        this.table.add(this.rayAmountLabel);
        this.table.add(this.rayAmountValue);

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

        final LightPropertyField property = this;

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
                    LightPropertyField propertyField = null;
                    if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, false))
                        propertyField = (LightPropertyField) map.spriteMenu.selectedSpriteTools.get(i).properties.get(map.spriteMenu.selectedSpriteTools.get(i).properties.indexOf(property, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.rValue.setText(property.rValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(property, true))
                        continue;
                    LightPropertyField propertyField = null;
                    if (map.selectedObjects.get(i).properties.contains(property, false))
                        propertyField = (LightPropertyField) map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(property, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
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
                    LightPropertyField propertyField = null;
                    if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, false))
                        propertyField = (LightPropertyField) map.spriteMenu.selectedSpriteTools.get(i).properties.get(map.spriteMenu.selectedSpriteTools.get(i).properties.indexOf(property, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.gValue.setText(property.gValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(property, true))
                        continue;
                    LightPropertyField propertyField = null;
                    if (map.selectedObjects.get(i).properties.contains(property, false))
                        propertyField = (LightPropertyField) map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(property, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
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
                    LightPropertyField propertyField = null;
                    if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, false))
                        propertyField = (LightPropertyField) map.spriteMenu.selectedSpriteTools.get(i).properties.get(map.spriteMenu.selectedSpriteTools.get(i).properties.indexOf(property, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.bValue.setText(property.bValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(property, true))
                        continue;
                    LightPropertyField propertyField = null;
                    if (map.selectedObjects.get(i).properties.contains(property, false))
                        propertyField = (LightPropertyField) map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(property, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
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
                    LightPropertyField propertyField = null;
                    if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, false))
                        propertyField = (LightPropertyField) map.spriteMenu.selectedSpriteTools.get(i).properties.get(map.spriteMenu.selectedSpriteTools.get(i).properties.indexOf(property, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.aValue.setText(property.aValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(property, true))
                        continue;
                    LightPropertyField propertyField = null;
                    if (map.selectedObjects.get(i).properties.contains(property, false))
                        propertyField = (LightPropertyField) map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(property, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
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


        this.distanceValue.getListeners().clear();
        this.rayAmountValue.getListeners().clear();

        TextField.TextFieldClickListener distanceClickListener = distanceValue.new TextFieldClickListener()
        {
            @Override
            public boolean keyTyped(InputEvent event, char character)
            {
                for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                {
                    if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, true))
                        continue;
                    LightPropertyField propertyField = null;
                    if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, false))
                        propertyField = (LightPropertyField) map.spriteMenu.selectedSpriteTools.get(i).properties.get(map.spriteMenu.selectedSpriteTools.get(i).properties.indexOf(property, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.distanceValue.setText(property.distanceValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(property, true))
                        continue;
                    LightPropertyField propertyField = null;
                    if (map.selectedObjects.get(i).properties.contains(property, false))
                        propertyField = (LightPropertyField) map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(property, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.distanceValue.setText(property.distanceValue.getText());});
                    }
                }
                super.keyTyped(event, character);
                for (int i = 0; i < textFieldActions.size; i++)
                    textFieldActions.get(i).action();
                textFieldActions.clear();
                return false;
            }
        };
        this.distanceValue.addListener(distanceClickListener);

        TextField.TextFieldClickListener rayAmountClickListener = rayAmountValue.new TextFieldClickListener()
        {
            @Override
            public boolean keyTyped(InputEvent event, char character)
            {
                for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                {
                    if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, true))
                        continue;
                    LightPropertyField propertyField = null;
                    if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, false))
                        propertyField = (LightPropertyField) map.spriteMenu.selectedSpriteTools.get(i).properties.get(map.spriteMenu.selectedSpriteTools.get(i).properties.indexOf(property, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.rayAmountValue.setText(property.rayAmountValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(property, true))
                        continue;
                    LightPropertyField propertyField = null;
                    if (map.selectedObjects.get(i).properties.contains(property, false))
                        propertyField = (LightPropertyField) map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(property, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.rayAmountValue.setText(property.rayAmountValue.getText());});
                    }
                }
                super.keyTyped(event, character);
                for (int i = 0; i < textFieldActions.size; i++)
                    textFieldActions.get(i).action();
                textFieldActions.clear();
                return false;
            }
        };
        this.rayAmountValue.addListener(rayAmountClickListener);
    }

    @Override
    protected void addLockedListener()
    {
        final Map map = menu.map;

        final LightPropertyField thisProperty = this;

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
                    LightPropertyField propertyField = null;
                    if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(thisProperty, false))
                        propertyField = (LightPropertyField) map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.get(map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.rValue.setText(thisProperty.rValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedSprites.size; i ++)
                {
                    if (map.selectedSprites.get(i).lockedProperties.contains(thisProperty, true))
                        continue;
                    LightPropertyField propertyField = null;
                    if (map.selectedSprites.get(i).lockedProperties.contains(thisProperty, false))
                        propertyField = (LightPropertyField) map.selectedSprites.get(i).lockedProperties.get(map.selectedSprites.get(i).lockedProperties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() -> {finalPropertyField.rValue.setText(thisProperty.rValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, true))
                        continue;
                    LightPropertyField propertyField = null;
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, false))
                        propertyField = (LightPropertyField) map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
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
                    LightPropertyField propertyField = null;
                    if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(thisProperty, false))
                        propertyField = (LightPropertyField) map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.get(map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.gValue.setText(thisProperty.gValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedSprites.size; i ++)
                {
                    if (map.selectedSprites.get(i).lockedProperties.contains(thisProperty, true))
                        continue;
                    LightPropertyField propertyField = null;
                    if (map.selectedSprites.get(i).lockedProperties.contains(thisProperty, false))
                        propertyField = (LightPropertyField) map.selectedSprites.get(i).lockedProperties.get(map.selectedSprites.get(i).lockedProperties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.gValue.setText(thisProperty.gValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, true))
                        continue;
                    LightPropertyField propertyField = null;
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, false))
                        propertyField = (LightPropertyField) map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
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
                    LightPropertyField propertyField = null;
                    if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(thisProperty, false))
                        propertyField = (LightPropertyField) map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.get(map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.bValue.setText(thisProperty.bValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedSprites.size; i ++)
                {
                    if (map.selectedSprites.get(i).lockedProperties.contains(thisProperty, true))
                        continue;
                    LightPropertyField propertyField = null;
                    if (map.selectedSprites.get(i).lockedProperties.contains(thisProperty, false))
                        propertyField = (LightPropertyField) map.selectedSprites.get(i).lockedProperties.get(map.selectedSprites.get(i).lockedProperties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.bValue.setText(thisProperty.bValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, true))
                        continue;
                    LightPropertyField propertyField = null;
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, false))
                        propertyField = (LightPropertyField) map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
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
                    LightPropertyField propertyField = null;
                    if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(thisProperty, false))
                        propertyField = (LightPropertyField) map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.get(map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.aValue.setText(thisProperty.aValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedSprites.size; i ++)
                {
                    if (map.selectedSprites.get(i).lockedProperties.contains(thisProperty, true))
                        continue;
                    LightPropertyField propertyField = null;
                    if (map.selectedSprites.get(i).lockedProperties.contains(thisProperty, false))
                        propertyField = (LightPropertyField) map.selectedSprites.get(i).lockedProperties.get(map.selectedSprites.get(i).lockedProperties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.aValue.setText(thisProperty.aValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, true))
                        continue;
                    LightPropertyField propertyField = null;
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, false))
                        propertyField = (LightPropertyField) map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
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
        this.distanceValue.getListeners().clear();
        this.rayAmountValue.getListeners().clear();

        TextField.TextFieldClickListener distanceClickListener = distanceValue.new TextFieldClickListener()
        {
            @Override
            public boolean keyTyped(InputEvent event, char character)
            {
                for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                {
                    if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(thisProperty, true))
                        continue;
                    LightPropertyField propertyField = null;
                    if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(thisProperty, false))
                        propertyField = (LightPropertyField) map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.get(map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.distanceValue.setText(thisProperty.distanceValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedSprites.size; i ++)
                {
                    if (map.selectedSprites.get(i).lockedProperties.contains(thisProperty, true))
                        continue;
                    LightPropertyField propertyField = null;
                    if (map.selectedSprites.get(i).lockedProperties.contains(thisProperty, false))
                        propertyField = (LightPropertyField) map.selectedSprites.get(i).lockedProperties.get(map.selectedSprites.get(i).lockedProperties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.distanceValue.setText(thisProperty.distanceValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, true))
                        continue;
                    LightPropertyField propertyField = null;
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, false))
                        propertyField = (LightPropertyField) map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.distanceValue.setText(thisProperty.distanceValue.getText());});
                    }
                }
                super.keyTyped(event, character);
                for (int i = 0; i < textFieldActions.size; i++)
                    textFieldActions.get(i).action();
                textFieldActions.clear();
                return false;
            }
        };
        this.distanceValue.addListener(distanceClickListener);

        TextField.TextFieldClickListener rayAmountClickListener = rayAmountValue.new TextFieldClickListener()
        {
            @Override
            public boolean keyTyped(InputEvent event, char character)
            {
                for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                {
                    if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(thisProperty, true))
                        continue;
                    LightPropertyField propertyField = null;
                    if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(thisProperty, false))
                        propertyField = (LightPropertyField) map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.get(map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.rayAmountValue.setText(thisProperty.rayAmountValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedSprites.size; i ++)
                {
                    if (map.selectedSprites.get(i).lockedProperties.contains(thisProperty, true))
                        continue;
                    LightPropertyField propertyField = null;
                    if (map.selectedSprites.get(i).lockedProperties.contains(thisProperty, false))
                        propertyField = (LightPropertyField) map.selectedSprites.get(i).lockedProperties.get(map.selectedSprites.get(i).lockedProperties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.rayAmountValue.setText(thisProperty.rayAmountValue.getText());});
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, true))
                        continue;
                    LightPropertyField propertyField = null;
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, false))
                        propertyField = (LightPropertyField) map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(thisProperty, false));
                    if (propertyField != null)
                    {
                        final LightPropertyField finalPropertyField = propertyField;
                        textFieldActions.add(() ->
                        {finalPropertyField.rayAmountValue.setText(thisProperty.rayAmountValue.getText());});
                    }
                }
                super.keyTyped(event, character);
                for (int i = 0; i < textFieldActions.size; i++)
                    textFieldActions.get(i).action();
                textFieldActions.clear();
                return false;
            }
        };
        this.rayAmountValue.addListener(rayAmountClickListener);
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
    public float getDistance()
    {
        return Float.parseFloat(this.distanceValue.getText());
    }
    public int getRayAmount()
    {
        return Integer.parseInt(this.rayAmountValue.getText());
    }

    public void setRGBADR(float r, float g, float b, float a, float distance, int rayAmount)
    {
        this.rValue.setText(Float.toString(r));
        this.gValue.setText(Float.toString(g));
        this.bValue.setText(Float.toString(b));
        this.aValue.setText(Float.toString(a));
        this.distanceValue.setText(Float.toString(distance));
        this.rayAmountValue.setText(Float.toString(rayAmount));
    }

    @Override
    public void setSize(float width, float height)
    {
        float removeable = 0;
        if(this.removeable)
            removeable = height;
        float distanceRayAmountWidth = ((((width - removeable) / 1.88f)) / 4);
        float distanceRayAmountLabelWidth = ((((width - removeable) / 1.88f)) / 4) * .6f;
        float distanceRayAmountValueWidth = ((((width - removeable) / 1.88f)) / 4) * 1.4f;
        float rgbaValueWidth = ((width - removeable - distanceRayAmountWidth * 4) / 4);
        this.table.getCell(this.rValue).size(rgbaValueWidth, height);
        this.table.getCell(this.gValue).size(rgbaValueWidth, height);
        this.table.getCell(this.bValue).size(rgbaValueWidth, height);
        this.table.getCell(this.aValue).size(rgbaValueWidth, height);
        this.table.getCell(this.distanceLabel).size(distanceRayAmountLabelWidth, height);
        this.table.getCell(this.distanceValue).size(distanceRayAmountValueWidth, height);
        this.table.getCell(this.rayAmountLabel).size(distanceRayAmountLabelWidth, height);
        this.table.getCell(this.rayAmountValue).size(distanceRayAmountValueWidth, height);
        if(this.removeable)
            this.table.getCell(this.remove).size(height, height);
        this.table.invalidateHierarchy();
        super.setSize(width, height);
    }

    @Override
    public boolean equals(Object o)
    {
        if(o instanceof LightPropertyField)
        {
            LightPropertyField toCompare = (LightPropertyField) o;
            return this.rValue.getText().equals(toCompare.rValue.getText()) &&
                    this.gValue.getText().equals(toCompare.gValue.getText()) &&
                    this.bValue.getText().equals(toCompare.bValue.getText()) &&
                    this.aValue.getText().equals(toCompare.aValue.getText()) &&
                    this.distanceValue.getText().equals(toCompare.distanceValue.getText()) &&
                    this.rayAmountValue.getText().equals(toCompare.rayAmountValue.getText());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.rValue.getText().hashCode() +
                this.gValue.getText().hashCode() +
                this.bValue.getText().hashCode() +
                this.aValue.getText().hashCode() +
                this.distanceValue.getText().hashCode() +
                this.rayAmountValue.getText().hashCode() +
                (this.removeable ? 0 : 1);
    }
}
