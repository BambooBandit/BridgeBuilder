package com.bamboo.bridgebuilder.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.FloatArray;
import com.bamboo.bridgebuilder.commands.Command;
import com.bamboo.bridgebuilder.commands.DrawMapPoint;
import com.bamboo.bridgebuilder.commands.DrawMapPolygon;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapSprite;

public class InstanceOrSpriteToolDialog extends Window
{
    private TextButton instance;
    private TextButton spriteTool;
    private TextButton close;

    private Skin skin;

    private Table choiceTable;
    private Table table;

    private Map map;

    private MapSprite mapSprite;

    public float objectX, objectY;
    public FloatArray mapPolygonVertices; // allows for seeing where you are clicking when constructing a new MapObject polygon


    public InstanceOrSpriteToolDialog(Stage stage, Skin skin, Map map, MapSprite mapSprite, FloatArray mapPolygonVertices, float x, float y)
    {
        super("Instance or Sprite Tool", skin);
        this.skin = skin;

        this.map = map;
        this.mapSprite = mapSprite;

        this.objectX = x;
        this.objectY = y;
        this.mapPolygonVertices = mapPolygonVertices;

        this.choiceTable = new Table();
        this.table = new Table();

        this.instance = new TextButton("Instance", skin);
        this.spriteTool = new TextButton("SpriteTool", skin);

        this.instance.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Command command;
                if(mapPolygonVertices == null)
                    command = new DrawMapPoint(map, mapSprite, objectX, objectY, false);
                else
                {
                    command = new DrawMapPolygon(map, mapSprite, mapPolygonVertices, objectX, objectY, false);
                    map.input.clearMapPolygonVertices(Input.Buttons.RIGHT);
                }
                map.executeCommand(command);
                close();
            }
        });

        this.spriteTool.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Command command;
                if(mapPolygonVertices == null)
                    command = new DrawMapPoint(map, mapSprite, objectX, objectY, true);
                else
                {
                    command = new DrawMapPolygon(map, mapSprite, mapPolygonVertices, objectX, objectY, true);
                    map.input.clearMapPolygonVertices(Input.Buttons.RIGHT);
                }
                map.executeCommand(command);
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

        this.choiceTable.add(this.instance).padBottom(15);
        this.choiceTable.add(this.spriteTool).padBottom(15);
        this.table.add(this.choiceTable).row();
        this.table.add(this.close);

        this.add(this.table);

        setSize(Gdx.graphics.getWidth() / 3f, Gdx.graphics.getHeight() / 2f);
        this.setPosition((stage.getWidth() / 2f), (stage.getHeight() / 2f), Align.center);
        stage.addActor(this);
        setVisible(true);
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
