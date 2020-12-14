package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.utils.FloatArray;
import com.bamboo.bridgebuilder.EditorPolygon;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapPolygon;
import com.bamboo.bridgebuilder.map.MapSprite;
import com.bamboo.bridgebuilder.map.ObjectLayer;

public class DrawMapPolygon implements Command
{
    private Map map;
    private ObjectLayer selectedObjectLayer;
    private MapSprite selectedMapSprite;
    private FloatArray vertices;
    private float objectX;
    private float objectY;
    public MapPolygon mapPolygon;

    public DrawMapPolygon(Map map, ObjectLayer selectedObjectLayer, FloatArray vertices, float objectX, float objectY)
    {
        this.map = map;
        this.selectedObjectLayer = selectedObjectLayer;
        this.vertices = new FloatArray(vertices);
        this.objectX = objectX;
        this.objectY = objectY;
    }

    public DrawMapPolygon(Map map, MapSprite mapSprite, FloatArray vertices, float objectX, float objectY)
    {
        this.map = map;
        this.selectedMapSprite = mapSprite;
        this.vertices = new FloatArray(vertices);
        this.objectX = objectX;
        this.objectY = objectY;
    }

    @Override
    public void execute()
    {
        if(this.selectedObjectLayer != null)
        {
            if (this.mapPolygon == null)
                this.mapPolygon = new MapPolygon(this.map, this.selectedObjectLayer, vertices.toArray(), this.objectX, this.objectY);
            this.selectedObjectLayer.addMapObject(mapPolygon);
        }
        else
        {
            if (this.mapPolygon == null)
            {
                EditorPolygon editorPolygon = new EditorPolygon(vertices.toArray());
                float xOffset = this.objectX - this.selectedMapSprite.getX();
                float yOffset = this.objectY - this.selectedMapSprite.getY();
                float width = this.selectedMapSprite.sprite.getWidth();
                float height = this.selectedMapSprite.sprite.getHeight();
                editorPolygon.setOrigin((-xOffset) + width / 2, (-yOffset) + height / 2);
                editorPolygon.setRotation(Utils.degreeAngleFix(-this.selectedMapSprite.rotation));
                editorPolygon.setScale(1 / this.selectedMapSprite.scale, 1 / this.selectedMapSprite.scale);
                this.mapPolygon = new MapPolygon(this.map, this.selectedMapSprite, editorPolygon.getTransformedVertices(), this.objectX, this.objectY);
            }
            this.mapPolygon.setRotation(this.selectedMapSprite.rotation);
            this.mapPolygon.setScale(this.selectedMapSprite.scale);
            this.selectedMapSprite.createAttachedMapObject(this.map, this.mapPolygon);
        }
    }

    @Override
    public void undo()
    {
        if(this.selectedObjectLayer != null)
            this.selectedObjectLayer.children.removeValue(mapPolygon, true);
        else
            this.selectedMapSprite.removeAttachedMapObject(this.mapPolygon);
        this.map.input.objectVerticePosition.set(this.objectX, this.objectY);
    }
}
