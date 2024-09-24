package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapPoint;
import com.bamboo.bridgebuilder.map.ObjectLayer;

public class DrawBranch implements Command
{
    private Map map;
    private ObjectLayer layer;
    public MapPoint mapPoint = null;
    private float x;
    private float y;
    private MapPoint lastBranchPlacedOld;

    private Array<Command> chainedCommands; // Used for adding double linked connections in one execution

    private boolean doubleLinked = false;

    public DrawBranch(Map map, ObjectLayer layer, float x, float y)
    {
        this.map = map;
        this.layer = layer;
        this.x = x;
        this.y = y;
        this.lastBranchPlacedOld = map.lastBranchPlaced;

        if(map.editor.fileMenu.buttonPane.branchDialog.isDoubleLinked())
            doubleLinked = true;
    }

    @Override
    public void execute()
    {
        if(chainedCommands != null)
            chainedCommands.clear();
        if(this.mapPoint == null)
            this.mapPoint = new MapPoint(this.map, layer, this.x, this.y);
        if(this.map.lastBranchPlaced != null)
        {
            // connect last branch and this branch
            SnapMapPointBranch snapMapPointBranch = new SnapMapPointBranch(this.map.lastBranchPlaced, this.mapPoint);
            addCommandToChain(snapMapPointBranch);
            if(this.doubleLinked)
            {
                snapMapPointBranch = new SnapMapPointBranch(this.mapPoint, this.map.lastBranchPlaced);
                addCommandToChain(snapMapPointBranch);
            }
        }
        this.lastBranchPlacedOld = this.map.lastBranchPlaced;
        this.map.lastBranchPlaced = this.mapPoint;

        this.layer.addMapObject(this.mapPoint);

        if(this.chainedCommands != null)
            for(int i = 0; i < this.chainedCommands.size; i ++)
                this.chainedCommands.get(i).execute();
    }

    @Override
    public void undo()
    {
        this.layer.children.removeValue(this.mapPoint, true);
        this.map.lastBranchPlaced = this.lastBranchPlacedOld;

        if(this.chainedCommands != null)
            for(int i = 0; i < this.chainedCommands.size; i ++)
                this.chainedCommands.get(i).undo();
    }

    public void addCommandToChain(Command command)
    {
        if(this.chainedCommands == null)
            this.chainedCommands = new Array<>();
        this.chainedCommands.add(command);
    }
}
