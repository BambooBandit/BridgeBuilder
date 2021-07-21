package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.OpaqueColorPropertyField;

public class OpaqueColorPropertyFieldData extends PropertyData
{
    public String p; // property
    public float r, g, b;

    public OpaqueColorPropertyFieldData(){}
    public OpaqueColorPropertyFieldData(OpaqueColorPropertyField propertyField)
    {
        this.p = propertyField.getProperty();
        this.r = propertyField.getR();
        this.g = propertyField.getG();
        this.b = propertyField.getB();
    }
}
