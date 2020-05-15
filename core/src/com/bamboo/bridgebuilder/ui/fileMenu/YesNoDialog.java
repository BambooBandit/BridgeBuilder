package com.bamboo.bridgebuilder.ui.fileMenu;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class YesNoDialog extends Window
{
    private Label areYouSureLabel;
    private Table yesNoTable;
    private TextButton yes;
    private TextButton no;
    private TextButton cancel;

    public YesNoDialog(String action, Stage stage, String title, Skin skin, boolean hasCancelButton)
    {
        super(title, skin);

        this.areYouSureLabel = new Label(action, skin);

        this.yesNoTable = new Table();

        this.yes = new TextButton("Yes", skin);
        this.yes.setColor(Color.GREEN);
        this.yes.addListener(new ClickListener() {@Override public void clicked(InputEvent event, float x, float y) {
            yes();
            remove();
        }});

        this.no = new TextButton("No", skin);
        this.no.setColor(Color.FIREBRICK);
        this.no.addListener(new ClickListener() {@Override public void clicked(InputEvent event, float x, float y) {
            no();
            remove();
        }});

        if(hasCancelButton)
        {
            this.cancel = new TextButton("Cancel", skin);
            this.cancel.addListener(new ClickListener()
            {
                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    cancelDialog();
                }
            });
        }

        this.add(areYouSureLabel).row();
        setWidth(getPrefWidth());
        this.yesNoTable.add(yes).width(getWidth() / 5).padRight(15).padLeft(15);
        this.yesNoTable.add(no).width(getWidth() / 5).padRight(15).padLeft(15);
        if(hasCancelButton)
        {
            this.yesNoTable.add(cancel).width(getWidth() / 5).padRight(15).padLeft(15);
        }
        this.add(yesNoTable);
        setHeight(getPrefHeight());
        this.setPosition((stage.getWidth() / 2), (stage.getHeight() / 2), Align.center);

        stage.addActor(this);

        this.addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int key)
            {
                if(key == Input.Keys.Y)
                {
                    yes();
                    cancelDialog();
                }
                else if(key == Input.Keys.N)
                {
                    no();
                    cancelDialog();
                }
                else if(key == Input.Keys.ESCAPE)
                    cancelDialog();
                return super.keyDown(event, key);
            }
        });
    }

    public void yes() {}
    public void no() {}
    private void cancelDialog()
    {
        remove();
    };
}
