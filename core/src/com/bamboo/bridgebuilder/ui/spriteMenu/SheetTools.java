package com.bamboo.bridgebuilder.ui.spriteMenu;

public enum SheetTools
{
    MAP("map"), FLATMAP("flatMap"), CANYONMAP("canyonMap"), CANYONBACKDROP("canyonBackdrop"), MESAMAP("mesaMap");

    public String name;
    SheetTools(String name)
    {
        this.name = name;
    }
}
