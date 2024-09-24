package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
        this.initialHeight = map.editor.fileMenu.buttonPane.stairsDialog.getInitialHeight();
        this.finalHeight = map.editor.fileMenu.buttonPane.stairsDialog.getFinalHeight();
        this.stairAmountPerMeter = map.editor.fileMenu.buttonPane.stairsDialog.getStairAmount();
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
            float distance = (Utils.getDistance(x1, x2, y1 + initialHeight, y2 + finalHeight) + Utils.getDistance(x4, x3, y4 + initialHeight, y3 + finalHeight)) / 2f;
            int stairAmount = (int) (stairAmountPerMeter * distance);
            if(stairAmount == 0)
                stairAmount = 1;
            float thickness = map.editor.fileMenu.buttonPane.stairsDialog.getThickness();
            float progress = (1f / (stairAmount - 1f)) * thickness;
            float height = ((finalHeight - initialHeight) * progress);
            float heightOffset = map.editor.fileMenu.buttonPane.stairsDialog.getHeightOffset();
            float fromXStepSize = ((x1 - stairX) * (1f - progress)) + ((x2 - stairX) * progress);
            float fromYStepSize = ((y1 - stairY) * (1f - progress)) + (((y2 + height) - stairY) * progress);
            float toXStepSize = ((x4 - stairX - (x4 - stairX)) * (1f - progress)) + ((x3 - stairX - (x4 - stairX)) * progress);
            float toYStepSize = (((y4 + height) - stairY - (y4 - stairY)) * (1f - progress)) + ((y3 - stairY - (y4 - stairY)) * progress);
            for (int i = 0; i < stairAmount; i++)
            {
                progress = (((float) i) / (stairAmount - 1f));
                float fromX = (x1 * (1f - progress)) + (x2 * progress);
                float fromY = (y1 * (1f - progress)) + (y2 * progress);
                float toX = (x4 * (1f - progress)) + (x3 * progress);
                float toY = (y4 * (1f - progress)) + (y3 * progress);
                height = ((finalHeight - initialHeight) * progress) + initialHeight;
                this.map.editor.activeMap.lastFencePlaced = null;
                DrawFence drawFromFence = new DrawFence(this.map, selectedSpriteLayer, fromX, fromY);
                drawFromFence.execute();
                DrawFence drawToFence = new DrawFence(this.map, selectedSpriteLayer, toX, toY);
                drawToFence.execute();
                for(int k = 0; k < drawToFence.connectors.size; k ++)
                {
                    MapSprite connector = drawToFence.connectors.get(k);
                    connector.y1Offset += height + heightOffset;
                    connector.y2Offset += height + heightOffset;
                    connector.y3Offset += height;
                    connector.y4Offset += height;

                    if(map.editor.fileMenu.buttonPane.stairsDialog.shouldSnap())
                    {
                        connector.x1Offset += fromXStepSize / 2f;
                        connector.y1Offset += fromYStepSize / 2f;
                        connector.x2Offset += toXStepSize / 2f;
                        connector.y2Offset += toYStepSize / 2f;
                        connector.x3Offset -= toXStepSize / 2f;
                        connector.y3Offset -= toYStepSize / 2f;
                        connector.x4Offset -= fromXStepSize / 2f;
                        connector.y4Offset -= fromYStepSize / 2f;
                    }

                    float[] spriteVertices = connector.sprite.getVertices();
                    connector.offsetMovebox1.setPosition(spriteVertices[SpriteBatch.X2] + map.cameraX + connector.x1Offset - (connector.offsetMovebox1.scale * connector.offsetMovebox1.width / 2f), spriteVertices[SpriteBatch.Y2] + map.cameraY + connector.y1Offset - (connector.offsetMovebox1.scale * connector.offsetMovebox1.height / 2f));
                    connector.offsetMovebox2.setPosition(spriteVertices[SpriteBatch.X3] + map.cameraX + connector.x2Offset - (connector.offsetMovebox2.scale * connector.offsetMovebox2.width / 2f), spriteVertices[SpriteBatch.Y3] + map.cameraY + connector.y2Offset - (connector.offsetMovebox2.scale * connector.offsetMovebox2.height / 2f));
                    connector.offsetMovebox3.setPosition(spriteVertices[SpriteBatch.X4] + map.cameraX + connector.x3Offset - (connector.offsetMovebox3.scale * connector.offsetMovebox3.width / 2f), spriteVertices[SpriteBatch.Y4] + map.cameraY + connector.y3Offset - (connector.offsetMovebox3.scale * connector.offsetMovebox3.height / 2f));
                    connector.offsetMovebox4.setPosition(spriteVertices[SpriteBatch.X1] + map.cameraX + connector.x4Offset - (connector.offsetMovebox4.scale * connector.offsetMovebox4.width / 2f), spriteVertices[SpriteBatch.Y1] + map.cameraY + connector.y4Offset - (connector.offsetMovebox4.scale * connector.offsetMovebox4.height / 2f));
                    connector.polygon.setOffset(connector.x1Offset, connector.x2Offset, connector.x3Offset, connector.x4Offset, connector.y1Offset, connector.y2Offset, connector.y3Offset, connector.y4Offset);
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
