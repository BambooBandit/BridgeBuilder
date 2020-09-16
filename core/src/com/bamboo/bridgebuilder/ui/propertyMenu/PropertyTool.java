package com.bamboo.bridgebuilder.ui.propertyMenu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.bamboo.bridgebuilder.EditorAssets;
import com.bamboo.bridgebuilder.map.Map;

import static com.bamboo.bridgebuilder.BridgeBuilder.toolHeight;

public class PropertyTool extends Group
{
    private Image background;
    protected Image image;

    public PropertyTools tool;

    public PropertyTool(Map map, PropertyTools tool, Skin skin)
    {
        this.tool = tool;
        this.background = new Image(EditorAssets.getUIAtlas().createPatch("textfield"));
        this.image = new Image(new Texture("ui/" + tool.name + ".png")); // TODO pack it in atlas

        this.background.setSize(toolHeight, toolHeight);
        this.image.setSize(toolHeight, toolHeight);

        addActor(background);
        addActor(image);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        this.image.setSize(width, height);
        this.background.setSize(width, height);
    }
}