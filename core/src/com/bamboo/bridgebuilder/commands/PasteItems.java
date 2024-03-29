package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.map.*;

public class PasteItems implements Command
{
    private Array<LayerChild> cutItems;
    private Layer fromLayer;
    private Layer toLayer;
    Map fromMap;
    Map toMap;
    MapSprite oldParent;

    public PasteItems(Map map, Layer toLayer)
    {
        this.cutItems = new Array(map.editor.copiedItems);


        this.fromLayer = cutItems.first().layer;
        this.toLayer = toLayer;

        this.fromMap = cutItems.first().layer.map;
        this.toMap = map;

        if(toMap.editAttachedMapSprite != null && cutItems.first() instanceof MapSprite)
        {
            oldParent = ((MapSprite) cutItems.first()).parentSprite;
        }
    }

    @Override
    public void execute()
    {
        if(cutItems.first() instanceof MapSprite)
        {
            for(int i = 0; i < cutItems.size; i ++)
            {
                MapSprite mapSprite = (MapSprite) cutItems.get(i);
                if (mapSprite.attachedMapObjects != null)
                {
                    for (int k = 0; k < mapSprite.attachedMapObjects.size; k++)
                    {
                        MapObject mapObject = mapSprite.attachedMapObjects.get(k);
                        mapObject.attachedMapObjectManager.attachedMapObjects.add(mapObject);
                    }
                }
            }
        }

        for(int i = 0; i < cutItems.size; i ++)
            this.toLayer.children.add(cutItems.get(i));
        if(this.toLayer instanceof ObjectLayer)
            ((ObjectLayer) this.toLayer).children.sort();

        if(toMap.editAttachedMapSprite != null && cutItems.first() instanceof MapSprite)
        {
            for(int i = 0; i < cutItems.size; i ++)
            {
                MapSprite mapSprite = (MapSprite) cutItems.get(i);
                if(mapSprite == toMap.editAttachedMapSprite)
                    continue;
                mapSprite.parentSprite = toMap.editAttachedMapSprite;
                toMap.selectedSprites.first().updateBounds();
            }
        }

        fromMap.editor.copiedItems.clear();

        convertToLayerAndMapAndMapSheet(toLayer, toMap);

        this.toLayer.map.propertyMenu.rebuild();
        this.toLayer.map.input.mouseMoved(Gdx.input.getX(), Gdx.input.getY());

        if(this.toLayer.map.editor.fileMenu.toolPane.depth.selected)
            this.toLayer.map.colorizeDepth();

        if(this.toLayer.map.editor.fileMenu.toolPane.spriteGridColors.selected)
            this.toLayer.map.updateLayerSpriteGrids();
    }

    @Override
    public void undo()
    {
        if(toMap.editAttachedMapSprite != null && cutItems.first() instanceof MapSprite)
        {
            for(int i = 0; i < cutItems.size; i ++)
            {
                MapSprite mapSprite = (MapSprite) cutItems.get(i);
                if(mapSprite == toMap.editAttachedMapSprite)
                    continue;
                mapSprite.parentSprite = oldParent;
                toMap.selectedSprites.first().updateBounds();
            }
        }

        for(int i = 0; i < cutItems.size; i ++)
            this.toLayer.children.removeValue(cutItems.get(i), true);

        fromMap.editor.copiedItems.addAll(cutItems);

        convertToLayerAndMapAndMapSheet(fromLayer, fromMap);

        this.toLayer.map.propertyMenu.rebuild();
        this.toLayer.map.input.mouseMoved(Gdx.input.getX(), Gdx.input.getY());

        if(this.toLayer.map.editor.fileMenu.toolPane.depth.selected)
            this.toLayer.map.colorizeDepth();

        if(this.toLayer.map.editor.fileMenu.toolPane.spriteGridColors.selected)
            this.toLayer.map.updateLayerSpriteGrids();
    }

    private void convertToLayerAndMapAndMapSheet(Layer toLayer, Map toMap)
    {
        for(int i = 0; i < cutItems.size; i ++)
        {
            LayerChild layerChild = cutItems.get(i);
            convertItemToLayerAndMapAndMapSheet(layerChild, toLayer, toMap);
        }
    }

    private void convertItemToLayerAndMapAndMapSheet(LayerChild layerChild, Layer toLayer, Map toMap)
    {
        if(layerChild.map != toMap)
        {
            layerChild.setID(layerChild.map.getAndIncrementId());
        }

        layerChild.map = toMap;
        layerChild.layer = toLayer;
        if(layerChild instanceof MapSprite)
        {
            MapSprite mapSprite = (MapSprite) layerChild;
            mapSprite.tool = toMap.spriteMenu.getSpriteTool(mapSprite.tool.name, mapSprite.tool.sheet.name);

            if(mapSprite.attachedSprites != null)
            {
                for(int k = 0; k < mapSprite.attachedSprites.children.size; k ++)
                {
                    MapSprite attachedMapSprite = mapSprite.attachedSprites.children.get(k);
                    if(attachedMapSprite == mapSprite)
                        continue;
                    convertItemToLayerAndMapAndMapSheet(attachedMapSprite, toLayer, toMap);
                }
            }
        }
    }
}
