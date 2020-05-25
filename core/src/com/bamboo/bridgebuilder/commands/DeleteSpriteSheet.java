package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.bamboo.bridgebuilder.map.*;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteSheet;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

public class DeleteSpriteSheet implements Command
{
    private Array<MapSprite> mapSprites;
    private IntArray indexes;

    public Map map;
    public SpriteSheet spriteSheet;

    public DeleteSpriteSheet(Map map, SpriteSheet spriteSheet)
    {
        this.map = map;
        this.spriteSheet = spriteSheet;
        this.mapSprites = new Array<>();
        this.indexes = new IntArray();

        // Store how to re-add MapSprites
        for(int k = 0; k < map.layers.size; k ++)
        {
            Layer layer = map.layers.get(k);
            if(layer instanceof SpriteLayer)
            {
                SpriteLayer spriteLayer = (SpriteLayer) layer;
                for(int s = 0; s < spriteLayer.children.size; s++)
                {
                    MapSprite mapSprite = spriteLayer.children.get(s);
                    if(mapSprite.tool.sheet == spriteSheet)
                        addMapSprite(mapSprite);
                }
            }
        }
    }

    private void addMapSprite(MapSprite mapSprite)
    {
        int index = mapSprite.layer.children.indexOf(mapSprite, true);
        this.mapSprites.add(mapSprite);
        this.indexes.add(index);
    }

    @Override
    public void execute()
    {
        // Delete sprite sheet ui
        this.map.spriteMenu.spriteTable.removeActor(this.spriteSheet.label);
        for(int k = 0; k < this.spriteSheet.children.size; k ++)
        {
            Table child = this.spriteSheet.children.get(k);
            SpriteTool spriteTool = child.findActor("spriteTool");
            if(spriteTool.isSelected)
                spriteTool.unselect();
            this.map.spriteMenu.selectedSpriteTools.removeValue(spriteTool, true);
            this.map.spriteMenu.spriteTable.removeActor(child);
        }
        this.map.spriteMenu.spriteSheets.removeValue(this.spriteSheet, true);

        // Delete all MapSprites in use
        for(int i = 0; i < this.mapSprites.size; i ++)
        {
            MapSprite mapSprite = this.mapSprites.get(i);
            if(mapSprite.attachedMapObjects != null)
            {
                for (int k = 0; k < mapSprite.attachedMapObjects.size; k++)
                {
                    MapObject mapObject = mapSprite.attachedMapObjects.get(k);
                    if (mapObject instanceof MapPoint)
                        ((MapPoint) mapObject).destroyLight();
                    else if (mapObject instanceof MapPolygon)
                        ((MapPolygon) mapObject).destroyBody();
                }
            }
            mapSprite.layer.children.removeValue(mapSprite, true);
            mapSprite.unselect();
            this.map.propertyMenu.rebuild();
            this.map.input.mouseMoved(Gdx.input.getX(), Gdx.input.getY());
        }

        if(map.editor.fileMenu.toolPane.depth.selected)
            map.colorizeDepth();
    }

    @Override
    public void undo()
    {
        // Re-add sprite sheet ui
        this.map.spriteMenu.spriteTable.add(this.spriteSheet.label).width(0).row();
        for(int i = 0; i < this.spriteSheet.children.size; i ++)
        {
            Table child = this.spriteSheet.children.get(i);
            this.map.spriteMenu.spriteTable.add(child).grow();
            if((i + 1) % 5 == 0)
                this.map.spriteMenu.spriteTable.row();
        }
        this.map.spriteMenu.spriteSheets.add(this.spriteSheet);

        map.spriteMenu.spriteTable.row();
        map.spriteMenu.spriteTable.padBottom(500).row();

        // Re-add all the MapSprites
        for(int i = 0; i < this.mapSprites.size; i ++)
        {
            MapSprite mapSprite = this.mapSprites.get(i);
            int index = this.indexes.get(i);
            mapSprite.layer.children.insert(index, mapSprite);
        }
        this.map.propertyMenu.rebuild();
        this.map.input.mouseMoved(Gdx.input.getX(), Gdx.input.getY());

        if(map.editor.fileMenu.toolPane.depth.selected)
            map.colorizeDepth();
    }
}
