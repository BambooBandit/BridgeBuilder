package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.map.*;

public class DeleteMapObjects implements Command
{
    private Array<MapObject> selectedObjects;
    private Array<MapObject> deletedObjects;
    private Layer selectedLayer;
    private boolean attached;

    public DeleteMapObjects(Array<MapObject> selectedMapObjects, Layer selectedLayer)
    {
        if(selectedMapObjects.size > 0)
            this.selectedObjects = new Array<>(selectedMapObjects);
        this.selectedLayer = selectedLayer;
        this.deletedObjects = new Array<>();
        if(this.selectedLayer instanceof SpriteLayer)
        {
            attached = true;
            for (int i = 0; i < selectedMapObjects.size; i++)
            {
                if (selectedMapObjects.get(i).layer != selectedLayer)
                {
                    attached = false;
                    return;
                }
            }
        }
    }

    @Override
    public void execute()
    {
        if(this.selectedLayer != null)
        {
            this.deletedObjects.clear();
            if(attached)
            {
                if(this.selectedObjects.size > 0)
                {
                    this.deletedObjects.addAll(this.selectedObjects);
                    for(int i = 0; i < this.selectedObjects.size; i ++)
                        this.selectedObjects.get(i).unselect();
                    for(int i = 0; i < this.selectedObjects.size; i ++)
                    {
                        MapObject mapObject = this.selectedObjects.get(i);
                        mapObject.attachedSprite.removeAttachedMapObject(mapObject);
                    }
                    this.selectedLayer.map.propertyMenu.rebuild();
                    this.selectedLayer.map.input.mouseMoved(Gdx.input.getX(), Gdx.input.getY());
                    return;
                }
            }
            this.deletedObjects.addAll(this.selectedObjects);
            for(int i = 0; i < this.selectedObjects.size; i ++)
            {
                MapObject mapObject = this.selectedObjects.get(i);
                if(mapObject instanceof MapPoint)
                {
                    MapPoint mapPoint = (MapPoint) mapObject;
                    mapPoint.destroyLight();

                    if(mapPoint.fromBranchPoints != null)
                    {
                        for (int k = 0; k < mapPoint.fromBranchPoints.size; k++)
                            mapPoint.fromBranchPoints.get(k).toBranchPoints.removeValue(mapPoint, true);
                    }
                }
                else if(mapObject instanceof MapPolygon)
                {
                    MapPolygon mapPolygon = (MapPolygon) mapObject;
                    mapPolygon.destroyBody();
                }
                mapObject.unselect();
            }
            this.selectedLayer.map.editor.selectedCountTooltip.label.setText((selectedLayer.map.selectedObjects.size + selectedLayer.map.selectedSprites.size) + " selected");

            this.selectedLayer.map.colorizeGroup();
            this.selectedLayer.children.removeAll(this.selectedObjects, true);
            this.selectedLayer.map.propertyMenu.rebuild();
            this.selectedLayer.map.input.mouseMoved(Gdx.input.getX(), Gdx.input.getY());
            if(this.selectedLayer.map.editor.fileMenu.toolPane.spriteGridColors.selected)
                this.selectedLayer.map.updateLayerSpriteGrids();
        }
    }

    @Override
    public void undo()
    {
        if(this.selectedLayer != null)
        {
            if (attached)
            {
                for(int i = 0; i < this.selectedObjects.size; i ++)
                {
                    MapObject mapObject = this.selectedObjects.get(i);
                    if(mapObject.attachedMapObjectManager.spriteTool != null)
                    {
                        mapObject.attachedMapObjectManager.spriteTool.attachedMapObjectManagers.add(mapObject.attachedMapObjectManager);
                        mapObject.attachedMapObjectManager.addCopyOfMapObjectToAllMapSpritesOfThisSpriteTool(mapObject);
                    }
                    else
                    {
                        mapObject.attachedSprite.attachedMapObjectManagers.add(mapObject.attachedMapObjectManager);
                        mapObject.attachedSprite.addAttachedMapObject(mapObject);
                        mapObject.layer = mapObject.attachedSprite.layer;
                        mapObject.attachedMapObjectManager.attachedMapObjects.add(mapObject);
                    }
                    mapObject.attachedMapObjectManager.selectObjectOfParentSprite(mapObject.attachedSprite);
                }
                this.selectedObjects.clear();
                this.selectedObjects.addAll(this.selectedLayer.map.selectedObjects);

                this.selectedLayer.map.propertyMenu.rebuild();
                this.selectedLayer.map.input.mouseMoved(Gdx.input.getX(), Gdx.input.getY());

                if(this.selectedLayer.map.editor.fileMenu.toolPane.spriteGridColors.selected)
                    this.selectedLayer.map.updateLayerSpriteGrids();
                return;
            }
        }
        for(int i = 0; i < this.deletedObjects.size; i ++)
        {
            MapObject mapObject = this.deletedObjects.get(i);
            if(selectedLayer instanceof ObjectLayer)
            {
                if(mapObject instanceof MapPoint)
                {
                    MapPoint mapPoint = (MapPoint) mapObject;
                    if(mapPoint.fromBranchPoints != null)
                    {
                        for (int k = 0; k < mapPoint.fromBranchPoints.size; k++)
                            mapPoint.fromBranchPoints.get(k).toBranchPoints.add(mapPoint);
                    }
                }
            }
            this.selectedLayer.children.add(mapObject);
            mapObject.select();
        }
        this.selectedLayer.map.editor.selectedCountTooltip.label.setText((selectedLayer.map.selectedObjects.size + selectedLayer.map.selectedSprites.size) + " selected");

        this.selectedLayer.map.propertyMenu.rebuild();
        this.selectedLayer.map.input.mouseMoved(Gdx.input.getX(), Gdx.input.getY());
        this.selectedLayer.map.colorizeGroup();
        if(this.selectedLayer.map.editor.fileMenu.toolPane.spriteGridColors.selected)
            this.selectedLayer.map.updateLayerSpriteGrids();
    }
}
