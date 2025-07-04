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
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.FieldFieldPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.LightPropertyField;

import static com.bamboo.bridgebuilder.BridgeBuilder.toolHeight;

public class PropertyPresetDialog extends Window
{
    private Table newTopProperty;
    private Table newLightProperty;
    private Table newBlockedProperty;
    private Table newRayhandlerProperty;
    private Table newDisablePerspectiveProperty;
    private Table newGroundProperty;
    private Table newDustTypeProperty;
    private Table newCollisionSortProperty;
    private Table newCollisionSortBackProperty;
    private Table newAnimatedProperty;
    private Table newFadeLimitProperty;
    private Table newBlowableProperty;
    private Array<Table> propertyGroups;

    private Table presetTable;
    private ScrollPane scrollPane;

    private TextButton close;

    private Skin skin;

    private Map map;

    public PropertyPresetDialog(Map map, Stage stage, Skin skin)
    {
        super("Property Presets", skin);
        this.propertyGroups = new Array<>();
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

        createPropertyPresets();

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
                for(int i = 0; i < propertyGroups.size; i ++)
                {
                    SelectionType[] selectionTypes = (SelectionType[]) propertyGroups.get(i).getUserObject();
                    if(typesContains(SelectionType.POINT, selectionTypes))
                        this.presetTable.add(propertyGroups.get(i)).pad(5);
                }
                this.presetTable.add(this.newLightProperty).pad(5);
            }
            else if(allPolygons)
            {
                for(int i = 0; i < propertyGroups.size; i ++)
                {
                    SelectionType[] selectionTypes = (SelectionType[]) propertyGroups.get(i).getUserObject();
                    if(typesContains(SelectionType.POLYGON, selectionTypes))
                        this.presetTable.add(propertyGroups.get(i)).pad(5);
                }
                this.presetTable.add(this.newBlockedProperty).pad(5);
                this.presetTable.add(this.newCollisionSortProperty).pad(5);
                this.presetTable.add(this.newCollisionSortBackProperty).pad(5);
                this.presetTable.add(this.newDustTypeProperty).pad(5);

                if(this.map.selectedLayer instanceof SpriteLayer)
                    this.presetTable.add(this.newBlowableProperty).pad(5);
            }
        }
        else if(this.map.spriteMenu.selectedSpriteTools.size > 0)
        {
            for(int i = 0; i < propertyGroups.size; i ++)
            {
                SelectionType[] selectionTypes = (SelectionType[]) propertyGroups.get(i).getUserObject();
                if(typesContains(SelectionType.SPRITETOOL, selectionTypes))
                    this.presetTable.add(propertyGroups.get(i)).pad(5);
            }

            this.presetTable.add(this.newTopProperty).pad(5);
            this.presetTable.add(this.newAnimatedProperty).pad(5);
            this.presetTable.add(this.newFadeLimitProperty).pad(5);
        }
        else if(this.map.selectedLayer != null)
        {
            if(this.map.selectedLayer instanceof ObjectLayer)
            {
                for(int i = 0; i < propertyGroups.size; i ++)
                {
                    SelectionType[] selectionTypes = (SelectionType[]) propertyGroups.get(i).getUserObject();
                    if(typesContains(SelectionType.OBJECTLAYER, selectionTypes))
                        this.presetTable.add(propertyGroups.get(i)).pad(5);
                }
                this.presetTable.add(this.newRayhandlerProperty).pad(5);
            }
            else if(this.map.selectedLayer instanceof SpriteLayer)
            {
                for(int i = 0; i < propertyGroups.size; i ++)
                {
                    SelectionType[] selectionTypes = (SelectionType[]) propertyGroups.get(i).getUserObject();
                    if(typesContains(SelectionType.SPRITELAYER, selectionTypes))
                        this.presetTable.add(propertyGroups.get(i)).pad(5);
                }
                this.presetTable.add(this.newDisablePerspectiveProperty).pad(5);
                this.presetTable.add(this.newGroundProperty).pad(5);
            }
        }
        else
        {
            for(int i = 0; i < propertyGroups.size; i ++)
            {
                SelectionType[] selectionTypes = (SelectionType[]) propertyGroups.get(i).getUserObject();
                if(typesContains(SelectionType.MAP, selectionTypes))
                    this.presetTable.add(propertyGroups.get(i)).pad(5);
            }
        }
    }

    private boolean typesContains(SelectionType type, SelectionType[] selectionTypes)
    {
        for(int i = 0; i < selectionTypes.length; i ++)
        {
            if (selectionTypes[i] == type)
                return true;
        }
        return false;
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

    private void createPropertyPresets()
    {
        this.createTop();
        this.createLight();
        this.createBlocked();
        this.createRayhandler();
        this.createDisablePerspective();
        this.createGround();
        this.createDustType();
        this.createCollisionSort();
        this.createCollisionSortBack();
        this.createAnimated();
        this.createWind();
        this.createFadeLimit();
        propertyGroups.add(this.createPropertyGroup("Optional Dialogue", new SelectionType[]{SelectionType.POLYGON},
                new PropertyValue("PAHRI", "Bridge//playerAsksPahriAboutBridge"),
                new PropertyValue("insideTrigger", ""),
                new PropertyValue("eventLimit", "-1"),
                new PropertyValue("eventLimitType", "POLYGONENTER")
        ));
        propertyGroups.add(this.createPropertyGroup("Cutscene Event", new SelectionType[]{SelectionType.POLYGON},
                new PropertyValue("cutsceneEvent", "PAHRIFIRSTPEDESTAL"),
                new PropertyValue("insideTrigger", ""),
                new PropertyValue("eventLimit", "1")
        ));
        propertyGroups.add(this.createPropertyGroup("Flicker", new SelectionType[]{SelectionType.POINT},
                new PropertyValue("flicker", ""),
                new PropertyValue("minFlickerAlpha", ".5"),
                new PropertyValue("flickerTimeMultiplier", ".25")
        ));
        propertyGroups.add(this.createPropertyGroup("Spawn Active AI", new SelectionType[]{SelectionType.POLYGON},
                new PropertyValue("spawn", ""),
                new PropertyValue("min", "1"),
                new PropertyValue("max", "5"),
                new PropertyValue("characterType", "MONSTER")
        ));
        propertyGroups.add(this.createPropertyGroup("Dust", new SelectionType[]{SelectionType.POLYGON},
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
                new PropertyValue("dustGroundPosition", "Value")
        ));
        propertyGroups.add(this.createPropertyGroup("Interactable", new SelectionType[]{SelectionType.POLYGON},
                new PropertyValue("interactable", "7"),
                new PropertyValue("title", "Value"),
                new PropertyValue("message", "Value")
        ));
        propertyGroups.add(this.createPropertyGroup("Change Map Beacon", new SelectionType[]{SelectionType.POLYGON},
                new PropertyValue("x", "Value"),
                new PropertyValue("y", "Value"),
                new PropertyValue("showName", "Value"),
                new PropertyValue("toFloor", "0"),
                new PropertyValue("changeMap", "Value"),
                new PropertyValue("angle", "Value"),
                new PropertyValue("cameraAngle", "Value")
        ));
        propertyGroups.add(this.createPropertyGroup("ambience", new SelectionType[]{SelectionType.POLYGON},
                new PropertyValue("ambientDampenEvent", "Value"),
                new PropertyValue("dampener", "0"),
                new PropertyValue("insideTrigger", "Value"),
                new PropertyValue("eventLimit", "-1"),
                new PropertyValue("eventLimitType", "POLYGONENTER"),
                new PropertyValue("roomAmbience", "WOODENINDOORS")
        ));
    }

    private void createTop()
    {
        SpriteDrawable spriteDrawable;
        Table table;
        FieldFieldPropertyValuePropertyField fieldFieldPropertyValuePropertyField;
        float pad = Gdx.graphics.getHeight() / 35;

        this.newTopProperty = new Table();
        spriteDrawable = new SpriteDrawable(new Sprite(new Texture("ui/whitePixel.png")));
        spriteDrawable.getSprite().setColor(Color.DARK_GRAY);
        this.newTopProperty.background(spriteDrawable);
        this.newTopProperty.add(new Label("Top Sprite", this.skin)).padTop(pad / 2).row();
        table = new Table();
        fieldFieldPropertyValuePropertyField = new FieldFieldPropertyValuePropertyField("top", "...4tree1...", this.skin, null, null, false);
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
        fieldFieldPropertyValuePropertyField.clearListeners();
        table.add(fieldFieldPropertyValuePropertyField).padLeft(pad).padRight(pad).padTop(pad / 6).padBottom(pad / 6).row();
        fieldFieldPropertyValuePropertyField = new FieldFieldPropertyValuePropertyField("fade0", "true", this.skin, null, null, false);
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
        fieldFieldPropertyValuePropertyField.clearListeners();
        table.add(fieldFieldPropertyValuePropertyField).padLeft(pad).padRight(pad).padTop(pad / 2).padBottom(pad).row();
        this.newTopProperty.add(table);
        this.newTopProperty.setTouchable(Touchable.enabled);
        this.newTopProperty.addListener(new InputListener(){
            @Override
            public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                ((SpriteDrawable) newTopProperty.getBackground()).getSprite().setColor(Color.FOREST);
            }
            @Override
            public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                ((SpriteDrawable) newTopProperty.getBackground()).getSprite().setColor(Color.DARK_GRAY);
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {
                AddProperty addProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects, "top", "");
                addProperty.addAddPropertyCommandToChain(new AddProperty(map, PropertyTools.NEW, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects, "fade0", "true"));
                map.executeCommand(addProperty);
                return false;
            }
        });
    }

    private void createLight()
    {
        SpriteDrawable spriteDrawable;
        Table table;
        LightPropertyField lightPropertyField;
        float pad = Gdx.graphics.getHeight() / 35;

        this.newLightProperty = new Table();
        spriteDrawable = new SpriteDrawable(new Sprite(new Texture("ui/whitePixel.png")));
        spriteDrawable.getSprite().setColor(Color.DARK_GRAY);
        this.newLightProperty.background(spriteDrawable);
        this.newLightProperty.add(new Label("Point Light", this.skin)).padTop(pad / 2).row();
        table = new Table();
        lightPropertyField = new LightPropertyField(this.skin, null, null, false, 1, 1, 1, 1, 5, 25);
        lightPropertyField.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
        lightPropertyField.clearListeners();
        table.add(lightPropertyField).padLeft(pad).padRight(pad).padTop(pad / 2).padBottom(pad).row();
        this.newLightProperty.add(table);
        this.newLightProperty.setTouchable(Touchable.enabled);
        this.newLightProperty.addListener(new InputListener(){
            @Override
            public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                ((SpriteDrawable) newLightProperty.getBackground()).getSprite().setColor(Color.FOREST);
            }
            @Override
            public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                ((SpriteDrawable) newLightProperty.getBackground()).getSprite().setColor(Color.DARK_GRAY);
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {
                AddProperty addProperty = new AddProperty(1, 1, 1, 1, 5, 25, map, PropertyTools.NEWLIGHT, map.selectedLayer, map.selectedSprites, map.spriteMenu.selectedSpriteTools, map.selectedObjects);
                map.executeCommand(addProperty);
                return false;
            }
        });
    }

    private void createBlocked()
    {
        SpriteDrawable spriteDrawable;
        Table table;
        FieldFieldPropertyValuePropertyField fieldFieldPropertyValuePropertyField;
        float pad = Gdx.graphics.getHeight() / 35;

        this.newBlockedProperty = new Table();
        spriteDrawable = new SpriteDrawable(new Sprite(new Texture("ui/whitePixel.png")));
        spriteDrawable.getSprite().setColor(Color.DARK_GRAY);
        this.newBlockedProperty.background(spriteDrawable);
        this.newBlockedProperty.add(new Label("Blocked Polygon", this.skin)).padTop(pad / 2).row();
        table = new Table();
        fieldFieldPropertyValuePropertyField = new FieldFieldPropertyValuePropertyField("blocked", "", this.skin, null, null, false);
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
        fieldFieldPropertyValuePropertyField.clearListeners();
        table.add(fieldFieldPropertyValuePropertyField).padLeft(pad).padRight(pad).padTop(pad / 2).padBottom(pad).row();
        this.newBlockedProperty.add(table);
        this.newBlockedProperty.setTouchable(Touchable.enabled);
        this.newBlockedProperty.addListener(new InputListener(){
            @Override
            public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                ((SpriteDrawable) newBlockedProperty.getBackground()).getSprite().setColor(Color.FOREST);
            }
            @Override
            public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                ((SpriteDrawable) newBlockedProperty.getBackground()).getSprite().setColor(Color.DARK_GRAY);
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {
                AddProperty addProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects, "blocked", "");
                map.executeCommand(addProperty);
                return false;
            }
        });
    }

    private void createRayhandler()
    {
        SpriteDrawable spriteDrawable;
        Table table;
        FieldFieldPropertyValuePropertyField fieldFieldPropertyValuePropertyField;
        float pad = Gdx.graphics.getHeight() / 35;

        this.newRayhandlerProperty = new Table();
        spriteDrawable = new SpriteDrawable(new Sprite(new Texture("ui/whitePixel.png")));
        spriteDrawable.getSprite().setColor(Color.DARK_GRAY);
        this.newRayhandlerProperty.background(spriteDrawable);
        this.newRayhandlerProperty.add(new Label("Rayhandler", this.skin)).padTop(pad / 2).row();
        table = new Table();
        fieldFieldPropertyValuePropertyField = new FieldFieldPropertyValuePropertyField("rayhandler", "", this.skin, null, null, false);
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
        fieldFieldPropertyValuePropertyField.clearListeners();
        table.add(fieldFieldPropertyValuePropertyField).padLeft(pad).padRight(pad).padTop(pad / 2).padBottom(pad).row();
        this.newRayhandlerProperty.add(table);
        this.newRayhandlerProperty.setTouchable(Touchable.enabled);
        this.newRayhandlerProperty.addListener(new InputListener(){
            @Override
            public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                ((SpriteDrawable) newRayhandlerProperty.getBackground()).getSprite().setColor(Color.FOREST);
            }
            @Override
            public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                ((SpriteDrawable) newRayhandlerProperty.getBackground()).getSprite().setColor(Color.DARK_GRAY);
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {
                AddProperty addProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects, "rayhandler", "");
                map.executeCommand(addProperty);
                return false;
            }
        });
    }

    private void createCollisionSort()
    {
        SpriteDrawable spriteDrawable;
        Table table;
        FieldFieldPropertyValuePropertyField fieldFieldPropertyValuePropertyField;
        float pad = Gdx.graphics.getHeight() / 35;

        this.newCollisionSortProperty = new Table();
        spriteDrawable = new SpriteDrawable(new Sprite(new Texture("ui/whitePixel.png")));
        spriteDrawable.getSprite().setColor(Color.DARK_GRAY);
        this.newCollisionSortProperty.background(spriteDrawable);
        this.newCollisionSortProperty.add(new Label("Collision Sort Front", this.skin)).padTop(pad / 2).row();
        table = new Table();
        fieldFieldPropertyValuePropertyField = new FieldFieldPropertyValuePropertyField("collisionSort", "", this.skin, null, null, false);
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
        fieldFieldPropertyValuePropertyField.clearListeners();
        table.add(fieldFieldPropertyValuePropertyField).padLeft(pad).padRight(pad).padTop(pad / 2).padBottom(pad).row();
        this.newCollisionSortProperty.add(table);
        this.newCollisionSortProperty.setTouchable(Touchable.enabled);
        this.newCollisionSortProperty.addListener(new InputListener(){
            @Override
            public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                ((SpriteDrawable) newCollisionSortProperty.getBackground()).getSprite().setColor(Color.FOREST);
            }
            @Override
            public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                ((SpriteDrawable) newCollisionSortProperty.getBackground()).getSprite().setColor(Color.DARK_GRAY);
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {
                AddProperty addProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects, "collisionSort", "");
                map.executeCommand(addProperty);
                return false;
            }
        });
    }

    private void createCollisionSortBack()
    {
        SpriteDrawable spriteDrawable;
        Table table;
        FieldFieldPropertyValuePropertyField fieldFieldPropertyValuePropertyField;
        float pad = Gdx.graphics.getHeight() / 35;

        this.newCollisionSortBackProperty = new Table();
        spriteDrawable = new SpriteDrawable(new Sprite(new Texture("ui/whitePixel.png")));
        spriteDrawable.getSprite().setColor(Color.DARK_GRAY);
        this.newCollisionSortBackProperty.background(spriteDrawable);
        this.newCollisionSortBackProperty.add(new Label("Collision Sort Back", this.skin)).padTop(pad / 2).row();
        table = new Table();
        fieldFieldPropertyValuePropertyField = new FieldFieldPropertyValuePropertyField("collisionSortBack", "", this.skin, null, null, false);
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
        fieldFieldPropertyValuePropertyField.clearListeners();
        table.add(fieldFieldPropertyValuePropertyField).padLeft(pad).padRight(pad).padTop(pad / 2).padBottom(pad).row();
        this.newCollisionSortBackProperty.add(table);
        this.newCollisionSortBackProperty.setTouchable(Touchable.enabled);
        this.newCollisionSortBackProperty.addListener(new InputListener(){
            @Override
            public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                ((SpriteDrawable) newCollisionSortBackProperty.getBackground()).getSprite().setColor(Color.FOREST);
            }
            @Override
            public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                ((SpriteDrawable) newCollisionSortBackProperty.getBackground()).getSprite().setColor(Color.DARK_GRAY);
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {
                AddProperty addProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects, "collisionSortBack", "");
                map.executeCommand(addProperty);
                return false;
            }
        });
    }

    private void createDisablePerspective()
    {
        SpriteDrawable spriteDrawable;
        Table table;
        FieldFieldPropertyValuePropertyField fieldFieldPropertyValuePropertyField;
        float pad = Gdx.graphics.getHeight() / 35;

        this.newDisablePerspectiveProperty = new Table();
        spriteDrawable = new SpriteDrawable(new Sprite(new Texture("ui/whitePixel.png")));
        spriteDrawable.getSprite().setColor(Color.DARK_GRAY);
        this.newDisablePerspectiveProperty.background(spriteDrawable);
        this.newDisablePerspectiveProperty.add(new Label("Disable Perspective", this.skin)).padTop(pad / 2).row();
        table = new Table();
        fieldFieldPropertyValuePropertyField = new FieldFieldPropertyValuePropertyField("disablePerspective", "", this.skin, null, null, false);
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
        fieldFieldPropertyValuePropertyField.clearListeners();
        table.add(fieldFieldPropertyValuePropertyField).padLeft(pad).padRight(pad).padTop(pad / 2).padBottom(pad).row();
        this.newDisablePerspectiveProperty.add(table);
        this.newDisablePerspectiveProperty.setTouchable(Touchable.enabled);
        this.newDisablePerspectiveProperty.addListener(new InputListener(){
            @Override
            public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                ((SpriteDrawable) newDisablePerspectiveProperty.getBackground()).getSprite().setColor(Color.FOREST);
            }
            @Override
            public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                ((SpriteDrawable) newDisablePerspectiveProperty.getBackground()).getSprite().setColor(Color.DARK_GRAY);
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {
                AddProperty addProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects, "disablePerspective", "");
                map.executeCommand(addProperty);
                return false;
            }
        });
    }

    private void createFadeLimit()
    {
        SpriteDrawable spriteDrawable;
        Table table;
        FieldFieldPropertyValuePropertyField fieldFieldPropertyValuePropertyField;
        float pad = Gdx.graphics.getHeight() / 35;

        this.newFadeLimitProperty = new Table();
        spriteDrawable = new SpriteDrawable(new Sprite(new Texture("ui/whitePixel.png")));
        spriteDrawable.getSprite().setColor(Color.DARK_GRAY);
        this.newFadeLimitProperty.background(spriteDrawable);
        this.newFadeLimitProperty.add(new Label("Fade Limit", this.skin)).padTop(pad / 2).row();
        table = new Table();
        fieldFieldPropertyValuePropertyField = new FieldFieldPropertyValuePropertyField("fadeLimit", "", this.skin, null, null, false);
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
        fieldFieldPropertyValuePropertyField.clearListeners();
        table.add(fieldFieldPropertyValuePropertyField).padLeft(pad).padRight(pad).padTop(pad / 2).padBottom(pad).row();
        this.newFadeLimitProperty.add(table);
        this.newFadeLimitProperty.setTouchable(Touchable.enabled);
        this.newFadeLimitProperty.addListener(new InputListener(){
            @Override
            public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                ((SpriteDrawable) newFadeLimitProperty.getBackground()).getSprite().setColor(Color.FOREST);
            }
            @Override
            public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                ((SpriteDrawable) newFadeLimitProperty.getBackground()).getSprite().setColor(Color.DARK_GRAY);
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {
                AddProperty addProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects, "fadeLimit", "");
                map.executeCommand(addProperty);
                return false;
            }
        });
    }

    private void createAnimated()
    {
        SpriteDrawable spriteDrawable;
        Table table;
        FieldFieldPropertyValuePropertyField fieldFieldPropertyValuePropertyField;
        float pad = Gdx.graphics.getHeight() / 35;

        this.newAnimatedProperty = new Table();
        spriteDrawable = new SpriteDrawable(new Sprite(new Texture("ui/whitePixel.png")));
        spriteDrawable.getSprite().setColor(Color.DARK_GRAY);
        this.newAnimatedProperty.background(spriteDrawable);
        this.newAnimatedProperty.add(new Label("Animated", this.skin)).padTop(pad / 2).row();
        table = new Table();
        fieldFieldPropertyValuePropertyField = new FieldFieldPropertyValuePropertyField("animated", "...", this.skin, null, null, false);
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
        fieldFieldPropertyValuePropertyField.clearListeners();
        table.add(fieldFieldPropertyValuePropertyField).padLeft(pad).padRight(pad).padTop(pad / 2).padBottom(pad / 6).row();
        fieldFieldPropertyValuePropertyField = new FieldFieldPropertyValuePropertyField("fps", "...", this.skin, null, null, false);
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
        fieldFieldPropertyValuePropertyField.clearListeners();
        table.add(fieldFieldPropertyValuePropertyField).padLeft(pad).padRight(pad).padTop(pad / 6).padBottom(pad / 6).row();
        fieldFieldPropertyValuePropertyField = new FieldFieldPropertyValuePropertyField("pingpong", "...", this.skin, null, null, false);
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
        fieldFieldPropertyValuePropertyField.clearListeners();
        table.add(fieldFieldPropertyValuePropertyField).padLeft(pad).padRight(pad).padTop(pad / 6).padBottom(pad / 6).row();
        fieldFieldPropertyValuePropertyField = new FieldFieldPropertyValuePropertyField("loop", "...", this.skin, null, null, false);
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
        fieldFieldPropertyValuePropertyField.clearListeners();
        table.add(fieldFieldPropertyValuePropertyField).padLeft(pad).padRight(pad).padTop(pad / 6).padBottom(pad / 6).row();
        fieldFieldPropertyValuePropertyField = new FieldFieldPropertyValuePropertyField("random", "...", this.skin, null, null, false);
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
        fieldFieldPropertyValuePropertyField.clearListeners();
        table.add(fieldFieldPropertyValuePropertyField).padLeft(pad).padRight(pad).padTop(pad / 6).padBottom(pad).row();
        this.newAnimatedProperty.add(table);
        this.newAnimatedProperty.setTouchable(Touchable.enabled);
        this.newAnimatedProperty.addListener(new InputListener(){
            @Override
            public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                ((SpriteDrawable) newAnimatedProperty.getBackground()).getSprite().setColor(Color.FOREST);
            }
            @Override
            public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                ((SpriteDrawable) newAnimatedProperty.getBackground()).getSprite().setColor(Color.DARK_GRAY);
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {
                AddProperty chainedProperty;
                AddProperty addProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects, "animated", "Value");
                chainedProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects, "fps", "10");
                addProperty.addAddPropertyCommandToChain(chainedProperty);
                chainedProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects, "pingpong", "Value");
                addProperty.addAddPropertyCommandToChain(chainedProperty);
                chainedProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects, "loop", "Value");
                addProperty.addAddPropertyCommandToChain(chainedProperty);
                chainedProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects, "random", "Value");
                addProperty.addAddPropertyCommandToChain(chainedProperty);
                map.executeCommand(addProperty);
                return false;
            }
        });
    }

    private void createWind()
    {
        SpriteDrawable spriteDrawable;
        Table table;
        FieldFieldPropertyValuePropertyField fieldFieldPropertyValuePropertyField;
        float pad = Gdx.graphics.getHeight() / 35;

        this.newBlowableProperty = new Table();
        spriteDrawable = new SpriteDrawable(new Sprite(new Texture("ui/whitePixel.png")));
        spriteDrawable.getSprite().setColor(Color.DARK_GRAY);
        this.newBlowableProperty.background(spriteDrawable);
        this.newBlowableProperty.add(new Label("Blowable", this.skin)).padTop(pad / 2).row();
        table = new Table();
        fieldFieldPropertyValuePropertyField = new FieldFieldPropertyValuePropertyField("wind", "...", this.skin, null, null, false);
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
        fieldFieldPropertyValuePropertyField.clearListeners();
        table.add(fieldFieldPropertyValuePropertyField).padLeft(pad).padRight(pad).padTop(pad / 2).padBottom(pad / 6).row();
        fieldFieldPropertyValuePropertyField = new FieldFieldPropertyValuePropertyField("skewWind", "...", this.skin, null, null, false);
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
        fieldFieldPropertyValuePropertyField.clearListeners();
        table.add(fieldFieldPropertyValuePropertyField).padLeft(pad).padRight(pad).padTop(pad / 6).padBottom(pad / 6).row();
        fieldFieldPropertyValuePropertyField = new FieldFieldPropertyValuePropertyField("fpsWind", "...", this.skin, null, null, false);
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
        fieldFieldPropertyValuePropertyField.clearListeners();
        table.add(fieldFieldPropertyValuePropertyField).padLeft(pad).padRight(pad).padTop(pad / 6).padBottom(pad / 6).row();
        fieldFieldPropertyValuePropertyField = new FieldFieldPropertyValuePropertyField("blowResistTop", "...", this.skin, null, null, false);
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
        fieldFieldPropertyValuePropertyField.clearListeners();
        table.add(fieldFieldPropertyValuePropertyField).padLeft(pad).padRight(pad).padTop(pad / 6).padBottom(pad / 6).row();
        fieldFieldPropertyValuePropertyField = new FieldFieldPropertyValuePropertyField("blowResit", "...", this.skin, null, null, false);
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
        fieldFieldPropertyValuePropertyField.clearListeners();
        table.add(fieldFieldPropertyValuePropertyField).padLeft(pad).padRight(pad).padTop(pad / 6).padBottom(pad).row();
        this.newBlowableProperty.add(table);
        this.newBlowableProperty.setTouchable(Touchable.enabled);
        this.newBlowableProperty.addListener(new InputListener(){
            @Override
            public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                ((SpriteDrawable) newBlowableProperty.getBackground()).getSprite().setColor(Color.FOREST);
            }
            @Override
            public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                ((SpriteDrawable) newBlowableProperty.getBackground()).getSprite().setColor(Color.DARK_GRAY);
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {
                AddProperty chainedProperty;
                AddProperty addProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects, "wind", "Value");
                chainedProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects, "skewWind", "Value");
                addProperty.addAddPropertyCommandToChain(chainedProperty);
                chainedProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects, "fpsWind", "Value");
                addProperty.addAddPropertyCommandToChain(chainedProperty);
                chainedProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects, "blowResistTop", "17.5");
                addProperty.addAddPropertyCommandToChain(chainedProperty);
                chainedProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects, "blowResist", "17.5");
                addProperty.addAddPropertyCommandToChain(chainedProperty);
                map.executeCommand(addProperty);
                return false;
            }
        });
    }

    public void createGround()
    {
        SpriteDrawable spriteDrawable;
        Table table;
        FieldFieldPropertyValuePropertyField fieldFieldPropertyValuePropertyField;
        float pad = Gdx.graphics.getHeight() / 35;

        this.newGroundProperty = new Table();
        spriteDrawable = new SpriteDrawable(new Sprite(new Texture("ui/whitePixel.png")));
        spriteDrawable.getSprite().setColor(Color.DARK_GRAY);
        this.newGroundProperty.background(spriteDrawable);
        this.newGroundProperty.add(new Label("Ground", this.skin)).padTop(pad / 2).row();
        table = new Table();
        fieldFieldPropertyValuePropertyField = new FieldFieldPropertyValuePropertyField("ground", "", this.skin, null, null, false);
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
        fieldFieldPropertyValuePropertyField.clearListeners();
        table.add(fieldFieldPropertyValuePropertyField).padLeft(pad).padRight(pad).padTop(pad / 2).padBottom(pad).row();
        this.newGroundProperty.add(table);
        this.newGroundProperty.setTouchable(Touchable.enabled);
        this.newGroundProperty.addListener(new InputListener(){
            @Override
            public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                ((SpriteDrawable) newGroundProperty.getBackground()).getSprite().setColor(Color.FOREST);
            }
            @Override
            public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                ((SpriteDrawable) newGroundProperty.getBackground()).getSprite().setColor(Color.DARK_GRAY);
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {
                AddProperty addProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects, "ground", "");
                map.executeCommand(addProperty);
                return false;
            }
        });
    }

    public void createDustType()
    {
        SpriteDrawable spriteDrawable;
        Table table;
        FieldFieldPropertyValuePropertyField fieldFieldPropertyValuePropertyField;
        float pad = Gdx.graphics.getHeight() / 35;

        this.newDustTypeProperty = new Table();
        spriteDrawable = new SpriteDrawable(new Sprite(new Texture("ui/whitePixel.png")));
        spriteDrawable.getSprite().setColor(Color.DARK_GRAY);
        this.newDustTypeProperty.background(spriteDrawable);
        this.newDustTypeProperty.add(new Label("Dust Type", this.skin)).padTop(pad / 2).row();
        table = new Table();
        fieldFieldPropertyValuePropertyField = new FieldFieldPropertyValuePropertyField("dustType", "...dirt...", this.skin, null, null, false);
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
        fieldFieldPropertyValuePropertyField.clearListeners();
        table.add(fieldFieldPropertyValuePropertyField).padLeft(pad).padRight(pad).padTop(pad / 2).padBottom(pad).row();
        this.newDustTypeProperty.add(table);
        this.newDustTypeProperty.setTouchable(Touchable.enabled);
        this.newDustTypeProperty.addListener(new InputListener(){
            @Override
            public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                ((SpriteDrawable) newDustTypeProperty.getBackground()).getSprite().setColor(Color.FOREST);
            }
            @Override
            public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                ((SpriteDrawable) newDustTypeProperty.getBackground()).getSprite().setColor(Color.DARK_GRAY);
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {
                AddProperty addProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects, "dustType", "");
                map.executeCommand(addProperty);
                return false;
            }
        });
    }

    private Table createPropertyGroup(String title, SelectionType[] selectionTypes, PropertyValue... properties)
    {
        SpriteDrawable spriteDrawable;
        Table table;
        FieldFieldPropertyValuePropertyField fieldFieldPropertyValuePropertyField;
        float pad = Gdx.graphics.getHeight() / 35;

        Table propertyTable = new Table();
        propertyTable.setUserObject(selectionTypes);
        spriteDrawable = new SpriteDrawable(new Sprite(new Texture("ui/whitePixel.png")));
        spriteDrawable.getSprite().setColor(Color.DARK_GRAY);
        propertyTable.background(spriteDrawable);
        propertyTable.add(new Label(title, this.skin)).padTop(pad / 2).row();
        table = new Table();
        for(int i = 0; i < properties.length; i ++)
        {
            fieldFieldPropertyValuePropertyField = new FieldFieldPropertyValuePropertyField(properties[i].property, "...", this.skin, null, null, false);
            fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
            fieldFieldPropertyValuePropertyField.clearListeners();
            if(i < properties.length - 1)
                table.add(fieldFieldPropertyValuePropertyField).padLeft(pad).padRight(pad).padTop(pad / 2).padBottom(pad / 6).row();
            else
                table.add(fieldFieldPropertyValuePropertyField).padLeft(pad).padRight(pad).padTop(pad / 2).padBottom(pad).row();
        }
        propertyTable.add(table);
        propertyTable.setTouchable(Touchable.enabled);
        propertyTable.addListener(new InputListener(){
            @Override
            public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                ((SpriteDrawable) propertyTable.getBackground()).getSprite().setColor(Color.FOREST);
            }
            @Override
            public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                ((SpriteDrawable) propertyTable.getBackground()).getSprite().setColor(Color.DARK_GRAY);
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {
                AddProperty chainedProperty;
                AddProperty addProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects, properties[0].property, properties[0].value);

                for(int i = 1; i < properties.length; i ++)
                {
                    chainedProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects, properties[i].property, properties[i].value);
                    addProperty.addAddPropertyCommandToChain(chainedProperty);
                }

                map.executeCommand(addProperty);
                return false;
            }
        });
        return propertyTable;
    }

    public class PropertyValue
    {
        public String property;
        public String value;
        public PropertyValue(String property, String value)
        {
            this.property = property;
            this.value = value;
        }
    }

    public enum SelectionType
    {
        POINT, POLYGON, SPRITE, SPRITETOOL, MAP, OBJECTLAYER, SPRITELAYER
    }
}
