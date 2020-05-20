package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

public class AttachedMapObjectManager
{
    private SpriteTool spriteTool;
    public Array<MapObject> attachedMapObjects;
    private Map map;

    public static int idIncrementer = 0;
    public int id = 0;

    public Array<PropertyField> properties;

    public AttachedMapObjectManager(Map map, SpriteTool spriteTool, MapObject mapObject, MapSprite mapSprite)
    {
        this.map = map;
        this.spriteTool = spriteTool;
        this.attachedMapObjects = new Array<>();
        this.attachedMapObjects.add(mapObject);
        this.properties = new Array<>();
        mapObject.attachedMapObjectManager = this;
        mapObject.properties = this.properties;
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
            double dragAngle = Utils.getAngleDegree(0, 0, xOffset, yOffset);
            float dragAmount = (float) Math.sqrt(Math.pow(xOffset, 2) + Math.pow(yOffset, 2));
            float rotation = Utils.radianAngleFix((float) Math.toRadians(dragAngle));
            float rotationX = (float) (Math.cos(rotation) * dragAmount);
            float rotationY = (float) (Math.sin(rotation) * dragAmount);
            mapObject.setPosition(mapObject.getX() + rotationX, mapObject.getY() + rotationY);
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

    public boolean removeAttachedMapObject(MapObject mapObject)
    {
        if(mapObject instanceof MapPoint)
            ((MapPoint) mapObject).destroyLight();
        else if(mapObject instanceof MapPolygon)
            ((MapPolygon) mapObject).destroyBody();
        boolean removed = this.attachedMapObjects.removeValue(mapObject, true);

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
                            MapObject attachedMapObject = child.attachedMapObjects.get(s);
                            if(attachedMapObject instanceof MapPoint)
                                ((MapPoint) attachedMapObject).destroyLight();
                            else if(attachedMapObject instanceof MapPolygon)
                                ((MapPolygon) attachedMapObject).destroyBody();
                            child.attachedMapObjects.removeIndex(s);
                            s --;
                        }
                    }
                }
            }
        }
        return removed;
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
        float xOffset = mapObjectCopy.getX() - mapObjectCopy.attachedSprite.getX();
        float yOffset = mapObjectCopy.getY() - mapObjectCopy.attachedSprite.getY();
        mapObjectCopy.attachedSprite = mapSprite;
        mapObjectCopy.layer = mapSprite.layer;
        mapObjectCopy.setPosition(mapSprite.getX() + xOffset, mapSprite.getY() + yOffset);
        mapObjectCopy.setOriginBasedOnParentSprite();
        mapSprite.addAttachedMapObject(mapObjectCopy);
        this.attachedMapObjects.add(mapObjectCopy);
    }

    public void addCopyOfMapObjectToThisMapSprite(MapSprite mapSprite)
    {
        if(this.attachedMapObjects == null)
            return;
        addCopyOfMapObjectToThisMapSprite(this.attachedMapObjects.first(), mapSprite);
    }

    public void selectObjectOfParentSprite(MapSprite mapSprite)
    {
        for(int i = 0; i < this.attachedMapObjects.size; i ++)
        {
            MapObject mapObject = this.attachedMapObjects.get(i);
            if(mapObject.attachedSprite == mapSprite)
                mapObject.select();
        }
    }
}