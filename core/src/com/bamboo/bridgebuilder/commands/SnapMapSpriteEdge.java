package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.map.MapSprite;

public class SnapMapSpriteEdge implements Command
{
    private MapSprite fromSprite;
    private MapSprite oldToSprite;
    private MapSprite toSprite;

    public SnapMapSpriteEdge(MapSprite fromSprite, MapSprite toSprite)
    {
        this.fromSprite = fromSprite;
        this.oldToSprite = fromSprite.toEdgeSprite;
        this.toSprite = toSprite;
    }

    @Override
    public void execute()
    {
        this.fromSprite.toEdgeSprite = this.toSprite;
        {
            if(this.toSprite != null)
            {
                if (this.toSprite.fromEdgeSprites == null)
                    this.toSprite.fromEdgeSprites = new Array<>();
                this.toSprite.fromEdgeSprites.add(this.fromSprite);
            }
        }
    }

    @Override
    public void undo()
    {
        this.fromSprite.toEdgeSprite = this.oldToSprite;
        if(this.toSprite != null)
            this.toSprite.fromEdgeSprites.removeValue(this.fromSprite, true);
    }
}
