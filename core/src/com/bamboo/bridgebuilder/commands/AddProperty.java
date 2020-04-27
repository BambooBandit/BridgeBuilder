package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.map.Layer;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapObject;
import com.bamboo.bridgebuilder.ui.propertyMenu.PropertyTools;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

public class AddProperty implements Command
{
    private Map map;
    private PropertyTools tool;

    private Layer selectedLayer;
    private Array<SpriteTool> selectedSpriteTools;
    private Array<MapObject> selectedMapObjects;

    private Array<PropertyField> propertyFields; // If this has already been executed, and you had 3 MapObjects selected for example, this array would be 3 long, tracking those new property objects.

    public AddProperty(Map map, PropertyTools tool, Layer selectedLayer, Array<SpriteTool> selectedSpriteTools, Array<MapObject> selectedMapObjects)
    {
        this.map = map;
        this.tool = tool;
        this.selectedLayer = selectedLayer;
        this.selectedSpriteTools = new Array(selectedSpriteTools);
        this.selectedMapObjects = new Array(selectedMapObjects);
    }

    @Override
    public void execute()
    {
        if(this.propertyFields == null)
        {
            this.propertyFields = new Array<>();
            if (tool == PropertyTools.NEW)
                map.propertyMenu.newProperty(false, selectedLayer, selectedSpriteTools, selectedMapObjects);
            else
                map.propertyMenu.newProperty(true, selectedLayer, selectedSpriteTools, selectedMapObjects);

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
            else if(selectedLayer != null)
                selectedLayer.properties.add(propertyFields.first());
            else
                map.propertyMenu.mapPropertyPanel.properties.add(propertyFields.first());
            map.propertyMenu.rebuild();
        }
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
        else if(selectedLayer != null)
            selectedLayer.properties.removeIndex(selectedLayer.properties.size - 1);
        else
            this.map.propertyMenu.mapPropertyPanel.properties.removeIndex(this.map.propertyMenu.mapPropertyPanel.properties.size - 1);
        this.map.propertyMenu.rebuild();
    }
}