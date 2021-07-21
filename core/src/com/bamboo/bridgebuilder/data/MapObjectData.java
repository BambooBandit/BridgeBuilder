package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.MapObject;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.*;

import java.util.ArrayList;

public abstract class MapObjectData extends LayerChildData
{
    public ArrayList<PropertyData> p; // props

    // Used for attached map objects only
    public float oX, oY; //offsetX, offsetY

    public MapObjectData() {}
    public MapObjectData(MapObject mapObject, float oX, float oY)
    {
        super(mapObject);

        if(mapObject.properties.size > 0)
            this.p = new ArrayList<>();
        for(int i = 0; i < mapObject.properties.size; i ++)
        {
            PropertyField property = mapObject.properties.get(i);
            if(property instanceof ColorPropertyField)
                this.p.add(new ColorPropertyFieldData((ColorPropertyField) property));
            else if(property instanceof LightPropertyField)
                this.p.add(new LightPropertyFieldData((LightPropertyField) property));
            else if(property instanceof FieldFieldPropertyValuePropertyField)
                this.p.add(new FieldFieldPropertyValuePropertyFieldData((FieldFieldPropertyValuePropertyField) property));
            else if(property instanceof LabelFieldPropertyValuePropertyField)
                this.p.add(new LabelFieldPropertyValuePropertyFieldData((LabelFieldPropertyValuePropertyField) property));
            else if(property instanceof LabelLabelPropertyValuePropertyField)
                this.p.add(new LabelLabelPropertyValuePropertyFieldData((LabelLabelPropertyValuePropertyField) property));
        }

        this.oX = oX;
        this.oY = oY;
    }
}
