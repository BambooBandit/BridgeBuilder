package com.bamboo.bridgebuilder.ui.propertyMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.EditorAssets;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapObject;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

public class PropertyPanel extends Group
{
    public static int textFieldHeight = 35;

    private BridgeBuilder editor;
    private Map map;
    private PropertyMenu menu;

    private Image background;
    private Stack stack;
    private ScrollPane scrollPane;
    public Table table; // Holds all the text fields

    private Skin skin;

    public PropertyPanel(Skin skin, PropertyMenu menu, BridgeBuilder editor, Map map)
    {
        this.skin = skin;
        this.menu = menu;
        this.editor = editor;
        this.map = map;

        this.background = new Image(EditorAssets.getUIAtlas().createPatch("load-background"));
        this.stack = new Stack();
        this.table = new Table();
        this.table.left().top();

        this.scrollPane = new ScrollPane(this.table, skin);

        this.stack.add(this.background);

        this.addActor(this.scrollPane);
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

        this.scrollPane.setSize(width, height);
        this.scrollPane.invalidateHierarchy();

        this.background.setBounds(0, 0, width, height);
        this.stack.setSize(width, height);
        this.stack.invalidateHierarchy();

        super.setSize(width, height);
    }

    public void newProperty(boolean light)
    {
        if(map.selectedObjects.size > 0)
        {
            for (int i = 0; i < map.selectedObjects.size; i++)
            {
                if(light)
                    this.map.selectedObjects.get(i).properties.add(new PropertyField(this.skin, menu, true, 1, 1, 1, 1, 100, 25));
                else
                    this.map.selectedObjects.get(i).properties.add(new PropertyField("Property", "Value", this.skin, menu, true));
            }
        }
        else if(map.spriteMenu.selectedSpriteTools.size > 0)
        {
            for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
            {
                if(light)
                    this.map.spriteMenu.selectedSpriteTools.get(i).properties.add(new PropertyField(this.skin, menu, true, 1, 1, 1, 1, 100, 25));
                else
                    this.map.spriteMenu.selectedSpriteTools.get(i).properties.add(new PropertyField("Property", "Value", this.skin, menu, true));
            }
        }
        else
        {
            if(light)
                this.map.propertyMenu.mapPropertyPanel.properties.add(new PropertyField(this.skin, menu, true, 1, 1, 1, 1, 100, 25));
            else
                this.map.propertyMenu.mapPropertyPanel.properties.add(new PropertyField("Property", "Value", this.skin, menu, true));
        }
    }

    public void newProperty(String property, String value)
    {
        if(map.selectedObjects.size > 0)
        {
            for (int i = 0; i < map.selectedObjects.size; i++)
                this.map.selectedObjects.get(i).properties.add(new PropertyField(property, value, this.skin, menu, true));
        }
        else if(map.spriteMenu.selectedSpriteTools.size > 0)
        {
            for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                this.map.spriteMenu.selectedSpriteTools.get(i).properties.add(new PropertyField(property, value, this.skin, menu, true));
        }
        else
        {
            this.map.propertyMenu.mapPropertyPanel.properties.add(new PropertyField(property, value, this.skin, menu, true));
        }
    }

    public void newProperty(float r, float g, float b, float a)
    {
        if(map.selectedObjects.size > 0)
        {
            for (int i = 0; i < map.selectedObjects.size; i++)
                this.map.selectedObjects.get(i).properties.add(new PropertyField(this.skin, menu, true, r, g, b, a));
        }
        else if(map.spriteMenu.selectedSpriteTools.size > 0)
        {
            for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                this.map.spriteMenu.selectedSpriteTools.get(i).properties.add(new PropertyField(this.skin, menu, true, r, g, b, a));
        }
        else
        {
            this.map.propertyMenu.mapPropertyPanel.properties.add(new PropertyField(this.skin, menu, true, r, g, b, a));
        }
    }

    public void newProperty(float r, float g, float b, float a, float distance, int rayAmount)
    {
        if(map.selectedObjects.size > 0)
        {
            for (int i = 0; i < map.selectedObjects.size; i++)
                this.map.selectedObjects.get(i).properties.add(new PropertyField(this.skin, menu, true, r, g, b, a, distance, rayAmount));
        }
        else if(map.spriteMenu.selectedSpriteTools.size > 0)
        {
            for (int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i++)
                this.map.spriteMenu.selectedSpriteTools.get(i).properties.add(new PropertyField(this.skin, menu, true, r, g, b, a, distance, rayAmount));
        }
        else
            this.map.propertyMenu.mapPropertyPanel.properties.add(new PropertyField(this.skin, menu, true, r, g, b, a, distance, rayAmount));
    }

