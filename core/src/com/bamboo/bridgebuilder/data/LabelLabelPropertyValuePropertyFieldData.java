package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.LabelLabelPropertyValuePropertyField;

public class LabelLabelPropertyValuePropertyFieldData extends PropertyData
{
    public String prop;
    public String val;
    public LabelLabelPropertyValuePropertyFieldData(){}
    public LabelLabelPropertyValuePropertyFieldData(LabelLabelPropertyValuePropertyField property)
    {
        super(property);
        this.prop = property.getProperty();
        this.val = property.getValue();
    }
}
