package com.bamboo.bridgebuilder.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.Utils;

public class MinMaxDialog extends Window
{
    private Label minSizeLabel;
    private Label maxSizeLabel;
    private Label minRotationLabel;
    private Label maxRotationLabel;
    private Label minRLabel;
    private Label maxRLabel;
    private Label minGLabel;
    private Label maxGLabel;
    private Label minBLabel;
    private Label maxBLabel;
    private Label minALabel;
    private Label maxALabel;

    private TextField minSizeTextfield;
    private TextField maxSizeTextfield;
    private TextField minRotationTextfield;
    private TextField maxRotationTextfield;
    private TextField minRTextfield;
    private TextField maxRTextfield;
    private TextField minGTextfield;
    private TextField maxGTextfield;
    private TextField minBTextfield;
    private TextField maxBTextfield;
    private TextField minATextfield;
    private TextField maxATextfield;

    private Table minMaxTable;

    private TextButton reset;
    private TextButton close;

    private float minSizeValue = 1;
    private float maxSizeValue = 1;
    private float minRotationValue = 0;
    private float maxRotationValue = 0;
    private float minRValue = 1;
    private float maxRValue = 1;
    private float minGValue = 1;
    private float maxGValue = 1;
    private float minBValue = 1;
    private float maxBValue = 1;
    private float minAValue = 1;
    private float maxAValue = 1;
    
    // Set after every click so everything uses the same random value.
    public float randomSizeValue = 1;
    public float randomRotationValue = 0;
    public float randomRValue = 1;
    public float randomGValue = 1;
    public float randomBValue = 1;
    public float randomAValue = 1;

    private BridgeBuilder editor;

