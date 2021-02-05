package com.bamboo.bridgebuilder.commands;

import com.bamboo.bridgebuilder.EditorPoint;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapPoint;
import com.bamboo.bridgebuilder.map.MapSprite;
import com.bamboo.bridgebuilder.map.ObjectLayer;

public class DrawMapPoint implements Command
{
    private Map map;
    private ObjectLayer selectedObjectLayer;
    private MapSprite selectedMapSprite;
    private MapPoint mapPoint = null;
    private float x;
    private float y;
    private boolean spriteTool;

    public DrawMapPoint(Map map, ObjectLayer selectedObjectLayer, float x, float y)
    {
        this.map = map;
        this.selectedObjectLayer = selectedObjectLayer;
        this.x = x;
        this.y = y;
    }

    public DrawMapPoint(Map map, MapSprite selectedMapSprite, float x, float y, boolean spriteTool)
    {
        this.map = map;
        this.selectedMapSprite = selectedMapSprite;
        this.x = x;
        this.y = y;
        this.spriteTool = spriteTool;
    }

    @Override
    public void execute()
    {
        if(this.selectedObjectLayer != null)
        {
            if (this.mapPoint == null)
                this.mapPoint = new MapPoint(this.map, this.selectedObjectLayer, this.x, this.y);
            this.selectedObjectLayer.addMapObject(this.mapPoint);
        }
        else
        {
            if (this.mapPoint == null)
            {
                EditorPoint editorPoint = new EditorPoint();
                editorPoint.setPosition(this.x, this.y);
                float xOffset = this.x - this.selectedMapSprite.getX();
                float yOffset = this.y - this.selectedMapSprite.getY();
                float width = this.selectedMapSprite.sprite.getWidth();
                float height = this.selectedMapSprite.sprite.getHeight();
                editorPoint.setOrigin((-xOffset) + width / 2, (-yOffset) + height / 2);
                editorPoint.setRotation(Utils.degreeAngleFix(-this.selectedMapSprite.rotation));
                editorPoint.setScale(1 / this.selectedMapSprite.scale, 1 / this.selectedMapSprite.scale);
                this.mapPoint = new MapPoint(this.map, this.selectedMapSprite, editorPoint.getTransformedX(), editorPoint.getTransformedY());
            }
            this.mapPoint.setRotation(this.selectedMapSprite.rotation);
            this.mapPoint.setScale(this.selectedMapSprite.scale);
            this.selectedMapSprite.createAttachedMapObject(this.map, this.mapPoint, spriteTool);
        }
    }

    @Override
    public void undo()
    {
        if(this.selectedObjectLayer != null)
            this.selectedObjectLayer.children.removeValue(this.mapPoint, true);
        else
            this.selectedMapSprite.removeAttachedMapObject(this.mapPoint);
    }
}
