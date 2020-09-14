package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.map.*;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.FieldFieldPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

public class DrawMapSprite implements Command {
    private Map map;
    private SpriteLayer layer;
    private MapSprite mapSprite = null;
    private float x;
    private float y;

    private Array<DrawMapSprite> chainedCommands; // Used for adding multiple mapsprites in one execution

    public DrawMapSprite(Map map, SpriteLayer layer, float x, float y) {
        this.map = map;
        this.layer = layer;
        this.x = x;
        this.y = y;
    }

    @Override
    public void execute() {
        if (this.mapSprite == null) {
            SpriteLayer layer = (SpriteLayer) this.map.selectedLayer;
            SpriteTool spriteTool = this.map.getSpriteToolFromSelectedTools();
            this.mapSprite = new MapSprite(this.map, layer, spriteTool, this.x, this.y);
            this.map.shuffleRandomSpriteTool(false);
        }
        this.layer.addMapSprite(this.mapSprite);

        if (this.map.editor.fileMenu.toolPane.depth.selected)
            this.map.colorizeDepth();

        if (this.chainedCommands != null)
            for (int i = 0; i < this.chainedCommands.size; i++)
                this.chainedCommands.get(i).execute();
    }

    @Override
    public void undo() {
        this.layer.children.removeValue(this.mapSprite, true);

        if (this.map.editor.fileMenu.toolPane.depth.selected)
            this.map.colorizeDepth();

        if (this.map.editor.fileMenu.toolPane.spriteGridColors.selected)
            this.map.updateLayerSpriteGrids();

        if (this.chainedCommands != null)
            for (int i = 0; i < this.chainedCommands.size; i++)
                this.chainedCommands.get(i).undo();
    }

    public void addCommandToChain(DrawMapSprite command) {
        if (this.chainedCommands == null)
            this.chainedCommands = new Array<>();
        this.chainedCommands.add(command);
    }
}