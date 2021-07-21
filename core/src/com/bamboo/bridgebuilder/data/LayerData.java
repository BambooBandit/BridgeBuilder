package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.Layer;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.*;

import java.util.ArrayList;

public abstract class LayerData
{
    public String name;
    public int w, h;
    public float x, y, z;
    public ArrayList<PropertyData> props;

    public LayerData(){}
    public LayerData(Layer layer)
    {
        this.name = layer.layerField.layerName.getText();
        this.w = layer.width;
        this.h = layer.height;
        this.x = layer.x;
        this.y = layer.y;
        this.z = layer.z;

        if(layer.properties.size > 0)
            this.props = new ArrayList<>();
        for(int i = 0; i < layer.properties.size; i ++)
        {
            PropertyField property = (PropertyField) layer.properties.get(i);
            if(property instanceof ColorPropertyField)
                this.props.add(new ColorPropertyFieldData((ColorPropertyField) property));
            else if(property instanceof LightPropertyField)
                this.props.add(new LightPropertyFieldData((LightPropertyField) property));
            else if(property instanceof FieldFieldPropertyValuePropertyField)
                this.props.add(new FieldFieldPropertyValuePropertyFieldData((FieldFieldPropertyValuePropertyField) property));
            else if(property instanceof LabelFieldPropertyValuePropertyField)
                this.props.add(new LabelFieldPropertyValuePropertyFieldData((LabelFieldPropertyValuePropertyField) property));
        }
    }
}
