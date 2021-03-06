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
import com.bamboo.bridgebuilder.commands.SelectSpriteTool;
import com.bamboo.bridgebuilder.map.Layer;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapSprite;
import com.bamboo.bridgebuilder.map.SpriteLayer;
import com.bamboo.bridgebuilder.ui.fileMenu.YesNoDialog;

import static com.bamboo.bridgebuilder.BridgeBuilder.toolHeight;

public class SpriteMenuToolPane extends Group
{
    private Stack pane;
    private Table toolTable;
    private Image background;
    private Skin skin;
    private BridgeBuilder editor;
    private Map map;

    public SpriteMenuTool sprites;
    public SpriteMenuTool lines;
    public SpriteMenuTool darkMode;
    public SpriteMenuTool newSpritesheet;
    public SpriteMenuTool removeSpriteSheet;

    public NewSpriteSheetDialog newSpriteSheetDialog;

    public SpriteMenu menu;

    public SpriteMenuToolPane(BridgeBuilder editor, SpriteMenu menu, Map map, Skin skin)
    {
        this.menu = menu;
        this.map = map;
        this.toolTable = new Table();
        this.sprites = new SpriteMenuTool(SpriteMenuTools.SPRITESELECT, this, skin);
        this.lines = new SpriteMenuTool(SpriteMenuTools.LINES, this, skin);
        this.darkMode = new SpriteMenuTool(SpriteMenuTools.DARK_MODE, this, skin);
        this.newSpritesheet = new SpriteMenuTool(SpriteMenuTools.NEW_SPRITESHEET, this, skin);
        this.removeSpriteSheet = new SpriteMenuTool(SpriteMenuTools.REMOVE_SPRITESHEET, this, skin);
        this.toolTable.left();
        this.toolTable.add(this.sprites).padRight(1);
        this.toolTable.add(this.lines).padRight(1);
        this.toolTable.add(this.darkMode).padRight(1);
        this.toolTable.add(this.newSpritesheet).padRight(1);
        this.toolTable.add(this.removeSpriteSheet).padRight(1);

        this.sprites.select();

        this.editor = editor;
        this.skin = skin;
        this.pane = new Stack();

        this.background = new Image(EditorAssets.getUIAtlas().createPatch("load-background"));
        this.pane.add(this.background);
        this.pane.add(this.toolTable);

        this.addActor(this.pane);

        this.newSpriteSheetDialog = new NewSpriteSheetDialog(map, editor.stage, skin);
    }

    @Override
    public void setSize(float width, float height)
    {
        this.pane.setSize(width, height);
        this.background.setBounds(0, 0, width, height);

        // Resize all buttons in the pane
        this.sprites.setSize(toolHeight, toolHeight);
        this.lines.setSize(toolHeight, toolHeight);
        this.darkMode.setSize(toolHeight, toolHeight);
        this.newSpritesheet.setSize(toolHeight, toolHeight);
        this.removeSpriteSheet.setSize(toolHeight, toolHeight);

        this.toolTable.getCell(this.sprites).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.lines).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.darkMode).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.newSpritesheet).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.removeSpriteSheet).size(toolHeight, toolHeight);
        this.toolTable.invalidateHierarchy();

        this.pane.invalidateHierarchy();

        super.setSize(width, height);
    }

    /** Tool was clicked on. If it's a sprite, see if CONTROL was being held down to handle selecting or removing multiple sprites. */
    public void selectTool(SpriteMenuTool selectedTool)
    {
        if(selectedTool.tool == SpriteMenuTools.LINES)
        {
            if (selectedTool.isSelected)
                selectedTool.unselect();
            else
                selectedTool.select();
        }
        else if(selectedTool.tool == SpriteMenuTools.DARK_MODE)
        {
            if (selectedTool.isSelected)
                selectedTool.unselect();
            else
                selectedTool.select();
        }
        else if(selectedTool.tool == SpriteMenuTools.SPRITE)
        {
            if(this.removeSpriteSheet.isSelected)
            {
                int count = spriteSheetInstanceCount((SpriteTool) selectedTool);
                if(count > 0)
                {
                    new YesNoDialog("Remove sprite sheet and all " + count + " MapSprites belonging to it?", editor.stage, "", EditorAssets.getUISkin(), true)
                    {
                        @Override
                        public void yes()
                        {
                            map.spriteMenu.removeSpriteSheet(selectedTool.sheet.name);
                        }

                        @Override
                        public void no()
                        {
                        }
                    };
                }
                else
                    map.spriteMenu.removeSpriteSheet(selectedTool.sheet.name);
                return;
            }
            SelectSpriteTool selectSpriteTool = new SelectSpriteTool(map, (SpriteTool) selectedTool, Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT));
            map.executeCommand(selectSpriteTool);
        }
        else if(selectedTool.tool == SpriteMenuTools.NEW_SPRITESHEET)
        {
            this.newSpriteSheetDialog.open();
        }
        else if(selectedTool.tool == SpriteMenuTools.REMOVE_SPRITESHEET)
        {
            if (selectedTool.isSelected)
                selectedTool.unselect();
            else
                selectedTool.select();
        }
    }

    public int spriteSheetInstanceCount(SpriteTool spriteTool)
    {
        int count = 0;
        SpriteSheet spriteSheet = spriteTool.sheet;
        for(int i = 0; i < map.layers.size; i ++)
        {
            Layer layer = map.layers.get(i);
            if(layer instanceof SpriteLayer)
            {
                SpriteLayer spriteLayer = (SpriteLayer) layer;
                for(int k = 0; k < spriteLayer.children.size; k ++)
                {
                    MapSprite mapSprite = spriteLayer.children.get(k);
                    if(mapSprite.tool.sheet == spriteSheet)
                        count ++;
                }
            }
        }
        return count;
    }
}
