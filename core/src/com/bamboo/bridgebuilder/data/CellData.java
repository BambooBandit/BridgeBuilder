package com.bamboo.bridgebuilder.data;

import com.badlogic.gdx.graphics.Color;
import com.bamboo.bridgebuilder.map.SpriteGrid;

public class CellData
{
    public String t; // dust type
    public int c; // color, rgb
    public CellData(){}
    public CellData(SpriteGrid.SpriteCell spriteCell)
    {
        this.t = spriteCell.dustType;
        this.c = Color.rgb888(spriteCell.r, spriteCell.g, spriteCell.b);
    }
}
