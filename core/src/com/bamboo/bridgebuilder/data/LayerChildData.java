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
        // use this to convert from old maps to new
//        if(this instanceof MapPointData || this instanceof MapPolygonData)
//        {
//            MapObject mapObject = (MapObject) layerChild;
//            if(mapObject.attachedMapObjectManager != null && mapObject == mapObject.attachedMapObjectManager.cookieCutter)
//                this.i = layerChild.map.getAndIncrementId();
//        }

        this.x = layerChild.getX();
        this.y = layerChild.getY();
    }
}
