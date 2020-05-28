package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.ui.fileMenu.Tools;
import com.bamboo.bridgebuilder.ui.layerMenu.LayerField;
import com.bamboo.bridgebuilder.ui.layerMenu.LayerTypes;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.FieldFieldPropertyValuePropertyField;

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
        if(this.map.zoom < this.z)
            return;

        setCameraZoomToThisLayer();

        conditional:
        if(this.map.editor.fileMenu.toolPane.perspective.selected && Utils.doesLayerHavePerspective(this.map, this) && Utils.isLayerGround(this))
        {
            if(Gdx.graphics.getHeight() == 0)
                break conditional;
            float[] m = this.map.camera.combined.getValues();
            float skew = 0;
            float antiDepth = 0;
            try
            {
                FieldFieldPropertyValuePropertyField property = Utils.getSkewPerspectiveProperty(this.map, this);
                skew = Float.parseFloat(property.value.getText());
                property = Utils.getAntiDepthPerspectiveProperty(this.map, this);
                antiDepth = Float.parseFloat(property.value.getText());
            }
            catch (NumberFormatException e){}
            if(antiDepth >= .1f)
                skew /= antiDepth * 15;
            m[Matrix4.M31] += skew;
            m[Matrix4.M11] += this.map.camera.position.y / (-8f / skew) - ((.097f * antiDepth) / (antiDepth + .086f));
            this.map.camera.invProjectionView.set(this.map.camera.combined);
            Matrix4.inv(this.map.camera.invProjectionView.val);
            this.map.camera.frustum.update(this.map.camera.invProjectionView);
            this.editor.batch.setProjectionMatrix(this.map.camera.combined);
        }

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

        this.map.camera.update();
        this.editor.batch.setProjectionMatrix(this.map.camera.combined);
        setCameraZoomToSelectedLayer();
    }

    public void addMapSprite(MapSprite mapSprite)
    {
        this.children.add(mapSprite);
        if(mapSprite.attachedMapObjects != null)
            this.map.updateLayerGraphs();
    }
}
