package com.bamboo.bridgebuilder.ui.propertyMenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.TextFieldAction;
import com.bamboo.bridgebuilder.map.Map;

public class PropertyField extends Group
{
    private Label property; // Null if removeable, rgba, or rgbaDistanceRayAmount is true
    public TextField propertyTextField; // Null if removeable is false, or rgba or rgbaDistanceRayAmount is true
    public TextField value; // Null if rgba, or rgbaDistanceRayAmount is true
    private Label distanceLabel; // Null if rgbaDistanceRayAmount is false
    private Label rayAmountLabel; // Null if rgbaDistanceRayAmount is false
    public TextField rValue; // Null if rgba and rgbaDistanceRayAmount is false
    public TextField gValue; // Null if rgba and rgbaDistanceRayAmount is false
    public TextField bValue; // Null if rgba and rgbaDistanceRayAmount is false
    public TextField aValue; // Null if rgba and rgbaDistanceRayAmount is false
    public TextField distanceValue; // Null if rgbaDistanceRayAmount is false
    public TextField rayAmountValue; // Null if rgbaDistanceRayAmount is false
    private TextButton remove; // Null if removeable is false
    private Table table;
    private boolean removeable;

    public boolean rgba;                   // Only one of these can be true. Both can be false.
    public boolean rgbaDistanceRayAmount;  // Only one of these can be true. Both can be false.

    private PropertyMenu menu;

