package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.map.MapSprite;

public class SnapMapSpriteFlicker implements Command
{
    private MapSprite fromSprite;
    private MapSprite oldToSprite;
    private MapSprite toSprite;

    public SnapMapSpriteFlicker(MapSprite fromSprite, MapSprite toSprite)
    {
        this.fromSprite = fromSprite;
        this.oldToSprite = fromSprite.toFlickerSprite;
        this.toSprite = toSprite;
    }

    @Override
    public void execute()
    {
        this.fromSprite.toFlickerSprite = this.toSprite;
        {
            if(this.toSprite != null)
            {
                if (this.toSprite.fromFlickerSprites == null)
                    this.toSprite.fromFlickerSprites = new Array<>();
                this.toSprite.fromFlickerSprites.add(this.fromSprite);
            }
        }
    }

    @Override
    public void undo()
    {
        this.fromSprite.toFlickerSprite = this.oldToSprite;
        if(this.toSprite != null)
            this.toSprite.fromFlickerSprites.removeValue(this.fromSprite, true);
    }
}
