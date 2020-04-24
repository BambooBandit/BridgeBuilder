package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.ui.fileMenu.Tools;
import com.bamboo.bridgebuilder.ui.layerMenu.LayerField;
import com.bamboo.bridgebuilder.ui.layerMenu.LayerTypes;

public class SpriteLayer extends Layer
{
    public Array<MapSprite> children;
    public SpriteLayer(BridgeBuilder editor, Map map, LayerTypes type, LayerField layerField)
    {
        super(editor, map, type, layerField);
        this.children = super.children;
    }

    @Override
    public void draw()
    {
        setCameraZoomToThisLayer();

        for(int i = 0; i < this.children.size; i ++)
            this.children.get(i).draw();

        if(map.selectedLayer == this && layerField.visibleImg.isVisible() && Utils.isFileToolThisType(editor, Tools.BRUSH) && this.map.getSpriteToolFromSelectedTools() != null)
        {
            for(int i = 0; i < this.map.getSpriteToolFromSelectedTools().previewSprites.size; i ++)
            {
                this.map.getSpriteToolFromSelectedTools().previewSprites.get(i).setAlpha(.25f);
                this.map.getSpriteToolFromSelectedTools().previewSprites.get(i).draw(editor.batch);
            }
        }

        setCameraZoomToSelectedLayer();
    }

    public void addMapSprite(MapSprite mapSprite)
    {
        this.children.add(mapSprite);
    }
}
