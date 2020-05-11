package com.bamboo.bridgebuilder.commands;

import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapObject;
import com.bamboo.bridgebuilder.map.MapPolygon;

public class SelectPolygonVertice implements Command
{
    private Map map;

    private MapPolygon oldPolygon;
    private int oldPolygonVertice;
    private MapPolygon newPolygon;
    private int newPolygonVertice;

    public SelectPolygonVertice(Map map)
    {
        this.map = map;

        for(int i = 0; i < this.map.selectedObjects.size; i ++)
        {
            MapObject mapObject = this.map.selectedObjects.get(i);
            if(mapObject instanceof MapPolygon)
            {
                MapPolygon mapPolygon = (MapPolygon) mapObject;
                if(mapPolygon.indexOfSelectedVertice != -1)
                {
                    this.oldPolygon = mapPolygon;
                    this.oldPolygonVertice = mapPolygon.indexOfSelectedVertice;
                }
                if(mapPolygon.indexOfHoveredVertice != -1)
                {
                    this.newPolygon = mapPolygon;
                    this.newPolygonVertice = mapPolygon.indexOfHoveredVertice;
                }
            }
        }
    }

    @Override
    public void execute()
    {
        if(this.oldPolygon != null)
            this.selectVertice(this.oldPolygon, -1);
        if(this.newPolygon != null)
            this.selectVertice(this.newPolygon, this.newPolygonVertice);
        else if(this.oldPolygon != null)
            this.selectVertice(this.oldPolygon, -1);
    }

    @Override
    public void undo()
    {
        if(this.newPolygon != null)
            this.selectVertice(this.newPolygon, -1);
        if(this.oldPolygon != null)
            this.selectVertice(this.oldPolygon, this.oldPolygonVertice);
    }

    private void selectVertice(MapPolygon mapPolygon, int index)
    {
        if(index == -1)
            mapPolygon.moveBox.setPosition(mapPolygon.getX(), mapPolygon.getY());
        else
            mapPolygon.moveBox.setPosition(mapPolygon.polygon.getTransformedVertices()[index], mapPolygon.polygon.getTransformedVertices()[index + 1]);
        mapPolygon.indexOfSelectedVertice = index;
    }
}
