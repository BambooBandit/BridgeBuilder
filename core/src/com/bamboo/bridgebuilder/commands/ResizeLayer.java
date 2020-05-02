package com.bamboo.bridgebuilder.commands;

import com.bamboo.bridgebuilder.map.Layer;

public class ResizeLayer implements Command
{
    private Layer layer;
    private int oldWidth;
    private int oldHeight;
    private float oldZ;
    private int newWidth;
    private int newHeight;
    private float newZ;
    private boolean down;
    private boolean right;

    public ResizeLayer(Layer layer, int oldWidth, int oldHeight, float oldZ, int newWidth, int newHeight, float newZ, boolean down, boolean right)
    {
        this.layer = layer;
        this.oldWidth = oldWidth;
        this.oldHeight = oldHeight;
        this.oldZ = oldZ;
        this.newWidth = newWidth;
        this.newHeight = newHeight;
        this.newZ = newZ;
        this.down = down;
        this.right = right;
    }

    @Override
    public void execute()
    {
        this.layer.setZ(this.newZ);
        this.layer.resize(this.newWidth, this.newHeight, this.down, this.right);
    }

    @Override
    public void undo()
    {
        this.layer.setZ(this.oldZ);
        this.layer.resize(this.oldWidth, this.oldHeight, this.down, this.right);
    }
}
