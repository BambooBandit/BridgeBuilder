package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.EditorPolygon;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.ui.manipulators.MoveBox;
import com.bamboo.bridgebuilder.ui.manipulators.RotationBox;
import com.bamboo.bridgebuilder.ui.manipulators.ScaleBox;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.ColorPropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.LabelFieldPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

public class MapSprite extends LayerChild
{
    public float rotation, scale, perspectiveScale;
    public EditorPolygon polygon; // Used to show the sprite bounds even when scaled and rotated
    public RotationBox rotationBox;
    public MoveBox moveBox;
    public ScaleBox scaleBox;
    public boolean selected;
    public Array<PropertyField> lockedProperties; // properties such as rotation. They belong to all sprites
    public float z;
    public int id; // Used to be able to set any sprites id and specifically retrieve it in the game
    public Sprite sprite;
    public SpriteTool tool;
    public float width, height;

    public int layerOverrideIndex; // Only used to keep the reloading simple. When the MapSprite is made, store the index of the override layer here and use it later to set the bottom variable once all layers are created. 0 means no override. Index starts at 1.
    public Layer layerOverride; // If this is not null, before drawing this sprite, draw that whole layer. Used to organize different layer heights

    float[] verts; // Used to pass to the sprite batch for skewable drawing

    public MapSprite(Map map, Layer layer, SpriteTool tool, float x, float y)
    {
        super(map, layer, x, y);
        this.lockedProperties = new Array<>();
        this.sprite = new TextureAtlas.AtlasSprite((TextureAtlas.AtlasRegion) tool.textureRegion);
        this.sprite.setSize(this.sprite.getWidth() / 64, this.sprite.getHeight() / 64);
        x -= this.sprite.getWidth() / 2;
        y -= this.sprite.getHeight() / 2;
        this.position.set(x, y);
        this.sprite.setPosition(x, y);
        this.width = this.sprite.getWidth();
        this.height = this.sprite.getHeight();
        this.tool = tool;
        float[] vertices = {0, 0, this.width, 0, this.width, this.height, 0, this.height};
        this.polygon = new EditorPolygon(vertices);
        this.polygon.setPosition(x, y);
        this.polygon.setOrigin(this.width / 2, this.height / 2);
        this.rotationBox = new RotationBox();
        this.rotationBox.setPosition(x + this.width, y + this.height / 2);
        this.moveBox = new MoveBox();
        this.moveBox.setPosition(x + this.width, y + this.height / 2);
        this.scaleBox = new ScaleBox();
        this.scaleBox.setPosition(x + this.width, y + this.height / 2);
        this.verts = new float[20];
        this.scale = 1;

        this.lockedProperties.add(new LabelFieldPropertyValuePropertyField("ID", "0", map.skin, map.propertyMenu, null, false));
        this.lockedProperties.add(new LabelFieldPropertyValuePropertyField("Rotation", "0", map.skin, map.propertyMenu, null, false));
        this.lockedProperties.add(new LabelFieldPropertyValuePropertyField("Scale", "1", map.skin, map.propertyMenu, null, false));
        this.lockedProperties.add(new LabelFieldPropertyValuePropertyField("Z", "0", map.skin, map.propertyMenu, null, false));
        this.lockedProperties.add(new ColorPropertyField(map.skin, map.propertyMenu, null, false, 1, 1, 1, 1));
    }

    @Override
    public void setPosition(float x, float y)
    {
        super.setPosition(x, y);
        this.polygon.setPosition(x, y);
        this.sprite.setPosition(x, y);

        x += this.width;
        y += this.height / 2;
        this.rotationBox.setPosition(x, y);
        this.moveBox.setPosition(x, y);
        this.scaleBox.setPosition(x, y);
    }

