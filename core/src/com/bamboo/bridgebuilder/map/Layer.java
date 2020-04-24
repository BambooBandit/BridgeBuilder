package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.ui.layerMenu.LayerField;
import com.bamboo.bridgebuilder.ui.layerMenu.LayerTypes;
import com.bamboo.bridgebuilder.ui.manipulators.MoveBox;

public abstract class Layer<T extends LayerChild>
{
    protected BridgeBuilder editor;
    public Map map;

    public Array<T> children;
    public int width;
    public int height;
    public float z;
    public float x, y;
    public MoveBox moveBox;
    public LayerField layerField;
    public LayerTypes type;
    public MapSprite overrideSprite; // If null, render layer in the order its in. If not, when pressing layer ^ override or something, set this to equal the top sprite of the sprite layer beneath this layer. This will always be drawn before that sprite. Keep going down to do it to even lower layers.
    public byte floor;

    public Layer(BridgeBuilder editor, Map map, LayerTypes type, LayerField layerField)
    {
        this.width = 5;
        this.height = 5;
        this.z = 0;
        this.children = new Array<>();
        this.editor = editor;
        this.map = map;
        this.type = type;
        this.layerField = layerField;

        this.moveBox = new MoveBox();
        this.moveBox.setPosition(x + (this.width), y + (this.height));
    }

    public void setPosition(float x, float y)
    {
        float xOffset = x - this.x;
        float yOffset = y - this.y;
        this.x = x;
        this.y = y;
        this.moveBox.setPosition(x + (this.width), y + (this.height));
        for(int i = 0; i < children.size; i ++)
            children.get(i).setPosition(children.get(i).position.x + xOffset, children.get(i).position.y + yOffset);
    }

    public void drawMoveBox()
    {
        if(map.selectedLayer == this)
            moveBox.sprite.draw(map.editor.batch);
    }

    public abstract void draw();

    public void resize(int width, int height, boolean down, boolean right)
    {
        int oldWidth = this.width;
        int oldHeight = this.height;
        this.width = width;
        this.height = height;
        if(width > oldWidth) // grow horizontal
        {
            if(!right) // grow left
            {
                float widthIncrease = (width - oldWidth);
                for(int i = 0; i < children.size; i ++)
                    children.get(i).setPosition(children.get(i).position.x + widthIncrease, children.get(i).position.y);
            }
        }
        else // shrink horizontal
        {
            if(!right) // shrink left
            {
                float widthShrink = (oldWidth - width);
                for(int i = 0; i < children.size; i ++)
                    children.get(i).setPosition(children.get(i).position.x - widthShrink, children.get(i).position.y);
            }
        }

        if(height > oldHeight) // grow vertical
        {
            if(down) // grow down
            {
                float heightIncrease = (height - oldHeight);
                for(int i = 0; i < children.size; i ++)
                    children.get(i).setPosition(children.get(i).position.x, children.get(i).position.y + heightIncrease);
            }
        }
        else // shrink vertical
        {
            if(down) // shrink down
            {
                float heightShrink = (oldHeight - height);
                for(int i = 0; i < children.size; i ++)
                    children.get(i).setPosition(children.get(i).position.x, children.get(i).position.y - heightShrink);
            }
        }
        this.moveBox.setPosition(x + (this.width), y + (this.height));
    }

    public void setZ(float z)
    {
        this.z = z;
    }

    public void setCameraZoomToThisLayer()
    {
        if(editor.fileMenu.toolPane.parallax.selected)
        {
            this.map.camera.zoom = this.map.zoom - z;
            this.map.camera.update();
            this.editor.batch.setProjectionMatrix(map.camera.combined);
            this.editor.shapeRenderer.setProjectionMatrix(map.camera.combined);
        }
    }

    public void setCameraZoomToSelectedLayer()
    {
        if(this.map.selectedLayer == null)
            return;
        if(editor.fileMenu.toolPane.parallax.selected)
        {
            this.map.camera.zoom = this.map.zoom - this.map.selectedLayer.z;
            this.map.camera.update();
            this.editor.batch.setProjectionMatrix(map.camera.combined);
            this.editor.shapeRenderer.setProjectionMatrix(map.camera.combined);
        }
    }
}
