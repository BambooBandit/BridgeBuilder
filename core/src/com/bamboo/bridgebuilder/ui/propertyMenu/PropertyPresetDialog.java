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
import com.bamboo.bridgebuilder.commands.AddProperty;
import com.bamboo.bridgebuilder.map.*;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.FieldFieldPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.LightPropertyField;

import static com.bamboo.bridgebuilder.ui.propertyMenu.PropertyMenu.toolHeight;

public class PropertyPresetDialog extends Window
{
    private Table newTopProperty;
    private Table newLightProperty;
    private Table newBlockedProperty;
    private Table newRayhandlerProperty;
    private Table newDisablePerspectiveProperty;
    private Table newPerspectiveProperty;
    private Table newGroundProperty;
    private Table newDustTypeProperty;

    private Table presetTable;
    private ScrollPane scrollPane;

    private TextButton close;

    private Skin skin;

    private Map map;

    public PropertyPresetDialog(Map map, Stage stage, Skin skin)
    {
        super("Property Presets", skin);
        this.skin = skin;
        this.map = map;
        this.presetTable = new Table();
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
                this.presetTable.add(this.newLightProperty).pad(5);
            }
            else if(allPolygons)
            {
                this.presetTable.add(this.newBlockedProperty).pad(5);
                this.presetTable.add(this.newDustTypeProperty).pad(5);
            }
        }
        else if(this.map.spriteMenu.selectedSpriteTools.size > 0)
        {
            this.presetTable.add(this.newTopProperty).pad(5);
        }
        else if(this.map.selectedLayer != null)
        {
            if(this.map.selectedLayer instanceof ObjectLayer)
                this.presetTable.add(this.newRayhandlerProperty).pad(5);
            else if(this.map.selectedLayer instanceof SpriteLayer)
            {
                this.presetTable.add(this.newDisablePerspectiveProperty).pad(5);
                this.presetTable.add(this.newPerspectiveProperty).pad(5);
                this.presetTable.add(this.newGroundProperty).pad(5);
            }
        }
        else
        {
            this.presetTable.add(this.newPerspectiveProperty).pad(5);
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

    private void createPropertyPresets()
    {
        this.createTop();
        this.createLight();
        this.createBlocked();
        this.createRayhandler();
        this.createPerspective();
        this.createDisablePerspective();
        this.createGround();
        this.createDustType();
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
                AddProperty addProperty = new AddProperty(map, PropertyTools.NEWLIGHT, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects);
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

    private void createPerspective()
    {
        SpriteDrawable spriteDrawable;
        Table table;
        FieldFieldPropertyValuePropertyField fieldFieldPropertyValuePropertyField;
        float pad = Gdx.graphics.getHeight() / 35;

        this.newPerspectiveProperty = new Table();
        spriteDrawable = new SpriteDrawable(new Sprite(new Texture("ui/whitePixel.png")));
        spriteDrawable.getSprite().setColor(Color.DARK_GRAY);
        this.newPerspectiveProperty.background(spriteDrawable);
        this.newPerspectiveProperty.add(new Label("Perspective", this.skin)).padTop(pad / 2).row();
        table = new Table();
        fieldFieldPropertyValuePropertyField = new FieldFieldPropertyValuePropertyField("skew", "...", this.skin, null, null, false);
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
        fieldFieldPropertyValuePropertyField.clearListeners();
        table.add(fieldFieldPropertyValuePropertyField).padLeft(pad).padRight(pad).padTop(pad / 2).padBottom(pad / 6).row();
        fieldFieldPropertyValuePropertyField = new FieldFieldPropertyValuePropertyField("antiDepth", "...", this.skin, null, null, false);
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
        fieldFieldPropertyValuePropertyField.clearListeners();
        table.add(fieldFieldPropertyValuePropertyField).padLeft(pad).padRight(pad).padTop(pad / 6).padBottom(pad / 6).row();
        fieldFieldPropertyValuePropertyField = new FieldFieldPropertyValuePropertyField("topScale", "...", this.skin, null, null, false);
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
        fieldFieldPropertyValuePropertyField.clearListeners();
        table.add(fieldFieldPropertyValuePropertyField).padLeft(pad).padRight(pad).padTop(pad / 6).padBottom(pad / 6).row();
        fieldFieldPropertyValuePropertyField = new FieldFieldPropertyValuePropertyField("bottomScale", "...", this.skin, null, null, false);
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 6f, toolHeight);
        fieldFieldPropertyValuePropertyField.clearListeners();
        table.add(fieldFieldPropertyValuePropertyField).padLeft(pad).padRight(pad).padTop(pad / 6).padBottom(pad).row();
        this.newPerspectiveProperty.add(table);
        this.newPerspectiveProperty.setTouchable(Touchable.enabled);
        this.newPerspectiveProperty.addListener(new InputListener(){
            @Override
            public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                ((SpriteDrawable) newPerspectiveProperty.getBackground()).getSprite().setColor(Color.FOREST);
            }
            @Override
            public void exit (InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                ((SpriteDrawable) newPerspectiveProperty.getBackground()).getSprite().setColor(Color.DARK_GRAY);
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
            {
                AddProperty chainedProperty;
                AddProperty addProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects, "skew", "0");
                chainedProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects, "antiDepth", "0");
                addProperty.addAddPropertyCommandToChain(chainedProperty);
                chainedProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects, "topScale", "1");
                addProperty.addAddPropertyCommandToChain(chainedProperty);
                chainedProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects, "bottomScale", "1");
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
}
