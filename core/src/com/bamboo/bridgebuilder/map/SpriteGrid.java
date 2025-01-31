package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ScreenUtils;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.ui.BBShapeRenderer;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.ColorPropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.FieldFieldPropertyValuePropertyField;

import static com.badlogic.gdx.graphics.GL20.*;

/** Creates a grid of information based on sprites on whichever floor this represents. Information such as average sprite color in cells, and what material you are walking on. */
public class SpriteGrid
{
    public ObjectLayer objectLayer;
    public Array<ObjectLayer> objectLayers;
    public Array<SpriteLayer> spriteLayers;
    public Array<SpriteCell> grid; // Going from bottom left to top right. Every x amount of indices is x width of the layer and height of 1. False is not blocked, true is blocked.

    private FrameBuffer fbo;
    
    public SpriteGrid(ObjectLayer objectLayer)
    {
        this.objectLayer = objectLayer;

        this.objectLayers = new Array<>();
        this.spriteLayers = new Array<>();

        this.grid = new Array<>(this.objectLayer.width * this.objectLayer.height);

        this.grid.clear();
        int newSize = this.objectLayer.width * this.objectLayer.height;
        if(this.grid.size >= newSize)
            this.grid.removeRange(newSize, this.grid.size - 1);
        for(int i = this.grid.size; i < newSize; i ++)
            this.grid.add(new SpriteCell());

        this.fbo = new FrameBuffer(Pixmap.Format.RGBA8888, this.objectLayer.width, this.objectLayer.height, false);
    }

    public void drawColor()
    {
        this.objectLayer.map.editor.shapeRenderer.set(BBShapeRenderer.ShapeType.Filled);
        for(int i = 0; i < this.grid.size; i ++)
        {
            SpriteCell cell = this.grid.get(i);
            this.objectLayer.map.editor.shapeRenderer.setColor(cell.r, cell.g, cell.b, cell.a);
            int x = (int) Math.floor(i % this.objectLayer.width);
            int y = (int) Math.floor(i / this.objectLayer.width);
            this.objectLayer.map.editor.shapeRenderer.rect(x - objectLayer.map.cameraX, y - objectLayer.map.cameraY, 1, 1);
        }
    }

    public void drawTypes()
    {
        this.objectLayer.map.editor.batch.setProjectionMatrix(this.objectLayer.map.editor.stage.getCamera().combined);

        for(int i = 0; i < this.grid.size; i ++)
        {
            int x = (int) Math.floor(i % this.objectLayer.width);
            int y = (int) Math.floor(i / this.objectLayer.width);
            SpriteCell spriteCell = this.grid.get(i);
            if(spriteCell.dustType != null)
            {
                Vector3 project = Utils.project(objectLayer.map.camera, x + .5f - objectLayer.map.cameraX, y + .5f - objectLayer.map.cameraY);
                Utils.centerPrint(objectLayer.map.editor.batch, spriteCell.dustType, project.x, project.y);
            }
        }

        this.objectLayer.map.editor.batch.setProjectionMatrix(this.objectLayer.map.camera.combined);
    }

