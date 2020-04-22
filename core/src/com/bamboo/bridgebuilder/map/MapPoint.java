package com.bamboo.bridgebuilder.map;

import box2dLight.PointLight;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.ui.manipulators.MoveBox;

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
        }
    }

    @Override
    public void draw()
    {
        pointShape[0] = position.x + 0;
        pointShape[1] = position.y + 0;
        pointShape[2] = position.x - 4;
        pointShape[3] = position.y + 8;
        pointShape[4] = position.x - 1;
        pointShape[5] = position.y + 11;
        pointShape[6] = position.x + 1;
        pointShape[7] = position.y + 11;
        pointShape[8] = position.x + 4;
        pointShape[9] = position.y + 8;
        map.editor.shapeRenderer.polygon(pointShape);
    }

    @Override
    public boolean isHoveredOver(float x, float y)
    {
        double distance = Math.sqrt(Math.pow((x - position.x), 2) + Math.pow((y - position.y), 2));
        return distance <= 15;
    }
}
