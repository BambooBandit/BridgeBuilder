package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.FieldFieldPropertyValuePropertyField;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

/** Creates a grid of information based on sprites on whichever floor this represents. Information such as average sprite color in cells, and what material you are walking on. */
public class SpriteGrid
{
    private ObjectLayer objectLayer;
    private Array<SpriteCell> grid; // Going from bottom left to top right. Every x amount of indices is x width of the layer and height of 1. False is not blocked, true is blocked.

    private FrameBuffer fbo;

    public SpriteGrid(ObjectLayer objectLayer)
    {
        this.objectLayer = objectLayer;

        this.grid = new Array<SpriteCell>(this.objectLayer.width * this.objectLayer.height);

        this.fbo = new FrameBuffer(Pixmap.Format.RGBA8888, objectLayer.width * 64, objectLayer.height * 64, false);

        this.resizeGrid();
    }

    public void drawColor()
    {
//        objectLayer.map.editor.batch.draw(texture, 0, texture.getHeight() / 64, texture.getWidth() / 64, texture.getHeight() / -64);
        objectLayer.map.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        for(int i = 0; i < this.grid.size; i ++)
        {
            SpriteCell cell = this.grid.get(i);
            objectLayer.map.editor.shapeRenderer.setColor(cell.r, cell.g, cell.b, cell.a);
            int x = (int) Math.floor(i % this.objectLayer.width);
            int y = (int) Math.floor(i / this.objectLayer.width);
            objectLayer.map.editor.shapeRenderer.rect(x, y, 1, 1);
        }
    }

    public void update()
    {
        updateColorGrid();

        for(int i = 0; i < this.grid.size; i ++)
        {
            int x = (int) Math.floor(i % this.objectLayer.width);
            int y = (int) Math.floor(i / this.objectLayer.width);

            SpriteCell cell = this.grid.get(i);
            cell.dustType = checkCellForDustTypePolygons(x, y);
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
        float oldCamX = objectLayer.map.camera.position.x;
        float oldCamY = objectLayer.map.camera.position.y;
        this.objectLayer.map.camera.viewportWidth = this.objectLayer.width / this.objectLayer.map.camera.zoom;
        this.objectLayer.map.camera.viewportHeight = this.objectLayer.height / this.objectLayer.map.camera.zoom;
        this.objectLayer.map.camera.position.set(this.objectLayer.map.camera.viewportWidth * this.objectLayer.map.camera.zoom / 2f, this.objectLayer.map.camera.viewportHeight * this.objectLayer.map.camera.zoom / 2f, 0);
        this.objectLayer.map.camera.update();

        Gdx.gl.glClearColor(0, 0, 0, 0);
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

        Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, objectLayer.width * 64, objectLayer.height * 64);

        this.fbo.end();
        FrameBuffer.unbind();

        this.fbo.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // Reset camera after drawing to fbo
        this.objectLayer.map.camera.position.set(oldCamX, oldCamY, 0);
        this.objectLayer.map.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        this.objectLayer.map.editor.batch.setBlendFunction(-1, -1);
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
    public String checkCellForDustTypePolygons(int x, int y)
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

        // Check polygons in this object layer
        for(int i = 0; i < this.objectLayer.children.size; i ++)
        {
            MapObject mapObject = this.objectLayer.children.get(i);
            if(mapObject instanceof MapPolygon)
            {
                MapPolygon mapPolygon = (MapPolygon) mapObject;
                FieldFieldPropertyValuePropertyField property = (FieldFieldPropertyValuePropertyField) Utils.getPropertyField(mapPolygon.properties, "dustType");
                if(property != null)
                {
                    if(Intersector.overlapConvexPolygons(rectangle, mapPolygon.polygon.getTransformedVertices(), null))
                        return property.value.getText();
                }
            }
        }

        // Check attached polygons in all sprite layers in the same floor, as well as other object layers in the same floor
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
                        FieldFieldPropertyValuePropertyField property = (FieldFieldPropertyValuePropertyField) Utils.getPropertyField(mapPolygon.properties, "dustType");
                        if(property != null)
                        {
                            if(Intersector.overlapConvexPolygons(rectangle, mapPolygon.polygon.getTransformedVertices(), null))
                                return property.value.getText();
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
                                    FieldFieldPropertyValuePropertyField property = (FieldFieldPropertyValuePropertyField) Utils.getPropertyField(mapPolygon.properties, "dustType");
                                    if(property != null)
                                    {
                                        if(Intersector.overlapConvexPolygons(rectangle, mapPolygon.polygon.getTransformedVertices(), null))
                                            return property.value.getText();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public void resizeGrid()
    {
        this.grid.clear();
        int newSize = this.objectLayer.width * this.objectLayer.height;
        if(this.grid.size >= newSize)
            this.grid.removeRange(newSize, this.grid.size - 1);
        for(int i = this.grid.size; i < newSize; i ++)
        {
            this.grid.add(new SpriteCell());
        }
        this.fbo = new FrameBuffer(Pixmap.Format.RGBA8888, objectLayer.width * 64, objectLayer.height * 64, false);
        update();
    }

    public void clear()
    {
        this.grid.clear();
    }

    public class SpriteCell
    {
        public String dustType;
        public float r, g, b, a;
    }
}
