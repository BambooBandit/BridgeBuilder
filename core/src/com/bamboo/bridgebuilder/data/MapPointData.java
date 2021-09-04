package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.MapPoint;

import java.util.ArrayList;

public class MapPointData extends MapObjectData
{
    public ArrayList<Long> bId; // to branch mapPoint ids
    public MapPointData(){}
    public MapPointData(MapPoint mapPoint, float offsetX, float offsetY)
    {
        super(mapPoint, offsetX, offsetY);
        if(mapPoint.toBranchPoints != null && mapPoint.toBranchPoints.size > 0)
        {
            this.bId = new ArrayList<>(mapPoint.toBranchPoints.size);
            for(int i = 0; i < mapPoint.toBranchPoints.size; i ++)
                this.bId.add(mapPoint.toBranchPoints.get(i).id);
        }
    }
}
