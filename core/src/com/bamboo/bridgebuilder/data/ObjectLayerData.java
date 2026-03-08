package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.MapObject;
import com.bamboo.bridgebuilder.map.MapPoint;
import com.bamboo.bridgebuilder.map.MapPolygon;
import com.bamboo.bridgebuilder.map.ObjectLayer;

import java.util.ArrayList;

public class ObjectLayerData extends LayerData
{
    public ArrayList<MapObjectData> children;
    public ArrayList<CellData> grid;
    public ObjectLayerData(){}
    public ObjectLayerData(ObjectLayer layer, MapData mapData)
    {
        super(layer);
        if(layer.children.size > 0)
            this.children = new ArrayList<>();

        for(int i = 0; i < layer.children.size; i ++)
        {
            MapObject mapObject = layer.children.get(i);
            if(mapObject instanceof MapPoint)
                this.children.add(new MapPointData((MapPoint) mapObject, 0, 0));
            else
                this.children.add(new MapPolygonData((MapPolygon) mapObject, 0, 0));
        }
        ObjectLayer objectLayer = layer;
        if(objectLayer.spriteGrid != null)
        {
            if(objectLayer.spriteGrid.grid.size > 0)
                this.grid = new ArrayList<>(objectLayer.spriteGrid.grid.size);


            for(int i = 0; i < objectLayer.spriteGrid.grid.size; i ++)
                this.grid.add(new CellData(objectLayer.spriteGrid.grid.get(i), mapData));

            CellData lastCell = null;
            int write = 0;
            for (int read = 0; read < grid.size(); read++)
            {
                CellData cell = grid.get(read);
                if (cell.skip == 0 && cell.t == -1 && cell.c == 0 && ((cell.f == 0 && !mapData.footstepHeavy) || (cell.f == 1 && mapData.footstepHeavy)))
                {
                    if (lastCell == null)
                    {
                        lastCell = cell;
                        grid.set(write++, cell);
                    }
                    else
                        lastCell.skip++;
                }
                else
                {
                    lastCell = cell;
                    grid.set(write++, cell);
                }
            }

            grid.subList(write, grid.size()).clear();
        }
    }
}
