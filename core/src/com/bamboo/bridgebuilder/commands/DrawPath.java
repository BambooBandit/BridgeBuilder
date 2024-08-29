package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapSprite;
import com.bamboo.bridgebuilder.map.SpriteLayer;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

public class DrawPath implements Command {
    private Map map;
    private SpriteLayer layer;
    private MapSprite mapSprite = null;
    private float x;
    private float y;

    private Array<DrawPath> chainedCommands; // Used for adding multiple mapsprites in one execution

    public DrawPath(Map map, SpriteTool spriteTool, SpriteLayer layer, float x, float y)
    {
        this.map = map;
        this.layer = layer;
        this.x = x;
        this.y = y;
        this.mapSprite = new MapSprite(this.map, layer, spriteTool, this.x, this.y, null);
        this.map.shuffleRandomSpriteTool(false, -1);
    }

    @Override
    public void execute() {
        this.layer.addMapSprite(this.mapSprite, -1);

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

    public void addCommandToChain(DrawPath command) {
        if (this.chainedCommands == null)
            this.chainedCommands = new Array<>();
        this.chainedCommands.add(command);
    }
}