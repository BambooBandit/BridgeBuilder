package com.bamboo.bridgebuilder.ui;

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
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapObject;
import com.bamboo.bridgebuilder.map.MapPoint;
import com.bamboo.bridgebuilder.map.MapPolygon;
import com.bamboo.bridgebuilder.ui.propertyMenu.PropertyTools;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.FieldFieldPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.LightPropertyField;

import static com.bamboo.bridgebuilder.ui.propertyMenu.PropertyMenu.toolHeight;

public class PropertyPresetDialog extends Window
{
    private Table newTopProperty;
    private Table newLightProperty;
    private Table newBlockedProperty;
    private Table newRayhandlerProperty;
    private Table newPerspectiveProperty;

    private Table presetTable;

    private TextButton close;

    private Skin skin;

    private Map map;

    public PropertyPresetDialog(Map map, Stage stage, Skin skin)
    {
        super("Property Presets", skin);
        this.skin = skin;
        this.map = map;
        this.presetTable = new Table();

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

        this.add(this.presetTable).row();
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
            }
        }
        else if(this.map.spriteMenu.selectedSpriteTools.size > 0)
        {
            this.presetTable.add(this.newTopProperty).pad(5);
        }
        else if(this.map.selectedLayer != null)
        {
            this.presetTable.add(this.newRayhandlerProperty).pad(5);
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
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 4.5f, toolHeight);
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
        lightPropertyField.setSize(Gdx.graphics.getWidth() / 4.5f, toolHeight);
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
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 4.5f, toolHeight);
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
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 4.5f, toolHeight);
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
        fieldFieldPropertyValuePropertyField = new FieldFieldPropertyValuePropertyField("topPerspective", "...", this.skin, null, null, false);
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 4.5f, toolHeight);
        fieldFieldPropertyValuePropertyField.clearListeners();
        table.add(fieldFieldPropertyValuePropertyField).padLeft(pad).padRight(pad).padTop(pad / 2).padBottom(pad / 6).row();
        fieldFieldPropertyValuePropertyField = new FieldFieldPropertyValuePropertyField("bottomPerspective", "...", this.skin, null, null, false);
        fieldFieldPropertyValuePropertyField.setSize(Gdx.graphics.getWidth() / 4.5f, toolHeight);
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
                AddProperty addProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects, "topPerspective", "1");
                AddProperty addSecondProperty = new AddProperty(map, PropertyTools.NEW, map.selectedLayer, map.spriteMenu.selectedSpriteTools, map.selectedObjects, "Perspective", "1");
                addProperty.addAddPropertyCommandToChain(addSecondProperty);
                map.executeCommand(addProperty);
                return false;
            }
        });
    }
}
