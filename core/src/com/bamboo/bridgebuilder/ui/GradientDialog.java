package com.bamboo.bridgebuilder.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class GradientDialog extends Window
{
    private TextButton close;

    private Skin skin;

    private Table table;
    private Label fromColorLabel;
    private Label toColorLabel;
    private TextField fromColorFieldR;
    private TextField fromColorFieldG;
    private TextField fromColorFieldB;
    private TextField fromColorFieldA;
    private TextField toColorFieldR;
    private TextField toColorFieldG;
    private TextField toColorFieldB;
    private TextField toColorFieldA;

    public GradientDialog(Stage stage, Skin skin)
    {
        super("Gradient", skin);
        this.skin = skin;

        this.table = new Table();

        this.fromColorLabel = new Label("From: ", skin);
        this.toColorLabel = new Label("To: ", skin);
        this.fromColorFieldR = new TextField("1.0", skin);
        this.fromColorFieldG = new TextField("1.0", skin);
        this.fromColorFieldB = new TextField("1.0", skin);
        this.fromColorFieldA = new TextField("1.0", skin);
        this.toColorFieldR = new TextField("1.0", skin);
        this.toColorFieldG = new TextField("1.0", skin);
        this.toColorFieldB = new TextField("1.0", skin);
        this.toColorFieldA = new TextField("1.0", skin);

        this.fromColorFieldR.setColor(Color.RED);
        this.fromColorFieldG.setColor(Color.GREEN);
        this.fromColorFieldB.setColor(Color.BLUE);
        this.toColorFieldR.setColor(Color.RED);
        this.toColorFieldG.setColor(Color.GREEN);
        this.toColorFieldB.setColor(Color.BLUE);

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

        this.table.add(this.fromColorLabel).padRight(5).padBottom(15);
        this.table.add(this.fromColorFieldR).width(50).padBottom(15);
        this.table.add(this.fromColorFieldG).width(50).padBottom(15);
        this.table.add(this.fromColorFieldB).width(50).padBottom(15);
        this.table.add(this.fromColorFieldA).width(50).padBottom(15).row();
        this.table.add(this.toColorLabel).padRight(5).padTop(15);
        this.table.add(this.toColorFieldR).width(50).padTop(15);
        this.table.add(this.toColorFieldG).width(50).padTop(15);
        this.table.add(this.toColorFieldB).width(50).padTop(15);
        this.table.add(this.toColorFieldA).width(50).padTop(15).row();
        this.table.add(this.close);

        this.add(this.table);

        setSize(Gdx.graphics.getWidth() / 4f, Gdx.graphics.getHeight() / 4f);
        this.setPosition((stage.getWidth() / 2f), (stage.getHeight() / 2f), Align.center);
        stage.addActor(this);
        setVisible(false);

        InputListener fromListener = new InputListener()
        {
            public boolean keyTyped(InputEvent event, char character)
            {
                fromColorLabel.setColor(getFromR(), getFromG(), getFromB(), 1);
                return false;
            }
        };
        this.fromColorFieldR.addListener(fromListener);
        this.fromColorFieldG.addListener(fromListener);
        this.fromColorFieldB.addListener(fromListener);

        InputListener toListener = new InputListener()
        {
            public boolean keyTyped(InputEvent event, char character)
            {
                toColorLabel.setColor(getToR(), getToG(), getToB(), 1);
                return false;
            }
        };
        this.toColorFieldR.addListener(toListener);
        this.toColorFieldG.addListener(toListener);
        this.toColorFieldB.addListener(toListener);
    }

    public void close()
    {
        this.setVisible(false);
    }

    public void open()
    {
        this.setVisible(true);
    }

    public float getFromR()
    {
        float r = 1;
        try { r = Float.parseFloat(this.fromColorFieldR.getText()); } catch (NumberFormatException e){}
        return r;
    }

    public float getFromG()
    {
        float g = 1;
        try { g = Float.parseFloat(this.fromColorFieldG.getText()); } catch (NumberFormatException e){}
        return g;
    }

    public float getFromB()
    {
        float b = 1;
        try { b = Float.parseFloat(this.fromColorFieldB.getText()); } catch (NumberFormatException e){}
        return b;
    }

    public float getFromA()
    {
        float a = 1;
        try { a = Float.parseFloat(this.fromColorFieldA.getText()); } catch (NumberFormatException e){}
        return a;
    }

    public float getToR()
    {
        float r = 1;
        try { r = Float.parseFloat(this.toColorFieldR.getText()); } catch (NumberFormatException e){}
        return r;
    }

    public float getToG()
    {
        float g = 1;
        try { g = Float.parseFloat(this.toColorFieldG.getText()); } catch (NumberFormatException e){}
        return g;
    }

    public float getToB()
    {
        float b = 1;
        try { b = Float.parseFloat(this.toColorFieldB.getText()); } catch (NumberFormatException e){}
        return b;
    }

    public float getToA()
    {
        float a = 1;
        try { a = Float.parseFloat(this.toColorFieldA.getText()); } catch (NumberFormatException e){}
        return a;
    }
}
