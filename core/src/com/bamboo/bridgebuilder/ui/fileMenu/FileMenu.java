package com.bamboo.bridgebuilder.ui.fileMenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.EditorAssets;
import com.bamboo.bridgebuilder.map.Map;

import static com.bamboo.bridgebuilder.map.Map.untitledCount;

public class FileMenu extends Group
{
    private Table fileMenuTable;
    private Table buttonTable;
    public MapTabPane mapTabPane;
    public ToolPane toolPane;

    private TextButton newButton;
    private TextButton openButton;
    private TextButton saveButton;
    private TextButton saveAsButton;
    private TextButton saveBBMDefaultsButton;
    private TextButton setBBMDefaultsButton;
    private TextButton undoButton;
    private TextButton redoButton;

    private BridgeBuilder editor;

    public FileMenu(Skin skin, BridgeBuilder editor)
    {
        this.editor = editor;

        this.newButton = new TextButton("New", skin);
        this.openButton = new TextButton("Open", skin);
        this.saveButton = new TextButton("Save", skin);
        this.saveAsButton = new TextButton("Save As", skin);
        this.saveBBMDefaultsButton = new TextButton("Save BBM Defaults", skin);
        this.setBBMDefaultsButton = new TextButton("Set BBM Defaults", skin);
        this.undoButton = new TextButton("Undo", skin);
        this.redoButton = new TextButton("Redo", skin);

        // Buttons text color
        this.newButton.getLabel().setColor(Color.BLACK);
        this.openButton.getLabel().setColor(Color.BLACK);
        this.saveButton.getLabel().setColor(Color.BLACK);
        this.saveAsButton.getLabel().setColor(Color.BLACK);
        this.saveBBMDefaultsButton.getLabel().setColor(Color.BLACK);
        this.setBBMDefaultsButton.getLabel().setColor(Color.BLACK);
        this.undoButton.getLabel().setColor(Color.BLACK);
        this.redoButton.getLabel().setColor(Color.BLACK);

        // Add listeners
        this.newButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                new AreYouSureDialog("Create a new map with BBM default settings/properties?", FileMenu.this.editor.stage, "", EditorAssets.getUISkin(), true)
                {
                    Map newMap;
                    @Override
                    public void yes()
                    {
                    }

                    @Override
                    public void no()
                    {
                        newMap = new Map(FileMenu.this.editor, "untitled " + untitledCount++);
                        FileMenu.this.editor.addToMaps(newMap);
                        mapTabPane.lookAtMap(newMap);
                    }
                };
            }
        });
        this.openButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            }
        });
        this.saveButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            }
        });
        this.saveAsButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            }
        });
        this.saveBBMDefaultsButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            }
        });
        this.setBBMDefaultsButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
            }
        });
        this.undoButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if(editor.getScreen() != null)
                {
                    Map map = (Map) editor.getScreen();
                    map.undo();
                }
            }
        });
        this.redoButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if(editor.getScreen() != null)
                {
                    Map map = (Map) editor.getScreen();
                    map.redo();
                }
            }
        });

        // Add header and buttons to the buttonTable
        this.buttonTable = new Table();
        this.buttonTable.add(this.newButton);
        this.buttonTable.add(this.openButton);
        this.buttonTable.add(this.saveButton);
        this.buttonTable.add(this.saveAsButton);
        this.buttonTable.add(this.saveBBMDefaultsButton);
        this.buttonTable.add(this.setBBMDefaultsButton);
        this.buttonTable.add(this.undoButton);
        this.buttonTable.add(this.redoButton);

        this.mapTabPane = new MapTabPane(this.editor, skin);
        this.toolPane = new ToolPane(this.editor, skin);

        this.fileMenuTable = new Table();
        this.fileMenuTable.add(this.buttonTable).row();
        this.fileMenuTable.add(this.mapTabPane).row();
        this.fileMenuTable.add(this.toolPane);
        this.addActor(this.fileMenuTable);
    }

    public void save(Map map, boolean removeMapAfterSaving, boolean closeApplicationAfterSaving)
    {
    }

    public void saveBBMDefaults(Map map)
    {
    }

    public void setBBMDefaults(Map map)
    {
    }

    public void saveAs(Map map, boolean removeMapAfterSaving, boolean closeApplicationAfterSaving)
    {
    }

    public void setSize(float width, float buttonHeight, float tabHeight, float toolHeight)
    {
        int buttonAmount = buttonTable.getCells().size;
        float buttonWidth = width / buttonAmount;
        this.buttonTable.getCell(this.newButton).size(buttonWidth, buttonHeight);
        this.buttonTable.getCell(this.openButton).size(buttonWidth, buttonHeight);
        this.buttonTable.getCell(this.saveButton).size(buttonWidth, buttonHeight);
        this.buttonTable.getCell(this.saveAsButton).size(buttonWidth, buttonHeight);
        this.buttonTable.getCell(this.saveBBMDefaultsButton).size(buttonWidth, buttonHeight);
        this.buttonTable.getCell(this.setBBMDefaultsButton).size(buttonWidth, buttonHeight);
        this.buttonTable.getCell(this.undoButton).size(buttonWidth, buttonHeight);
        this.buttonTable.getCell(this.redoButton).size(buttonWidth, buttonHeight);
        this.buttonTable.invalidateHierarchy();

        this.mapTabPane.setSize(width, tabHeight);
        this.toolPane.setSize(width, toolHeight);

        this.fileMenuTable.invalidateHierarchy();

        super.setSize(width, buttonHeight + this.mapTabPane.getHeight() + this.toolPane.getHeight());

    }

    @Override
    public void setPosition (float x, float y)
    {
        super.setPosition(x + getWidth() / 2, y + getHeight() / 2);
    }

    private Json createJson()
    {
        return null;
    }
}
