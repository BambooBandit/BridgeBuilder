package com.bamboo.bridgebuilder.ui.layerMenu;

public enum LayerTools
{
    NEWSPRITE("newSpriteLayer", LayerTypes.SPRITE), NEWOBJECT("newObjectLayer", LayerTypes.OBJECT);

    public String name;
    public LayerTypes type;
    LayerTools(String name, LayerTypes type)
    {
        this.name = name;
        this.type = type;
    }
}
