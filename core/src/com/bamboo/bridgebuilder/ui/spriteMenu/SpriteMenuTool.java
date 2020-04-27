package com.bamboo.bridgebuilder.ui.spriteMenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bamboo.bridgebuilder.EditorAssets;

import static com.bamboo.bridgebuilder.BridgeBuilder.toolHeight;

public class SpriteMenuTool extends Group
{
    private Image background;
    protected Image image;

    protected SpriteMenuToolPane spriteMenuToolPane;

    public boolean isSelected;

    public SpriteMenuTools tool;
    public SheetTools sheetTool;

    /** For sprite menu tools */
    public SpriteMenuTool(SpriteMenuTools tool, final SpriteMenuToolPane spriteMenuToolPane, Skin skin)
    {
        this.tool = tool;
        this.spriteMenuToolPane = spriteMenuToolPane;
        this.background = new Image(EditorAssets.getUIAtlas().createPatch("textfield"));
        this.image = new Image(new Texture("ui/" + tool.name + ".png")); // TODO pack it in atlas

        this.background.setSize(toolHeight, toolHeight);
        this.image.setSize(toolHeight, toolHeight);

        addActor(background);
        addActor(image);

        final SpriteMenuTool selectedTool = this;
        addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                spriteMenuToolPane.selectTool(selectedTool);
            }
        });
    }

    /** For sprite tools */
    public SpriteMenuTool(SpriteMenuTools tool, SheetTools sheetTool, Image image, final SpriteMenuToolPane spriteMenuToolPane, Skin skin)
    {
        this.tool = tool;
        this.sheetTool = sheetTool;
        this.spriteMenuToolPane = spriteMenuToolPane;
        this.image = image;

        addActor(image);
        setSize(image.getWidth(), image.getHeight());
    }

    public void select()
    {
        if(this.background!= null)
            this.background.setColor(Color.GREEN);
        else
            this.image.setColor(Color.GREEN);

        this.isSelected = true;

        if(this.tool == SpriteMenuTools.LINES)
            spriteMenuToolPane.menu.spriteTable.setDebug(true);
        if(this.tool == SpriteMenuTools.DARK_MODE)
            spriteMenuToolPane.menu.reColorCheckers();
        if(this.tool == SpriteMenuTools.SPRITESELECT)
            spriteMenuToolPane.menu.spriteScrollPane.setVisible(true);
    }

    public void unselect()
    {
        if(this.background!= null)
            this.background.setColor(Color.WHITE);
        else
            this.image.setColor(Color.WHITE);

        this.isSelected = false;

        if(this.tool == SpriteMenuTools.LINES)
            spriteMenuToolPane.menu.spriteTable.setDebug(false);
        if(this.tool == SpriteMenuTools.DARK_MODE)
            spriteMenuToolPane.menu.reColorCheckers();
        if(this.tool == SpriteMenuTools.SPRITESELECT)
            spriteMenuToolPane.menu.spriteScrollPane.setVisible(false);
    }
}