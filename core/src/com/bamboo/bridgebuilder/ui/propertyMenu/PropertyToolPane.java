package com.bamboo.bridgebuilder.ui.propertyMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.EditorAssets;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.commands.AddProperty;
import com.bamboo.bridgebuilder.map.*;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.ColorPropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.FieldFieldPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

import static com.bamboo.bridgebuilder.BridgeBuilder.toolHeight;

public class PropertyToolPane extends Group
{
    private Stack pane;
    private Table toolTable;
    private Image background;
    private Skin skin;
    private BridgeBuilder editor;

    private PropertyTool newProperty;
    private PropertyPresetDialog propertyPresetDialog;
    private TextButton more;
    private TextButton apply;

    public PropertyMenu menu;


    public PropertyToolPane(BridgeBuilder editor, Map map, PropertyMenu menu, Skin skin)
    {
        this.menu = menu;
        this.toolTable = new Table();
        this.newProperty = new PropertyTool(map, PropertyTools.NEW, skin);
        this.more = new TextButton("More", skin);
        this.propertyPresetDialog = new PropertyPresetDialog(map, editor.stage, skin);
        this.newProperty.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                AddProperty addProperty = new AddProperty(map, newProperty.tool, map.selectedLayer, map.selectedSprites, map.spriteMenu.selectedSpriteTools, map.selectedObjects);
                map.executeCommand(addProperty);
            }
        });
        this.more.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                propertyPresetDialog.open();
            }
        });

        this.apply = new TextButton("Apply", skin);
        setApplyListener();
        this.toolTable.left();
        this.toolTable.add(this.newProperty).padRight(1);
        this.toolTable.add(this.more).padRight(1);
        this.toolTable.add(this.apply);

        this.editor = editor;
        this.skin = skin;
        this.pane = new Stack();

        this.background = new Image(EditorAssets.getUIAtlas().createPatch("load-background"));
        this.pane.add(this.background);
        this.pane.add(this.toolTable);

        this.addActor(this.pane);
    }

    @Override
    public void setSize(float width, float height)
    {
        this.pane.setSize(width, height);
        this.background.setBounds(0, 0, width, height);

        // Resize all buttons in the pane
        this.newProperty.setSize(toolHeight, toolHeight);
        this.toolTable.getCell(this.newProperty).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.more).size(toolHeight * 2, toolHeight);
        this.toolTable.getCell(this.apply).size(toolHeight * 2, toolHeight);
        this.toolTable.invalidateHierarchy();

        this.pane.invalidateHierarchy();

        super.setSize(width, height);
    }

    public void setApplyListener()
    {
        this.apply.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                apply(menu.map);
            }
        });
    }

    public static void apply(Map map)
    {
        // sprite tools
        for(int i = 0; i < map.spriteMenu.spriteTable.getChildren().size; i ++)
        {
            if(map.spriteMenu.spriteTable.getChildren().get(i) instanceof Table)
            {
                SpriteTool spriteTool = ((Table) map.spriteMenu.spriteTable.getChildren().get(i)).findActor("spriteTool");
                // top
                PropertyField propertyField = Utils.getPropertyField(spriteTool.properties, "top");
                if(propertyField != null)
                {
                    FieldFieldPropertyValuePropertyField topProperty = (FieldFieldPropertyValuePropertyField) propertyField;
                    String topValue = topProperty.value.getText();
                    spriteTool.setTopSprites(topValue);
                }
                else if(spriteTool.topSprites != null)
                    spriteTool.setTopSprites("");
            }
        }

        // Layer children
        for(int i = 0; i < map.layers.size; i ++)
        {
            Layer layer = map.layers.get(i);
            // Map sprites
            if(layer instanceof SpriteLayer)
            {
                SpriteLayer spriteLayer = (SpriteLayer) layer;
                for(int k = 0; k < spriteLayer.children.size; k ++)
                {
                    MapSprite mapSprite = spriteLayer.children.get(k);
                    // Set sprite color
                    ColorPropertyField colorProperty = Utils.getLockedColorField("Tint", mapSprite.lockedProperties);
                    mapSprite.setColor(colorProperty.getR(), colorProperty.getG(), colorProperty.getB(), colorProperty.getA());

                    // Attached map sprites
                    if(mapSprite.attachedSprites != null)
                    {
                        for(int s = 0; s < mapSprite.attachedSprites.children.size; s++)
                        {
                            MapSprite attachedMapSprite = mapSprite.attachedSprites.children.get(s);
                            // Set sprite color
                            colorProperty = Utils.getLockedColorField("Tint", attachedMapSprite.lockedProperties);
                            attachedMapSprite.setColor(colorProperty.getR(), colorProperty.getG(), colorProperty.getB(), colorProperty.getA());
                        }
                    }

                    // Attached map objects
                    if(mapSprite.attachedMapObjects != null)
                    {
                        for(int s = 0; s < mapSprite.attachedMapObjects.size; s ++)
                        {
                            MapObject mapObject = mapSprite.attachedMapObjects.get(s);
                            // Map polygon
                            if(mapObject instanceof MapPolygon)
                            {
                                MapPolygon mapPolygon = (MapPolygon) mapObject;
                                // Set blocked
                                PropertyField propertyField = Utils.getPropertyField(mapPolygon.properties, "blocked");
                                if (propertyField != null)
                                    mapPolygon.createBody();
                                else
                                    mapPolygon.destroyBody();
                            }
                            // Map point
                            else
                            {
                                MapPoint mapPoint = (MapPoint) mapObject;
                                // Create light
                                PropertyField propertyField = Utils.getLightField(mapPoint.properties);
                                if (propertyField != null)
                                    mapPoint.createLight();
                                else
                                    mapPoint.destroyLight();
                            }
                        }
                    }
                }
            }
            // Map objects
            if(layer instanceof ObjectLayer)
            {
                ObjectLayer objectLayer = (ObjectLayer) layer;
                for(int k = 0; k < objectLayer.children.size; k ++)
                {
                    MapObject mapObject = objectLayer.children.get(k);
                    // Map polygons
                    if(mapObject instanceof MapPolygon)
                    {
                        MapPolygon mapPolygon = (MapPolygon) mapObject;
                        // Set blocked
                        PropertyField propertyField = Utils.getPropertyField(mapPolygon.properties, "blocked");
                        if (propertyField != null)
                            mapPolygon.createBody();
                        else
                            mapPolygon.destroyBody();
                    }
                    // Map point
                    else
                    {
                        MapPoint mapPoint = (MapPoint) mapObject;
                        // Create light
                        PropertyField propertyField = Utils.getLightField(mapPoint.properties);
                        if (propertyField != null)
                            mapPoint.createLight();
                        else
                            mapPoint.destroyLight();
                    }
                }
            }
        }

        if(map.editor.fileMenu.toolPane.perspective.selected)
            updatePerspective(map);
    }

    public static void updatePerspective(Map map)
    {
        // Disable perspective for now
        if(1 == 1)
            return;
        for(int i = 0; i < map.layers.size; i++)
        {
            Layer layer = map.layers.get(i);
            if(layer instanceof SpriteLayer)
            {
                SpriteLayer spriteLayer = (SpriteLayer) layer;
                for (int k = 0; k < spriteLayer.children.size; k++)
                {
                    MapSprite mapSprite = spriteLayer.children.get(k);
                    mapSprite.updatePerspective();
                }
            }
        }
    }
}
