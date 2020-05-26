package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.ColorPropertyField;

public class ColorPropertyFieldData extends PropertyData
{
    public float r, g, b, a;
    public ColorPropertyFieldData(){}
    public ColorPropertyFieldData(ColorPropertyField propertyField)
    {
        this.r = propertyField.getR();
        this.g = propertyField.getG();
        this.b = propertyField.getB();
        this.a = propertyField.getA();
    }
}
