package com.bamboo.bridgebuilder.ui.fileMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.EditorAssets;
import com.bamboo.bridgebuilder.commands.MoveMapSpriteIndex;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapSprite;
import com.bamboo.bridgebuilder.ui.*;
import com.bamboo.bridgebuilder.ui.spriteMenu.GroupDialog;

import static com.bamboo.bridgebuilder.BridgeBuilder.toolHeight;

/** Handles switching views of maps via tabs, adding and removing tabs.*/
public class ButtonPane extends Group
{
    private Stack pane;
    private Table toolTable;
    private Image background;

    private TextButton bringUp;
    private TextButton bringDown;
    private TextButton bringTop;
    private TextButton bringBottom;
    private TextButton sort;

    public GradientDialog gradientDialog;
    private TextButton gradientButton;

    public SplatDialog splatDialog;
    private TextButton splatButton;

    public MinMaxDialog minMaxDialog;
    private TextButton minMaxButton;

    public StairsDialog stairsDialog;
    private TextButton stairsButton;

    public BranchDialog branchDialog;
    private TextButton branchButton;

    public GroupDialog groupDialog;
    private TextButton groupButton;

    private TextButton mergeButton;

    public PaintDialog paintDialog;
    private TextButton paintButton;

    public ThinDialog thinDialog;
    private TextButton thinButton;

    public ShadeDialog shadeDialog;
    private TextButton shadeButton;

    public PathDialog pathDialog;
    private TextButton pathButton;

    private BridgeBuilder editor;

    public ButtonPane(BridgeBuilder editor, Skin skin)
    {
        this.editor = editor;
        this.toolTable = new Table();
        this.bringUp = new TextButton("^", skin);
        this.bringDown = new TextButton("v", skin);
        this.bringTop = new TextButton("^^", skin);
        this.bringBottom = new TextButton("vv", skin);
        this.sort = new TextButton("sort", skin);

        this.gradientDialog = new GradientDialog(editor.stage, skin);
        this.gradientButton = new TextButton("Gradient", skin);

        this.splatDialog = new SplatDialog(editor.stage, skin);
        this.splatButton = new TextButton("Splat", skin);

        this.minMaxDialog = new MinMaxDialog(editor, editor.stage, skin);
        this.minMaxButton = new TextButton("Min Max", skin);

        this.stairsDialog = new StairsDialog(editor.stage, skin);
        this.stairsButton = new TextButton("Stairs", skin);

        this.branchDialog = new BranchDialog(editor.stage, skin);
        this.branchButton = new TextButton("Branches", skin);

        this.groupDialog = new GroupDialog(editor.stage, skin, editor);
        this.groupButton = new TextButton("Group", skin);

        this.mergeButton = new TextButton("Merge", skin);

        this.paintDialog = new PaintDialog(editor.stage, skin);
        this.paintButton = new TextButton("Paint", skin);

        this.thinDialog = new ThinDialog(editor.stage, skin);
        this.thinButton = new TextButton("Thin", skin);

        this.shadeDialog = new ShadeDialog(editor.stage, skin, editor);
        this.shadeButton = new TextButton("Shade", skin);

        this.pathDialog = new PathDialog(editor.stage, skin);
        this.pathButton = new TextButton("Path", skin);

        setListeners();
        this.toolTable.left();
        this.toolTable.add(this.bringUp);
        this.toolTable.add(this.bringDown);
        this.toolTable.add(this.bringTop);
        this.toolTable.add(this.bringBottom).padRight(4);
        this.toolTable.add(this.sort).padRight(4);
        this.toolTable.add(this.gradientButton).padRight(4);
        this.toolTable.add(this.splatButton).padRight(4);
        this.toolTable.add(this.minMaxButton).padRight(4);
        this.toolTable.add(this.stairsButton).padRight(4);
        this.toolTable.add(this.branchButton).padRight(4);
        this.toolTable.add(this.groupButton).padRight(4);
        this.toolTable.add(this.mergeButton).padRight(4);
        this.toolTable.add(this.paintButton).padRight(4);
        this.toolTable.add(this.thinButton).padRight(4);
        this.toolTable.add(this.shadeButton).padRight(4);
        this.toolTable.add(this.pathButton).padRight(4);

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

        this.toolTable.getCell(this.bringUp).size(toolHeight, toolHeight * .75f);
        this.toolTable.getCell(this.bringDown).size(toolHeight, toolHeight * .75f);
        this.toolTable.getCell(this.bringTop).size(toolHeight, toolHeight * .75f);
        this.toolTable.getCell(this.bringBottom).size(toolHeight, toolHeight * .75f);
        this.toolTable.getCell(this.sort).size(toolHeight * 2.25f, toolHeight * .75f);
        this.toolTable.getCell(this.gradientButton).size(toolHeight * 2.25f, toolHeight * .75f);
        this.toolTable.getCell(this.splatButton).size(toolHeight * 2.25f, toolHeight * .75f);
        this.toolTable.getCell(this.minMaxButton).size(toolHeight * 2.25f, toolHeight * .75f);
        this.toolTable.getCell(this.stairsButton).size(toolHeight * 2.25f, toolHeight * .75f);
        this.toolTable.getCell(this.branchButton).size(toolHeight * 2.25f, toolHeight * .75f);
        this.toolTable.getCell(this.groupButton).size(toolHeight * 2.25f, toolHeight * .75f);
        this.toolTable.getCell(this.mergeButton).size(toolHeight * 2.25f, toolHeight * .75f);
        this.toolTable.getCell(this.paintButton).size(toolHeight * 2.25f, toolHeight * .75f);
        this.toolTable.getCell(this.thinButton).size(toolHeight * 2.25f, toolHeight * .75f);
        this.toolTable.getCell(this.shadeButton).size(toolHeight * 2.25f, toolHeight * .75f);
        this.toolTable.getCell(this.pathButton).size(toolHeight * 2.25f, toolHeight * .75f);
        this.toolTable.invalidateHierarchy();

        this.pane.invalidateHierarchy();

        super.setSize(width, height);
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

        this.sort.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                Map map = ((Map)editor.getScreen());
                if(map == null)
                    return;
                map.sort();
            }
        });

