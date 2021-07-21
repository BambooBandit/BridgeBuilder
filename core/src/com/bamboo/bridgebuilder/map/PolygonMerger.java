package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.bamboo.bridgebuilder.commands.MergeMapPolygons;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

public class PolygonMerger
{
    private Map map;
    private Array<MapPolygon> mergedPolygons;

    private FloatArray temp;

    private Array<FloatArray> result;


    public PolygonMerger(Map map)
    {
        this.map = map;
        this.mergedPolygons = new Array<>();
        this.temp = new FloatArray();
        this.result = new Array<>();
    }

    public Array<FloatArray> merge(Array<MapObject> polygons)
    {
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
                prepMergePolygon((MapPolygon) polygons.get(k));
            }
        }
        mergeResults();
        return result;
    }

    private void mergeResults()
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
                    mergePolygons(polygon1, polygon2);
                    result.removeIndex(k);
                    mergeResults();
                    return;
                }
            }
        }
        return;
    }

    private void prepMergePolygon(MapPolygon polygon)
    {
        if(mergedPolygons.contains(polygon, true))
            return;

        FloatArray resultArray = null;
        if(result.size > 0)
        {
            for (int i = 0; i < result.size; i++)
            {
                temp.clear();
                temp.addAll(polygon.polygon.getTransformedVertices());
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
            temp.clear();
            temp.addAll(polygon.polygon.getTransformedVertices());
            resultArray.addAll(temp);
        }
        else
        {
            // turn resultArray into a merge of resultArray and temp
            mergePolygons(resultArray, polygon);
        }
    }

    private void mergePolygons(FloatArray resultArray, MapPolygon polygon)
    {
        temp.clear();
        temp.addAll(polygon.polygon.getTransformedVertices());
        mergePolygons(resultArray, temp);

        mergedPolygons.add(polygon);
    }

    private void mergePolygons(FloatArray resultArray, FloatArray polygon)
    {
        // create polygons
        Coordinate[] pc1 = new Coordinate[(resultArray.size / 2) + 1];
        for(int i = 0; i < pc1.length - 1; i ++)
            pc1[i] = new Coordinate(resultArray.get(i * 2), resultArray.get((i * 2) + 1));
        pc1[pc1.length - 1] = new Coordinate(resultArray.get(0), resultArray.get(1));
        Coordinate[] pc2 = new Coordinate[(polygon.size / 2) + 1];
        for(int i = 0; i < pc2.length - 1; i ++)
            pc2[i] = new Coordinate(polygon.get(i * 2), polygon.get((i * 2) + 1));
        pc2[pc2.length - 1] = new Coordinate(polygon.get(0), polygon.get(1));
        Polygon p1 = new GeometryFactory().createPolygon(pc1);
        Polygon p2 = new GeometryFactory().createPolygon(pc2);
        // calculate union
        Geometry union = p1.union(p2);

        Coordinate[] unionCoords = union.getCoordinates();
        resultArray.clear();
        for(int i = 0; i < unionCoords.length - 1; i ++)
        {
            resultArray.add((float) unionCoords[i].x);
            resultArray.add((float) unionCoords[i].y);
        }
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
