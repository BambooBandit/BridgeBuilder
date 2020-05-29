package com.bamboo.bridgebuilder.ui.fileMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.EditorAssets;
import com.bamboo.bridgebuilder.commands.MoveMapSpriteIndex;
import com.bamboo.bridgebuilder.map.Layer;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapSprite;
import com.bamboo.bridgebuilder.map.SpriteLayer;
import com.bamboo.bridgebuilder.ui.GradientDialog;
import com.bamboo.bridgebuilder.ui.MinMaxDialog;
import com.bamboo.bridgebuilder.ui.propertyMenu.PropertyToolPane;

import static com.bamboo.bridgebuilder.BridgeBuilder.toolHeight;

/** Handles switching views of maps via tabs, adding and removing tabs.*/
public class ToolPane extends Group
{
    private Stack pane;
    private Table toolTable;
    private Image background;

    public Tool brush;
    public Tool drawPoint;
    public Tool drawRectangle;
    public Tool drawObject;
    public Tool objectVerticeSelect;
    public Tool boxSelect;
    public Tool select;
    public Tool grab;
    public Tool gradient;
    public Tool random;
    public Tool blocked;
    public Tool parallax;
    public Tool perspective;
    public Tool top;
    public Tool depth;
    public Tool lines;
    public Tool b2drender;
    public Tool selectedTool;
    private TextButton bringUp;
    private TextButton bringDown;
    private TextButton bringTop;
    private TextButton bringBottom;
    private TextButton layerDownOverride;
    private TextButton layerUpOverride;
    private TextButton layerOverrideReset;

    public GradientDialog gradientDialog;
    private TextButton gradientButton;

    public MinMaxDialog minMaxDialog;
    private TextButton minMaxButton;

    private BridgeBuilder editor;

    public Label fps;

