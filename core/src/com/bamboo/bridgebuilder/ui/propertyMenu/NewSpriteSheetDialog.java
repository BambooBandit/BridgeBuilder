package com.bamboo.bridgebuilder.ui.propertyMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteSheet;

import java.io.File;

public class NewSpriteSheetDialog extends Window
{
    private Table table;

    private Table textFieldTable;
    private TextField sheetName;
    private Label extensionLabel;

    private Table createCloseTable;
    private TextButton create;
    private TextButton close;

    private Skin skin;

    private Map map;

    boolean valid = false;

    public NewSpriteSheetDialog(Map map, Stage stage, Skin skin)
    {
        super("New Spritesheet", skin);
        this.skin = skin;
        this.map = map;
        this.table = new Table();
        this.textFieldTable = new Table();
        this.createCloseTable = new Table();

        this.sheetName = new TextField("sheet", skin);
        this.extensionLabel = new Label(".atlas", skin);

        this.create = new TextButton("Create", skin);
        this.create.setColor(Color.FIREBRICK);
        this.create.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            if(valid)
            {
                String name = sheetName.getText();
                map.spriteMenu.createSpriteSheet(name, skin);
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
        this.textFieldTable.add(this.sheetName).padRight(3);
        this.textFieldTable.add(this.extensionLabel).row();
        this.add(textFieldTable).padTop(pad / 2).padBottom(pad / 2).row();
        this.createCloseTable.add(this.create).pad(pad);
        this.createCloseTable.add(this.close).pad(pad);
        this.add(this.createCloseTable);

        this.sheetName.addListener(new InputListener()
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
        setVisible(false);
    }

    public void close()
    {
        this.setVisible(false);
    }

    public void open()
    {
        checkTextField();
        this.setVisible(true);
    }

    private void checkTextField()
    {
        String string = sheetName.getText() + ".atlas";
        File tempFile = new File(string);
        boolean exists = tempFile.exists();

        conditional:
        if(exists)
        {
            for(int i = 0; i < map.spriteMenu.spriteSheets.size; i ++)
            {
                SpriteSheet spriteSheet = map.spriteMenu.spriteSheets.get(i);
                if(spriteSheet.name.equals(sheetName.getText()))
                {
                    this.valid = false;
                    break conditional;
                }
            }
            this.valid = true;
        }
        else
            this.valid = false;

        if(this.valid)
            this.create.setColor(Color.GREEN);
        else
            this.create.setColor(Color.FIREBRICK);
    }
}
