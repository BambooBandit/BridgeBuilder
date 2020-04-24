package com.bamboo.bridgebuilder.ui.spriteMenu;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.EditorAssets;
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

    public SpriteMenu(Skin skin, BridgeBuilder editor, Map map)
    {
        this.editor = editor;
        this.map = map;

        this.selectedSpriteTools = new Array<>();

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
                    table.getCells().get(i).size(table.getCells().get(i).getMinWidth() - (table.getCells().get(i).getMinWidth() / (amount * 3f)), table.getCells().get(i).getMinHeight() - (table.getCells().get(i).getMinHeight() / (amount * 3f)));
                    if(table.getCells().get(i).getActor() instanceof SpriteTool)
                    {
                        SpriteTool spriteTool = (SpriteTool) table.getCells().get(i).getActor();
                        spriteTool.image.setSize(table.getCells().get(i).getMinWidth(), table.getCells().get(i).getMinHeight());
                        spriteTool.setSize(table.getCells().get(i).getMinWidth(), table.getCells().get(i).getMinHeight());
                        table.invalidateHierarchy();
                        table.pack();
                    }
                    else
                        table.getCells().get(i).getActor().setZIndex(200);
                }
                return true;
            }
        });

        this.stack = new Stack();
        this.background = new Image(EditorAssets.getUIAtlas().createPatch("load-background"));
        this.toolPane = new SpriteMenuToolPane(this.editor, this, map, skin);

        int id;
        id = createSpriteSheet(SheetTools.MAP, skin, 0);
        id = createSpriteSheet(SheetTools.FLATMAP, skin, id);
        id = createSpriteSheet(SheetTools.CANYONMAP, skin, id);
        id = createSpriteSheet(SheetTools.CANYONBACKDROP, skin, id);
        id = createSpriteSheet(SheetTools.MESAMAP, skin, id);

        this.stack.add(this.background);
        this.stack.add(this.spriteScrollPane);
        this.stack.setPosition(0, toolHeight);

        this.addActor(this.stack);
        this.addActor(this.toolPane);
    }

    private int createSpriteSheet(SheetTools sheetTool, Skin skin, int id)
    {
        // Add all the sprites to the spriteTable as Images
        spriteTable.padLeft(1);
        spriteTable.padTop(1);
        spriteTable.add(new Label(sheetTool.name, skin));
        spriteTable.row();
        for(int i = 0; i < EditorAssets.getGameAtlas(sheetTool.name).getRegions().size; i ++)
        {
            TextureAtlas.AtlasRegion spriteRegion = EditorAssets.getGameAtlas(sheetTool.name).getRegions().get(i);

            SpriteTool sprite = new SpriteTool(SpriteMenuTools.SPRITE, sheetTool, new Image(spriteRegion), spriteRegion, spriteRegion.name, id, 0, 0, toolPane, skin);
            float minSideLength = 100;
            float newWidth = sprite.image.getWidth() / 5;
            float newHeight = sprite.image.getHeight() / 5;

            if(newWidth < minSideLength)
            {
                float multiplier = minSideLength / newWidth;
                newWidth = minSideLength;
                newHeight *= multiplier;
            }
            if(newHeight < minSideLength)
            {
                float multiplier = minSideLength / newHeight;
                newHeight = minSideLength;
                newWidth *= multiplier;
            }
            sprite.image.setSize(newWidth, newHeight);
            sprite.setSize(newWidth, newHeight);
            id ++;
            spriteTable.add(sprite);
            if((i + 1) % 5 == 0)
                spriteTable.row();
        }
        spriteTable.row();
        spriteTable.padBottom(500).row();
        return id;
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

    public SpriteTool getSpriteTool(SpriteMenuTools spriteMenuTools, String name, String sheetName)
    {
        if(spriteMenuTools == SpriteMenuTools.SPRITE)
        {
            for(int i = 0; i < spriteTable.getChildren().size; i ++)
            {
                if(spriteTable.getChildren().get(i) instanceof SpriteTool)
                {
                    SpriteTool spriteTool = (SpriteTool) spriteTable.getChildren().get(i);
                    if (spriteTool.name.equals(name) && spriteTool.sheetTool.name.equals(sheetName))
                        return spriteTool;
                }
            }
        }
        return null;
    }
}
