package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.LayerChild;

public abstract class LayerChildData
{
    public float x, y;
    public LayerChildData(){}
    public LayerChildData(LayerChild layerChild)
    {
        this.x = layerChild.getX();
        this.y = layerChild.getY();
    }
}
