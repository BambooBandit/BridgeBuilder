package com.bamboo.bridgebuilder.ui.layerMenu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bamboo.bridgebuilder.EditorAssets;

import static com.bamboo.bridgebuilder.BridgeBuilder.toolHeight;

public class LayerTool extends Group
{
    private Image background;
    protected Image image;

    protected LayerToolPane layerToolPane;

    public LayerTools tool;

    public LayerTool(LayerTools tool, final LayerToolPane layerToolPane, Skin skin)
    {
        this.tool = tool;
        this.layerToolPane = layerToolPane;
        this.background = new Image(EditorAssets.getUIAtlas().createPatch("textfield"));
        this.image = new Image(new Texture("ui/" + tool.name + ".png")); // TODO pack it in atlas

        this.background.setSize(toolHeight, toolHeight);
        this.image.setSize(toolHeight, toolHeight);

        addActor(background);
        addActor(image);

        final LayerTypes newLayerType = tool.type;

        addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                layerToolPane.menu.newLayer(newLayerType);
            }
        });
    }
}