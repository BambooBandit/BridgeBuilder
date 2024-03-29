package com.bamboo.bridgebuilder.map;

import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LongArray;
import com.bamboo.bridgebuilder.EditorPoint;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.ui.BBShapeRenderer;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.FieldFieldPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.LightPropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;

public class MapPoint extends MapObject
{
    public static float[] pointShape = new float[10];
    public EditorPoint point;
    public PointLight light;

    // branch connections
    public LongArray toBranchIds;
    public Array<MapPoint> toBranchPoints;
    public Array<MapPoint> fromBranchPoints;

    public MapPoint(Map map, Layer layer, float x, float y)
    {
        super(map, layer, x, y);
        this.point = new EditorPoint();
        this.point.setPosition(x, y);
        setPosition(x, y);
    }

    public MapPoint(Map map, MapSprite mapSprite, float x, float y)
    {
        super(map, mapSprite, x, y);
        this.point = new EditorPoint();
        this.point.setPosition(x, y);
        setPosition(x, y);
        setOriginBasedOnParentSprite();
    }

    public MapPoint(Map map, float x, float y)
    {
        super(map, x, y);
        this.point = new EditorPoint();
        this.point.setPosition(x, y);
        setPosition(x, y);
        setOriginBasedOnParentSprite();
    }

    @Override public void update() {}

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

        if(this.light != null)
            this.light.setPosition(this.point.getTransformedX(), this.point.getTransformedY());
    }

    @Override
    public float getX()
    {
        return this.x;
    }

    @Override
    public float getY()
    {
        return this.y;
    }

    @Override
    public void draw()
    {
        map.editor.shapeRenderer.set(BBShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.CYAN);

        float x = this.point.getTransformedX() - map.cameraX;
        float y = this.point.getTransformedY() - map.cameraY;
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

        PropertyField propertyField = Utils.getPropertyField(properties, "angle");
        if (propertyField != null)
        {
            FieldFieldPropertyValuePropertyField angleProperty = (FieldFieldPropertyValuePropertyField) propertyField;
            try
            {
                float angle = (float) Math.toRadians(Float.parseFloat(angleProperty.getValue()));
                drawAngle(angle);
                return;
            }
            catch (NumberFormatException e)
            {
                return;
            }
        }
    }

    private void drawAngle(float angle)
    {
        map.editor.shapeRenderer.line(x - map.cameraX, y - map.cameraY, (float) (x - map.cameraX + Math.cos(angle) * .4), (float) (y - map.cameraY + Math.sin(angle) * .4));
    }

    @Override
    public void draw(float xOffset, float yOffset)
    {
        setPosition(getX() + xOffset, getY() + yOffset);
        map.editor.shapeRenderer.set(BBShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.CYAN);
        float x = this.point.getTransformedX() - map.cameraX;
        float y = this.point.getTransformedY() - map.cameraY;
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

        PropertyField propertyField = Utils.getPropertyField(properties, "angle");
        if (propertyField != null)
        {
            FieldFieldPropertyValuePropertyField angleProperty = (FieldFieldPropertyValuePropertyField) propertyField;
            try
            {
                float angle = (float) Math.toRadians(Float.parseFloat(angleProperty.getValue()));
                drawAngle(angle);
                return;
            }
            catch (NumberFormatException e)
            {
                return;
            }
        }

        setPosition(getX() - xOffset, getY() - yOffset);

    }

    @Override
    public void drawHoverOutline()
    {
        map.editor.shapeRenderer.set(BBShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.ORANGE);
        float x = this.point.getTransformedX() - map.cameraX;
        float y = this.point.getTransformedY() - map.cameraY;
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
        map.editor.shapeRenderer.set(BBShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.GREEN);
        float x = this.point.getTransformedX() - map.cameraX;
        float y = this.point.getTransformedY() - map.cameraY;
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
        map.editor.shapeRenderer.set(BBShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.YELLOW);
        float x = this.point.getTransformedX() - map.cameraX;
        float y = this.point.getTransformedY() - map.cameraY;
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
        return distance <= .3f;
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
        mapPoint.attachedId = this.attachedId;
        mapPoint.attachedMapObjectManager = this.attachedMapObjectManager;
        mapPoint.properties = this.attachedMapObjectManager.properties;
        if(this.light != null)
            mapPoint.createLight();
        return mapPoint;
    }

    @Override
    public void setScale(float scale)
    {
        if (this.attachedSprite == null)
            return;
        this.point.setScale(scale, scale);
        if(this.light != null)
            this.light.setPosition(this.point.getTransformedX(), this.point.getTransformedY());
    }

    @Override
    public float getScale()
    {
        if(this.attachedSprite == null)
            return 1;
        return this.attachedSprite.scale;
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
        if(this.light != null)
            this.light.setPosition(this.point.getTransformedX(), this.point.getTransformedY());
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

    public void createLight()
    {
        if(this.light != null)
        {
            updateLightProperties();
            return;
        }
        LightPropertyField lightField = Utils.getLightField(this.properties);
        this.light = new PointLight(this.map.rayHandler, lightField.getRayAmount());
        this.light.setColor(lightField.getR(), lightField.getG(), lightField.getB(), lightField.getA());
        this.light.setDistance(lightField.getDistance());
        this.light.setPosition(getX(), getY());
    }

    public void updateLightProperties()
    {
        if(this.light == null)
            return;
        LightPropertyField lightField = Utils.getLightField(this.properties);
        if(this.light.getRayNum() != lightField.getRayAmount())
        {
            this.destroyLight();
            this.createLight();
            return;
        }
        this.light.setColor(lightField.getR(), lightField.getG(), lightField.getB(), lightField.getA());
        this.light.setDistance(lightField.getDistance());
    }

    public void destroyLight()
    {
        if(this.light == null)
            return;
        this.light.remove();
        this.light = null;
    }
}
