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
    private boolean shiftHeld;

    private Array<MapSprite> oldSelectedSprites;
    private Array<MapObject> oldSelectedObjects;

    private boolean areAllHoveredAreSelected;

    public SelectLayerChildren(Map map, float dragStartX, float dragStartY, float dragCurrentX, float dragCurrentY, boolean shiftHeld)
    {
        this.map = map;
        this.shiftHeld = shiftHeld;

        Utils.boxSelectCommandVertices[0] = dragStartX;
        Utils.boxSelectCommandVertices[1] = dragStartY;
        Utils.boxSelectCommandVertices[2] = dragStartX;
        Utils.boxSelectCommandVertices[3] = dragCurrentY;
        Utils.boxSelectCommandVertices[4] = dragCurrentX;
        Utils.boxSelectCommandVertices[5] = dragCurrentY;
        Utils.boxSelectCommandVertices[6] = dragCurrentX;
        Utils.boxSelectCommandVertices[7] = dragStartY;

        this.hoveredChildren = new Array<>();

        this.areAllHoveredAreSelected = true;
        for(int i = 0; i < map.selectedLayer.children.size; i ++)
        {
            LayerChild layerChild = (LayerChild) map.selectedLayer.children.get(i);
            if(layerChild.isHoveredOver(Utils.boxSelectCommandVertices))
            {
                this.hoveredChildren.add(layerChild);
                if(!layerChild.selected)
                    this.areAllHoveredAreSelected = false;
            }
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

        if(!shiftHeld)
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
            for(int i = 0; i < this.hoveredChildren.size; i ++)
                this.hoveredChildren.get(i).select();
        }
        else
        {
            for(int i = 0; i < this.hoveredChildren.size; i ++)
            {
                if(this.areAllHoveredAreSelected)
                    this.hoveredChildren.get(i).unselect();
                else
                    this.hoveredChildren.get(i).select();
            }
        }
        this.map.propertyMenu.rebuild();

        if(map.editor.fileMenu.toolPane.depth.selected)
            map.colorizeDepth();

        this.map.editor.selectedCountTooltip.label.setText((map.selectedObjects.size + map.selectedSprites.size) + " selected");
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

        if(map.editor.fileMenu.toolPane.depth.selected)
            map.colorizeDepth();

        this.map.editor.selectedCountTooltip.label.setText((map.selectedObjects.size + map.selectedSprites.size) + " selected");
    }
}
