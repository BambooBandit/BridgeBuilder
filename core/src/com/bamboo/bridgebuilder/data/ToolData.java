package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.AttachedMapObjectManager;
import com.bamboo.bridgebuilder.map.MapObject;
import com.bamboo.bridgebuilder.map.MapPoint;
import com.bamboo.bridgebuilder.map.MapPolygon;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.*;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

import java.util.ArrayList;

public class ToolData
{
    public String name;
    public String type;
    public ArrayList<PropertyData> propertyData;
    public ArrayList<PropertyData> lockedPropertyData;
    public ArrayList<MapObjectData> attachedObjects;
    public ToolData(){}
    public ToolData(SpriteTool spriteTool)
    {
        this.name = spriteTool.name;
        this.type = spriteTool.tool.type;

        this.propertyData = new ArrayList<>();
        for(int i = 0; i < spriteTool.properties.size; i ++)
        {
            PropertyField property = spriteTool.properties.get(i);
            if(property instanceof ColorPropertyField)
                this.propertyData.add(new ColorPropertyFieldData((ColorPropertyField) property));
            else if(property instanceof LightPropertyField)
                this.propertyData.add(new LightPropertyFieldData((LightPropertyField) property));
            else if(property instanceof FieldFieldPropertyValuePropertyField)
                this.propertyData.add(new FieldFieldPropertyValuePropertyFieldData((FieldFieldPropertyValuePropertyField) property));
            else if(property instanceof LabelFieldPropertyValuePropertyField)
                this.propertyData.add(new LabelFieldPropertyValuePropertyFieldData((LabelFieldPropertyValuePropertyField) property));
        }

        this.lockedPropertyData = new ArrayList<>();
        for(int i = 0; i < spriteTool.lockedProperties.size; i ++)
        {
            PropertyField property = spriteTool.lockedProperties.get(i);
            if(property instanceof ColorPropertyField)
                this.lockedPropertyData.add(new ColorPropertyFieldData((ColorPropertyField) property));
            else if(property instanceof LightPropertyField)
                this.lockedPropertyData.add(new LightPropertyFieldData((LightPropertyField) property));
            else if(property instanceof FieldFieldPropertyValuePropertyField)
                this.lockedPropertyData.add(new FieldFieldPropertyValuePropertyFieldData((FieldFieldPropertyValuePropertyField) property));
            else if(property instanceof LabelFieldPropertyValuePropertyField)
                this.lockedPropertyData.add(new LabelFieldPropertyValuePropertyFieldData((LabelFieldPropertyValuePropertyField) property));
        }

        this.attachedObjects = new ArrayList<>();
        for(int i = 0; i < spriteTool.attachedMapObjectManagers.size; i++)
        {
            AttachedMapObjectManager attachedMapObjectManager = spriteTool.attachedMapObjectManagers.get(i);
            MapObject mapObject = attachedMapObjectManager.attachedMapObjects.first();
            MapObjectData mapObjectData;
            if(mapObject instanceof MapPoint)
                mapObjectData = new MapPointData((MapPoint) mapObject);
            else
                mapObjectData = new MapPolygonData((MapPolygon) mapObject);
            this.attachedObjects.add(mapObjectData);
        }
    }
}
