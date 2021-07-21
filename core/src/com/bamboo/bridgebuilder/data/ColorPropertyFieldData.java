package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.ColorPropertyField;

public class ColorPropertyFieldData extends PropertyData
{
    public String p; // prop
    public float r, g, b, a;
    public static float defaultAlphaValue = 1;

    public ColorPropertyFieldData(){}
    public ColorPropertyFieldData(ColorPropertyField propertyField)
    {
        this.p = propertyField.getProperty();
        this.r = propertyField.getR();
        this.g = propertyField.getG();
        this.b = propertyField.getB();
        this.a = propertyField.getA() - defaultAlphaValue;
    }
}
