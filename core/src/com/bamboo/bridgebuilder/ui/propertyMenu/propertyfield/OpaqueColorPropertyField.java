package com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.commands.RemoveProperty;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.ui.propertyMenu.PropertyMenu;

public class OpaqueColorPropertyField extends PropertyField
{
    public Label property;
    public TextButton copy;
    public TextButton paste;
    public TextField rValue;
    public TextField gValue;
    public TextField bValue;

    public OpaqueColorPropertyField(final PropertyMenu menu, Array<PropertyField> properties, boolean removeable)
    {
        super(menu, properties, removeable);
    }

    public OpaqueColorPropertyField(Skin skin, final PropertyMenu menu, Array<PropertyField> properties, boolean removeable, String property, float r, float g, float b)
    {
        super(menu, properties, removeable);

        TextField.TextFieldFilter filter = new TextField.TextFieldFilter()
        {
            @Override
            public boolean acceptChar(TextField textField, char c)
            {
                return c == '.' || Character.isDigit(c);
            }
        };

        this.property = new Label(property, skin);
        this.copy = new TextButton("C", skin);
        this.paste = new TextButton("P", skin);
        setCopyPasteListeners();
        this.rValue = new TextField(Float.toString(r), skin);
        this.gValue = new TextField(Float.toString(g), skin);
        this.bValue = new TextField(Float.toString(b), skin);
        this.rValue.setColor(Color.RED);
        this.gValue.setColor(Color.GREEN);
        this.bValue.setColor(Color.BLUE);
        this.rValue.setTextFieldFilter(filter);
        this.gValue.setTextFieldFilter(filter);
        this.bValue.setTextFieldFilter(filter);

        this.table = new Table();
        this.table.bottom().left();
        this.table.add(this.property);
        this.table.add(this.copy);
        this.table.add(this.paste);
        this.table.add(this.rValue);
        this.table.add(this.gValue);
        this.table.add(this.bValue);

        if(menu != null)
        {
            if (removeable)
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
            } else
                addLockedListener();
        }

