package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.map.LayerChild;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapObject;
import com.bamboo.bridgebuilder.map.MapSprite;

public class SelectMapSprite implements Command
{
    private Map map;
    private LayerChild hoveredChild;
    private boolean ctrlHeld;

    private Array<MapSprite> oldSelectedSprites;
    private Array<MapObject> oldSelectedObjects;

    public SelectMapSprite(Map map, LayerChild hoveredChild, boolean ctrlHeld)
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
            MapSprite hoveredMapSprite = (MapSprite) hoveredChild;
            if(this.ctrlHeld)
            {
                if(map.selectedSprites.contains(hoveredMapSprite, true))
                    map.selectedSprites.removeValue(hoveredMapSprite, true);
                else
                    map.selectedSprites.add(hoveredMapSprite);
            }
            else
            {
                map.selectedSprites.clear();
                map.selectedSprites.add(hoveredMapSprite);
            }
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
                    map.selectedObjects.removeValue(hoveredMapObject, true);
                else
                    map.selectedObjects.add(hoveredMapObject);
            }
            else
            {
                map.selectedObjects.clear();
                map.selectedObjects.add(hoveredMapObject);
            }
        }
    }

    @Override
    public void undo()
    {
        if(hoveredChild instanceof MapSprite)
        {
            map.selectedSprites.clear();
            map.selectedSprites.addAll(this.oldSelectedSprites);
        }
        else if(hoveredChild instanceof MapObject)
        {
            map.selectedObjects.clear();
            map.selectedObjects.addAll(this.oldSelectedObjects);
        }
    }
}
