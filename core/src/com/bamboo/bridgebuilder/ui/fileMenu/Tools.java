package com.bamboo.bridgebuilder.ui.fileMenu;

public enum Tools
{
    BRUSH("brush"), RANDOM("random"), DEPTH("depth"), ERASER("eraser"), FILL("fill"), BIND("bind"), STAMP("stamp"), DRAWPOINT("point"), DRAWRECTANGLE("drawRectangle"), DRAWOBJECT("drawObject"), OBJECTVERTICESELECT("objectVerticeSelect"), BOXSELECT("boxSelect"), SELECT("select"), GRAB("grab"), GRADIENT("gradient"), BLOCKED("blocked"), SPRITEGRIDCOLORS("spriteGridColors"), PARALLAX("parallaxScrolling"), PERSPECTIVE("perspective"), TOP("top"), LINES("lines"), B2DR("b2dr");

    public String name;
    Tools(String name)
    {
        this.name = name;
    }
}
