package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.bamboo.bridgebuilder.EditorPoint;

public class MapPoint extends MapObject
{
    public static float[] pointShape = new float[10];
    public EditorPoint point;

    public MapPoint(Map map, Layer layer, float x, float y)
    {
        super(map, layer, x, y);
        this.point = new EditorPoint();
        this.point.setPosition(x, y);
    }

    public MapPoint(Map map, MapSprite mapSprite, float x, float y)
    {
        super(map, mapSprite, x, y);
        this.point = new EditorPoint();
        this.point.setPosition(x, y);
        setOriginBasedOnParentSprite();
    }

    @Override
    public void setPosition(float x, float y)
    {
        super.setPosition(x, y);
        this.point.setPosition(x, y);
        if (this.attachedSprite != null && this.attachedSprite instanceof MapSprite)
        {
            MapSprite mapSprite = this.attachedSprite;
            point.setRotation(mapSprite.rotation);
            point.setScale(mapSprite.scale, mapSprite.scale);
        }

        this.moveBox.setPosition(x, y);
        setOriginBasedOnParentSprite();
    }

    @Override
    public float getX()
    {
        return this.point.getX();
    }

    @Override
    public float getY()
    {
        return this.point.getY();
    }

    @Override
    public void draw()
    {
        map.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.CYAN);

        float x = this.point.getTransformedX();
        float y = this.point.getTransformedY();
        pointShape[0] = x + 0;
        pointShape[1] = y + 0;
        pointShape[2] = x - .1333f;
        pointShape[3] = y + .2666f;
        pointShape[4] = x - .0333f;
        pointShape[5] = y + .3666f;
        pointShape[6] = x + .0333f;
        pointShape[7] = y + .3666f;
        pointShape[8] = x + .1333f;
        pointShape[9] = y + .2666f;
        map.editor.shapeRenderer.polygon(pointShape);
    }

    @Override
    public void draw(float xOffset, float yOffset)
    {
        setPosition(getX() + xOffset, getY() + yOffset);
        map.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.CYAN);
        float x = this.point.getTransformedX();
        float y = this.point.getTransformedY();
        pointShape[0] = x + 0;
        pointShape[1] = y + 0;
        pointShape[2] = x - .1333f;
        pointShape[3] = y + .2666f;
        pointShape[4] = x - .0333f;
        pointShape[5] = y + .3666f;
        pointShape[6] = x + .0333f;
        pointShape[7] = y + .3666f;
        pointShape[8] = x + .1333f;
        pointShape[9] = y + .2666f;
        map.editor.shapeRenderer.polygon(pointShape);
        setPosition(getX() - xOffset, getY() - yOffset);
    }

    @Override
    public void drawHoverOutline()
    {
        map.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.ORANGE);
        float x = this.point.getTransformedX();
        float y = this.point.getTransformedY();
        pointShape[0] = x + 0;
        pointShape[1] = y + 0;
        pointShape[2] = x - .1333f;
        pointShape[3] = y + .2666f;
        pointShape[4] = x - .0333f;
        pointShape[5] = y + .3666f;
        pointShape[6] = x + .0333f;
        pointShape[7] = y + .3666f;
        pointShape[8] = x + .1333f;
        pointShape[9] = y + .2666f;
        map.editor.shapeRenderer.polygon(pointShape);
    }

    @Override
    public void drawSelectedOutline()
    {
        map.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.GREEN);
        float x = this.point.getTransformedX();
        float y = this.point.getTransformedY();
        pointShape[0] = x + 0;
        pointShape[1] = y + 0;
        pointShape[2] = x - .1333f;
        pointShape[3] = y + .2666f;
        pointShape[4] = x - .0333f;
        pointShape[5] = y + .3666f;
        pointShape[6] = x + .0333f;
        pointShape[7] = y + .3666f;
        pointShape[8] = x + .1333f;
        pointShape[9] = y + .2666f;
        map.editor.shapeRenderer.polygon(pointShape);
    }

    @Override
    public void drawSelectedHoveredOutline()
    {
        map.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.YELLOW);
        float x = this.point.getTransformedX();
        float y = this.point.getTransformedY();
        pointShape[0] = x + 0;
        pointShape[1] = y + 0;
        pointShape[2] = x - .1333f;
        pointShape[3] = y + .2666f;
        pointShape[4] = x - .0333f;
        pointShape[5] = y + .3666f;
        pointShape[6] = x + .0333f;
        pointShape[7] = y + .3666f;
        pointShape[8] = x + .1333f;
        pointShape[9] = y + .2666f;
        map.editor.shapeRenderer.polygon(pointShape);
    }

    @Override
    public boolean isHoveredOver(float x, float y)
    {
        float pointX = this.point.getTransformedX();
        float pointY = this.point.getTransformedY();

        double distance = Math.sqrt(Math.pow((x - pointX), 2) + Math.pow((y - pointY), 2));
        return distance <= .6f;
    }

    @Override
    public boolean isHoveredOver(float[] vertices)
    {
        float pointX = this.point.getTransformedX();
        float pointY = this.point.getTransformedY();

        return Intersector.isPointInPolygon(vertices, 0, vertices.length, pointX, pointY);
    }

    @Override
    public MapObject copy()
    {
        MapPoint mapPoint;
        if (this.attachedSprite != null)
            mapPoint = new MapPoint(map, this.attachedSprite, this.point.getX(), this.point.getY());
        else
            mapPoint = new MapPoint(map, this.layer, this.point.getX(), this.point.getY());
        mapPoint.id = this.id;
        mapPoint.attachedMapObjectManager = this.attachedMapObjectManager;
        return mapPoint;
    }

    @Override
    public void setScale(float scale)
    {
        if (this.attachedSprite == null)
            return;
        this.point.setScale(scale, scale);
    }

    @Override
    public float getRotation()
    {
        return this.point.getRotation();
    }

    @Override
    public void setRotation(float degrees)
    {
        if (this.attachedSprite == null)
            return;
        this.point.setRotation(degrees);
    }

    @Override
    public void setOriginBasedOnParentSprite()
    {
        if (this.attachedSprite == null)
            return;
        float xOffset = this.point.getX() - this.attachedSprite.getX();
        float yOffset = this.point.getY() - this.attachedSprite.getY();
        float width = this.attachedSprite.sprite.getWidth();
        float height = this.attachedSprite.sprite.getHeight();
        this.point.setOrigin((-xOffset) + width / 2, (-yOffset) + height / 2);
    }
}
