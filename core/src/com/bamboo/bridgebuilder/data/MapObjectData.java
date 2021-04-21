package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.MapObject;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.*;

import java.util.ArrayList;

public abstract class MapObjectData extends LayerChildData
{
    public ArrayList<PropertyData> props;
    public ArrayList<PropertyData> lProps; // locked properties
    public long id;

    // Used for attached map objects only
    public float offsetX, offsetY;

    public MapObjectData() {}
    public MapObjectData(MapObject mapObject, float offsetX, float offsetY)
    {
        super(mapObject);

        this.id = mapObject.id;

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
        }
        this.lProps = new ArrayList<>();
        for(int i = 0; i < mapObject.lockedProperties.size; i ++)
        {
            PropertyField property = mapObject.lockedProperties.get(i);
            if(property instanceof ColorPropertyField)
                this.lProps.add(new ColorPropertyFieldData((ColorPropertyField) property));
            else if(property instanceof LightPropertyField)
                this.lProps.add(new LightPropertyFieldData((LightPropertyField) property));
            else if(property instanceof FieldFieldPropertyValuePropertyField)
                this.lProps.add(new FieldFieldPropertyValuePropertyFieldData((FieldFieldPropertyValuePropertyField) property));
            else if(property instanceof LabelFieldPropertyValuePropertyField)
                this.lProps.add(new LabelFieldPropertyValuePropertyFieldData((LabelFieldPropertyValuePropertyField) property));
        }

        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
}
