package com.bamboo.bridgebuilder.ui.fileMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.EditorAssets;
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
    public Tool spriteGridColors;
    public Tool parallax;
    public Tool top;
    public Tool depth;
    public Tool lines;
    public Tool b2drender;
    public Tool attachedSprites;
    public Tool selectAttachedSprites;
    public Tool splat;
    public Tool fence;
    public Tool branch;
    public Tool stairs;
    public Tool selectedTool;
    public Tool filledPolygons;
    public Tool groupPolygons;
    public Tool paint;
    public Tool thin;
    public Tool path;
    public Tool staple;

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
        this.stairs = new Tool(editor, this, false, Tools.STAIRS);
        this.random = new Tool(editor, this, true, Tools.RANDOM);
        this.spriteGridColors = new Tool(editor, this, true, Tools.SPRITEGRIDCOLORS);
        this.parallax = new Tool(editor, this, true, Tools.PARALLAX);
        this.parallax.select();
        this.top = new Tool(editor, this, true, Tools.TOP);
        this.top.select();
        this.depth = new Tool(editor, this, true, Tools.DEPTH);
        this.lines = new Tool(editor, this, true, Tools.LINES);
        this.b2drender = new Tool(editor, this, true, Tools.B2DR);
        this.attachedSprites = new Tool(editor, this, true, Tools.ATTACHEDSPRITES);
        this.selectAttachedSprites = new Tool(editor, this, true, Tools.SELECTATTACHEDSPRITES);
        this.splat = new Tool(editor, this, true, Tools.SPLAT);
        this.fence = new Tool(editor, this, true, Tools.FENCE);
        this.branch = new Tool(editor, this, true, Tools.BRANCH);
        this.filledPolygons = new Tool(editor, this, true, Tools.FILLED);
        this.groupPolygons = new Tool(editor, this, true, Tools.GROUP);
        this.paint = new Tool(editor, this, false, Tools.PAINT);
        this.thin = new Tool(editor, this, false, Tools.THIN);
        this.path = new Tool(editor, this, false, Tools.PATH);
        this.staple = new Tool(editor, this, false, Tools.STAPLE);

        this.fps = new Label("0", skin);

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
        this.toolTable.add(this.stairs).padRight(1);
        this.toolTable.add(this.random).padRight(1);
        this.toolTable.add(this.spriteGridColors).padRight(1);
        this.toolTable.add(this.parallax).padRight(1);
        this.toolTable.add(this.top).padRight(1);
        this.toolTable.add(this.depth).padRight(1);
        this.toolTable.add(this.lines).padRight(1);
        this.toolTable.add(this.b2drender).padRight(1);
        this.toolTable.add(this.attachedSprites).padRight(1);
        this.toolTable.add(this.selectAttachedSprites).padRight(1);
        this.toolTable.add(this.splat).padRight(1);
        this.toolTable.add(this.fence).padRight(1);
        this.toolTable.add(this.branch).padRight(1);
        this.toolTable.add(this.filledPolygons).padRight(1);
        this.toolTable.add(this.groupPolygons).padRight(1);
        this.toolTable.add(this.paint).padRight(1);
        this.toolTable.add(this.thin).padRight(1);
        this.toolTable.add(this.path).padRight(1);
        this.toolTable.add(this.staple).padRight(4);
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
        this.brush.setSize(toolHeight, toolHeight);
        this.drawPoint.setSize(toolHeight, toolHeight);
        this.drawRectangle.setSize(toolHeight, toolHeight);
        this.drawObject.setSize(toolHeight, toolHeight);
        this.objectVerticeSelect.setSize(toolHeight, toolHeight);
        this.boxSelect.setSize(toolHeight, toolHeight);
        this.select.setSize(toolHeight, toolHeight);
        this.grab.setSize(toolHeight, toolHeight);
        this.gradient.setSize(toolHeight, toolHeight);
        this.stairs.setSize(toolHeight, toolHeight);
        this.random.setSize(toolHeight, toolHeight);
        this.spriteGridColors.setSize(toolHeight, toolHeight);
        this.parallax.setSize(toolHeight, toolHeight);
        this.top.setSize(toolHeight, toolHeight);
        this.depth.setSize(toolHeight, toolHeight);
        this.lines.setSize(toolHeight, toolHeight);
        this.b2drender.setSize(toolHeight, toolHeight);
        this.attachedSprites.setSize(toolHeight, toolHeight);
        this.selectAttachedSprites.setSize(toolHeight, toolHeight);
        this.splat.setSize(toolHeight, toolHeight);
        this.fence.setSize(toolHeight, toolHeight);
        this.branch.setSize(toolHeight, toolHeight);
        this.filledPolygons.setSize(toolHeight, toolHeight);
        this.groupPolygons.setSize(toolHeight, toolHeight);
        this.paint.setSize(toolHeight, toolHeight);
        this.thin.setSize(toolHeight, toolHeight);
        this.path.setSize(toolHeight, toolHeight);
        this.staple.setSize(toolHeight, toolHeight);

        this.toolTable.getCell(this.brush).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.drawPoint).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.drawRectangle).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.drawObject).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.objectVerticeSelect).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.boxSelect).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.select).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.grab).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.gradient).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.stairs).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.random).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.spriteGridColors).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.parallax).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.top).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.depth).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.lines).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.b2drender).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.attachedSprites).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.selectAttachedSprites).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.splat).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.fence).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.branch).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.groupPolygons).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.paint).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.thin).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.path).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.staple).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.filledPolygons).size(toolHeight, toolHeight);
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
                else if(selectedTool == this.branch)
                    this.editor.activeMap.lastBranchPlaced = null;
                if(selectedTool == this.random && this.fence.selected)
                    return;
                if(selectedTool == this.fence && this.stairs.selected)
                    return;
                selectedTool.unselect();
            }
            else
            {
                if(selectedTool == this.depth && this.editor.activeMap != null)
                    this.editor.activeMap.colorizeDepth();
                else if(selectedTool == this.spriteGridColors && this.editor.activeMap != null)
                    this.editor.activeMap.updateLayerSpriteGrids();
                else if(selectedTool == this.fence)
                    this.random.select();
                selectedTool.select();
                if(selectedTool == this.fence || selectedTool == this.stairs)
                    editor.activeMap.shuffleRandomSpriteTool(false, -1);

                if(selectedTool == this.attachedSprites)
                {
                    if(this.editor.activeMap.selectedSprites.size == 1)
                        this.editor.activeMap.selectedSprites.first().enableEditAttachedSpritesMode();
                    else
                        selectedTool.unselect();
                }
            }
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
                {
                    if(tool == this.stairs)
                    {
                        this.fence.select();
                        this.random.select();
                        editor.activeMap.shuffleRandomSpriteTool(false, -1);
                    }
                    tool.select();
                }
                else if(!tool.isToggleable)
                    tool.unselect();
            }
        }
    }

    public Tool getTool()
    {
        return selectedTool;
    }
}