    public MinMaxDialog(BridgeBuilder editor, Stage stage, Skin skin)
    {
        super("Min Max", skin);
        this.editor = editor;
        this.minMaxTable = new Table();

        this.reset = new TextButton("Reset", skin);
        this.reset.addListener(new ClickListener() {@Override public void clicked(InputEvent event, float x, float y) {
            reset();
        }});

        this.close = new TextButton("Close", skin);
        this.close.setColor(Color.FIREBRICK);
        this.close.addListener(new ClickListener() {@Override public void clicked(InputEvent event, float x, float y) {
            setVisible(false);
        }});

        this.minSizeLabel = new Label("Min Size", skin);
        this.maxSizeLabel = new Label("Max Size", skin);
        this.minRotationLabel = new Label("Min Rotation", skin);
        this.maxRotationLabel = new Label("Max Rotation", skin);
        this.minRLabel = new Label("Min R", skin);
        this.maxRLabel = new Label("Max R", skin);
        this.minGLabel = new Label("Min G", skin);
        this.maxGLabel = new Label("Max G", skin);
        this.minBLabel = new Label("Min B", skin);
        this.maxBLabel = new Label("Max B", skin);
        this.minALabel = new Label("Min A", skin);
        this.maxALabel = new Label("Max A", skin);
        this.minSizeTextfield = new TextField("1", skin);
        this.maxSizeTextfield = new TextField("1", skin);
        this.minRotationTextfield = new TextField("0", skin);
        this.maxRotationTextfield = new TextField("0", skin);
        this.minRTextfield = new TextField("1", skin);
        this.maxRTextfield = new TextField("1", skin);
        this.minGTextfield = new TextField("1", skin);
        this.maxGTextfield = new TextField("1", skin);
        this.minBTextfield = new TextField("1", skin);
        this.maxBTextfield = new TextField("1", skin);
        this.minATextfield = new TextField("1", skin);
        this.maxATextfield = new TextField("1", skin);

        this.minMaxTable.add(minSizeLabel);
        this.minMaxTable.add(minSizeTextfield).row();
        this.minMaxTable.add(maxSizeLabel);
        this.minMaxTable.add(maxSizeTextfield).row();
        this.minMaxTable.add(minRotationLabel);
        this.minMaxTable.add(minRotationTextfield).row();
        this.minMaxTable.add(maxRotationLabel);
        this.minMaxTable.add(maxRotationTextfield).row();
        this.minMaxTable.add(minRLabel);
        this.minMaxTable.add(minRTextfield).row();
        this.minMaxTable.add(maxRLabel);
        this.minMaxTable.add(maxRTextfield).row();
        this.minMaxTable.add(minGLabel);
        this.minMaxTable.add(minGTextfield).row();
        this.minMaxTable.add(maxGLabel);
        this.minMaxTable.add(maxGTextfield).row();
        this.minMaxTable.add(minBLabel);
        this.minMaxTable.add(minBTextfield).row();
        this.minMaxTable.add(maxBLabel);
        this.minMaxTable.add(maxBTextfield).row();
        this.minMaxTable.add(minALabel);
        this.minMaxTable.add(minATextfield).row();
        this.minMaxTable.add(maxALabel);
        this.minMaxTable.add(maxATextfield).row();

        this.add(minMaxTable).row();

        this.add(reset).row();
        this.add(close);

        setSize(getPrefWidth(), getPrefHeight());

        this.setPosition((stage.getWidth() / 2f), (stage.getHeight() / 2f), Align.center);

        stage.addActor(this);
        setVisible(false);

        TextField.TextFieldFilter valueFilter = new TextField.TextFieldFilter()
        {
            @Override
            public boolean acceptChar(TextField textField, char c)
            {
                return c == '.' || c == '-' || Character.isDigit(c);
            }
        };

        this.minSizeTextfield.setTextFieldFilter(valueFilter);
        this.minSizeTextfield.addListener(new InputListener()
        {
            @Override
            public boolean keyTyped (InputEvent event, char character)
            {
                try
                {
                    minSizeValue = Float.parseFloat(minSizeTextfield.getText());
                }
                catch(NumberFormatException e)
                {
                    minSizeValue = 0;
                }
                shuffle();
                return false;
            }
        });

        this.maxSizeTextfield.setTextFieldFilter(valueFilter);
        this.maxSizeTextfield.addListener(new InputListener()
        {
            @Override
            public boolean keyTyped (InputEvent event, char character)
            {
                try
                {
                    maxSizeValue = Float.parseFloat(maxSizeTextfield.getText());
                }
                catch(NumberFormatException e)
                {
                    maxSizeValue = 0;
                }
                shuffle();
                return false;
            }
        });


        this.minRotationTextfield.setTextFieldFilter(valueFilter);
        this.minRotationTextfield.addListener(new InputListener()
        {
            @Override
            public boolean keyTyped (InputEvent event, char character)
            {
                try
                {
                    minRotationValue = Float.parseFloat(minRotationTextfield.getText());
                }
                catch(NumberFormatException e)
                {
                    minRotationValue = 0;
                }
                shuffle();
                return false;
            }
        });

        this.maxRotationTextfield.setTextFieldFilter(valueFilter);
        this.maxRotationTextfield.addListener(new InputListener()
        {
            @Override
            public boolean keyTyped (InputEvent event, char character)
            {
                try
                {
                    maxRotationValue = Float.parseFloat(maxRotationTextfield.getText());
                }
                catch(NumberFormatException e)
                {
                    maxRotationValue = 0;
                }
                shuffle();
                return false;
            }
        });

        this.minRTextfield.setTextFieldFilter(valueFilter);
        this.minRTextfield.addListener(new InputListener()
        {
            @Override
            public boolean keyTyped (InputEvent event, char character)
            {
                try
                {
                    minRValue = Float.parseFloat(minRTextfield.getText());
                }
                catch(NumberFormatException e)
                {
                    minRValue = 0;
                }
                shuffle();
                return false;
            }
        });

        this.maxRTextfield.setTextFieldFilter(valueFilter);
        this.maxRTextfield.addListener(new InputListener()
        {
            @Override
            public boolean keyTyped (InputEvent event, char character)
            {
                try
                {
                    maxRValue = Float.parseFloat(maxRTextfield.getText());
                }
                catch(NumberFormatException e)
                {
                    maxRValue = 0;
                }
                shuffle();
                return false;
            }
        });

        this.minGTextfield.setTextFieldFilter(valueFilter);
        this.minGTextfield.addListener(new InputListener()
        {
            @Override
            public boolean keyTyped (InputEvent event, char character)
            {
                try
                {
                    minGValue = Float.parseFloat(minGTextfield.getText());
                }
                catch(NumberFormatException e)
                {
                    minGValue = 0;
                }
                shuffle();
                return false;
            }
        });

        this.maxGTextfield.setTextFieldFilter(valueFilter);
        this.maxGTextfield.addListener(new InputListener()
        {
            @Override
            public boolean keyTyped (InputEvent event, char character)
            {
                try
                {
                    maxGValue = Float.parseFloat(maxGTextfield.getText());
                }
                catch(NumberFormatException e)
                {
                    maxGValue = 0;
                }
                shuffle();
                return false;
            }
        });

        this.minBTextfield.setTextFieldFilter(valueFilter);
        this.minBTextfield.addListener(new InputListener()
        {
            @Override
            public boolean keyTyped (InputEvent event, char character)
            {
                try
                {
                    minBValue = Float.parseFloat(minBTextfield.getText());
                }
                catch(NumberFormatException e)
                {
                    minBValue = 0;
                }
                shuffle();
                return false;
            }
        });

        this.maxBTextfield.setTextFieldFilter(valueFilter);
        this.maxBTextfield.addListener(new InputListener()
        {
            @Override
            public boolean keyTyped (InputEvent event, char character)
            {
                try
                {
                    maxBValue = Float.parseFloat(maxBTextfield.getText());
                }
                catch(NumberFormatException e)
                {
                    maxBValue = 0;
                }
                shuffle();
                return false;
            }
        });

        this.minATextfield.setTextFieldFilter(valueFilter);
        this.minATextfield.addListener(new InputListener()
        {
            @Override
            public boolean keyTyped (InputEvent event, char character)
            {
                try
                {
                    minAValue = Float.parseFloat(minATextfield.getText());
                }
                catch(NumberFormatException e)
                {
                    minAValue = 0;
                }
                shuffle();
                return false;
            }
        });

        this.maxATextfield.setTextFieldFilter(valueFilter);
        this.maxATextfield.addListener(new InputListener()
        {
            @Override
            public boolean keyTyped (InputEvent event, char character)
            {
                try
                {
                    maxAValue = Float.parseFloat(maxATextfield.getText());
                }
                catch(NumberFormatException e)
                {
                    maxAValue = 0;
                }
                shuffle();
                return false;
            }
        });
    }
    
