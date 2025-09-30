package com.bamboo.bridgebuilder.ui.propertyMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.commands.AddProperty;
import com.bamboo.bridgebuilder.map.*;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.ColorPropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.FieldFieldPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.LightPropertyField;

import static com.bamboo.bridgebuilder.BridgeBuilder.toolHeight;

public class PropertyPresetDialog extends Window
{
    private Array<PropertyPreset> presets;
    private Table presetTable;
    private ScrollPane scrollPane;
    private TextButton close;
    private Skin skin;
    private Map map;

    public PropertyPresetDialog(Map map, Stage stage, Skin skin)
    {
        super("Property Presets", skin);
        this.presets = new Array<>();
        this.skin = skin;
        this.map = map;
        this.presetTable = new Table();
        this.presetTable.pad(toolHeight * 3f);
        this.scrollPane = new ScrollPane(this.presetTable);

        this.close = new TextButton("Close", skin);
        this.close.setColor(Color.FIREBRICK);
        this.close.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                close();
            }
        });

        registerPropertyPresets();

        this.add(scrollPane).row();
        this.add(this.close);

        setSize(Gdx.graphics.getWidth() / 1.75f, Gdx.graphics.getHeight() / 1.75f);
        this.setPosition((stage.getWidth() / 2f), (stage.getHeight() / 2f), Align.center);
        stage.addActor(this);
        setVisible(false);
    }

    public void rebuild()
    {
        this.presetTable.clearChildren();

        if(this.map.selectedObjects.size > 0)
        {
            boolean allPoints = true;
            boolean allPolygons = true;
            for(int i = 0; i < this.map.selectedObjects.size; i++)
            {
                MapObject mapObject = this.map.selectedObjects.get(i);
                if(mapObject instanceof MapPolygon)
                    allPoints = false;
                else if(mapObject instanceof MapPoint)
                    allPolygons = false;
            }

            if(allPoints)
            {
                addPresetsForSelection(SelectionType.POINT);
            }
            else if(allPolygons)
            {
                addPresetsForSelection(SelectionType.POLYGON);

                // Special case for SpriteLayer polygons
                if(this.map.selectedLayer instanceof SpriteLayer)
                {
                    addPresetsForSelection(SelectionType.POLYGON_IN_SPRITE_LAYER);
                }
            }
        }
        else if(this.map.spriteMenu.selectedSpriteTools.size > 0)
        {
            addPresetsForSelection(SelectionType.SPRITETOOL);
        }
        else if(this.map.selectedLayer != null)
        {
            if(this.map.selectedLayer instanceof ObjectLayer)
            {
                addPresetsForSelection(SelectionType.OBJECTLAYER);
            }
            else if(this.map.selectedLayer instanceof SpriteLayer)
            {
                addPresetsForSelection(SelectionType.SPRITELAYER);
            }
        }
        else
        {
            addPresetsForSelection(SelectionType.MAP);
        }
    }

    private void addPresetsForSelection(SelectionType selectionType)
    {
        for(int i = 0; i < presets.size; i ++)
        {
            PropertyPreset preset = presets.get(i);
            if(preset.appliesToSelection(selectionType))
            {
                this.presetTable.add(preset.getTable()).pad(5);
            }
        }
    }

    public void close()
    {
        this.setVisible(false);
    }

    public void open()
    {
        rebuild();
        this.setVisible(true);
    }

    // ==== PRESET REGISTRATION METHODS ====

    private void registerPropertyPresets()
    {
        // Special preset with custom UI (Point Light)
        addCustomPreset(new SelectionType[]{SelectionType.POINT},
                createLightPresetTable("Point Light"), this::createLightAction);

        addCustomPreset(new SelectionType[]{SelectionType.OBJECTLAYER},
                createColorPresetTable("Rayhandler Ambience Override"), createColorAction("Ambient Override"));

        // Simple property presets

        addSimplePreset("Blocked Polygon", new SelectionType[]{SelectionType.POLYGON},
                new PropertyValue("blocked", ""));

        addSimplePreset("Blocked Line", new SelectionType[]{SelectionType.POINT},
                new PropertyValue("blocked", ""));

        addMultiPropertyPreset("Double Sided Blocked Line", new SelectionType[]{SelectionType.POINT},
                new PropertyValue("blocked", ""),
                new PropertyValue("doubleSided", ""));

        addSimplePreset("Phaseable", new SelectionType[]{SelectionType.POINT},
                new PropertyValue("phaseable", ""));

        addSimplePreset("Rayhandler", new SelectionType[]{SelectionType.OBJECTLAYER},
                new PropertyValue("rayhandler", ""));

        addSimplePreset("Playable Floor", new SelectionType[]{SelectionType.OBJECTLAYER},
                new PropertyValue("playableFloor", ""));

        addSimplePreset("Change Floor", new SelectionType[]{SelectionType.OBJECTLAYER},
                new PropertyValue("changeFloor", ""));

        addSimplePreset("Disable Perspective", new SelectionType[]{SelectionType.SPRITELAYER},
                new PropertyValue("disablePerspective", ""));

        addSimplePreset("Ground", new SelectionType[]{SelectionType.SPRITELAYER},
                new PropertyValue("ground", ""));

        addSimplePreset("Shadows", new SelectionType[]{SelectionType.SPRITELAYER},
                new PropertyValue("shadows", ""));

        addSimplePreset("Ignore Dust Type", new SelectionType[]{SelectionType.SPRITELAYER},
                new PropertyValue("ignoreDustType", ""));

        addSimplePreset("Collision Sort Front", new SelectionType[]{SelectionType.POLYGON},
                new PropertyValue("collisionSort", ""));

        addSimplePreset("Collision Sort Back", new SelectionType[]{SelectionType.POLYGON},
                new PropertyValue("collisionSortBack", ""));

        addSimplePreset("Dust Type", new SelectionType[]{SelectionType.POLYGON},
                new PropertyValue("dustType", "...dirt..."));

        addSimplePreset("Fade Limit", new SelectionType[]{SelectionType.SPRITETOOL},
                new PropertyValue("fadeLimit", ""));

        // Multi-property presets
        addMultiPropertyPreset("Top Sprite", new SelectionType[]{SelectionType.SPRITETOOL},
                new PropertyValue("top", "...4tree1..."),
                new PropertyValue("fade0", "true"));

        addMultiPropertyPreset("Animated", new SelectionType[]{SelectionType.SPRITETOOL},
                new PropertyValue("animated", "Value"),
                new PropertyValue("fps", "10"),
                new PropertyValue("pingpong", "Value"),
                new PropertyValue("loop", "Value"),
                new PropertyValue("random", "Value"));

        addMultiPropertyPreset("Blowable", new SelectionType[]{SelectionType.POLYGON_IN_SPRITE_LAYER},
                new PropertyValue("wind", "Value"),
                new PropertyValue("skewWind", "Value"),
                new PropertyValue("fpsWind", "Value"),
                new PropertyValue("blowResistTop", "17.5"),
                new PropertyValue("blowResist", "17.5"));

        addMultiPropertyPreset("Optional Dialogue", new SelectionType[]{SelectionType.POLYGON},
                new PropertyValue("PAHRI", "Bridge//playerAsksPahriAboutBridge"),
                new PropertyValue("insideTrigger", ""),
                new PropertyValue("eventLimit", "-1"),
                new PropertyValue("eventLimitType", "POLYGONENTER"));

        addMultiPropertyPreset("Cutscene Event", new SelectionType[]{SelectionType.POLYGON},
                new PropertyValue("cutsceneEvent", "PAHRIFIRSTPEDESTAL"),
                new PropertyValue("insideTrigger", ""),
                new PropertyValue("eventLimit", "1"));

        addMultiPropertyPreset("Flicker", new SelectionType[]{SelectionType.POINT},
                new PropertyValue("flicker", ""),
                new PropertyValue("minFlickerAlpha", ".5"),
                new PropertyValue("flickerTimeMultiplier", ".25"));

        addMultiPropertyPreset("Spawn Active AI", new SelectionType[]{SelectionType.POLYGON},
                new PropertyValue("spawn", ""),
                new PropertyValue("min", "1"),
                new PropertyValue("max", "5"),
                new PropertyValue("characterType", "MONSTER"));

        addMultiPropertyPreset("Dust", new SelectionType[]{SelectionType.POLYGON},
                new PropertyValue("dust", "BOUNDARY"),
                new PropertyValue("dustDirection", "0"),
                new PropertyValue("dustSpeed", "0"),
                new PropertyValue("dustSize", "3"),
                new PropertyValue("dustR", "1"),
                new PropertyValue("dustG", "1"),
                new PropertyValue("dustB", "1"),
                new PropertyValue("dustAngle", "90"),
                new PropertyValue("dustFrequencyMin", ".2"),
                new PropertyValue("dustFrequencyMax", ".5"),
                new PropertyValue("dustGroundPosition", "Value"));

        addMultiPropertyPreset("Interactable", new SelectionType[]{SelectionType.POLYGON},
                new PropertyValue("interactable", "7"),
                new PropertyValue("title", "Value"),
                new PropertyValue("message", "Value"));

        addMultiPropertyPreset("Change Map Beacon", new SelectionType[]{SelectionType.POLYGON},
                new PropertyValue("x", "Value"),
                new PropertyValue("y", "Value"),
                new PropertyValue("showName", "Value"),
                new PropertyValue("toFloor", "0"),
                new PropertyValue("changeMap", "Value"),
                new PropertyValue("angle", "Value"),
                new PropertyValue("cameraAngle", "Value"));

        addMultiPropertyPreset("ambience", new SelectionType[]{SelectionType.POLYGON},
                new PropertyValue("ambientDampenEvent", "Value"),
                new PropertyValue("dampener", "0"),
                new PropertyValue("insideTrigger", "Value"),
                new PropertyValue("eventLimit", "-1"),
                new PropertyValue("eventLimitType", "POLYGONENTER"),
                new PropertyValue("roomAmbience", "WOODENINDOORS"));
    }

    // Convenience method for single property presets
    private void addSimplePreset(String title, SelectionType[] selectionTypes, PropertyValue property)
    {
        addMultiPropertyPreset(title, selectionTypes, property);
    }

    // Convenience method for single property presets with custom action
    private void addSimplePreset(String title, SelectionType[] selectionTypes, Runnable action)
    {
        presets.add(new PropertyPreset(selectionTypes, createPresetTable(title, new PropertyValue[0]), action));
    }

    // Method for presets with custom UI and action
    private void addCustomPreset(SelectionType[] selectionTypes, Table table, Runnable action)
    {
        presets.add(new PropertyPreset(selectionTypes, table, action));
    }

    // Main method for adding multi-property presets
    private void addMultiPropertyPreset(String title, SelectionType[] selectionTypes, PropertyValue... properties)
    {
        Table presetTable = createPresetTable(title, properties);

        Runnable action = () -> {
            if(properties.length == 0) return;

            AddProperty addProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer,
                    map.selectedSprites, map.spriteMenu.selectedSpriteTools, map.selectedObjects,
                    properties[0].property, properties[0].value);

            for(int i = 1; i < properties.length; i++)
            {
                AddProperty chainedProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer,
                        map.selectedSprites, map.spriteMenu.selectedSpriteTools, map.selectedObjects,
                        properties[i].property, properties[i].value);
                addProperty.addAddPropertyCommandToChain(chainedProperty);
            }

            map.executeCommand(addProperty);
        };

        presets.add(new PropertyPreset(selectionTypes, presetTable, action));
    }

    private Table createPresetTable(String title, PropertyValue[] properties)
    {
        float pad = Gdx.graphics.getHeight() / 35;

        Table presetTable = new Table();
        SpriteDrawable spriteDrawable = new SpriteDrawable(new Sprite(new Texture("ui/whitePixel.png")));
        spriteDrawable.getSprite().setColor(Color.DARK_GRAY);
        presetTable.background(spriteDrawable);
        presetTable.add(new Label(title, this.skin)).padTop(pad / 2).row();

        if(properties.length > 0)
        {
            Table fieldsTable = new Table();
            for(int i = 0; i < properties.length; i++)
            {
                FieldFieldPropertyValuePropertyField field = new FieldFieldPropertyValuePropertyField(
                        properties[i].property, properties[i].getDisplayValue(), this.skin, null, null, false);
                field.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
                field.clearListeners();

                if(i < properties.length - 1)
                    fieldsTable.add(field).padLeft(pad).padRight(pad).padTop(pad / 2).padBottom(pad / 6).row();
                else
                    fieldsTable.add(field).padLeft(pad).padRight(pad).padTop(pad / 2).padBottom(pad).row();
            }
            presetTable.add(fieldsTable);
        }

        presetTable.setTouchable(Touchable.enabled);
        return presetTable;
    }

    // Special action for light preset
    private void createLightAction()
    {
        AddProperty addProperty = new AddProperty(1, 1, 1, 1, 5, 25, map, PropertyTools.NEWLIGHT,
                map.selectedLayer, map.selectedSprites, map.spriteMenu.selectedSpriteTools, map.selectedObjects);
        map.executeCommand(addProperty);
    }

    // Special table creation for light preset
    private Table createLightPresetTable(String title)
    {
        float pad = Gdx.graphics.getHeight() / 35;

        Table presetTable = new Table();
        SpriteDrawable spriteDrawable = new SpriteDrawable(new Sprite(new Texture("ui/whitePixel.png")));
        spriteDrawable.getSprite().setColor(Color.DARK_GRAY);
        presetTable.background(spriteDrawable);
        presetTable.add(new Label(title, this.skin)).padTop(pad / 2).row();

        Table fieldsTable = new Table();
        LightPropertyField lightPropertyField = new LightPropertyField(this.skin, null, null, false, 1, 1, 1, 1, 5, 25);
        lightPropertyField.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
        lightPropertyField.clearListeners();
        fieldsTable.add(lightPropertyField).padLeft(pad).padRight(pad).padTop(pad / 2).padBottom(pad).row();
        presetTable.add(fieldsTable);

        presetTable.setTouchable(Touchable.enabled);
        return presetTable;
    }

    // Special action for color preset
    private Runnable createColorAction(String property)
    {
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                AddProperty addProperty = new AddProperty(property, 1, 1, 1, 1, map, PropertyTools.NEWCOLOR,
                        map.selectedLayer, map.selectedSprites, map.spriteMenu.selectedSpriteTools, map.selectedObjects);
                map.executeCommand(addProperty);
            }
        };
        return runnable;
    }

    private Table createColorPresetTable(String title)
    {
        float pad = Gdx.graphics.getHeight() / 35;

        Table presetTable = new Table();
        SpriteDrawable spriteDrawable = new SpriteDrawable(new Sprite(new Texture("ui/whitePixel.png")));
        spriteDrawable.getSprite().setColor(Color.DARK_GRAY);
        presetTable.background(spriteDrawable);
        presetTable.add(new Label(title, this.skin)).padTop(pad / 2).row();

        Table fieldsTable = new Table();
        ColorPropertyField ColorPropertyField = new ColorPropertyField(this.skin, null, null, false, "Ambient Override", 1, 1, 1, 1);
        ColorPropertyField.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
        ColorPropertyField.clearListeners();
        fieldsTable.add(ColorPropertyField).padLeft(pad).padRight(pad).padTop(pad / 2).padBottom(pad).row();
        presetTable.add(fieldsTable);

        presetTable.setTouchable(Touchable.enabled);
        return presetTable;
    }

    // ==== HELPER CLASSES ====

    private static class PropertyPreset
    {
        private final SelectionType[] selectionTypes;
        private final Table table;
        private final Runnable action;

        public PropertyPreset(SelectionType[] selectionTypes, Table table, Runnable action)
        {
            this.selectionTypes = selectionTypes;
            this.table = table;
            this.action = action;

            // Add click listener to the table
            table.addListener(new InputListener(){
                @Override
                public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor)
                {
                    ((SpriteDrawable) table.getBackground()).getSprite().setColor(Color.FOREST);
                }
                @Override
                public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor)
                {
                    ((SpriteDrawable) table.getBackground()).getSprite().setColor(Color.DARK_GRAY);
                }
                @Override
                public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
                {
                    action.run();
                    return false;
                }
            });
        }

        public boolean appliesToSelection(SelectionType selectionType)
        {
            for(SelectionType type : selectionTypes)
            {
                if(type == selectionType)
                    return true;
            }
            return false;
        }

        public Table getTable()
        {
            return table;
        }
    }

    public static class PropertyValue
    {
        public String property;
        public String value;

        public PropertyValue(String property, String value)
        {
            this.property = property;
            this.value = value;
        }

        public String getDisplayValue()
        {
            return value.isEmpty() ? "..." : value;
        }
    }

    public enum SelectionType
    {
        POINT, POLYGON, SPRITE, SPRITETOOL, MAP, OBJECTLAYER, SPRITELAYER, POLYGON_IN_SPRITE_LAYER
    }
}