package com.bamboo.bridgebuilder.commands;

import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapPoint;
import com.bamboo.bridgebuilder.map.MapSprite;
import com.bamboo.bridgebuilder.map.ObjectLayer;

public class DrawMapPoint implements Command
{
    private Map map;
    private ObjectLayer selectedObjectLayer;
    private MapSprite selectedMapSprite;
    private MapPoint mapPoint = null;
    private float x;
    private float y;

    public DrawMapPoint(Map map, ObjectLayer selectedObjectLayer, float x, float y)
    {
        this.map = map;
        this.selectedObjectLayer = selectedObjectLayer;
        this.x = x;
        this.y = y;
    }

    public DrawMapPoint(Map map, MapSprite selectedMapSprite, float x, float y)
    {
        this.map = map;
        this.selectedMapSprite = selectedMapSprite;
        this.x = x;
        this.y = y;
    }

    @Override
    public void execute()
    {
        if(this.selectedObjectLayer != null)
        {
            if (this.mapPoint == null)
                this.mapPoint = new MapPoint(this.map, this.selectedObjectLayer, this.x, this.y);
            this.selectedObjectLayer.addMapObject(this.mapPoint);
        }
        else
        {
            if (this.mapPoint == null)
                this.mapPoint = new MapPoint(this.map, this.selectedMapSprite, this.x, this.y);
            this.selectedMapSprite.createAttachedMapObject(this.map, this.mapPoint);
        }
    }

    @Override
    public void undo()
    {
        if(this.selectedObjectLayer != null)
            this.selectedObjectLayer.children.removeValue(this.mapPoint, true);
        else
            this.selectedMapSprite.removeAttachedMapObject(this.mapPoint);
    }
}