    /** Remove all properties with the property value of the string.
     * Return true if something was removed to allow for recursive removing all the properties.
     * External use always returns false. */
    public boolean removeProperty(String propertyName)
    {
        for(int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i ++)
        {
            PropertyField propertyField = null;
            Array<PropertyField> properties = map.spriteMenu.selectedSpriteTools.get(i).properties;
            for (int k = 0; k < properties.size; k++)
            {
                propertyField = properties.get(k);
                if (propertyField.getProperty().equals(propertyName))
                    break;

            }
            if (propertyField != null)
            {
                properties.removeValue(propertyField, false);
                return removeProperty(propertyName);
            }
        }
        return false;
    }

    public void removeProperty(PropertyField propertyField)
    {
        for(int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i ++)
        {
            Array<PropertyField> properties = map.spriteMenu.selectedSpriteTools.get(i).properties;
            properties.removeValue(propertyField, false);
        }
        for(int i = 0; i < map.selectedObjects.size; i ++)
        {
            Array<PropertyField> properties = map.selectedObjects.get(i).properties;
            properties.removeValue(propertyField, false);
        }
        map.propertyMenu.mapPropertyPanel.properties.removeValue(propertyField, false);
    }

    /** Rebuilds the table to remove gaps when removing properties. */
    public void rebuild()
    {
        this.table.clearChildren();
        // Selecting one sprite tool
        if(map.spriteMenu.selectedSpriteTools.size == 1)
        {
            menu.propertyTypeLabel.setText("Sprite Tool Custom Properties");
            this.table.add(this.menu.propertyTypeLabel).row();
            Array<PropertyField> properties = map.spriteMenu.selectedSpriteTools.first().properties;
            for (int i = 0; i < properties.size; i++)
                this.table.add(properties.get(i)).padBottom(1).row();
        }
        // Selecting more than one sprite tool
        else if(map.spriteMenu.selectedSpriteTools.size > 1) // Only add common properties and locked properties
        {
            menu.propertyTypeLabel.setText("Sprite Tools Custom Properties");
            SpriteTool firstTool = map.spriteMenu.selectedSpriteTools.first();
            for(int i = 0; i < firstTool.lockedProperties.size; i ++)
                this.table.add(firstTool.lockedProperties.get(i)).padBottom(1).row();
            this.table.add(this.menu.propertyTypeLabel).row();
            for(int i = 0; i < firstTool.properties.size; i ++)
            {
                boolean commonProperty = true;
                for(int k = 1; k < map.spriteMenu.selectedSpriteTools.size; k ++)
                {
                    if(!map.spriteMenu.selectedSpriteTools.get(k).properties.contains(firstTool.properties.get(i), false))
                    {
                        commonProperty = false;
                        break;
                    }
                }
                if(commonProperty)
                    this.table.add(firstTool.properties.get(i)).padBottom(1).row();
            }
        }
        // Selecting one object
        else if(map.selectedObjects.size == 1)
        {
            menu.propertyTypeLabel.setText("Object Custom Properties");
            this.table.add(this.menu.propertyTypeLabel).row();
            Array<PropertyField> properties = map.selectedObjects.first().properties;
            for (int i = 0; i < properties.size; i++)
                this.table.add(properties.get(i)).padBottom(1).row();
        }
        // Selecting more than one object
        else if(map.selectedObjects.size > 1) // Only add common properties
        {
            menu.propertyTypeLabel.setText("Objects Custom Properties");
            this.table.add(this.menu.propertyTypeLabel).row();
            MapObject mapObject = map.selectedObjects.first();
            for(int i = 0; i < mapObject.properties.size; i ++)
            {
                boolean commonProperty = true;
                for(int k = 1; k < map.selectedObjects.size; k ++)
                {
                    if(!map.selectedObjects.get(k).properties.contains(mapObject.properties.get(i), false))
                    {
                        commonProperty = false;
                        break;
                    }
                }
                if(commonProperty)
                    this.table.add(mapObject.properties.get(i)).padBottom(1).row();
            }
        }
        // Selecting nothing, show map properties
        else
        {
            menu.propertyTypeLabel.setText("Map Custom Properties");
            this.table.add(this.menu.propertyTypeLabel).row();
            for(int i = 0; i < map.propertyMenu.mapPropertyPanel.properties.size; i ++)
            {
                this.table.add(map.propertyMenu.mapPropertyPanel.properties.get(i)).padBottom(1).row();
            }
        }
        setSize(getWidth(), getHeight()); // Resize to fit the fields
    }
}