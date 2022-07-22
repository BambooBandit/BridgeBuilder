package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.map.*;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

/** Command pattern used for undo/redo. TODO pool them. */
public interface Command
{
    void execute();
    void undo();

    /** Exhaustive search to see if an action would cause a change or not. Use if quick efficient checks are not optimal. */
    static boolean shouldExecute(Map map, Class<? extends Command> command){
        if(command == SelectPolygonVertice.class)
        {
            boolean noneSelected = true;
            boolean noneHovered = true;
            for(int i = 0; i < map.selectedObjects.size; i ++)
            {
                MapObject mapObject = map.selectedObjects.get(i);
                if(mapObject instanceof MapPolygon)
                {
                    MapPolygon mapPolygon = (MapPolygon) mapObject;
                    if(mapPolygon.indexOfSelectedVertice != -1 && mapPolygon.indexOfHoveredVertice == mapPolygon.indexOfSelectedVertice)
                        return false;
                    if(mapPolygon.indexOfSelectedVertice != -1)
                        noneSelected = false;
                    if(mapPolygon.indexOfHoveredVertice != -1)
                        noneHovered = false;
                }
            }
            if(noneSelected && noneHovered)
                return false;
        }
        else if(command == SelectLayer.class)
        {
            return map.editAttachedMapSprite == null && (map.groupPolygons == null || map.selectedLayer != map.groupPolygons) && !Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT);
        }
        else if(command == SelectSecondaryLayer.class)
        {
            return map.editAttachedMapSprite == null && (map.groupPolygons == null || map.selectedLayer != map.groupPolygons) && Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT);
        }
        else if(command == CreateStairs.class)
        {
            return Utils.canBuildFenceFromSelectedSpriteTools(map);
        }
        else if(command == DeleteSelectedMapSprites.class)
        {
            boolean attachedParentSelected = false;
            if(map.editAttachedMapSprite != null)
            {
                for(int i = 0; i < map.selectedSprites.size; i ++)
                {
                    MapSprite mapSprite = map.selectedSprites.get(i);
                    if(mapSprite.attachedSprites != null)
                    {
                        attachedParentSelected = true;
                        break;
                    }
                }
            }
            return !attachedParentSelected;
        }
        else if(command == DrawMapSprite.class)
        {
            return !(map.editAttachedMapSprite != null && (map.selectedSprites.size != 1 || (map.selectedSprites.first().attachedSprites == null || !map.selectedSprites.first().attachedSprites.equals(map.selectedLayer))));
        }
        else if(command == PaintMapSprite.class)
        {
            return map.selectedLayer != null && map.selectedLayer instanceof SpriteLayer;
        }
        else if(command == DrawFence.class)
        {
            boolean selectedFencePost = false;
            SpriteTool spriteTool = map.getSpriteToolFromSelectedTools();
            if (spriteTool.hasAttachedMapObjects())
            {
                for(int i = 0; i < spriteTool.attachedMapObjectManagers.size; i ++)
                {
                    if(Utils.getPropertyField(spriteTool.attachedMapObjectManagers.get(i).properties, "fenceStart") != null)
                        selectedFencePost = true;
                }
            }
            return map.editor.fileMenu.toolPane.fence.selected && Utils.canBuildFenceFromSelectedSpriteTools(map) && !(map.editAttachedMapSprite != null && (map.selectedSprites.size != 1 || (map.selectedSprites.first().attachedSprites == null || !map.selectedSprites.first().attachedSprites.equals(map.selectedLayer)))) && selectedFencePost;
        }
        else if(command == DrawBranch.class)
        {
            return map.editor.fileMenu.toolPane.branch.selected && map.selectedLayer != null && map.selectedLayer instanceof ObjectLayer;
        }
        return true;
    }
}
