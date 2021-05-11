package com.bamboo.bridgebuilder.ui.spriteMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.bamboo.bridgebuilder.commands.LayerOverride;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapSprite;
import com.bamboo.bridgebuilder.map.SpriteLayer;

public class LayerOverrideDialog extends Window
{
    private TextButton front;
    private TextButton back;
    private TextButton close;

    private Skin skin;

    private Table choiceTable;
    private Table table;

    private Map map;

    private MapSprite mapSprite;
    private SpriteLayer layer;

    public LayerOverrideDialog(Stage stage, Skin skin, Map map, SpriteLayer layer, MapSprite mapSprite)
    {
        super("Layer Override", skin);
        this.skin = skin;

        this.map = map;
        this.layer = layer;
        this.mapSprite = mapSprite;

        this.choiceTable = new Table();
        this.table = new Table();

        this.front = new TextButton("Front", skin);
        this.back = new TextButton("Back", skin);

        this.front.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            front();
            close();
            }
        });

        this.back.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            back();
            close();
            }
        });

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

        this.choiceTable.add(this.front).padBottom(15);
        this.choiceTable.add(this.back).padBottom(15);
        this.table.add(this.choiceTable).row();
        this.table.add(this.close);

        this.add(this.table);

        setSize(Gdx.graphics.getWidth() / 3f, Gdx.graphics.getHeight() / 2f);
        this.setPosition((stage.getWidth() / 2f), (stage.getHeight() / 2f), Align.center);
        stage.addActor(this);
        setVisible(true);
    }

    private void front()
    {
        LayerOverride layerOverride = new LayerOverride(this.layer, this.mapSprite, true);
        this.map.input.overrideLayer = null;
        this.map.executeCommand(layerOverride);
    }

    private void back()
    {
        LayerOverride layerOverride = new LayerOverride(this.layer, this.mapSprite, false);
        this.map.input.overrideLayer = null;
        this.map.executeCommand(layerOverride);
    }

    public void close()
    {
        this.setVisible(false);
    }

    public void open()
    {
        this.setVisible(true);
    }

}
