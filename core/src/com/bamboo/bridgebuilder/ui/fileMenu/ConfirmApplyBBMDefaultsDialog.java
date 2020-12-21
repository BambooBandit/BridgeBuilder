package com.bamboo.bridgebuilder.ui.fileMenu;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.bamboo.bridgebuilder.map.Map;

public class ConfirmApplyBBMDefaultsDialog extends Window
{
    private Label areYouSureLabel;
    private Table yesNoTable;
    private TextButton yes;
    private TextButton no;
    private TextButton overrideOneSheet;
    private TextButton cancel;
    private Map map;

    public ConfirmApplyBBMDefaultsDialog(Map map, Stage stage, Skin skin)
    {
        super("", skin);

        this.map = map;
        this.areYouSureLabel = new Label("Override and set BBM properties to default for this map?", skin);

        this.yesNoTable = new Table();

        this.yes = new TextButton("Yes", skin);
        this.yes.setColor(Color.GREEN);
        this.yes.addListener(new ClickListener() {@Override public void clicked(InputEvent event, float x, float y) {
            map.editor.fileMenu.setBBMDefaults(map);
            remove();
        }});

        this.no = new TextButton("No", skin);
        this.no.setColor(Color.FIREBRICK);
        this.no.addListener(new ClickListener() {@Override public void clicked(InputEvent event, float x, float y) {
            remove();
        }});

        this.overrideOneSheet = new TextButton("Override one sheet", skin);
        this.overrideOneSheet.addListener(new ClickListener() {@Override public void clicked(InputEvent event, float x, float y) {
            ApplyOneBBMDefaultDialog applyOneBBMDefaultDialog = new ApplyOneBBMDefaultDialog(map, stage, skin);
            remove();
        }});


        this.add(areYouSureLabel).row();
        setWidth(getPrefWidth());
        this.yesNoTable.add(yes).width(getWidth() / 5).padRight(15).padLeft(15);
        this.yesNoTable.add(no).width(getWidth() / 5).padRight(15).padLeft(15).row();
        this.yesNoTable.add(overrideOneSheet);
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
                    map.editor.fileMenu.setBBMDefaults(map);
                    cancelDialog();
                    return true;
                }
                else if(key == Input.Keys.N)
                {
                    cancelDialog();
                    return true;
                }
                else if(key == Input.Keys.ESCAPE)
                {
                    cancelDialog();
                    return true;
                }
                return super.keyDown(event, key);
            }
        });
    }

    private void cancelDialog()
    {
        remove();
    };
}
