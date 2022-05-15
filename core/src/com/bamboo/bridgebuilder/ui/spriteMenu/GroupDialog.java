package com.bamboo.bridgebuilder.ui.spriteMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.commands.CloseGroupDialog;
import com.bamboo.bridgebuilder.map.LayerChild;
import com.bamboo.bridgebuilder.map.ObjectLayer;

public class GroupDialog extends Window
{
    private TextButton close;

    private Skin skin;

    private Table table;
    private Label addLabel;
    private Label removeLabel;
    private Label createLabel;
    private Label selectLabel;
    public CheckBox addCheckBox;
    public CheckBox removeCheckBox;
    public CheckBox createCheckBox;
    public CheckBox selectCheckBox;

    private ButtonGroup<CheckBox> checkBoxGroup;

    private BridgeBuilder editor;

    public GroupDialog(Stage stage, Skin skin, BridgeBuilder editor)
    {
        super("Group", skin);
        this.editor = editor;
        this.skin = skin;

        this.table = new Table();

        ChangeListener changeListener = new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                CheckBox checkBox = (CheckBox) actor;
                if(checkBox.isChecked())
                {
                    if(checkBox == addCheckBox || checkBox == removeCheckBox)
                    {
                        if(editor.activeMap.selectedSprites.size == 0)
                        {
                            checkBox.setChecked(false);
                            return;
                        }
                        editor.fileMenu.toolPane.selectTool(editor.fileMenu.toolPane.select);
                        if(editor.activeMap.groupPolygons != null && editor.activeMap.selectedLayer != editor.activeMap.groupPolygons)
                        {
                            editor.activeMap.selectedLayerPriorToGroupMode = editor.activeMap.selectedLayer;
                            if(editor.activeMap.selectedLayer != null)
                                editor.activeMap.selectedLayer.layerField.unselect();
                            editor.activeMap.selectedLayer = editor.activeMap.groupPolygons;
                        }
                    }
                    else if(checkBox == createCheckBox)
                    {
                        if(editor.activeMap.selectedSprites.size == 0)
                        {
                            checkBox.setChecked(false);
                            return;
                        }
                        editor.fileMenu.toolPane.selectTool(editor.fileMenu.toolPane.drawObject);
                        if(editor.activeMap.groupPolygons == null)
                            editor.activeMap.groupPolygons = new ObjectLayer(editor, editor.activeMap, null);
                        if(editor.activeMap.selectedLayer != editor.activeMap.groupPolygons)
                        {
                            editor.activeMap.selectedLayerPriorToGroupMode = editor.activeMap.selectedLayer;
                            if(editor.activeMap.selectedLayer != null)
                                editor.activeMap.selectedLayer.layerField.unselect();
                            editor.activeMap.selectedLayer = editor.activeMap.groupPolygons;
                        }
                    }
                    else if(checkBox == selectCheckBox)
                    {
                        editor.fileMenu.toolPane.selectTool(editor.fileMenu.toolPane.select);
                        if(editor.activeMap.groupPolygons != null && editor.activeMap.selectedLayer != editor.activeMap.groupPolygons)
                        {
                            editor.activeMap.selectedLayerPriorToGroupMode = editor.activeMap.selectedLayer;
                            if(editor.activeMap.selectedLayer != null)
                                editor.activeMap.selectedLayer.layerField.unselect();
                            editor.activeMap.selectedLayer = editor.activeMap.groupPolygons;
                        }
                    }
                }
            }
        };
        this.addLabel = new Label("Add to existing group: ", skin);
        this.removeLabel = new Label("Remove from existing group: ", skin);
        this.createLabel = new Label("Add to new group: ", skin);
        this.selectLabel = new Label("Select group: ", skin);
        this.addCheckBox = new CheckBox("", skin);
        this.removeCheckBox = new CheckBox("", skin);
        this.createCheckBox = new CheckBox("", skin);
        this.selectCheckBox = new CheckBox("", skin);

        this.checkBoxGroup = new ButtonGroup<>();
        this.checkBoxGroup.setMinCheckCount(0);
        this.checkBoxGroup.add(addCheckBox);
        this.checkBoxGroup.add(removeCheckBox);
        this.checkBoxGroup.add(createCheckBox);
        this.checkBoxGroup.add(selectCheckBox);

        this.addCheckBox.addListener(changeListener);
        this.removeCheckBox.addListener(changeListener);
        this.createCheckBox.addListener(changeListener);
        this.selectCheckBox.addListener(changeListener);

        this.close = new TextButton("Close", skin);
        this.close.setColor(Color.FIREBRICK);
        this.close.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                closeCommand();
            }
        });

        this.table.add(this.addLabel).padBottom(15);
        this.table.add(this.addCheckBox).padBottom(15).row();
        this.table.add(this.removeLabel).padBottom(15);
        this.table.add(this.removeCheckBox).padBottom(15).row();
        this.table.add(this.createLabel).padBottom(15);
        this.table.add(this.createCheckBox).padBottom(15).row();
        this.table.add(this.selectLabel).padBottom(15);
        this.table.add(this.selectCheckBox).padBottom(15).row();
        this.table.add(this.close);

        this.add(this.table);

        setSize(Gdx.graphics.getWidth() / 3f, Gdx.graphics.getHeight() / 2f);
        this.setPosition((stage.getWidth() / 2f), (stage.getHeight() / 2f), Align.center);
        stage.addActor(this);
        setVisible(false);
    }

    public void open()
    {
        editor.activeMap.selectedLayerPriorToGroupMode = editor.activeMap.selectedLayer;
        this.setVisible(true);
    }

    public void close()
    {
        if(!isVisible())
            return;
        addCheckBox.setChecked(false);
        removeCheckBox.setChecked(false);
        createCheckBox.setChecked(false);
        selectCheckBox.setChecked(false);

        if(editor.activeMap != null)
        {
            editor.activeMap.selectedLayer = editor.activeMap.selectedLayerPriorToGroupMode;
            if (editor.activeMap.selectedLayer != null)
                editor.activeMap.selectedLayer.layerField.select();
            if (editor.activeMap.groupPolygons != null)
            {
                for (int i = 0; i < editor.activeMap.groupPolygons.children.size; i++)
                {
                    LayerChild layerChild = editor.activeMap.groupPolygons.children.get(i);
                    layerChild.unselect();
                }
            }
        }
        this.setVisible(false);
    }

    public void closeCommand()
    {
        CloseGroupDialog closeGroupDialog = new CloseGroupDialog(this.editor.activeMap);
        editor.activeMap.executeCommand(closeGroupDialog);
    }

    public boolean shouldAdd()
    {
        if(!this.isVisible())
            return false;

        return addCheckBox.isChecked();
    }

    public boolean shouldRemove()
    {
        if(!this.isVisible())
            return false;

        return removeCheckBox.isChecked();
    }

    public boolean shouldCreate()
    {
        if(!this.isVisible())
            return false;

        return createCheckBox.isChecked();
    }

    public boolean shouldSelect()
    {
        if(!this.isVisible())
            return false;

        return selectCheckBox.isChecked();
    }
}
