package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.bamboo.bridgebuilder.map.MapObject;
import com.bamboo.bridgebuilder.map.MapSprite;
import com.bamboo.bridgebuilder.map.SpriteLayer;

import java.util.Iterator;

public class DeleteSelectedMapSprites implements Command
{
    private Array<MapSprite> selectedSprites;
    private IntMap<MapSprite> deletedSprites;
    private SpriteLayer selectedLayer;

    public DeleteSelectedMapSprites(Array<MapSprite> selectedMapSprites, SpriteLayer selectedLayer)
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
                if(mapSprite.attachedMapObjects != null)
                {
                    for (int k = 0; k < mapSprite.attachedMapObjects.size; k++)
                    {
                        MapObject mapObject = mapSprite.attachedMapObjects.get(k);
                        mapObject.attachedMapObjectManager.removeAttachedMapObject(mapObject);
                    }
                }
                this.deletedSprites.put(this.selectedLayer.children.indexOf(mapSprite, true), mapSprite);
            }
            this.selectedLayer.children.removeAll(this.selectedSprites, true);
            for(int i = 0; i < this.selectedSprites.size; i ++)
                this.selectedSprites.get(i).unselect();
            this.selectedLayer.map.propertyMenu.rebuild();
            this.selectedLayer.map.input.mouseMoved(Gdx.input.getX(), Gdx.input.getY());
        }

        if(this.selectedLayer.map.editor.fileMenu.toolPane.depth.selected)
            this.selectedLayer.map.colorizeDepth();

        if(this.selectedLayer.map.editor.fileMenu.toolPane.spriteGridColors.selected)
            this.selectedLayer.map.updateLayerSpriteGrids();
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

        if(this.selectedLayer.map.editor.fileMenu.toolPane.depth.selected)
            this.selectedLayer.map.colorizeDepth();

        if(this.selectedLayer.map.editor.fileMenu.toolPane.spriteGridColors.selected)
            this.selectedLayer.map.updateLayerSpriteGrids();
    }
}
