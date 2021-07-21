package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.LightPropertyField;

public class LightPropertyFieldData extends PropertyData
{
    public float r, g, b, a;
    public float d; // distance
    public int ra; // ray
    public LightPropertyFieldData(){}
    public LightPropertyFieldData(LightPropertyField propertyField)
    {
        super(propertyField);
        this.r = propertyField.getR();
        this.g = propertyField.getG();
        this.b = propertyField.getB();
        this.a = propertyField.getA();
        this.d = propertyField.getDistance();
        this.ra = propertyField.getRayAmount();
    }
}
