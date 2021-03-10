package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.map.Layer;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapObject;
import com.bamboo.bridgebuilder.map.MapSprite;
import com.bamboo.bridgebuilder.ui.propertyMenu.PropertyTools;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

public class AddProperty implements Command
{
    private Map map;
    private PropertyTools tool;

    private Layer selectedLayer;
    private Array<MapSprite> selectedSprites;
    private Array<SpriteTool> selectedSpriteTools;
    private Array<MapObject> selectedMapObjects;

    private Array<PropertyField> propertyFields; // If this has already been executed, and you had 3 MapObjects selected for example, this array would be 3 long, tracking those new property objects.

    private String property, value;

    private Array<AddProperty> chainedAddPropertyCommands; // Used for adding multiple properties in one execution

    public AddProperty(Map map, PropertyTools tool, Layer selectedLayer, Array<MapSprite> selectedSprites, Array<SpriteTool> selectedSpriteTools, Array<MapObject> selectedMapObjects)
    {
        this.map = map;
        this.tool = tool;
        this.selectedLayer = selectedLayer;
        this.selectedSprites = new Array(selectedSprites);
        this.selectedSpriteTools = new Array(selectedSpriteTools);
        this.selectedMapObjects = new Array(selectedMapObjects);
    }

    public AddProperty(Map map, PropertyTools tool, Layer selectedLayer, Array<SpriteTool> selectedSpriteTools, Array<MapObject> selectedMapObjects, String property, String value)
    {
        this.map = map;
        this.tool = tool;
        this.selectedLayer = selectedLayer;
        this.selectedSpriteTools = new Array(selectedSpriteTools);
        this.selectedMapObjects = new Array(selectedMapObjects);
        this.property = property;
        this.value = value;
    }

    @Override
    public void execute()
    {
        if(this.propertyFields == null)
        {
            this.propertyFields = new Array<>();
            if (tool == PropertyTools.NEW)
            {
                if(property == null)
                    map.propertyMenu.newProperty(false, selectedLayer, selectedSprites, selectedSpriteTools, selectedMapObjects);
                else
                    map.propertyMenu.newProperty(property, value, selectedLayer, selectedSprites, selectedSpriteTools, selectedMapObjects);
            }
            else
                map.propertyMenu.newProperty(true, selectedLayer, selectedSprites, selectedSpriteTools, selectedMapObjects);

            // Track the property fields
            if(selectedMapObjects.size > 0)
            {
                for(int i = 0; i < selectedMapObjects.size; i ++)
                    propertyFields.add(selectedMapObjects.get(i).properties.peek());
            }
            else if(selectedSpriteTools.size > 0)
            {
                for(int i = 0; i < selectedSpriteTools.size; i ++)
                    propertyFields.add(selectedSpriteTools.get(i).properties.peek());
            }
            else if(selectedSprites != null && selectedSprites.size > 0)
            {
                for(int i = 0; i < selectedSprites.size; i ++)
                    propertyFields.add(selectedSprites.get(i).instanceSpecificProperties.peek());
            }
            else if(selectedLayer != null)
                propertyFields.add((PropertyField) selectedLayer.properties.peek());
            else
                propertyFields.add(map.propertyMenu.mapPropertyPanel.properties.peek());
        }
        else
        {
            if(selectedMapObjects.size > 0)
            {
                for(int i = 0; i < selectedMapObjects.size; i ++)
                    selectedMapObjects.get(i).properties.add(propertyFields.get(i));
            }
            else if(selectedSpriteTools.size > 0)
            {
                for(int i = 0; i < selectedSpriteTools.size; i ++)
                    selectedSpriteTools.get(i).properties.add(propertyFields.get(i));
            }
            else if(selectedSprites.size > 0)
            {
                for(int i = 0; i < selectedSprites.size; i ++)
                    selectedSprites.get(i).instanceSpecificProperties.add(propertyFields.get(i));
            }
            else if(selectedLayer != null)
                selectedLayer.properties.add(propertyFields.first());
            else
                map.propertyMenu.mapPropertyPanel.properties.add(propertyFields.first());
            map.propertyMenu.rebuild();
        }

        if(this.chainedAddPropertyCommands != null)
            for(int i = 0; i < this.chainedAddPropertyCommands.size; i ++)
                this.chainedAddPropertyCommands.get(i).execute();
    }

    @Override
    public void undo()
    {
        // Remove the last property
        if(selectedMapObjects.size > 0)
        {
            for(int i = 0; i < selectedMapObjects.size; i ++)
            {
                MapObject mapObject = selectedMapObjects.get(i);
                mapObject.properties.removeIndex(mapObject.properties.size - 1);
            }
        }
        else if(selectedSpriteTools.size > 0)
        {
            for(int i = 0; i < selectedSpriteTools.size; i ++)
            {
                SpriteTool spriteTool = selectedSpriteTools.get(i);
                spriteTool.properties.removeIndex(spriteTool.properties.size - 1);
            }
        }
        else if(selectedSprites.size > 0)
        {
            for(int i = 0; i < selectedSprites.size; i ++)
            {
                MapSprite mapSprite = selectedSprites.get(i);
                mapSprite.instanceSpecificProperties.removeIndex(mapSprite.instanceSpecificProperties.size - 1);
            }
        }
        else if(selectedLayer != null)
            selectedLayer.properties.removeIndex(selectedLayer.properties.size - 1);
        else
            this.map.propertyMenu.mapPropertyPanel.properties.removeIndex(this.map.propertyMenu.mapPropertyPanel.properties.size - 1);
        this.map.propertyMenu.rebuild();

        if(this.chainedAddPropertyCommands != null)
            for(int i = 0; i < this.chainedAddPropertyCommands.size; i ++)
                this.chainedAddPropertyCommands.get(i).undo();
    }

    public void addAddPropertyCommandToChain(AddProperty addProperty)
    {
        if(this.chainedAddPropertyCommands == null)
            this.chainedAddPropertyCommands = new Array<>();
        this.chainedAddPropertyCommands.add(addProperty);
    }
}
