package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.bamboo.bridgebuilder.map.*;

import java.util.Iterator;

public class DeleteMapSprites implements Command
{
    private Array<MapSprite> selectedSprites;
    private IntMap<MapSprite> deletedSprites;
    private SpriteLayer selectedLayer;

    public DeleteMapSprites(Array<MapSprite> selectedMapSprites, SpriteLayer selectedLayer)
    {
        if(selectedMapSprites.size > 0)
            this.selectedSprites = new Array<>(selectedMapSprites);
        this.selectedLayer = selectedLayer;
        this.deletedSprites = new IntMap();
    }

    @Override
    public void execute()
    {
        if(this.selectedLayer != null)
        {
            for(int i = 0; i < this.selectedSprites.size; i ++)
            {
                MapSprite mapSprite = this.selectedSprites.get(i);
                for(int k = 0; k < mapSprite.attachedMapObjects.size; k ++)
                {
                    MapObject mapObject = mapSprite.attachedMapObjects.get(k);
                    if(mapObject instanceof MapPoint)
                        ((MapPoint) mapObject).destroyLight();
                    else if(mapObject instanceof MapPolygon)
                        ((MapPolygon) mapObject).destroyBody();
                }
                this.deletedSprites.put(this.selectedLayer.children.indexOf(mapSprite, true), mapSprite);
            }
            this.selectedLayer.children.removeAll(this.selectedSprites, true);
            for(int i = 0; i < this.selectedSprites.size; i ++)
                this.selectedSprites.get(i).unselect();
            this.selectedLayer.map.propertyMenu.rebuild();
            this.selectedLayer.map.input.mouseMoved(Gdx.input.getX(), Gdx.input.getY());
        }
    }

    @Override
    public void undo()
    {
        Iterator<IntMap.Entry<MapSprite>> iterator = this.deletedSprites.iterator();
        while(iterator.hasNext())
        {
            IntMap.Entry<MapSprite> entry = iterator.next();
            this.selectedLayer.children.insert(entry.key, entry.value);
            entry.value.select();
        }
        this.selectedLayer.map.propertyMenu.rebuild();
        this.selectedLayer.map.input.mouseMoved(Gdx.input.getX(), Gdx.input.getY());
    }
}
