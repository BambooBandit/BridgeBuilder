package com.bamboo.bridgebuilder.ui.propertyMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.EditorAssets;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.map.Layer;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapSprite;
import com.bamboo.bridgebuilder.map.SpriteLayer;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.ColorPropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.FieldFieldPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

import static com.bamboo.bridgebuilder.BridgeBuilder.toolHeight;

public class PropertyToolPane extends Group
{
    private Stack pane;
    private Table toolTable;
    private Image background;
    private Skin skin;
    private BridgeBuilder editor;

    private PropertyTool newProperty;
    private PropertyTool newLightProperty;
    private TextButton apply;

    public PropertyMenu menu;

    public PropertyToolPane(BridgeBuilder editor, Map map, PropertyMenu menu, Skin skin)
    {
        this.menu = menu;
        this.toolTable = new Table();
        this.newProperty = new PropertyTool(map, PropertyTools.NEW, this, skin);
        this.newLightProperty = new PropertyTool(map, PropertyTools.NEWLIGHT, this, skin);
        this.apply = new TextButton("Apply", skin);
        setApplyListener();
        this.toolTable.left();
        this.toolTable.add(this.newProperty).padRight(1);
        this.toolTable.add(this.newLightProperty).padRight(1);
        this.toolTable.add(this.apply);

        this.editor = editor;
        this.skin = skin;
        this.pane = new Stack();

        this.background = new Image(EditorAssets.getUIAtlas().createPatch("load-background"));
        this.pane.add(this.background);
        this.pane.add(this.toolTable);

        this.addActor(this.pane);
    }

    @Override
    public void setSize(float width, float height)
    {
        this.pane.setSize(width, height);
        this.background.setBounds(0, 0, width, height);

        // Resize all buttons in the pane
        this.toolTable.getCell(this.newProperty).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.newLightProperty).size(toolHeight, toolHeight);
        this.toolTable.getCell(this.apply).size(toolHeight * 2, toolHeight);
        this.toolTable.invalidateHierarchy();

        this.pane.invalidateHierarchy();

        super.setSize(width, height);
    }

    public void setApplyListener()
    {
        this.apply.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                apply(menu.map);
            }
        });
    }

    public static void apply(Map map)
    {
        // map sprites
        for(int i = 0; i < map.spriteMenu.spriteTable.getChildren().size; i ++)
        {
            if(map.spriteMenu.spriteTable.getChildren().get(i) instanceof Table)
            {
                SpriteTool spriteTool = ((Table) map.spriteMenu.spriteTable.getChildren().get(i)).findActor("spriteTool");
                // top
                PropertyField propertyField = Utils.getPropertyField(spriteTool.properties, "top");
                if(propertyField != null)
                {
                    FieldFieldPropertyValuePropertyField topProperty = (FieldFieldPropertyValuePropertyField) propertyField;
                    String topValue = topProperty.value.getText();
                    spriteTool.setTopSprites(topValue);
                }
                else if(spriteTool.topSprites != null)
                    spriteTool.setTopSprites("");
            }
        }

        // Set sprite color
        for(int i = 0; i < map.layers.size; i ++)
        {
            if(map.layers.get(i) instanceof SpriteLayer)
            {
                SpriteLayer spriteLayer = (SpriteLayer) map.layers.get(i);
                for(int k = 0; k < spriteLayer.children.size; k ++)
                {
                    MapSprite mapSprite = spriteLayer.children.get(k);
                    ColorPropertyField colorProperty = Utils.getLockedColorField(mapSprite.lockedProperties);
                    mapSprite.setColor(colorProperty.getR(), colorProperty.getG(), colorProperty.getB(), colorProperty.getA());
                }
            }
        }

        if(map.editor.fileMenu.toolPane.perspective.selected)
            updatePerspective(map);
    }

    public static void updatePerspective(Map map)
    {
        for(int i = 0; i < map.layers.size; i++)
        {
            Layer layer = map.layers.get(i);
            if(!(layer instanceof SpriteLayer))
                continue;
            SpriteLayer spriteLayer = (SpriteLayer) layer;
            for(int k = 0; k < spriteLayer.children.size; k ++)
            {
                MapSprite mapSprite = spriteLayer.children.get(k);
                mapSprite.updatePerspectiveScale();
            }
        }
    }
}