        addActor(this.table);
    }

    @Override
    protected void addRemoveableListener()
    {
        final Map map = menu.map;

        final OpaqueColorPropertyField property = this;

        this.rValue.getListeners().clear();
        this.gValue.getListeners().clear();
        this.bValue.getListeners().clear();

        TextField.TextFieldClickListener rClickListener = rValue.new TextFieldClickListener()
        {
            @Override
            public boolean keyTyped(InputEvent event, char character)
            {
                if(map.selectedSprites.size == 0)
                {
                    for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                    {
                        if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, true))
                            continue;
                        OpaqueColorPropertyField propertyField = null;
                        if (Utils.containsEquivalentPropertyField(map.spriteMenu.selectedSpriteTools.get(i).properties, property))
                            propertyField = (ColorPropertyField) map.spriteMenu.selectedSpriteTools.get(i).properties.get(Utils.indexOfEquivalentProperty(map.spriteMenu.selectedSpriteTools.get(i).properties, property));
                        if (propertyField != null)
                        {
                            final OpaqueColorPropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {
                                finalPropertyField.rValue.setText(property.rValue.getText());
                            });
                        }
                    }
                }
                else if(map.spriteMenu.selectedSpriteTools.size == 0)
                {
                    for (int i = 0; i < map.selectedSprites.size; i++)
                    {
                        if (map.selectedSprites.get(i).instanceSpecificProperties.contains(property, true))
                            continue;
                        OpaqueColorPropertyField propertyField = null;
                        if (Utils.containsEquivalentPropertyField(map.selectedSprites.get(i).instanceSpecificProperties, property))
                            propertyField = (ColorPropertyField) map.selectedSprites.get(i).instanceSpecificProperties.get(Utils.indexOfEquivalentProperty(map.selectedSprites.get(i).instanceSpecificProperties, property));
                        if (propertyField != null)
                        {
                            final OpaqueColorPropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {
                                finalPropertyField.rValue.setText(property.rValue.getText());
                            });
                        }
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(property, true))
                        continue;
                    OpaqueColorPropertyField propertyField = null;
                    if (Utils.containsEquivalentPropertyField(map.selectedObjects.get(i).properties, property))
                        propertyField = (ColorPropertyField) map.selectedObjects.get(i).properties.get(Utils.indexOfEquivalentProperty(map.selectedObjects.get(i).properties, property));
                    if (propertyField != null)
                    {
                        final OpaqueColorPropertyField finalPropertyField = propertyField;
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
                if(map.selectedSprites.size == 0)
                {
                    for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                    {
                        if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, true))
                            continue;
                        OpaqueColorPropertyField propertyField = null;
                        if (Utils.containsEquivalentPropertyField(map.spriteMenu.selectedSpriteTools.get(i).properties, property))
                            propertyField = (ColorPropertyField) map.spriteMenu.selectedSpriteTools.get(i).properties.get(Utils.indexOfEquivalentProperty(map.spriteMenu.selectedSpriteTools.get(i).properties, property));
                        if (propertyField != null)
                        {
                            final OpaqueColorPropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {
                                finalPropertyField.gValue.setText(property.gValue.getText());
                            });
                        }
                    }
                }
                else if(map.spriteMenu.selectedSpriteTools.size == 0)
                {
                    for (int i = 0; i < map.selectedSprites.size; i++)
                    {
                        if (map.selectedSprites.get(i).instanceSpecificProperties.contains(property, true))
                            continue;
                        OpaqueColorPropertyField propertyField = null;
                        if (Utils.containsEquivalentPropertyField(map.selectedSprites.get(i).instanceSpecificProperties, property))
                            propertyField = (ColorPropertyField) map.selectedSprites.get(i).instanceSpecificProperties.get(Utils.indexOfEquivalentProperty(map.selectedSprites.get(i).instanceSpecificProperties, property));
                        if (propertyField != null)
                        {
                            final OpaqueColorPropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {finalPropertyField.gValue.setText(property.gValue.getText());});
                        }
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(property, true))
                        continue;
                    OpaqueColorPropertyField propertyField = null;
                    if (Utils.containsEquivalentPropertyField(map.selectedObjects.get(i).properties, property))
                        propertyField = (ColorPropertyField) map.selectedObjects.get(i).properties.get(Utils.indexOfEquivalentProperty(map.selectedObjects.get(i).properties, property));
                    if (propertyField != null)
                    {
                        final OpaqueColorPropertyField finalPropertyField = propertyField;
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
                if(map.selectedSprites.size == 0)
                {
                    for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                    {
                        if (map.spriteMenu.selectedSpriteTools.get(i).properties.contains(property, true))
                            continue;
                        OpaqueColorPropertyField propertyField = null;
                        if (Utils.containsEquivalentPropertyField(map.spriteMenu.selectedSpriteTools.get(i).properties, property))
                            propertyField = (ColorPropertyField) map.spriteMenu.selectedSpriteTools.get(i).properties.get(Utils.indexOfEquivalentProperty(map.spriteMenu.selectedSpriteTools.get(i).properties, property));
                        if (propertyField != null)
                        {
                            final OpaqueColorPropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {
                                finalPropertyField.bValue.setText(property.bValue.getText());
                            });
                        }
                    }
                }
                else if(map.spriteMenu.selectedSpriteTools.size == 0)
                {
                    for (int i = 0; i < map.selectedSprites.size; i++)
                    {
                        if (map.selectedSprites.get(i).instanceSpecificProperties.contains(property, true))
                            continue;
                        OpaqueColorPropertyField propertyField = null;
                        if (Utils.containsEquivalentPropertyField(map.selectedSprites.get(i).instanceSpecificProperties, property))
                            propertyField = (ColorPropertyField) map.selectedSprites.get(i).instanceSpecificProperties.get(Utils.indexOfEquivalentProperty(map.selectedSprites.get(i).instanceSpecificProperties, property));
                        if (propertyField != null)
                        {
                            final OpaqueColorPropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {
                                finalPropertyField.bValue.setText(property.bValue.getText());
                            });
                        }
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(property, true))
                        continue;
                    OpaqueColorPropertyField propertyField = null;
                    if (Utils.containsEquivalentPropertyField(map.selectedObjects.get(i).properties, property))
                        propertyField = (ColorPropertyField) map.selectedObjects.get(i).properties.get(Utils.indexOfEquivalentProperty(map.selectedObjects.get(i).properties, property));
                    if (propertyField != null)
                    {
                        final OpaqueColorPropertyField finalPropertyField = propertyField;
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
    }

    @Override
    protected void addLockedListener()
    {
        final Map map = menu.map;

        final OpaqueColorPropertyField thisProperty = this;

        this.rValue.getListeners().clear();
        this.gValue.getListeners().clear();
        this.bValue.getListeners().clear();

        TextField.TextFieldClickListener rClickListener = rValue.new TextFieldClickListener()
        {
            @Override
            public boolean keyTyped(InputEvent event, char character)
            {
                if(map.selectedSprites.size == 0)
                {
                    for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                    {
                        if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(thisProperty, true))
                            continue;
                        OpaqueColorPropertyField propertyField = null;
                        if (Utils.containsEquivalentPropertyField(map.spriteMenu.selectedSpriteTools.get(i).lockedProperties, thisProperty))
                            propertyField = (ColorPropertyField) map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.get(Utils.indexOfEquivalentProperty(map.spriteMenu.selectedSpriteTools.get(i).lockedProperties, thisProperty));
                        if (propertyField != null)
                        {
                            final OpaqueColorPropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {
                                finalPropertyField.rValue.setText(thisProperty.rValue.getText());
                            });
                        }
                    }
                }
                else if(map.spriteMenu.selectedSpriteTools.size == 0)
                {
                    for (int i = 0; i < map.selectedSprites.size; i++)
                    {
                        if (map.selectedSprites.get(i).instanceSpecificProperties.contains(thisProperty, true))
                            continue;
                        OpaqueColorPropertyField propertyField = null;
                        if (Utils.containsEquivalentPropertyField(map.selectedSprites.get(i).instanceSpecificProperties, thisProperty))
                            propertyField = (ColorPropertyField) map.selectedSprites.get(i).instanceSpecificProperties.get(Utils.indexOfEquivalentProperty(map.selectedSprites.get(i).instanceSpecificProperties, thisProperty));
                        if (propertyField != null)
                        {
                            final OpaqueColorPropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {
                                finalPropertyField.rValue.setText(thisProperty.rValue.getText());
                            });
                        }
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, true))
                        continue;
                    OpaqueColorPropertyField propertyField = null;
                    if (Utils.containsEquivalentPropertyField(map.selectedObjects.get(i).properties, thisProperty))
                        propertyField = (ColorPropertyField) map.selectedObjects.get(i).properties.get(Utils.indexOfEquivalentProperty(map.selectedObjects.get(i).properties, thisProperty));
                    if (propertyField != null)
                    {
                        final OpaqueColorPropertyField finalPropertyField = propertyField;
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
                if(map.selectedSprites.size == 0)
                {
                    for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                    {
                        if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(thisProperty, true))
                            continue;
                        OpaqueColorPropertyField propertyField = null;
                        if (Utils.containsEquivalentPropertyField(map.spriteMenu.selectedSpriteTools.get(i).lockedProperties, thisProperty))
                            propertyField = (ColorPropertyField) map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.get(Utils.indexOfEquivalentProperty(map.spriteMenu.selectedSpriteTools.get(i).lockedProperties, thisProperty));
                        if (propertyField != null)
                        {
                            final OpaqueColorPropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {
                                finalPropertyField.gValue.setText(thisProperty.gValue.getText());
                            });
                        }
                    }
                }
                else if(map.spriteMenu.selectedSpriteTools.size == 0)
                {
                    for (int i = 0; i < map.selectedSprites.size; i++)
                    {
                        if (map.selectedSprites.get(i).instanceSpecificProperties.contains(thisProperty, true))
                            continue;
                        OpaqueColorPropertyField propertyField = null;
                        if (Utils.containsEquivalentPropertyField(map.selectedSprites.get(i).instanceSpecificProperties, thisProperty))
                            propertyField = (ColorPropertyField) map.selectedSprites.get(i).instanceSpecificProperties.get(Utils.indexOfEquivalentProperty(map.selectedSprites.get(i).instanceSpecificProperties, thisProperty));
                        if (propertyField != null)
                        {
                            final OpaqueColorPropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {finalPropertyField.gValue.setText(thisProperty.gValue.getText());});
                        }
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, true))
                        continue;
                    OpaqueColorPropertyField propertyField = null;
                    if (Utils.containsEquivalentPropertyField(map.selectedObjects.get(i).properties, thisProperty))
                        propertyField = (ColorPropertyField) map.selectedObjects.get(i).properties.get(Utils.indexOfEquivalentProperty(map.selectedObjects.get(i).properties, thisProperty));
                    if (propertyField != null)
                    {
                        final OpaqueColorPropertyField finalPropertyField = propertyField;
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
                if(map.selectedSprites.size == 0)
                {
                    for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                    {
                        if (map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.contains(thisProperty, true))
                            continue;
                        OpaqueColorPropertyField propertyField = null;
                        if (Utils.containsEquivalentPropertyField(map.spriteMenu.selectedSpriteTools.get(i).lockedProperties, thisProperty))
                            propertyField = (ColorPropertyField) map.spriteMenu.selectedSpriteTools.get(i).lockedProperties.get(Utils.indexOfEquivalentProperty(map.spriteMenu.selectedSpriteTools.get(i).lockedProperties, thisProperty));
                        if (propertyField != null)
                        {
                            final OpaqueColorPropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {
                                finalPropertyField.bValue.setText(thisProperty.bValue.getText());
                            });
                        }
                    }
                }
                else if(map.spriteMenu.selectedSpriteTools.size == 0)
                {
                    for (int i = 0; i < map.selectedSprites.size; i++)
                    {
                        if (map.selectedSprites.get(i).instanceSpecificProperties.contains(thisProperty, true))
                            continue;
                        OpaqueColorPropertyField propertyField = null;
                        if (Utils.containsEquivalentPropertyField(map.selectedSprites.get(i).instanceSpecificProperties, thisProperty))
                            propertyField = (ColorPropertyField) map.selectedSprites.get(i).instanceSpecificProperties.get(Utils.indexOfEquivalentProperty(map.selectedSprites.get(i).instanceSpecificProperties, thisProperty));
                        if (propertyField != null)
                        {
                            final OpaqueColorPropertyField finalPropertyField = propertyField;
                            textFieldActions.add(() ->
                            {
                                finalPropertyField.bValue.setText(thisProperty.bValue.getText());
                            });
                        }
                    }
                }
                for (int i = 0; i < map.selectedObjects.size; i++)
                {
                    if (map.selectedObjects.get(i).properties.contains(thisProperty, true))
                        continue;
                    OpaqueColorPropertyField propertyField = null;
                    if (Utils.containsEquivalentPropertyField(map.selectedObjects.get(i).properties, thisProperty))
                        propertyField = (ColorPropertyField) map.selectedObjects.get(i).properties.get(Utils.indexOfEquivalentProperty(map.selectedObjects.get(i).properties, thisProperty));
                    if (propertyField != null)
                    {
                        final OpaqueColorPropertyField finalPropertyField = propertyField;
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
    }

    public String getProperty()
    {
        return this.property.getText().toString();
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

    @Override
    public void setSize(float width, float height)
    {
        float removeable = 0;
        if(this.removeable)
            removeable = height;
        float valueWidth = ((width - removeable) / 5);
        this.table.getCell(this.property).size((valueWidth / 5f) * 3, height);
        this.table.getCell(this.copy).size(valueWidth / 5f, height);
        this.table.getCell(this.paste).size(valueWidth / 5f, height);
        this.table.getCell(this.rValue).size(valueWidth, height);
        this.table.getCell(this.gValue).size(valueWidth, height);
        this.table.getCell(this.bValue).size(valueWidth, height);
        if(this.removeable)
            this.table.getCell(this.remove).size(height, height);
        this.table.invalidateHierarchy();
        super.setSize(width, height);
    }

    public void setRGB(float r, float g, float b)
    {
        this.rValue.setText(Float.toString(r));
        this.gValue.setText(Float.toString(g));
        this.bValue.setText(Float.toString(b));
    }


    @Override
    public boolean equals(PropertyField propertyField)
    {
        if(propertyField instanceof OpaqueColorPropertyField)
        {
            OpaqueColorPropertyField toCompare = (OpaqueColorPropertyField) propertyField;
            return this.property.getText().toString().equals(toCompare.property.getText().toString()) &&
                    this.rValue.getText().equals(toCompare.rValue.getText()) &&
                    this.gValue.getText().equals(toCompare.gValue.getText()) &&
                    this.bValue.getText().equals(toCompare.bValue.getText());
        }
        return false;
    }

    public void setCopyPasteListeners()
    {
        this.copy.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                BridgeBuilder.bridgeBuilder.copyColor(getR(), getG(), getB(), -1);
            }
        });

        this.paste.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                setRGB(BridgeBuilder.bridgeBuilder.copyColorR == -1 ? getR() : BridgeBuilder.bridgeBuilder.copyColorR, BridgeBuilder.bridgeBuilder.copyColorG == -1 ? getG() : BridgeBuilder.bridgeBuilder.copyColorG, BridgeBuilder.bridgeBuilder.copyColorB == -1 ? getB() : BridgeBuilder.bridgeBuilder.copyColorB);
            }
        });
    }
}
