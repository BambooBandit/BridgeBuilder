package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.BooleanArray;

public class BlockedGrid
{
    private ObjectLayer objectLayer;
    private BooleanArray grid; // Going from bottom left to top right. Every x amount of indices is x width of the layer and height of 1. False is not blocked, true is blocked.

    public BlockedGrid(ObjectLayer objectLayer)
    {
        this.objectLayer = objectLayer;

        this.grid = new BooleanArray(this.objectLayer.width * this.objectLayer.height);
        this.resizeGrid();
    }

    public void drawBlocked()
    {
        objectLayer.map.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        objectLayer.map.editor.shapeRenderer.setColor(1, 0, 0, .35f);
        for(int i = 0; i < this.grid.size; i ++)
        {
            if(this.grid.get(i))
            {
                int x = (int) Math.floor(i % this.objectLayer.width);
                int y = (int) Math.floor(i / this.objectLayer.width);
                objectLayer.map.editor.shapeRenderer.rect(x, y, 1, 1);
            }
        }
    }

    public void update()
    {
        for(int i = 0; i < this.grid.size; i ++)
        {
            int x = (int) Math.floor(i % this.objectLayer.width);
            int y = (int) Math.floor(i / this.objectLayer.width);
            this.grid.set(i, checkCellForBlockedPolygons(x, y));
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

        // Check bodies in this object layer
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

        // Check attached bodies in all sprite layers in the same floor, as well as other object layers in the same floor
        int currentFloor = Integer.parseInt(this.objectLayer.layerField.layerName.getText().substring(6));
        int iterationFloor = -1;
        for(int i = 0; i < this.objectLayer.map.layers.size; i ++)
        {
            Layer layer = this.objectLayer.map.layers.get(i);
            if(layer instanceof ObjectLayer)
            {
                ObjectLayer objectLayer = (ObjectLayer) layer;
                String name = objectLayer.layerField.layerName.getText();
                if(name.startsWith("floor ") && Character.isDigit(name.charAt(name.length() - 1)))
                {
                    iterationFloor = Integer.parseInt(name.substring(6));
                    continue;
                }
            }
            if(layer instanceof ObjectLayer)
            {
                ObjectLayer objectLayer = (ObjectLayer) layer;
                for(int k = 0; k < objectLayer.children.size; k ++)
                {
                    MapObject mapObject = objectLayer.children.get(k);
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
            }
            else if(layer instanceof SpriteLayer)
            {
                SpriteLayer spriteLayer = (SpriteLayer) layer;
                if(iterationFloor == currentFloor)
                {
                    for(int k = 0; k < spriteLayer.children.size; k ++)
                    {
                        MapSprite mapSprite = spriteLayer.children.get(k);
                        if(mapSprite.attachedMapObjects != null)
                        {
                            for(int s = 0; s < mapSprite.attachedMapObjects.size; s ++)
                            {
                                MapObject mapObject = mapSprite.attachedMapObjects.get(s);
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
                        }
                    }
                }
            }
        }
        return false;
    }

    public void resizeGrid()
    {
        this.grid.clear();
        int newSize = this.objectLayer.width * this.objectLayer.height;
        for(int i = 0; i < newSize; i ++)
            this.grid.add(false);
        update();
    }

    public void clear()
    {
        this.grid.clear();
    }
}
