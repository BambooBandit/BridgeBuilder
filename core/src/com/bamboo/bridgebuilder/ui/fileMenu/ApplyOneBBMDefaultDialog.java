package com.bamboo.bridgebuilder.ui.fileMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.map.Map;

public class ApplyOneBBMDefaultDialog extends Window
{
    private Table table;

    private Table textFieldTable;
    private TextField defaultSheetName;
    private TextField currentSheetName;
    private Label extensionLabel1;
    private Label extensionLabel2;

    private Table createCloseTable;
    private TextButton apply;
    private TextButton close;

    private Skin skin;

    private Map map;

    boolean defaultValid = false;
    boolean currentValid = false;

    public ApplyOneBBMDefaultDialog(Map map, Stage stage, Skin skin)
    {
        super("Override and set BBM properties to default...", skin);
        this.skin = skin;
        this.map = map;
        this.table = new Table();
        this.textFieldTable = new Table();
        this.createCloseTable = new Table();

        this.defaultSheetName = new TextField("Default sheet", skin);
        this.currentSheetName = new TextField("Current sheet", skin);
        this.extensionLabel1 = new Label(".atlas", skin);
        this.extensionLabel2 = new Label(".atlas", skin);

        this.apply = new TextButton("Apply", skin);
        this.apply.setColor(Color.FIREBRICK);
        this.apply.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            if(defaultValid && currentValid)
            {
                String defaultName = defaultSheetName.getText();
                String currentName = currentSheetName.getText();

                map.editor.fileMenu.setBBMDefaults(map, defaultName, currentName);
                close();
            }
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

        float pad = Gdx.graphics.getHeight() / 35f;
        this.add(this.table).row();
        this.textFieldTable.add(this.defaultSheetName).padRight(3);
        this.textFieldTable.add(this.extensionLabel1).row();
        this.textFieldTable.add(this.currentSheetName).padRight(3);
        this.textFieldTable.add(this.extensionLabel2).row();
        this.add(textFieldTable).padTop(pad / 2).padBottom(pad / 2).row();
        this.createCloseTable.add(this.apply).pad(pad);
        this.createCloseTable.add(this.close).pad(pad);
        this.add(this.createCloseTable);

        this.defaultSheetName.addListener(new InputListener()
        {
            public boolean keyTyped (InputEvent event, char character)
            {
                checkTextField();
                return false;
            }
        });

        this.currentSheetName.addListener(new InputListener()
        {
            public boolean keyTyped (InputEvent event, char character)
            {
                checkTextField();
                return false;
            }
        });

        setSize(Gdx.graphics.getWidth() / 4f, Gdx.graphics.getHeight() / 4f);
        this.setPosition((stage.getWidth() / 2f), (stage.getHeight() / 2f), Align.center);
        stage.addActor(this);
        setVisible(true);
    }

    public void close()
    {
        this.setVisible(false);
        remove();
    }

    public void open()
    {
        checkTextField();
        this.setVisible(true);
    }

    private void checkTextField()
    {
        String defaultName = defaultSheetName.getText();
        String currentName = currentSheetName.getText();
        this.defaultValid = Utils.isSpriteSheetInFolder(defaultName);
        this.currentValid = Utils.isSpriteSheetInFolder(currentName);
        if(this.defaultValid && this.currentValid)
            this.apply.setColor(Color.GREEN);
        else
            this.apply.setColor(Color.FIREBRICK);
    }
}
