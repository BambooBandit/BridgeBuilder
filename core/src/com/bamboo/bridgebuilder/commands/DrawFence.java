package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.map.*;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.FieldFieldPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.LabelFieldPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

public class DrawFence implements Command
{
    private Map map;
    private SpriteLayer layer;
    public MapSprite mapSprite = null;
    public Array<MapSprite> connectors;
    private float x;
    private float y;
    private MapSprite lastFencePlacedOld;

    private Array<DrawFence> chainedCommands; // Used for adding multiple mapsprites in one execution

    private boolean inFront = false;

    public DrawFence(Map map, SpriteLayer layer, float x, float y)
    {
        this.map = map;
        this.layer = layer;
        this.x = x;
        this.y = y;
        this.connectors = new Array<>();
        this.lastFencePlacedOld = map.lastFencePlaced;

        if(map.editor.fileMenu.toolPane.stairsDialog.shouldConnectorBeInFront())
            inFront = true;
    }

    @Override
    public void execute()
    {
        if(this.mapSprite == null)
        {
            SpriteLayer layer = (SpriteLayer) this.map.selectedLayer;
            SpriteTool spriteTool = this.map.getSpriteToolFromSelectedTools();
            this.mapSprite = new MapSprite(this.map, layer, spriteTool, this.x, this.y);
            if(map.editor.fileMenu.toolPane.stairsDialog.shouldParentHeightBeCentered())
                this.mapSprite.setPosition(this.mapSprite.x, this.mapSprite.y + (this.mapSprite.height / 2f));
            if(map.editor.fileMenu.toolPane.stairsDialog.shouldParentBeTransparent())
            {
                Color color = mapSprite.sprite.getColor();
                mapSprite.setColor(color.r, color.g, color.b, 0);
            }
            this.map.shuffleRandomSpriteTool(false, -1);

            for(int stack = 1; stack < map.editor.fileMenu.toolPane.stairsDialog.getStackAmount(); stack ++)
                createStackedPost(this.mapSprite, stack);
        }
        if(this.map.lastFencePlaced != null)
        {
            // connect last fence and this fence
            if(connectors.size == 0)
                connectFences();
            else
            {
                for(int i = 0; i < this.connectors.size; i ++)
                {
                    MapSprite connector = this.connectors.get(i);
                    connector.layer.children.add(connector);
                }
            }
        }
        this.lastFencePlacedOld = this.map.lastFencePlaced;
        this.map.lastFencePlaced = this.mapSprite;

        if(lastFencePlacedOld != null)
        {
            map.lastFencePlacedDistance = Utils.getDistance(map.lastFencePlaced.x, lastFencePlacedOld.x, map.lastFencePlaced.y, lastFencePlacedOld.y);
            map.editor.fenceDistanceTooltip.label.setText("(Fence Distance) last: " + (Math.round(map.lastFencePlacedDistance * 100.0) / 100.0) + ". current: 0");
        }

        this.layer.addMapSprite(this.mapSprite, -1);

        if(this.map.editor.fileMenu.toolPane.depth.selected)
            this.map.colorizeDepth();

        if(this.chainedCommands != null)
            for(int i = 0; i < this.chainedCommands.size; i ++)
                this.chainedCommands.get(i).execute();
    }

    @Override
    public void undo()
    {
        this.layer.children.removeValue(this.mapSprite, true);
        for(int i = 0; i < this.connectors.size; i ++)
        {
            MapSprite connector = this.connectors.get(i);
            connector.layer.children.removeValue(connector, true);
        }
        this.map.lastFencePlaced = this.lastFencePlacedOld;

        if(this.map.editor.fileMenu.toolPane.depth.selected)
            this.map.colorizeDepth();

        if(this.map.editor.fileMenu.toolPane.spriteGridColors.selected)
            this.map.updateLayerSpriteGrids();

        if(this.chainedCommands != null)
            for(int i = 0; i < this.chainedCommands.size; i ++)
                this.chainedCommands.get(i).undo();
    }

    public void addCommandToChain(DrawFence command)
    {
        if(this.chainedCommands == null)
            this.chainedCommands = new Array<>();
        this.chainedCommands.add(command);
    }

    private void connectFences()
    {
        MapSprite fromFence = this.map.lastFencePlaced;
        MapSprite toFence = this.mapSprite;

        for(int stack = 0; stack < map.editor.fileMenu.toolPane.stairsDialog.getStackAmount(); stack ++)
        {
            int fenceID = 1;
            while (1 == 1)
            {
                MapPoint fromPoint = getAttachedMapPointWithPropertyValue(fromFence, "fenceStart", fenceID);
                MapPoint toPoint = getAttachedMapPointWithPropertyValue(toFence, "fenceEnd", fenceID);
                if (fromPoint != null && toPoint != null)
                {
                    createConnectorForFence(fromFence, toFence, fromPoint.x, fromPoint.y, toPoint.x, toPoint.y, stack);
                    fenceID++;
                } else
                    break;
            }
        }

    }

