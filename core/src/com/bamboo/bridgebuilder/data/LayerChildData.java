package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.LayerChild;

public abstract class LayerChildData
{
    public float x, y;
    public int fId; // to flicker mapSprite id
    public long id;

    public LayerChildData(){}
    public LayerChildData(LayerChild layerChild)
    {
        if(layerChild.toFlicker != null)
            this.fId = layerChild.toFlicker.id;

        this.id = layerChild.id;

        this.x = layerChild.getX();
        this.y = layerChild.getY();
    }
}
