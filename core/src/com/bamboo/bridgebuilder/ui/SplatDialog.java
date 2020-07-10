package com.bamboo.bridgebuilder.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class SplatDialog extends Window
{
    private TextButton close;

    private Skin skin;

    private Table table;
    private Label minSpawnLabel;
    private Label maxSpawnLabel;
    private Label xMaxDisplacementLabel;
    private Label yMaxDisplacementLabel;
    private TextField minSpawnField;
    private TextField maxSpawnField;
    private TextField xMaxDisplacementField;
    private TextField yMaxDisplacementField;

    public SplatDialog(Stage stage, Skin skin)
    {
        super("Splat", skin);
        this.skin = skin;

        this.table = new Table();

        this.minSpawnLabel = new Label("Min spawn amount: ", skin);
        this.maxSpawnLabel = new Label("Max spawn amount: ", skin);
        this.xMaxDisplacementLabel = new Label("X max displacement: ", skin);
        this.yMaxDisplacementLabel = new Label("Y max displacement", skin);
        this.minSpawnField = new TextField("1", skin);
        this.maxSpawnField = new TextField("1", skin);
        this.xMaxDisplacementField = new TextField("0", skin);
        this.yMaxDisplacementField = new TextField("0", skin);

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

        this.table.add(this.minSpawnLabel).padBottom(15);
        this.table.add(this.minSpawnField).padBottom(15).row();
        this.table.add(this.maxSpawnLabel).padBottom(15);
        this.table.add(this.maxSpawnField).padBottom(15).row();
        this.table.add(this.xMaxDisplacementLabel).padBottom(15);
        this.table.add(this.xMaxDisplacementField).padBottom(15).row();
        this.table.add(this.yMaxDisplacementLabel).padBottom(15);
        this.table.add(this.yMaxDisplacementField).padBottom(15).row();
        this.table.add(this.close);

        this.add(this.table);

        setSize(Gdx.graphics.getWidth() / 3f, Gdx.graphics.getHeight() / 2f);
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

    public int getMinSpawn()
    {
        int num = 1;
        try { num = Integer.parseInt(this.minSpawnField.getText()); } catch (NumberFormatException e){}
        return num;
    }

    public int getMaxSpawn()
    {
        int num = 1;
        try { num = Integer.parseInt(this.maxSpawnField.getText()); } catch (NumberFormatException e){}
        return num;
    }

    public float getMaxXDisplacement()
    {
        float num = 0;
        try { num = Float.parseFloat(this.xMaxDisplacementField.getText()); } catch (NumberFormatException e){}
        return num;
    }

    public float getMaxYDisplacement()
    {
        float num = 0;
        try { num = Float.parseFloat(this.yMaxDisplacementField.getText()); } catch (NumberFormatException e){}
        return num;
    }

}
