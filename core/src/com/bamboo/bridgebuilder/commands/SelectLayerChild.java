package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.map.LayerChild;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapObject;
import com.bamboo.bridgebuilder.map.MapSprite;

public class SelectLayerChild implements Command
{
    private Map map;
    private LayerChild hoveredChild;
    private boolean ctrlHeld;

    private Array<MapSprite> oldSelectedSprites;
    private Array<MapObject> oldSelectedObjects;

    public SelectLayerChild(Map map, LayerChild hoveredChild, boolean ctrlHeld)
    {
        this.map = map;
        this.ctrlHeld = ctrlHeld;
        this.hoveredChild = hoveredChild;
    }

    @Override
    public void execute()
    {
        if(hoveredChild instanceof MapSprite)
        {
            if(this.oldSelectedSprites == null)
                this.oldSelectedSprites = new Array<>(map.selectedSprites);
            else
            {
                this.oldSelectedSprites.clear();
                this.oldSelectedSprites.addAll(map.selectedSprites);
            }
            if(map.selectedObjects.size > 0)
            {
                if(this.oldSelectedObjects == null)
                    this.oldSelectedObjects = new Array<>(map.selectedObjects);
                else
                {
                    this.oldSelectedObjects.clear();
                    this.oldSelectedObjects.addAll(map.selectedObjects);
                }
            }
            MapSprite hoveredMapSprite = (MapSprite) hoveredChild;
            if(this.ctrlHeld)
            {
                if(map.selectedSprites.contains(hoveredMapSprite, true))
                    hoveredMapSprite.unselect();
                else
                    hoveredMapSprite.select();
            }
            else
            {
                for (int i = 0; i < this.map.selectedSprites.size; i++)
                {
                    this.map.selectedSprites.get(i).unselect();
                    i--;
                }
                hoveredMapSprite.select();
            }

            if(map.editor.fileMenu.toolPane.depth.selected)
                map.colorizeDepth();
        }
        else if(hoveredChild instanceof MapObject)
        {
            if(this.oldSelectedObjects == null)
                this.oldSelectedObjects = new Array<>(map.selectedObjects);
            else
            {
                this.oldSelectedObjects.clear();
                this.oldSelectedObjects.addAll(map.selectedObjects);
            }
            MapObject hoveredMapObject = (MapObject) hoveredChild;
            if(this.ctrlHeld)
            {
                if(map.selectedObjects.contains(hoveredMapObject, true))
                    hoveredMapObject.unselect();
                else
                    hoveredMapObject.select();
            }
            else
            {
                for(int i = 0; i < this.map.selectedObjects.size; i ++)
                {
                    this.map.selectedObjects.get(i).unselect();
                    i--;
                }
                hoveredMapObject.select();
            }
        }
        this.map.propertyMenu.rebuild();
    }

    @Override
    public void undo()
    {
        if(hoveredChild instanceof MapSprite)
        {
            for(int i = 0; i < map.selectedSprites.size; i ++)
            {
                map.selectedSprites.get(i).unselect();
                i--;
            }
            for(int i = 0; i < this.oldSelectedSprites.size; i ++)
                this.oldSelectedSprites.get(i).select();
            for(int i = 0; i < map.selectedObjects.size; i ++)
            {
                map.selectedObjects.get(i).unselect();
                i--;
            }
            if(this.oldSelectedObjects != null)
            {
                for (int i = 0; i < this.oldSelectedObjects.size; i++)
                    this.oldSelectedObjects.get(i).select();
            }

            if(map.editor.fileMenu.toolPane.depth.selected)
                map.colorizeDepth();
        }
        else if(hoveredChild instanceof MapObject)
        {
            for(int i = 0; i < map.selectedObjects.size; i ++)
            {
                map.selectedObjects.get(i).unselect();
                i--;
            }
            for(int i = 0; i < this.oldSelectedObjects.size; i ++)
                this.oldSelectedObjects.get(i).select();
        }
        this.map.propertyMenu.rebuild();
    }
}
