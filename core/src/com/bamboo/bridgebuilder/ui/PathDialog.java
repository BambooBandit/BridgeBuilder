package com.bamboo.bridgebuilder.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class PathDialog extends Window
{
    private TextButton close;

    private Skin skin;

    private Table table;

    private Label radiusLabel;
    private TextField radiusField;

    public PathDialog(Stage stage, Skin skin)
    {
        super("Path", skin);
        this.skin = skin;

        this.table = new Table();

        this.radiusLabel = new Label("Radius: ", skin);
        this.radiusField = new TextField("5", skin);

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

        this.table.add(this.radiusLabel).padBottom(15);
        this.table.add(this.radiusField).width(Gdx.graphics.getWidth() / 40f).padBottom(15).row();
        this.table.add(this.close);

        this.add(this.table);

        setSize(Gdx.graphics.getWidth() / 6.5f, Gdx.graphics.getHeight() / 2.75f);
        this.setPosition((stage.getWidth() / 2f), (stage.getHeight() / 2f), Align.center);
        stage.addActor(this);
        setVisible(false);
    }

    public void close()
    {
        this.setVisible(false);
    }

    public void open()
    {
        this.setVisible(true);
    }

    public float getRadius()
    {
        float radius = 5;
        try{
            radius = Float.parseFloat(radiusField.getText());
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        return radius;
    }
}
