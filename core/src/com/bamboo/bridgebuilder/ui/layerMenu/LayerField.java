package com.bamboo.bridgebuilder.ui.layerMenu;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.commands.MoveLayerIndex;
import com.bamboo.bridgebuilder.commands.RemoveLayer;
import com.bamboo.bridgebuilder.map.Layer;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.ObjectLayer;
import com.bamboo.bridgebuilder.map.SpriteLayer;

import static com.bamboo.bridgebuilder.BridgeBuilder.toolHeight;

public class LayerField extends Group
{
    public TextField layerName;
    private LayerTypes type;
    public Image typeImage;
    public Image attachedImage;
    private TextButton up;
    private TextButton down;
    private Stack visibilityStack;
    private Stack attachedVisibilityStack;
    public Image visibleImg;
    public Image notVisibleImg;
    public Image attachedVisibleImg;
    public Image attachedNotVisibleImg;
    public TextButton remove;
    private Table table;

    public Layer mapLayer;

    private LayerMenu menu;

    public boolean isSelected = false;
    public boolean isSecondarySelected = false;

    public LayerField(String name, LayerTypes type, BridgeBuilder editor, Map map, Skin skin, final LayerMenu menu)
    {
        this.menu = menu;
        this.type = type;

        if(type == LayerTypes.SPRITE)
            this.mapLayer = new SpriteLayer(editor, map, this);
        else if(type == LayerTypes.OBJECT)
            this.mapLayer = new ObjectLayer(editor, map, this);

        this.layerName = new TextField(name, skin);
        this.layerName.setDisabled(true);
        this.layerName.addListener(new InputListener()
        {
            public boolean keyDown (InputEvent event, int keycode)
            {
                if(keycode == Input.Keys.ENTER)
                {
                    createOrRemoveGrid(mapLayer, layerName);
                    return true;
                }
                return false;
            }
        });

        this.layerName.addListener(new ClickListener()
        {
            @Override
            public void clicked (InputEvent event, float x, float y)
            {
                for(int i = 0; i < map.layerMenu.layers.size; i ++)
                {
                    LayerField layer = map.layerMenu.layers.get(i);
                    if(layer.layerName == layerName)
                        continue;
                    layer.layerName.clearSelection();
                    layer.layerName.setDisabled(true);
                }
                int tapCount = getTapCount();
                if(tapCount == 2)
                    layerName.setDisabled(false);
            }
            });

        this.table = new Table();
        this.table.bottom().left();
        this.typeImage = new Image(new Texture("ui/" + type.name + ".png")); // TODO pack it in atlas
        if(type == LayerTypes.SPRITE)
        {
            this.attachedImage = new Image(new Texture("ui/" + LayerTypes.OBJECT.name + ".png")); // TODO pack it in atlas
            this.attachedVisibilityStack = new Stack();
            this.attachedVisibleImg = new Image(new Texture("ui/visible.png")); // TODO pack it in atlas
            this.attachedNotVisibleImg = new Image(new Texture("ui/notVisible.png")); // TODO pack it in atlas
        }
        this.up = new TextButton("^", skin);
        this.down = new TextButton("v", skin);
        this.visibilityStack = new Stack();
        this.visibleImg = new Image(new Texture("ui/visible.png")); // TODO pack it in atlas
        this.notVisibleImg = new Image(new Texture("ui/notVisible.png")); // TODO pack it in atlas
        this.visibilityStack.add(this.typeImage);
        this.visibilityStack.add(this.visibleImg);
        this.visibilityStack.add(this.notVisibleImg);
        this.notVisibleImg.setVisible(false);
        if(this.attachedImage != null)
        {
            this.attachedVisibilityStack.add(this.attachedImage);
            this.attachedVisibilityStack.add(this.attachedVisibleImg);
            this.attachedVisibilityStack.add(this.attachedNotVisibleImg);
            this.attachedNotVisibleImg.setVisible(false);
        }
        this.remove = new TextButton("X", skin);
        this.remove.setColor(Color.FIREBRICK);

        this.table.add(this.layerName);
        this.table.add(this.visibilityStack);
        if(this.attachedVisibilityStack != null)
            this.table.add(this.attachedVisibilityStack);
        else
            this.table.add().padRight(toolHeight);
        this.table.add(this.up);
        this.table.add(this.down);
        this.table.add(this.remove);

        final LayerField layer = this;
        this.up.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                int oldIndex = map.layerMenu.layers.indexOf(layer, true);
                int newIndex = oldIndex - 1;
                if(newIndex < 0)
                    return;
                MoveLayerIndex moveLayerIndex = new MoveLayerIndex(map, layer.mapLayer, oldIndex, newIndex);
                map.executeCommand(moveLayerIndex);
            }
        });
        this.down.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                int oldIndex = map.layerMenu.layers.indexOf(layer, true);
                int newIndex = oldIndex + 1;
                if(newIndex >= map.layerMenu.layers.size)
                    return;
                MoveLayerIndex moveLayerIndex = new MoveLayerIndex(map, layer.mapLayer, oldIndex, newIndex);
                map.executeCommand(moveLayerIndex);
            }
        });
        this.visibleImg.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                visibleImg.setVisible(false);
                notVisibleImg.setVisible(true);
            }
        });
        this.notVisibleImg.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                visibleImg.setVisible(true);
                notVisibleImg.setVisible(false);
            }
        });
        if(this.attachedVisibilityStack != null)
        {
            this.attachedVisibleImg.addListener(new ClickListener()
            {
                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    attachedVisibleImg.setVisible(false);
                    attachedNotVisibleImg.setVisible(true);
                }
            });
            this.attachedNotVisibleImg.addListener(new ClickListener()
            {
                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    attachedVisibleImg.setVisible(true);
                    attachedNotVisibleImg.setVisible(false);
                }
            });
        }
        this.remove.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                RemoveLayer removeLayer = new RemoveLayer(map, layer.mapLayer);
                map.executeCommand(removeLayer);
            }
        });

        addActor(this.table);
    }

    @Override
    public void setSize(float width, float height)
    {
        this.layerName.setSize(width - (height * 5), height);
        this.table.getCell(this.layerName).size(width - (height * 5), height);
        this.table.getCell(this.up).size(height, height);
        this.table.getCell(this.down).size(height, height);
        this.table.getCell(this.visibilityStack).size(height, height);
        this.typeImage.setSize(height, height);
        this.visibleImg.setSize(height, height);
        this.notVisibleImg.setSize(height, height);
        if(this.attachedVisibilityStack != null)
        {
            this.table.getCell(this.attachedVisibilityStack).size(height, height);
            this.attachedImage.setSize(height, height);
            this.attachedVisibleImg.setSize(height, height);
            this.attachedNotVisibleImg.setSize(height, height);
        }
        this.table.getCell(this.remove).size(height, height);
        this.table.invalidateHierarchy();
        super.setSize(width, height);
    }

    public void select()
    {
        if(isSecondarySelected)
            secondaryUnselect();

        this.layerName.setColor(Color.GREEN);
        this.up.setColor(Color.GREEN);
        this.down.setColor(Color.GREEN);
        this.isSelected = true;
    }

    public void secondarySelect()
    {
        if(isSelected)
            return;

        this.layerName.setColor(.75f, 1, .75f, 1);
        this.up.setColor(.75f, 1, .75f, 1);
        this.down.setColor(.75f, 1, .75f, 1);
        this.isSecondarySelected = true;
    }

    public void unselect()
    {
        if(!isSelected)
            return;

        this.layerName.setColor(Color.WHITE);
        this.up.setColor(Color.WHITE);
        this.down.setColor(Color.WHITE);
        this.isSelected = false;
    }

    public void secondaryUnselect()
    {
        if(isSelected)
            return;
        mapLayer.map.secondarySelectedLayer = null;
        this.layerName.setColor(Color.WHITE);
        this.up.setColor(Color.WHITE);
        this.down.setColor(Color.WHITE);
        this.isSecondarySelected = false;
    }

    public static void createOrRemoveGrid(Layer mapLayer, TextField layerName)
    {
        if(mapLayer instanceof ObjectLayer)
        {
            String name = layerName.getText();
            ObjectLayer objectLayer = (ObjectLayer) mapLayer;
            if (name.startsWith("floor ") && Character.isDigit(name.charAt(name.length() - 1)))
                objectLayer.createGrid();
            else
                objectLayer.removeGrid();
        }
    }
}