    @Override
    public void draw()
    {
        if(layerOverride != null)
            layerOverride.draw();

        float lowestYOffset = -1;
        float tinyHeight = -1;
        if(sprite instanceof TextureAtlas.AtlasSprite)
            lowestYOffset = ((TextureAtlas.AtlasSprite) sprite).getAtlasRegion().offsetY;
        if(tool.topSprites != null)
            for (int i = 0; i < tool.topSprites.size; i++)
                if(tool.topSprites.get(i).getAtlasRegion().offsetY < lowestYOffset)
                    lowestYOffset = tool.topSprites.get(i).getAtlasRegion().offsetY;
        if(sprite instanceof TextureAtlas.AtlasSprite)
            tinyHeight = ((TextureAtlas.AtlasSprite) sprite).getAtlasRegion().offsetY - lowestYOffset;

        float u = sprite.getU();
        float v = sprite.getV();
        float u2 = sprite.getU2();
        float v2 = sprite.getV2();
        float xCenterScreen = Gdx.graphics.getWidth() / 2;
        float xCenterSprite = Utils.project(map.camera,sprite.getX() + sprite.getWidth() / 2, sprite.getY()).x;
        float yCenterScreen = Gdx.graphics.getHeight() / 2;
        float ySprite = Utils.project(map.camera,sprite.getX(), sprite.getY()).y;
        float xSkewAmount = ((xCenterSprite - xCenterScreen) / 3) * z;
        float ySkewAmount = ((ySprite - yCenterScreen) / 5) * z;
        float xOffset = xSkewAmount * (tinyHeight / sprite.getHeight());
        float yOffset = ySkewAmount * (tinyHeight / sprite.getHeight());
        float heightDifferencePercentage = sprite.getRegionHeight() / sprite.getHeight();
        xSkewAmount *= heightDifferencePercentage;
        ySkewAmount *= heightDifferencePercentage;

        if(!map.editor.fileMenu.toolPane.parallax.selected)
        {
            xSkewAmount = 0;
            ySkewAmount = 0;
            xOffset = 0;
            yOffset = 0;
        }
        float[] vertices = sprite.getVertices();

        verts[0] = vertices[SpriteBatch.X2] + xSkewAmount + xOffset;
        verts[1] = vertices[SpriteBatch.Y2] + ySkewAmount + yOffset;
        float colorFloatBits = sprite.getColor().toFloatBits();
        verts[2] = colorFloatBits;
        verts[3] = u;
        verts[4] = v;

        verts[5] = vertices[SpriteBatch.X3] + xSkewAmount + xOffset;
        verts[6] = vertices[SpriteBatch.Y3] + ySkewAmount + yOffset ;
        verts[7] = colorFloatBits;
        verts[8] = u2;
        verts[9] = v;

        verts[10] = vertices[SpriteBatch.X4] + xOffset;
        verts[11] = vertices[SpriteBatch.Y4] + yOffset;
        verts[12] = colorFloatBits;
        verts[13] = u2;
        verts[14] = v2;

        verts[15] = vertices[SpriteBatch.X1] + xOffset;
        verts[16] = vertices[SpriteBatch.Y1] + yOffset;
        verts[17] = colorFloatBits;
        verts[18] = u;
        verts[19] = v2;

        map.editor.batch.draw(sprite.getTexture(), verts, 0, verts.length);

        if(map.editor.fileMenu.toolPane.top.selected)
        {
            if (tool.topSprites != null)
            {
                for (int i = 0; i < tool.topSprites.size; i++)
                {
                    tool.topSprites.get(i).setPosition(sprite.getX(), sprite.getY());
                    tool.topSprites.get(i).setRotation(sprite.getRotation());

                    tinyHeight = tool.topSprites.get(i).getAtlasRegion().offsetY - lowestYOffset;

                    u = tool.topSprites.get(i).getU();
                    v = tool.topSprites.get(i).getV();
                    u2 = tool.topSprites.get(i).getU2();
                    v2 = tool.topSprites.get(i).getV2();
                    xCenterScreen = Gdx.graphics.getWidth() / 2;
                    xCenterSprite = Utils.project(map.camera, tool.topSprites.get(i).getX() + tool.topSprites.get(i).getWidth() / 2, tool.topSprites.get(i).getY()).x;
                    yCenterScreen = Gdx.graphics.getHeight() / 2;
                    ySprite = Utils.project(map.camera, tool.topSprites.get(i).getX(), tool.topSprites.get(i).getY()).y;
                    xSkewAmount = ((xCenterSprite - xCenterScreen) / 3) * z;
                    ySkewAmount = ((ySprite - yCenterScreen) / 5) * z;
                    xOffset = xSkewAmount * (tinyHeight / tool.topSprites.get(i).getHeight());
                    yOffset = ySkewAmount * (tinyHeight / tool.topSprites.get(i).getHeight());

                    heightDifferencePercentage = tool.topSprites.get(i).getRegionHeight() / tool.topSprites.get(i).getHeight();

                    xSkewAmount *= heightDifferencePercentage;
                    ySkewAmount *= heightDifferencePercentage;

                    if (!map.editor.fileMenu.toolPane.parallax.selected)
                    {
                        xSkewAmount = 0;
                        ySkewAmount = 0;
                        xOffset = 0;
                        yOffset = 0;
                    }
                    vertices = tool.topSprites.get(i).getVertices();

                    verts[0] = vertices[SpriteBatch.X2] + xSkewAmount + xOffset;
                    verts[1] = vertices[SpriteBatch.Y2] + ySkewAmount + yOffset;
                    verts[2] = Color.toFloatBits(255, 255, 255, 255);
                    verts[3] = u;
                    verts[4] = v;

                    verts[5] = vertices[SpriteBatch.X3] + xSkewAmount + xOffset;
                    verts[6] = vertices[SpriteBatch.Y3] + ySkewAmount + yOffset;
                    verts[7] = Color.toFloatBits(255, 255, 255, 255);
                    verts[8] = u2;
                    verts[9] = v;

                    verts[10] = vertices[SpriteBatch.X4] + xOffset;
                    verts[11] = vertices[SpriteBatch.Y4] + yOffset;
                    verts[12] = Color.toFloatBits(255, 255, 255, 255);
                    verts[13] = u2;
                    verts[14] = v2;

                    verts[15] = vertices[SpriteBatch.X1] + xOffset;
                    verts[16] = vertices[SpriteBatch.Y1] + yOffset;
                    verts[17] = Color.toFloatBits(255, 255, 255, 255);
                    verts[18] = u;
                    verts[19] = v2;

                    map.editor.batch.draw(tool.topSprites.get(i).getTexture(), verts, 0, verts.length);
                }
            }
        }
    }

