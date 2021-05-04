package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

public class AttachedMapObjectManager implements Comparable<AttachedMapObjectManager>
{
    public SpriteTool spriteTool; // null if instance specific
    public Array<MapObject> attachedMapObjects;
    private Map map;

    public static int idIncrementer = 0;

    public Array<PropertyField> properties;

    public MapObject cookieCutter;

    // Used for generating during opening new map
    public float offsetX, offsetY;

    // Created from an existing MapSprite
    public AttachedMapObjectManager(Map map, SpriteTool spriteTool, MapObject mapObject, MapSprite mapSprite)
    {
        this.map = map;
        this.spriteTool = spriteTool;
        this.attachedMapObjects = new Array<>();
        this.attachedMapObjects.add(mapObject);
        this.properties = new Array<>();
        this.cookieCutter = mapObject;
        this.offsetX = mapObject.getX() - mapSprite.getX();
        this.offsetY = mapObject.getY() - mapSprite.getY();
        mapObject.attachedMapObjectManager = this;
        mapObject.properties = this.properties;
        if(spriteTool != null)
            mapObject.attachedId = idIncrementer ++;
        else
            mapObject.attachedId = -1;
        addCopyOfMapObjectToAllOtherMapSpritesOfThisSpriteTool(mapObject, mapSprite);
        mapSprite.addAttachedMapObject(mapObject);
    }

    /** Created from a save file. */
    public AttachedMapObjectManager(Map map, SpriteTool spriteTool, MapObject mapObject, float offsetX, float offsetY)
    {
        this.map = map;
        this.spriteTool = spriteTool;
        this.attachedMapObjects = new Array<>();
        this.attachedMapObjects.add(mapObject);
        this.properties = new Array<>();
        this.cookieCutter = mapObject;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        mapObject.attachedMapObjectManager = this;
        mapObject.properties = this.properties;
        if(spriteTool != null)
            mapObject.attachedId = idIncrementer ++;
        else
            mapObject.attachedId = -1;
    }

    public MapObject getMapObjectByParent(MapSprite mapSprite)
    {
        for(int i = 0; i < attachedMapObjects.size; i ++)
        {
            MapObject mapObject = attachedMapObjects.get(i);
            if(mapObject.attachedSprite == mapSprite)
                return mapObject;
        }
        return null;
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

            if(mapObject.attachedSprite != null)
            {
                this.offsetX = mapObject.getX() - mapObject.attachedSprite.getX();
                this.offsetY = mapObject.getY() - mapObject.attachedSprite.getY();
            }
        }
    }

    public void moveVerticeBy(int index, float xOffset, float yOffset)
    {
        if(!(this.cookieCutter instanceof MapPolygon))
            return;
        MapPolygon mapPolygon = (MapPolygon) this.cookieCutter;
        float verticeX = mapPolygon.getVerticeX(index);
        float verticeY = mapPolygon.getVerticeY(index);
        mapPolygon.moveVertice(index, verticeX + xOffset, verticeY + yOffset);
        for(int i = 0; i < this.attachedMapObjects.size; i ++)
        {
            mapPolygon = (MapPolygon) this.attachedMapObjects.get(i);
            mapPolygon.setPosition(mapPolygon.x, mapPolygon.y);
        }
        this.offsetX = mapPolygon.getX() - mapPolygon.attachedSprite.getX();
        this.offsetY = mapPolygon.getY() - mapPolygon.attachedSprite.getY();
    }

    public boolean removeAttachedMapObject(MapObject mapObject)
    {
        if(mapObject instanceof MapPoint)
            ((MapPoint) mapObject).destroyLight();
        else if(mapObject instanceof MapPolygon)
            ((MapPolygon) mapObject).destroyBody();
        boolean removed = this.attachedMapObjects.removeValue(mapObject, true);
        if(mapObject.selected)
            mapObject.unselect();
        return removed;
    }

    public boolean deleteAttachedMapObjectFromAll(MapObject mapObject)
    {
        boolean removed = this.attachedMapObjects.removeValue(mapObject, true);
        if(!removed)
            return false;

        if(mapObject instanceof MapPoint)
            ((MapPoint) mapObject).destroyLight();
        else if(mapObject instanceof MapPolygon)
            ((MapPolygon) mapObject).destroyBody();

        if(this.spriteTool == null)
            return removed;

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
                        if(child.attachedMapObjects.get(s).attachedId == mapObject.attachedId)
                        {
                            MapObject attachedMapObject = child.attachedMapObjects.get(s);
                            removeAttachedMapObject(attachedMapObject);
                            child.attachedMapObjects.removeIndex(s);
                            s --;
                        }
                    }
                }
            }
        }
        return removed;
    }

    public boolean deleteAttachedMapObjectFromMapSprite(MapObject mapObject, MapSprite mapSprite)
    {
        boolean removed = this.attachedMapObjects.removeValue(mapObject, true);
        if(!removed)
            return false;

        if(mapObject instanceof MapPoint)
            ((MapPoint) mapObject).destroyLight();
        else if(mapObject instanceof MapPolygon)
            ((MapPolygon) mapObject).destroyBody();

        for(int s = 0; s < mapSprite.attachedMapObjects.size; s ++)
        {
            boolean remove = false;
            if(mapObject.attachedId == -1)
            {
                if(mapObject == mapSprite.attachedMapObjects.get(s))
                    remove = true;
            }
            else if(mapSprite.attachedMapObjects.get(s).attachedId == mapObject.attachedId)
                remove = true;
            if(remove)
            {
                MapObject attachedMapObject = mapSprite.attachedMapObjects.get(s);
                removeAttachedMapObject(attachedMapObject);
                mapSprite.attachedMapObjects.removeIndex(s);
                s --;
            }
        }
        return removed;
    }

    public void unselect(MapSprite mapSprite)
    {
        for(int i = 0; i < this.attachedMapObjects.size; i ++)
            this.attachedMapObjects.get(i).unselect();
    }

