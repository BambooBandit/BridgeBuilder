package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.EditorPolygon;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.commands.DisableAttachedSpriteEditMode;
import com.bamboo.bridgebuilder.commands.EnableAttachedSpriteEditMode;
import com.bamboo.bridgebuilder.ui.manipulators.MoveBox;
import com.bamboo.bridgebuilder.ui.manipulators.RotationBox;
import com.bamboo.bridgebuilder.ui.manipulators.ScaleBox;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.ColorPropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.LabelFieldPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.LabelLabelPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

public class MapSprite extends LayerChild implements Comparable<MapSprite>
{
    public float rotation, scale;
    public EditorPolygon polygon; // Used to show the sprite bounds even when scaled and rotated
    public RotationBox rotationBox;
    public MoveBox moveBox;
    public ScaleBox scaleBox;
    public Array<PropertyField> lockedProperties; // properties such as rotation. They belong to all sprites
    public Array<PropertyField> instanceSpecificProperties; // properties for just the mapsprite, not the spritetool
    public float z;
    public static int idCounter = 1;
    public int id; // Used to be able to set any sprites id and specifically retrieve it in the game
    public TextureAtlas.AtlasSprite sprite;
    public SpriteTool tool;
    public float width, height;

    public int layerOverrideIndex; // Only used to keep the reloading simple. When the MapSprite is made, store the index of the override layer here and use it later to set the bottom variable once all layers are created. 0 means no override. Index starts at 1.
    public Layer layerOverride; // If this is not null, before drawing this sprite, draw that whole layer. Used to organize different layer heights

    private float[] verts; // Used to pass to the sprite batch for skewable drawing

    public Array<MapObject> attachedMapObjects;
    public Array<AttachedMapObjectManager> attachedMapObjectManagers; // instance specific

    public SpriteLayer attachedSprites; // For when this map sprite has other map sprites attached to it. They all will act as one whole map sprite.
    public MapSprite parentSprite; // For the above

    // sprite connections
    public int edgeId;
    public MapSprite toEdgeSprite;
    public Array<MapSprite> fromEdgeSprites;
    public Array<LayerChild> fromFlickers;

    public float x1Offset = 0, y1Offset = 0, x2Offset = 0, y2Offset = 0, x3Offset = 0, y3Offset = 0, x4Offset = 0, y4Offset = 0;
    public MoveBox offsetMovebox1, offsetMovebox2, offsetMovebox3, offsetMovebox4;

    public Vector2 c1, c2;

    public MapSprite(Map map, Layer layer, SpriteTool tool, float x, float y)
    {
        super(map, layer, x, y);
        this.lockedProperties = new Array<>();
        this.instanceSpecificProperties = new Array<>();
        this.sprite = new TextureAtlas.AtlasSprite((TextureAtlas.AtlasRegion) tool.textureRegion);
        this.sprite.setSize(sprite.getAtlasRegion().originalWidth / 64f, sprite.getAtlasRegion().originalHeight / 64f);
        this.width = this.sprite.getWidth();
        this.height = this.sprite.getHeight();
        this.sprite.setOrigin(width / 2, height / 2);
        x -= this.sprite.getWidth() / 2;
        y -= this.sprite.getHeight() / 2;
        this.tool = tool;
        float[] vertices = {0, 0, this.width, 0, this.width, this.height, 0, this.height};
        this.polygon = new EditorPolygon(vertices);
        this.polygon.setPosition(x, y);
        this.polygon.setOrigin(this.sprite.getOriginX(), this.sprite.getOriginY());

        this.rotationBox = new RotationBox();
        this.rotationBox.setPosition(x + this.width, y + this.height / 2);
        this.moveBox = new MoveBox();
        this.moveBox.setPosition(x + this.width, y + this.height / 2);
        this.scaleBox = new ScaleBox();
        this.scaleBox.setPosition(x + this.width, y + this.height / 2);

        this.offsetMovebox1 = new MoveBox();
        this.offsetMovebox2 = new MoveBox();
        this.offsetMovebox3 = new MoveBox();
        this.offsetMovebox4 = new MoveBox();
        float[] spriteVertices = this.sprite.getVertices();
        offsetMovebox1.setPosition(spriteVertices[SpriteBatch.X2] + map.cameraX + x1Offset - (offsetMovebox1.scale * offsetMovebox1.width / 2f), spriteVertices[SpriteBatch.Y2] + map.cameraY + y1Offset - (offsetMovebox1.scale * offsetMovebox1.height / 2f));
        offsetMovebox2.setPosition(spriteVertices[SpriteBatch.X3] + map.cameraX + x2Offset - (offsetMovebox2.scale * offsetMovebox2.width / 2f), spriteVertices[SpriteBatch.Y3] + map.cameraY + y2Offset - (offsetMovebox2.scale * offsetMovebox2.height / 2f));
        offsetMovebox3.setPosition(spriteVertices[SpriteBatch.X4] + map.cameraX + x3Offset - (offsetMovebox3.scale * offsetMovebox3.width / 2f), spriteVertices[SpriteBatch.Y4] + map.cameraY + y3Offset - (offsetMovebox3.scale * offsetMovebox3.height / 2f));
        offsetMovebox4.setPosition(spriteVertices[SpriteBatch.X1] + map.cameraX + x4Offset - (offsetMovebox4.scale * offsetMovebox4.width / 2f), spriteVertices[SpriteBatch.Y1] + map.cameraY + y4Offset - (offsetMovebox4.scale * offsetMovebox4.height / 2f));

        this.verts = new float[20];
        this.scale = 1;

        TextField.TextFieldFilter valueFilter = new TextField.TextFieldFilter()
        {
            @Override
            public boolean acceptChar(TextField textField, char c)
            {
                return c == '.' || c == '-' || Character.isDigit(c);
            }
        };
        LabelLabelPropertyValuePropertyField idProperty = new LabelLabelPropertyValuePropertyField("ID", "0", map.skin, map.propertyMenu, null, false);
        LabelFieldPropertyValuePropertyField rotationProperty = new LabelFieldPropertyValuePropertyField("Rotation", "0", map.skin, map.propertyMenu, null, false);
        LabelFieldPropertyValuePropertyField scaleProperty = new LabelFieldPropertyValuePropertyField("Scale", "1", map.skin, map.propertyMenu, null, false);
        LabelFieldPropertyValuePropertyField zProperty = new LabelFieldPropertyValuePropertyField("Z", "0", map.skin, map.propertyMenu, null, false);
        LabelFieldPropertyValuePropertyField fenceProperty = new LabelFieldPropertyValuePropertyField("Fence", "false", map.skin, map.propertyMenu, null, false);
        LabelFieldPropertyValuePropertyField ignoreProperty = new LabelFieldPropertyValuePropertyField("IgnoreProps", "false", map.skin, map.propertyMenu, null, false);
        ColorPropertyField colorProperty = new ColorPropertyField(map.skin, map.propertyMenu, null, false, "Tint", 1, 1, 1, 1);

        this.lockedProperties.add(idProperty);
        this.lockedProperties.add(rotationProperty);
        this.lockedProperties.add(scaleProperty);
        this.lockedProperties.add(zProperty);
        this.lockedProperties.add(fenceProperty);
        this.lockedProperties.add(ignoreProperty);
        this.lockedProperties.add(colorProperty);

        rotationProperty.value.setTextFieldFilter(valueFilter);
        rotationProperty.value.getListeners().clear();
        TextField.TextFieldClickListener rotationListener = rotationProperty.value.new TextFieldClickListener(){
            @Override
            public boolean keyDown (InputEvent event, int keycode)
            {
                try
                {
                    if (keycode == Input.Keys.ENTER)
                    {
                        for(int i = 0; i < map.selectedSprites.size; i ++)
                            map.selectedSprites.get(i).setRotation(Float.parseFloat(rotationProperty.value.getText()));
                    }
                }
                catch (NumberFormatException e) { }
                return true;
            }
        };
        rotationProperty.value.addListener(rotationListener);

        scaleProperty.value.setTextFieldFilter(valueFilter);
        scaleProperty.value.getListeners().clear();
        TextField.TextFieldClickListener scaleListener = scaleProperty.value.new TextFieldClickListener(){
            @Override
            public boolean keyDown (InputEvent event, int keycode)
            {
                try
                {
                    if (keycode == Input.Keys.ENTER)
                    {
                        for(int i = 0; i < map.selectedSprites.size; i ++)
                            map.selectedSprites.get(i).setScale(Float.parseFloat(scaleProperty.value.getText()));
                    }
                }
                catch (NumberFormatException e) { }
                return true;
            }
        };
        scaleProperty.value.addListener(scaleListener);

        zProperty.value.setTextFieldFilter(valueFilter);
        zProperty.value.getListeners().clear();
        TextField.TextFieldClickListener zListener = zProperty.value.new TextFieldClickListener(){
            @Override
            public boolean keyDown (InputEvent event, int keycode)
            {
                try
                {
                    if (keycode == Input.Keys.ENTER)
                    {
                        for(int i = 0; i < map.selectedSprites.size; i ++)
                            map.selectedSprites.get(i).setZ(Float.parseFloat(zProperty.value.getText()));
                    }
                }
                catch (NumberFormatException e) { }
                return true;
            }
        };
        zProperty.value.addListener(zListener);

        if(tool.hasAttachedMapObjects())
        {
            for(int i = 0; i < tool.attachedMapObjectManagers.size; i ++)
            {
                AttachedMapObjectManager attachedMapObjectManager = tool.attachedMapObjectManagers.get(i);
                attachedMapObjectManager.addCopyOfMapObjectToThisMapSprite(this);
            }
        }
        float randomScale = this.map.editor.fileMenu.toolPane.minMaxDialog.randomSizeValue;
        float randomRotation = this.map.editor.fileMenu.toolPane.minMaxDialog.randomRotationValue;
        float randomR = this.map.editor.fileMenu.toolPane.minMaxDialog.randomRValue;
        float randomG = this.map.editor.fileMenu.toolPane.minMaxDialog.randomGValue;
        float randomB = this.map.editor.fileMenu.toolPane.minMaxDialog.randomBValue;
        float randomA = this.map.editor.fileMenu.toolPane.minMaxDialog.randomAValue;
        this.setScale(randomScale);
        this.setRotation(randomRotation);
        this.setColor(randomR, randomG, randomB, randomA);
        this.setPosition(x, y);
        this.updatePerspective();
    }