    private static Array<TextFieldAction> textFieldActions = new Array<>();
    public PropertyField(String property, String value, Skin skin, final PropertyMenu menu, boolean removeable)
    {
        this.menu = menu;

        this.removeable = removeable;


        TextField.TextFieldFilter filter = null;
        if (removeable)
            this.propertyTextField = new TextField(property, skin);
        else
            this.property = new Label(property, skin);
        this.value = new TextField(value, skin);

        this.table = new Table();
        this.table.bottom().left();
        if (removeable)
            this.table.add(this.propertyTextField);
        else
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

    public PropertyField(Skin skin, final PropertyMenu menu, boolean removeable, float r, float g, float b, float a)
    {
        this.menu = menu;

        this.removeable = removeable;

        this.rgba = true;

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

    public PropertyField(Skin skin, final PropertyMenu menu, boolean removeable, float r, float g, float b, float a, float distance, int rayAmount)
    {
        this.menu = menu;

        this.removeable = removeable;

        this.rgbaDistanceRayAmount = true;

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
    public void setSize(float width, float height)
    {
        if(rgba)
        {
            float removeable = 0;
            if(this.removeable)
                removeable = height;
            float valueWidth = ((width - removeable) / 4);
            this.table.getCell(this.rValue).size(valueWidth, height);
            this.table.getCell(this.gValue).size(valueWidth, height);
            this.table.getCell(this.bValue).size(valueWidth, height);
            this.table.getCell(this.aValue).size(valueWidth, height);
        }
        else if(rgbaDistanceRayAmount)
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
        }
        else
        {
            this.value.setSize(width / 2, height);
            if (this.removeable)
            {
                this.table.getCell(this.propertyTextField).size(width / 2, height);
                this.table.getCell(this.value).size((width / 2) - height, height);
            } else
            {
                this.table.getCell(this.property).size(width / 2, height);
                this.table.getCell(this.value).size(width / 2, height);
            }
        }
        if(this.removeable)
            this.table.getCell(this.remove).size(height, height);
        this.table.invalidateHierarchy();
        super.setSize(width, height);
    }

    public String getProperty()
    {
        if(rgba || rgbaDistanceRayAmount)
            return null;
        if(this.removeable)
            return this.propertyTextField.getText();
        else
            return this.property.getText().toString();
    }
    public String getValue()
    {
        if(rgba || rgbaDistanceRayAmount)
            return null;
        return this.value.getText();
    }
    public float getR()
    {
        if(rgba || rgbaDistanceRayAmount)
            return Float.parseFloat(this.rValue.getText());
        return -1;
    }
    public float getG()
    {
        if(rgba || rgbaDistanceRayAmount)
            return Float.parseFloat(this.gValue.getText());
        return -1;
    }
    public float getB()
    {
        if(rgba || rgbaDistanceRayAmount)
            return Float.parseFloat(this.bValue.getText());
        return -1;
    }
    public float getA()
    {
        if(rgba || rgbaDistanceRayAmount)
            return Float.parseFloat(this.aValue.getText());
        return -1;
    }
    public float getDistance()
    {
        if (rgbaDistanceRayAmount)
            return Float.parseFloat(this.distanceValue.getText());
        return -1;
    }
    public int getRayAmount()
    {
        if (rgbaDistanceRayAmount)
            return Integer.parseInt(this.rayAmountValue.getText());
        return -1;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o instanceof PropertyField)
        {
            PropertyField toCompare = (PropertyField) o;
            if(rgba && toCompare.rgba)
                return this.rValue.getText().equals(toCompare.rValue.getText()) &&
                        this.gValue.getText().equals(toCompare.gValue.getText()) &&
                        this.bValue.getText().equals(toCompare.bValue.getText()) &&
                        this.aValue.getText().equals(toCompare.aValue.getText());
            else if(rgbaDistanceRayAmount && toCompare.rgbaDistanceRayAmount)
                return this.rValue.getText().equals(toCompare.rValue.getText()) &&
                        this.gValue.getText().equals(toCompare.gValue.getText()) &&
                        this.bValue.getText().equals(toCompare.bValue.getText()) &&
                        this.aValue.getText().equals(toCompare.aValue.getText()) &&
                        this.distanceValue.getText().equals(toCompare.distanceValue.getText()) &&
                        this.rayAmountValue.getText().equals(toCompare.rayAmountValue.getText());
            else if(!rgba && !toCompare.rgba && !rgbaDistanceRayAmount && !toCompare.rgbaDistanceRayAmount && this.propertyTextField != null && toCompare.propertyTextField != null)
                return this.propertyTextField.getText().equals(toCompare.propertyTextField.getText()) && this.value.getText().equals(toCompare.value.getText());
            else if(!rgba && !toCompare.rgba && !rgbaDistanceRayAmount && !toCompare.rgbaDistanceRayAmount)
                return this.value.getText().equals(toCompare.value.getText());
        }
        return false;
    }

    @Override
    public int hashCode() {
        if(rgba)
            return this.rValue.getText().hashCode() +
                    this.gValue.getText().hashCode() +
                    this.bValue.getText().hashCode() +
                    this.aValue.getText().hashCode();
        else if(rgbaDistanceRayAmount)
            return this.rValue.getText().hashCode() +
                    this.gValue.getText().hashCode() +
                    this.bValue.getText().hashCode() +
                    this.aValue.getText().hashCode() +
                    this.distanceValue.getText().hashCode() +
                    this.rayAmountValue.getText().hashCode();
        else if(this.propertyTextField != null)
            return this.propertyTextField.getText().hashCode() + this.value.getText().hashCode();
        else
            return this.value.getText().hashCode();
    }

    /** Any changes you make to one removeable property will change all other identical properties in a selection of objects or tiles*/
    private void addRemoveableListener()
    {
        final Map map = menu.map;

        final PropertyField property = this;

        if(rgba || rgbaDistanceRayAmount)
        {
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
                        PropertyField propertyField = null;
                        if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, false))
                            propertyField = map.spriteMenu.selectedSpriteTools.get(i).properties.get(map.spriteMenu.selectedSpriteTools.get(i).properties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {finalPropertyField.rValue.setText(property.rValue.getText());});
                        }
                    }
                    for (int i = 0; i < map.selectedObjects.size; i++)
                    {
                        if (map.selectedObjects.get(i).properties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.selectedObjects.get(i).properties.contains(property, false))
                            propertyField = map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
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
                        PropertyField propertyField = null;
                        if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, false))
                            propertyField = map.spriteMenu.selectedSpriteTools.get(i).properties.get(map.spriteMenu.selectedSpriteTools.get(i).properties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {finalPropertyField.gValue.setText(property.gValue.getText());});
                        }
                    }
                    for (int i = 0; i < map.selectedObjects.size; i++)
                    {
                        if (map.selectedObjects.get(i).properties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.selectedObjects.get(i).properties.contains(property, false))
                            propertyField = map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
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
                        PropertyField propertyField = null;
                        if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, false))
                            propertyField = map.spriteMenu.selectedSpriteTools.get(i).properties.get(map.spriteMenu.selectedSpriteTools.get(i).properties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {finalPropertyField.bValue.setText(property.bValue.getText());});
                        }
                    }
                    for (int i = 0; i < map.selectedObjects.size; i++)
                    {
                        if (map.selectedObjects.get(i).properties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.selectedObjects.get(i).properties.contains(property, false))
                            propertyField = map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
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
                        PropertyField propertyField = null;
                        if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, false))
                            propertyField = map.spriteMenu.selectedSpriteTools.get(i).properties.get(map.spriteMenu.selectedSpriteTools.get(i).properties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {finalPropertyField.aValue.setText(property.aValue.getText());});
                        }
                    }
                    for (int i = 0; i < map.selectedObjects.size; i++)
                    {
                        if (map.selectedObjects.get(i).properties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.selectedObjects.get(i).properties.contains(property, false))
                            propertyField = map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
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
        if(rgbaDistanceRayAmount)
        {
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
                        PropertyField propertyField = null;
                        if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, false))
                            propertyField = map.spriteMenu.selectedSpriteTools.get(i).properties.get(map.spriteMenu.selectedSpriteTools.get(i).properties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {finalPropertyField.distanceValue.setText(property.distanceValue.getText());});
                        }
                    }
                    for (int i = 0; i < map.selectedObjects.size; i++)
                    {
                        if (map.selectedObjects.get(i).properties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.selectedObjects.get(i).properties.contains(property, false))
                            propertyField = map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
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
                        PropertyField propertyField = null;
                        if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, false))
                            propertyField = map.spriteMenu.selectedSpriteTools.get(i).properties.get(map.spriteMenu.selectedSpriteTools.get(i).properties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {finalPropertyField.rayAmountValue.setText(property.rayAmountValue.getText());});
                        }
                    }
                    for (int i = 0; i < map.selectedObjects.size; i++)
                    {
                        if (map.selectedObjects.get(i).properties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.selectedObjects.get(i).properties.contains(property, false))
                            propertyField = map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
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
        if(!rgba && !rgbaDistanceRayAmount)
        {
            this.propertyTextField.getListeners().clear();
            this.value.getListeners().clear();

            TextField.TextFieldClickListener propertyClickListener = propertyTextField.new TextFieldClickListener()
            {
                @Override
                public boolean keyTyped(InputEvent event, char character)
                {
                    for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                    {
                        if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, false))
                            propertyField = map.spriteMenu.selectedSpriteTools.get(i).properties.get(map.spriteMenu.selectedSpriteTools.get(i).properties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {
                                finalPropertyField.propertyTextField.setText(property.propertyTextField.getText());
                            });
                        }
                    }
                    for (int i = 0; i < map.selectedObjects.size; i++)
                    {
                        if (map.selectedObjects.get(i).properties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.selectedObjects.get(i).properties.contains(property, false))
                            propertyField = map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {
                                finalPropertyField.propertyTextField.setText(property.propertyTextField.getText());
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
            this.propertyTextField.addListener(propertyClickListener);

            TextField.TextFieldClickListener valueClickListener = value.new TextFieldClickListener()
            {
                @Override
                public boolean keyTyped(InputEvent event, char character)
                {
                    for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                    {
                        if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, false))
                            propertyField = map.spriteMenu.selectedSpriteTools.get(i).properties.get(map.spriteMenu.selectedSpriteTools.get(i).properties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {finalPropertyField.value.setText(property.value.getText());});
                        }
                    }
                    for (int i = 0; i < map.selectedObjects.size; i++)
                    {
                        if (map.selectedObjects.get(i).properties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.selectedObjects.get(i).properties.contains(property, false))
                            propertyField = map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {finalPropertyField.value.setText(property.value.getText());});
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
    }

    /** Any changes you make to one locked property will change all other identical properties in a selection of objects or tiles*/
    private void addLockedListener()
    {
        final Map map = menu.map;

        final PropertyField property = this;

        if(rgba || rgbaDistanceRayAmount)
        {
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
                        if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(property, false))
                            propertyField = map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.get(map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {finalPropertyField.rValue.setText(property.rValue.getText());});
                        }
                    }
                    for (int i = 0; i < map.selectedSprites.size; i ++)
                    {
                        if (map.selectedSprites.get(i).lockedProperties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.selectedSprites.get(i).lockedProperties.contains(property, false))
                            propertyField = map.selectedSprites.get(i).lockedProperties.get(map.selectedSprites.get(i).lockedProperties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() -> {finalPropertyField.rValue.setText(property.rValue.getText());});
                        }
                    }
                    for (int i = 0; i < map.selectedObjects.size; i++)
                    {
                        if (map.selectedObjects.get(i).properties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.selectedObjects.get(i).properties.contains(property, false))
                            propertyField = map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
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
                        if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(property, false))
                            propertyField = map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.get(map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {finalPropertyField.gValue.setText(property.gValue.getText());});
                        }
                    }
                    for (int i = 0; i < map.selectedSprites.size; i ++)
                    {
                        if (map.selectedSprites.get(i).lockedProperties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.selectedSprites.get(i).lockedProperties.contains(property, false))
                            propertyField = map.selectedSprites.get(i).lockedProperties.get(map.selectedSprites.get(i).lockedProperties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {finalPropertyField.gValue.setText(property.gValue.getText());});
                        }
                    }
                    for (int i = 0; i < map.selectedObjects.size; i++)
                    {
                        if (map.selectedObjects.get(i).properties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.selectedObjects.get(i).properties.contains(property, false))
                            propertyField = map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
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
                        if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(property, false))
                            propertyField = map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.get(map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {finalPropertyField.bValue.setText(property.bValue.getText());});
                        }
                    }
                    for (int i = 0; i < map.selectedSprites.size; i ++)
                    {
                        if (map.selectedSprites.get(i).lockedProperties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.selectedSprites.get(i).lockedProperties.contains(property, false))
                            propertyField = map.selectedSprites.get(i).lockedProperties.get(map.selectedSprites.get(i).lockedProperties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {finalPropertyField.bValue.setText(property.bValue.getText());});
                        }
                    }
                    for (int i = 0; i < map.selectedObjects.size; i++)
                    {
                        if (map.selectedObjects.get(i).properties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.selectedObjects.get(i).properties.contains(property, false))
                            propertyField = map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
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
                        if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(property, false))
                            propertyField = map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.get(map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {finalPropertyField.aValue.setText(property.aValue.getText());});
                        }
                    }
                    for (int i = 0; i < map.selectedSprites.size; i ++)
                    {
                        if (map.selectedSprites.get(i).lockedProperties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.selectedSprites.get(i).lockedProperties.contains(property, false))
                            propertyField = map.selectedSprites.get(i).lockedProperties.get(map.selectedSprites.get(i).lockedProperties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {finalPropertyField.aValue.setText(property.aValue.getText());});
                        }
                    }
                    for (int i = 0; i < map.selectedObjects.size; i++)
                    {
                        if (map.selectedObjects.get(i).properties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.selectedObjects.get(i).properties.contains(property, false))
                            propertyField = map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
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
        if(rgbaDistanceRayAmount)
        {
            this.distanceValue.getListeners().clear();
            this.rayAmountValue.getListeners().clear();

            TextField.TextFieldClickListener distanceClickListener = distanceValue.new TextFieldClickListener()
            {
                @Override
                public boolean keyTyped(InputEvent event, char character)
                {
                    for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                    {
                        if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(property, false))
                            propertyField = map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.get(map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {finalPropertyField.distanceValue.setText(property.distanceValue.getText());});
                        }
                    }
                    for (int i = 0; i < map.selectedSprites.size; i ++)
                    {
                        if (map.selectedSprites.get(i).lockedProperties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.selectedSprites.get(i).lockedProperties.contains(property, false))
                            propertyField = map.selectedSprites.get(i).lockedProperties.get(map.selectedSprites.get(i).lockedProperties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {finalPropertyField.distanceValue.setText(property.distanceValue.getText());});
                        }
                    }
                    for (int i = 0; i < map.selectedObjects.size; i++)
                    {
                        if (map.selectedObjects.get(i).properties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.selectedObjects.get(i).properties.contains(property, false))
                            propertyField = map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
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
                        if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(property, false))
                            propertyField = map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.get(map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {finalPropertyField.rayAmountValue.setText(property.rayAmountValue.getText());});
                        }
                    }
                    for (int i = 0; i < map.selectedSprites.size; i ++)
                    {
                        if (map.selectedSprites.get(i).lockedProperties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.selectedSprites.get(i).lockedProperties.contains(property, false))
                            propertyField = map.selectedSprites.get(i).lockedProperties.get(map.selectedSprites.get(i).lockedProperties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {finalPropertyField.rayAmountValue.setText(property.rayAmountValue.getText());});
                        }
                    }
                    for (int i = 0; i < map.selectedObjects.size; i++)
                    {
                        if (map.selectedObjects.get(i).properties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.selectedObjects.get(i).properties.contains(property, false))
                            propertyField = map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
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
        if(!rgba && !rgbaDistanceRayAmount)
        {
            this.value.getListeners().clear();

            TextField.TextFieldClickListener valueClickListener = value.new TextFieldClickListener()
            {
                @Override
                public boolean keyTyped(InputEvent event, char character)
                {
                    for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                    {
                        if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(property, false))
                            propertyField = map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.get(map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {finalPropertyField.value.setText(property.value.getText());});
                        }
                    }
                    for (int i = 0; i < map.selectedSprites.size; i ++)
                    {
                        if (map.selectedSprites.get(i).lockedProperties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.selectedSprites.get(i).lockedProperties.contains(property, false))
                            propertyField = map.selectedSprites.get(i).lockedProperties.get(map.selectedSprites.get(i).lockedProperties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {finalPropertyField.value.setText(property.value.getText());});
                        }
                    }
                    for (int i = 0; i < map.selectedObjects.size; i++)
                    {
                        if (map.selectedObjects.get(i).properties.contains(property, true))
                            continue;
                        PropertyField propertyField = null;
                        if (map.selectedObjects.get(i).properties.contains(property, false))
                            propertyField = map.selectedObjects.get(i).properties.get(map.selectedObjects.get(i).properties.indexOf(property, false));
                        if (propertyField != null)
                        {
                            final PropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {finalPropertyField.value.setText(property.value.getText());});
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
    }

    public void setRGBA(float r, float g, float b, float a)
    {
        if(rgba)
        {
            this.rValue.setText(Float.toString(r));
            this.gValue.setText(Float.toString(g));
            this.bValue.setText(Float.toString(b));
            this.aValue.setText(Float.toString(a));
        }
    }

    public void setRGBADR(float r, float g, float b, float a, float distance, int rayAmount)
    {
        if(rgbaDistanceRayAmount)
        {
            this.rValue.setText(Float.toString(r));
            this.gValue.setText(Float.toString(g));
            this.bValue.setText(Float.toString(b));
            this.aValue.setText(Float.toString(a));
            this.distanceValue.setText(Float.toString(distance));
            this.rayAmountValue.setText(Float.toString(rayAmount));
        }
    }
}
