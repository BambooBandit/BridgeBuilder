package com.bamboo.bridgebuilder.ui.fileMenu;

import com.badlogic.gdx.Gdx;
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
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.commands.CreateSpriteSheet;
import com.bamboo.bridgebuilder.commands.CutMapObjects;
import com.bamboo.bridgebuilder.commands.CutSelectedMapSprites;
import com.bamboo.bridgebuilder.commands.PasteItems;
import com.bamboo.bridgebuilder.data.*;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapSprite;
import com.bamboo.bridgebuilder.map.SpriteLayer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import static com.bamboo.bridgebuilder.BridgeBuilder.prefs;
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
    private TextButton applyBBMDefaultsButton;
    public TextButton cutButton;
    public TextButton pasteButton;
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
        this.saveBBMDefaultsButton = new TextButton("Save Default", skin);
        this.applyBBMDefaultsButton = new TextButton("Apply Default", skin);
        this.cutButton = new TextButton("Cut", skin, "checked");
        this.pasteButton = new TextButton("Paste", skin, "checked");
        this.undoButton = new TextButton("Undo", skin);
        this.redoButton = new TextButton("Redo", skin);

        // Buttons text color
        this.newButton.getLabel().setColor(Color.BLACK);
        this.openButton.getLabel().setColor(Color.BLACK);
        this.saveButton.getLabel().setColor(Color.BLACK);
        this.saveAsButton.getLabel().setColor(Color.BLACK);
        this.saveBBMDefaultsButton.getLabel().setColor(Color.BLACK);
        this.applyBBMDefaultsButton.getLabel().setColor(Color.BLACK);
        this.cutButton.getLabel().setColor(Color.BLACK);
        this.pasteButton.getLabel().setColor(Color.BLACK);
        this.undoButton.getLabel().setColor(Color.BLACK);
        this.redoButton.getLabel().setColor(Color.BLACK);

        // Add listeners
        this.newButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                newMap();
            }
        });
        this.openButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                open();
            }
        });
        this.saveButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if(editor.activeMap != null)
                    save(editor.activeMap, false, false);
            }
        });
        this.saveAsButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if(editor.activeMap != null)
                    saveAs(editor.activeMap, false, false);
            }
        });
        this.saveBBMDefaultsButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if(editor.getScreen() != null)
                {
                    Map map = (Map) editor.getScreen();
                    new YesNoDialog("Override and save new BBM default properties?", editor.stage, "", EditorAssets.getUISkin(), false)
                    {
                        @Override
                        public void yes()
                        {
                            editor.fileMenu.saveBBMDefaults(map);
                        }

                        @Override
                        public void no()
                        {
                        }
                    };
                }
            }
        });
        this.applyBBMDefaultsButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if(editor.getScreen() != null)
                {
                    Map map = (Map) editor.getScreen();
                    new ConfirmApplyBBMDefaultsDialog(map, editor.stage, EditorAssets.getUISkin());

                }
            }
        });
        this.cutButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if(editor.getScreen() != null)
                {
                    Map map = (Map) editor.getScreen();
                    if(map.selectedLayer == null)
                        return;
                    if(map.selectedObjects.size != 0)
                    {
                        CutMapObjects cutMapObjects = new CutMapObjects(map.selectedObjects, map.selectedLayer);
                        map.executeCommand(cutMapObjects);
                    }
                    else if(map.selectedSprites.size != 0)
                    {
                        CutSelectedMapSprites cutSelectedMapSprites = new CutSelectedMapSprites(map.selectedSprites, (SpriteLayer) map.selectedLayer);
                        map.executeCommand(cutSelectedMapSprites);
                    }
                }
            }
        });
        this.pasteButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if(editor.getScreen() != null && editor.copiedItems.size != 0)
                {
                    Map map = (Map) editor.getScreen();
                    if(map.editor.copiedItems.first() instanceof MapSprite)
                    {
                        for (int i = 0; i < map.editor.copiedItems.size; i++)
                        {
                            MapSprite copiedMapSprite = (MapSprite) map.editor.copiedItems.get(i);
                            if(!map.spriteMenu.hasSpriteSheet(copiedMapSprite.tool.sheet.name))
                            {
                                CreateSpriteSheet createSpriteSheet = new CreateSpriteSheet(map, copiedMapSprite.tool.sheet.name);
                                map.executeCommand(createSpriteSheet);
                            }
                        }
                    }
                    PasteItems pasteItems = new PasteItems(map, map.selectedLayer);
                    map.executeCommand(pasteItems);
                }
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
        this.buttonTable.add(this.applyBBMDefaultsButton);
        this.buttonTable.add(this.cutButton);
        this.buttonTable.add(this.pasteButton);
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

    public void open()
    {
        if (editor.fileChooserOpen)
            return;
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                editor.fileChooserOpen = true;
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter bbmFilter = new FileNameExtensionFilter(
                        "bbm files (*.bbm)", "bbm");
                chooser.setFileFilter(bbmFilter);
                String path = prefs.getString("lastSave", "null");
                if(!path.equals("null"))
                    chooser.setSelectedFile(new File(path));
                JFrame f = new JFrame();
                f.setVisible(true);
                f.setAlwaysOnTop(true);
                f.toFront();
                f.setVisible(false);
                int res = chooser.showOpenDialog(f);
                f.dispose();
                editor.fileChooserOpen = false;
                if (res == JFileChooser.APPROVE_OPTION)
                {
                    Gdx.app.postRunnable(() ->
                    {
                        try
                        {
                            File file = chooser.getSelectedFile();
                            String content = new Scanner(file).useDelimiter("\\Z").next();
                            Json json = createJson();
                            MapData mapData = json.fromJson(MapData.class, content);
                            Map newMap = new Map(editor, mapData);
                            newMap.file = file;
                            editor.addToMaps(newMap);
                            mapTabPane.lookAtMap(newMap);
                        }
                        catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }).start();
    }

    public void newMap()
    {
        new YesNoDialog("Create a new map with BBM default settings/properties?", FileMenu.this.editor.stage, "", EditorAssets.getUISkin(), true)
        {
            Map newMap;
            @Override
            public void yes()
            {
                try
                {
                    File file = new File("defaultBBM.bbm");
                    String content = null;
                    content = new Scanner(file).useDelimiter("\\Z").next();
                    Json json = createJson();
                    MapData mapData = json.fromJson(MapData.class, content);
                    mapData.layers.clear();
                    Map newMap = new Map(editor, mapData);
                    newMap.file = file;
                    editor.addToMaps(newMap);
                    mapTabPane.lookAtMap(newMap);
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
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

    public void save(Map map, boolean removeMapAfterSaving, boolean closeApplicationAfterSaving)
    {
        if (map.file == null)
        {
            saveAs(map, removeMapAfterSaving, closeApplicationAfterSaving);
            return;
        }
        boolean depthSelected = editor.fileMenu.toolPane.depth.selected;
        if(depthSelected)
            editor.fileMenu.toolPane.selectTool(editor.fileMenu.toolPane.depth);
        MapData mapData = new MapData(map, false);
        if(depthSelected)
            editor.fileMenu.toolPane.selectTool(editor.fileMenu.toolPane.depth);

        Json json = createJson();

        File file = map.file;
        try
        {
            //Create the file
            if (file.createNewFile())
                Utils.println("File is created!");
            else
                Utils.println("File already exists.");

            //Write Content
            FileWriter writer = new FileWriter(file);
            writer.write(json.prettyPrint(mapData));
            writer.close();

            map.setChanged(false);

            if(removeMapAfterSaving)
                editor.fileMenu.mapTabPane.removeMap(map);
            if(closeApplicationAfterSaving)
                Gdx.app.exit();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void saveAs(Map map, boolean removeMapAfterSaving, boolean closeApplicationAfterSaving)
    {
        if(editor.fileChooserOpen || editor.getScreen() == null)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                editor.fileChooserOpen = true;
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter bbmFilter = new FileNameExtensionFilter(
                        "bbm files (*.bbm)", "bbm");
                chooser.setFileFilter(bbmFilter);
                if(map.file != null)
                    chooser.setSelectedFile(map.file);
                else
                    chooser.setSelectedFile(new File("map.bbm"));
                JFrame f = new JFrame();
                f.setVisible(true);
                f.setAlwaysOnTop(true);
                f.toFront();
                f.setVisible(false);
                int res = chooser.showSaveDialog(f);
                f.dispose();
                editor.fileChooserOpen = false;
                if (res == JFileChooser.APPROVE_OPTION)
                {
                    Gdx.app.postRunnable(() ->
                    {
                        map.setName(chooser.getSelectedFile().getName());
                        MapData mapData = new MapData(map, false);
                        Json json = createJson();

                        File file = chooser.getSelectedFile();
                        map.file = file;
                        try
                        {
                            //Create the file
                            file.createNewFile();
                            prefs.putString("lastSave", file.getAbsolutePath());
                            prefs.flush();

                            //Write Content
                            FileWriter writer = new FileWriter(file);
                            writer.write(json.prettyPrint(mapData));
                            writer.close();

                            map.setChanged(false);

                            if(removeMapAfterSaving)
                                editor.fileMenu.mapTabPane.removeMap(map);
                            if(closeApplicationAfterSaving)
                                Gdx.app.exit();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }).start();
    }

    public void saveBBMDefaults(Map map)
    {
        MapData mapData = new MapData(map, true);

        Json json = createJson();

        File file = new File("defaultBBM.bbm");
        try
        {
            //Create the file
            if (file.createNewFile())
                Utils.println("File is created!");
            else
                Utils.println("File already exists.");

            //Write Content
            FileWriter writer = new FileWriter(file);
            writer.write(json.prettyPrint(mapData));
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void setBBMDefaults(Map map)
    {
        try
        {
            File file = new File("defaultBBM.bbm");
            String content = null;
            content = new Scanner(file).useDelimiter("\\Z").next();
            Json json = createJson();
            MapData mapData = json.fromJson(MapData.class, content);
            map.loadMap(mapData, true);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public void setBBMDefaults(Map map, String defaultSheet, String currentSheet)
    {
        try
        {
            File file = new File("defaultBBM.bbm");
            String content = null;
            content = new Scanner(file).useDelimiter("\\Z").next();
            Json json = createJson();
            MapData mapData = json.fromJson(MapData.class, content);
            map.loadMap(mapData, defaultSheet, currentSheet);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
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
        this.buttonTable.getCell(this.applyBBMDefaultsButton).size(buttonWidth, buttonHeight);
        this.buttonTable.getCell(this.cutButton).size(buttonWidth, buttonHeight);
        this.buttonTable.getCell(this.pasteButton).size(buttonWidth, buttonHeight);
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
        Json json = new Json();
        json.addClassTag("map", MapData.class);
        json.addClassTag("prop", PropertyData.class);
        json.addClassTag("ffProp", FieldFieldPropertyValuePropertyFieldData.class);
        json.addClassTag("lfProp", LabelFieldPropertyValuePropertyFieldData.class);
        json.addClassTag("llProp", LabelLabelPropertyValuePropertyFieldData.class);
        json.addClassTag("rgbaProp", ColorPropertyFieldData.class);
        json.addClassTag("rgbProp", OpaqueColorPropertyFieldData.class);
        json.addClassTag("lightProp", LightPropertyFieldData.class);
        json.addClassTag("layer", LayerData.class);
        json.addClassTag("sLayer", SpriteLayerData.class);
        json.addClassTag("oLayer", ObjectLayerData.class);
        json.addClassTag("child", LayerChildData.class);
        json.addClassTag("sprite", MapSpriteData.class);
        json.addClassTag("aSprite", AttachedMapSpriteData.class);
        json.addClassTag("sheet", SpriteSheetData.class);
        json.addClassTag("tool", ToolData.class);
        json.addClassTag("obj", MapObjectData.class);
        json.addClassTag("point", MapPointData.class);
        json.addClassTag("poly", MapPolygonData.class);
        json.addClassTag("cell", CellData.class);
        json.addClassTag("group", GroupMapPolygonData.class);
        return json;
    }

}
