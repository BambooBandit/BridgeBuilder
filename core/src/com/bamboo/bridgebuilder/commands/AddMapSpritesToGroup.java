package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapPolygon;
import com.bamboo.bridgebuilder.map.MapSprite;

public class AddMapSpritesToGroup implements Command
{
    private Map map;

    private Array<MapSprite> mapSprites;
    private MapPolygon group;

    private CloseGroupDialog closeGroupDialog;


    public AddMapSpritesToGroup(Map map, MapPolygon group)
    {
        this.map = map;
        this.mapSprites = new Array<>();
        if(group.mapSprites == null)
            group.mapSprites = new Array<>();

        for(int i = 0; i < map.selectedSprites.size; i ++)
        {
            MapSprite selectedSprite = map.selectedSprites.get(i);
            if(!group.mapSprites.contains(selectedSprite, true))
                this.mapSprites.add(selectedSprite);
        }

        this.group = group;

        this.closeGroupDialog = new CloseGroupDialog(map);
    }

    @Override
    public void execute()
    {
        this.group.mapSprites.addAll(this.mapSprites);
        closeGroupDialog.execute();
    }

    @Override
    public void undo()
    {
        closeGroupDialog.undo();
        this.group.mapSprites.removeAll(this.mapSprites, true);
    }
}
