package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.MapObject;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.*;

import java.util.ArrayList;

public abstract class MapObjectData extends LayerChildData
{
    public ArrayList<PropertyData> props;

    // Used for attached map objects only
    public boolean attached = false;
    public float offsetX, offsetY;

    public MapObjectData() {}
    public MapObjectData(MapObject mapObject)
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
        }

        if(mapObject.attachedSprite != null)
        {
            this.attached = true;
            this.offsetX = mapObject.getX() - mapObject.attachedSprite.getX();
            this.offsetY = mapObject.getY() - mapObject.attachedSprite.getY();
        }
    }
}