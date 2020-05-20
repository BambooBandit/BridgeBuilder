package com.bamboo.bridgebuilder.ui.spriteMenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.BBColors;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.EditorAssets;
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
                    if(table.getCells().get(i).getActor() == null)
                        continue;
                    if(table.getCells().get(i).getActor() instanceof Table)
                    {
                        table.getCells().get(i).getActor().setSize(table.getCells().get(i).getMaxWidth(), table.getCells().get(i).getMaxHeight());

                        SpriteTool spriteTool = ((Table) table.getCells().get(i).getActor()).findActor("spriteTool");
                        spriteTool.image.setSize(spriteTool.image.getWidth() - (spriteTool.image.getWidth() / (amount * 3f)), spriteTool.image.getHeight() - (spriteTool.image.getHeight() / (amount * 3f)));
                        spriteTool.setSize(spriteTool.image.getWidth(), spriteTool.image.getHeight());

                        table.getCells().get(i).grow();

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

        this.stack.add(this.background);
        this.stack.add(this.spriteScrollPane);
        this.stack.setPosition(0, toolHeight);

        this.addActor(this.stack);
        this.addActor(this.toolPane);
    }

    // TODO undo/redo
    public void createSpriteSheet(String name, Skin skin)
    {
        SpriteSheet spriteSheet = new SpriteSheet(name);
        this.spriteSheets.add(spriteSheet);

        EditorAssets.assets.load(name + ".atlas", TextureAtlas.class);
        EditorAssets.assets.finishLoading();
        EditorAssets.setMapAtlas(name, EditorAssets.getAssets().get(name + ".atlas"));

        // Add all the sprites to the spriteTable as Images
        spriteTable.padLeft(1);
        spriteTable.padTop(1);
        Label label = new Label(name, skin);
        spriteSheet.label = label;
        spriteTable.add(label).width(0).row();
        boolean checkerDark = false;
        boolean rowOdd = true;
        for(int i = 0; i < EditorAssets.getMapAtlas(name).getRegions().size; i ++)
        {
            TextureAtlas.AtlasRegion spriteRegion = EditorAssets.getMapAtlas(name).getRegions().get(i);

            SpriteTool spriteTool = new SpriteTool(SpriteMenuTools.SPRITE, spriteSheet, new Image(spriteRegion), spriteRegion, spriteRegion.name, 0, 0, toolPane, skin);
            spriteTool.setName("spriteTool");
            map.propertyMenu.setSpriteProperties(spriteTool);
            SpriteDrawable backgroundDrawable = new SpriteDrawable(new Sprite(new Texture("ui/whitePixel.png")));
            if(checkerDark)
                backgroundDrawable.getSprite().setColor(Color.WHITE);
            else
                backgroundDrawable.getSprite().setColor(Color.LIGHT_GRAY);
            Table cellTable = new Table(skin);
            cellTable.setName(spriteSheet.name);
            cellTable.setTouchable(Touchable.enabled);
            cellTable.addListener(new ClickListener()
            {
                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    toolPane.selectTool(spriteTool);
                }
            });
            cellTable.background(backgroundDrawable);
            cellTable.add(spriteTool).grow();
            spriteSheet.children.add(cellTable);

            float minimumArea = 300;
            float maximumArea = 1000;
            float newWidth = spriteTool.image.getWidth() / 25;
            float newHeight = spriteTool.image.getHeight() / 25;
            float multiplier = 1;
            if(newWidth * newHeight < minimumArea)
                multiplier = (float) Math.sqrt(minimumArea / (newWidth * newHeight));
            else if(newWidth * newHeight > maximumArea)
                multiplier = (float) Math.sqrt(maximumArea/ (newWidth * newHeight));
            newWidth *= multiplier;
            newHeight *= multiplier;
            spriteTool.image.setSize(newWidth, newHeight);
            spriteTool.setSize(newWidth, newHeight);
            checkerDark = !checkerDark;
            spriteTable.add(cellTable).center().grow();
            if((i + 1) % 5 == 0)
            {
                rowOdd = !rowOdd;
                checkerDark = !rowOdd;
                spriteTable.row();
            }
        }

        spriteTable.row();
        spriteTable.padBottom(500).row();
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
}
