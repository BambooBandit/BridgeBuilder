package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.Layer;

public abstract class LayerData
{
    public String name;
    public int w, h;
    public float x, y, z;
    public LayerData(){}
    public LayerData(Layer layer)
    {
        this.name = layer.layerField.layerName.getText();
        this.w = layer.width;
        this.h = layer.height;
        this.x = layer.x;
        this.y = layer.y;
        this.z = layer.z;
    }
}
