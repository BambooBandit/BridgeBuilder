package com.bamboo.bridgebuilder.ui.propertyMenu;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.EditorAssets;
import com.bamboo.bridgebuilder.map.Layer;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapObject;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.LabelFieldPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteMenuTools;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

public class PropertyMenu extends Group
{
    private BridgeBuilder editor;

    public Map map;

    private Image background;

    public MapPropertyPanel mapPropertyPanel;
    public LayerPropertyPanel layerPropertyPanel;
    public RemoveablePropertyPanel spritePropertyPanel;
    public Label propertyTypeLabel;
    private PropertyPanel propertyPanel; // Custom properties
    private PropertyToolPane toolPane;

    public static int toolHeight = 35;

    private Stack stack;
    public Table propertyTable; // Holds all the properties

    private Skin skin;

    public PropertyMenu(Skin skin, BridgeBuilder editor, Map map)
    {
        this.editor = editor;
        this.map = map;
        this.skin = skin;

        this.stack = new Stack();
        this.background = new Image(EditorAssets.getUIAtlas().createPatch("load-background"));
        this.mapPropertyPanel = new MapPropertyPanel(skin, this, this.editor);
        this.layerPropertyPanel = new LayerPropertyPanel(skin, this, this.editor);
        this.layerPropertyPanel.setVisible(false);
        this.spritePropertyPanel = new RemoveablePropertyPanel(skin, this, this.editor);
        this.spritePropertyPanel.setVisible(false);
        this.propertyPanel = new PropertyPanel(skin, this, this.editor, map);
        this.toolPane = new PropertyToolPane(this.editor, map,this, skin);

        this.propertyTypeLabel = new Label("", skin);

        this.propertyTable = new Table();
        this.propertyTable.left().bottom();
        this.propertyTable.add(this.mapPropertyPanel).padBottom(5).row();
        this.propertyTable.add(this.layerPropertyPanel).padBottom(5).row();
        this.propertyTable.add(this.spritePropertyPanel).padBottom(5).row();
        this.propertyTable.add(this.propertyPanel);

        setSpriteProperties();

        this.stack.add(this.background);
        this.stack.add(this.propertyTable);
        this.stack.setPosition(0, toolHeight);

        this.addActor(this.stack);
        this.addActor(this.toolPane);
    }

    @Override
    public void setSize(float width, float height)
    {
        this.background.setBounds(0, 0, width, height - toolHeight);
        this.mapPropertyPanel.setSize(width, toolHeight);
        this.layerPropertyPanel.setSize(width, toolHeight);
        this.spritePropertyPanel.setSize(width, toolHeight);
        float propertyPanelStackHeight = mapPropertyPanel.getHeight();

        if(this.layerPropertyPanel.isVisible())
            propertyPanelStackHeight += this.layerPropertyPanel.getHeight();
        else
            this.layerPropertyPanel.setSize(width, 0);

        if(this.spritePropertyPanel.isVisible())
            propertyPanelStackHeight += this.spritePropertyPanel.getHeight();
        else
            this.spritePropertyPanel.setSize(width, 0);

        this.propertyPanel.setSize(width, height - toolHeight - 5 - 5 - 5 - propertyPanelStackHeight);
        this.propertyPanel.setPosition(0, toolHeight);
        this.propertyTable.invalidateHierarchy();
        this.toolPane.setSize(width, toolHeight);

        this.stack.setSize(width, height - toolHeight);
        this.stack.invalidateHierarchy();

        super.setSize(width, height);
    }

    public void newProperty(boolean light, Layer selectedLayer, Array<SpriteTool> selectedSpriteTools, Array<MapObject> selectedMapObject)
    {
        this.propertyPanel.newProperty(light, selectedLayer, selectedSpriteTools, selectedMapObject);
        rebuild();
    }

    public void newProperty(String property, String value, Layer selectedLayer, Array<SpriteTool> selectedSpriteTools, Array<MapObject> selectedMapObject)
    {
        this.propertyPanel.newProperty(property, value, selectedLayer, selectedSpriteTools, selectedMapObject);
        rebuild();
    }

