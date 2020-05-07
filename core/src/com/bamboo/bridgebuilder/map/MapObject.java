package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.ui.manipulators.MoveBox;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;

public abstract class MapObject extends LayerChild
{
    public MoveBox moveBox;
    public Array<PropertyField> properties;

    public MapSprite attachedSprite = null;

    public MapObject(Map map, Layer layer, float x, float y)
    {
        super(map, layer, x, y);
        this.properties = new Array<>();
        this.moveBox = new MoveBox();
        this.moveBox.setPosition(x, y);
        this.position.set(x, y);
    }

    public MapObject(Map map, MapSprite mapSprite, float x, float y)
    {
        super(map, mapSprite.layer, x, y);
        this.attachedSprite = mapSprite;
        this.properties = new Array<>();
        this.moveBox = new MoveBox();
        this.moveBox.setPosition(x, y);
        this.position.set(x, y);
    }

    @Override
    public void setPosition(float x, float y)
    {
        super.setPosition(x, y);
        this.moveBox.setPosition(x, y);
    }

    public void drawMoveBox()
    {
        if(selected)
            moveBox.sprite.draw(map.editor.batch);
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

    public final boolean isAttachedObject()
    {
        return this.attachedSprite != null;
    }

    public abstract void draw(float xOffset, float yOffset);
}