    public void generateRandomValues()
    {
        this.randomSizeValue = Utils.randomFloat(this.minSizeValue, this.maxSizeValue);
        this.randomRotationValue = Utils.randomFloat(this.minRotationValue, this.maxRotationValue);
        this.randomRValue = Utils.randomFloat(this.minRValue, this.maxRValue);
        this.randomGValue = Utils.randomFloat(this.minGValue, this.maxGValue);
        this.randomBValue = Utils.randomFloat(this.minBValue, this.maxBValue);
        this.randomAValue = Utils.randomFloat(this.minAValue, this.maxAValue);
    }

    public void reset()
    {
        this.minSizeTextfield.setText("1");
        this.maxSizeTextfield.setText("1");
        this.minRotationTextfield.setText("0");
        this.maxRotationTextfield.setText("0");
        this.minRTextfield.setText("1");
        this.maxRTextfield.setText("1");
        this.minGTextfield.setText("1");
        this.maxGTextfield.setText("1");
        this.minBTextfield.setText("1");
        this.maxBTextfield.setText("1");
        this.minATextfield.setText("1");
        this.maxATextfield.setText("1");
        this.minSizeValue = 1;
        this.maxSizeValue = 1;
        this.minRotationValue = 0;
        this.maxRotationValue = 0;
        this.minRValue = 1;
        this.maxRValue = 1;
        this.minGValue = 1;
        this.maxGValue = 1;
        this.minBValue = 1;
        this.maxBValue = 1;
        this.minAValue = 1;
        this.maxAValue = 1;
    }

    /** Shuffle the selected sprite tools in order to reflect the recent changes in min max values. */
    private void shuffle()
    {
        for(int i = 0; i < editor.maps.size; i++)
            editor.maps.get(i).shuffleRandomSpriteTool(false);
    }
}
