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
    public float rot, scl, w, h;
    public int loi; // layer override index. The index of the layer that has been overridden to always draw behind this sprite. It is also counting from 1 rather than 0 for JSON purposes.
    public static int defaultColorValue = 1;
    public static int defaultScaleValue = 1;
    public boolean parent;
    public float x1, y1, x2, y2, x3, y3, x4, y4;
    public long eId; // to edge mapSprite id
    public boolean fence; // Used to tell which parts of the attached sprites are fences
    public boolean ignoreProps; // Used to tell whether or not to ignore properties for this sprite
    public ArrayList<PropertyData> props; // instance specific properties
    public ArrayList<MapObjectData> objs; // this map sprite instance attached objects
    public ArrayList<Long> toIDs; // attached tool map object ID's

    public MapSpriteData() {}
    public MapSpriteData(MapSprite mapSprite)
    {
        super(mapSprite);
        this.z = mapSprite.z;
        this.n = mapSprite.tool.name;

        if(mapSprite.attachedSprites != null && mapSprite.attachedSprites.children.size > 0)
            parent = true;

        if(mapSprite.tool.sheet.name.startsWith("editor"))
        {
            this.sN = mapSprite.tool.sheet.name.substring(6);
            this.sN = Character.toLowerCase(this.sN.charAt(0)) + this.sN.substring(1);
        }
        else
            this.sN = mapSprite.tool.sheet.name;

        this.rot = mapSprite.sprite.getRotation();
        this.scl = mapSprite.sprite.getScaleX() - defaultScaleValue;
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

        this.x1 = mapSprite.x1Offset;
        this.y1 = mapSprite.y1Offset;
        this.x2 = mapSprite.x2Offset;
        this.y2 = mapSprite.y2Offset;
        this.x3 = mapSprite.x3Offset;
        this.y3 = mapSprite.y3Offset;
        this.x4 = mapSprite.x4Offset;
        this.y4 = mapSprite.y4Offset;

        if(mapSprite.toEdgeSprite != null)
            this.eId = mapSprite.toEdgeSprite.id;

        LabelFieldPropertyValuePropertyField fenceProperty = (LabelFieldPropertyValuePropertyField) Utils.getPropertyField(mapSprite.lockedProperties, "Fence");
        this.fence = fenceProperty.value.getText().equals("true");

        LabelFieldPropertyValuePropertyField ignoreProperty = (LabelFieldPropertyValuePropertyField) Utils.getPropertyField(mapSprite.lockedProperties, "IgnoreProps");
        this.ignoreProps = ignoreProperty.value.getText().equals("true");

        if(mapSprite.instanceSpecificProperties.size > 0)
            this.props = new ArrayList<>();
        for(int i = 0; i < mapSprite.instanceSpecificProperties.size; i ++)
        {
            PropertyField property = mapSprite.instanceSpecificProperties.get(i);
            if(property instanceof ColorPropertyField)
                this.props.add(new ColorPropertyFieldData((ColorPropertyField) property));
            else if(property instanceof LightPropertyField)
                this.props.add(new LightPropertyFieldData((LightPropertyField) property));
            else if(property instanceof FieldFieldPropertyValuePropertyField)
                this.props.add(new FieldFieldPropertyValuePropertyFieldData((FieldFieldPropertyValuePropertyField) property));
            else if(property instanceof LabelFieldPropertyValuePropertyField)
                this.props.add(new LabelFieldPropertyValuePropertyFieldData((LabelFieldPropertyValuePropertyField) property));
        }

        // tool object id's
//        System.out.println(mapSprite.tool.attachedMapObjectManagers);
        if(mapSprite.tool.attachedMapObjectManagers != null)
        {
            this.toIDs = new ArrayList<>();
            for(int i = 0; i < mapSprite.tool.attachedMapObjectManagers.size; i ++)
            {
                AttachedMapObjectManager attachedMapObjectManager = mapSprite.tool.attachedMapObjectManagers.get(i);
                MapObject mapObject = attachedMapObjectManager.getMapObjectByParent(mapSprite);
                this.toIDs.add(mapObject.id);
            }
        }

        if(mapSprite.attachedMapObjectManagers != null)
        {
            if(mapSprite.attachedMapObjectManagers.size > 0)
                this.objs = new ArrayList<>();
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
                this.objs.add(mapObjectData);
            }
        }
    }
}
