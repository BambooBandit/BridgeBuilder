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
    public ArrayList<PropertyData> p; // properties
    public ArrayList<PropertyData> lP; // locked properties
    public ArrayList<MapObjectData> o; // attached objects
    public String nT; // next tool
    public String pT; // previous tool
    public ToolData(){}
    public ToolData(SpriteTool spriteTool)
    {
        this.n = spriteTool.name;

        if(spriteTool.properties.size > 0)
            this.p = new ArrayList<>();
        for(int i = 0; i < spriteTool.properties.size; i ++)
        {
            PropertyField property = spriteTool.properties.get(i);
            if(property instanceof ColorPropertyField)
                this.p.add(new ColorPropertyFieldData((ColorPropertyField) property));
            else if(property instanceof LightPropertyField)
                this.p.add(new LightPropertyFieldData((LightPropertyField) property));
            else if(property instanceof FieldFieldPropertyValuePropertyField)
                this.p.add(new FieldFieldPropertyValuePropertyFieldData((FieldFieldPropertyValuePropertyField) property));
            else if(property instanceof LabelFieldPropertyValuePropertyField)
                this.p.add(new LabelFieldPropertyValuePropertyFieldData((LabelFieldPropertyValuePropertyField) property));
        }

        if(spriteTool.lockedProperties.size > 0)
            this.lP = new ArrayList<>();
        for(int i = 0; i < spriteTool.lockedProperties.size; i ++)
        {
            PropertyField property = spriteTool.lockedProperties.get(i);
            if(property instanceof ColorPropertyField)
                this.lP.add(new ColorPropertyFieldData((ColorPropertyField) property));
            else if(property instanceof LightPropertyField)
                this.lP.add(new LightPropertyFieldData((LightPropertyField) property));
            else if(property instanceof FieldFieldPropertyValuePropertyField)
                this.lP.add(new FieldFieldPropertyValuePropertyFieldData((FieldFieldPropertyValuePropertyField) property));
            else if(property instanceof LabelFieldPropertyValuePropertyField)
                this.lP.add(new LabelFieldPropertyValuePropertyFieldData((LabelFieldPropertyValuePropertyField) property));
        }

        if(spriteTool.attachedMapObjectManagers != null)
        {
            if(spriteTool.attachedMapObjectManagers.size > 0)
                this.o = new ArrayList<>();
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
                mapObjectData.i = 0;
                this.o.add(mapObjectData);
            }
        }

        if(spriteTool.nextTool != null)
            this.nT = spriteTool.nextTool.name;
        if(spriteTool.previousTool != null)
            this.pT = spriteTool.previousTool.name;
    }
}
