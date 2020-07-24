package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.MapSprite;

public class MapSpriteData extends LayerChildData
{
    public float z;
    public float r, g, b, a;
    public long id;
    public String n; // Sprite name
    public String sN; // Sheet name
    public float rot, scl, w, h;
    public int loi; // layer override index. The index of the layer that has been overridden to always draw behind this sprite. It is also counting from 1 rather than 0 for JSON purposes.
    public static int defaultColorValue = 1;
    public static int defaultScaleValue = 1;
    public boolean parent;
    public float x1, y1, x2, y2, x3, y3, x4, y4;
    public int eId; // to edge mapSprite id

    public MapSpriteData() {}
    public MapSpriteData(MapSprite mapSprite)
    {
        super(mapSprite);
        this.z = mapSprite.z;
        this.id = mapSprite.id;
        this.n = mapSprite.tool.name;

        if(mapSprite.attachedSprites != null && mapSprite.attachedSprites.children.size > 0)
            parent = true;

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

        this.x1 = mapSprite.x1Offset;
        this.y1 = mapSprite.y1Offset;
        this.x2 = mapSprite.x2Offset;
        this.y2 = mapSprite.y2Offset;
        this.x3 = mapSprite.x3Offset;
        this.y3 = mapSprite.y3Offset;
        this.x4 = mapSprite.x4Offset;
        this.y4 = mapSprite.y4Offset;

        if(mapSprite.toEdgeSprite != null)
            this.eId = mapSprite.toEdgeSprite.id;
    }
}
