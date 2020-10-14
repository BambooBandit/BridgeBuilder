package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.FieldFieldPropertyValuePropertyField;

import static com.badlogic.gdx.graphics.GL20.*;

/** Creates a grid of information based on sprites on whichever floor this represents. Information such as average sprite color in cells, and what material you are walking on. */
public class SpriteGrid
{
    private ObjectLayer objectLayer;
    public Array<SpriteCell> grid; // Going from bottom left to top right. Every x amount of indices is x width of the layer and height of 1. False is not blocked, true is blocked.

    private FrameBuffer fbo;
    
    public SpriteGrid(ObjectLayer objectLayer)
    {
        this.objectLayer = objectLayer;

        this.grid = new Array<>(this.objectLayer.width * this.objectLayer.height);

        this.grid.clear();
        int newSize = this.objectLayer.width * this.objectLayer.height;
        if(this.grid.size >= newSize)
            this.grid.removeRange(newSize, this.grid.size - 1);
        for(int i = this.grid.size; i < newSize; i ++)
            this.grid.add(new SpriteCell());
        this.fbo = new FrameBuffer(Pixmap.Format.RGBA8888, this.objectLayer.width * 1, this.objectLayer.height * 1, false);
    }

    public void drawBlocked()
    {
        this.objectLayer.map.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        this.objectLayer.map.editor.shapeRenderer.setColor(1, 0, 0, .35f);
        for(int i = 0; i < this.grid.size; i ++)
        {
            if(this.grid.get(i).blocked)
            {
                int x = (int) Math.floor(i % this.objectLayer.width);
                int y = (int) Math.floor(i / this.objectLayer.width);
                this.objectLayer.map.editor.shapeRenderer.rect(x, y, 1, 1);
            }
        }
    }

    public void drawColor()
    {
        this.objectLayer.map.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        for(int i = 0; i < this.grid.size; i ++)
        {
            SpriteCell cell = this.grid.get(i);
            this.objectLayer.map.editor.shapeRenderer.setColor(cell.r, cell.g, cell.b, cell.a);
            int x = (int) Math.floor(i % this.objectLayer.width);
            int y = (int) Math.floor(i / this.objectLayer.width);
            this.objectLayer.map.editor.shapeRenderer.rect(x, y, 1, 1);
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
                Vector3 project = Utils.project(objectLayer.map.camera, x + .5f, y + .5f);
                Utils.centerPrint(objectLayer.map.editor.batch, spriteCell.dustType, project.x, project.y);
            }
        }

