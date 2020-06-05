package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.EditorPolygon;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.ui.manipulators.MoveBox;
import com.bamboo.bridgebuilder.ui.manipulators.RotationBox;
import com.bamboo.bridgebuilder.ui.manipulators.ScaleBox;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.ColorPropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.FieldFieldPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.LabelFieldPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

public class MapSprite extends LayerChild
{
    public float rotation, scale;
    public EditorPolygon polygon; // Used to show the sprite bounds even when scaled and rotated
    public RotationBox rotationBox;
    public MoveBox moveBox;
    public ScaleBox scaleBox;
    public Array<PropertyField> lockedProperties; // properties such as rotation. They belong to all sprites
    public float z;
    public int id; // Used to be able to set any sprites id and specifically retrieve it in the game
    public Sprite sprite;
    public SpriteTool tool;
    public float width, height;

    public int layerOverrideIndex; // Only used to keep the reloading simple. When the MapSprite is made, store the index of the override layer here and use it later to set the bottom variable once all layers are created. 0 means no override. Index starts at 1.
    public Layer layerOverride; // If this is not null, before drawing this sprite, draw that whole layer. Used to organize different layer heights

    private float[] verts; // Used to pass to the sprite batch for skewable drawing

    public Array<MapObject> attachedMapObjects;


