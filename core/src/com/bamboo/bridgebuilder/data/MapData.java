package com.bamboo.bridgebuilder.data;

import com.badlogic.gdx.math.Rectangle;
import com.bamboo.bridgebuilder.EditorPolygon;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.map.*;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.*;
import com.dongbat.walkable.PathHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MapData
{
    public String name;
    public ArrayList<SpriteSheetData> sheets;
    public ArrayList<LayerData> layers;
    public ArrayList<PropertyData> lProps;
    public ArrayList<PropertyData> props;
    public ArrayList<GroupMapPolygonData> groups;
    public long idCounter;
//    public String pathfinding; // yaml

    public MapData(){}
    public MapData(Map map, boolean settingBBMDefaults)
    {
//        this.pathfinding = savePathfindingMeshAsYaml(map);



        this.idCounter = map.idCounter;
        float oldPerspective = map.perspectiveZoom;
        map.perspectiveZoom = 0;
        map.updateLayerSpriteGrids();
        for(int i = 0; i < map.layers.size; i ++)
        {
            map.layers.get(i).update();
        }
        map.perspectiveZoom = oldPerspective;

        this.name = map.name;
        if(map.propertyMenu.mapPropertyPanel.lockedProperties.size > 0)
            this.lProps = new ArrayList<>();
        if(map.propertyMenu.mapPropertyPanel.properties.size > 0)
            this.props = new ArrayList<>();
        for(int i = 0; i < map.propertyMenu.mapPropertyPanel.lockedProperties.size; i ++)
        {
            PropertyField property = map.propertyMenu.mapPropertyPanel.lockedProperties.get(i);
            if(property instanceof ColorPropertyField)
                this.lProps.add(new ColorPropertyFieldData((ColorPropertyField) property));
            else if(property instanceof OpaqueColorPropertyField)
                this.lProps.add(new OpaqueColorPropertyFieldData((OpaqueColorPropertyField) property));
            else if(property instanceof LightPropertyField)
                this.lProps.add(new LightPropertyFieldData((LightPropertyField) property));
            else if(property instanceof FieldFieldPropertyValuePropertyField)
                this.lProps.add(new FieldFieldPropertyValuePropertyFieldData((FieldFieldPropertyValuePropertyField) property));
            else if(property instanceof LabelFieldPropertyValuePropertyField)
                this.lProps.add(new LabelFieldPropertyValuePropertyFieldData((LabelFieldPropertyValuePropertyField) property));
        }
        for(int i = 0; i < map.propertyMenu.mapPropertyPanel.properties.size; i ++)
        {
            PropertyField property = map.propertyMenu.mapPropertyPanel.properties.get(i);
            if(property instanceof ColorPropertyField)
                this.props.add(new ColorPropertyFieldData((ColorPropertyField) property));
            else if(property instanceof LightPropertyField)
                this.props.add(new LightPropertyFieldData((LightPropertyField) property));
            else if(property instanceof FieldFieldPropertyValuePropertyField)
                this.props.add(new FieldFieldPropertyValuePropertyFieldData((FieldFieldPropertyValuePropertyField) property));
            else if(property instanceof LabelFieldPropertyValuePropertyField)
                this.props.add(new LabelFieldPropertyValuePropertyFieldData((LabelFieldPropertyValuePropertyField) property));
        }
        this.sheets = new ArrayList<>(4);

        this.layers = new ArrayList<>();
        for(int i = 0; i < map.layers.size; i ++)
        {
            Layer layer = map.layers.get(i);
            if(layer instanceof SpriteLayer)
                this.layers.add(new SpriteLayerData((SpriteLayer) layer));
            else if(layer instanceof ObjectLayer)
                this.layers.add(new ObjectLayerData((ObjectLayer) layer));
        }
        FieldFieldPropertyValuePropertyField sky = (FieldFieldPropertyValuePropertyField) Utils.getPropertyField(map.propertyMenu.mapPropertyPanel.properties, "sky");
        if(sky != null)
        {
            if(!map.spriteMenu.hasSpriteSheet(sky.value.getText()))
                map.spriteMenu.createSpriteSheet(sky.value.getText());
        }
        for(int i = 0; i < map.spriteMenu.spriteSheets.size; i ++)
            this.sheets.add(new SpriteSheetData(map, map.spriteMenu.spriteSheets.get(i)));

        if(map.groupPolygons != null && map.groupPolygons.children.size > 0)
        {
            this.groups = new ArrayList<>();
            for (int i = 0; i < map.groupPolygons.children.size; i ++)
            {
                this.groups.add(new GroupMapPolygonData(((MapPolygon)map.groupPolygons.children.get(i)), 0, 0));
            }
        }

        // Remove all the map data such as layers since they are not default information
        if(settingBBMDefaults)
        {
            this.name = "defaultBBM.bbm";
            this.layers.clear();
            if(this.groups != null)
                this.groups.clear();
        }
    }

    private String savePathfindingMeshAsYaml(Map map)
    {
        boolean hasPathfindingLayer = false;
        float width = 0, height = 0, z = 0;
        for(int i = 0; i < map.layers.size; i ++)
        {
            Layer layer = map.layers.get(i);
            if(Utils.getPropertyField(layer.properties, "pathfinding") != null)
                hasPathfindingLayer = true;
            if(Utils.getPropertyField(layer.properties, "playableFloor") != null)
            {
                width = layer.width;
                height = layer.height;
                z = layer.z;
            }
        }
        PathHelper pathHelper = new PathHelper(width, height, 1000);
        ArrayList<MapPolygon> obstacles = getObstacles(map, hasPathfindingLayer, z);
        int size = obstacles.size();
        for(int i = 0; i < size; i ++)
        {
            pathHelper.addPolygon(obstacles.get(i).polygon.getTransformedVertices());
        }




        ByteArrayOutputStream outputStream = null;

        try
        {
            outputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

            objectOutputStream.writeObject(pathHelper);
            objectOutputStream.flush();
            objectOutputStream.close();

        } catch (IOException e)
        {
            e.printStackTrace();
        }



        return outputStream.toString();
    }

    private ArrayList<MapPolygon> getObstacles(Map map, boolean hasPathfindingLayer, float z)
    {
        ArrayList<MapPolygon> obstacles = new ArrayList<MapPolygon>();

        for(int i = 0; i < map.layers.size; i ++)
        {
            Layer layer = map.layers.get(i);
            if(layer instanceof SpriteLayer)
            {
                SpriteLayer spriteLayer = (SpriteLayer) layer;
                for(int k = 0; k < spriteLayer.children.size; k ++)
                {
                    MapSprite mapSprite = spriteLayer.children.get(k);
                    if(mapSprite.attachedMapObjects != null)
                    {
                        for (int s = 0; s < mapSprite.attachedMapObjects.size; s++)
                        {
                            MapObject mapObject = mapSprite.attachedMapObjects.get(s);
                            if(mapObject instanceof MapPolygon)
                                addObstacle((MapPolygon) mapObject, obstacles, hasPathfindingLayer, z);
                        }
                    }
                }
            }
            else if(layer instanceof ObjectLayer)
            {
                ObjectLayer objectLayer = (ObjectLayer) layer;
                for(int k = 0; k < objectLayer.children.size; k ++)
                {
                    MapObject mapObject = objectLayer.children.get(k);
                    if(mapObject instanceof MapPolygon)
                        addObstacle((MapPolygon) mapObject, obstacles, hasPathfindingLayer, z);
                }
            }
        }
        return obstacles;
    }

    private void addObstacle(MapPolygon mapPolygon, ArrayList<MapPolygon> obstacles, boolean hasPathfindingLayer, float z)
    {
        Layer layer = mapPolygon.layer;
        if(layer == null && mapPolygon.attachedSprite != null)
            layer = mapPolygon.attachedSprite.layer;
        if(layer.z != z)
            return;
        EditorPolygon polygon = mapPolygon.polygon;
        if ((hasPathfindingLayer && Utils.containsProperty(layer.properties, "pathfinding")) || (!hasPathfindingLayer && (Utils.containsProperty(mapPolygon.properties, "pathfinding") || (Utils.containsProperty(mapPolygon.properties, "blocked") && !Utils.containsProperty(mapPolygon.properties, "ignorePathfinding") && !Utils.containsProperty(layer.properties, "ignorePathfinding")))))
        {
            if (polygon.area() < 0) // counter-clockwise. flip
            {
                float[] newPolygon = new float[polygon.getVertices().length];
                for (int i = 0; i < polygon.getVertices().length; i += 2)
                {
                    newPolygon[polygon.getVertices().length - 1 - i - 1] = polygon.getVertices()[i];
                    newPolygon[polygon.getVertices().length - 1 - i] = polygon.getVertices()[i + 1];
                }
                polygon.setVertices(newPolygon);
            }

            Rectangle boundingRectangle = polygon.getBoundingRectangle();
            if (boundingRectangle.width > 1f || boundingRectangle.height > 1f)
                obstacles.add(mapPolygon);
        }
    }
}

