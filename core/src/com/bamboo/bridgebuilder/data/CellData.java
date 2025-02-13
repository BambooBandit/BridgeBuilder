package com.bamboo.bridgebuilder.data;

import com.badlogic.gdx.graphics.Color;
import com.bamboo.bridgebuilder.map.SpriteGrid;

public class CellData
{
    public static int defaultCValue;
    public static Color defaultColorValue;
    public int t; // dust type
    public int c; // color, rgb
    public int f; // footprint
    public int skip; // skip
    public CellData(){}
    public CellData(SpriteGrid.SpriteCell spriteCell, MapData mapData)
    {
        this.t = spriteCell.spriteGrid.objectLayer.map.getCellTypeID(spriteCell.dustType);
        this.f = spriteCell.footprint;
        if(this.t == -1)
        {
            this.c = 0;
            this.f = 0;
        }
        else
        {
            if(Math.abs(spriteCell.r - defaultColorValue.r) < .01f && Math.abs(spriteCell.g - defaultColorValue.g) < .01f && Math.abs(spriteCell.b - defaultColorValue.b) < .01f)
            {
                this.c = 0;
                this.f = 0;
            }
            else
                this.c = Color.rgb888(spriteCell.r, spriteCell.g, spriteCell.b) - defaultCValue;
        }

        if(mapData.footstepHeavy) // flip 0 and 1 for map file optimization
        {
            if(this.f == 0)
                this.f = 1;
            else
                this.f = 0;
        }
    }
}
