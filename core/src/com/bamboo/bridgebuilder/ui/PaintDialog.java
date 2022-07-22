package com.bamboo.bridgebuilder.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class PaintDialog extends Window
{
    private TextButton close;

    private Skin skin;

    private Table table;
    private Label rLabel;
    private TextField rField;
    private Label gLabel;
    private TextField gField;
    private Label bLabel;
    private TextField bField;
    private Label aLabel;
    private TextField aField;
    private Label radiusLabel;
    private TextField radiusField;
    private Label strengthLabel;
    private TextField strengthField;

    public PaintDialog(Stage stage, Skin skin)
    {
        super("Paint", skin);
        this.skin = skin;

        this.table = new Table();

        this.rLabel = new Label("R: ", skin);
        this.rField = new TextField("1", skin);

        this.gLabel = new Label("G: ", skin);
        this.gField = new TextField("1", skin);

        this.bLabel = new Label("B: ", skin);
        this.bField = new TextField("1", skin);

        this.aLabel = new Label("A: ", skin);
        this.aField = new TextField("1", skin);

        this.radiusLabel = new Label("Radius: ", skin);
        this.radiusField = new TextField("10", skin);

        this.strengthLabel = new Label("Strength: ", skin);
        this.strengthField = new TextField(".5", skin);

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

        this.table.add(this.rLabel).padBottom(15);
        this.table.add(this.rField).width(Gdx.graphics.getWidth() / 40f).padBottom(15).row();
        this.table.add(this.gLabel).padBottom(15);
        this.table.add(this.gField).width(Gdx.graphics.getWidth() / 40f).padBottom(15).row();
        this.table.add(this.bLabel).padBottom(15);
        this.table.add(this.bField).width(Gdx.graphics.getWidth() / 40f).padBottom(15).row();
        this.table.add(this.aLabel).padBottom(15);
        this.table.add(this.aField).width(Gdx.graphics.getWidth() / 40f).padBottom(15).row();
        this.table.add(this.radiusLabel).padBottom(15);
        this.table.add(this.radiusField).width(Gdx.graphics.getWidth() / 40f).padBottom(15).row();
        this.table.add(this.strengthLabel).padBottom(15);
        this.table.add(this.strengthField).width(Gdx.graphics.getWidth() / 40f).padBottom(15).row();
        this.table.add(this.close);

        this.add(this.table);

        setSize(Gdx.graphics.getWidth() / 12.5f, Gdx.graphics.getHeight() / 3f);
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

    public float getR()
    {
        float r = 1;
        try{
            r = Float.parseFloat(rField.getText());
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        return r;
    }

    public float getG()
    {
        float g = 1;
        try{
            g = Float.parseFloat(gField.getText());
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        return g;
    }

    public float getB()
    {
        float b = 1;
        try{
            b = Float.parseFloat(bField.getText());
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        return b;
    }

    public float getA()
    {
        float a = 1;
        try{
            a = Float.parseFloat(aField.getText());
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        return a;
    }

    public float getRadius()
    {
        float radius = 10;
        try{
            radius = Float.parseFloat(radiusField.getText());
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        return radius;
    }

    public float getStrength()
    {
        float strength = .5f;
        try{
            strength = Float.parseFloat(strengthField.getText());
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        return strength;
    }

}
