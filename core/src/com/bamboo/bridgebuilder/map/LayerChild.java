package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.LabelLabelPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;

public abstract class LayerChild
{
    public float x, y;
    public Map map;
    public Layer layer;
    public boolean selected;
    public float perspectiveOffsetX = 1;
    public float perspectiveOffsetY = 1;
    public float perspectiveScale = 1;
    public long flickerId;
    public MapSprite toFlicker;
    public long id; // Used to be able to set any layer childs  id and specifically retrieve it in the game

    public Array<PropertyField> lockedProperties; // properties such as ID, rotation, scale, etc. They belong to all layer children

    public LayerChild(Map map, Layer layer, float x, float y)
    {
        this.map = map;
        this.layer = layer;

        this.lockedProperties = new Array<>();
        LabelLabelPropertyValuePropertyField idProperty = new LabelLabelPropertyValuePropertyField("ID", "0", map.skin, map.propertyMenu, null, false);
        this.lockedProperties.add(idProperty);
        setID(map.getAndIncrementId());
    }

    public LayerChild(Map map, float x, float y)
    {
        this.map = map;

        this.lockedProperties = new Array<>();
        LabelLabelPropertyValuePropertyField idProperty = new LabelLabelPropertyValuePropertyField("ID", "0", map.skin, map.propertyMenu, null, false);
        this.lockedProperties.add(idProperty);
        setID(map.getAndIncrementId());
    }

    public void setID(long id)
    {
        for(int i = 0; i < lockedProperties.size; i ++)
        {
            PropertyField propertyField = lockedProperties.get(i);
            if(propertyField instanceof LabelLabelPropertyValuePropertyField)
            {
                LabelLabelPropertyValuePropertyField labelLabelProperty = (LabelLabelPropertyValuePropertyField) propertyField;
                if(labelLabelProperty.getProperty().equals("ID"))
                {
                    labelLabelProperty.value.setText(Long.toString(id));
                    break;
                }
            }
        }
        this.id = id;
    }

    public abstract void update();
    public abstract void draw();
    public abstract void drawHoverOutline();
    public abstract void drawSelectedOutline();
    public abstract void drawSelectedHoveredOutline();

    public abstract boolean isHoveredOver(float x, float y);
    public abstract boolean isHoveredOver(float[] vertices);

    public abstract void select();
    public abstract void unselect();

    public abstract void setPosition(float x, float y);
    public abstract float getX();
    public abstract float getY();

    public abstract void setRotation(float degrees);
    public abstract void setScale(float scale);
    public abstract float getScale();
    public abstract float getRotation();
}
