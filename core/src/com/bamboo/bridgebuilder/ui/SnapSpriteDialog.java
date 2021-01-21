package com.bamboo.bridgebuilder.ui;

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
import com.bamboo.bridgebuilder.commands.SnapMapSpriteEdge;
import com.bamboo.bridgebuilder.commands.SnapMapSpriteFlicker;
import com.bamboo.bridgebuilder.map.LayerChild;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapSprite;

public class SnapSpriteDialog extends Window
{
    private TextButton edge;
    private TextButton flicker;
    private TextButton close;

    private Skin skin;

    private Table choiceTable;
    private Table table;

    private Map map;

    private LayerChild from;
    private LayerChild to;

    public SnapSpriteDialog(Stage stage, Skin skin, Map map, LayerChild from, LayerChild to)
    {
        super("Splat", skin);
        this.skin = skin;

        this.map = map;
        this.from = from;
        this.to = to;

        this.choiceTable = new Table();
        this.table = new Table();

        this.edge = new TextButton("Edge", skin);
        this.flicker = new TextButton("Flicker", skin);

        this.edge.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if(from instanceof MapSprite && (to instanceof MapSprite || to == null))
                {
                    edge();
                    close();
                }
            }
        });

        this.flicker.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if(to instanceof MapSprite || to == null)
                {
                    flicker();
                    close();
                }
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

        this.choiceTable.add(this.edge).padBottom(15);
        this.choiceTable.add(this.flicker).padBottom(15);
        this.table.add(this.choiceTable).row();
        this.table.add(this.close);

        this.add(this.table);

        setSize(Gdx.graphics.getWidth() / 3f, Gdx.graphics.getHeight() / 2f);
        this.setPosition((stage.getWidth() / 2f), (stage.getHeight() / 2f), Align.center);
        stage.addActor(this);
        setVisible(true);
    }

    private void edge()
    {
        SnapMapSpriteEdge snapMapSpriteEdge = new SnapMapSpriteEdge((MapSprite)this.from, (MapSprite)this.to);
        this.map.input.snapFromThisObject = null;
        this.map.executeCommand(snapMapSpriteEdge);
    }

    private void flicker()
    {
        SnapMapSpriteFlicker snapMapSpriteFlicker = new SnapMapSpriteFlicker(this.from, (MapSprite)this.to);
        this.map.input.snapFromThisObject = null;
        this.map.executeCommand(snapMapSpriteFlicker);
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
