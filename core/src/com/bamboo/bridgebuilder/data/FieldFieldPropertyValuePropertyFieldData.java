package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.FieldFieldPropertyValuePropertyField;

public class FieldFieldPropertyValuePropertyFieldData extends PropertyData
{
    public String prop;
    public String val;
    FieldFieldPropertyValuePropertyFieldData(){}
    FieldFieldPropertyValuePropertyFieldData(FieldFieldPropertyValuePropertyField property)
    {
        super(property);
        this.prop = property.getProperty();
        this.val = property.getValue();
    }
}
