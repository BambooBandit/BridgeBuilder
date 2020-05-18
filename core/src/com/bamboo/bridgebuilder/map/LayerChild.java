package com.bamboo.bridgebuilder.map;

import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.FieldFieldPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;

public abstract class LayerChild
{
    public float x, y;
    protected Map map;
    public Layer layer;
    public boolean selected;
    public float perspective = 0;

    public LayerChild(Map map, Layer layer, float x, float y)
    {
        this.map = map;
        this.layer = layer;
    }

    public void updatePerspective()
    {
        PropertyField bottomProperty = Utils.getPropertyField(map.propertyMenu.mapPropertyPanel.properties, "bottomPerspective");
        PropertyField topProperty = Utils.getPropertyField(map.propertyMenu.mapPropertyPanel.properties, "topPerspective");
        if(bottomProperty == null || topProperty == null)
            return;
        FieldFieldPropertyValuePropertyField bottomPropertyField = (FieldFieldPropertyValuePropertyField) bottomProperty;
        FieldFieldPropertyValuePropertyField topPropertyField = (FieldFieldPropertyValuePropertyField) topProperty;
        float perspectiveBottom = Float.parseFloat(bottomPropertyField.value.getText());
        float perspectiveTop = Float.parseFloat(topPropertyField.value.getText());

        float mapHeight = layer.height;
        float positionY = getY();

        float coeff = positionY / mapHeight;
        float delta = perspectiveTop - perspectiveBottom;

        this.perspective = perspectiveBottom + coeff * delta;

        this.setScale(getScale());
        this.setPosition(getX(), getY());



//        float totalScale = scale + perspectiveScale;
//        if(totalScale <= 0)
//            return;
//
//        this.sprite.setScale(totalScale);
//        this.polygon.setScale(totalScale, totalScale);
//        if(this.tool.topSprites != null)
//        {
//            for(int i = 0; i < this.tool.topSprites.size; i ++)
//                this.tool.topSprites.get(i).setScale(totalScale);
//        }
//        for(int i = 0; i < drawableAttachedMapObjects.size; i ++)
//        {
//            if(drawableAttachedMapObjects.get(i).polygon != null)
//                drawableAttachedMapObjects.get(i).polygon.setScale(totalScale, totalScale);
//            drawableAttachedMapObjects.get(i).updateLightsAndBodies();
//        }
    }

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
