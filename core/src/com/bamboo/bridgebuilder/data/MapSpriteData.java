package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.MapSprite;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.*;

import java.util.ArrayList;

public class MapSpriteData extends LayerChildData
{
    public float z;
    public float r, g, b, a;
    public int id; // Manually definable ID
    public String n; // Sprite name
    public String sN; // Sheet name
    public float rot, scl, w, h;
    public int loi; // layer override index. The index of the layer that has been overridden to always draw behind this sprite. It is also counting from 1 rather than 0 for JSON purposes.
    public static int defaultColorValue = 1;
    public static int defaultScaleValue = 1;
    public ArrayList<PropertyData> lProps;

    public MapSpriteData() {}
    public MapSpriteData(MapSprite mapSprite)
    {
        super(mapSprite);
        this.z = mapSprite.z;
        this.id = mapSprite.id;
        this.n = mapSprite.tool.name;

        if(mapSprite.tool.sheet.name.startsWith("editor"))
        {
            this.sN = mapSprite.tool.sheet.name.substring(6);
            this.sN = Character.toLowerCase(this.sN.charAt(0)) + this.sN.substring(1);
        }
        else
            this.sN = mapSprite.tool.sheet.name;

        this.rot = mapSprite.sprite.getRotation();
        this.scl = mapSprite.sprite.getScaleX() - defaultScaleValue;
        this.w = mapSprite.sprite.getWidth();
        this.h = mapSprite.sprite.getHeight();
        this.r = mapSprite.sprite.getColor().r - defaultColorValue;
        this.g = mapSprite.sprite.getColor().g - defaultColorValue;
        this.b = mapSprite.sprite.getColor().b - defaultColorValue;
        this.a = mapSprite.sprite.getColor().a - defaultColorValue;
        if(mapSprite.layerOverride != null)
            this.loi = mapSprite.layer.map.layers.indexOf(mapSprite.layerOverride, true) + 1;
        else
            this.loi = 0;

        this.lProps = new ArrayList<>();
        for(int i = 0; i < mapSprite.lockedProperties.size; i ++)
        {
            PropertyField property = mapSprite.lockedProperties.get(i);
            if(property instanceof ColorPropertyField)
                this.lProps.add(new ColorPropertyFieldData((ColorPropertyField) property));
            else if(property instanceof LightPropertyField)
                this.lProps.add(new LightPropertyFieldData((LightPropertyField) property));
            else if(property instanceof FieldFieldPropertyValuePropertyField)
                this.lProps.add(new FieldFieldPropertyValuePropertyFieldData((FieldFieldPropertyValuePropertyField) property));
            else if(property instanceof LabelFieldPropertyValuePropertyField)
                this.lProps.add(new LabelFieldPropertyValuePropertyFieldData((LabelFieldPropertyValuePropertyField) property));
        }
    }
}
