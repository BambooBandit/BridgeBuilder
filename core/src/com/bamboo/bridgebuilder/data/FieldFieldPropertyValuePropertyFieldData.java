package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.FieldFieldPropertyValuePropertyField;

public class FieldFieldPropertyValuePropertyFieldData extends PropertyData
{
    public String property;
    public String value;
    FieldFieldPropertyValuePropertyFieldData(){}
    FieldFieldPropertyValuePropertyFieldData(FieldFieldPropertyValuePropertyField property)
    {
        super(property);
        this.property = property.getProperty();
        this.value = property.getValue();
    }
}
