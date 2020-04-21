package com.bamboo.bridgebuilder.ui.fileMenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class AreYouSureDialog extends Dialog
{
    private Label areYouSureLabel;
    private Table yesNoTable;
    private TextButton yes;
    private TextButton no;
    private TextButton cancel;

    public AreYouSureDialog(String action, Stage stage, String title, Skin skin, boolean hasCancelButton)
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
                    remove();
                }
            });
        }

        this.getContentTable().add(areYouSureLabel).row();
        this.yesNoTable.add(yes);
        this.yesNoTable.add(no);
        if(hasCancelButton)
            this.yesNoTable.add(cancel);
        this.getContentTable().add(yesNoTable);

        setSize(getPrefWidth(), getPrefHeight());

        this.setPosition((stage.getWidth() / 2), (stage.getHeight() / 2), Align.center);

        stage.addActor(this);
    }

    public void yes() {}
    public void no() {}
}
