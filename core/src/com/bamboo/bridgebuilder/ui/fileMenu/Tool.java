package com.bamboo.bridgebuilder.ui.fileMenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bamboo.bridgebuilder.EditorAssets;

import static com.bamboo.bridgebuilder.BridgeBuilder.toolHeight;

public class Tool extends Group
{
    private Image background;
    private Image image;

    private ToolPane toolPane;

    public boolean selected;

    public Tools tool;

    public boolean isToggleable;

    public Tool(Tools tool, final ToolPane toolPane, Skin skin, boolean isToggleable)
    {
        this.isToggleable = isToggleable;
        this.tool = tool;
        this.background = new Image(EditorAssets.getUIAtlas().createPatch("textfield"));
        this.image = new Image(new Texture("ui/" + tool.name + ".png")); // TODO pack it in atlas

        this.background.setSize(toolHeight, toolHeight);
        this.image.setSize(toolHeight, toolHeight);

        addActor(background);
        addActor(image);

        final Tool selectedTool = this;
        addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                toolPane.selectTool(selectedTool);
            }
        });
    }

    public void select()
    {
        this.background.setColor(Color.GREEN);
        this.selected = true;
    }

    public void unselect()
    {
        this.background.setColor(Color.WHITE);
        this.selected = false;
    }
}
