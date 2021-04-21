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
    public String n; // sprite name
    public ArrayList<PropertyData> props; // properties
    public ArrayList<PropertyData> lProps; // locked properties
    public ArrayList<MapObjectData> objs; // attached objects
    public ToolData(){}
    public ToolData(SpriteTool spriteTool)
    {
        this.n = spriteTool.name;

        this.props = new ArrayList<>();
        for(int i = 0; i < spriteTool.properties.size; i ++)
        {
            PropertyField property = spriteTool.properties.get(i);
            if(property instanceof ColorPropertyField)
                this.props.add(new ColorPropertyFieldData((ColorPropertyField) property));
            else if(property instanceof LightPropertyField)
                this.props.add(new LightPropertyFieldData((LightPropertyField) property));
            else if(property instanceof FieldFieldPropertyValuePropertyField)
                this.props.add(new FieldFieldPropertyValuePropertyFieldData((FieldFieldPropertyValuePropertyField) property));
            else if(property instanceof LabelFieldPropertyValuePropertyField)
                this.props.add(new LabelFieldPropertyValuePropertyFieldData((LabelFieldPropertyValuePropertyField) property));
        }

        this.lProps = new ArrayList<>();
        for(int i = 0; i < spriteTool.lockedProperties.size; i ++)
        {
            PropertyField property = spriteTool.lockedProperties.get(i);
            if(property instanceof ColorPropertyField)
                this.lProps.add(new ColorPropertyFieldData((ColorPropertyField) property));
            else if(property instanceof LightPropertyField)
                this.lProps.add(new LightPropertyFieldData((LightPropertyField) property));
            else if(property instanceof FieldFieldPropertyValuePropertyField)
                this.lProps.add(new FieldFieldPropertyValuePropertyFieldData((FieldFieldPropertyValuePropertyField) property));
            else if(property instanceof LabelFieldPropertyValuePropertyField)
                this.lProps.add(new LabelFieldPropertyValuePropertyFieldData((LabelFieldPropertyValuePropertyField) property));
        }

        if(spriteTool.attachedMapObjectManagers != null)
        {
            if(spriteTool.attachedMapObjectManagers.size > 0)
                this.objs = new ArrayList<>();
            spriteTool.attachedMapObjectManagers.sort();
            for (int i = 0; i < spriteTool.attachedMapObjectManagers.size; i++)
            {
                AttachedMapObjectManager attachedMapObjectManager = spriteTool.attachedMapObjectManagers.get(i);
                if(attachedMapObjectManager.attachedMapObjects.size == 0)
                    continue;
                MapObject mapObject = attachedMapObjectManager.attachedMapObjects.first();
                MapObjectData mapObjectData;
                if (mapObject instanceof MapPoint)
                    mapObjectData = new MapPointData((MapPoint) mapObject, attachedMapObjectManager.offsetX, attachedMapObjectManager.offsetY);
                else
                    mapObjectData = new MapPolygonData((MapPolygon) mapObject, attachedMapObjectManager.offsetX, attachedMapObjectManager.offsetY);
                this.objs.add(mapObjectData);
            }
        }
    }
}