    private MapPoint getAttachedMapPointWithPropertyValue(MapSprite fence, String propertyString, int value)
    {
        for(int i = 0; i < fence.attachedMapObjects.size; i ++)
        {
            MapObject mapObject = fence.attachedMapObjects.get(i);
            if(mapObject instanceof MapPoint)
            {
                MapPoint mapPoint = (MapPoint) mapObject;
                for(int k = 0; k < mapPoint.properties.size; k ++)
                {
                    PropertyField property = mapPoint.properties.get(k);
                    if(property instanceof FieldFieldPropertyValuePropertyField)
                    {
                        FieldFieldPropertyValuePropertyField fieldProperty = (FieldFieldPropertyValuePropertyField) property;
                        if(fieldProperty.property.getText().equals(propertyString) && fieldProperty.value.getText().equals(Integer.toString(value)))
                            return mapPoint;
                    }
                }
            }
        }
        return null;
    }

    private void createStackedPost(MapSprite post, int stack)
    {
        float stackHeightMultiplier = map.editor.fileMenu.toolPane.stairsDialog.getStackHeightMultiplier();
        if (post.attachedSprites == null)
        {
            post.attachedSprites = new SpriteLayer(map.editor, map, null);
            post.attachedSprites.perspective = post.layer.perspective;
            post.attachedSprites.addMapSprite(post, 0);
        }

        SpriteTool spriteTool = this.map.getSpriteToolFromSelectedTools();
        MapSprite stackedPost = new MapSprite(this.map, post.attachedSprites, spriteTool, post.x + post.width / 2f, post.y + (post.height / 2f) + (post.height * stack * stackHeightMultiplier));
        (post.attachedSprites).addMapSprite(stackedPost, -1);
        stackedPost.parentSprite = post;
        if(map.editor.fileMenu.toolPane.stairsDialog.shouldParentBeTransparent())
        {
            Color color = stackedPost.sprite.getColor();
            stackedPost.setColor(color.r, color.g, color.b, 0);
        }

        map.shuffleRandomSpriteTool(false, stack);
    }

