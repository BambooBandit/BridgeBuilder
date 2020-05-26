package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.Layer;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.ObjectLayer;
import com.bamboo.bridgebuilder.map.SpriteLayer;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.*;

import java.util.ArrayList;

public class MapData
{
    public String name;
    public ArrayList<SpriteSheetData> spriteSheets;
    public ArrayList<LayerData> layers;
    public ArrayList<PropertyData> mapLockedProperties;
    public ArrayList<PropertyData> mapProperties;

    public MapData(){}
    public MapData(Map map, boolean settingFLMDefaults)
    {
        this.name = map.name;
        this.mapLockedProperties = new ArrayList<>();
        this.mapProperties = new ArrayList<>();
        for(int i = 0; i < map.propertyMenu.mapPropertyPanel.lockedProperties.size; i ++)
        {
            PropertyField property = map.propertyMenu.mapPropertyPanel.lockedProperties.get(i);
            if(property instanceof ColorPropertyField)
                this.mapLockedProperties.add(new ColorPropertyFieldData((ColorPropertyField) property));
            else if(property instanceof LightPropertyField)
                this.mapLockedProperties.add(new LightPropertyFieldData((LightPropertyField) property));
            else if(property instanceof FieldFieldPropertyValuePropertyField)
                this.mapLockedProperties.add(new FieldFieldPropertyValuePropertyFieldData((FieldFieldPropertyValuePropertyField) property));
            else if(property instanceof LabelFieldPropertyValuePropertyField)
                this.mapLockedProperties.add(new LabelFieldPropertyValuePropertyFieldData((LabelFieldPropertyValuePropertyField) property));
        }
        for(int i = 0; i < map.propertyMenu.mapPropertyPanel.properties.size; i ++)
        {
            PropertyField property = map.propertyMenu.mapPropertyPanel.properties.get(i);
            if(property instanceof ColorPropertyField)
                this.mapProperties.add(new ColorPropertyFieldData((ColorPropertyField) property));
            else if(property instanceof LightPropertyField)
                this.mapProperties.add(new LightPropertyFieldData((LightPropertyField) property));
            else if(property instanceof FieldFieldPropertyValuePropertyField)
                this.mapProperties.add(new FieldFieldPropertyValuePropertyFieldData((FieldFieldPropertyValuePropertyField) property));
            else if(property instanceof LabelFieldPropertyValuePropertyField)
                this.mapProperties.add(new LabelFieldPropertyValuePropertyFieldData((LabelFieldPropertyValuePropertyField) property));
        }
        this.spriteSheets = new ArrayList<>(4);

//        boolean map = false;
//        boolean tiles = false;
//        boolean flatMap = false;
//        boolean canyonMap = false;
//        boolean canyonBackdrop = false;
//        boolean desertTiles = false;
//        boolean canyonTiles = false;
//        boolean mesaMap = false;

        this.layers = new ArrayList<>();
        for(int i = 0; i < map.layers.size; i ++)
        {
            Layer layer = map.layers.get(i);
            if(layer instanceof SpriteLayer)
                this.layers.add(new SpriteLayerData(layer));
            else if(layer instanceof ObjectLayer)
                this.layers.add(new ObjectLayerData(layer));
        }
        for(int i = 0; i < map.spriteMenu.spriteSheets.size; i ++)
            this.spriteSheets.add(new SpriteSheetData(map, map.spriteMenu.spriteSheets.get(i)));

        // Remove all the map data such as layers and tiles since they are not default information
        if(settingFLMDefaults)
        {
            this.name = "defaultBBM.bbm";
            this.layers.clear();
        }
    }
}

