package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.bamboo.bridgebuilder.EditorPolygon;
import com.bamboo.bridgebuilder.PhysicsBits;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.FieldFieldPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;

public class MapPolygon extends MapObject
{
    public EditorPolygon polygon;
    public int indexOfSelectedVertice = -1; // x index. y is + 1
    public int indexOfHoveredVertice = -1; // x index. y is + 1
    float centroidX, centroidY;
    public Body body;

    public MapPolygon(Map map, Layer layer, float[] vertices, float x, float y)
    {
        super(map, layer, x, y);
        this.polygon = new EditorPolygon(vertices);
        this.polygon.setPosition(x, y);
        computeCentroid();
    }

    public MapPolygon(Map map, MapSprite mapSprite, float[] vertices, float x, float y)
    {
        super(map, mapSprite, x, y);
        this.polygon = new EditorPolygon(vertices);
        this.polygon.setPosition(x, y);
        computeCentroid();
        this.setOriginBasedOnParentSprite();
    }

    @Override
    public void draw()
    {
        map.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.CYAN);
        map.editor.shapeRenderer.polygon(this.polygon.getTransformedVertices());

        PropertyField propertyField = Utils.getPropertyField(properties, "angle");
        if (propertyField != null)
        {
            FieldFieldPropertyValuePropertyField angleProperty = (FieldFieldPropertyValuePropertyField) propertyField;
            try
            {
                float angle = (float) Math.toRadians(Float.parseFloat(angleProperty.getValue()));
                drawCentroidAndAngle(angle);
                return;
            }
            catch (NumberFormatException e)
            {
                return;
            }
        }
    }

    @Override
    public void draw(float xOffset, float yOffset)
    {
        setPosition(getX() + xOffset, getY() + yOffset);
        map.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.CYAN);
        map.editor.shapeRenderer.polygon(this.polygon.getTransformedVertices());

        PropertyField propertyField = Utils.getPropertyField(properties, "angle");
        if (propertyField != null)
        {
            FieldFieldPropertyValuePropertyField angleProperty = (FieldFieldPropertyValuePropertyField) propertyField;
            try
            {
                float angle = (float) Math.toRadians(Float.parseFloat(angleProperty.getValue()));
                drawCentroidAndAngle(angle);
                return;
            }
            catch (NumberFormatException e)
            {
                return;
            }
        }
        setPosition(getX() - xOffset, getY() - yOffset);
    }

    @Override
    public void setPosition(float x, float y)
    {
        super.setPosition(x, y);
        this.polygon.setPosition(x, y);
        if (this.attachedSprite != null && this.attachedSprite instanceof MapSprite)
        {
            MapSprite mapSprite = this.attachedSprite;
            polygon.setRotation(mapSprite.rotation);
            polygon.setScale(mapSprite.scale, mapSprite.scale);
        }
        computeCentroid();

        if (indexOfSelectedVertice != -1)
            this.moveBox.setPosition(polygon.getTransformedVertices()[indexOfSelectedVertice], polygon.getTransformedVertices()[indexOfSelectedVertice + 1]);
        else
            this.moveBox.setPosition(x, y);
        setOriginBasedOnParentSprite();

        if(this.body != null)
            this.body.setTransform(this.polygon.getTransformedVertices()[0], this.polygon.getTransformedVertices()[1], (float) Math.toRadians(this.getRotation()));
    }

    @Override
    public void setScale(float scale)
    {
        if (this.attachedSprite == null)
            return;
        this.polygon.setScale(scale, scale);

        this.remakeBody();
    }

    @Override
    public float getRotation()
    {
        return this.polygon.getRotation();
    }

    @Override
    public void setRotation(float degrees)
    {
        if (this.attachedSprite == null)
            return;

        this.polygon.setRotation(degrees);

        if(this.body != null)
        {
            this.body.setTransform(this.polygon.getTransformedVertices()[0], this.polygon.getTransformedVertices()[1], (float) Math.toRadians(this.getRotation()));
        }
    }

    @Override
    public void drawHoverOutline()
    {
        map.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.ORANGE);
        map.editor.shapeRenderer.polygon(this.polygon.getTransformedVertices());
    }

    @Override
    public void drawSelectedOutline()
    {
        map.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.GREEN);
        map.editor.shapeRenderer.polygon(this.polygon.getTransformedVertices());
    }

    @Override
    public void drawSelectedHoveredOutline()
    {
        map.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.YELLOW);
        map.editor.shapeRenderer.polygon(this.polygon.getTransformedVertices());
    }

    private void drawCentroidAndAngle(float angle)
    {
        map.editor.shapeRenderer.circle(centroidX, centroidY, 5);
        map.editor.shapeRenderer.line(centroidX, centroidY, (float) (centroidX + Math.cos(angle) * 25), (float) (centroidY + Math.sin(angle) * 25));
    }

    @Override
    public float getX()
    {
        return this.polygon.getX();
    }

    @Override
    public float getY()
    {
        return this.polygon.getY();
    }

    @Override
    public void setOriginBasedOnParentSprite()
    {
        if (this.attachedSprite == null)
            return;
        float xOffset = this.polygon.getX() - this.attachedSprite.getX();
        float yOffset = this.polygon.getY() - this.attachedSprite.getY();
        float width = this.attachedSprite.sprite.getWidth();
        float height = this.attachedSprite.sprite.getHeight();
        this.polygon.setOrigin((-xOffset) + width / 2, (-yOffset) + height / 2);
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

    public void moveVertice(float x, float y)
    {
        float[] vertices = this.polygon.getVertices();
        vertices[indexOfSelectedVertice] = x - this.polygon.getX();
        vertices[indexOfSelectedVertice + 1] = y - this.polygon.getY();
        this.polygon.setVertices(vertices);
        setPosition(polygon.getX(), polygon.getY());
        remakeBody();
    }

    public void moveVertice(int index, float x, float y)
    {
        float[] vertices = this.polygon.getVertices();
        vertices[index] = x - this.polygon.getX();
        vertices[index + 1] = y - this.polygon.getY();
        setPosition(polygon.getX(), polygon.getY());
        remakeBody();
    }


    public void createBody()
    {
        if(this.body != null)
            return;
        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        float oldRotation = this.polygon.getRotation();
        float oldX = this.polygon.getX();
        float oldY = this.polygon.getY();
        this.polygon.setRotation(0);
        float offsetX = this.getX() - this.polygon.getTransformedVertices()[0];
        float offsetY = this.getY() - this.polygon.getTransformedVertices()[1];
        this.polygon.setPosition(offsetX, offsetY);
        float[] vertices = this.polygon.getTransformedVertices();
        this.polygon.setRotation(oldRotation);
        this.polygon.setPosition(oldX, oldY);
        shape.set(vertices);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0;
        fixtureDef.filter.categoryBits = PhysicsBits.WORLD_PHYSICS;
        fixtureDef.filter.maskBits = PhysicsBits.LIGHT_PHYSICS;
        this.body = this.map.world.createBody(bodyDef).createFixture(fixtureDef).getBody();
        this.body.setTransform(this.polygon.getTransformedVertices()[0], this.polygon.getTransformedVertices()[1], (float) Math.toRadians(this.getRotation()));

    }

    public void destroyBody()
    {
        if(this.body == null)
            return;
        this.map.world.destroyBody(this.body);
        this.body = null;
    }

    /** Used for when the vertices of the body are affected, like during scaling, etc.*/
    public void remakeBody()
    {
        if(this.body == null)
            return;
        this.destroyBody();
        this.createBody();
    }

    public float getVerticeX()
    {
        if (indexOfSelectedVertice == -1)
            return -1;
        float[] vertices = this.polygon.getVertices();
        return vertices[indexOfSelectedVertice] + this.polygon.getX();
    }

    public float getVerticeY()
    {
        if (indexOfSelectedVertice == -1)
            return -1;
        float[] vertices = this.polygon.getVertices();
        return vertices[indexOfSelectedVertice + 1] + this.polygon.getY();
    }

    public float getVerticeX(int index)
    {
        float[] vertices = this.polygon.getVertices();
        return vertices[index] + this.polygon.getX();
    }

    public float getVerticeY(int index)
    {
        float[] vertices = this.polygon.getVertices();
        return vertices[index + 1] + this.polygon.getY();
    }

    public void drawSelectedVertices()
    {
        if (indexOfSelectedVertice != -1)
        {
            map.editor.shapeRenderer.setColor(Color.GREEN);
            map.editor.shapeRenderer.circle(polygon.getTransformedVertices()[indexOfSelectedVertice], polygon.getTransformedVertices()[indexOfSelectedVertice + 1], .1f, 7);
        }
    }

    public void drawHoveredVertices()
    {
        if (indexOfHoveredVertice != -1)
        {
            map.editor.shapeRenderer.setColor(Color.ORANGE);
            map.editor.shapeRenderer.circle(polygon.getTransformedVertices()[indexOfHoveredVertice], polygon.getTransformedVertices()[indexOfHoveredVertice + 1], .1f, 7);
        }
    }

    private void computeCentroid()
    {
        float[] vertices = polygon.getTransformedVertices();
        float signedArea = 0;
        float x0; // Current vertex X
        float y0; // Current vertex Y
        float x1; // Next vertex X
        float y1; // Next vertex Y
        float a;  // Partial signed area

        // For all vertices except last
        int i;
        for (i = 0; i < vertices.length - 2; i += 2)
        {
            x0 = vertices[i];
            y0 = vertices[i + 1];
            x1 = vertices[i + 2];
            y1 = vertices[i + 3];
            a = x0 * y1 - x1 * y0;
            signedArea += a;
            centroidX += (x0 + x1) * a;
            centroidY += (y0 + y1) * a;
        }

        // Do last vertex separately to avoid performing an expensive
        // modulus operation in each iteration.
        x0 = vertices[i];
        y0 = vertices[i + 1];
        x1 = vertices[0];
        y1 = vertices[1];
        a = x0 * y1 - x1 * y0;
        signedArea += a;
        centroidX += (x0 + x1) * a;
        centroidY += (y0 + y1) * a;

        signedArea *= 0.5;
        centroidX /= (6.0 * signedArea);
        centroidY /= (6.0 * signedArea);
    }

    @Override
    public MapObject copy()
    {
        MapPolygon mapPolygon;
        if (this.attachedSprite != null)
            mapPolygon = new MapPolygon(map, this.attachedSprite, this.polygon.getVertices(), this.polygon.getX(), this.polygon.getY());
        else
            mapPolygon = new MapPolygon(map, this.layer, this.polygon.getVertices(), this.polygon.getX(), this.polygon.getY());
        mapPolygon.id = this.id;
        mapPolygon.attachedMapObjectManager = this.attachedMapObjectManager;
        mapPolygon.properties = this.attachedMapObjectManager.properties;
        if(this.body != null)
            mapPolygon.createBody();
        return mapPolygon;
    }
}