    public void update()
    {
        // reset
        for(int i = 0; i < this.grid.size; i ++)
        {
            SpriteCell cell = this.grid.get(i);
            cell.dustType = null;
            cell.dustIndex = -1;
            cell.r = 0;
            cell.g = 0;
            cell.b = 0;
            cell.a = 1;
            cell.spriteGrid = this;
        }

        updateColorGrid();

        int index = 0;
        for(int k = 0; k < spriteLayers.size; k ++)
        {
            for (int i = 0; i < spriteLayers.get(k).children.size; i++)
            {
                MapSprite mapSprite = spriteLayers.get(k).children.get(i);
                if (Utils.containsProperty(mapSprite.instanceSpecificProperties, "dustType") || Utils.containsProperty(mapSprite.tool.properties, "dustType"))
                {
                    checkAllCellsInPolygonBox(mapSprite, index);
                    index ++;
                }
            }
        }
        for(int k = 0; k < objectLayers.size; k ++)
        {
            for (int i = 0; i < objectLayers.get(k).children.size; i++)
            {
                MapObject mapObject = objectLayers.get(k).children.get(i);
                if (mapObject instanceof MapPolygon)
                {
                    MapPolygon mapPolygon = (MapPolygon) mapObject;
                    checkAllCellsInPolygonBox(mapPolygon, index);
                    index ++;
                }
            }
        }

        // Check attached bodies in all sprite layers in the same floor, as well as other object layers in the same floor
        float currentFloor = objectLayer.z;
        float iterationFloor = currentFloor - 1;
        for(int i = 0; i < this.objectLayer.map.layers.size; i ++)
        {
            Layer layer = this.objectLayer.map.layers.get(i);
            if(layer instanceof ObjectLayer)
            {
                iterationFloor = layer.z;
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
                        checkAllCellsInPolygonBox(mapPolygon, i);
                    }
                }
            }
            else if(layer instanceof SpriteLayer)
            {
                iterationFloor = layer.z;
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
                                    checkAllCellsInPolygonBox(mapPolygon, i);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static float[] rectangle = new float[8];
    private static float[] triangle = new float[6];
    private static Polygon polygon1 = new Polygon();
    private static Polygon polygon2 = new Polygon();
    public void checkAllCellsInPolygonBox(MapPolygon mapPolygon, int index)
    {
        float oldPolygonX = mapPolygon.polygon.getX();
        float oldPolygonY = mapPolygon.polygon.getY();
        mapPolygon.polygon.setPosition(oldPolygonX + mapPolygon.map.cameraX, oldPolygonY + mapPolygon.map.cameraY);

        Rectangle polygonRectangle = mapPolygon.polygon.getBoundingRectangle();

        boolean dT = false;

        FieldFieldPropertyValuePropertyField property = (FieldFieldPropertyValuePropertyField) Utils.getPropertyField(mapPolygon.properties, "dustType");
        String comparedDustType = null;
        if(property != null)
        {
            comparedDustType = property.value.getText();
            dT = true;
        }

        float shiftSize = 0f;
        int rectX = (int) ((int) Math.floor(polygonRectangle.x) - 1 - shiftSize);
        int rectY = (int) ((int) Math.floor(polygonRectangle.y) - 1 - shiftSize);
        int rectWidth = (int) ((int) Math.ceil(polygonRectangle.width) + ((1 + shiftSize) * 2));
        int rectHeight = (int) ((int) Math.ceil(polygonRectangle.height) + ((1 + shiftSize) * 2));

        for(int y = rectY; y < rectY + rectHeight; y++)
        {
            for(int x = rectX; x < rectX + rectWidth; x ++)
            {
                SpriteCell cell = getCell(x, y);
                if(cell == null)
                    continue;

                if(dT)
                {
                    if (!doesDustTypeHavePriority(cell.dustType, cell.dustIndex, comparedDustType, index))
                        continue;
                }

                float bezelSize = 0f;
                rectangle[0] = x + bezelSize;
                rectangle[1] = y + bezelSize;
                rectangle[2] = x + 1f - (bezelSize * 2f);
                rectangle[3] = y + bezelSize;
                rectangle[4] = x + 1f - (bezelSize * 2f);
                rectangle[5] = y + 1f - (bezelSize * 2f);
                rectangle[6] = x + bezelSize;
                rectangle[7] = y + 1f - (bezelSize * 2f);

                polygon1.setVertices(rectangle);
                FloatArray triangles = Utils.triangleFan(mapPolygon.polygon.getTransformedVertices());

                if(dT)
                {
                    boolean intersects = false;

                    triangles:
                    for(int i = 0; i < triangles.size; i += 6)
                    {
                        triangle[0] = triangles.get(i);
                        triangle[1] = triangles.get(i + 1);
                        triangle[2] = triangles.get(i + 2);
                        triangle[3] = triangles.get(i + 3);
                        triangle[4] = triangles.get(i + 4);
                        triangle[5] = triangles.get(i + 5);
                        polygon2.setVertices(triangle);
                        if(Intersector.overlapConvexPolygons(polygon1, polygon2))
                        {
                            intersects = true;
                            break triangles;
                        }
                    }

                    if(intersects)
                    {
                        cell.dustIndex = index;
                        if(cell.a > 0)
                            cell.dustType = comparedDustType;
                    }
                }
            }
        }
        mapPolygon.polygon.setPosition(oldPolygonX, oldPolygonY);
    }

    public void checkAllCellsInPolygonBox(MapSprite mapSprite, int index)
    {
        float oldPolygonX = mapSprite.polygon.getX();
        float oldPolygonY = mapSprite.polygon.getY();
//        mapSprite.polygon.setPosition(oldPolygonX + mapSprite.map.cameraX, oldPolygonY + mapSprite.map.cameraY);

        Rectangle polygonRectangle = mapSprite.polygon.getBoundingRectangle();

        boolean dT = false;

        FieldFieldPropertyValuePropertyField property = (FieldFieldPropertyValuePropertyField) Utils.getPropertyField(mapSprite, "dustType");
        String comparedDustType = null;
        ColorPropertyField colorProperty = Utils.getLockedColorField("Tint", mapSprite.lockedProperties);
        float alpha = Float.parseFloat(colorProperty.aValue.getText());
        if(property != null && mapSprite.layer != null && !Utils.containsProperty(mapSprite.layer.properties, "ignoreDustType") && alpha > .2f)
        {
            comparedDustType = property.value.getText();
            dT = true;
        }

        float shiftSize = 0f;
        int rectX = (int) ((int) Math.floor(polygonRectangle.x) - 1 - shiftSize);
        int rectY = (int) ((int) Math.floor(polygonRectangle.y) - 1 - shiftSize);
        int rectWidth = (int) ((int) Math.ceil(polygonRectangle.width) + ((1 + shiftSize) * 2));
        int rectHeight = (int) ((int) Math.ceil(polygonRectangle.height) + ((1 + shiftSize) * 2));

        for(int y = rectY; y < rectY + rectHeight; y++)
        {
            for(int x = rectX; x < rectX + rectWidth; x ++)
            {
                SpriteCell cell = getCell(x, y);
                if(cell == null)
                    continue;

                if(dT)
                {
//                    if (!doesDustTypeHavePriority(cell.dustType, cell.dustIndex, comparedDustType, index))
//                        continue;
                }

                float bezelSize = 0f;
                rectangle[0] = x + bezelSize;
                rectangle[1] = y + bezelSize;
                rectangle[2] = x + 1f - (bezelSize * 2f);
                rectangle[3] = y + bezelSize;
                rectangle[4] = x + 1f - (bezelSize * 2f);
                rectangle[5] = y + 1f - (bezelSize * 2f);
                rectangle[6] = x + bezelSize;
                rectangle[7] = y + 1f - (bezelSize * 2f);

                polygon1.setVertices(rectangle);
                polygon2.setVertices(mapSprite.polygon.getTransformedVertices());
//                FloatArray triangles = Utils.triangleFan(mapSprite.polygon.getTransformedVertices());

                if(dT && Intersector.overlapConvexPolygons(polygon1, polygon2))
                {
                    cell.dustIndex = index;
                    if(cell.a > 0)
                        cell.dustType = comparedDustType;
                }
            }
        }
//        mapSprite.polygon.setPosition(oldPolygonX, oldPolygonY);
    }

    private static Color rgba8888ToColor = new Color();
    private void updateColorGrid()
    {
        if(objectLayer.map.groupPolygons != null)
        {
            for (int i = 0; i < objectLayer.map.groupPolygons.children.size; i++)
            {
                MapPolygon mapPolygon = (MapPolygon) objectLayer.map.groupPolygons.children.get(i);
                if (mapPolygon.mapSprites != null)
                {
                    for (int k = 0; k < mapPolygon.mapSprites.size; k++)
                    {
                        MapSprite mapSprite = mapPolygon.mapSprites.get(k);
                        ColorPropertyField colorProperty = Utils.getLockedColorField("Tint", mapSprite.lockedProperties);
                        mapSprite.setColor(colorProperty.getR(), colorProperty.getG(), colorProperty.getB(), colorProperty.getA());
                    }
                }
            }
        }

        // Render all sprite layers of the same floor to an fbo
        float currentFloor = objectLayer.z;
        float iterationFloor = currentFloor - 1;

        this.fbo.bind();
        this.fbo.begin();

        this.objectLayer.map.editor.batch.setBlendFunction(-1, -1);
        Gdx.gl20.glBlendFuncSeparate(Gdx.gl.GL_SRC_ALPHA,Gdx.gl.GL_ONE_MINUS_SRC_ALPHA, Gdx.gl.GL_ONE,Gdx.gl.GL_ONE);

        // Prepare camera to handle fbo
        float oldCamX = this.objectLayer.map.cameraX;
        float oldCamY = this.objectLayer.map.cameraY;
        this.objectLayer.map.camera.viewportWidth = this.objectLayer.width / this.objectLayer.map.camera.zoom;
        this.objectLayer.map.camera.viewportHeight = this.objectLayer.height / this.objectLayer.map.camera.zoom;
        this.objectLayer.map.cameraX = this.objectLayer.map.camera.viewportWidth * this.objectLayer.map.camera.zoom / 2f;
        this.objectLayer.map.cameraY = this.objectLayer.map.camera.viewportHeight * this.objectLayer.map.camera.zoom / 2f;
        this.objectLayer.map.camera.update();

        float bgAlpha = 1;
        if(Utils.containsProperty(this.objectLayer.map.propertyMenu.mapPropertyPanel.properties, "suspended"))
            bgAlpha = 0;
        Gdx.gl.glClearColor(this.objectLayer.map.r, this.objectLayer.map.g, this.objectLayer.map.b, bgAlpha);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);

        this.objectLayer.map.editor.batch.begin();
        for(int i = 0; i < this.objectLayer.map.layers.size; i ++)
            this.objectLayer.map.layers.get(i).update();
        for(int i = 0; i < this.objectLayer.map.layers.size; i ++)
        {
            Layer layer = this.objectLayer.map.layers.get(i);
            if(layer instanceof ObjectLayer)
            {
                iterationFloor = layer.z;
            }
            else if(layer instanceof SpriteLayer)
            {
                iterationFloor = layer.z;
                SpriteLayer spriteLayer = (SpriteLayer) layer;
                if(iterationFloor != currentFloor || Utils.getPropertyField(spriteLayer.properties, "ground") == null)
                    continue;
                spriteLayer.draw();
            }
        }
        this.objectLayer.map.editor.batch.end();

        Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, this.objectLayer.width, this.objectLayer.height);

