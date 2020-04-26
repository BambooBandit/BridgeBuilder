package com.bamboo.bridgebuilder.ui.propertyMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.EditorAssets;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.LabelFieldPropertyValuePropertyField;

public class LayerPropertyPanel extends Group
{
    public static int textFieldHeight = 32;

    private BridgeBuilder editor;
    private PropertyMenu menu;

    private Image background;
    private Stack stack;
    public Table table; // Holds all the text fields

    public LabelFieldPropertyValuePropertyField layerWidthProperty;
    public LabelFieldPropertyValuePropertyField layerHeightProperty;
    public LabelFieldPropertyValuePropertyField layerZProperty;

    private Table buttonDirectionTable;
    private ButtonGroup<TextButton> buttonDirectionUpDownGroup;
    private ButtonGroup<TextButton> buttonDirectionLeftRightGroup;
    public TextButton down, up, right, left, apply; // Dictates which direction the map grows

    public LayerPropertyPanel(Skin skin, PropertyMenu menu, BridgeBuilder editor)
    {
        this.editor = editor;
        this.menu = menu;

        this.background = new Image(EditorAssets.getUIAtlas().createPatch("load-background"));
        this.stack = new Stack();
        this.table = new Table();
        this.table.left().top();

        TextField.TextFieldFilter.DigitsOnlyFilter filter = new TextField.TextFieldFilter.DigitsOnlyFilter();

        this.layerWidthProperty = new LabelFieldPropertyValuePropertyField("Layer Width", "5", skin, menu, null, false);
        this.layerWidthProperty.value.setTextFieldFilter(filter);
        this.layerHeightProperty = new LabelFieldPropertyValuePropertyField("Layer Height", "5", skin, menu, null, false);
        this.layerHeightProperty.value.setTextFieldFilter(filter);
        this.layerZProperty = new LabelFieldPropertyValuePropertyField("Layer Z", "0", skin, menu, null, false);
        this.layerZProperty.value.setTextFieldFilter(new TextField.TextFieldFilter()
        {
            @Override
            public boolean acceptChar(TextField textField, char c)
            {
                return c == '.' || Character.isDigit(c) || c == '-';
            }
        });


        this.buttonDirectionTable = new Table();
        this.buttonDirectionUpDownGroup = new ButtonGroup<>();
        this.buttonDirectionLeftRightGroup = new ButtonGroup<>();
        this.down = new TextButton("v", skin, "checked");
        this.up = new TextButton("^", skin, "checked");
        this.right = new TextButton(">", skin, "checked");
        this.left = new TextButton("<", skin, "checked");
        this.apply = new TextButton("Apply", skin);
        this.buttonDirectionUpDownGroup.add(down, up);
        this.buttonDirectionLeftRightGroup.add(right, left);
        this.buttonDirectionTable.add(down, up, right, left, apply);
        this.apply.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                menu.map.selectedLayer.setZ(Float.parseFloat(layerZProperty.value.getText()));
                menu.map.selectedLayer.resize(Integer.parseInt(layerWidthProperty.value.getText()), Integer.parseInt(layerHeightProperty.value.getText()), down.isChecked(), right.isChecked());
            }
        });

        this.table.add(this.layerWidthProperty).padBottom(1).row();
        this.table.add(this.layerHeightProperty).padBottom(1).row();
        this.table.add(this.layerZProperty).padBottom(1).row();
        this.table.add(this.buttonDirectionTable);

        this.stack.add(this.background);
        this.stack.add(this.table);

        this.addActor(this.stack);
    }

    @Override
    public void setSize(float width, float height)
    {
        for(int i = 0; i < this.table.getChildren().size; i ++)
        {
            this.table.getChildren().get(i).setSize(width, textFieldHeight);
            this.table.getCell(this.table.getChildren().get(i)).size(width, textFieldHeight);
        }
        for(int i = 0; i < this.buttonDirectionTable.getChildren().size; i ++)
        {
            this.buttonDirectionTable.getChildren().get(i).setSize(width / 7, textFieldHeight);
            this.buttonDirectionTable.getCell(this.buttonDirectionTable.getChildren().get(i)).size(width / 7, textFieldHeight);
        }
        this.buttonDirectionTable.getCell(this.apply).size(width - (width / 7) * 4, textFieldHeight);
        this.buttonDirectionTable.invalidateHierarchy();

        float newHeight = textFieldHeight * 4;

        this.background.setBounds(0, 0, width, newHeight);
        this.stack.setSize(width, newHeight);
        this.stack.invalidateHierarchy();

        if(height == 0)
            super.setSize(0, 0);
        else
            super.setSize(width, newHeight);
    }
}