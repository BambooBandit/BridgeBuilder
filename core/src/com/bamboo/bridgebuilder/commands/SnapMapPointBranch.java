package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.map.MapPoint;

public class SnapMapPointBranch implements Command
{
    private MapPoint fromPoint;
    private Array<MapPoint> oldToPoints;
    private MapPoint toPoint;

    public SnapMapPointBranch(MapPoint fromPoint, MapPoint toPoint)
    {
        this.fromPoint = fromPoint;
        if(fromPoint.toBranchPoints != null)
            this.oldToPoints = new Array<>(fromPoint.toBranchPoints);
        this.toPoint = toPoint;
    }

    @Override
    public void execute()
    {
        // Unsnap
        if(this.toPoint != null && this.fromPoint.toBranchPoints != null && this.fromPoint.toBranchPoints.contains(this.toPoint, true))
        {
            this.fromPoint.toBranchPoints.removeValue(this.toPoint, true);
            this.toPoint.fromBranchPoints.removeValue(this.fromPoint, true);
            return;
        }

        // Snap
        if(this.toPoint != null)
        {
            if(this.fromPoint.toBranchPoints == null)
                this.fromPoint.toBranchPoints = new Array<>();
            this.fromPoint.toBranchPoints.add(this.toPoint);

            if (this.toPoint.fromBranchPoints == null)
                this.toPoint.fromBranchPoints = new Array<>();
            this.toPoint.fromBranchPoints.add(this.fromPoint);
        }
    }

    @Override
    public void undo()
    {
        this.fromPoint.toBranchPoints.clear();
        if(this.oldToPoints != null)
            this.fromPoint.toBranchPoints.addAll(this.oldToPoints);
        if(this.toPoint != null)
            this.toPoint.fromBranchPoints.removeValue(this.fromPoint, true);
    }
}
