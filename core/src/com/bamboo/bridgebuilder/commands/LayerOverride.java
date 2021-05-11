package com.bamboo.bridgebuilder.commands;

import com.bamboo.bridgebuilder.map.MapSprite;
import com.bamboo.bridgebuilder.map.SpriteLayer;

public class LayerOverride implements Command
{
    private SpriteLayer layer;
    private MapSprite oldSprite;
    private SpriteLayer oldLayer;
    private MapSprite mapSprite;
    private boolean front;

    public LayerOverride(SpriteLayer layer, MapSprite mapSprite, boolean front)
    {
        this.layer = layer;
        this.front = front;
        if(front)
            this.oldSprite = layer.overrideSprite;
        else
            this.oldSprite = layer.overrideSpriteBack;

        this.oldLayer = null;
        if(mapSprite != null)
        {
            if(front)
                this.oldLayer = (SpriteLayer) mapSprite.layerOverride;
            else
                this.oldLayer = (SpriteLayer) mapSprite.layerOverrideBack;
        }
        this.mapSprite = mapSprite;
    }

    @Override
    public void execute()
    {
        if(this.front)
        {
            if (this.layer.overrideSprite != null)
                this.layer.overrideSprite.layerOverride = null;
        }
        else
        {
            if (this.layer.overrideSpriteBack != null)
                this.layer.overrideSpriteBack.layerOverrideBack = null;
        }

        if(this.front)
            this.layer.overrideSprite = this.mapSprite;
        else
            this.layer.overrideSpriteBack = this.mapSprite;

        if(this.mapSprite != null)
        {
            if(this.front)
                this.mapSprite.layerOverride = this.layer;
            else
                this.mapSprite.layerOverrideBack = this.layer;
        }
    }

    @Override
    public void undo()
    {
        if(this.front)
            this.layer.overrideSprite = this.oldSprite;
        else
            this.layer.overrideSpriteBack = this.oldSprite;

        if(this.oldSprite != null)
        {
            if(this.front)
                this.oldSprite.layerOverride = this.layer;
            else
                this.oldSprite.layerOverrideBack = this.layer;
        }

        if(this.mapSprite != null)
        {
            if(this.front)
                this.mapSprite.layerOverride = this.oldLayer;
            else
                this.mapSprite.layerOverrideBack = this.oldLayer;
        }
    }
}
