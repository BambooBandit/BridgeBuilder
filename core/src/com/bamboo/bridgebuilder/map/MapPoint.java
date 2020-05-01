package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;

public class MapPoint extends MapObject
{
    public static float[] pointShape = new float[10];

    public MapPoint(Map map, Layer layer, float x, float y)
    {
        super(map, layer, x, y);
    }

    @Override
    public void setPosition(float x, float y)
    {
        super.setPosition(x, y);
        if (this.attachedSprite != null)
        {
            float centerX = attachedSprite.position.x + attachedSprite.width / 2;
            float centerY = attachedSprite.position.y + attachedSprite.height / 2;
            float angle = (float) Math.toRadians(this.attachedSprite.sprite.getRotation()); // Convert to radians

            float rotatedX = (float) (Math.cos(angle) * (position.x - centerX) - Math.sin(angle) * (position.y - centerY) + centerX);
            float rotatedY = (float) (Math.sin(angle) * (position.x - centerX) + Math.cos(angle) * (position.y - centerY) + centerY);
            x = rotatedX;
            y = rotatedY;
            float scaledX = rotatedX + (centerX - rotatedX) * (1 - attachedSprite.sprite.getScaleX());
            float scaledY = rotatedY + (centerY - rotatedY) * (1 - attachedSprite.sprite.getScaleY());
            super.setPosition(scaledX, scaledY);

            this.moveBox.setPosition(x, y);
        }
    }

    @Override
    public void draw()
    {
        map.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.CYAN);
        pointShape[0] = position.x + 0;
        pointShape[1] = position.y + 0;
        pointShape[2] = position.x - .1333f;
        pointShape[3] = position.y + .2666f;
        pointShape[4] = position.x - .0333f;
        pointShape[5] = position.y + .3666f;
        pointShape[6] = position.x + .0333f;
        pointShape[7] = position.y + .3666f;
        pointShape[8] = position.x + .1333f;
        pointShape[9] = position.y + .2666f;
        map.editor.shapeRenderer.polygon(pointShape);
    }

    @Override
    public void drawHoverOutline()
    {
        map.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.ORANGE);
        pointShape[0] = position.x + 0;
        pointShape[1] = position.y + 0;
        pointShape[2] = position.x - .1333f;
        pointShape[3] = position.y + .2666f;
        pointShape[4] = position.x - .0333f;
        pointShape[5] = position.y + .3666f;
        pointShape[6] = position.x + .0333f;
        pointShape[7] = position.y + .3666f;
        pointShape[8] = position.x + .1333f;
        pointShape[9] = position.y + .2666f;
        map.editor.shapeRenderer.polygon(pointShape);
    }

    @Override
    public void drawSelectedOutline()
    {
        map.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.GREEN);
        pointShape[0] = position.x + 0;
        pointShape[1] = position.y + 0;
        pointShape[2] = position.x - .1333f;
        pointShape[3] = position.y + .2666f;
        pointShape[4] = position.x - .0333f;
        pointShape[5] = position.y + .3666f;
        pointShape[6] = position.x + .0333f;
        pointShape[7] = position.y + .3666f;
        pointShape[8] = position.x + .1333f;
        pointShape[9] = position.y + .2666f;
        map.editor.shapeRenderer.polygon(pointShape);
    }

    @Override
    public void drawSelectedHoveredOutline()
    {
        map.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.YELLOW);
        pointShape[0] = position.x + 0;
        pointShape[1] = position.y + 0;
        pointShape[2] = position.x - .1333f;
        pointShape[3] = position.y + .2666f;
        pointShape[4] = position.x - .0333f;
        pointShape[5] = position.y + .3666f;
        pointShape[6] = position.x + .0333f;
        pointShape[7] = position.y + .3666f;
        pointShape[8] = position.x + .1333f;
        pointShape[9] = position.y + .2666f;
        map.editor.shapeRenderer.polygon(pointShape);
    }

    @Override
    public boolean isHoveredOver(float x, float y)
    {
        double distance = Math.sqrt(Math.pow((x - position.x), 2) + Math.pow((y - position.y), 2));
        return distance <= .6f;
    }

    @Override
    public boolean isHoveredOver(float[] vertices)
    {
        return Intersector.isPointInPolygon(vertices, 0, vertices.length, position.x, position.y);
    }
}
