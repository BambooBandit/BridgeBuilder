package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapSprite;
import com.bamboo.bridgebuilder.map.SpriteLayer;

public class CreateStairs implements Command
{
    private Map map;
    private SpriteLayer selectedSpriteLayer;
    private FloatArray vertices;
    private float stairX;
    private float stairY;
    private float initialHeight;
    private float finalHeight;
    private float stairAmountPerMeter;

    private Array<DrawFence> chainedCommands; // Used for adding multiple fences (stairs) in one execution


    public CreateStairs(Map map, SpriteLayer spriteLayer, FloatArray vertices, float stairX, float stairY)
    {
        this.map = map;
        this.selectedSpriteLayer = spriteLayer;
        this.vertices = new FloatArray(vertices);
        this.stairX = stairX;
        this.stairY = stairY;
        this.initialHeight = map.editor.fileMenu.toolPane.stairsDialog.getInitialHeight();
        this.finalHeight = map.editor.fileMenu.toolPane.stairsDialog.getFinalHeight();
        this.stairAmountPerMeter = map.editor.fileMenu.toolPane.stairsDialog.getStairAmount();
    }

    @Override
    public void execute()
    {
        if(chainedCommands == null || chainedCommands.size == 0)
        {
            float x1 = vertices.get(0) + stairX;
            float y1 = vertices.get(1) + stairY;
            float x2 = vertices.get(2) + stairX;
            float y2 = vertices.get(3) + stairY;
            float x3 = vertices.get(4) + stairX;
            float y3 = vertices.get(5) + stairY;
            float x4 = vertices.get(6) + stairX;
            float y4 = vertices.get(7) + stairY;
            float distance = (Utils.getDistance(x1, x2, y1, y2) + Utils.getDistance(x4, x3, y4, y3)) / 2f;
            int stairAmount = (int) (stairAmountPerMeter * distance);
            if(stairAmount == 0)
                stairAmount = 1;
            for (int i = 0; i < stairAmount; i++)
            {
                float progress = ((float) i) / (stairAmount - 1f);
                float fromX = (x1 * (1f - progress)) + (x2 * progress);
                float fromY = (y1 * (1f - progress)) + (y2 * progress);
                float toX = (x4 * (1f - progress)) + (x3 * progress);
                float toY = (y4 * (1f - progress)) + (y3 * progress);
                float height = ((finalHeight - initialHeight) * progress) + initialHeight;
                this.map.editor.activeMap.lastFencePlaced = null;
                DrawFence drawFromFence = new DrawFence(this.map, selectedSpriteLayer, fromX, fromY);
                drawFromFence.execute();
                DrawFence drawToFence = new DrawFence(this.map, selectedSpriteLayer, toX, toY);
                drawToFence.execute();
                for(int k = 0; k < drawToFence.connectors.size; k ++)
                {
                    MapSprite connector = drawToFence.connectors.get(k);
                    connector.y1Offset += height;
                    connector.y2Offset += height;
                    connector.y3Offset += height;
                    connector.y4Offset += height;
                }

                addCommandToChain(drawFromFence);
                addCommandToChain(drawToFence);
                this.map.editor.activeMap.lastFencePlaced = null;
            }
        }
        else
        {
            if (this.chainedCommands != null)
            {
                this.map.editor.activeMap.lastFencePlaced = null;
                for (int i = 0; i < this.chainedCommands.size; i++)
                {
                    this.chainedCommands.get(i).execute();
                    if(i % 2 != 0)
                        this.map.editor.activeMap.lastFencePlaced = null;
                }
                this.map.editor.activeMap.lastFencePlaced = null;
            }
        }
    }

    @Override
    public void undo()
    {
        this.map.input.stairVerticePosition.set(this.stairX, this.stairY);

        if (this.chainedCommands != null)
        {
            this.map.editor.activeMap.lastFencePlaced = null;
            for (int i = 0; i < this.chainedCommands.size; i++)
            {
                this.chainedCommands.get(i).undo();
                if(i % 2 != 0)
                    this.map.editor.activeMap.lastFencePlaced = null;
            }
        }
    }

    public void addCommandToChain(DrawFence command) {
        if (this.chainedCommands == null)
            this.chainedCommands = new Array<>();
        this.chainedCommands.add(command);
    }
}