    public void setID(int id)
    {
        for(int i = 0; i < lockedProperties.size; i ++)
        {
            PropertyField propertyField = lockedProperties.get(i);
            if(propertyField instanceof LabelLabelPropertyValuePropertyField)
            {
                LabelLabelPropertyValuePropertyField labelLabelProperty = (LabelLabelPropertyValuePropertyField) propertyField;
                if(labelLabelProperty.getProperty().equals("ID"))
                {
                    labelLabelProperty.value.setText(Integer.toString(id));
                    break;
                }
            }
        }
        this.id = id;
    }


    @Override
    public float getX()
    {
        return this.x;
    }

    @Override
    public float getY()
    {
        return this.y;
    }

    public float getLowestY()
    {
        float[] vertices = sprite.getVertices();
        float lowestY = vertices[SpriteBatch.Y4] - y3Offset;
        float secondLowestY = lowestY + 1;

        offsetVerts[0] = vertices[SpriteBatch.Y2] - y1Offset;
        offsetVerts[1] = vertices[SpriteBatch.Y3] - y2Offset;
        offsetVerts[2] = vertices[SpriteBatch.Y4] - y3Offset;
        offsetVerts[3] = vertices[SpriteBatch.Y1] - y4Offset;
        int index = 0;
        for(int i = 0; i < offsetVerts.length; i ++)
        {
            float y = offsetVerts[i];
            if(y <= lowestY)
            {
                lowestY = y;
                index = i;
            }

        }
        for(int i = 0; i < offsetVerts.length; i ++)
        {
            float y = offsetVerts[i];
            if(y < secondLowestY && i != index)
                secondLowestY = y;
        }
        return (lowestY + secondLowestY) / 2f;
    }

    public float getLowestX()
    {
        float lowestX = 0;
        float[] vertices = sprite.getVertices();
        lowestX = vertices[SpriteBatch.X4];

        offsetVerts[0] = vertices[SpriteBatch.X2] - x1Offset;
        offsetVerts[1] = vertices[SpriteBatch.X3] - x2Offset;
        offsetVerts[2] = vertices[SpriteBatch.X4] - x3Offset;
        offsetVerts[3] = vertices[SpriteBatch.X1] - x4Offset;
        for (int i = 0; i < offsetVerts.length; i++)
        {
            float x = offsetVerts[i];
            if (x <= lowestX)
                lowestX = x;

        }
        return lowestX;
    }

    public float getHighestX()
    {
        float highestX = 0;
        float[] vertices = sprite.getVertices();
        highestX = vertices[SpriteBatch.X4];

        offsetVerts[0] = vertices[SpriteBatch.X2] - x1Offset;
        offsetVerts[1] = vertices[SpriteBatch.X3] - x2Offset;
        offsetVerts[2] = vertices[SpriteBatch.X4] - x3Offset;
        offsetVerts[3] = vertices[SpriteBatch.X1] - x4Offset;
        for (int i = 0; i < offsetVerts.length; i++)
        {
            float x = offsetVerts[i];
            if (x >= highestX)
                highestX = x;

        }
        return highestX;
    }