    public void newProperty(float r, float g, float b, float a)
    {
//        this.propertyPanel.newProperty(r, g, b, a);
//        rebuild();
    }

    public void newProperty(float r, float g, float b, float a, float distance, int rayAmount)
    {
//        this.propertyPanel.newProperty(r, g, b, a, distance, rayAmount);
//        rebuild();
    }

    private void setSpriteProperties()
    {
        for(int i = 0; i < map.spriteMenu.spriteTable.getChildren().size; i ++)
        {
            if(map.spriteMenu.spriteTable.getChildren().get(i) instanceof Table)
            {
                SpriteTool spriteTool = ((Table) map.spriteMenu.spriteTable.getChildren().get(i)).findActor("spriteTool");
                LabelFieldPropertyValuePropertyField probability = new LabelFieldPropertyValuePropertyField("Probability", "1.0", skin, this, null,false);
                probability.value.setTextFieldFilter(new TextField.TextFieldFilter()
                {
                    @Override
                    public boolean acceptChar(TextField textField, char c)
                    {
                        return c == '.' || Character.isDigit(c);
                    }
                });

                spriteTool.lockedProperties.add(probability);
                LabelFieldPropertyValuePropertyField type = new LabelFieldPropertyValuePropertyField("Type", "", skin, this, null, false);
                spriteTool.lockedProperties.add(type);
                LabelFieldPropertyValuePropertyField z = new LabelFieldPropertyValuePropertyField("spawnZ", "", skin, this, null, false);
                spriteTool.lockedProperties.add(z);
            }
        }
    }

    public void removeProperty(String propertyName)
    {
        this.propertyPanel.removeProperty(propertyName);
        rebuild();
        this.propertyTable.invalidateHierarchy();
    }

    public void removeProperty(PropertyField propertyField)
    {
        this.propertyPanel.removeProperty(propertyField);
        rebuild();
        this.propertyTable.invalidateHierarchy();
    }

    /** Upon selecting a new sprite tool, rebuild property menu to only show the properties of that sprite tool.
     * If multiple sprite tools are selected, only show the common properties. A common property has the same property and value. */
    public void rebuild()
    {
        this.spritePropertyPanel.table.clearChildren();
        if(map.selectedLayer != null)
        {
            this.layerPropertyPanel.setVisible(true);
            this.layerPropertyPanel.layerWidthProperty.value.setText(Integer.toString(map.selectedLayer.width));
            this.layerPropertyPanel.layerHeightProperty.value.setText(Integer.toString(map.selectedLayer.height));
            this.layerPropertyPanel.layerZProperty.value.setText(Float.toString(map.selectedLayer.z));
        }
        else
            this.layerPropertyPanel.setVisible(false);
        if(map.spriteMenu.selectedSpriteTools.size == 1)
        {
            if(map.spriteMenu.selectedSpriteTools.first().tool == SpriteMenuTools.SPRITE)
            {
                Array<PropertyField> spriteProperties = map.spriteMenu.selectedSpriteTools.first().lockedProperties;
                for (int i = 0; i < spriteProperties.size; i++)
                    this.spritePropertyPanel.table.add(spriteProperties.get(i)).padBottom(1).row();
                this.spritePropertyPanel.setVisible(true);
            }
        }
        if(map.selectedSprites.size > 0)
        {
            Array<PropertyField> spriteProperties = map.selectedSprites.first().lockedProperties;
            for (int i = 0; i < spriteProperties.size; i++)
                this.spritePropertyPanel.table.add(spriteProperties.get(i)).padBottom(1).row();
            this.spritePropertyPanel.setVisible(true);
        }

        if(this.layerPropertyPanel.isVisible())
            this.layerPropertyPanel.setSize(getWidth(), toolHeight);
        else
            this.layerPropertyPanel.setSize(getWidth(), 0);
        if(this.spritePropertyPanel.isVisible())
            this.spritePropertyPanel.setSize(getWidth(), toolHeight);
        else
            this.spritePropertyPanel.setSize(getWidth(), 0);

        this.propertyPanel.rebuild();
        this.propertyTable.invalidateHierarchy();

        setSize(getWidth(), getHeight()); // refits everything
    }
}