package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.map.Layer;
import com.bamboo.bridgebuilder.map.LayerChild;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapSprite;

public class PasteItems implements Command
{
    private Array<LayerChild> cutItems;
    private Layer fromLayer;
    private Layer toLayer;
    Map fromMap;
    Map toMap;

    public PasteItems(Map map, Layer toLayer)
    {
        this.cutItems = new Array(map.editor.copiedItems);

        this.fromLayer = cutItems.first().layer;
        this.toLayer = toLayer;

        this.fromMap = cutItems.first().layer.map;
        this.toMap = map;
    }

    @Override
    public void execute()
    {
        for(int i = 0; i < cutItems.size; i ++)
            this.toLayer.children.add(cutItems.get(i));

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
