package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.ui.layerMenu.LayerField;
import com.bamboo.bridgebuilder.ui.layerMenu.LayerTypes;
import com.bamboo.bridgebuilder.ui.manipulators.MoveBox;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;

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
    public MapSprite overrideSprite; // If null, render layer in the order its in. If not, this will always be drawn before that sprite.
    public MapSprite overrideSpriteBack; // If null, render layer in the order its in. If not, this will always be drawn before that sprite if its above the horizon in perspective.
    public byte floor;

    public Array<PropertyField> properties;

    public Perspective perspective;

    public Layer(BridgeBuilder editor, Map map, LayerTypes type, LayerField layerField)
    {
        this.perspective = new Perspective(map, this, map.camera, this.z, true);

        this.width = 5;
        this.height = 5;
        this.z = 0;
        this.children = new Array<>();
        this.editor = editor;
        this.map = map;
        this.type = type;
        this.layerField = layerField;

        this.properties = new Array<>();

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
            children.get(i).setPosition(children.get(i).getX() + xOffset, children.get(i).getY() + yOffset);
    }

    public void drawMoveBox()
    {
        if(map.selectedLayer == this)
            moveBox.sprite.draw(map.editor.batch);
    }

    public void update()
    {
        perspective.update();
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
                    children.get(i).setPosition(children.get(i).getX() + widthIncrease, children.get(i).getY());
            }
        }
        else // shrink horizontal
        {
            if(!right) // shrink left
            {
                float widthShrink = (oldWidth - width);
                for(int i = 0; i < children.size; i ++)
                    children.get(i).setPosition(children.get(i).getX() - widthShrink, children.get(i).getY());
            }
        }

        if(height > oldHeight) // grow vertical
        {
            if(down) // grow down
            {
                float heightIncrease = (height - oldHeight);
                for(int i = 0; i < children.size; i ++)
                    children.get(i).setPosition(children.get(i).getX(), children.get(i).getY() + heightIncrease);
            }
        }
        else // shrink vertical
        {
            if(down) // shrink down
            {
                float heightShrink = (oldHeight - height);
                for(int i = 0; i < children.size; i ++)
                    children.get(i).setPosition(children.get(i).getX(), children.get(i).getY() - heightShrink);
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
        if(1 == 1)
            return;
        if(editor.fileMenu.toolPane.parallax.selected && !editor.fileMenu.toolPane.spriteGridColors.selected)
        {
            this.map.camera.zoom = this.map.zoom;
            this.map.camera.update();
            this.editor.batch.setProjectionMatrix(map.camera.combined);
            this.editor.shapeRenderer.setProjectionMatrix(map.camera.combined);
        }
        else
        {
            this.map.camera.zoom = this.map.zoom;
            this.map.camera.update();
            this.editor.batch.setProjectionMatrix(map.camera.combined);
            this.editor.shapeRenderer.setProjectionMatrix(map.camera.combined);
        }
    }

    public void setCameraZoomToSelectedLayer()
    {
        if(this.map.selectedLayer == null)
            return;
        if(editor.fileMenu.toolPane.parallax.selected && !editor.fileMenu.toolPane.spriteGridColors.selected)
        {
            this.map.camera.zoom = this.map.zoom;
            this.map.camera.update();
            this.editor.batch.setProjectionMatrix(map.camera.combined);
            this.editor.shapeRenderer.setProjectionMatrix(map.camera.combined);
        }
    }
}