    @Override
    public void drawHoverOutline()
    {
        map.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.ORANGE);
        map.editor.shapeRenderer.polygon(polygon.getTransformedVertices());
    }

    @Override
    public void drawSelectedOutline()
    {
        map.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.GREEN);
        map.editor.shapeRenderer.polygon(polygon.getTransformedVertices());
    }

    @Override
    public void drawSelectedHoveredOutline()
    {
        map.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.YELLOW);
        map.editor.shapeRenderer.polygon(polygon.getTransformedVertices());
    }

    public void drawTopSprites()
    {
        if(this.sprite != null)
        {
            if(this.tool.topSprites != null)
            {
                for(int i = 0; i < this.tool.topSprites.size; i ++)
                    map.editor.batch.draw(this.tool.topSprites.get(i), position.x, position.y);
            }
        }
    }

    public void drawRotationBox()
    {
        if(selected)
            rotationBox.sprite.draw(map.editor.batch);
    }
    public void drawMoveBox()
    {
        if(selected)
            moveBox.sprite.draw(map.editor.batch);
    }
    public void drawScaleBox()
    {
        if(selected)
            scaleBox.sprite.draw(map.editor.batch);
    }

    public void setID(int id)
    {
        for(int i = 0; i < lockedProperties.size; i ++)
        {
            PropertyField propertyField = lockedProperties.get(i);
            if(propertyField instanceof LabelFieldPropertyValuePropertyField)
            {
                LabelFieldPropertyValuePropertyField labelFieldProperty = (LabelFieldPropertyValuePropertyField) propertyField;
                if(labelFieldProperty.getProperty().equals("ID"))
                {
                    labelFieldProperty.value.setText(Integer.toString(id));
                    break;
                }
            }
        }
        this.id = id;
    }

    public void setZ(float z)
    {
        for(int i = 0; i < lockedProperties.size; i ++)
        {
            PropertyField propertyField = lockedProperties.get(i);
            if(propertyField instanceof LabelFieldPropertyValuePropertyField)
            {
                LabelFieldPropertyValuePropertyField labelFieldProperty = (LabelFieldPropertyValuePropertyField) propertyField;
                if(labelFieldProperty.getProperty().equals("z"))
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
        ColorPropertyField colorProperty = Utils.getLockedColorField(this.lockedProperties);
        colorProperty.rValue.setText(Float.toString(r));
        colorProperty.gValue.setText(Float.toString(g));
        colorProperty.bValue.setText(Float.toString(b));
        colorProperty.aValue.setText(Float.toString(a));
    }

    public void setRotation(float degree)
    {
        this.rotation = degree;
        this.sprite.setRotation(degree);
        this.polygon.setRotation(degree);
        if(this.tool.topSprites != null)
        {
            for(int i = 0; i < this.tool.topSprites.size; i++)
                this.tool.topSprites.get(i).setRotation(degree);
        }
        LabelFieldPropertyValuePropertyField labelFieldProperty = Utils.getLockedPropertyField(this.lockedProperties, "Rotation");
        labelFieldProperty.value.setText(Float.toString(this.rotation));
    }

    public void rotate(float degree)
    {
        this.rotation += degree;
        Utils.spritePositionCopy.set(position);
        Vector2 endPos = Utils.spritePositionCopy.sub(Utils.centerOrigin).rotate(degree).add(Utils.centerOrigin); // TODO don't assume this was set in case rotate is used somewhere else
        setPosition(endPos.x, endPos.y);
        this.sprite.rotate(degree);
        this.polygon.rotate(degree);
        if(this.tool.topSprites != null)
        {
            for(int i = 0; i < this.tool.topSprites.size; i ++)
                this.tool.topSprites.get(i).rotate(degree);
        }

        LabelFieldPropertyValuePropertyField labelFieldProperty = Utils.getLockedPropertyField(this.lockedProperties, "Rotation");
        labelFieldProperty.value.setText(Float.toString(this.rotation));
    }

    public void setScale(float scale)
    {
        if(scale <= 0)
            return;
        this.scale = scale;
        this.sprite.setScale(scale);
        this.polygon.setScale(scale, scale);
        if(this.tool.topSprites != null)
        {
            for(int i = 0; i < this.tool.topSprites.size; i ++)
                this.tool.topSprites.get(i).setScale(scale);
        }

        LabelFieldPropertyValuePropertyField labelFieldProperty = Utils.getLockedPropertyField(this.lockedProperties, "Scale");
        labelFieldProperty.value.setText(Float.toString(this.scale));
    }

    public void select()
    {
        this.map.selectedSprites.add(this);
        this.selected = true;
    }
    public void unselect()
    {
        this.map.selectedSprites.removeValue(this, true);
        this.selected = false;
    }

    public void updatePerspectiveScale()
    {
//        if(map.propertyMenu.mapPropertyPanel.getPropertyField("perspectiveMinScale") == null || map.propertyMenu.mapPropertyPanel.getPropertyField("perspectiveMaxScale") == null)
//            return;
//        float perspectiveMinScale = Float.parseFloat(map.propertyMenu.mapPropertyPanel.getPropertyField("perspectiveMinScale").value.getText());
//        float perspectiveMaxScale = Float.parseFloat(map.propertyMenu.mapPropertyPanel.getPropertyField("perspectiveMaxScale").value.getText());
//        float mapHeight = layer.height;
//        float positionY = position.y;
//
//        float coeff = positionY / mapHeight;
//
//        float delta = perspectiveMinScale - perspectiveMaxScale;
//
//        this.perspectiveScale = perspectiveMaxScale + coeff * delta;
//
//        float totalScale = scale + perspectiveScale;
//
//        if(totalScale <= 0)
//            return;
//
//        this.sprite.setScale(totalScale);
//        this.polygon.setScale(totalScale, totalScale);
//        if(this.tool.topSprites != null)
//        {
//            for(int i = 0; i < this.tool.topSprites.size; i ++)
//                this.tool.topSprites.get(i).setScale(totalScale);
//        }
    }

    @Override
    public boolean isHoveredOver(float x, float y)
    {
        return this.polygon.contains(x, y);
    }
}
