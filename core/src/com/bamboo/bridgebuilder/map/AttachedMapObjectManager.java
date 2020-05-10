package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

public class AttachedMapObjectManager
{
    private SpriteTool spriteTool;
    public Array<MapObject> attachedMapObjects;
    private Map map;

    public static int idIncrementer = 0;
    public int id = 0;

    public AttachedMapObjectManager(Map map, SpriteTool spriteTool, MapObject mapObject, MapSprite mapSprite)
    {
        this.map = map;
        this.spriteTool = spriteTool;
        this.attachedMapObjects = new Array<>();
        this.attachedMapObjects.add(mapObject);
        mapObject.attachedMapObjectManager = this;
        mapObject.id = idIncrementer ++;
        this.id = mapObject.id;
        addCopyOfMapObjectToAllOtherMapSpritesOfThisSpriteTool(mapObject, mapSprite);
        mapSprite.addAttachedMapObject(mapObject);
    }

    public void moveBy(float xOffset, float yOffset)
    {
        for(int i = 0; i < this.attachedMapObjects.size; i ++)
        {
            MapObject mapObject = this.attachedMapObjects.get(i);
            mapObject.setPosition(mapObject.position.x + xOffset, mapObject.position.y + yOffset);
        }
    }

    public void moveVerticeBy(int index, float xOffset, float yOffset)
    {
        if(!(this.attachedMapObjects.first() instanceof MapPolygon))
            return;
        MapPolygon mapPolygon = (MapPolygon) this.attachedMapObjects.first();
        float verticeX = mapPolygon.getVerticeX(index);
        float verticeY = mapPolygon.getVerticeY(index);
        mapPolygon.moveVertice(index, verticeX + xOffset, verticeY + yOffset);
        for(int i = 0; i < this.attachedMapObjects.size; i ++)
        {
            mapPolygon = (MapPolygon) this.attachedMapObjects.get(i);
            mapPolygon.setPosition(mapPolygon.polygon.getX(), mapPolygon.polygon.getY());
        }
    }

    public void removeAttachedMapObject(MapObject mapObject)
    {
        this.attachedMapObjects.removeValue(mapObject, true);

        for(int i = 0; i < this.map.layers.size; i ++)
        {
            Layer layer = this.map.layers.get(i);
            if (layer instanceof SpriteLayer)
            {
                SpriteLayer spriteLayer = (SpriteLayer) layer;
                for (int k = 0; k < spriteLayer.children.size; k++)
                {
                    MapSprite child = spriteLayer.children.get(k);
                    if (child.tool != this.spriteTool)
                        continue;

                    for(int s = 0; s < child.attachedMapObjects.size; s ++)
                    {
                        if(child.attachedMapObjects.get(s).id == mapObject.id)
                        {
                            child.attachedMapObjects.removeIndex(s);
                            s --;
                        }
                    }
                }
            }
        }
    }

    public void unselect(MapSprite mapSprite)
    {
        for(int i = 0; i < this.attachedMapObjects.size; i ++)
            this.attachedMapObjects.get(i).unselect();
    }

    public void addCopyOfMapObjectToAllMapSpritesOfThisSpriteToolThatDontContainIt(MapObject mapObject, MapSprite mapSprite)
    {
        for(int i = 0; i < this.map.layers.size; i ++)
        {
            Layer layer = this.map.layers.get(i);
            if(layer instanceof SpriteLayer)
            {
                SpriteLayer spriteLayer = (SpriteLayer) layer;
                for(int k = 0; k < spriteLayer.children.size; k ++)
                {
                    MapSprite child = spriteLayer.children.get(k);
                    if(child.tool != this.spriteTool)
                        continue;
                    boolean alreadyContains = false;
                    if(child.attachedMapObjects == null)
                        child.attachedMapObjects = new Array<>();
                    else
                    {
                        for(int s = 0; s < child.attachedMapObjects.size; s++)
                        {
                            if(child.attachedMapObjects.get(s).id == mapObject.id)
                            {
                                alreadyContains = true;
                                break;
                            }
                        }
                    }
                    if(!alreadyContains)
                        addCopyOfMapObjectToThisMapSprite(mapObject, child);
                }
            }
        }
    }

    /** Intended for using when creating a new attached map object. Since it is already a part of the MapSprite, add it to all others of that SpriteTool*/
    public void addCopyOfMapObjectToAllOtherMapSpritesOfThisSpriteTool(MapObject mapObject, MapSprite mapSprite)
    {
        for(int i = 0; i < this.map.layers.size; i ++)
        {
            Layer layer = this.map.layers.get(i);
            if(layer instanceof SpriteLayer)
            {
                SpriteLayer spriteLayer = (SpriteLayer) layer;
                for(int k = 0; k < spriteLayer.children.size; k ++)
                {
                    MapSprite child = spriteLayer.children.get(k);
                    if(child == mapSprite || child.tool != this.spriteTool)
                        continue;
                    else
                        addCopyOfMapObjectToThisMapSprite(mapObject, child);
                }
            }
        }
    }

    /** Intended for using when creating a new attached map object.*/
    public void addCopyOfMapObjectToAllMapSpritesOfThisSpriteTool(MapObject mapObject)
    {
        for(int i = 0; i < this.map.layers.size; i ++)
        {
            Layer layer = this.map.layers.get(i);
            if(layer instanceof SpriteLayer)
            {
                SpriteLayer spriteLayer = (SpriteLayer) layer;
                for(int k = 0; k < spriteLayer.children.size; k ++)
                {
                    MapSprite child = spriteLayer.children.get(k);
                    if(child.tool != this.spriteTool)
                        continue;
                    else
                        addCopyOfMapObjectToThisMapSprite(mapObject, child);
                }
            }
        }
    }

    public void addCopyOfMapObjectToThisMapSprite(MapObject mapObject, MapSprite mapSprite)
    {
        MapObject mapObjectCopy = mapObject.copy();
        float xOffset = mapObjectCopy.position.x - mapObjectCopy.attachedSprite.position.x;
        float yOffset = mapObjectCopy.position.y - mapObjectCopy.attachedSprite.position.y;
        mapObjectCopy.attachedSprite = mapSprite;
        mapObjectCopy.layer = mapSprite.layer;
        mapObjectCopy.setPosition(mapSprite.position.x + xOffset, mapSprite.position.y + yOffset);
        mapSprite.addAttachedMapObject(mapObjectCopy);
        this.attachedMapObjects.add(mapObjectCopy);
    }

    public void addCopiesOfAllMapObjectsToThisMapSprite(MapSprite mapSprite)
    {
        for(int i = 0; i < this.attachedMapObjects.size; i ++)
        {
            MapObject mapObject = this.attachedMapObjects.get(i);
            addCopyOfMapObjectToThisMapSprite(mapObject, mapSprite);
        }
    }
}