    private static Vector2 skewProject = new Vector2();
    /** Take a coordinate on the local sprite and return where it would be skewed. */
    public Vector2 skewOffset(float x, float y, float height)
    {
        x -= map.cameraX;
        y -= map.cameraY;
        if(!map.editor.fileMenu.toolPane.parallax.selected)
        {
            skewProject.set(0, 0);
            return skewProject;
        }

        Vector3 screenCenter = Utils.unproject(map.camera, Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        float perspectiveScaleX = this.perspectiveScale;
        float perspectiveScaleY = this.perspectiveScale / 2f;
        float xDis = x - screenCenter.x;
        float yDis = y - screenCenter.y;
        float xOffset = z * xDis * perspectiveScaleX;
        float yFactor = z * yDis;
        float yOffset = (yFactor * height * perspectiveScaleY) / 2f;
        xOffset *= height;

        skewProject.set(xOffset, yOffset);
        skewProject.set(0, 0);
        return skewProject;
    }

    @Override
    public void update()
    {
        if(Utils.getPropertyField(layer.properties, "ground") == null)
        {
            updatePerspectiveTall();
        }
        else
        {
            this.perspectiveOffsetX = map.cameraX;
            this.perspectiveOffsetY = map.cameraY;
            this.perspectiveScale = 1;
        }

        float spriteX;
        float spriteY;
        if(parentSprite == null)
        {
            spriteX = this.x - ((this.width * this.scale) - this.width) / 2f;
            spriteY = this.y - ((this.height * this.scale) - this.height) / 2f;
            if(Utils.getPropertyField(layer.properties, "ground") != null)
            {
                spriteX = this.x;
                spriteY = this.y;
            }
        }
        else
        {
            spriteX = this.parentSprite.x - ((this.parentSprite.width * this.parentSprite.scale) - this.parentSprite.width) / 2f;
            spriteY = this.parentSprite.y - ((this.parentSprite.height * this.parentSprite.scale) - this.parentSprite.height) / 2f;
            if(Utils.getPropertyField(layer.properties, "ground") != null)
            {
                spriteX = this.parentSprite.x;
                spriteY = this.parentSprite.y;
            }
        }
        sprite.setPosition(spriteX - this.perspectiveOffsetX, spriteY - this.perspectiveOffsetY);
        sprite.setOriginCenter();
        sprite.setOrigin(width / 2f, sprite.getOriginY());
        sprite.setScale(this.scale * this.perspectiveScale);

        if(attachedSprites != null)
        {
            for(int i = 0; i < attachedSprites.children.size; i ++)
            {
                MapSprite child = attachedSprites.children.get(i);
                if(child == this)
                    continue;
                child.update();
            }
        }
    }

    public static float[] offsetVerts = new float[4];
    @Override
    public void draw()
    {
        if(layerOverride != null && layerOverride.layerField.visibleImg.isVisible())
            layerOverride.draw();

        if(map.editAttachedMapSprite != null && !selected && (attachedSprites == null || map.selectedLayer != attachedSprites) && (parentSprite == null || map.selectedLayer != parentSprite.attachedSprites))
            sprite.setAlpha(sprite.getColor().a / 3.5f);

        float spriteX;
        float spriteY;
        if(parentSprite == null)
        {
            spriteX = this.x - ((this.width * this.scale) - this.width) / 2f;
            spriteY = this.y - ((this.height * this.scale) - this.height) / 2f;
            if(Utils.getPropertyField(layer.properties, "ground") != null)
            {
                spriteX = this.x;
                spriteY = this.y;
            }
        }
        else
        {
            spriteX = this.parentSprite.x - ((this.parentSprite.width * this.parentSprite.scale) - this.parentSprite.width) / 2f;
            spriteY = this.parentSprite.y - ((this.parentSprite.height * this.parentSprite.scale) - this.parentSprite.height) / 2f;
            if(Utils.getPropertyField(layer.properties, "ground") != null)
            {
                spriteX = this.parentSprite.x;
                spriteY = this.parentSprite.y;
            }
        }

        sprite.setPosition(spriteX - this.perspectiveOffsetX, spriteY - this.perspectiveOffsetY);

        if(layer.perspective.skew > .005f && !Utils.isLayerGround(layer) && (sprite.getBoundingRectangle().y) < -150)
            return;
        sprite.setOriginCenter();
        sprite.setOrigin(width / 2f, sprite.getOriginY());
        sprite.setScale(this.scale * this.perspectiveScale);

        float u = sprite.getU();
        float v = sprite.getV();
        float u2 = sprite.getU2();
        float v2 = sprite.getV2();
        float[] vertices = sprite.getVertices();
        float colorFloatBits = sprite.getColor().toFloatBits();

        if(layerOverride != null && layerOverride.layerField.isSelected)
            colorFloatBits = Color.BLUE.toFloatBits();

        float lowestY = getLowestY();

        if(parentSprite == null)
        {
            Vector2 offset;
            offset = skewOffset(getX() + (width / 2f), getY(), ((vertices[SpriteBatch.Y2] + y1Offset) - lowestY));
            verts[0] = vertices[SpriteBatch.X2] + x1Offset + offset.x;
            verts[1] = vertices[SpriteBatch.Y2] + y1Offset + offset.y;
            verts[2] = colorFloatBits;
            verts[3] = u;
            verts[4] = v;
            offset = skewOffset(getX() + (width / 2f), getY(), ((vertices[SpriteBatch.Y3] + y2Offset) - lowestY));
            verts[5] = vertices[SpriteBatch.X3] + x2Offset + offset.x;
            verts[6] = vertices[SpriteBatch.Y3] + y2Offset + offset.y;
            verts[7] = colorFloatBits;
            verts[8] = u2;
            verts[9] = v;

            offset = skewOffset(getX() + (width / 2f), getY(), ((vertices[SpriteBatch.Y4] + y3Offset) - lowestY));
            verts[10] = vertices[SpriteBatch.X4] + x3Offset + offset.x;
            verts[11] = vertices[SpriteBatch.Y4] + y3Offset + offset.y;
            verts[12] = colorFloatBits;
            verts[13] = u2;
            verts[14] = v2;
            offset = skewOffset(getX() + (width / 2f), getY(), ((vertices[SpriteBatch.Y4] + y4Offset) - lowestY));
            verts[15] = vertices[SpriteBatch.X1] + x4Offset + offset.x;
            verts[16] = vertices[SpriteBatch.Y1] + y4Offset + offset.y;
            verts[17] = colorFloatBits;
            verts[18] = u;
            verts[19] = v2;
        }
        else
        {
            float thisSpriteX = this.x - ((this.width * this.scale) - this.width) / 2f;
            float thisSpriteY = this.y - ((this.height * this.scale) - this.height) / 2f;
            float parentSpriteX = parentSprite.x - ((parentSprite.width * parentSprite.scale) - parentSprite.width) / 2f;
            float parentSpriteY = parentSprite.y - ((parentSprite.height * parentSprite.scale) - parentSprite.height) / 2f;
            float attachedOffsetX = ((thisSpriteX - parentSpriteX) * (this.parentSprite.perspectiveScale));
            float attachedOffsetY = ((thisSpriteY - parentSpriteY) * (this.parentSprite.perspectiveScale));
            lowestY = parentSprite.getLowestY();

            LabelFieldPropertyValuePropertyField fenceProperty = (LabelFieldPropertyValuePropertyField) Utils.getPropertyField(lockedProperties, "Fence");
            if(this.parentSprite.toEdgeSprite != null && fenceProperty.value.getText().equals("true"))
            {
                float oldX = sprite.getX();
                float oldY = sprite.getY();
                float oldScale = sprite.getScaleX();
                float oldEdgeX = this.parentSprite.toEdgeSprite.sprite.getX();
                float oldEdgeY = this.parentSprite.toEdgeSprite.sprite.getY();
                float edgeSpriteX = this.parentSprite.toEdgeSprite.x - ((this.parentSprite.toEdgeSprite.width * this.parentSprite.toEdgeSprite.scale) - this.parentSprite.toEdgeSprite.width) / 2f;
                float edgeSpriteY = this.parentSprite.toEdgeSprite.y - ((this.parentSprite.toEdgeSprite.height * this.parentSprite.toEdgeSprite.scale) - this.parentSprite.toEdgeSprite.height) / 2f;
                float oldEdgeScale = this.parentSprite.toEdgeSprite.sprite.getScaleX();
                float attachedOffsetNoPerspectiveX = (thisSpriteX - parentSpriteX);
                float attachedOffsetNoPerspectiveY = (thisSpriteY - parentSpriteY);
                sprite.setPosition(thisSpriteX - attachedOffsetNoPerspectiveX, thisSpriteY - attachedOffsetNoPerspectiveY);
                sprite.setScale(scale);
                this.parentSprite.toEdgeSprite.sprite.setPosition(edgeSpriteX, edgeSpriteY);
                this.parentSprite.toEdgeSprite.sprite.setScale(this.parentSprite.toEdgeSprite.scale);
                float[] edgeVertices = this.parentSprite.toEdgeSprite.sprite.getVertices();
                vertices = sprite.getVertices();
                float x1 = vertices[SpriteBatch.X1] - (vertices[SpriteBatch.X2] + (attachedOffsetNoPerspectiveX) + this.x1Offset);
                float y1 = vertices[SpriteBatch.Y1] - (vertices[SpriteBatch.Y2] + (attachedOffsetNoPerspectiveY) + this.y1Offset);
                float x2 = edgeVertices[SpriteBatch.X1] - (vertices[SpriteBatch.X3] + (attachedOffsetNoPerspectiveX) + this.x2Offset);
                float y2 = edgeVertices[SpriteBatch.Y1] - (vertices[SpriteBatch.Y3] + (attachedOffsetNoPerspectiveY) + this.y2Offset);
                float x3 = edgeVertices[SpriteBatch.X1] - (vertices[SpriteBatch.X4] + (attachedOffsetNoPerspectiveX) + this.x3Offset);
                float y3 = edgeVertices[SpriteBatch.Y1] - (vertices[SpriteBatch.Y4] + (attachedOffsetNoPerspectiveY) + this.y3Offset);
                float x4 = vertices[SpriteBatch.X1] - (vertices[SpriteBatch.X1] + (attachedOffsetNoPerspectiveX) + this.x4Offset);
                float y4 = vertices[SpriteBatch.Y1] - (vertices[SpriteBatch.Y1] + (attachedOffsetNoPerspectiveY) + this.y4Offset);
                sprite.setPosition(oldX, oldY);
                this.parentSprite.toEdgeSprite.sprite.setPosition(oldEdgeX, oldEdgeY);
                sprite.setScale(oldScale);
                this.parentSprite.toEdgeSprite.sprite.setScale(oldEdgeScale);
                vertices = sprite.getVertices();

                float x1Offset = x1 * perspectiveScale;
                float y1Offset = y1 * perspectiveScale;
                float x4Offset = x4 * perspectiveScale;
                float y4Offset = y4 * perspectiveScale;
//                Vector2 offset = Utils.skewOffset(parent.z, parent.x + (width * scale / 2f), parent.y, ((vertices[BBSpriteBatch.Y1] - y1Offset) - lowestY), perspectiveScale, map.camera);
                Vector2 offset;
                offset = parentSprite.skewOffset(parentSprite.getX() + (parentSprite.width / 2f), parentSprite.getLowestY(), (vertices[SpriteBatch.Y1] - y1Offset) - lowestY);
                verts[0] = vertices[SpriteBatch.X1] - x1Offset + offset.x;
                verts[1] = vertices[SpriteBatch.Y1] - y1Offset + offset.y;
                verts[2] = colorFloatBits;
                verts[3] = u;
                verts[4] = v;
//                offset = Utils.skewOffset(parent.z, parent.x + (width * scale / 2f), parent.y, ((vertices[BBSpriteBatch.Y1] - y4Offset) - lowestY), perspectiveScale, map.camera);
                offset = parentSprite.skewOffset(parentSprite.getX() + (parentSprite.width / 2f), parentSprite.getLowestY(), (vertices[SpriteBatch.Y1] - y4Offset) - lowestY);
                verts[15] = vertices[SpriteBatch.X1] - x4Offset + offset.x;
                verts[16] = vertices[SpriteBatch.Y1] - y4Offset + offset.y;
                verts[17] = colorFloatBits;
                verts[18] = u;
                verts[19] = v2;

                MapSprite parentSprite = this.parentSprite.toEdgeSprite;
                float blX = parentSprite.sprite.getVertices()[SpriteBatch.X1];
                float blY = parentSprite.sprite.getVertices()[SpriteBatch.Y1];

                lowestY = parentSprite.getLowestY();
                float x2Offset = x2 * parentSprite.perspectiveScale;
                float x3Offset = x3 * parentSprite.perspectiveScale;
                float y2Offset = y2 * parentSprite.perspectiveScale;
                float y3Offset = y3 * parentSprite.perspectiveScale;
//                offset = Utils.skewOffset(parent.z, parent.x + (width * scale / 2f), parent.y, ((blY - y2Offset) - lowestY), parent.perspectiveScale, map.camera);
                offset = parentSprite.skewOffset(parentSprite.getX() + (parentSprite.width / 2f), parentSprite.y, ((blY - y2Offset) - lowestY));
                verts[5] = blX - x2Offset + offset.x;
                verts[6] = blY - y2Offset + offset.y;
                verts[7] = colorFloatBits;
                verts[8] = u2;
                verts[9] = v;
//                offset = Utils.skewOffset(parent.z, parent.x + (width * scale / 2f), parent.y, ((blY - y3Offset) - lowestY), parent.perspectiveScale, map.camera);
                offset = parentSprite.skewOffset(parentSprite.getX() + (parentSprite.width / 2f), parentSprite.y, ((blY - y3Offset) - lowestY));
                verts[10] = blX - x3Offset + offset.x;
                verts[11] = blY - y3Offset + offset.y;
                verts[12] = colorFloatBits;
                verts[13] = u2;
                verts[14] = v2;
            }
            else
            {
                Vector2 offset;
                offset = parentSprite.skewOffset(parentSprite.getX() + (parentSprite.width / 2f), parentSprite.getLowestY(), (vertices[SpriteBatch.Y2] + y1Offset) - lowestY);
                verts[0] = vertices[SpriteBatch.X2] + x1Offset + offset.x + attachedOffsetX;
                verts[1] = vertices[SpriteBatch.Y2] + y1Offset + offset.y + attachedOffsetY;
                verts[2] = colorFloatBits;
                verts[3] = u;
                verts[4] = v;
                offset = parentSprite.skewOffset(parentSprite.getX() + (parentSprite.width / 2f), parentSprite.getLowestY(), (vertices[SpriteBatch.Y3] + y2Offset) - lowestY);
                verts[5] = vertices[SpriteBatch.X3] + x2Offset + offset.x + attachedOffsetX;
                verts[6] = vertices[SpriteBatch.Y3] + y2Offset + offset.y + attachedOffsetY;
                verts[7] = colorFloatBits;
                verts[8] = u2;
                verts[9] = v;
                offset = parentSprite.skewOffset(parentSprite.getX() + (parentSprite.width / 2f), parentSprite.getLowestY(), (vertices[SpriteBatch.Y4] + y3Offset) - lowestY);
                verts[10] = vertices[SpriteBatch.X4] + x3Offset + offset.x + attachedOffsetX;
                verts[11] = vertices[SpriteBatch.Y4] + y3Offset + offset.y + attachedOffsetY;
                verts[12] = colorFloatBits;
                verts[13] = u2;
                verts[14] = v2;
                lowestY = parentSprite.getLowestY();
                offset = parentSprite.skewOffset(parentSprite.getX() + (parentSprite.width / 2f), parentSprite.getLowestY(), (vertices[SpriteBatch.Y1] + y4Offset) - lowestY);
                verts[15] = vertices[SpriteBatch.X1] + x4Offset + offset.x + attachedOffsetX;
                verts[16] = vertices[SpriteBatch.Y1] + y4Offset + offset.y + attachedOffsetY;
                verts[17] = colorFloatBits;
                verts[18] = u;
                verts[19] = v2;
            }
        }

        map.editor.batch.draw(sprite.getTexture(), verts, 0, verts.length);

        if(map.editAttachedMapSprite != null && !selected && (attachedSprites == null || map.selectedLayer != attachedSprites) && (parentSprite == null || map.selectedLayer != parentSprite.attachedSprites))
            sprite.setAlpha(sprite.getColor().a * 3.5f);

        if(map.editor.fileMenu.toolPane.top.selected)
        {
            if (tool.topSprites != null)
            {
                for (int i = 0; i < tool.topSprites.size; i++)
                {
                    TextureAtlas.AtlasSprite topsprite = tool.topSprites.get(i);


                    spriteX = this.x - ((this.width * this.scale) - this.width) / 2f;
                    spriteY = this.y - ((this.height * this.scale) - this.height) / 2f;
                    if(Utils.getPropertyField(layer.properties, "ground") != null)
                    {
                        spriteX = this.x;
                        spriteY = this.y;
                    }
                    topsprite.setPosition(spriteX - perspectiveOffsetX, spriteY - perspectiveOffsetY);
                    topsprite.setRotation(sprite.getRotation());
                    topsprite.setOrigin(sprite.getOriginX(), sprite.getOriginY());
                    topsprite.setScale(this.scale * perspectiveScale);

                    if(map.editAttachedMapSprite != null && !selected && (attachedSprites == null || map.selectedLayer != attachedSprites) && (parentSprite == null || map.selectedLayer != parentSprite.attachedSprites))
                        topsprite.setAlpha(topsprite.getColor().a / 3.5f);

                    u = topsprite.getU();
                    v = topsprite.getV();
                    u2 = topsprite.getU2();
                    v2 = topsprite.getV2();
//                    if(parentSprite == null)
                    vertices = topsprite.getVertices();

                    float colorToFloatBits = sprite.getColor().toFloatBits();

//                    float width = topsprite.getRegionWidth() / 64f;
                    float height = topsprite.getRegionHeight() / 64f;

                    Vector2 offset;
                    offset = skewOffset(getX() + (width / 2f), getY(), (vertices[SpriteBatch.Y2] - lowestY));
                    verts[0] = vertices[SpriteBatch.X2] + x1Offset + offset.x;
                    verts[1] = vertices[SpriteBatch.Y2] + y1Offset + offset.y;
                    verts[2] = colorToFloatBits;
                    verts[3] = u;
                    verts[4] = v;
                    offset = skewOffset(getX() + (width / 2f), getY(), (vertices[SpriteBatch.Y3] - lowestY));
                    verts[5] = vertices[SpriteBatch.X3] + x2Offset + offset.x;
                    verts[6] = vertices[SpriteBatch.Y3] + y2Offset + offset.y;
                    verts[7] = colorToFloatBits;
                    verts[8] = u2;
                    verts[9] = v;

                    offset = skewOffset(getX() + (width / 2f), getY(), (vertices[SpriteBatch.Y4] - lowestY));
                    verts[10] = vertices[SpriteBatch.X4] + x3Offset + offset.x;
                    verts[11] = vertices[SpriteBatch.Y4] + y3Offset + offset.y;
                    verts[12] = colorToFloatBits;
                    verts[13] = u2;
                    verts[14] = v2;
                    offset = skewOffset(getX() + (width / 2f), getY(), (vertices[SpriteBatch.Y1] - lowestY));
                    verts[15] = vertices[SpriteBatch.X1] + x4Offset + offset.x;
                    verts[16] = vertices[SpriteBatch.Y1] + y4Offset + offset.y;
                    verts[17] = colorToFloatBits;
                    verts[18] = u;
                    verts[19] = v2;

                    map.editor.batch.draw(topsprite.getTexture(), verts, 0, verts.length);

                    if(map.editAttachedMapSprite != null && !selected && (attachedSprites == null || map.selectedLayer != attachedSprites) && (parentSprite == null || map.selectedLayer != parentSprite.attachedSprites))
                        topsprite.setAlpha(topsprite.getColor().a * 3.5f);
                }
            }
        }
    }

    @Override
    public void drawHoverOutline()
    {
        polygon.setPosition(x - map.cameraX, y - map.cameraY);
        map.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.ORANGE);
        map.editor.shapeRenderer.polygon(polygon.getTransformedVertices());
        polygon.setPosition(x, y);
    }

