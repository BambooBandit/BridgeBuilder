package com.bamboo.bridgebuilder.ui.spriteMenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.BBColors;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.EditorAssets;
import com.bamboo.bridgebuilder.commands.CreateSpriteSheet;
import com.bamboo.bridgebuilder.commands.DeleteSpriteSheet;
import com.bamboo.bridgebuilder.map.Map;

import java.util.Iterator;

import static com.bamboo.bridgebuilder.BridgeBuilder.toolHeight;

public class SpriteMenu extends Group
{
    private BridgeBuilder editor;
    private Map map;

    public ScrollPane spriteScrollPane;
    private Stack stack;
    private Image background;
    public SpriteMenuToolPane toolPane;
    public Table spriteTable; // Holds all the sprites

    public Array<SpriteTool> selectedSpriteTools;

    public Array<SpriteSheet> spriteSheets;

    public SpriteMenu(Skin skin, BridgeBuilder editor, Map map)
    {
        this.editor = editor;
        this.map = map;

        this.selectedSpriteTools = new Array<>();

        this.spriteSheets = new Array<>();

        this.spriteTable = new Table();
        this.spriteTable.left().top();
        this.spriteScrollPane = new ScrollPane(this.spriteTable, EditorAssets.getUISkin());
        Iterator<EventListener> iterator = spriteScrollPane.getListeners().iterator();
        while (iterator.hasNext())
        {
            EventListener listener = iterator.next();
            if (listener instanceof InputListener)
                iterator.remove();
        }
        this.spriteScrollPane.addListener(new InputListener()
        {
            public boolean scrolled (InputEvent event, float x, float y, int amount)
            {
                Table table = (Table) spriteScrollPane.getWidget();
                for(int i = 0; i < table.getCells().size; i ++)
                {
                    Cell cell = table.getCells().get(i);
                    if(cell.getActor() == null)
                        continue;
                    if(cell.getActor() instanceof Table)
                        increaseTableSize(cell, (Table) cell.getActor(), amount);
                    else
                        cell.getActor().setZIndex(200);
                }
                return true;
            }
        });

        this.stack = new Stack();
        this.background = new Image(EditorAssets.getUIAtlas().createPatch("load-background"));
        this.toolPane = new SpriteMenuToolPane(this.editor, this, map, skin);

        this.stack.add(this.background);
        this.stack.add(this.spriteScrollPane);
        this.stack.setPosition(0, toolHeight);

        this.addActor(this.stack);
        this.addActor(this.toolPane);
    }

    public void increaseTableSize(Cell cell, Table table, int amount)
    {
        SpriteTool spriteTool = table.findActor("spriteTool");

        spriteTool.width = spriteTool.width * (1 - 1f / (amount * 3f));
        spriteTool.height = spriteTool.height * (1 - 1f / (amount * 3f));

        if(!table.isVisible())
            return;

        table.setSize(cell.getMaxWidth(), cell.getMaxHeight());
        spriteTool.image.setSize(spriteTool.width, spriteTool.height);
        spriteTool.setSize(spriteTool.width, spriteTool.height);

        cell.grow();

        table.invalidateHierarchy();
        table.pack();
    }

    public boolean createSpriteSheet(String name)
    {
        for(int i = 0; i < this.spriteSheets.size; i++)
        {
            if(this.spriteSheets.get(i).name.equals(name))
                return false;
        }
        CreateSpriteSheet createSpriteSheet = new CreateSpriteSheet(this.map, name);
        this.map.executeCommand(createSpriteSheet);
        return true;
    }

    public SpriteTool getSpriteTool(String spriteName, String spriteSheetName)
    {
        for(int i = 0; i < spriteTable.getChildren().size; i ++)
        {
            if (spriteTable.getChildren().get(i) instanceof Table)
            {
                Table cellTable = (Table) map.spriteMenu.spriteTable.getChildren().get(i);
                SpriteTool tool = cellTable.findActor("spriteTool");
                if(tool.name.equals(spriteName) && tool.sheet.name.equals(spriteSheetName))
                    return tool;
            }
        }
        return null;
    }

