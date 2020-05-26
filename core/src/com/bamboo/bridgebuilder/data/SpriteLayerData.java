package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.Layer;
import com.bamboo.bridgebuilder.map.MapSprite;

import java.util.ArrayList;

public class SpriteLayerData extends LayerData
{
    public ArrayList<MapSpriteData> children;
    public SpriteLayerData(){}
    public SpriteLayerData(Layer layer)
    {
        super(layer);
        this.children = new ArrayList<>();
        for(int i = 0; i < layer.children.size; i ++)
            this.children.add(new MapSpriteData((MapSprite) layer.children.get(i)));
    }
}
