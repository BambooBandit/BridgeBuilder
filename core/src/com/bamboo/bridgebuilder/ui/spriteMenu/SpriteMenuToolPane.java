package com.bamboo.bridgebuilder.ui.spriteMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.EditorAssets;
import com.bamboo.bridgebuilder.map.Map;

import static com.bamboo.bridgebuilder.BridgeBuilder.toolHeight;

public class SpriteMenuToolPane extends Group
{
    private Stack pane;
    private Table toolTable;
    private Image background;
    private Skin skin;
    private BridgeBuilder editor;
    private Map map;

    private SpriteMenuTool sprites;
    private SpriteMenuTool lines;

    public SpriteMenu menu;

    public SpriteMenuToolPane(BridgeBuilder editor, SpriteMenu menu, Map map, Skin skin)
    {
        this.menu = menu;
        this.map = map;
        this.toolTable = new Table();
        this.sprites = new SpriteMenuTool(SpriteMenuTools.SPRITESELECT, this, skin);
        this.lines = new SpriteMenuTool(SpriteMenuTools.LINES, this, skin);
        this.toolTable.left();
        this.toolTable.add(this.sprites).padRight(1);
        this.toolTable.add(this.lines).padRight(1);

        this.sprites.select();
        selectMultipleTiles();

        this.editor = editor;
        this.skin = skin;
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
        this.toolTable.getCell(this.sprites).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.lines).size(toolHeight, toolHeight);
        this.toolTable.invalidateHierarchy();

        this.pane.invalidateHierarchy();

        super.setSize(width, height);
    }

    /** Tool was clicked on. If it's a tile, see if CONTROL was being held down to handle selecting or removing multiple tiles. */
    public void selectTool(SpriteMenuTool selectedTool)
    {
        if(selectedTool.tool == SpriteMenuTools.LINES)
        {
            if (selectedTool.isSelected)
                selectedTool.unselect();
            else
                selectedTool.select();
        }
        if(selectedTool.tool == SpriteMenuTools.SPRITESELECT)
        {
            this.sprites.select();
            selectMultipleTiles();
        }
        if(selectedTool.tool == SpriteMenuTools.SPRITE)
        {
            for(int i = 0; i < this.menu.spriteTable.getChildren().size; i ++)
            {
                if(this.menu.spriteTable.getChildren().get(i) instanceof SpriteTool)
                {
                    SpriteTool tool = (SpriteTool) this.menu.spriteTable.getChildren().get(i);
                    if (tool == selectedTool)
                    {
                        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                        {
                            if (tool.isSelected)
                            {
                                this.menu.selectedSpriteTools.removeValue(tool, false);
                                this.map.propertyMenu.rebuild();
                                tool.unselect();
                            } else
                            {
                                this.menu.selectedSpriteTools.add(tool);
                                this.map.propertyMenu.rebuild();
                                tool.select();
                            }
                        } else
                        {
                            this.menu.selectedSpriteTools.clear();
                            this.menu.selectedSpriteTools.add(tool);
                            this.map.propertyMenu.rebuild();
                            tool.select();
                        }
                    } else if (tool.tool == SpriteMenuTools.SPRITE)
                    {
                        if (!Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                        {
                            this.menu.selectedSpriteTools.removeValue(tool, false);
                            this.map.propertyMenu.rebuild();
                            tool.unselect();
                        }
                    }
                }
            }
        }
        this.menu.selectedSpriteTools.sort();
    }

    /** Used to select all the selected tiles/sprites when switching from tiles to sprites panels*/
    private void selectMultipleTiles()
    {
        this.menu.selectedSpriteTools.clear();
        if(this.sprites.isSelected)
        {
            for(int i = 0; i < this.menu.spriteTable.getChildren().size; i ++)
            {
                if(this.menu.spriteTable.getChildren().get(i) instanceof SpriteTool)
                {
                    if (((SpriteTool) this.menu.spriteTable.getChildren().get(i)).isSelected)
                        this.menu.selectedSpriteTools.add((SpriteTool) this.menu.spriteTable.getChildren().get(i));
                }
            }
        }
        if(this.map.propertyMenu != null)
            this.map.propertyMenu.rebuild();
    }

    /** Draws preview sprites to show how the tiles/sprites would look like if placed. */
    private void buildPreviewSprites()
    {

    }
}
