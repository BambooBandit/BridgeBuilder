package com.bamboo.bridgebuilder.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class BranchDialog extends Window
{
    private TextButton close;

    private Skin skin;

    private Table table;
    private Label doubleLinkedLabel;
    private CheckBox doubleLinkedCheckBox;

    public BranchDialog(Stage stage, Skin skin)
    {
        super("MapPoint connections/branches", skin);
        this.skin = skin;

        this.table = new Table();

        this.doubleLinkedLabel = new Label("Double linked branches: ", skin);
        this.doubleLinkedCheckBox = new CheckBox("", skin);
        this.doubleLinkedCheckBox.setChecked(false);

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

        this.table.add(this.doubleLinkedLabel).padBottom(15);
        this.table.add(this.doubleLinkedCheckBox).padBottom(15).row();
        this.table.add(this.close);

        this.add(this.table);

        setSize(Gdx.graphics.getWidth() / 3f, Gdx.graphics.getHeight() / 3f);
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

    public boolean isDoubleLinked()
    {
        return doubleLinkedCheckBox.isChecked();
    }
}
