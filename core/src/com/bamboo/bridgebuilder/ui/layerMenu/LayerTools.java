package com.bamboo.bridgebuilder.ui.layerMenu;

public enum LayerTools
{
    NEWSPRITE("newSpriteLayer", null, LayerTypes.SPRITE, false), NEWOBJECT("newObjectLayer", null, LayerTypes.OBJECT, false), OBJECTVISIBILITY("objectLayer", "visible", null, true), MANYOBJECTNOTVISIBLE("objectLayer", "manyNotVisible", null, false), MANYOBJECTVISIBLE("objectLayer", "manyVisible", null, false);

    public String name;
    public String nameTop;
    public LayerTypes type;
    public boolean toggle;
    LayerTools(String name, String nameTop, LayerTypes type, boolean toggle)
    {
        this.name = name;
        this.nameTop = nameTop;
        this.type = type;
        this.toggle = toggle;
    }
}
