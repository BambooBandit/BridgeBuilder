package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.map.*;

public class DeleteMapObjects implements Command
{
    private Array<MapObject> selectedObjects;
    private Array<MapObject> deletedObjects;
    private Layer selectedLayer;

    public DeleteMapObjects(Array<MapObject> selectedMapObjects, Layer selectedLayer)
    {
        if(selectedMapObjects.size > 0)
            this.selectedObjects = new Array<>(selectedMapObjects);
        this.selectedLayer = selectedLayer;
        this.deletedObjects = new Array<>();
    }

    @Override
    public void execute()
    {
        if(this.selectedLayer != null)
        {
            this.deletedObjects.clear();
            if(this.selectedLayer instanceof SpriteLayer)
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
                    ((MapPoint) mapObject).destroyLight();
                else if(mapObject instanceof MapPolygon)
                    ((MapPolygon) mapObject).destroyBody();
                mapObject.unselect();
            }
            this.selectedLayer.children.removeAll(this.selectedObjects, true);
            this.selectedLayer.map.propertyMenu.rebuild();
            this.selectedLayer.map.input.mouseMoved(Gdx.input.getX(), Gdx.input.getY());
        }
    }

    @Override
    public void undo()
    {
        if(this.selectedLayer != null)
        {
            if (this.selectedLayer instanceof SpriteLayer)
            {
                for(int i = 0; i < this.selectedObjects.size; i ++)
                {
                    MapObject mapObject = this.selectedObjects.get(i);
                    mapObject.attachedMapObjectManager.spriteTool.attachedMapObjectManagers.add(mapObject.attachedMapObjectManager);
                    mapObject.attachedMapObjectManager.addCopyOfMapObjectToAllMapSpritesOfThisSpriteTool(mapObject);
                    mapObject.attachedMapObjectManager.selectObjectOfParentSprite(mapObject.attachedSprite);
                }
                this.selectedObjects.clear();
                this.selectedObjects.addAll(this.selectedLayer.map.selectedObjects);

                this.selectedLayer.map.propertyMenu.rebuild();
                this.selectedLayer.map.input.mouseMoved(Gdx.input.getX(), Gdx.input.getY());
                return;
            }
        }
        for(int i = 0; i < this.deletedObjects.size; i ++)
        {
            MapObject mapObject = this.deletedObjects.get(i);
            this.selectedLayer.children.add(mapObject);
            mapObject.select();
        }
        this.selectedLayer.map.propertyMenu.rebuild();
        this.selectedLayer.map.input.mouseMoved(Gdx.input.getX(), Gdx.input.getY());
    }
}
