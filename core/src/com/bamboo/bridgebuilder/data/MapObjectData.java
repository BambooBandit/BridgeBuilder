package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.MapObject;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.*;

import java.util.ArrayList;

public abstract class MapObjectData extends LayerChildData
{
    public ArrayList<PropertyData> props;

    // Used for attached map objects only
    public float offsetX, offsetY;

    public MapObjectData() {}
    public MapObjectData(MapObject mapObject, float offsetX, float offsetY)
    {
        super(mapObject);

        this.props = new ArrayList<>();
        for(int i = 0; i < mapObject.properties.size; i ++)
        {
            PropertyField property = mapObject.properties.get(i);
            if(property instanceof ColorPropertyField)
                this.props.add(new ColorPropertyFieldData((ColorPropertyField) property));
            else if(property instanceof LightPropertyField)
                this.props.add(new LightPropertyFieldData((LightPropertyField) property));
            else if(property instanceof FieldFieldPropertyValuePropertyField)
                this.props.add(new FieldFieldPropertyValuePropertyFieldData((FieldFieldPropertyValuePropertyField) property));
            else if(property instanceof LabelFieldPropertyValuePropertyField)
                this.props.add(new LabelFieldPropertyValuePropertyFieldData((LabelFieldPropertyValuePropertyField) property));
            else if(property instanceof LabelLabelPropertyValuePropertyField)
                this.props.add(new LabelLabelPropertyValuePropertyFieldData((LabelLabelPropertyValuePropertyField) property));
        }

        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
}
