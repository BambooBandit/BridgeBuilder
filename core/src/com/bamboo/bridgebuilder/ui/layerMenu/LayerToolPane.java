package com.bamboo.bridgebuilder.ui.layerMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.EditorAssets;
import com.bamboo.bridgebuilder.map.Map;

import static com.bamboo.bridgebuilder.BridgeBuilder.toolHeight;

public class LayerToolPane extends Group
{
    private Stack pane;
    private Table toolTable;
    private Image background;
    private Skin skin;
    private BridgeBuilder editor;

    private LayerTool newSpriteLayer;
    private LayerTool newObjectLayer;

    public LayerMenu menu;

    public LayerToolPane(BridgeBuilder editor, LayerMenu menu, Skin skin, Map map)
    {
        this.menu = menu;
        this.toolTable = new Table();
        this.newSpriteLayer = new LayerTool(LayerTools.NEWSPRITE, this, skin, map);
        this.newObjectLayer = new LayerTool(LayerTools.NEWOBJECT, this, skin, map);
        this.toolTable.left();
        this.toolTable.add(this.newSpriteLayer).padRight(1);
        this.toolTable.add(this.newObjectLayer).padRight(1);

        this.editor = editor;
        this.skin = skin;
        this.pane = new Stack();

        this.background = new Image(EditorAssets.getUIAtlas().createPatch("load-background"));
        this.pane.add(this.background);
        this.pane.add(this.toolTable);

        this.addActor(this.pane);
    }

    @Override
    public void setSize(float width, float height)
    {
        this.pane.setSize(width, height);
        this.background.setBounds(0, 0, width, height);

        // Resize all buttons in the pane
        this.newSpriteLayer.setSize(toolHeight, toolHeight);
        this.newObjectLayer.setSize(toolHeight, toolHeight);
        this.toolTable.getCell(this.newSpriteLayer).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.newObjectLayer).size(toolHeight, toolHeight);
        this.toolTable.invalidateHierarchy();

        this.pane.invalidateHierarchy();

        super.setSize(width, height);
    }
}
