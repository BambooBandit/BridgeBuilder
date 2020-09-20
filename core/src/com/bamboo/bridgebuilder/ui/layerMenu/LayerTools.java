package com.bamboo.bridgebuilder.ui.layerMenu;

public enum LayerTools
{
    NEWSPRITE("newSpriteLayer", null, LayerTypes.SPRITE), NEWOBJECT("newObjectLayer", null, LayerTypes.OBJECT), OBJECTVISIBILITY("objectLayer", "visible", null);

    public String name;
    public String nameTop;
    public LayerTypes type;
    LayerTools(String name, String nameTop, LayerTypes type)
    {
        this.name = name;
        this.nameTop = nameTop;
        this.type = type;
    }
}
