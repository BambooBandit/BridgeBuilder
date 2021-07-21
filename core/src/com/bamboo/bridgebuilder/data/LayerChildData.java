package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.LayerChild;

public abstract class LayerChildData
{
    public float x, y;
    public long fId; // to flicker mapSprite id
    public long i; // id

    public LayerChildData(){}
    public LayerChildData(LayerChild layerChild)
    {
        if(layerChild.toFlicker != null)
            this.fId = layerChild.toFlicker.id;

        this.i = layerChild.id;

        this.x = layerChild.getX();
        this.y = layerChild.getY();
    }
}
