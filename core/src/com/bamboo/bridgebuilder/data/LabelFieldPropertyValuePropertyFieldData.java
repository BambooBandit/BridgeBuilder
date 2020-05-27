package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.LabelFieldPropertyValuePropertyField;

public class LabelFieldPropertyValuePropertyFieldData extends PropertyData
{
    public String prop;
    public String val;
    public LabelFieldPropertyValuePropertyFieldData(){}
    public LabelFieldPropertyValuePropertyFieldData(LabelFieldPropertyValuePropertyField property)
    {
        super(property);
        this.prop = property.getProperty();
        this.val = property.getValue();
    }
}
