package com.bamboo.bridgebuilder.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class StairsDialog extends Window
{
    private TextButton close;

    private Skin skin;

    private Table table;
    private Label initialHeightLabel;
    private Label finalHeightLabel;
    private Label stairAmountLabel;
    private Label thicknessLabel;
    private Label snapLabel;
    private TextField initialHeightField;
    private TextField finalHeightField;
    private TextField stairAmountField;
    private TextField thicknessField;
    private CheckBox snapCheckBox;

    public StairsDialog(Stage stage, Skin skin)
    {
        super("Stairs", skin);
        this.skin = skin;

        this.table = new Table();

        this.initialHeightLabel = new Label("Initial height: ", skin);
        this.finalHeightLabel = new Label("Final height: ", skin);
        this.stairAmountLabel = new Label("Stair amount per meter: ", skin);
        this.thicknessLabel = new Label("Thickness: ", skin);
        this.snapLabel = new Label("Should snap: ", skin);
        this.initialHeightField = new TextField("0", skin);
        this.finalHeightField = new TextField("5", skin);
        this.stairAmountField = new TextField("1", skin);
        this.thicknessField = new TextField("1", skin);
        this.snapCheckBox = new CheckBox("", skin);
        this.snapCheckBox.setChecked(true);

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

        this.table.add(this.initialHeightLabel).padBottom(15);
        this.table.add(this.initialHeightField).padBottom(15).row();
        this.table.add(this.finalHeightLabel).padBottom(15);
        this.table.add(this.finalHeightField).padBottom(15).row();
        this.table.add(this.stairAmountLabel).padBottom(15);
        this.table.add(this.stairAmountField).padBottom(15).row();
        this.table.add(this.thicknessLabel).padBottom(15);
        this.table.add(this.thicknessField).padBottom(15).row();
        this.table.add(this.snapLabel).padBottom(15);
        this.table.add(this.snapCheckBox).padBottom(15).row();
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

    public int getInitialHeight()
    {
        int num = 0;
        try { num = Integer.parseInt(this.initialHeightField.getText()); } catch (NumberFormatException e){}
        return num;
    }

    public int getFinalHeight()
    {
        int num = 5;
        try { num = Integer.parseInt(this.finalHeightField.getText()); } catch (NumberFormatException e){}
        return num;
    }

    public float getStairAmount()
    {
        float num = 1;
        try { num = Float.parseFloat(this.stairAmountField.getText()); } catch (NumberFormatException e){}
        return num;
    }

    public float getThickness()
    {
        float num = 1;
        try { num = Float.parseFloat(this.thicknessField.getText()); } catch (NumberFormatException e){}
        return num;
    }

    public boolean shouldSnap()
    {
        return snapCheckBox.isChecked();
    }
}