//        this.layerDownOverride.addListener(new ClickListener()
//        {
//            @Override
//            public void clicked(InputEvent event, float x, float y)
//            {
//                Map map = ((Map)editor.getScreen());
//                if(map == null || map.selectedLayer == null)
//                    return;
//                if(map.selectedLayer.overrideSprite == null)
//                {
//                    int layerIndex = map.layers.indexOf(map.selectedLayer, true);
//                    if(layerIndex == 0)
//                        return;
//                    for(int i = layerIndex - 1; i > 0; i--)
//                    {
//                        Layer layer = map.layers.get(i);
//                        if(layer instanceof SpriteLayer)
//                        {
//                            SpriteLayer spriteLayer = (SpriteLayer) layer;
//                            if(spriteLayer.children.size == 0)
//                                continue;
//                            MapSprite mapSprite = spriteLayer.children.peek();
//                            if(mapSprite.layerOverride != null)
//                                continue;
//                            map.selectedLayer.overrideSprite = mapSprite;
//                            mapSprite.layerOverride = map.selectedLayer;
//                            return;
//                        }
//                    }
//                }
//                else
//                {
//                    int layerIndex = map.layers.indexOf(map.selectedLayer.overrideSprite.layer, true);
//                    int spriteIndex = map.selectedLayer.overrideSprite.layer.children.indexOf(map.selectedLayer.overrideSprite, true);
//                    spriteIndex --;
//                    MapSprite mapSprite;
//                    if(spriteIndex < 0)
//                    {
//                        while(layerIndex - 1 > 0)
//                        {
//                            layerIndex--;
//                            if(map.layers.get(layerIndex) instanceof SpriteLayer)
//                                break;
//                        }
//                        if(layerIndex < 0 || !(map.layers.get(layerIndex) instanceof SpriteLayer))
//                            return;
//                        int i = ((SpriteLayer) map.layers.get(layerIndex)).children.size - 1;
//                        while(i >= 0)
//                        {
//                            mapSprite = (MapSprite) map.layers.get(layerIndex).children.get(i);
//                            if(mapSprite.layerOverride != null)
//                                i --;
//                            else
//                                break;
//                        }
//                        spriteIndex = i;
//                    }
//                    if(spriteIndex < 0)
//                        return;
//                    mapSprite = (MapSprite) map.layers.get(layerIndex).children.get(spriteIndex);
//                    map.selectedLayer.overrideSprite.layerOverride = null;
//                    map.selectedLayer.overrideSprite = mapSprite;
//                    map.selectedLayer.overrideSprite.layerOverride = map.selectedLayer;
//                }
//            }
//        });
//
//        this.layerUpOverride.addListener(new ClickListener()
//        {
//            @Override
//            public void clicked(InputEvent event, float x, float y)
//            {
//                Map map = ((Map)editor.getScreen());
//                if(map == null || map.selectedLayer == null)
//                    return;
//                if(map.selectedLayer.overrideSprite == null)
//                {
//                    int layerIndex = map.layers.indexOf(map.selectedLayer, true);
//                    if(layerIndex == map.layers.size - 1)
//                        return;
//                    for(int i = layerIndex + 1; i < map.layers.size; i++)
//                    {
//                        Layer layer = map.layers.get(i);
//                        if(layer instanceof SpriteLayer)
//                        {
//                            SpriteLayer spriteLayer = (SpriteLayer) layer;
//                            if(spriteLayer.children.size == 0)
//                                continue;
//                            MapSprite mapSprite = spriteLayer.children.peek();
//                            if(mapSprite.layerOverride != null)
//                                continue;
//                            map.selectedLayer.overrideSprite = mapSprite;
//                            mapSprite.layerOverride = map.selectedLayer;
//                            return;
//                        }
//                    }
//                }
//                else
//                {
//                    int layerIndex = map.layers.indexOf(map.selectedLayer.overrideSprite.layer, true);
//                    int spriteIndex = map.selectedLayer.overrideSprite.layer.children.indexOf(map.selectedLayer.overrideSprite, true);
//                    spriteIndex ++;
//                    MapSprite mapSprite;
//                    if(spriteIndex >= map.selectedLayer.overrideSprite.layer.children.size)
//                    {
//                        while(layerIndex + 1 < map.layers.size - 1)
//                        {
//                            layerIndex++;
//                            if(map.layers.get(layerIndex) instanceof SpriteLayer)
//                                break;
//                        }
//                        if(layerIndex >= map.layers.size || !(map.layers.get(layerIndex) instanceof SpriteLayer))
//                            return;
//                        int i = 0;
//                        while(i < map.layers.get(layerIndex).children.size)
//                        {
//                            mapSprite = (MapSprite) map.layers.get(layerIndex).children.get(i);
//                            if(mapSprite.layerOverride != null)
//                                i ++;
//                            else
//                                break;
//                        }
//                        spriteIndex = i;
//                    }
//
//                    if(spriteIndex >= map.layers.get(layerIndex).children.size)
//                        return;
//
//                    mapSprite = (MapSprite) map.layers.get(layerIndex).children.get(spriteIndex);
//                    map.selectedLayer.overrideSprite.layerOverride = null;
//                    map.selectedLayer.overrideSprite = mapSprite;
//                    map.selectedLayer.overrideSprite.layerOverride = map.selectedLayer;
//                }
//            }
//        });
//        this.layerOverrideReset.addListener(new ClickListener()
//        {
//            @Override
//            public void clicked(InputEvent event, float x, float y)
//            {
//                Map map = ((Map)editor.getScreen());
//                if(map == null || map.selectedLayer == null)
//                    return;
//                if(map.selectedLayer.overrideSprite != null)
//                {
//                    map.selectedLayer.overrideSprite.layerOverride = null;
//                    map.selectedLayer.overrideSprite = null;
//                }
//            }
//        });

        this.minMaxButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                minMaxDialog.setVisible(true);
            }
        });

        this.stairsButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                stairsDialog.setVisible(true);
            }
        });

        this.branchButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                branchDialog.setVisible(true);
            }
        });

        this.groupButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if(editor.activeMap != null)
                    groupDialog.open();
            }
        });

        this.mergeButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if(editor.activeMap != null)
                {
                    MergeDialog mergeDialog = new MergeDialog(editor.stage, editor.activeMap.skin, editor.activeMap);
                    mergeDialog.open();
//                    editor.activeMap.mergePolygons();
                }
            }
        });

        this.paintButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                paintDialog.open();
            }
        });

        this.thinButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                thinDialog.open();
            }
        });

        this.shadeButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                shadeDialog.open();
            }
        });

        this.pathButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                pathDialog.open();
            }
        });
    }
}
