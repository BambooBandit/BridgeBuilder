package com.bamboo.bridgebuilder.map;

import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.EditorPolygon;
import com.bamboo.bridgebuilder.ui.manipulators.MoveBox;
import com.bamboo.bridgebuilder.ui.propertyMenu.PropertyField;

public abstract class MapObject extends LayerChild
{
//    protected float rotation;
//    public RotationBox rotationBox;
    public MoveBox moveBox;
    private boolean selected;
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

    public void select()
    {
        this.selected = true;
    }
    public void unselect()
    {
        this.selected = false;
    }

    public abstract boolean isHoveredOver(float x, float y);
}