    public void removeSpriteSheet(String name)
    {

        for(int i = 0; i < this.spriteSheets.size; i ++)
        {
            SpriteSheet spriteSheet = this.spriteSheets.get(i);
            if (!spriteSheet.name.equals(name))
                continue;

            DeleteSpriteSheet deleteSpriteSheet = new DeleteSpriteSheet(map, spriteSheet);
            map.executeCommand(deleteSpriteSheet);
            return;
        }
    }

    @Override
    public void setSize(float width, float height)
    {
        this.stack.setSize(width, height - toolHeight);
        this.background.setBounds(0, 0, width, height - toolHeight);
        this.toolPane.setSize(width, toolHeight);

        this.stack.invalidateHierarchy();

        super.setSize(width, height);
    }

//    public SpriteTool getSpriteTool(SpriteMenuTools spriteMenuTools, String name, String sheetName)
//    {
//        if(spriteMenuTools == SpriteMenuTools.SPRITE)
//        {
//            for(int i = 0; i < spriteTable.getChildren().size; i ++)
//            {
//                if(spriteTable.getChildren().get(i) instanceof SpriteTool)
//                {
//                    SpriteTool spriteTool = (SpriteTool) spriteTable.getChildren().get(i);
//                    if (spriteTool.name.equals(name) && spriteTool.sheet.name.equals(sheetName))
//                        return spriteTool;
//                }
//            }
//        }
//        return null;
//    }

    public void reColorCheckers()
    {
        boolean checkerDark = false;
        boolean rowOdd = true;
        int spriteToolCount = 0;
        for(int i = 0; i < spriteTable.getChildren().size; i++)
        {
            if(spriteTable.getChildren().get(i) instanceof Table)
            {
                Table cellTable = (Table) spriteTable.getChildren().get(i);
                SpriteDrawable spriteDrawable = (SpriteDrawable) cellTable.getBackground();
                Sprite sprite = spriteDrawable.getSprite();
                if(!((SpriteTool)cellTable.findActor("spriteTool")).isSelected)
                {
                    if(toolPane.darkMode.isSelected)
                    {
                        if (checkerDark)
                            sprite.setColor(Color.BLACK);
                        else
                            sprite.setColor(BBColors.darkDarkGrey);
                    }
                    else
                    {
                        if (checkerDark)
                            sprite.setColor(Color.LIGHT_GRAY);
                        else
                            sprite.setColor(Color.WHITE);
                    }
                }

                checkerDark = !checkerDark;
                if((spriteToolCount + 1) % 5 == 0)
                {
                    rowOdd = !rowOdd;
                    checkerDark = !rowOdd;
                }
                spriteToolCount ++;
            }
        }
    }

    public boolean hasSpriteSheet(String name)
    {
        for(int i = 0; i < spriteSheets.size; i ++)
        {
            if(spriteSheets.get(i).name.equals(name))
                return true;
        }
        return false;
    }

    public void setSearchFilter(String searchFilter)
    {
        for(int i = 0; i < spriteTable.getChildren().size; i++)
        {
            if(spriteTable.getChildren().get(i) instanceof Table)
            {
                Table cellTable = (Table) spriteTable.getChildren().get(i);

                SpriteTool spriteTool = cellTable.findActor("spriteTool");
                if (searchFilter.length() == 0 || spriteTool.name.toLowerCase().contains(searchFilter.toLowerCase()))
                {
                    // unhide
                    cellTable.setVisible(true);

                    spriteTool.image.setSize(spriteTool.width, spriteTool.height);
                    spriteTool.setSize(spriteTool.width, spriteTool.height);

                    spriteTable.getCell(cellTable).grow();
                    cellTable.invalidateHierarchy();
                    cellTable.pack();
                }
                else
                {
                    // hide
                    cellTable.setVisible(false);

                    spriteTool.image.setSize(0, 0);
                    spriteTool.setSize(0, 0);

                    spriteTable.getCell(cellTable).grow();
                    cellTable.invalidateHierarchy();
                    cellTable.pack();
                }
            }
        }
    }
}
