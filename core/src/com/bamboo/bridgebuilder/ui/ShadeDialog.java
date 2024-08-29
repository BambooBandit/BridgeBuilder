package com.bamboo.bridgebuilder.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.commands.PaintMapSprite;
import com.bamboo.bridgebuilder.map.MapSprite;

public class ShadeDialog extends Window
{
    private TextButton apply;
    private TextButton close;

    private Skin skin;

    private Table table;

    private Label leftLabel;
    private Label leftRLabel;
    private TextField leftRField;
    private Label leftGLabel;
    private TextField leftGField;
    private Label leftBLabel;
    private TextField leftBField;

    private Label rightLabel;
    private Label rightRLabel;
    private TextField rightRField;
    private Label rightGLabel;
    private TextField rightGField;
    private Label rightBLabel;
    private TextField rightBField;

    private Label sunAngleLabel;
    private TextField sunAngleField;

    private Label flipLabel;
    public CheckBox flipCheckBox;

    private BridgeBuilder editor;

    public ShadeDialog(Stage stage, Skin skin, BridgeBuilder editor)
    {
        super("Shade", skin);
        this.editor = editor;

        this.skin = skin;

        this.table = new Table();

        this.leftLabel = new Label("Light", skin);
        this.leftRLabel = new Label("R: ", skin);
        this.leftRField = new TextField("1", skin);

        this.leftGLabel = new Label("G: ", skin);
        this.leftGField = new TextField("1", skin);

        this.leftBLabel = new Label("B: ", skin);
        this.leftBField = new TextField("1", skin);

        this.rightLabel = new Label("Shade", skin);
        this.rightRLabel = new Label("R: ", skin);
        this.rightRField = new TextField("1", skin);

        this.rightGLabel = new Label("G: ", skin);
        this.rightGField = new TextField("1", skin);

        this.rightBLabel = new Label("B: ", skin);
        this.rightBField = new TextField("1", skin);

        this.sunAngleLabel = new Label("Sun Angle: ", skin);
        this.sunAngleField = new TextField("150", skin);

        this.flipLabel = new Label("Flip: ", skin);
        this.flipCheckBox = new CheckBox("", skin);

        this.apply = new TextButton("Apply", skin);
        this.apply.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                apply();
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

        this.table.add(this.leftLabel).padBottom(15);
        this.table.add();
        this.table.add(this.rightLabel).padBottom(15).padBottom(15).row();
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
        this.table.add(this.sunAngleLabel).padBottom(15);
        this.table.add(this.sunAngleField).width(Gdx.graphics.getWidth() / 40f).padBottom(15);
        this.table.add(this.flipLabel).padBottom(15);
        this.table.add(this.flipCheckBox).padBottom(15).row();
        this.table.add(this.apply).padBottom(15);
        this.table.row();
        this.table.add(this.close);

        this.add(this.table);

        setSize(Gdx.graphics.getWidth() / 6.5f, Gdx.graphics.getHeight() / 2.75f);
        this.setPosition((stage.getWidth() / 2f), (stage.getHeight() / 2f), Align.center);
        stage.addActor(this);
        setVisible(false);
    }

    public void apply()
    {
        if(editor.activeMap == null)
            return;

        PaintMapSprite paintMapSprite = null;

        boolean flip = flipCheckBox.isChecked();
        for(int i = 0; i < editor.activeMap.selectedSprites.size; i ++)
        {
            MapSprite sprite = editor.activeMap.selectedSprites.get(i);
            float angle = 0;
            if(sprite.toEdgeSprite == null && sprite.parentSprite == null)
                continue;
            if(sprite.toEdgeSprite != null)
                angle = Utils.getAngleDegree(sprite.x, sprite.y, sprite.toEdgeSprite.x, sprite.toEdgeSprite.y) + (flip ? 90 : -90);
            else
            {
                if(sprite.parentSprite.toEdgeSprite == null)
                    continue;
                angle = Utils.getAngleDegree(sprite.parentSprite.x, sprite.parentSprite.y, sprite.parentSprite.toEdgeSprite.x, sprite.parentSprite.toEdgeSprite.y) + (flip ? 90 : -90);
            }

            float norm = Utils.matchAngles(getSunAngle(), angle);
            float normR = MathUtils.lerp(getR(false), getR(true), norm);
            float normG = MathUtils.lerp(getG(false), getG(true), norm);
            float normB = MathUtils.lerp(getB(false), getB(true), norm);
            if(paintMapSprite == null)
                paintMapSprite = new PaintMapSprite(this.editor.activeMap, sprite, normR, normG, normB, sprite.sprite.getColor().a);
            else
                paintMapSprite.addCommandToChain(new PaintMapSprite(this.editor.activeMap, sprite, normR, normG, normB, sprite.sprite.getColor().a));
        }
        if(paintMapSprite != null)
        {
            this.editor.activeMap.executeCommand(paintMapSprite);
        }
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

    public float getSunAngle()
    {
        float angle = 150;
        try{
            angle = Float.parseFloat(sunAngleField.getText());
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        return angle;
    }
}
