package com.bamboo.bridgebuilder.commands;

import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapSprite;
import com.bamboo.bridgebuilder.map.SpriteLayer;

public class MoveMapSpriteIndex implements Command
{
    private Map map;
    private int oldIndex;
    private MapSprite mapSprite;
    private boolean up, allTheWay;
    private boolean moved = false;

    public MoveMapSpriteIndex(Map map, MapSprite mapSprite, boolean up, boolean allTheWay)
    {
        this.map = map;
        this.mapSprite = mapSprite;
        this.up = up;
        this.allTheWay = allTheWay;
    }

    @Override
    public void execute()
    {
        if(up && !allTheWay)
            moveUp();
        else if(!up && !allTheWay)
            moveDown();
        else if(up && allTheWay)
            moveAllTheWayUp();
        else
            moveAllTheWayDown();

        if(map.editor.fileMenu.toolPane.depth.selected)
            map.colorizeDepth();
    }

    @Override
    public void undo()
    {
        if(!moved)
            return;
        if(up && !allTheWay)
            moveDown();
        else if(!up && !allTheWay)
            moveUp();
        else if(allTheWay)
            moveToIndex();

        if(map.editor.fileMenu.toolPane.depth.selected)
            map.colorizeDepth();
    }

    private void moveUp()
    {
        SpriteLayer layer = (SpriteLayer) this.mapSprite.layer;
        int index = layer.children.indexOf(this.mapSprite, true);
        if(index < layer.children.size - 1)
        {
            this.moved = true;
            layer.children.swap(index, index + 1);
        }
        else
            this.moved = false;
    }

    private void moveDown()
    {
        SpriteLayer layer = (SpriteLayer) this.mapSprite.layer;
        int index = layer.children.indexOf(this.mapSprite, true);
        if (index > 0)
        {
            this.moved = true;
            layer.children.swap(index, index - 1);
        }
        else
            this.moved = false;
    }

    private void moveAllTheWayUp()
    {
        SpriteLayer layer = (SpriteLayer) this.mapSprite.layer;
        this.oldIndex = layer.children.indexOf(this.mapSprite, true);
        layer.children.removeIndex(this.oldIndex);
        layer.children.add(this.mapSprite);
        this.moved = true;
    }

    private void moveAllTheWayDown()
    {
        SpriteLayer layer = (SpriteLayer) this.mapSprite.layer;
        this.oldIndex = layer.children.indexOf(this.mapSprite, true);
        layer.children.removeIndex(this.oldIndex);
        layer.children.insert(0, this.mapSprite);
        this.moved = true;
    }

    private void moveToIndex()
    {
        SpriteLayer layer = (SpriteLayer) this.mapSprite.layer;
        layer.children.removeValue(this.mapSprite, true);
        layer.children.insert(this.oldIndex, this.mapSprite);
        this.moved = true;
    }
}
