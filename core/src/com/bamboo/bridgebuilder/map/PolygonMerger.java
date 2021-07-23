package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.bamboo.bridgebuilder.commands.MergeMapPolygons;
import com.bamboo.bridgebuilder.ui.FailedToMergeDialog;
import org.locationtech.jts.geom.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class PolygonMerger
{
    private Map map;
    private Array<MapPolygon> mergedPolygons;

    private FloatArray temp;

    public Array<FloatArray> result;

    public MapPolygon failedToMergePolygon;
    public FloatArray failedPolygon1;
    public FloatArray failedPolygon2;

    private float scale = 1;

    DecimalFormat df;

    public PolygonMerger(Map map)
    {
        this.map = map;
        this.mergedPolygons = new Array<>();
        this.temp = new FloatArray();
        this.result = new Array<>();

        this.failedPolygon1 = new FloatArray();
        this.failedPolygon2 = new FloatArray();

        df = new DecimalFormat("#.#######");
        df.setRoundingMode(RoundingMode.CEILING);
    }

    private void setTemp(MapPolygon polygon)
    {
        temp.clear();
        temp.addAll(polygon.polygon.getTransformedVertices());
        for(int i = 0; i < temp.size; i ++)
            temp.set(i, temp.get(i) * scale);
    }

    public Array<FloatArray> merge(Array<MapObject> polygons)
    {
        failedToMergePolygon = null;
        failedPolygon1.clear();
        failedPolygon2.clear();
        result.clear();
        mergedPolygons.clear();

        for(int i = 0; i < polygons.size; i ++)
            if(!(polygons.get(i) instanceof MapPolygon))
                return result;

        for(int i = 0; i < polygons.size; i ++)
        {
            for (int k = 0; k < polygons.size; k++)
            {
                if(i == k)
                    continue;
                if(!prepMergePolygon((MapPolygon) polygons.get(k)))
                {
                    FailedToMergeDialog failedToMergeDialog = new FailedToMergeDialog(map.stage, map.skin, map, map.polygonMerger);
                    map.polygonMerger.result.clear();
                    return result;
                }
            }
        }
        mergeResults();
        return result;
    }

    private boolean mergeResults()
    {
        for(int i = 0; i < result.size; i ++)
        {
            for(int k = 0; k < result.size; k ++)
            {
                if(i == k)
                    continue;
                FloatArray polygon1 = result.get(i);
                FloatArray polygon2 = result.get(k);
                if(Intersector.intersectPolygons(polygon1, polygon2))
                {
                    boolean mergedSuccessfully = mergePolygons(polygon1, polygon2);
                    if(!mergedSuccessfully)
                    {
                        FailedToMergeDialog failedToMergeDialog = new FailedToMergeDialog(map.stage, map.skin, map, map.polygonMerger);
                        map.polygonMerger.result.clear();
                        return false;
                    }

                    result.removeIndex(k);
                    mergeResults();
                    return mergedSuccessfully;
                }
            }
        }
        return true;
    }

    private boolean prepMergePolygon(MapPolygon polygon)
    {
        if(mergedPolygons.contains(polygon, true))
            return true;

        FloatArray resultArray = null;
        if(result.size > 0)
        {
            for (int i = 0; i < result.size; i++)
            {
                setTemp(polygon);
                if(result.get(i).size > 0 && Intersector.intersectPolygons(result.get(i), temp))
                {
                    resultArray = result.get(i);
                    break;
                }
            }
        }
        if(resultArray == null)
        {
            resultArray = new FloatArray();
            result.add(resultArray);
        }

        if(resultArray.size == 0)
        {
            mergedPolygons.add(polygon);
            setTemp(polygon);
            resultArray.addAll(temp);
        }
        else
        {
            // turn resultArray into a merge of resultArray and temp
            return mergePolygons(resultArray, polygon);
        }
        return true;
    }

    private boolean mergePolygons(FloatArray resultArray, MapPolygon polygon)
    {
        setTemp(polygon);

        mergedPolygons.add(polygon);

        boolean mergedSuccessfully = mergePolygons(resultArray, temp);
        if(!mergedSuccessfully)
            failedToMergePolygon = polygon;
        return mergedSuccessfully;
    }

    private boolean mergePolygons(FloatArray resultArray, FloatArray polygon)
    {
        // create polygons
        Coordinate[] pc1 = new Coordinate[(resultArray.size / 2) + 1];
        for(int i = 0; i < pc1.length - 1; i ++)
            pc1[i] = new Coordinate(Float.parseFloat(df.format(resultArray.get(i * 2))), Float.parseFloat(df.format(resultArray.get((i * 2) + 1))));
        pc1[pc1.length - 1] = new Coordinate(Float.parseFloat(df.format(resultArray.get(0))), Float.parseFloat(df.format(resultArray.get(1))));
        Coordinate[] pc2 = new Coordinate[(polygon.size / 2) + 1];
        for(int i = 0; i < pc2.length - 1; i ++)
            pc2[i] = new Coordinate(Float.parseFloat(df.format(polygon.get(i * 2))), Float.parseFloat(df.format(polygon.get((i * 2) + 1))));
        pc2[pc2.length - 1] = new Coordinate(Float.parseFloat(df.format(polygon.get(0))), Float.parseFloat(df.format(polygon.get(1))));
        Polygon p1 = new GeometryFactory().createPolygon(pc1);
        Polygon p2 = new GeometryFactory().createPolygon(pc2);
        // calculate union
        Geometry union;
        try
        {
            union = p1.union(p2);
        }catch (TopologyException e)
        {
            e.printStackTrace();
            failedPolygon1.addAll(resultArray);
            failedPolygon2.addAll(polygon);
            for(int i = 0; i < failedPolygon1.size; i ++)
                failedPolygon1.set(i, failedPolygon1.get(i) / scale);
            for(int i = 0; i < failedPolygon2.size; i ++)
                failedPolygon2.set(i, failedPolygon2.get(i) / scale);
            return false;
        }

        Coordinate[] unionCoords = union.getCoordinates();
        resultArray.clear();
        for(int i = 0; i < unionCoords.length - 1; i ++)
        {
            resultArray.add((float) unionCoords[i].x);
            resultArray.add((float) unionCoords[i].y);
        }
        return true;
    }

    public Array<MapPolygon> convertToMapPolygons(Array<FloatArray> polygonVertices)
    {
        if(polygonVertices == null || polygonVertices.size == 0)
            return null;

        for (int i = 0; i < this.map.selectedObjects.size; i++)
        {
            this.map.selectedObjects.get(i).unselect();
            i--;
        }

        Array<MapPolygon> mapPolygons = new Array<>();
        ObjectLayer selectedObjectLayer = (ObjectLayer) map.selectedLayer;
        for(int i = 0; i < polygonVertices.size; i ++)
        {
            FloatArray vertices = polygonVertices.get(i);
            MapPolygon mapPolygon = new MapPolygon(map, selectedObjectLayer, vertices.toArray(), map.cameraX, map.cameraY);
            mapPolygons.add(mapPolygon);
        }
        MergeMapPolygons mergeMapPolygons = new MergeMapPolygons(map, selectedObjectLayer, mapPolygons);
        map.executeCommand(mergeMapPolygons);


        return mapPolygons;
    }
}
