package com.bamboo.bridgebuilder.data;

import com.badlogic.gdx.graphics.Color;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.map.*;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.*;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

import java.util.ArrayList;

public class MapData
{
    public String name;
    public ArrayList<SpriteSheetData> sheets;
    public ArrayList<LayerData> layers;
    public ArrayList<PropertyData> lProps;
    public ArrayList<PropertyData> props;
    public ArrayList<GroupMapPolygonData> groups;
    public long idCounter;

    public MapData(){}
    public MapData(Map map, boolean settingBBMDefaults)
    {
        this.idCounter = map.idCounter;
        float oldPerspective = map.perspectiveZoom;
        map.perspectiveZoom = 0;
        map.updateLayerSpriteGrids();
        for(int i = 0; i < map.layers.size; i ++)
        {
            map.layers.get(i).update();
        }
        map.perspectiveZoom = oldPerspective;

        this.name = map.name;
        if(map.propertyMenu.mapPropertyPanel.lockedProperties.size > 0)
            this.lProps = new ArrayList<>();
        if(map.propertyMenu.mapPropertyPanel.properties.size > 0)
            this.props = new ArrayList<>();

        OpaqueColorPropertyField backgroundColor = Utils.getLockedOpaqueColorField("Background", map.propertyMenu.mapPropertyPanel.lockedProperties);
        CellData.defaultCValue = Color.rgb888(backgroundColor.getR(), backgroundColor.getG(), backgroundColor.getB());

        for(int i = 0; i < map.propertyMenu.mapPropertyPanel.lockedProperties.size; i ++)
        {
            PropertyField property = map.propertyMenu.mapPropertyPanel.lockedProperties.get(i);
            if(property instanceof ColorPropertyField)
                this.lProps.add(new ColorPropertyFieldData((ColorPropertyField) property));
            else if(property instanceof OpaqueColorPropertyField)
                this.lProps.add(new OpaqueColorPropertyFieldData((OpaqueColorPropertyField) property));
            else if(property instanceof LightPropertyField)
                this.lProps.add(new LightPropertyFieldData((LightPropertyField) property));
            else if(property instanceof FieldFieldPropertyValuePropertyField)
                this.lProps.add(new FieldFieldPropertyValuePropertyFieldData((FieldFieldPropertyValuePropertyField) property));
            else if(property instanceof LabelFieldPropertyValuePropertyField)
                this.lProps.add(new LabelFieldPropertyValuePropertyFieldData((LabelFieldPropertyValuePropertyField) property));
        }
        for(int i = 0; i < map.propertyMenu.mapPropertyPanel.properties.size; i ++)
        {
            PropertyField property = map.propertyMenu.mapPropertyPanel.properties.get(i);
            if(property instanceof ColorPropertyField)
                this.props.add(new ColorPropertyFieldData((ColorPropertyField) property));
            else if(property instanceof LightPropertyField)
                this.props.add(new LightPropertyFieldData((LightPropertyField) property));
            else if(property instanceof FieldFieldPropertyValuePropertyField)
                this.props.add(new FieldFieldPropertyValuePropertyFieldData((FieldFieldPropertyValuePropertyField) property));
            else if(property instanceof LabelFieldPropertyValuePropertyField)
                this.props.add(new LabelFieldPropertyValuePropertyFieldData((LabelFieldPropertyValuePropertyField) property));
        }
        this.sheets = new ArrayList<>(4);

        this.layers = new ArrayList<>();
        for(int i = 0; i < map.layers.size; i ++)
        {
            Layer layer = map.layers.get(i);
            if(layer instanceof SpriteLayer)
                this.layers.add(new SpriteLayerData((SpriteLayer) layer));
            else if(layer instanceof ObjectLayer)
                this.layers.add(new ObjectLayerData((ObjectLayer) layer));
        }
        FieldFieldPropertyValuePropertyField sky = (FieldFieldPropertyValuePropertyField) Utils.getPropertyField(map.propertyMenu.mapPropertyPanel.properties, "sky");
        if(sky != null)
        {
            if(!map.spriteMenu.hasSpriteSheet(sky.value.getText()))
                map.spriteMenu.createSpriteSheet(sky.value.getText());
        }
        for(int i = 0; i < map.spriteMenu.spriteSheets.size; i ++)
            this.sheets.add(new SpriteSheetData(map, map.spriteMenu.spriteSheets.get(i)));

        if(map.groupPolygons != null && map.groupPolygons.children.size > 0)
        {
            this.groups = new ArrayList<>();
            for (int i = 0; i < map.groupPolygons.children.size; i ++)
            {
                this.groups.add(new GroupMapPolygonData(((MapPolygon)map.groupPolygons.children.get(i)), 0, 0));
            }
        }

        // Remove all the map data such as layers since they are not default information
        if(settingBBMDefaults)
        {
            this.name = "defaultBBM.bbm";
            this.layers.clear();
            if(this.groups != null)
                this.groups.clear();
        }
    }

    public boolean spriteToolContainsOriginalAttachedObjectID(SpriteTool spriteTool, float originalAttachedObjectID)
    {
        for(int i = 0; i < sheets.size(); i ++)
        {
            SpriteSheetData sheet = sheets.get(i);
            if(sheet.name.equals(spriteTool.sheet.name))
            {
                for(int k = 0; k < sheet.tools.size(); k ++)
                {
                    ToolData tool = sheet.tools.get(k);
                    if(tool.o != null)
                    {
                        for (int s = 0; s < tool.o.size(); s++)
                        {
                            MapObjectData object = tool.o.get(s);
                            if (object.i == originalAttachedObjectID)
                                return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}

