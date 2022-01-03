package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.ui.layerMenu.LayerField;
import com.bamboo.bridgebuilder.ui.layerMenu.LayerTypes;

public class ObjectLayer extends Layer
{
    public Array<MapObject> children;

    public SpriteGrid spriteGrid;

    public ObjectLayer(BridgeBuilder editor, Map map, LayerField layerField)
    {
        super(editor, map, LayerTypes.OBJECT, layerField);
        this.children = super.children;
    }

    @Override
    public void draw()
    {
        setCameraZoomToThisLayer();

        for(int i = 0; i < this.children.size; i ++)
            this.children.get(i).draw();

        for(int i = 0; i < this.children.size; i ++)
            this.children.get(i).drawOutline();

        setCameraZoomToSelectedLayer();
    }

    public void addMapObject(MapObject mapObject)
    {
        this.children.add(mapObject);
    }

    public void createGrid()
    {
        if(this.spriteGrid == null)
            this.spriteGrid = new SpriteGrid(this);
    }

    public void removeGrid()
    {
        if(this.spriteGrid != null)
            this.spriteGrid.clear();
        this.spriteGrid = null;
    }

    public void updateSpriteGrid()
    {
        if(this.spriteGrid != null)
            this.spriteGrid.update();
    }

    @Override
    public void resize(int width, int height, boolean down, boolean right)
    {
        super.resize(width, height, down, right);
        if(this.spriteGrid != null)
            this.spriteGrid.resizeGrid();
    }
}
