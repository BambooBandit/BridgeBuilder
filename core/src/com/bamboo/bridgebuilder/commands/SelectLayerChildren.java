package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.map.LayerChild;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapObject;
import com.bamboo.bridgebuilder.map.MapSprite;

public class SelectLayerChildren implements Command
{
    private Map map;
    private Array<LayerChild> hoveredChildren;
    private boolean ctrlHeld;

    private Array<MapSprite> oldSelectedSprites;
    private Array<MapObject> oldSelectedObjects;

    public SelectLayerChildren(Map map, float dragStartX, float dragStartY, float dragCurrentX, float dragCurrentY, boolean ctrlHeld)
    {
        this.map = map;
        this.ctrlHeld = ctrlHeld;

        Utils.boxSelectCommandVertices[0] = dragStartX;
        Utils.boxSelectCommandVertices[1] = dragStartY;
        Utils.boxSelectCommandVertices[2] = dragStartX;
        Utils.boxSelectCommandVertices[3] = dragCurrentY;
        Utils.boxSelectCommandVertices[4] = dragCurrentX;
        Utils.boxSelectCommandVertices[5] = dragStartY;
        Utils.boxSelectCommandVertices[6] = dragCurrentX;
        Utils.boxSelectCommandVertices[7] = dragCurrentY;

        this.hoveredChildren = new Array<>();

        for(int i = 0; i < map.selectedLayer.children.size; i ++)
        {
            LayerChild layerChild = (LayerChild) map.selectedLayer.children.get(i);
            if(layerChild.isHoveredOver(Utils.boxSelectCommandVertices))
                this.hoveredChildren.add(layerChild);
        }

    }

    @Override
    public void execute()
    {
        if(this.oldSelectedSprites == null)
            this.oldSelectedSprites = new Array<>(map.selectedSprites);
        else
        {
            this.oldSelectedSprites.clear();
            this.oldSelectedSprites.addAll(map.selectedSprites);
        }
        if(this.oldSelectedObjects == null)
            this.oldSelectedObjects = new Array<>(map.selectedObjects);
        else
        {
            this.oldSelectedObjects.clear();
            this.oldSelectedObjects.addAll(map.selectedObjects);
        }

        if(!ctrlHeld)
        {
            for (int i = 0; i < this.map.selectedSprites.size; i++)
            {
                this.map.selectedSprites.get(i).unselect();
                i--;
            }
            for (int i = 0; i < this.map.selectedObjects.size; i++)
            {
                this.map.selectedObjects.get(i).unselect();
                i--;
            }
        }
        for(int i = 0; i < this.hoveredChildren.size; i ++)
            this.hoveredChildren.get(i).select();
        this.map.propertyMenu.rebuild();
    }

    @Override
    public void undo()
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
        for(int i = 0; i < this.oldSelectedObjects.size; i ++)
            this.oldSelectedObjects.get(i).select();
        this.map.propertyMenu.rebuild();
    }
}
