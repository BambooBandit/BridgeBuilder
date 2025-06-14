package com.bamboo.bridgebuilder.ui.propertyMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.EditorAssets;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.map.Layer;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapObject;
import com.bamboo.bridgebuilder.map.MapSprite;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.FieldFieldPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.LightPropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;
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


    public void newProperty(float r, float g, float b, float a, float distance, int rayAmount, Layer selectedLayer, Array<MapSprite> selectedMapSprites, Array<SpriteTool> selectedSpriteTools, Array<MapObject> selectedMapObjects)
    {
        if(selectedMapObjects.size > 0)
        {
            for (int i = 0; i < selectedMapObjects.size; i++)
                addPropertyToList(r, g, b, a, distance, rayAmount, selectedMapObjects.get(i).properties);
        }
        else if(selectedSpriteTools.size > 0)
        {
            for (int i = 0; i < selectedSpriteTools.size; i++)
                addPropertyToList(r, g, b, a, distance, rayAmount, selectedSpriteTools.get(i).properties);
        }
        else if(selectedMapSprites.size > 0)
        {
            for (int i = 0; i < selectedMapSprites.size; i++)
                addPropertyToList(r, g, b, a, distance, rayAmount, selectedMapSprites.get(i).instanceSpecificProperties);
        }
        else if(selectedLayer != null)
            addPropertyToList(r, g, b, a, distance, rayAmount, selectedLayer.properties);
        else
            addPropertyToList(r, g, b, a, distance, rayAmount, this.map.propertyMenu.mapPropertyPanel.properties);
    }

    public void newProperty(Layer selectedLayer, Array<MapSprite> selectedMapSprites, Array<SpriteTool> selectedSpriteTools, Array<MapObject> selectedMapObjects)
    {
        if(selectedMapObjects.size > 0)
        {
            for (int i = 0; i < selectedMapObjects.size; i++)
                addPropertyToList(selectedMapObjects.get(i).properties);
        }
        else if(selectedSpriteTools.size > 0)
        {
            for (int i = 0; i < selectedSpriteTools.size; i++)
                addPropertyToList(selectedSpriteTools.get(i).properties);
        }
        else if(selectedMapSprites.size > 0)
        {
            for (int i = 0; i < selectedMapSprites.size; i++)
                addPropertyToList(selectedMapSprites.get(i).instanceSpecificProperties);
        }
        else if(selectedLayer != null)
            addPropertyToList(selectedLayer.properties);
        else
            addPropertyToList(this.map.propertyMenu.mapPropertyPanel.properties);
    }

    private void addPropertyToList(float r, float g, float b, float a, float distance, int rayAmount, Array<PropertyField> properties)
    {
        if(Utils.getLightField(properties) == null) // An object can only have maximum one light.
            properties.add(new LightPropertyField(this.skin, menu, properties, true, r, g, b, a, distance, rayAmount));
    }

    private void addPropertyToList(Array<PropertyField> properties)
    {
        properties.add(new FieldFieldPropertyValuePropertyField("Property", "Value", this.skin, menu, properties, true));
    }

    public void newProperty(String property, String value, Layer layer, Array<MapSprite> selectedSprites, Array<SpriteTool> selectedSpriteTools, Array<MapObject> selectedMapObjects)
    {
        if(selectedMapObjects.size > 0)
        {
            for (int i = 0; i < selectedMapObjects.size; i++)
            {
                MapObject mapObject = selectedMapObjects.get(i);
                mapObject.properties.add(new FieldFieldPropertyValuePropertyField(property, value, this.skin, menu, mapObject.properties, true));
            }
        }
        else if(selectedSpriteTools.size > 0)
        {
            for (int i = 0; i < selectedSpriteTools.size; i++)
            {
                SpriteTool spriteTool = selectedSpriteTools.get(i);
                spriteTool.properties.add(new FieldFieldPropertyValuePropertyField(property, value, this.skin, menu, spriteTool.properties, true));
            }
        }
        else if(selectedSprites != null && selectedSprites.size > 0)
        {
            for (int i = 0; i < selectedSprites.size; i++)
            {
                MapSprite mapSprite = selectedSprites.get(i);
                mapSprite.instanceSpecificProperties.add(new FieldFieldPropertyValuePropertyField(property, value, this.skin, menu, mapSprite.instanceSpecificProperties, true));
            }
        }
        else if(layer != null)
            layer.properties.add(new FieldFieldPropertyValuePropertyField(property, value, this.skin, menu, layer.properties, true));
        else
            this.map.propertyMenu.mapPropertyPanel.properties.add(new FieldFieldPropertyValuePropertyField(property, value, this.skin, menu, this.map.propertyMenu.mapPropertyPanel.properties, true));
    }

    /** Remove all properties with the property value of the string.
     * Return true if something was removed to allow for recursive removing all the properties.
     * External use always returns false. */
    public boolean removeProperty(String propertyName)
    {
        for(int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i ++)
        {
            Array<PropertyField> properties = map.spriteMenu.selectedSpriteTools.get(i).properties;
            PropertyField propertyField = Utils.getPropertyField(properties, propertyName);
            if (propertyField != null)
            {
                properties.removeValue(propertyField, false);
                return removeProperty(propertyName);
            }
        }
        for(int i = 0; i < map.selectedSprites.size; i ++)
        {
            Array<PropertyField> properties = map.selectedSprites.get(i).instanceSpecificProperties;
            PropertyField propertyField = Utils.getPropertyField(properties, propertyName);
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
        for(int i = 0; i < map.selectedSprites.size; i ++)
        {
            Array<PropertyField> properties = map.selectedSprites.get(i).instanceSpecificProperties;
            properties.removeValue(propertyField, false);
        }
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
        if(map.selectedLayer != null)
            map.selectedLayer.properties.removeValue(propertyField, false);
        map.propertyMenu.mapPropertyPanel.properties.removeValue(propertyField, false);
    }

    /** Rebuilds the table to remove gaps when removing properties. */
    public void rebuild()
    {
        this.table.clearChildren();
        // Selecting one object
        if(map.selectedObjects.size == 1)
        {
            menu.propertyTypeLabel.setText("Object Custom Properties");
            for(int i = 0; i < map.selectedObjects.first().lockedProperties.size; i ++)
                this.table.add(map.selectedObjects.first().lockedProperties.get(i)).padBottom(1).row();
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
                    if(!Utils.containsEquivalentPropertyField(map.selectedObjects.get(k).properties, mapObject.properties.get(i)))
                    {
                        commonProperty = false;
                        break;
                    }
                }
                if(commonProperty)
                    this.table.add(mapObject.properties.get(i)).padBottom(1).row();
            }
        }
        // Selecting one sprite tool
        else if(map.spriteMenu.selectedSpriteTools.size == 1)
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
                    if(!Utils.containsEquivalentPropertyField(map.spriteMenu.selectedSpriteTools.get(k).properties, firstTool.properties.get(i)))
                    {
                        commonProperty = false;
                        break;
                    }
                }
                if(commonProperty)
                    this.table.add(firstTool.properties.get(i)).padBottom(1).row();
            }
        }
        // Selecting one map sprite
        else if(map.selectedSprites.size == 1)
        {
            menu.propertyTypeLabel.setText("Map Sprite Custom Properties");
            this.table.add(this.menu.propertyTypeLabel).row();
            Array<PropertyField> properties = map.selectedSprites.first().instanceSpecificProperties;
            for (int i = 0; i < properties.size; i++)
                this.table.add(properties.get(i)).padBottom(1).row();
        }
        // Selecting more than one map sprite
        else if(map.selectedSprites.size > 1) // Only add common properties and locked properties
        {
            menu.propertyTypeLabel.setText("Map Sprites Custom Properties");
            MapSprite firstMapSprite = map.selectedSprites.first();
            for(int i = 0; i < firstMapSprite.lockedProperties.size; i ++)
                this.table.add(firstMapSprite.lockedProperties.get(i)).padBottom(1).row();
            this.table.add(this.menu.propertyTypeLabel).row();
            for(int i = 0; i < firstMapSprite.instanceSpecificProperties.size; i ++)
            {
                boolean commonProperty = true;
                for(int k = 1; k < map.selectedSprites.size; k ++)
                {
                    if(!Utils.containsEquivalentPropertyField(map.selectedSprites.get(k).instanceSpecificProperties, firstMapSprite.instanceSpecificProperties.get(i)))
                    {
                        commonProperty = false;
                        break;
                    }
                }
                if(commonProperty)
                    this.table.add(firstMapSprite.instanceSpecificProperties.get(i)).padBottom(1).row();
            }
        }
        // Selecting a layer
        else if(map.selectedLayer != null)
        {
            menu.propertyTypeLabel.setText("Layer Custom Properties");
            this.table.add(this.menu.propertyTypeLabel).row();
            for(int i = 0; i < map.selectedLayer.properties.size; i ++)
                this.table.add((PropertyField) map.selectedLayer.properties.get(i)).padBottom(1).row();
        }
        // Selecting nothing, show map properties
        else
        {
            menu.propertyTypeLabel.setText("Map Custom Properties");
            this.table.add(this.menu.propertyTypeLabel).row();
            for(int i = 0; i < map.propertyMenu.mapPropertyPanel.properties.size; i ++)
                this.table.add(map.propertyMenu.mapPropertyPanel.properties.get(i)).padBottom(1).row();
        }
        setSize(getWidth(), getHeight()); // Resize to fit the fields
    }
}