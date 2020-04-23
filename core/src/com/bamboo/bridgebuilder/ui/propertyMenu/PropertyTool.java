package com.bamboo.bridgebuilder.ui.propertyMenu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bamboo.bridgebuilder.EditorAssets;

import static com.bamboo.bridgebuilder.BridgeBuilder.toolHeight;

public class PropertyTool extends Group
{
    private Image background;
    protected Image image;

    protected PropertyToolPane propertyToolPane;

    public PropertyTools tool;

    public PropertyTool(PropertyTools tool, final PropertyToolPane propertyToolPane, Skin skin)
    {
        this.tool = tool;
        this.propertyToolPane = propertyToolPane;
        this.background = new Image(EditorAssets.getUIAtlas().createPatch("textfield"));
        this.image = new Image(new Texture("ui/" + tool.name + ".png")); // TODO pack it in atlas

        this.background.setSize(toolHeight, toolHeight);
        this.image.setSize(toolHeight, toolHeight);

        addActor(background);
        addActor(image);

        addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if(tool == PropertyTools.NEW)
                    propertyToolPane.menu.newProperty(false);
                else
                    propertyToolPane.menu.newProperty(true);
            }
        });
    }
}