        this.fbo.end();
        FrameBuffer.unbind();

        this.fbo.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // Reset camera after drawing to fbo
        this.objectLayer.map.cameraX = oldCamX;
        this.objectLayer.map.cameraY = oldCamY;
        this.objectLayer.map.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        this.objectLayer.map.editor.batch.setBlendFunction(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl20.glBlendFuncSeparate(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA,GL20.GL_ONE, GL20.GL_DST_ALPHA);

        // After rendering to fbo and retrieving pixmap, find average color of every cell.
        for(int i = 0; i < this.grid.size; i ++)
        {
            int x = (int) Math.floor(i % this.objectLayer.width);
            int y = (int) Math.floor(i / this.objectLayer.width);
            int rgba8888 = pixmap.getPixel((x), (y));
            Color.rgba8888ToColor(rgba8888ToColor, rgba8888);
            SpriteCell cell = this.grid.get(i);
            cell.r = rgba8888ToColor.r;
            cell.g = rgba8888ToColor.g;
            cell.b = rgba8888ToColor.b;
            cell.a = rgba8888ToColor.a;
            if(cell.a == 0)
                cell.dustType = null;
            cell.spriteGrid = this;
        }

        objectLayer.map.colorizeGroup();
    }

    public SpriteCell getCell(int x, int y)
    {
        if(x < 0 || y < 0)
            return null;
        if(x >= objectLayer.width || y >= objectLayer.height)
            return null;

        int index = (int) ((Math.ceil(x) + ((Math.floor(y) * Math.ceil(objectLayer.width)))));

        if(x == 0 && y == 0)
            index = 0;

        return grid.get(index);
    }

