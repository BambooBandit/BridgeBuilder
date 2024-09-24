package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.map.LayerChild;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapPolygon;
import com.bamboo.bridgebuilder.ui.spriteMenu.GroupDialog;

public class CloseGroupDialog implements Command
{
    private Map map;

    private CheckBox selectedCheckBox;
    private Array<LayerChild> selectedLayerChildren;

    public CloseGroupDialog(Map map)
    {
        this.map = map;
        GroupDialog groupDialog = map.editor.fileMenu.buttonPane.groupDialog;
        if(groupDialog.shouldAdd())
            this.selectedCheckBox = groupDialog.addCheckBox;
        else if(groupDialog.shouldCreate())
            this.selectedCheckBox = groupDialog.createCheckBox;
        else if(groupDialog.shouldSelect())
            this.selectedCheckBox = groupDialog.selectCheckBox;


        if(map.groupPolygons != null && map.groupPolygons.children.size > 0)
        {
            this.selectedLayerChildren = new Array<>();
            for(int i = 0; i < map.groupPolygons.children.size; i ++)
            {
                MapPolygon groupPolygon = (MapPolygon) map.groupPolygons.children.get(i);
                if(groupPolygon.selected)
                    this.selectedLayerChildren.add(groupPolygon);
            }
        }
    }

    @Override
    public void execute()
    {
        this.map.editor.fileMenu.buttonPane.groupDialog.close();
        map.colorizeGroup();
    }

    @Override
    public void undo()
    {
        this.map.editor.fileMenu.buttonPane.groupDialog.open();
        this.selectedCheckBox.setChecked(true);
        for(int i = 0; i < this.selectedLayerChildren.size; i ++)
            this.selectedLayerChildren.get(i).select();
        map.colorizeGroup();
    }
}
