package com.bamboo.bridgebuilder.ui.spriteMenu;

public enum SheetTools
{
    MAP("map"), FLATMAP("flatMap"), CANYONMAP("canyonMap"), CANYONBACKDROP("canyonBackdrop"), MESAMAP("mesaMap");

    public String name;
    public int tileSheetWidth;
    public int tileSheetHeight;
    SheetTools(String name)
    {
        this.name = name;
    }
}
