package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.LabelFieldPropertyValuePropertyField;

public class LabelFieldPropertyValuePropertyFieldData extends PropertyData
{
    public String p; // prop
    public String v; // val
    public LabelFieldPropertyValuePropertyFieldData(){}
    public LabelFieldPropertyValuePropertyFieldData(LabelFieldPropertyValuePropertyField property)
    {
        super(property);
        this.p = property.getProperty();
        this.v = property.getValue();
    }
}
