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
import com.bamboo.bridgebuilder.ui.SplatDialog;
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
    public Tool spriteGridColors;
    public Tool parallax;
    public Tool perspective;
    public Tool top;
    public Tool depth;
    public Tool lines;
    public Tool b2drender;
    public Tool attachedSprites;
    public Tool splat;
    public Tool fence;
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

    public SplatDialog splatDialog;
    private TextButton splatButton;

    public MinMaxDialog minMaxDialog;
    private TextButton minMaxButton;

    private BridgeBuilder editor;

    public Label fps;

    public ToolPane(BridgeBuilder editor, Skin skin)
    {
        this.editor = editor;
        this.toolTable = new Table();
        this.brush = new Tool(editor, this, false, Tools.BRUSH);
        this.drawPoint = new Tool(editor, this, false, Tools.DRAWPOINT);
        this.drawRectangle = new Tool(editor, this, false, Tools.DRAWRECTANGLE);
        this.drawObject = new Tool(editor, this, false, Tools.DRAWOBJECT);
        this.objectVerticeSelect = new Tool(editor, this, false, Tools.OBJECTVERTICESELECT);
        this.boxSelect = new Tool(editor, this, false, Tools.BOXSELECT);
        this.select = new Tool(editor, this, false, Tools.SELECT);
        this.grab = new Tool(editor, this, false, Tools.GRAB);
        this.gradient = new Tool(editor, this, false, Tools.GRADIENT);
        this.random = new Tool(editor, this, true, Tools.RANDOM);
        this.blocked = new Tool(editor, this, true, Tools.BLOCKED);
        this.spriteGridColors = new Tool(editor, this, true, Tools.SPRITEGRIDCOLORS);
        this.parallax = new Tool(editor, this, true, Tools.PARALLAX);
        this.parallax.select();
        this.perspective = new Tool(editor, this, true, Tools.PERSPECTIVE);
        this.top = new Tool(editor, this, true, Tools.TOP);
        this.top.select();
        this.depth = new Tool(editor, this, true, Tools.DEPTH);
        this.lines = new Tool(editor, this, true, Tools.LINES);
        this.b2drender = new Tool(editor, this, true, Tools.B2DR);
        this.attachedSprites = new Tool(editor, this, true, Tools.ATTACHEDSPRITES);
        this.splat = new Tool(editor, this, true, Tools.SPLAT);
        this.fence = new Tool(editor, this, true, Tools.FENCE);
        this.bringUp = new TextButton("^", skin);
        this.bringDown = new TextButton("v", skin);
        this.bringTop = new TextButton("^^", skin);
        this.bringBottom = new TextButton("vv", skin);
        this.layerDownOverride = new TextButton("Layer Override v", skin);
        this.layerUpOverride = new TextButton("Layer Override ^", skin);
        this.layerOverrideReset= new TextButton("Layer Override Reset", skin);

        this.gradientDialog = new GradientDialog(editor.stage, skin);
        this.gradientButton = new TextButton("Gradient", skin);

        this.splatDialog = new SplatDialog(editor.stage, skin);
        this.splatButton = new TextButton("Splat", skin);

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
        this.toolTable.add(this.spriteGridColors).padRight(1);
        this.toolTable.add(this.parallax).padRight(1);
        this.toolTable.add(this.perspective).padRight(1);
        this.toolTable.add(this.top).padRight(1);
        this.toolTable.add(this.depth).padRight(1);
        this.toolTable.add(this.lines).padRight(1);
        this.toolTable.add(this.b2drender).padRight(1);
        this.toolTable.add(this.attachedSprites).padRight(1);
        this.toolTable.add(this.splat).padRight(1);
        this.toolTable.add(this.fence).padRight(5);
        this.toolTable.add(this.bringUp).padRight(1);
        this.toolTable.add(this.bringDown).padRight(1);
        this.toolTable.add(this.bringTop).padRight(1);
        this.toolTable.add(this.bringBottom).padRight(5);
        this.toolTable.add(this.layerDownOverride).padRight(1);
        this.toolTable.add(this.layerUpOverride).padRight(1);
        this.toolTable.add(this.layerOverrideReset).padRight(5);
        this.toolTable.add(this.gradientButton).padRight(5);
        this.toolTable.add(this.splatButton).padRight(5);
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
        this.toolTable.getCell(this.spriteGridColors).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.parallax).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.perspective).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.top).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.depth).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.lines).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.b2drender).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.attachedSprites).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.splat).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.fence).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.bringUp).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.bringDown).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.bringTop).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.bringBottom).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.layerDownOverride).size(toolHeight * 4.75f, toolHeight);
        this.toolTable.getCell(this.layerUpOverride).size(toolHeight * 4.75f, toolHeight);
        this.toolTable.getCell(this.layerOverrideReset).size(toolHeight * 4.75f, toolHeight);
        this.toolTable.getCell(this.gradientButton).size(toolHeight * 2.75f, toolHeight);
        this.toolTable.getCell(this.splatButton).size(toolHeight * 2.75f, toolHeight);
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
                else if(selectedTool == this.attachedSprites)
                    this.editor.activeMap.editAttachedMapSprite.disableEditAttachedSpritesMode();
                else if(selectedTool == this.fence)
                    this.editor.activeMap.lastFencePlaced = null;
                selectedTool.unselect();
            }
            else
            {
                if(selectedTool == this.depth && this.editor.activeMap != null)
                    this.editor.activeMap.colorizeDepth();
                else if(selectedTool == this.spriteGridColors && this.editor.activeMap != null)
                    this.editor.activeMap.updateLayerSpriteGrids();
                if((selectedTool == this.spriteGridColors || selectedTool == this.blocked) && this.perspective.selected)
                    this.perspective.unselect();
                else if(selectedTool == this.perspective && (this.spriteGridColors.selected || this.blocked.selected))
                {
                    this.spriteGridColors.unselect();
                    this.blocked.unselect();
                }
                selectedTool.select();

                if(selectedTool == this.attachedSprites)
                {
                    if(this.editor.activeMap.selectedSprites.size == 1)
                        this.editor.activeMap.selectedSprites.first().enableEditAttachedSpritesMode();
                    else
                        selectedTool.unselect();
                }
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

        this.splatButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                splatDialog.open();
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
                            if(mapSprite.layerOverride != null)
                                continue;
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
                    MapSprite mapSprite;
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
                        int i = ((SpriteLayer) map.layers.get(layerIndex)).children.size - 1;
                        while(i >= 0)
                        {
                            mapSprite = (MapSprite) map.layers.get(layerIndex).children.get(i);
                            if(mapSprite.layerOverride != null)
                                i --;
                            else
                                break;
                        }
                        spriteIndex = i;
                    }
                    if(spriteIndex < 0)
                        return;
                    mapSprite = (MapSprite) map.layers.get(layerIndex).children.get(spriteIndex);
                    map.selectedLayer.overrideSprite.layerOverride = null;
                    map.selectedLayer.overrideSprite = mapSprite;
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
                            if(mapSprite.layerOverride != null)
                                continue;
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
                    MapSprite mapSprite;
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
                        int i = 0;
                        while(i < map.layers.get(layerIndex).children.size)
                        {
                            mapSprite = (MapSprite) map.layers.get(layerIndex).children.get(i);
                            if(mapSprite.layerOverride != null)
                                i ++;
                            else
                                break;
                        }
                        spriteIndex = i;
                    }

                    if(spriteIndex >= map.layers.get(layerIndex).children.size)
                        return;

                    mapSprite = (MapSprite) map.layers.get(layerIndex).children.get(spriteIndex);
                    map.selectedLayer.overrideSprite.layerOverride = null;
                    map.selectedLayer.overrideSprite = mapSprite;
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
