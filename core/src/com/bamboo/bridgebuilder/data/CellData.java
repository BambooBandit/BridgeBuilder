package com.bamboo.bridgebuilder.data;

import com.badlogic.gdx.graphics.Color;
import com.bamboo.bridgebuilder.map.SpriteGrid;

public class CellData
{
    public static int defaultCValue;
    public int t; // dust type
    public int c; // color, rgb
    public int f; // footprint
    public int skip; // skip
    public CellData(){}
    public CellData(SpriteGrid.SpriteCell spriteCell)
    {
        this.t = spriteCell.spriteGrid.objectLayer.map.getCellTypeID(spriteCell.dustType);
        this.f = spriteCell.footprint;
        if(this.t == 0)
        {
            this.c = 0;
            this.f = 0;
        }
        else
        {
            this.c = Color.rgb888(spriteCell.r, spriteCell.g, spriteCell.b) - defaultCValue;
            if(this.c < 260)
            {
                this.c = 0;
                this.f = 0;
            }
        }
    }
}
