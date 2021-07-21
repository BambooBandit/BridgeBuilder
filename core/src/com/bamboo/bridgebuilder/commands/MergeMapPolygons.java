package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapPolygon;
import com.bamboo.bridgebuilder.map.ObjectLayer;

public class MergeMapPolygons implements Command
{
    private Map map;
    private ObjectLayer selectedObjectLayer;
    public Array<MapPolygon> mapPolygons;
    public Array<MapPolygon> oldSelectedMapPolygons;

    public MergeMapPolygons(Map map, ObjectLayer selectedObjectLayer, Array<MapPolygon> mapPolygons)
    {
        this.map = map;
        this.selectedObjectLayer = selectedObjectLayer;
        this.mapPolygons = new Array(mapPolygons);
        this.oldSelectedMapPolygons = new Array(map.selectedObjects);
    }

    @Override
    public void execute()
    {
        for (int i = 0; i < this.map.selectedObjects.size; i++)
        {
            this.map.selectedObjects.get(i).unselect();
            i--;
        }

        for(int i = 0; i < mapPolygons.size; i ++)
        {
            MapPolygon mapPolygon = mapPolygons.get(i);
            this.selectedObjectLayer.addMapObject(mapPolygon);
            mapPolygon.select();
        }
        this.map.editor.selectedCountTooltip.label.setText((map.selectedObjects.size + map.selectedSprites.size) + " selected");
        this.selectedObjectLayer.children.sort();
    }

    @Override
    public void undo()
    {
        for(int i = 0; i < mapPolygons.size; i ++)
        {
            MapPolygon mapPolygon = mapPolygons.get(i);
            mapPolygon.unselect();
            this.selectedObjectLayer.children.removeValue(mapPolygon, true);
        }
        for(int i = 0; i < this.oldSelectedMapPolygons.size; i ++)
            this.oldSelectedMapPolygons.get(i).select();
        this.map.editor.selectedCountTooltip.label.setText((map.selectedObjects.size + map.selectedSprites.size) + " selected");

    }
}
