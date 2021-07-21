package com.bamboo.bridgebuilder.data;

import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.map.*;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.*;

import java.util.ArrayList;

public class MapSpriteData extends LayerChildData
{
    public float z;
    public float r, g, b, a;
    public String n; // Sprite name
    public String sN; // Sheet name
    public float ro, s, w, h; // rotation, scale, width, height
    public int loi; // layer override index. The index of the layer that has been overridden to always draw behind this sprite. It is also counting from 1 rather than 0 for JSON purposes.
    public int loiB; // back layer override index. ^
    public static int defaultColorValue = 1;
    public static int defaultScaleValue = 1;
    public boolean pa; // parent
    public float x1, y1, x2, y2, x3, y3, x4, y4;
    public long e; // eId - to edge mapSprite id
    public boolean f; // fence - Used to tell which parts of the attached sprites are fences
    public boolean iP; // ignore props - Used to tell whether or not to ignore properties for this sprite
    public ArrayList<PropertyData> p; // properties - instance specific properties
    public ArrayList<MapObjectData> o; // objects - this map sprite instance attached objects
    public ArrayList<Long> to; // toIDs - attached tool map object ID's

    public MapSpriteData() {}
    public MapSpriteData(MapSprite mapSprite)
    {
        super(mapSprite);
        this.z = mapSprite.z;
        this.n = mapSprite.tool.name;

        if(mapSprite.attachedSprites != null && mapSprite.attachedSprites.children.size > 0)
            pa = true;


        if(mapSprite.tool.sheet.name.startsWith("editor"))
        {
            this.sN = mapSprite.tool.sheet.name.substring(6);
            this.sN = Character.toLowerCase(this.sN.charAt(0)) + this.sN.substring(1);
        }
        else
            this.sN = mapSprite.tool.sheet.name;

        this.ro = mapSprite.sprite.getRotation();
        this.s = mapSprite.sprite.getScaleX() - defaultScaleValue;
        this.w = mapSprite.sprite.getWidth();
        this.h = mapSprite.sprite.getHeight();
        ColorPropertyField colorProperty = Utils.getLockedColorField("Tint", mapSprite.lockedProperties);
        this.r = colorProperty.getR() - defaultColorValue;
        this.g = colorProperty.getG() - defaultColorValue;
        this.b = colorProperty.getB() - defaultColorValue;
        this.a = colorProperty.getA() - defaultColorValue;
        if(mapSprite.layerOverride != null)
            this.loi = mapSprite.layer.map.layers.indexOf(mapSprite.layerOverride, true) + 1;
        else
            this.loi = 0;
        if(mapSprite.layerOverrideBack != null)
            this.loiB = mapSprite.layer.map.layers.indexOf(mapSprite.layerOverrideBack, true) + 1;
        else
            this.loiB = 0;

        this.x1 = mapSprite.x1Offset;
        this.y1 = mapSprite.y1Offset;
        this.x2 = mapSprite.x2Offset;
        this.y2 = mapSprite.y2Offset;
        this.x3 = mapSprite.x3Offset;
        this.y3 = mapSprite.y3Offset;
        this.x4 = mapSprite.x4Offset;
        this.y4 = mapSprite.y4Offset;

        if(mapSprite.toEdgeSprite != null)
            this.e = mapSprite.toEdgeSprite.id;

        LabelFieldPropertyValuePropertyField fenceProperty = (LabelFieldPropertyValuePropertyField) Utils.getPropertyField(mapSprite.lockedProperties, "Fence");
        this.f = fenceProperty.value.getText().equals("true");

        LabelFieldPropertyValuePropertyField ignoreProperty = (LabelFieldPropertyValuePropertyField) Utils.getPropertyField(mapSprite.lockedProperties, "IgnoreProps");
        this.iP = ignoreProperty.value.getText().equals("true");

        if(mapSprite.instanceSpecificProperties.size > 0)
            this.p = new ArrayList<>();
        for(int i = 0; i < mapSprite.instanceSpecificProperties.size; i ++)
        {
            PropertyField property = mapSprite.instanceSpecificProperties.get(i);
            if(property instanceof ColorPropertyField)
                this.p.add(new ColorPropertyFieldData((ColorPropertyField) property));
            else if(property instanceof LightPropertyField)
                this.p.add(new LightPropertyFieldData((LightPropertyField) property));
            else if(property instanceof FieldFieldPropertyValuePropertyField)
                this.p.add(new FieldFieldPropertyValuePropertyFieldData((FieldFieldPropertyValuePropertyField) property));
            else if(property instanceof LabelFieldPropertyValuePropertyField)
                this.p.add(new LabelFieldPropertyValuePropertyFieldData((LabelFieldPropertyValuePropertyField) property));
        }

        // tool object id's
        if(mapSprite.tool.attachedMapObjectManagers != null)
        {
            if(mapSprite.tool.attachedMapObjectManagers.size > 0)
                this.to = new ArrayList<>();
            for(int i = 0; i < mapSprite.tool.attachedMapObjectManagers.size; i ++)
            {
                AttachedMapObjectManager attachedMapObjectManager = mapSprite.tool.attachedMapObjectManagers.get(i);
                MapObject mapObject = attachedMapObjectManager.getMapObjectByParent(mapSprite);

                this.to.add(mapObject.id);
            }
        }

        if(mapSprite.attachedMapObjectManagers != null)
        {
            if(mapSprite.attachedMapObjectManagers.size > 0)
                this.o = new ArrayList<>();
            for (int i = 0; i < mapSprite.attachedMapObjectManagers.size; i++)
            {
                AttachedMapObjectManager attachedMapObjectManager = mapSprite.attachedMapObjectManagers.get(i);
                if(attachedMapObjectManager.attachedMapObjects.size == 0)
                    continue;
                MapObject mapObject = attachedMapObjectManager.attachedMapObjects.first();
                MapObjectData mapObjectData;
                if (mapObject instanceof MapPoint)
                    mapObjectData = new MapPointData((MapPoint) mapObject, attachedMapObjectManager.offsetX, attachedMapObjectManager.offsetY);
                else
                    mapObjectData = new MapPolygonData((MapPolygon) mapObject, attachedMapObjectManager.offsetX, attachedMapObjectManager.offsetY);
                this.o.add(mapObjectData);
            }
        }
    }
}
