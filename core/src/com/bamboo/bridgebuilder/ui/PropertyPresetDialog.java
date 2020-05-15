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
import com.bamboo.bridgebuilder.map.MapPolygon;
import com.bamboo.bridgebuilder.ui.propertyMenu.PropertyTools;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.FieldFieldPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.LightPropertyField;

import static com.bamboo.bridgebuilder.ui.propertyMenu.PropertyMenu.toolHeight;

public class PropertyPresetDialog extends Window
{
    private Table newTopProperty;
    private Table newLightProperty;

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

        this.add(presetTable).row();
        this.add(close);

        setSize(Gdx.graphics.getWidth() / 1.75f, Gdx.graphics.getHeight() / 1.75f);
        this.setPosition((stage.getWidth() / 2f), (stage.getHeight() / 2f), Align.center);
        stage.addActor(this);
        setVisible(false);
    }

    public void rebuild()
    {
        this.presetTable.clearChildren();

        if(map.selectedObjects.size > 0)
        {
            boolean allPoints = true;
            for(int i = 0; i < map.selectedObjects.size; i++)
            {
                if(map.selectedObjects.get(i) instanceof MapPolygon)
                {
                    allPoints = false;
                    break;
                }
            }
            if(allPoints)
            {
                this.presetTable.add(newLightProperty).pad(5);
            }
        }
        else if(map.spriteMenu.selectedSpriteTools.size > 0)
        {
            this.presetTable.add(newTopProperty).pad(5);
        }
        else if(map.selectedLayer != null)
        {

        }
        else
        {

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
        this.newLightProperty.add(new Label("Light", this.skin)).padTop(pad / 2).row();
        table = new Table();
        lightPropertyField = new LightPropertyField(this.skin, null, null, false, 1, 1, 1, 1, 10, 100);
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
}
