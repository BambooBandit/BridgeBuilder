package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.LabelFieldPropertyValuePropertyField;

public class LabelFieldPropertyValuePropertyFieldData extends PropertyData
{
    public String property;
    public String value;
    public LabelFieldPropertyValuePropertyFieldData(){}
    public LabelFieldPropertyValuePropertyFieldData(LabelFieldPropertyValuePropertyField property)
    {
        super(property);
        this.property = property.getProperty();
        this.value = property.getValue();
    }
}
