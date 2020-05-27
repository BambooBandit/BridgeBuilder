package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.LightPropertyField;

public class LightPropertyFieldData extends PropertyData
{
    public float r, g, b, a;
    public float dis;
    public int ray;
    public LightPropertyFieldData(){}
    public LightPropertyFieldData(LightPropertyField propertyField)
    {
        super(propertyField);
        this.r = propertyField.getR();
        this.g = propertyField.getG();
        this.b = propertyField.getB();
        this.a = propertyField.getA();
        this.dis = propertyField.getDistance();
        this.ray = propertyField.getRayAmount();
    }
}
