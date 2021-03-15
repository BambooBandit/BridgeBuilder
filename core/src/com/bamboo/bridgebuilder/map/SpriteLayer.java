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
    public Perspective perspective;

    public SpriteLayer(BridgeBuilder editor, Map map, LayerField layerField)
    {
        super(editor, map, LayerTypes.SPRITE, layerField);
        this.children = super.children;
        this.perspective = new Perspective(map, this, map.camera, this.z, true);
    }

    @Override
    public void update()
    {
        perspective.update();

        for(int i = 0; i < this.children.size; i ++)
            this.children.get(i).update();
    }


    @Override
    public void draw()
    {
        setCameraZoomToThisLayer();

        if(Utils.getPropertyField(properties, "ground") == null)
        {
            this.editor.batch.setProjectionMatrix(perspective.camera.combined);
        }
        else
        {
            this.editor.batch.setProjectionMatrix(perspective.perspectiveCamera.combined);
        }

        for(int i = 0; i < this.children.size; i ++)
        {
            MapSprite mapSprite = this.children.get(i);
            if(mapSprite.attachedSprites != null)
            {
                for(int k = 0; k < mapSprite.attachedSprites.children.size; k ++)
                {
                    if(map.editor.fileMenu.toolPane.top.selected || mapSprite.attachedSprites.children.get(k) == mapSprite)
                        mapSprite.attachedSprites.children.get(k).draw();
                }
            }
            else
                mapSprite.draw();
        }

        if(map.selectedLayer == this && layerField.visibleImg.isVisible() && Utils.isFileToolThisType(editor, Tools.BRUSH) && this.map.getSpriteToolFromSelectedTools() != null)
        {
            for(int i = 0; i < this.map.getSpriteToolFromSelectedTools().previewSprites.size; i ++)
            {
                this.map.getSpriteToolFromSelectedTools().previewSprites.get(i).setAlpha(.25f);
                this.map.getSpriteToolFromSelectedTools().previewSprites.get(i).draw(editor.batch);
            }
        }

        this.map.camera.update();
        this.editor.batch.setProjectionMatrix(this.map.camera.combined);
        setCameraZoomToSelectedLayer();
    }

    public void addMapSprite(MapSprite mapSprite, int index)
    {
        if(index == -1)
            this.children.add(mapSprite);
        else
            this.children.insert(index, mapSprite);

        if(map.editAttachedMapSprite != null)
        {
            mapSprite.parentSprite = map.selectedSprites.first();
            map.selectedSprites.first().updateBounds();
        }

        if(this.editor.fileMenu.toolPane.spriteGridColors.selected)
            this.map.updateLayerSpriteGrids();
    }

    @Override
    public void setZ(float z)
    {
        super.setZ(z);
        this.perspective.cameraHeight = z;
    }
}
