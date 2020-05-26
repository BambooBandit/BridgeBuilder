package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.MapObject;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.*;

import java.util.ArrayList;

public abstract class MapObjectData extends LayerChildData
{
    public ArrayList<PropertyData> propertyData;

    // Used for attached map objects only
    public boolean attached = false;
    public float offsetX, offsetY;

    public MapObjectData() {}
    public MapObjectData(MapObject mapObject)
    {
        super(mapObject);

        this.propertyData = new ArrayList<>();
        for(int i = 0; i < mapObject.properties.size; i ++)
        {
            PropertyField property = mapObject.properties.get(i);
            if(property instanceof ColorPropertyField)
                this.propertyData.add(new ColorPropertyFieldData((ColorPropertyField) property));
            else if(property instanceof LightPropertyField)
                this.propertyData.add(new LightPropertyFieldData((LightPropertyField) property));
            else if(property instanceof FieldFieldPropertyValuePropertyField)
                this.propertyData.add(new FieldFieldPropertyValuePropertyFieldData((FieldFieldPropertyValuePropertyField) property));
            else if(property instanceof LabelFieldPropertyValuePropertyField)
                this.propertyData.add(new LabelFieldPropertyValuePropertyFieldData((LabelFieldPropertyValuePropertyField) property));
        }

        if(mapObject.attachedSprite != null)
        {
            this.attached = true;
            this.offsetX = mapObject.getX() - mapObject.attachedSprite.getX();
            this.offsetY = mapObject.getY() - mapObject.attachedSprite.getY();
        }
    }
}
