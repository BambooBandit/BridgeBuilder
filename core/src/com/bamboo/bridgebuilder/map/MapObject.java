package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.ui.manipulators.MoveBox;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;

public abstract class MapObject extends LayerChild implements Comparable<MapObject>
{
    public MoveBox moveBox;
    public Array<PropertyField> properties;

    public MapSprite attachedSprite = null;

    public AttachedMapObjectManager attachedMapObjectManager;
    public int attachedId = 0; // Used to know if one attached map object is the same one on another map sprite of the same type

    public MapObject(Map map, Layer layer, float x, float y)
    {
        super(map, layer, x, y);
        this.properties = new Array<>();
        this.moveBox = new MoveBox();
        this.moveBox.setPosition(x, y);
    }

    public MapObject(Map map, MapSprite mapSprite, float x, float y)
    {
        super(map, mapSprite.layer, x, y);
        this.attachedSprite = mapSprite;
        this.moveBox = new MoveBox();
        this.moveBox.setPosition(x, y);
    }

    public MapObject(Map map, float x, float y)
    {
        super(map, x, y);
        this.moveBox = new MoveBox();
        this.moveBox.setPosition(x, y);
    }

    @Override
    public void setPosition(float x, float y)
    {
        this.x = x;
        this.y = y;
        this.moveBox.setPosition(x, y);
    }

    public void drawMoveBox()
    {
        if(this.selected && this.map.editor.fileMenu.toolPane.select.selected)
        {
            this.moveBox.setScale(this.map.zoom);
            this.moveBox.sprite.setPosition(this.moveBox.x - map.cameraX, this.moveBox.y - map.cameraY);
            this.moveBox.sprite.draw(this.map.editor.batch);
        }
    }

    @Override
    public void select()
    {
        if(this.selected)
            return;
        this.map.selectedObjects.add(this);
        this.selected = true;
    }

    @Override
    public void unselect()
    {
        this.map.selectedObjects.removeValue(this, true);
        this.selected = false;
    }

    public void drawOutline() { }

    public final boolean isAttachedObject()
    {
        return this.attachedSprite != null;
    }

    public abstract void draw(float xOffset, float yOffset);
    public abstract MapObject copy();
    public abstract void setOriginBasedOnParentSprite();

    public float getArea()
    {
        return 0;
    }

    @Override
    public int compareTo(MapObject other)
    {
        if(this instanceof MapPoint && other instanceof MapPoint)
            return 0;
        if(this instanceof MapPoint)
            return 1;
        if(other instanceof MapPoint)
            return -1;

        return Float.compare(other.getArea(), getArea());
    }
}