//    public void addCopyOfMapObjectToAllMapSpritesOfThisSpriteToolThatDontContainIt(MapObject mapObject, MapSprite mapSprite)
//    {
//        for(int i = 0; i < this.map.layers.size; i ++)
//        {
//            Layer layer = this.map.layers.get(i);
//            if(layer instanceof SpriteLayer)
//            {
//                SpriteLayer spriteLayer = (SpriteLayer) layer;
//                for(int k = 0; k < spriteLayer.children.size; k ++)
//                {
//                    MapSprite child = spriteLayer.children.get(k);
//                    if(child.tool != this.spriteTool)
//                        continue;
//                    boolean alreadyContains = false;
//                    if(child.attachedMapObjects == null)
//                        child.attachedMapObjects = new Array<>();
//                    else
//                    {
//                        for(int s = 0; s < child.attachedMapObjects.size; s++)
//                        {
//                            if(child.attachedMapObjects.get(s).id == mapObject.id)
//                            {
//                                alreadyContains = true;
//                                break;
//                            }
//                        }
//                    }
//                    if(!alreadyContains)
//                        addCopyOfMapObjectToThisMapSprite(mapObject, child);
//                }
//            }
//        }
//    }

    /** Intended for using when creating a new attached map object. Since it is already a part of the MapSprite, add it to all others of that SpriteTool*/
    public void addCopyOfMapObjectToAllOtherMapSpritesOfThisSpriteTool(MapObject mapObject, MapSprite mapSprite)
    {
        if(this.spriteTool == null)
            return;

        for(int i = 0; i < this.map.layers.size; i ++)
        {
            Layer layer = this.map.layers.get(i);
            if(layer instanceof SpriteLayer)
            {
                SpriteLayer spriteLayer = (SpriteLayer) layer;
                for(int k = 0; k < spriteLayer.children.size; k ++)
                {
                    MapSprite child = spriteLayer.children.get(k);
                    if(child.attachedSprites != null)
                    {
                        for(int s = 0; s < child.attachedSprites.children.size; s ++)
                        {
                            MapSprite attached = child.attachedSprites.children.get(s);
                            if(attached == mapSprite || attached.tool != this.spriteTool)
                                continue;
                            else
                                addCopyOfMapObjectToThisMapSprite(mapObject, attached);
                        }
                    }
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
        if(this.spriteTool == null)
            return;

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
        mapObjectCopy.attachedSprite = mapSprite;
        mapObjectCopy.layer = mapSprite.layer;
        mapObjectCopy.setPosition(mapSprite.getX() + offsetX, mapSprite.getY() + offsetY);
        mapObjectCopy.setOriginBasedOnParentSprite();
        mapSprite.addAttachedMapObject(mapObjectCopy);
        this.attachedMapObjects.add(mapObjectCopy);
    }

    public void addCopyOfMapObjectToThisMapSprite(MapSprite mapSprite)
    {
        if(this.attachedMapObjects == null)
            return;
        addCopyOfMapObjectToThisMapSprite(this.cookieCutter, mapSprite);
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

    @Override
    public int compareTo(AttachedMapObjectManager o) {
        return this.attachedMapObjects.first().compareTo(o.attachedMapObjects.first());
    }
}