    private void createConnectorForFence(MapSprite fromFence, MapSprite toFence, float fromX, float fromY, float toX, float toY, int stack)
    {
        boolean allSelectedArePosts = true;
        for(int i = 0; i < map.spriteMenu.selectedSpriteTools.size; i ++)
        {
            SpriteTool spriteTool = this.map.spriteMenu.selectedSpriteTools.get(i);
            boolean hasFencePost = false;
            if (spriteTool.attachedMapObjectManagers != null)
            {
                for (int k = 0; k < spriteTool.attachedMapObjectManagers.size; k++)
                {
                    AttachedMapObjectManager attachedMapObjectManager = spriteTool.attachedMapObjectManagers.get(k);
                    if (Utils.getPropertyField(attachedMapObjectManager.properties, "fenceStart") != null)
                        hasFencePost = true;
                }
            }
            if (!hasFencePost)
            {
                allSelectedArePosts = false;
                break;
            }
        }
        if(!allSelectedArePosts)
        {
            while (1 == 1)
            {
                SpriteTool spriteTool = this.map.getSpriteToolFromSelectedTools();
                if (!Utils.canBuildFenceFromSelectedSpriteTools(this.map))
                    return;
                boolean hasFencePost = false;
                if (spriteTool.attachedMapObjectManagers != null)
                {
                    for (int i = 0; i < spriteTool.attachedMapObjectManagers.size; i++)
                    {
                        AttachedMapObjectManager attachedMapObjectManager = spriteTool.attachedMapObjectManagers.get(i);
                        if (Utils.getPropertyField(attachedMapObjectManager.properties, "fenceStart") != null)
                            hasFencePost = true;
                    }
                }
                if (hasFencePost)
                {
                    this.map.shuffleRandomSpriteTool(true, stack);
                    continue;
                }
                else
                    break;
            }
        }
        else
        {
            this.map.shuffleRandomSpriteTool(false, stack);
        }

        if (fromFence.attachedSprites == null)
        {
            fromFence.attachedSprites = new SpriteLayer(map.editor, map, null);
            fromFence.attachedSprites.perspective = fromFence.layer.perspective;
            fromFence.attachedSprites.addMapSprite(fromFence, 0);
        }
        SpriteTool spriteTool = this.map.getSpriteToolFromSelectedTools();

        MapSprite connector = new MapSprite(this.map, fromFence.attachedSprites, spriteTool, fromX, fromY);
        LabelFieldPropertyValuePropertyField fenceProperty = Utils.getLockedPropertyField(connector.lockedProperties, "Fence");
        fenceProperty.value.setText("true");
        if(inFront)
            (fromFence.attachedSprites).addMapSprite(connector, -1);
        else
            (fromFence.attachedSprites).addMapSprite(connector, stack);
        fromX -= (connector.width / 2f) - (connector.width * connector.scale) / 2f;
        toX -= (connector.width / 2f) - (connector.width * connector.scale) / 2f;
        float stackHeightMultiplier = map.editor.fileMenu.toolPane.stairsDialog.getStackHeightMultiplier();
        fromY += fromFence.height * (stack * stackHeightMultiplier);
        toY += fromFence.height * (stack * stackHeightMultiplier);

        float overshoot = map.editor.fileMenu.toolPane.stairsDialog.getConnectorWidthOvershoot();
        float midX = (fromX + toX) / 2f;
        float xDiff = (toX - midX) * overshoot;
        fromX = midX - xDiff;
        toX = midX + xDiff;
        float midY = (fromY + toY) / 2f;
        float yDiff = (toY - midY) * overshoot;
        fromY = midY - yDiff;
        toY = midY + yDiff;

        float heightOffset = map.editor.fileMenu.toolPane.stairsDialog.getHeightOffset();

        if (map.editor.fileMenu.toolPane.stairsDialog.shouldConnectorHeightBeCentered())
            connector.setPosition(fromX, fromY - connector.height / 2f);
        else
            connector.setPosition(fromX, fromY);
        connector.y1Offset += heightOffset;
        connector.x2Offset = toX - (connector.x + connector.width * connector.scale);
        if (map.editor.fileMenu.toolPane.stairsDialog.shouldConnectorHeightBeCentered())
            connector.y2Offset = toY - (connector.y + connector.height / 2f) + heightOffset;
        else
            connector.y2Offset = toY - connector.y + heightOffset;
        connector.x3Offset = connector.x2Offset;
        if (map.editor.fileMenu.toolPane.stairsDialog.shouldConnectorHeightBeCentered())
            connector.y3Offset = toY - (connector.y + connector.height / 2f);
        else
            connector.y3Offset = toY - connector.y;
        float[] spriteVertices = connector.sprite.getVertices();
        connector.offsetMovebox1.setPosition(spriteVertices[SpriteBatch.X2] + map.cameraX + connector.x1Offset - (connector.offsetMovebox1.scale * connector.offsetMovebox1.width / 2f), spriteVertices[SpriteBatch.Y2] + map.cameraY + connector.y1Offset - (connector.offsetMovebox1.scale * connector.offsetMovebox1.height / 2f));
        connector.offsetMovebox2.setPosition(spriteVertices[SpriteBatch.X3] + map.cameraX + connector.x2Offset - (connector.offsetMovebox2.scale * connector.offsetMovebox2.width / 2f), spriteVertices[SpriteBatch.Y3] + map.cameraY + connector.y2Offset - (connector.offsetMovebox2.scale * connector.offsetMovebox2.height / 2f));
        connector.offsetMovebox3.setPosition(spriteVertices[SpriteBatch.X4] + map.cameraX + connector.x3Offset - (connector.offsetMovebox3.scale * connector.offsetMovebox3.width / 2f), spriteVertices[SpriteBatch.Y4] + map.cameraY + connector.y3Offset - (connector.offsetMovebox3.scale * connector.offsetMovebox3.height / 2f));
        connector.offsetMovebox4.setPosition(spriteVertices[SpriteBatch.X1] + map.cameraX + connector.x4Offset - (connector.offsetMovebox4.scale * connector.offsetMovebox4.width / 2f), spriteVertices[SpriteBatch.Y1] + map.cameraY + connector.y4Offset - (connector.offsetMovebox4.scale * connector.offsetMovebox4.height / 2f));
        connector.polygon.setOffset(connector.x1Offset, connector.x2Offset, connector.x3Offset, connector.x4Offset, connector.y1Offset, connector.y2Offset, connector.y3Offset, connector.y4Offset);
        connectors.add(connector);
        connector.parentSprite = fromFence;
        fromFence.toEdgeSprite = toFence;
        {
            if (toFence != null)
            {
                if (toFence.fromEdgeSprites == null)
                    toFence.fromEdgeSprites = new Array<>();
                toFence.fromEdgeSprites.add(fromFence);
            }
        }
        this.map.shuffleRandomSpriteTool(false, stack);
    }
}
