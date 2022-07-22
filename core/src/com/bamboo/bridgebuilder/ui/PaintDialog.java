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
    
    private Label leftRLabel;
    private TextField leftRField;
    private Label leftGLabel;
    private TextField leftGField;
    private Label leftBLabel;
    private TextField leftBField;
    private Label leftALabel;
    private TextField leftAField;
    private Label leftStrengthLabel;
    private TextField leftStrengthField;

    private Label rightRLabel;
    private TextField rightRField;
    private Label rightGLabel;
    private TextField rightGField;
    private Label rightBLabel;
    private TextField rightBField;
    private Label rightALabel;
    private TextField rightAField;
    private Label rightStrengthLabel;
    private TextField rightStrengthField;
    
    private Label radiusLabel;
    private TextField radiusField;

    public PaintDialog(Stage stage, Skin skin)
    {
        super("Paint", skin);
        this.skin = skin;

        this.table = new Table();

        this.leftRLabel = new Label("R: ", skin);
        this.leftRField = new TextField("1", skin);

        this.leftGLabel = new Label("G: ", skin);
        this.leftGField = new TextField("1", skin);

        this.leftBLabel = new Label("B: ", skin);
        this.leftBField = new TextField("1", skin);

        this.leftALabel = new Label("A: ", skin);
        this.leftAField = new TextField("1", skin);

        this.leftStrengthLabel = new Label("Strength: ", skin);
        this.leftStrengthField = new TextField(".5", skin);

        this.rightRLabel = new Label("R: ", skin);
        this.rightRField = new TextField("1", skin);

        this.rightGLabel = new Label("G: ", skin);
        this.rightGField = new TextField("1", skin);

        this.rightBLabel = new Label("B: ", skin);
        this.rightBField = new TextField("1", skin);

        this.rightALabel = new Label("A: ", skin);
        this.rightAField = new TextField("1", skin);

        this.rightStrengthLabel = new Label("Strength: ", skin);
        this.rightStrengthField = new TextField(".5", skin);

        this.radiusLabel = new Label("Radius: ", skin);
        this.radiusField = new TextField("10", skin);

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

        this.table.add(this.leftRLabel).padBottom(15);
        this.table.add(this.leftRField).width(Gdx.graphics.getWidth() / 40f);
        this.table.add(this.rightRLabel).padBottom(15);
        this.table.add(this.rightRField).width(Gdx.graphics.getWidth() / 40f).padBottom(15).row();
        this.table.add(this.leftGLabel).padBottom(15);
        this.table.add(this.leftGField).width(Gdx.graphics.getWidth() / 40f);
        this.table.add(this.rightGLabel).padBottom(15);
        this.table.add(this.rightGField).width(Gdx.graphics.getWidth() / 40f).padBottom(15).row();
        this.table.add(this.leftBLabel).padBottom(15);
        this.table.add(this.leftBField).width(Gdx.graphics.getWidth() / 40f);
        this.table.add(this.rightBLabel).padBottom(15);
        this.table.add(this.rightBField).width(Gdx.graphics.getWidth() / 40f).padBottom(15).row();
        this.table.add(this.leftALabel).padBottom(15);
        this.table.add(this.leftAField).width(Gdx.graphics.getWidth() / 40f);
        this.table.add(this.rightALabel).padBottom(15);
        this.table.add(this.rightAField).width(Gdx.graphics.getWidth() / 40f).padBottom(15).row();
        this.table.add(this.leftStrengthLabel).padBottom(15);
        this.table.add(this.leftStrengthField).width(Gdx.graphics.getWidth() / 40f);
        this.table.add(this.rightStrengthLabel).padBottom(15);
        this.table.add(this.rightStrengthField).width(Gdx.graphics.getWidth() / 40f).padBottom(15).row();
        this.table.add(this.radiusLabel).padBottom(15);
        this.table.add(this.radiusField).width(Gdx.graphics.getWidth() / 40f).padBottom(15).row();
        this.table.add(this.close);

        this.add(this.table);

        setSize(Gdx.graphics.getWidth() / 7f, Gdx.graphics.getHeight() / 3f);
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

    public float getR(boolean left)
    {
        float r = 1;
        try{
            if(left)
                r = Float.parseFloat(leftRField.getText());
            else
                r = Float.parseFloat(rightRField.getText());
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        return r;
    }

    public float getG(boolean left)
    {
        float g = 1;
        try{
            if(left)
                g = Float.parseFloat(leftGField.getText());
            else
                g = Float.parseFloat(rightGField.getText());
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        return g;
    }

    public float getB(boolean left)
    {
        float b = 1;
        try{
            if(left)
                b = Float.parseFloat(leftBField.getText());
            else
                b = Float.parseFloat(rightBField.getText());
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        return b;
    }

    public float getA(boolean left)
    {
        float a = 1;
        try{
            if(left)
                a = Float.parseFloat(leftAField.getText());
            else
                a = Float.parseFloat(rightAField.getText());
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        return a;
    }

    public float getStrength(boolean left)
    {
        float strength = .5f;
        try{
            if(left)
                strength = Float.parseFloat(leftStrengthField.getText());
            else
                strength = Float.parseFloat(rightStrengthField.getText());
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        return strength;
    }

    public float getRightR()
    {
        float r = 1;
        try{
            r = Float.parseFloat(rightRField.getText());
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        return r;
    }

    public float getRightG()
    {
        float g = 1;
        try{
            g = Float.parseFloat(rightGField.getText());
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        return g;
    }

    public float getRightB()
    {
        float b = 1;
        try{
            b = Float.parseFloat(rightBField.getText());
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        return b;
    }

    public float getRightA()
    {
        float a = 1;
        try{
            a = Float.parseFloat(rightAField.getText());
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        return a;
    }

    public float getRightStrength()
    {
        float strength = .5f;
        try{
            strength = Float.parseFloat(rightStrengthField.getText());
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        return strength;
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
}