    @Override
    public void drawSelectedOutline()
    {
        polygon.setPosition(x - map.cameraX, y - map.cameraY);
        map.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.GREEN);
        map.editor.shapeRenderer.polygon(polygon.getTransformedVertices());
        polygon.setPosition(x, y);
    }

    @Override
    public void drawSelectedHoveredOutline()
    {
        polygon.setPosition(x - map.cameraX, y - map.cameraY);
        map.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.YELLOW);
        map.editor.shapeRenderer.polygon(polygon.getTransformedVertices());
        polygon.setPosition(x, y);
    }

    public void drawRotationBox()
    {
        if(selected && map.editor.fileMenu.toolPane.select.selected)
        {
            rotationBox.setScale(map.zoom);
            rotationBox.sprite.setPosition(rotationBox.x + (rotationBox.width * rotationBox.scale) - map.cameraX, rotationBox.y - map.cameraY);
            rotationBox.sprite.draw(map.editor.batch);
        }
    }
    public void drawMoveBox()
    {
        if(selected && map.editor.fileMenu.toolPane.select.selected)
        {
            moveBox.setScale(map.zoom);
            moveBox.sprite.setPosition(moveBox.x - map.cameraX, moveBox.y - map.cameraY);
            moveBox.sprite.draw(map.editor.batch);

            if(map.selectedSprites.size == 1)
            {
                offsetMovebox1.setScale(map.zoom);
                offsetMovebox2.setScale(map.zoom);
                offsetMovebox3.setScale(map.zoom);
                offsetMovebox4.setScale(map.zoom);

                offsetMovebox1.sprite.setPosition(offsetMovebox1.x - map.cameraX, offsetMovebox1.y - map.cameraY);
                offsetMovebox2.sprite.setPosition(offsetMovebox2.x - map.cameraX, offsetMovebox2.y - map.cameraY);
                offsetMovebox3.sprite.setPosition(offsetMovebox3.x - map.cameraX, offsetMovebox3.y - map.cameraY);
                offsetMovebox4.sprite.setPosition(offsetMovebox4.x - map.cameraX, offsetMovebox4.y - map.cameraY);

                offsetMovebox1.sprite.draw(map.editor.batch);
                offsetMovebox2.sprite.draw(map.editor.batch);
                offsetMovebox3.sprite.draw(map.editor.batch);
                offsetMovebox4.sprite.draw(map.editor.batch);
            }
        }
    }
    public void drawScaleBox()
    {
        if(selected && map.editor.fileMenu.toolPane.select.selected)
        {
            scaleBox.setScale(map.zoom);
            scaleBox.sprite.setPosition(scaleBox.x + (2f * scaleBox.width * scaleBox.scale) - map.cameraX, scaleBox.y - map.cameraY);
            scaleBox.sprite.draw(map.editor.batch);
        }
    }

    public void setZ(float z)
    {
        for(int i = 0; i < lockedProperties.size; i ++)
        {
            PropertyField propertyField = lockedProperties.get(i);
            if(propertyField instanceof LabelFieldPropertyValuePropertyField)
            {
                LabelFieldPropertyValuePropertyField labelFieldProperty = (LabelFieldPropertyValuePropertyField) propertyField;
                if(labelFieldProperty.getProperty().equals("Z"))
                {
                    labelFieldProperty.value.setText(Float.toString(z));
                    break;
                }
            }
        }
        this.z = z;
    }

    public void setColor(float r, float g, float b, float a)
    {
        this.sprite.setColor(r, g, b, a);
        ColorPropertyField colorProperty = Utils.getLockedColorField("Tint", this.lockedProperties);
        colorProperty.rValue.setText(Float.toString(r));
        colorProperty.gValue.setText(Float.toString(g));
        colorProperty.bValue.setText(Float.toString(b));
        colorProperty.aValue.setText(Float.toString(a));
    }

    public void setRotation(float degree)
    {
        float rotateAmount = degree - this.rotation;
        this.rotation = degree;
        Utils.spritePositionCopy.set(getX(), getY());
        Vector2 endPos = Utils.spritePositionCopy.sub(Utils.centerOrigin).rotate(rotateAmount).add(Utils.centerOrigin); // TODO don't assume this was set in case rotate is used somewhere else
        this.sprite.setRotation(degree);
        this.polygon.setRotation(degree);
        setPosition(endPos.x, endPos.y);
        if(this.tool.topSprites != null)
        {
            for(int i = 0; i < this.tool.topSprites.size; i++)
                this.tool.topSprites.get(i).setRotation(degree);
        }

        if(this.attachedSprites != null)
        {
            for(int i = 0; i < this.attachedSprites.children.size; i ++)
            {
                MapSprite mapSprite = this.attachedSprites.children.get(i);
                if(mapSprite == this)
                    continue;
                mapSprite.setRotation(degree);
            }
        }

        LabelFieldPropertyValuePropertyField labelFieldProperty = Utils.getLockedPropertyField(this.lockedProperties, "Rotation");
        labelFieldProperty.value.setText(Float.toString(this.rotation));

        if(this.attachedMapObjects != null)
        {
            for (int i = 0; i < this.attachedMapObjects.size; i++)
            {
                MapObject mapObject = this.attachedMapObjects.get(i);
                mapObject.setRotation(this.rotation);
            }
        }
    }

    @Override
    public float getRotation()
    {
        return this.rotation;
    }

    @Override
    public void setPosition(float x, float y)
    {
        float oldX = this.x;
        float oldY = this.y;
        this.x = x;
        this.y = y;
        float offsetDifferenceX = this.x - oldX;
        float offsetDifferenceY = this.y - oldY;

        float yScaleDisplacement = 0;
        float xScaleDisplacement = 0;
        float scale = this.scale;

        if(this.attachedSprites != null)
        {
            for(int i = 0; i < this.attachedSprites.children.size; i ++)
            {
                MapSprite mapSprite = this.attachedSprites.children.get(i);
                if(mapSprite == this)
                    continue;
                mapSprite.setPosition(mapSprite.getX() + offsetDifferenceX, mapSprite.getY() + offsetDifferenceY);
            }
        }

        this.polygon.setPosition(x + xScaleDisplacement, y + yScaleDisplacement);
//        this.sprite.setPosition(x + xScaleDisplacement, y + yScaleDisplacement);
        this.sprite.setScale(scale);

        x += this.width / 2;
        y += this.height / 2;
        this.rotationBox.setPosition(x, y);
        this.moveBox.setPosition(x, y);
        this.scaleBox.setPosition(x, y);

        sprite.setPosition(x - map.cameraX - width / 2f, y - map.cameraY - height / 2f);
        float[] spriteVertices = this.sprite.getVertices();
        offsetMovebox1.setPosition(spriteVertices[SpriteBatch.X2] + map.cameraX + x1Offset - (offsetMovebox1.scale * offsetMovebox1.width / 2f), spriteVertices[SpriteBatch.Y2] + map.cameraY + y1Offset - (offsetMovebox1.scale * offsetMovebox1.height / 2f));
        offsetMovebox2.setPosition(spriteVertices[SpriteBatch.X3] + map.cameraX + x2Offset - (offsetMovebox2.scale * offsetMovebox2.width / 2f), spriteVertices[SpriteBatch.Y3] + map.cameraY + y2Offset - (offsetMovebox2.scale * offsetMovebox2.height / 2f));
        offsetMovebox3.setPosition(spriteVertices[SpriteBatch.X4] + map.cameraX + x3Offset - (offsetMovebox3.scale * offsetMovebox3.width / 2f), spriteVertices[SpriteBatch.Y4] + map.cameraY + y3Offset - (offsetMovebox3.scale * offsetMovebox3.height / 2f));
        offsetMovebox4.setPosition(spriteVertices[SpriteBatch.X1] + map.cameraX + x4Offset - (offsetMovebox4.scale * offsetMovebox4.width / 2f), spriteVertices[SpriteBatch.Y1] + map.cameraY + y4Offset - (offsetMovebox4.scale * offsetMovebox4.height / 2f));

        if(this.tool.hasAttachedMapObjects())
        {
            for(int i = 0; i < this.attachedMapObjects.size; i ++)
            {
                MapObject mapObject = this.attachedMapObjects.get(i);
                mapObject.setPosition(mapObject.getX() + offsetDifferenceX, mapObject.getY() + offsetDifferenceY);
            }
        }
    }

    @Override
    public void setScale(float scale)
    {
        if(scale <= 0)
            return;
        this.scale = scale;
        setPosition(x, y);

        if(!Utils.isLayerGround(this.layer))
        {
            float perspectiveScale = this.scale * this.perspectiveScale;
            scale = perspectiveScale;
        }
        this.sprite.setScale(scale);
        this.polygon.setScale(scale, scale);
        if(this.tool.topSprites != null)
        {
            for(int i = 0; i < this.tool.topSprites.size; i ++)
                this.tool.topSprites.get(i).setScale(scale);
        }

        if(this.attachedSprites != null)
        {
            for(int i = 0; i < this.attachedSprites.children.size; i ++)
            {
                MapSprite mapSprite = this.attachedSprites.children.get(i);
                if(mapSprite == this)
                    continue;
                mapSprite.setScale(scale);
            }
        }

        if(this.tool.hasAttachedMapObjects())
        {
            for(int i = 0; i < this.attachedMapObjects.size; i ++)
            {
                MapObject mapObject = this.attachedMapObjects.get(i);
                mapObject.setScale(scale);
            }
        }

        LabelFieldPropertyValuePropertyField labelFieldProperty = Utils.getLockedPropertyField(this.lockedProperties, "Scale");
        labelFieldProperty.value.setText(Float.toString(this.scale));
    }

    @Override
    public float getScale()
    {
        return this.scale;
    }

    @Override
    public void select()
    {
        if(this.selected)
            return;
        this.map.selectedSprites.add(this);
        this.selected = true;
    }
    @Override
    public void unselect()
    {
        this.map.selectedSprites.removeValue(this, true);
        this.selected = false;
        if(this.tool.attachedMapObjectManagers == null || !this.tool.hasAttachedMapObjects())
            return;
        for(int i = 0; i < this.attachedMapObjects.size; i ++)
            this.attachedMapObjects.get(i).unselect();
    }

    public void addAttachedMapObject(MapObject mapObject)
    {
        if(this.attachedMapObjects == null)
            this.attachedMapObjects = new Array<>();
        this.attachedMapObjects.add(mapObject);
        this.attachedMapObjects.sort();
    }

    public void createAttachedMapObject(Map map, MapObject mapObject, boolean spriteTool)
    {
        if(spriteTool)
            this.tool.createAttachedMapObject(map, mapObject, this);
        else
        {
            if(this.attachedMapObjectManagers == null)
                this.attachedMapObjectManagers = new Array<>();
            this.attachedMapObjectManagers.add(new AttachedMapObjectManager(map, null, mapObject, this));
        }
    }

    public void createAttachedMapObject(Map map, MapObject mapObject, float offsetX, float offsetY, boolean spriteTool)
    {
        if(spriteTool)
            this.tool.createAttachedMapObject(map, mapObject, offsetX, offsetY);
        else
        {
            if (this.attachedMapObjectManagers == null)
                this.attachedMapObjectManagers = new Array<>();
            this.attachedMapObjectManagers.add(new AttachedMapObjectManager(map, null, mapObject, offsetX, offsetY));
            mapObject.attachedSprite = this;
            addAttachedMapObject(mapObject);
        }
    }

    public void removeAttachedMapObject(MapObject mapObject)
    {
        if(!this.tool.removeAttachedMapObject(mapObject))
        {
            if(this.attachedMapObjectManagers == null)
                return;
            for(int i = 0; i < this.attachedMapObjectManagers.size; i ++)
            {
                if (this.attachedMapObjectManagers.get(i).deleteAttachedMapObjectFromMapSprite(mapObject, this))
                {
                    this.attachedMapObjectManagers.removeIndex(i);
                    return;
                }
            }
        }
    }

    @Override
    public boolean isHoveredOver(float x, float y)
    {
        polygon.setPosition(this.x - map.cameraX, this.y - map.cameraY);
        boolean contains = this.polygon.contains(x, y);
        polygon.setPosition(this.x, this.y);
        return contains;
    }

    @Override
    public boolean isHoveredOver(float[] vertices)
    {
        return Intersector.overlapConvexPolygons(polygon.getTransformedVertices(), vertices, null);
    }

    public void updatePerspective()
    {
        this.setPosition(getX(), getY());
    }

    public void enableEditAttachedSpritesMode()
    {
        if(map.editAttachedMapSprite != null)
            return;

        EnableAttachedSpriteEditMode enableAttachedSpriteEditMode = new EnableAttachedSpriteEditMode(map, this);
        map.executeCommand(enableAttachedSpriteEditMode);
    }

    public void disableEditAttachedSpritesMode()
    {
        if(map.editAttachedMapSprite == null)
            return;

        DisableAttachedSpriteEditMode disableAttachedSpriteEditMode = new DisableAttachedSpriteEditMode(map, this);
        map.executeCommand(disableAttachedSpriteEditMode);
    }

    public void updateBounds()
    {
        if(attachedSprites == null)
            return;

        float lowestX = this.getX();
        float highestX = this.getX() + this.width;
        float lowestY = this.getY();
        float highestY = this.getY() + this.height;
        for(int i = 0; i < attachedSprites.children.size; i ++)
        {
            MapSprite mapSprite = attachedSprites.children.get(i);
            if(mapSprite.getX() < lowestX)
                lowestX = mapSprite.getX();
            if(mapSprite.getX() + mapSprite.width > highestX)
                highestX = mapSprite.getX() + mapSprite.width;

            if(mapSprite.getY() < lowestY)
                lowestY = mapSprite.getY();
            if(mapSprite.getY() + mapSprite.height > highestY)
                highestY = mapSprite.getY() + mapSprite.height;
        }
        float xDifference = getX() - lowestX;
        float yDifference = getY() - lowestY;

        highestX -= (lowestX + xDifference);
        lowestX = -xDifference;
        highestY -= (lowestY + yDifference);
        lowestY = -yDifference;

        float[] vertices = {lowestX, lowestY, highestX, lowestY, highestX, highestY, lowestX, highestY};
        this.polygon.setVertices(vertices);
    }

    public static int getAndIncrementId()
    {
        return idCounter ++;
    }

    public static void resetIdCounter()
    {
        idCounter = 1;
    }
    
    private void updatePerspectiveTall()
    {
        float spriteX;
        float spriteY;
        if(parentSprite != null)
        {
            spriteX = this.parentSprite.x - ((this.parentSprite.width * this.parentSprite.scale) - this.parentSprite.width) / 2f;
            spriteY = this.parentSprite.y - ((this.parentSprite.height * this.parentSprite.scale) - this.parentSprite.height) / 2f;
        }
        else
        {
            spriteX = this.x - ((this.width * this.scale) - this.width) / 2f;
            spriteY = this.y - ((this.height * this.scale) - this.height) / 2f;
        }
        float trimX = spriteX;
        float trimY = spriteY;
        float trimHeight = (this.sprite.getAtlasRegion().getRegionHeight() / 64f);

//        if(this.parentSprite != null)
//        {
//            trimX = this.parentSprite.x;
//            trimY = this.parentSprite.y;
//        }

        float yScaleDisplacement = 0;
        float xScaleDisplacement = 0;

        float x = trimX;
        float y = trimY;

        Perspective perspective = ((SpriteLayer)this.layer).perspective;
        Vector3 p = perspective.projectWorldToPerspective(x, y);
        x = p.x;
        y = p.y;

        float scale = perspective.getScaleFactor(trimY);
        this.perspectiveScale = scale;
//        float depth = MathUtils.clamp(MathUtils.norm(-.075f, .35f, scale / ((perspective.skew * 25) * perspective.camera.zoom)), 0, 1);
//        if(mapSprite instanceof AnimatedMapSprite)
//        {
//            AnimatedMapSprite animatedMapSprite = (AnimatedMapSprite) mapSprite;
//            animatedMapSprite.animation.setDepth(depth);
//        }
//        else
//            mapSprite.sprite.setDepth(depth);

        scale *= this.scale;
        if(this.parentSprite != null)
        {
//            scale = 1;
//            this.scale = 1;
//            this.perspectiveScale = 1;
        }

        yScaleDisplacement += (((trimHeight * (scale)) - trimHeight)) / 2f;
        xScaleDisplacement += (((this.width) * (scale)) - this.width) / 2f;

        this.perspectiveOffsetX = trimX - (x + (xScaleDisplacement));
        this.perspectiveOffsetY = trimY - (y + (yScaleDisplacement));
    }

    public void updateC()
    {
        MapPoint c1Point = (MapPoint) Utils.getAttachedMapObjectWithProperty(this, "c1");
        MapPoint c2Point = (MapPoint) Utils.getAttachedMapObjectWithProperty(this, "c2");

        if(c1Point == null)
        {
            c1 = null;
            c2 = null;
            return;
        }

        c1 = new Vector2(c1Point.x, c1Point.y);
        c2 = new Vector2(c2Point.x, c2Point.y);


        // make c2 connect to edgeSprites c2
        if(toEdgeSprite != null)
        {
            float x1, y1;
            float x2, y2;
            if (Utils.getAttachedMapObjectWithProperty(this, "edgeC") != null && Utils.getAttachedMapObjectWithProperty(toEdgeSprite, "edgeC") != null)
            {
                MapPoint point1 = (MapPoint) Utils.getAttachedMapObjectWithProperty(this, "edgeC");
                MapPoint point2 = (MapPoint) Utils.getAttachedMapObjectWithProperty(toEdgeSprite, "edgeC");
                x1 = point1.x;
                y1 = point1.y;
                x2 = point2.x;
                y2 = point2.y;

                float dx = x2 - x1;
                float dy = y2 - y1;
                float radius = .05f;
                float length = (float) Math.sqrt(dx * dx + dy * dy);
                if (length > 0)
                {
                    dx /= length;
                    dy /= length;
                }
                dx *= length - radius;
                dy *= length - radius;
                float x4 = (x1 + dx);
                float y4 = (y1 + dy);
                dx = x2 - x1;
                dy = y2 - y1;
                length = (float) Math.sqrt(dx * dx + dy * dy);
                if (length > 0)
                {
                    dx /= length;
                    dy /= length;
                }
                dx *= length - radius;
                dy *= length - radius;
                float x3 = (x2 - dx);
                float y3 = (y2 - dy);
                x1 = x3;
                y1 = y3;
                x2 = x4;
                y2 = y4;
            } else
            {
                MapPoint point1 = (MapPoint) Utils.getAttachedMapObjectWithProperty(this, "c1");
                MapPoint point2 = (MapPoint) Utils.getAttachedMapObjectWithProperty(toEdgeSprite, "c2");
                x1 = point1.x;
                y1 = point1.y;
                x2 = point2.x;
                y2 = point2.y;
            }
            // Since this snaps from edgeC to edgeC, make it branch off to c2 as well
            if (Utils.getAttachedMapObjectWithProperty(this, "edgeC") != null && Utils.getAttachedMapObjectWithProperty(toEdgeSprite, "edgeC") != null)
            {
                MapPoint p2 = (MapPoint) Utils.getAttachedMapObjectWithProperty(this, "c2");
                MapPoint p1 = (MapPoint) Utils.getAttachedMapObjectWithProperty(toEdgeSprite, "edgeC");
                c1.set(x1, y1);
                c2.set(x2, y2);
            } else
            {
                c1.set(x1, y1);
                c2.set(x2, y2);
            }
            // End of the fence connections. Make the cPositions start and end to match
            if (toEdgeSprite.toEdgeSprite == null)
            {
                if (Utils.getAttachedMapObjectWithProperty(this, "edgeC") != null && Utils.getAttachedMapObjectWithProperty(toEdgeSprite, "edgeC") != null)
                {
                    MapPoint p2 = (MapPoint) Utils.getAttachedMapObjectWithProperty(this, "c2");
                    MapPoint point1 = (MapPoint) Utils.getAttachedMapObjectWithProperty(toEdgeSprite, "edgeC");
                    MapPoint point2;
                    if (p2.x > point1.x)
                        point2 = (MapPoint) Utils.getAttachedMapObjectWithProperty(toEdgeSprite, "c1");
                    else
                        point2 = (MapPoint) Utils.getAttachedMapObjectWithProperty(toEdgeSprite, "c2");
                    x1 = point1.x;
                    y1 = point1.y;
                    x2 = point2.x;
                    y2 = point2.y;

                    float dx = x2 - x1;
                    float dy = y2 - y1;
                    float radius = .05f;
                    float length = (float) Math.sqrt(dx * dx + dy * dy);
                    if (length > 0)
                    {
                        dx /= length;
                        dy /= length;
                    }
                    dx *= length - radius;
                    dy *= length - radius;
                    float x4 = (x1 + dx);
                    float y4 = (y1 + dy);
                    dx = x2 - x1;
                    dy = y2 - y1;
                    length = (float) Math.sqrt(dx * dx + dy * dy);
                    if (length > 0)
                    {
                        dx /= length;
                        dy /= length;
                    }
                    dx *= length - radius;
                    dy *= length - radius;
                    float x3 = (x2 - dx);
                    float y3 = (y2 - dy);
                    x1 = x3;
                    y1 = y3;
                    x2 = x4;
                    y2 = y4;
                } else
                {
                    MapPoint point1 = (MapPoint) Utils.getAttachedMapObjectWithProperty(toEdgeSprite, "c1");
                    MapPoint point2 = (MapPoint) Utils.getAttachedMapObjectWithProperty(toEdgeSprite, "c2");
                    x1 = point1.x;
                    y1 = point1.y;
                    x2 = point2.x;
                    y2 = point2.y;
                }
                toEdgeSprite.c1 = new Vector2();
                toEdgeSprite.c2 = new Vector2();
                toEdgeSprite.c1.set(x1, y1);
                toEdgeSprite.c2.set(x2, y2);
                if(toEdgeSprite.c1.x > toEdgeSprite.c2.x)
                {
                    float c1X = toEdgeSprite.c1.x;
                    float c1Y = toEdgeSprite.c1.y;
                    toEdgeSprite.c1.set(toEdgeSprite.c2.x, toEdgeSprite.c2.y);
                    toEdgeSprite.c2.set(c1X, c1Y);
                }
            }
        }


        if(c1.x > c2.x)
        {
            float c1X = c1.x;
            float c1Y = c1.y;
            c1.set(c2.x, c2.y);
            c2.set(c1X, c1Y);
        }
    }

    @Override
    public int compareTo(MapSprite other)
    {
        int thisCPresent = 1;
        int otherCPresent = 1;
        if(c1 == null)
            thisCPresent = 0;
        if(other.c1 == null)
            otherCPresent = 0;
        if(!(thisCPresent == 1 && otherCPresent == 1))
            return Integer.compare(thisCPresent, otherCPresent);

        /*if min and max z is within the other line, check which side of other line any this.point is on
        if right, draw last, if left draw first
        if min and max z are not both within the other line, you sort by min-z
        the higher min-z draws first
        if x1 < x2 then its normal, if x1 > x2 then the line is reversed*/

        float minZ;
        float maxZ;
        float otherMinZ;
        float otherMaxZ;

        if(c1.y < c2.y)
        {
            minZ = c1.y;
            maxZ = c2.y;
        }
        else
        {
            minZ = c2.y;
            maxZ = c1.y;
        }
        if(other.c1.y < other.c2.y)
        {
            otherMinZ = other.c1.y;
            otherMaxZ = other.c2.y;
        }
        else
        {
            otherMinZ = other.c2.y;
            otherMaxZ = other.c1.y;
        }

        float midCX = (c1.x + c2.x) / 2f;
        float midCY = (c1.y + c2.y) / 2f;
        float otherMidCX = (other.c1.x + other.c2.x) / 2f;
        float otherMidCY = (other.c1.y + other.c2.y) / 2f;

        // this sandwhiched between other, test side of line
        if(Utils.areTwoNumbersWithinNumbers(minZ, maxZ, otherMinZ, otherMaxZ))
        {
            return Intersector.pointLineSide(other.c2.x, other.c2.y, other.c1.x, other.c1.y, midCX, midCY);
        }
        // other sandwhiched between this, test side of line
        else if(Utils.areTwoNumbersWithinNumbers(otherMinZ, otherMaxZ, minZ, maxZ))
        {
            return Intersector.pointLineSide(c1.x, c1.y, c2.x, c2.y, otherMidCX, otherMidCY);
        }
        // No sandwhiching going on, test for which minZ is higher
        else
        {
            return Float.compare(otherMinZ, minZ);
        }
    }
}
