package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.ui.layerMenu.LayerField;
import com.bamboo.bridgebuilder.ui.layerMenu.LayerTypes;

public class ObjectLayer extends Layer
{
    public Array<MapObject> children;

    public ObjectLayer(BridgeBuilder editor, Map map, LayerTypes type, LayerField layerField)
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

        setCameraZoomToSelectedLayer();
    }

}
