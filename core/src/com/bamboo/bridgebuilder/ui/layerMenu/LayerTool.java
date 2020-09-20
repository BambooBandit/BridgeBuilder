package com.bamboo.bridgebuilder.ui.layerMenu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bamboo.bridgebuilder.EditorAssets;
import com.bamboo.bridgebuilder.commands.CreateLayer;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteMenuTool;

import static com.bamboo.bridgebuilder.BridgeBuilder.toolHeight;

public class LayerTool extends Group
{
    public Image background;
    public Image image;
    public Image imageTop;

    protected LayerToolPane layerToolPane;

    public boolean isSelected = false;

    public LayerTools tool;

    public LayerTool(LayerTools tool, final LayerToolPane layerToolPane, Skin skin, Map map)
    {
        this.tool = tool;
        this.layerToolPane = layerToolPane;
        this.background = new Image(EditorAssets.getUIAtlas().createPatch("textfield"));
        this.image = new Image(new Texture("ui/" + tool.name + ".png")); // TODO pack it in atlas


        this.background.setSize(toolHeight, toolHeight);
        this.image.setSize(toolHeight, toolHeight);

        addActor(background);
        addActor(image);
        if(tool.nameTop != null)
        {
            this.imageTop = new Image(new Texture("ui/" + tool.nameTop + ".png")); // TODO pack it in atlas
            this.imageTop.setSize(toolHeight, toolHeight);
            addActor(imageTop);
        }

        if(tool.type != null) {
            addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    CreateLayer createLayer = new CreateLayer(map, tool.type);
                    map.executeCommand(createLayer);
                }
            });
        }
        else
        {
            final LayerTool selectedTool = this;
            addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    layerToolPane.selectTool(selectedTool);
                }
            });
        }
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        this.image.setSize(width, height);
        this.background.setSize(width, height);
        if(this.imageTop != null)
            this.imageTop.setSize(width, height);
    }
}