    public void resizeGrid()
    {
        this.grid.clear();
        int newSize = this.objectLayer.width * this.objectLayer.height;
        if(this.grid.size >= newSize)
            this.grid.removeRange(newSize, this.grid.size - 1);
        for(int i = this.grid.size; i < newSize; i ++)
            this.grid.add(new SpriteCell());

        this.fbo = new FrameBuffer(Pixmap.Format.RGBA8888, this.objectLayer.width, this.objectLayer.height, false);

        update();
    }

    public void clear()
    {
        this.grid.clear();
    }

    public class SpriteCell
    {
        public int dustIndex = -1;
        public String dustType = null;
        public float r, g, b, a;
        public SpriteGrid spriteGrid;
    }

    private boolean doesDustTypeHavePriority(String dustType, int layerIndex, String comparingDustType, int comparingLayerIndex)
    {
        if(dustType == null && comparingDustType == null)
            return false;
        if(dustType == null)
            return true;
        if(comparingDustType == null)
            return false;
        if(comparingLayerIndex > layerIndex)
            return true;
        if(comparingLayerIndex < layerIndex)
            return false;
        return getDustTypePriority(dustType) < getDustTypePriority(comparingDustType);
    }

    private int getDustTypePriority(String dustType)
    {
        if(dustType == null)
            return -1;
        switch (dustType)
        {
            case "dirt": return 0;
            case "sand": return 1;
            case "gravel": return 2;
            case "stone": return 3;
            case "grass": return 4;
            case "stick": return 5;
            case "leaves": return 6;
            case "wood": return 7;
            case "puddle": return 8;
            default: return -1;
        }
    }
}
