package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.BooleanArray;

public class Graph
{
    private ObjectLayer objectLayer;
    private BooleanArray graph; // Going from bottom left to top right. Every x amount of indices is x width of the layer and height of 1. False is not blocked, true is blocked.

    public Graph(ObjectLayer objectLayer)
    {
        this.objectLayer = objectLayer;

        this.graph = new BooleanArray(this.objectLayer.width * this.objectLayer.height);
        this.resizeGrid();
    }

    public void drawBlocked()
    {
        objectLayer.map.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        objectLayer.map.editor.shapeRenderer.setColor(1, 0, 0, .35f);
        for(int i = 0; i < this.graph.size; i ++)
        {
            if(this.graph.get(i))
            {
                int x = (int) Math.floor(i % this.objectLayer.width);
                int y = (int) Math.floor(i / this.objectLayer.width);
                objectLayer.map.editor.shapeRenderer.rect(x, y, 1, 1);
            }
        }
    }

    public void update()
    {
        for(int i = 0; i < this.graph.size; i ++)
        {
            int x = (int) Math.floor(i % this.objectLayer.width);
            int y = (int) Math.floor(i / this.objectLayer.width);
            this.graph.set(i, checkCellForBlockedPolygons(x, y));
        }
    }

    private static float[] rectangle = new float[8];
    public boolean checkCellForBlockedPolygons(int x, int y)
    {
        float bezelSize = .1f;
        rectangle[0] = x + bezelSize;
        rectangle[1] = y + bezelSize;
        rectangle[2] = x + 1f - (bezelSize * 2f);
        rectangle[3] = y + bezelSize;
        rectangle[4] = x + 1f - (bezelSize * 2f);
        rectangle[5] = y + 1f - (bezelSize * 2f);
        rectangle[6] = x + bezelSize;
        rectangle[7] = y + 1f - (bezelSize * 2f);

        for(int i = 0; i < this.objectLayer.children.size; i ++)
        {
            MapObject mapObject = this.objectLayer.children.get(i);
            if(mapObject instanceof MapPolygon)
            {
                MapPolygon mapPolygon = (MapPolygon) mapObject;
                if(mapPolygon.body != null)
                {
                    if(Intersector.overlapConvexPolygons(rectangle, mapPolygon.polygon.getTransformedVertices(), null))
                        return true;
                }
            }
        }
        return false;
    }

    public void resizeGrid()
    {
        this.graph.clear();
        int newSize = this.objectLayer.width * this.objectLayer.height;
        for(int i = 0; i < newSize; i ++)
            this.graph.add(false);
        update();
    }

    public void clear()
    {
        this.graph.clear();
    }
}
