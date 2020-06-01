package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.map.SpriteGrid;

public class CellData
{
    public String t; // dust type
    public float r, g, b, a;
    public boolean bl; // blocked
    public CellData(){}
    public CellData(SpriteGrid.SpriteCell spriteCell)
    {
        this.t = spriteCell.dustType;
        this.r = spriteCell.r;
        this.g = spriteCell.g;
        this.b = spriteCell.b;
        this.a = spriteCell.a;
        this.bl = spriteCell.blocked;
    }
}
