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
    public float perspectiveScale = 0;

    public LayerChild(Map map, Layer layer, float x, float y)
    {
        this.map = map;
        this.layer = layer;
    }

    public void updatePerspective()
    {
        PropertyField topProperty = Utils.getPropertyField(map.propertyMenu.mapPropertyPanel.properties, "bottomScale");
        PropertyField bottomProperty = Utils.getPropertyField(map.propertyMenu.mapPropertyPanel.properties, "topScale");
        if(bottomProperty == null || topProperty == null)
            return;
        try
        {
            float perspectiveBottom = Float.parseFloat(((FieldFieldPropertyValuePropertyField) bottomProperty).value.getText());
            float perspectiveTop = Float.parseFloat(((FieldFieldPropertyValuePropertyField) topProperty).value.getText());

            float mapHeight = layer.height;
            float positionY = getY();

            float coeff = positionY / mapHeight;
            float delta = perspectiveTop - perspectiveBottom;

            this.perspectiveScale = perspectiveBottom + coeff * delta;

            this.setScale(getScale());
            this.setPosition(getX(), getY());
        }
        catch (NumberFormatException e){}
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