        this.objectLayer.map.editor.batch.setProjectionMatrix(this.objectLayer.map.camera.combined);
    }

    public void update()
    {
//        updateColorGrid();

        // reset
        for(int i = 0; i < this.grid.size; i ++)
        {
            SpriteCell cell = this.grid.get(i);
            cell.blocked = false;
            cell.dustType = null;
            cell.dustIndex = -1;
            cell.r = 0;
            cell.g = 0;
            cell.b = 0;
            cell.a = 1;
        }

        for(int i = 0; i < this.objectLayer.children.size; i ++)
        {
            MapObject mapObject = this.objectLayer.children.get(i);
            if(mapObject instanceof MapPolygon)
            {
                MapPolygon mapPolygon = (MapPolygon) mapObject;
                checkAllCellsInPolygonBox(mapPolygon, i);
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
                        checkAllCellsInPolygonBox(mapPolygon, i);
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
                                    checkAllCellsInPolygonBox(mapPolygon, i);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void checkAllCellsInPolygonBox(MapPolygon mapPolygon, int index)
    {
        Rectangle polygonRectangle = mapPolygon.polygon.getBoundingRectangle();

        boolean bl = false;
        boolean dT = false;
        if(mapPolygon.body != null)
            bl = true;

        FieldFieldPropertyValuePropertyField property = (FieldFieldPropertyValuePropertyField) Utils.getPropertyField(mapPolygon.properties, "dustType");
        String comparedDustType = null;
        if(property != null)
        {
            comparedDustType = property.value.getText();
            dT = true;
        }

        int rectX = (int) Math.floor(polygonRectangle.x) - 1;
        int rectY = (int) Math.floor(polygonRectangle.y) - 1;
        int rectWidth = (int) Math.ceil(polygonRectangle.width) + 2;
        int rectHeight = (int) Math.ceil(polygonRectangle.height) + 2;

        for(int y = rectY; y < rectY + rectHeight; y++)
        {
            for(int x = rectX; x < rectX + rectWidth; x ++)
            {
                SpriteCell cell = getCell(x, y);
                if(cell == null)
                    continue;

                if(bl && cell.blocked)
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

                if (Intersector.overlapConvexPolygons(rectangle, mapPolygon.polygon.getTransformedVertices(), null))
                {
                    if(bl)
                        cell.blocked = true;
                    if(dT)
                    {
                        cell.dustIndex = index;
                        cell.dustType = comparedDustType;
                    }
                }
            }
        }
    }

    private static Color rgba8888ToColor = new Color();
    private void updateColorGrid()
    {
        // Render all sprite layers of the same floor to an fbo
        int currentFloor = Integer.parseInt(this.objectLayer.layerField.layerName.getText().substring(6));
        int iterationFloor = -1;

        this.fbo.bind();
        this.fbo.begin();

        this.objectLayer.map.editor.batch.setBlendFunction(-1, -1);
        Gdx.gl20.glBlendFuncSeparate(Gdx.gl.GL_SRC_ALPHA,Gdx.gl.GL_ONE_MINUS_SRC_ALPHA, Gdx.gl.GL_ONE,Gdx.gl.GL_ONE);

        // Prepare camera to handle fbo
        float oldCamX = this.objectLayer.map.camera.position.x;
        float oldCamY = this.objectLayer.map.camera.position.y;
        this.objectLayer.map.camera.viewportWidth = this.objectLayer.width / this.objectLayer.map.camera.zoom;
        this.objectLayer.map.camera.viewportHeight = this.objectLayer.height / this.objectLayer.map.camera.zoom;
        this.objectLayer.map.camera.position.set(this.objectLayer.map.camera.viewportWidth * this.objectLayer.map.camera.zoom / 2f, this.objectLayer.map.camera.viewportHeight * this.objectLayer.map.camera.zoom / 2f, 0);
        this.objectLayer.map.camera.update();

        Gdx.gl.glClearColor(this.objectLayer.map.r, this.objectLayer.map.g, this.objectLayer.map.b, 1);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);

        this.objectLayer.map.editor.batch.begin();
        for(int i = 0; i < this.objectLayer.map.layers.size; i ++)
        {
            Layer layer = this.objectLayer.map.layers.get(i);
            if(layer instanceof ObjectLayer)
            {
                ObjectLayer objectLayer = (ObjectLayer) layer;
                String name = objectLayer.layerField.layerName.getText();
                if(name.startsWith("floor ") && Character.isDigit(name.charAt(name.length() - 1)))
                    iterationFloor = Integer.parseInt(name.substring(6));
            }
            else if(layer instanceof SpriteLayer)
            {
                SpriteLayer spriteLayer = (SpriteLayer) layer;
                if(iterationFloor != currentFloor || Utils.getPropertyField(spriteLayer.properties, "ground") == null)
                    continue;
                spriteLayer.draw();
            }
        }
        this.objectLayer.map.editor.batch.end();

        Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, this.objectLayer.width * 64, this.objectLayer.height * 64);

        this.fbo.end();
        FrameBuffer.unbind();

        this.fbo.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // Reset camera after drawing to fbo
        this.objectLayer.map.camera.position.set(oldCamX, oldCamY, 0);
        this.objectLayer.map.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        this.objectLayer.map.editor.batch.setBlendFunction(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl20.glBlendFuncSeparate(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA,GL20.GL_ONE, GL20.GL_DST_ALPHA);


        // After rendering to fbo and retrieving pixmap, find average color of every cell.
        for(int i = 0; i < this.grid.size; i ++)
        {
            int x = (int) Math.floor(i % this.objectLayer.width);
            int y = (int) Math.floor(i / this.objectLayer.width);
            float r = 0, g = 0, b = 0, a = 0;
            for(int px = 0; px < 64; px ++)
            {
                for(int py = 0; py < 64; py ++)
                {
                    int rgba8888 = pixmap.getPixel((x * 64) + px, (y * 64) + py);
                    Color.rgba8888ToColor(rgba8888ToColor, rgba8888);
                    r += rgba8888ToColor.r;
                    g += rgba8888ToColor.g;
                    b += rgba8888ToColor.b;
                    a += rgba8888ToColor.a;
                }
            }
            r /= (64 * 64);
            g /= (64 * 64);
            b /= (64 * 64);
            a /= (64 * 64);
            SpriteCell cell = this.grid.get(i);
            cell.r = r;
            cell.g = g;
            cell.b = b;
            cell.a = a;
        }
    }

    private static float[] rectangle = new float[8];

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

//        this.fbo = new FrameBuffer(Pixmap.Format.RGBA8888, this.objectLayer.width * 64, this.objectLayer.height * 64, false);
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
        public boolean blocked;
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
            case "grass": return 1;
            default: return -1;
        }
    }
}