    public ToolPane(BridgeBuilder editor, Skin skin)
    {
        this.editor = editor;
        this.toolTable = new Table();
        this.brush = new Tool(Tools.BRUSH, this, false);
        this.drawPoint = new Tool(Tools.DRAWPOINT, this, false);
        this.drawRectangle = new Tool(Tools.DRAWRECTANGLE, this, false);
        this.drawObject = new Tool(Tools.DRAWOBJECT, this, false);
        this.objectVerticeSelect = new Tool(Tools.OBJECTVERTICESELECT, this, false);
        this.boxSelect = new Tool(Tools.BOXSELECT, this, false);
        this.select = new Tool(Tools.SELECT, this, false);
        this.grab = new Tool(Tools.GRAB, this, false);
        this.gradient = new Tool(Tools.GRADIENT, this, false);
        this.random = new Tool(Tools.RANDOM, this, true);
        this.blocked = new Tool(Tools.BLOCKED, this, true);
        this.parallax = new Tool(Tools.PARALLAX, this, true);
        this.parallax.select();
        this.perspective = new Tool(Tools.PERSPECTIVE, this, true);
        this.top = new Tool(Tools.TOP, this, true);
        this.top.select();
        this.depth = new Tool(Tools.DEPTH, this, true);
        this.lines = new Tool(Tools.LINES, this, true);
        this.b2drender = new Tool(Tools.B2DR, this, true);
        this.bringUp = new TextButton("^", skin);
        this.bringDown = new TextButton("v", skin);
        this.bringTop = new TextButton("^^", skin);
        this.bringBottom = new TextButton("vv", skin);
        this.layerDownOverride = new TextButton("Layer Override v", skin);
        this.layerUpOverride = new TextButton("Layer Override ^", skin);
        this.layerOverrideReset= new TextButton("Layer Override Reset", skin);

        this.gradientDialog = new GradientDialog(editor.stage, skin);
        this.gradientButton = new TextButton("Gradient", skin);

        this.minMaxDialog = new MinMaxDialog(editor.stage, skin);
        this.minMaxButton = new TextButton("Min Max Settings", skin);

        this.fps = new Label("0", skin);

        setListeners();
        this.toolTable.left();
        this.toolTable.add(this.brush).padRight(1);
        this.toolTable.add(this.drawPoint).padRight(1);
        this.toolTable.add(this.drawRectangle).padRight(1);
        this.toolTable.add(this.drawObject).padRight(1);
        this.toolTable.add(this.objectVerticeSelect).padRight(1);
        this.toolTable.add(this.boxSelect).padRight(1);
        this.toolTable.add(this.select).padRight(1);
        this.toolTable.add(this.grab).padRight(1);
        this.toolTable.add(this.gradient).padRight(1);
        this.toolTable.add(this.random).padRight(1);
        this.toolTable.add(this.blocked).padRight(1);
        this.toolTable.add(this.parallax).padRight(1);
        this.toolTable.add(this.perspective).padRight(1);
        this.toolTable.add(this.top).padRight(1);
        this.toolTable.add(this.depth).padRight(1);
        this.toolTable.add(this.lines).padRight(1);
        this.toolTable.add(this.b2drender).padRight(5);
        this.toolTable.add(this.bringUp).padRight(1);
        this.toolTable.add(this.bringDown).padRight(1);
        this.toolTable.add(this.bringTop).padRight(1);
        this.toolTable.add(this.bringBottom).padRight(5);
        this.toolTable.add(this.layerDownOverride).padRight(1);
        this.toolTable.add(this.layerUpOverride).padRight(1);
        this.toolTable.add(this.layerOverrideReset).padRight(5);
        this.toolTable.add(this.gradientButton).padRight(5);
        this.toolTable.add(this.minMaxButton).padRight(5);
        this.toolTable.add(this.fps).padRight(1);

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
        this.toolTable.getCell(this.brush).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.drawPoint).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.drawRectangle).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.drawObject).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.objectVerticeSelect).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.boxSelect).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.select).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.grab).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.gradient).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.random).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.blocked).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.parallax).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.perspective).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.top).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.depth).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.lines).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.b2drender).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.bringUp).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.bringDown).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.bringTop).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.bringBottom).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.layerDownOverride).size(toolHeight * 4.75f, toolHeight);
        this.toolTable.getCell(this.layerUpOverride).size(toolHeight * 4.75f, toolHeight);
        this.toolTable.getCell(this.layerOverrideReset).size(toolHeight * 4.75f, toolHeight);
        this.toolTable.getCell(this.gradientButton).size(toolHeight * 2.75f, toolHeight);
        this.toolTable.getCell(this.minMaxButton).size(toolHeight * 4.75f, toolHeight);
        this.toolTable.getCell(this.fps).size(toolHeight, toolHeight);
        this.toolTable.invalidateHierarchy();

        this.pane.invalidateHierarchy();

        super.setSize(width, height);
    }

    public void selectTool(Tool selectedTool)
    {
        if(selectedTool.isToggleable)
        {
            if(selectedTool.selected)
            {
                if(selectedTool == this.depth && editor.activeMap != null)
                    PropertyToolPane.apply(editor.activeMap);
                selectedTool.unselect();
            }
            else
            {
                if(selectedTool == this.depth && editor.activeMap != null)
                    editor.activeMap.colorizeDepth();
                selectedTool.select();
            }

            if(selectedTool == this.perspective && editor.activeMap != null)
                PropertyToolPane.updatePerspective(editor.activeMap);
        }
        else
        {
            this.selectedTool = selectedTool;
            for (int i = 0; i < this.toolTable.getChildren().size; i++)
            {
                if(!(this.toolTable.getChildren().get(i) instanceof Tool))
                    continue;
                Tool tool = (Tool) this.toolTable.getChildren().get(i);
                if (tool == selectedTool)
                    tool.select();
                else if(!tool.isToggleable)
                    tool.unselect();
            }
        }
    }

    public Tool getTool()
    {
        return selectedTool;
    }

    private void setListeners()
    {
        this.gradientButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                gradientDialog.open();
            }
        });

        this.bringUp.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Map map = ((Map)editor.getScreen());
                if(map == null || map.selectedSprites.size != 1)
                    return;
                MapSprite selectedSprite = map.selectedSprites.first();
                MoveMapSpriteIndex moveMapSpriteIndex = new MoveMapSpriteIndex(map, selectedSprite, true, false);
                map.executeCommand(moveMapSpriteIndex);
            }
        });

        this.bringDown.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Map map = ((Map)editor.getScreen());
                if(map == null || map.selectedSprites.size != 1)
                    return;
                MapSprite selectedSprite = map.selectedSprites.first();
                MoveMapSpriteIndex moveMapSpriteIndex = new MoveMapSpriteIndex(map, selectedSprite, false, false);
                map.executeCommand(moveMapSpriteIndex);

            }
        });

        this.bringTop.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Map map = ((Map)editor.getScreen());
                if(map == null || map.selectedSprites.size != 1)
                    return;
                MapSprite selectedSprite = map.selectedSprites.first();
                MoveMapSpriteIndex moveMapSpriteIndex = new MoveMapSpriteIndex(map, selectedSprite, true, true);
                map.executeCommand(moveMapSpriteIndex);
            }
        });

        this.bringBottom.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Map map = ((Map)editor.getScreen());
                if(map == null || map.selectedSprites.size != 1)
                    return;
                MapSprite selectedSprite = map.selectedSprites.first();
                MoveMapSpriteIndex moveMapSpriteIndex = new MoveMapSpriteIndex(map, selectedSprite, false, true);
                map.executeCommand(moveMapSpriteIndex);
            }
        });

        this.layerDownOverride.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Map map = ((Map)editor.getScreen());
                if(map == null || map.selectedLayer == null)
                    return;
                if(map.selectedLayer.overrideSprite == null)
                {
                    int layerIndex = map.layers.indexOf(map.selectedLayer, true);
                    if(layerIndex == 0)
                        return;
                    for(int i = layerIndex - 1; i > 0; i--)
                    {
                        Layer layer = map.layers.get(i);
                        if(layer instanceof SpriteLayer)
                        {
                            SpriteLayer spriteLayer = (SpriteLayer) layer;
                            MapSprite mapSprite = spriteLayer.children.peek();
                            map.selectedLayer.overrideSprite = mapSprite;
                            mapSprite.layerOverride = map.selectedLayer;
                            return;
                        }
                    }
                }
                else
                {
                    int layerIndex = map.layers.indexOf(map.selectedLayer.overrideSprite.layer, true);
                    int spriteIndex = map.selectedLayer.overrideSprite.layer.children.indexOf(map.selectedLayer.overrideSprite, true);
                    spriteIndex --;
                    if(spriteIndex < 0)
                    {
                        while(layerIndex - 1 > 0)
                        {
                            layerIndex--;
                            if(map.layers.get(layerIndex) instanceof SpriteLayer)
                                break;
                        }
                        if(layerIndex < 0 || !(map.layers.get(layerIndex) instanceof SpriteLayer))
                            return;
                        spriteIndex = ((SpriteLayer) map.layers.get(layerIndex)).children.size - 1;
                    }
                    map.selectedLayer.overrideSprite.layerOverride = null;
                    map.selectedLayer.overrideSprite = (MapSprite) map.layers.get(layerIndex).children.get(spriteIndex);
                    map.selectedLayer.overrideSprite.layerOverride = map.selectedLayer;
                }
            }
        });

        this.layerUpOverride.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Map map = ((Map)editor.getScreen());
                if(map == null || map.selectedLayer == null)
                    return;
                if(map.selectedLayer.overrideSprite == null)
                {
                    int layerIndex = map.layers.indexOf(map.selectedLayer, true);
                    if(layerIndex == map.layers.size - 1)
                        return;
                    for(int i = layerIndex + 1; i < map.layers.size; i++)
                    {
                        Layer layer = map.layers.get(i);
                        if(layer instanceof SpriteLayer)
                        {
                            SpriteLayer spriteLayer = (SpriteLayer) layer;
                            MapSprite mapSprite = spriteLayer.children.peek();
                            map.selectedLayer.overrideSprite = mapSprite;
                            mapSprite.layerOverride = map.selectedLayer;
                            return;
                        }
                    }
                }
                else
                {
                    int layerIndex = map.layers.indexOf(map.selectedLayer.overrideSprite.layer, true);
                    int spriteIndex = map.selectedLayer.overrideSprite.layer.children.indexOf(map.selectedLayer.overrideSprite, true);
                    spriteIndex ++;
                    if(spriteIndex >= map.selectedLayer.overrideSprite.layer.children.size)
                    {
                        while(layerIndex + 1 < map.layers.size - 1)
                        {
                            layerIndex++;
                            if(map.layers.get(layerIndex) instanceof SpriteLayer)
                                break;
                        }
                        if(layerIndex >= map.layers.size || !(map.layers.get(layerIndex) instanceof SpriteLayer))
                            return;
                        spriteIndex = 0;
                    }
                    map.selectedLayer.overrideSprite.layerOverride = null;
                    map.selectedLayer.overrideSprite = (MapSprite) map.layers.get(layerIndex).children.get(spriteIndex);
                    map.selectedLayer.overrideSprite.layerOverride = map.selectedLayer;
                }
            }
        });
        this.layerOverrideReset.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Map map = ((Map)editor.getScreen());
                if(map == null || map.selectedLayer == null)
                    return;
                if(map.selectedLayer.overrideSprite != null)
                {
                    map.selectedLayer.overrideSprite.layerOverride = null;
                    map.selectedLayer.overrideSprite = null;
                }
            }
        });

        this.minMaxButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                minMaxDialog.setVisible(true);
            }
        });
    }
}
