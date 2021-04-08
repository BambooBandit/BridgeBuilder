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
    private Label heightLabel;
    private Label parentCenterHeightLabel;
    private Label connectorCenterHeightLabel;
    private Label snapLabel;
    private Label transparentParentLabel;
    private Label connectorInFrontLabel;
    private Label stackAmountLabel;
    private Label stackHeightMultiplierLabel;
    private Label connectorWidthOvershootLabel;
    private TextField initialHeightField;
    private TextField finalHeightField;
    private TextField stairAmountField;
    private TextField thicknessField;
    private TextField heightField;
    private TextField stackAmountField;
    private TextField stackHeightMultiplierField;
    private TextField connectorWidthOvershootField;
    private CheckBox snapCheckBox;
    private CheckBox parentCenterHeightCheckBox;
    private CheckBox connectorCenterHeightCheckBox;
    private CheckBox transparentParentCheckBox;
    private CheckBox connectorInFrontCheckBox;

    public StairsDialog(Stage stage, Skin skin)
    {
        super("Stairs and Fence", skin);
        this.skin = skin;

        this.table = new Table();

        this.initialHeightLabel = new Label("Stair initial height: ", skin);
        this.finalHeightLabel = new Label("Stair final height: ", skin);
        this.stairAmountLabel = new Label("Stair amount per meter: ", skin);
        this.thicknessLabel = new Label("Thickness: ", skin);
        this.heightLabel = new Label("Height offset: ", skin);
        this.transparentParentLabel = new Label("Should parent be transparent: ", skin);
        this.connectorInFrontLabel = new Label("Should connector be in front: ", skin);
        this.snapLabel = new Label("Should snap: ", skin);
        this.stackAmountLabel = new Label("Stack amount: ", skin);
        this.stackHeightMultiplierLabel = new Label("Stack height multiplier: ", skin);
        this.parentCenterHeightLabel = new Label("Should parent height be centered: ", skin);
        this.connectorCenterHeightLabel = new Label("Should connector height be centered: ", skin);
        this.connectorWidthOvershootLabel = new Label("Connector width overshoot: ", skin);
        this.initialHeightField = new TextField("0", skin);
        this.finalHeightField = new TextField("5", skin);
        this.stairAmountField = new TextField("1", skin);
        this.thicknessField = new TextField("1", skin);
        this.heightField = new TextField("0", skin);
        this.stackAmountField = new TextField("1", skin);
        this.stackHeightMultiplierField = new TextField("1", skin);
        this.connectorWidthOvershootField = new TextField("1.005", skin);
        this.parentCenterHeightCheckBox = new CheckBox("", skin);
        this.parentCenterHeightCheckBox.setChecked(false);
        this.connectorCenterHeightCheckBox = new CheckBox("", skin);
        this.connectorCenterHeightCheckBox.setChecked(false);
        this.snapCheckBox = new CheckBox("", skin);
        this.snapCheckBox.setChecked(true);
        this.transparentParentCheckBox = new CheckBox("", skin);
        this.transparentParentCheckBox.setChecked(false);
        this.connectorInFrontCheckBox = new CheckBox("", skin);
        this.connectorInFrontCheckBox.setChecked(false);

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
        this.table.add(this.heightLabel).padBottom(15);
        this.table.add(this.heightField).padBottom(15).row();
        this.table.add(this.stackAmountLabel).padBottom(15);
        this.table.add(this.stackAmountField).padBottom(15).row();
        this.table.add(this.stackHeightMultiplierLabel).padBottom(15);
        this.table.add(this.stackHeightMultiplierField).padBottom(15).row();
        this.table.add(this.connectorWidthOvershootLabel).padBottom(15);
        this.table.add(this.connectorWidthOvershootField).padBottom(15).row();
        this.table.add(this.parentCenterHeightLabel).padBottom(15);
        this.table.add(this.parentCenterHeightCheckBox).padBottom(15).row();
        this.table.add(this.connectorCenterHeightLabel).padBottom(15);
        this.table.add(this.connectorCenterHeightCheckBox).padBottom(15).row();
        this.table.add(this.transparentParentLabel).padBottom(15);
        this.table.add(this.transparentParentCheckBox).padBottom(15).row();
        this.table.add(this.connectorInFrontLabel).padBottom(15);
        this.table.add(this.connectorInFrontCheckBox).padBottom(15).row();
        this.table.add(this.snapLabel).padBottom(15);
        this.table.add(this.snapCheckBox).padBottom(15).row();
        this.table.add(this.close);

        this.add(this.table);

        setSize(Gdx.graphics.getWidth() / 3f, Gdx.graphics.getHeight() / 1.5f);
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

    public float getConnectorWidthOvershoot()
    {
        float num = 1.005f;
        try { num = Float.parseFloat(this.connectorWidthOvershootField.getText()); } catch (NumberFormatException e){}
        return num;
    }

    public float getInitialHeight()
    {
        float num = 0;
        try { num = Float.parseFloat(this.initialHeightField.getText()); } catch (NumberFormatException e){}
        return num;
    }

    public float getFinalHeight()
    {
        float num = 5;
        try { num = Float.parseFloat(this.finalHeightField.getText()); } catch (NumberFormatException e){}
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

    public int getStackAmount()
    {
        int num = 1;
        try { num = Integer.parseInt(this.stackAmountField.getText()); } catch (NumberFormatException e){}
        return num;
    }

    public float getStackHeightMultiplier()
    {
        float num = 1;
        try { num = Float.parseFloat(this.stackHeightMultiplierField.getText()); } catch (NumberFormatException e){}
        return num;
    }

    public float getHeightOffset()
    {
        float num = 0;
        try { num = Float.parseFloat(this.heightField.getText()); } catch (NumberFormatException e){}
        return num;
    }

    public boolean shouldSnap()
    {
        return snapCheckBox.isChecked();
    }

    public boolean shouldParentBeTransparent()
    {
        return transparentParentCheckBox.isChecked();
    }

    public boolean shouldParentHeightBeCentered()
    {
        return parentCenterHeightCheckBox.isChecked();
    }

    public boolean shouldConnectorHeightBeCentered()
    {
        return connectorCenterHeightCheckBox.isChecked();
    }

    public boolean shouldConnectorBeInFront()
    {
        return connectorInFrontCheckBox.isChecked();
    }

}
