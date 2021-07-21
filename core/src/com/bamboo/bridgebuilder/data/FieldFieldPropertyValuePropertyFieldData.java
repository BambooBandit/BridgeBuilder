package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.FieldFieldPropertyValuePropertyField;

public class FieldFieldPropertyValuePropertyFieldData extends PropertyData
{
    public String p; // prop
    public String v; // val
    FieldFieldPropertyValuePropertyFieldData(){}
    FieldFieldPropertyValuePropertyFieldData(FieldFieldPropertyValuePropertyField property)
    {
        super(property);
        this.p = property.getProperty();
        this.v = property.getValue();
    }
}
