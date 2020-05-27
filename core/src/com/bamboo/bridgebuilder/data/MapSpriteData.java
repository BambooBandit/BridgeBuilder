package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.MapSprite;

public class MapSpriteData extends LayerChildData
{
    public float z;
    public float r, g, b, a;
    public int id; // Manually definable ID
    public String n; // Sprite name
    public String sN; // Sheet name
    public float rot, scl;
    public int loi; // layer override index. The index of the layer that has been overridden to always draw behind this sprite. It is also counting from 1 rather than 0 for JSON purposes.
    public static int defaultColorValue = 1;
    public static int defaultScaleValue = 1;

    public MapSpriteData() {}
    public MapSpriteData(MapSprite mapSprite)
    {
        super(mapSprite);
        this.z = mapSprite.z;
        this.id = mapSprite.id;
        this.n = mapSprite.tool.name;
        this.sN = mapSprite.tool.sheet.name;
        this.rot = mapSprite.sprite.getRotation();
        this.scl = mapSprite.sprite.getScaleX() - defaultScaleValue;
        this.r = mapSprite.sprite.getColor().r - defaultColorValue;
        this.g = mapSprite.sprite.getColor().g - defaultColorValue;
        this.b = mapSprite.sprite.getColor().b - defaultColorValue;
        this.a = mapSprite.sprite.getColor().a - defaultColorValue;
        if(mapSprite.layerOverride != null)
            this.loi = mapSprite.layer.map.layers.indexOf(mapSprite.layerOverride, true) + 1;
        else
            this.loi = 0;
    }
}
