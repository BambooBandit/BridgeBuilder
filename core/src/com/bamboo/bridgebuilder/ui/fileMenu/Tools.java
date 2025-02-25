package com.bamboo.bridgebuilder.ui.fileMenu;

public enum Tools
{
    BRUSH("brush", "(B)"), BRANCH("branch", ""), RANDOM("random", ""), DEPTH("depth", ""), DRAWPOINT("point", "(P)"), DRAWRECTANGLE("drawRectangle", ""), DRAWOBJECT("drawObject", "(O)"), OBJECTVERTICESELECT("objectVerticeSelect", "(E)"), BOXSELECT("boxSelect", "(M)"), SELECT("select", "(V)"), GRAB("grab", "(H)"), GRADIENT("gradient", ""), SPRITEGRIDCOLORS("spriteGridColors", ""), PARALLAX("parallaxScrolling", "(L)"), TOP("top", "(T)"), LINES("lines", "(N)"), B2DR("b2dr", "(X)"), ATTACHEDSPRITES("attachedSprites", ""), SELECTATTACHEDSPRITES("selectAttachedSprites", ""), SPLAT("splat", ""), FENCE("fence", ""), STAIRS("stairs", ""), FILLED("filled", ""), GROUP("group", ""), PAINT("paint", ""), THIN("thin", ""), PATH("path", ""), STAPLE("staple", ""), EYEDROPPER("eyedropper", "(I)");

    public String name;
    public String shortcut;
    Tools(String name, String shortcut)
    {
        this.name = name;
        this.shortcut = shortcut;
    }
}
