package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapSprite;

public class PaintMapSprite implements Command
{
    private Map map;
    private MapSprite mapSprite = null;
    private float oldR, oldG, oldB, oldA;
    private float newR, newG, newB, newA;

    private Array<PaintMapSprite> chainedCommands; // Used for adding multiple mapsprites in one execution

    public PaintMapSprite(Map map, MapSprite mapSprite, float newR, float newG, float newB, float newA)
    {
        this.map = map;
        this.mapSprite = mapSprite;
        this.oldR = mapSprite.sprite.getColor().r;
        this.oldG = mapSprite.sprite.getColor().g;
        this.oldB = mapSprite.sprite.getColor().b;
        this.oldA = mapSprite.sprite.getColor().a;
        this.newR = newR;
        this.newG = newG;
        this.newB = newB;
        this.newA = newA;
    }

    @Override
    public void execute()
    {
        this.mapSprite.setColor(newR, newG, newB, newA);

        if (this.map.editor.fileMenu.toolPane.depth.selected)
            this.map.colorizeDepth();

        if (this.map.editor.fileMenu.toolPane.spriteGridColors.selected)
            this.map.updateLayerSpriteGrids();

        if (this.chainedCommands != null)
            for (int i = 0; i < this.chainedCommands.size; i++)
                this.chainedCommands.get(i).execute();
    }

    @Override
    public void undo()
    {
        this.mapSprite.setColor(oldR, oldG, oldB, oldA);

        if (this.map.editor.fileMenu.toolPane.depth.selected)
            this.map.colorizeDepth();

        if (this.map.editor.fileMenu.toolPane.spriteGridColors.selected)
            this.map.updateLayerSpriteGrids();

        if (this.chainedCommands != null)
            for (int i = 0; i < this.chainedCommands.size; i++)
                this.chainedCommands.get(i).undo();
    }

    public void addCommandToChain(PaintMapSprite command)
    {
        if (this.chainedCommands == null)
            this.chainedCommands = new Array<>();
        this.chainedCommands.add(command);
    }
}