    public MapSprite(Map map, Layer layer, SpriteTool tool, float x, float y)
    {
        super(map, layer, x, y);
        this.lockedProperties = new Array<>();
        this.sprite = new TextureAtlas.AtlasSprite((TextureAtlas.AtlasRegion) tool.textureRegion);
        this.sprite.setSize(this.sprite.getWidth() / 64, this.sprite.getHeight() / 64);
        this.sprite.setOriginCenter();
        x -= this.sprite.getWidth() / 2;
        y -= this.sprite.getHeight() / 2;
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

        TextField.TextFieldFilter valueFilter = new TextField.TextFieldFilter()
        {
            @Override
            public boolean acceptChar(TextField textField, char c)
            {
                return c == '.' || c == '-' || Character.isDigit(c);
            }
        };

        LabelFieldPropertyValuePropertyField idProperty = new LabelFieldPropertyValuePropertyField("ID", "0", map.skin, map.propertyMenu, null, false);
        LabelFieldPropertyValuePropertyField rotationProperty = new LabelFieldPropertyValuePropertyField("Rotation", "0", map.skin, map.propertyMenu, null, false);
        LabelFieldPropertyValuePropertyField scaleProperty = new LabelFieldPropertyValuePropertyField("Scale", "1", map.skin, map.propertyMenu, null, false);
        LabelFieldPropertyValuePropertyField zProperty = new LabelFieldPropertyValuePropertyField("Z", "0", map.skin, map.propertyMenu, null, false);
        ColorPropertyField colorProperty = new ColorPropertyField(map.skin, map.propertyMenu, null, false, 1, 1, 1, 1);

        this.lockedProperties.add(idProperty);
        this.lockedProperties.add(rotationProperty);
        this.lockedProperties.add(scaleProperty);
        this.lockedProperties.add(zProperty);
        this.lockedProperties.add(colorProperty);

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
        this.setPosition(x, y);
        this.updatePerspective();
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
        if(this.map.editor.fileMenu.toolPane.top.selected)
            drawTopSprites();
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
                    map.editor.batch.draw(this.tool.topSprites.get(i), getX(), getY());
            }
        }
    }

    public void drawRotationBox()
    {
        if(selected && map.editor.fileMenu.toolPane.select.selected)
            rotationBox.sprite.draw(map.editor.batch);
    }
    public void drawMoveBox()
    {
        if(selected && map.editor.fileMenu.toolPane.select.selected)
            moveBox.sprite.draw(map.editor.batch);
    }
    public void drawScaleBox()
    {
        if(selected )
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
        float rotateAmount = degree - this.rotation;
        this.rotation = degree;
        Utils.spritePositionCopy.set(getX(), getY());
        Vector2 endPos = Utils.spritePositionCopy.sub(Utils.centerOrigin).rotate(rotateAmount).add(Utils.centerOrigin); // TODO don't assume this was set in case rotate is used somewhere else
        setPosition(endPos.x, endPos.y);
        this.sprite.setRotation(degree);
        this.polygon.setRotation(degree);
        if(this.tool.topSprites != null)
        {
            for(int i = 0; i < this.tool.topSprites.size; i++)
                this.tool.topSprites.get(i).setRotation(degree);
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
        if(this.map.editor.fileMenu.toolPane.perspective.selected && Utils.doesLayerHavePerspective(this.map, this.layer) && !Utils.isLayerGround(this.layer))
        {
            if(Gdx.graphics.getHeight() == 0)
                return;

            float spriteAtlasWidth = this.sprite.getRegionWidth() / 64;
            float spriteAtlasHeight = this.sprite.getRegionHeight() / 64;
            float whiteSpaceWidth = (sprite.getWidth() - spriteAtlasWidth);

            map.camera.update();
            float[] m = this.map.camera.combined.getValues();
            float skew = 0;
            float antiDepth = 0;
            try
            {
                FieldFieldPropertyValuePropertyField property = Utils.getSkewPerspectiveProperty(this.map, this.layer);
                skew = Float.parseFloat(property.value.getText());
                property = Utils.getAntiDepthPerspectiveProperty(this.map, this.layer);
                antiDepth = Float.parseFloat(property.value.getText());
            }
            catch (NumberFormatException e){}
            if(antiDepth >= .1f)
                skew /= antiDepth * 15;
            m[Matrix4.M31] += skew;
            m[Matrix4.M11] += this.map.camera.position.y / ((-10f * this.map.camera.zoom) / skew) - ((.097f * antiDepth) / (antiDepth + .086f));
            this.map.camera.invProjectionView.set(this.map.camera.combined);
            Matrix4.inv(this.map.camera.invProjectionView.val);
            this.map.camera.frustum.update(this.map.camera.invProjectionView);

            xScaleDisplacement = sprite.getWidth() / 2;

            Vector3 p = Utils.project(this.map.camera, x + xScaleDisplacement, y);
            x = p.x;
            y = Gdx.graphics.getHeight() - p.y;
            this.map.camera.update();
            p = Utils.unproject(this.map.camera, x, y);
            x = p.x;
            y = p.y;

            yScaleDisplacement = ((spriteAtlasHeight * this.sprite.getScaleY()) - spriteAtlasHeight) / 2f;
            xScaleDisplacement = -(spriteAtlasWidth / 2);
            xScaleDisplacement -= (whiteSpaceWidth * sprite.getScaleX() / 2);
        }


        this.polygon.setPosition(x + xScaleDisplacement, y + yScaleDisplacement);
        this.sprite.setPosition(x + xScaleDisplacement, y + yScaleDisplacement);

        x += this.width / 2;
        y += this.height / 2;
        this.rotationBox.setPosition(x, y);
        this.moveBox.setPosition(x, y);
        this.scaleBox.setPosition(x, y);

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

        if(map.editor.fileMenu.toolPane.perspective.selected && !Utils.isLayerGround(this.layer))
        {
            float perspectiveScale = this.scale + this.perspectiveScale;
            scale = perspectiveScale;
        }
        this.sprite.setScale(scale);
        this.polygon.setScale(scale, scale);
        if(this.tool.topSprites != null)
        {
            for(int i = 0; i < this.tool.topSprites.size; i ++)
                this.tool.topSprites.get(i).setScale(scale);
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
        if(this.tool.attachedMapObjectManagers == null)
            return;
        for(int i = 0; i < this.attachedMapObjects.size; i ++)
            this.attachedMapObjects.get(i).unselect();
    }

    public void addAttachedMapObject(MapObject mapObject)
    {
        if(this.attachedMapObjects == null)
            this.attachedMapObjects = new Array<>();
        this.attachedMapObjects.add(mapObject);
    }

    public void createAttachedMapObject(Map map, MapObject mapObject)
    {
        this.tool.createAttachedMapObject(map, mapObject, this);
    }

    public void removeAttachedMapObject(MapObject mapObject)
    {
        this.tool.removeAttachedMapObject(mapObject);
    }

    @Override
    public boolean isHoveredOver(float x, float y)
    {
        return this.polygon.contains(x, y);
    }

    @Override
    public boolean isHoveredOver(float[] vertices)
    {
        return Intersector.overlapConvexPolygons(polygon.getTransformedVertices(), vertices, null);
    }
}
