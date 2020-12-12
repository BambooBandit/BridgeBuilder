package com.bamboo.bridgebuilder.commands;

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

    private boolean stairs = false;

    public DrawFence(Map map, SpriteLayer layer, float x, float y)
    {
        this.map = map;
        this.layer = layer;
        this.x = x;
        this.y = y;
        this.connectors = new Array<>();
        this.lastFencePlacedOld = map.lastFencePlaced;

        if(map.editor.fileMenu.toolPane.stairs.selected)
            stairs = true;
    }

    @Override
    public void execute()
    {
        if(this.mapSprite == null)
        {
            SpriteLayer layer = (SpriteLayer) this.map.selectedLayer;
            SpriteTool spriteTool = this.map.getSpriteToolFromSelectedTools();
            this.mapSprite = new MapSprite(this.map, layer, spriteTool, this.x, this.y);
            if(stairs)
                this.mapSprite.setPosition(this.mapSprite.x, this.mapSprite.y + (this.mapSprite.height / 2f));
            this.map.shuffleRandomSpriteTool(false);
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
        this.layer.addMapSprite(this.mapSprite);

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

        int fenceID = 1;
        while(1 == 1)
        {
            MapPoint fromPoint = getAttachedMapPointWithPropertyValue(fromFence, "fenceStart", fenceID);
            MapPoint toPoint = getAttachedMapPointWithPropertyValue(toFence, "fenceEnd", fenceID);
            if(fromPoint != null && toPoint != null)
            {
                createConnectorForFence(fromFence, toFence, fromPoint.x, fromPoint.y, toPoint.x, toPoint.y);
                fenceID ++;
            }
            else
                break;
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

    private void createConnectorForFence(MapSprite fromFence, MapSprite toFence, float fromX, float fromY, float toX, float toY)
    {
        while (1 == 1)
        {
            SpriteTool spriteTool = this.map.getSpriteToolFromSelectedTools();
            if(!Utils.canBuildFenceFromSelectedSpriteTools(this.map))
                return;
            boolean hasFencePost = false;
            if(spriteTool.attachedMapObjectManagers != null) {
                for (int i = 0; i < spriteTool.attachedMapObjectManagers.size; i++) {
                    AttachedMapObjectManager attachedMapObjectManager = spriteTool.attachedMapObjectManagers.get(i);
                    if (Utils.getPropertyField(attachedMapObjectManager.properties, "fenceStart") != null)
                        hasFencePost = true;
                }
            }
            if (hasFencePost)
            {
                this.map.shuffleRandomSpriteTool(true);
                continue;
            }
            else
            {
                if(fromFence.attachedSprites == null)
                {
                    fromFence.attachedSprites = new SpriteLayer(map.editor, map, null);
                    fromFence.attachedSprites.addMapSprite(fromFence);
                }

                MapSprite connector = new MapSprite(this.map, fromFence.attachedSprites, spriteTool, fromX, fromY);
                LabelFieldPropertyValuePropertyField fenceProperty = Utils.getLockedPropertyField(connector.lockedProperties, "Fence");
                fenceProperty.value.setText("true");
                (fromFence.attachedSprites).addMapSprite(connector);
                connector.setPosition(fromX, fromY - connector.height / 2f);
                connector.x2Offset = toX - (connector.x + connector.width);
                connector.y2Offset = toY - (connector.y + connector.height / 2f);
                connector.x3Offset = connector.x2Offset;
                connector.y3Offset = toY - (connector.y + connector.height / 2f);
                float[] spriteVertices = connector.sprite.getVertices();
                connector.offsetMovebox1.setPosition(spriteVertices[SpriteBatch.X2] + connector.x1Offset - connector.offsetMovebox1.width / 2f * connector.offsetMovebox1.scale, spriteVertices[SpriteBatch.Y2] + connector.y1Offset - connector.offsetMovebox1.height / 2f * connector.offsetMovebox1.scale);
                connector.offsetMovebox2.setPosition(spriteVertices[SpriteBatch.X3] + connector.x2Offset - connector.offsetMovebox2.width / 2f * connector.offsetMovebox2.scale, spriteVertices[SpriteBatch.Y3] + connector.y2Offset - connector.offsetMovebox2.height / 2f * connector.offsetMovebox2.scale);
                connector.offsetMovebox3.setPosition(spriteVertices[SpriteBatch.X4] + connector.x3Offset - connector.offsetMovebox3.width / 2f * connector.offsetMovebox3.scale, spriteVertices[SpriteBatch.Y4] + connector.y3Offset - connector.offsetMovebox3.height / 2f * connector.offsetMovebox3.scale);
                connector.offsetMovebox4.setPosition(spriteVertices[SpriteBatch.X1] + connector.x4Offset - connector.offsetMovebox4.width / 2f * connector.offsetMovebox4.scale, spriteVertices[SpriteBatch.Y1] + connector.y4Offset - connector.offsetMovebox4.height / 2f * connector.offsetMovebox4.scale);
                connector.polygon.setOffset(connector.x1Offset, connector.x2Offset, connector.x3Offset, connector.x4Offset, connector.y1Offset, connector.y2Offset, connector.y3Offset, connector.y4Offset);
                connectors.add(connector);
                connector.parentSprite = fromFence;
                fromFence.toEdgeSprite = toFence;
                {
                    if(toFence != null)
                    {
                        if (toFence.fromEdgeSprites == null)
                            toFence.fromEdgeSprites = new Array<>();
                        toFence.fromEdgeSprites.add(fromFence);
                    }
                }
                this.map.shuffleRandomSpriteTool(false);
                break;
            }
        }
    }
}
