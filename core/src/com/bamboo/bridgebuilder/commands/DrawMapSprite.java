package com.bamboo.bridgebuilder.commands;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.map.*;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.FieldFieldPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

public class DrawMapSprite implements Command
{
    private Map map;
    private SpriteLayer layer;
    private MapSprite mapSprite = null;
    private float x;
    private float y;

    private Array<DrawMapSprite> chainedCommands; // Used for adding multiple mapsprites in one execution

    public DrawMapSprite(Map map, SpriteLayer layer, float x, float y)
    {
        this.map = map;
        this.layer = layer;
        this.x = x;
        this.y = y;
    }

    @Override
    public void execute()
    {
        if(this.mapSprite == null)
        {
            SpriteLayer layer = (SpriteLayer) this.map.selectedLayer;
            SpriteTool spriteTool = this.map.getSpriteToolFromSelectedTools();
            this.mapSprite = new MapSprite(this.map, layer, spriteTool, this.x, this.y);
            this.map.shuffleRandomSpriteTool(false);
        }
        if(this.map.editor.fileMenu.toolPane.fence.selected)
        {
            if(this.map.lastFencePlaced != null)
            {
                // connect last fence and this fence
                connectFences();
            }
            this.map.lastFencePlaced = this.mapSprite;
        }
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

        if(this.map.editor.fileMenu.toolPane.depth.selected)
            this.map.colorizeDepth();

        if(this.map.editor.fileMenu.toolPane.spriteGridColors.selected)
            this.map.updateLayerSpriteGrids();

        if(this.chainedCommands != null)
            for(int i = 0; i < this.chainedCommands.size; i ++)
                this.chainedCommands.get(i).undo();
    }

    public void addCommandToChain(DrawMapSprite command)
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
            boolean doesntHaveFencePost = false;
            for (int k = 0; k < this.map.getAllSelectedSpriteTools().size; k++)
            {
                SpriteTool spriteTool1 = this.map.getAllSelectedSpriteTools().get(k);
                boolean hasFencePost = false;
                if (!spriteTool1.hasAttachedMapObjects())
                    doesntHaveFencePost = true;
                else {
                    for (int i = 0; i < spriteTool1.attachedMapObjectManagers.size; i++) {
                        AttachedMapObjectManager attachedMapObjectManager = spriteTool1.attachedMapObjectManagers.get(i);
                        if (Utils.getPropertyField(attachedMapObjectManager.properties, "fenceStart") != null && Utils.getPropertyField(attachedMapObjectManager.properties, "fenceEnd") != null)
                            hasFencePost = true;
                    }
                }
                if(!hasFencePost)
                    doesntHaveFencePost = true;
            }
            System.out.println("1 doesnt have fence post? " + doesntHaveFencePost + "... " + spriteTool.name);
            if (!doesntHaveFencePost)
                return;
            boolean hasFencePost = false;
            if(spriteTool.attachedMapObjectManagers != null) {
                for (int i = 0; i < spriteTool.attachedMapObjectManagers.size; i++) {
                    AttachedMapObjectManager attachedMapObjectManager = spriteTool.attachedMapObjectManagers.get(i);
                    System.out.println(attachedMapObjectManager.properties.size);
                    if (Utils.getPropertyField(attachedMapObjectManager.properties, "fenceStart") != null)
                        hasFencePost = true;
                }
            }
            System.out.println("2 doesnt have fence post? " + doesntHaveFencePost + "... " + spriteTool.name);
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
                (fromFence.attachedSprites).addMapSprite(connector);
                connector.setPosition(fromX, fromY - connector.height / 2f);
                connector.x2Offset = toX - (connector.x + connector.width);
                connector.y2Offset = toY - (connector.y + connector.height / 2f);
                connector.x3Offset = connector.x2Offset;
                connector.y3Offset = toY - (connector.y + connector.height / 2f);
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
                System.out.println("yayyy " + fromX + ", " + fromY + ", " + toX + ", " + toY);
                break;
            }
        }
    }
}
