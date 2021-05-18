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

    private Array<LayerOverride> chainedCommands; // Used for keeping track of layer overrides

    public DeleteSelectedMapSprites(Array<MapSprite> selectedMapSprites, SpriteLayer selectedLayer)
    {
        if(selectedMapSprites.size > 0)
            this.selectedSprites = new Array<>(selectedMapSprites);
        this.selectedLayer = selectedLayer;
        this.deletedSprites = new IntMap();

        for(int i = 0; i < selectedMapSprites.size; i ++)
        {
            MapSprite selectedMapSprite = selectedMapSprites.get(i);
            if (selectedMapSprite.layerOverride != null)
            {
                LayerOverride layerOverride = new LayerOverride((SpriteLayer) selectedMapSprite.layerOverride, null, true);
                addCommandToChain(layerOverride);
            }
            if (selectedMapSprite.layerOverrideBack != null)
            {
                LayerOverride layerOverride = new LayerOverride((SpriteLayer) selectedMapSprite.layerOverrideBack, null, false);
                addCommandToChain(layerOverride);
            }
        }
    }

    @Override
    public void execute()
    {
        if (this.chainedCommands != null)
            for (int i = 0; i < this.chainedCommands.size; i++)
                this.chainedCommands.get(i).execute();

        if(this.selectedLayer != null)
        {
            this.deletedSprites.clear();
            for(int i = 0; i < this.selectedSprites.size; i ++)
            {
                MapSprite mapSprite = this.selectedSprites.get(i);
                if(mapSprite.fromEdgeSprites != null)
                {
                    for (int k = 0; k < mapSprite.fromEdgeSprites.size; k++)
                        mapSprite.fromEdgeSprites.get(k).toEdgeSprite = null;
                }
                if(mapSprite.fromFlickers != null)
                {
                    for (int k = 0; k < mapSprite.fromFlickers.size; k++)
                        mapSprite.fromFlickers.get(k).toFlicker = null;
                }
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
        while(this.deletedSprites.size > 0)
        {
            Iterator<IntMap.Entry<MapSprite>> iterator = this.deletedSprites.iterator();
            while (iterator.hasNext())
            {
                IntMap.Entry<MapSprite> entry = iterator.next();
                if(entry.key > this.selectedLayer.children.size)
                    continue;
                this.selectedLayer.children.insert(entry.key, entry.value);
                if(entry.value.fromEdgeSprites != null)
                {
                    for (int k = 0; k < entry.value.fromEdgeSprites.size; k++)
                        entry.value.fromEdgeSprites.get(k).toEdgeSprite = entry.value;
                }
                if(entry.value.fromFlickers != null)
                {
                    for (int k = 0; k < entry.value.fromFlickers.size; k++)
                        entry.value.fromFlickers.get(k).toFlicker = entry.value;
                }
                this.deletedSprites.remove(entry.key);
                entry.value.select();
            }
        }
        this.selectedLayer.map.propertyMenu.rebuild();
        this.selectedLayer.map.input.mouseMoved(Gdx.input.getX(), Gdx.input.getY());

        if(this.selectedLayer.map.editor.fileMenu.toolPane.depth.selected)
            this.selectedLayer.map.colorizeDepth();

        if(this.selectedLayer.map.editor.fileMenu.toolPane.spriteGridColors.selected)
            this.selectedLayer.map.updateLayerSpriteGrids();

        if (this.chainedCommands != null)
            for (int i = 0; i < this.chainedCommands.size; i++)
                this.chainedCommands.get(i).undo();
    }

    public void addCommandToChain(LayerOverride command)
    {
        if (this.chainedCommands == null)
            this.chainedCommands = new Array<>();
        this.chainedCommands.add(command);
    }
}
