package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.LabelLabelPropertyValuePropertyField;

public class LabelLabelPropertyValuePropertyFieldData extends PropertyData
{
    public String p; // prop
    public String v; // val
    public LabelLabelPropertyValuePropertyFieldData(){}
    public LabelLabelPropertyValuePropertyFieldData(LabelLabelPropertyValuePropertyField property)
    {
        super(property);
        this.p = property.getProperty();
        this.v = property.getValue();
    }
}
