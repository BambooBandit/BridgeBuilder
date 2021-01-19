package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.map.LayerChild;
import com.bamboo.bridgebuilder.map.MapSprite;

public class SnapMapSpriteFlicker implements Command
{
    private LayerChild from;
    private MapSprite oldTo;
    private MapSprite to;

    public SnapMapSpriteFlicker(LayerChild from, MapSprite to)
    {
        this.from = from;
        this.oldTo = from.toFlicker;
        this.to = to;
    }

    @Override
    public void execute()
    {
        this.from.toFlicker = this.to;
        {
            if(this.to != null)
            {
                if (this.to.fromFlickers == null)
                    this.to.fromFlickers = new Array<>();
                this.to.fromFlickers.add(this.from);
            }
        }
    }

    @Override
    public void undo()
    {
        this.from.toFlicker = this.oldTo;
        if(this.to != null)
            this.to.fromFlickers.removeValue(this.from, true);
    }
}
