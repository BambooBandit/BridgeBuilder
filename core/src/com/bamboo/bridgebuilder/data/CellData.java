package com.bamboo.bridgebuilder.data;

import com.badlogic.gdx.graphics.Color;
import com.bamboo.bridgebuilder.map.SpriteGrid;

public class CellData
{
    public static int defaultCValue;
    public String t; // dust type
    public int c; // color, rgb
    public CellData(){}
    public CellData(SpriteGrid.SpriteCell spriteCell)
    {
        this.t = spriteCell.dustType;
        if(this.t == null)
            this.c = 0;
        else
        {
            this.c = Color.rgb888(spriteCell.r, spriteCell.g, spriteCell.b) - defaultCValue;
            if(this.c < 260)
                this.c = 0;
        }
    }
}
