package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.EditorPolygon;
import com.bamboo.bridgebuilder.PhysicsBits;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.ui.BBShapeRenderer;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.FieldFieldPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;

public class MapPolygon extends MapObject
{
    public EditorPolygon polygon;
    public int indexOfSelectedVertice = -1; // x index. y is + 1
    public int indexOfHoveredVertice = -1; // x index. y is + 1
    float centroidX, centroidY;
    public Body body;
    public Array<MapSprite> mapSprites; // Used only for Groups

    public MapPolygon(Map map, Layer layer, float[] vertices, float x, float y)
    {
        super(map, layer, x, y);
        this.polygon = new EditorPolygon(vertices);
        this.polygon.setPosition(x, y);
        setPosition(x, y);
        computeCentroid();
    }

    public MapPolygon(Map map, MapSprite mapSprite, float[] vertices, float x, float y)
    {
        super(map, mapSprite, x, y);
        this.polygon = new EditorPolygon(vertices);
        this.polygon.setPosition(x, y);
        setPosition(x, y);
        computeCentroid();
        this.setOriginBasedOnParentSprite();
    }

    public MapPolygon(Map map, float[] vertices, float x, float y)
    {
        super(map, x, y);
        this.polygon = new EditorPolygon(vertices);
        this.polygon.setPosition(x, y);
        setPosition(x, y);
        computeCentroid();
        this.setOriginBasedOnParentSprite();
    }

    @Override public void update() { }

    @Override
    public void drawOutline()
    {
        polygon.setPosition(x - map.cameraX, y - map.cameraY);
        float[] verts = this.polygon.getTransformedVertices();
        boolean isInScreen = isInScreen(verts);
        if(!isInScreen)
            return;
        map.editor.shapeRenderer.set(BBShapeRenderer.ShapeType.Line);
        if(mapSprites == null)
            map.editor.shapeRenderer.setColor(Color.CYAN);
        else
        {
            map.editor.shapeRenderer.setColor(.8f, 0, .8f, .9f);
            for(int i = 0; i < mapSprites.size; i ++)
            {
                if(mapSprites.get(i).selected)
                {
                    map.editor.shapeRenderer.setColor(Color.MAGENTA);
                    break;
                }
            }
        }

        map.editor.shapeRenderer.polygon(verts);

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
        propertyField = Utils.getPropertyField(properties, "dustAngle");
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
    public void draw()
    {
        polygon.setPosition(x - map.cameraX, y - map.cameraY);
        if(!map.editor.fileMenu.toolPane.filledPolygons.selected)
            return;

        float[] verts = this.polygon.getTransformedVertices();
        boolean isInScreen = isInScreen(verts);
        if(!isInScreen)
            return;
        map.editor.shapeRenderer.set(BBShapeRenderer.ShapeType.Filled);
        if(mapSprites == null)
        {
            if(Utils.containsProperty(properties, "blocked"))
                map.editor.shapeRenderer.setColor(1f, 0f, 0f, .3f);
            else
                map.editor.shapeRenderer.setColor(0f, 1f, 1f, .3f);
        }
        else
        {
            map.editor.shapeRenderer.setColor(.8f, 0, .8f, .3f);
            for(int i = 0; i < mapSprites.size; i ++)
            {
                if(mapSprites.get(i).selected)
                {
                    map.editor.shapeRenderer.setColor(1, 0, 1, .3f);
                    break;
                }
            }
        }

        map.editor.shapeRenderer.polygon(verts);

    }

    @Override
    public void draw(float xOffset, float yOffset)
    {
        setPosition(getX() + xOffset, getY() + yOffset);

        float[] verts = this.polygon.getTransformedVertices();
        boolean isInScreen = isInScreen(verts);
        if(!isInScreen)
            return;
        map.editor.shapeRenderer.set(BBShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.CYAN);

        map.editor.shapeRenderer.polygon(verts);

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

        setOriginBasedOnParentSprite();

        if (indexOfSelectedVertice != -1)
            this.moveBox.setPosition(polygon.getTransformedVertices()[indexOfSelectedVertice], polygon.getTransformedVertices()[indexOfSelectedVertice + 1]);
        else
        {
            Rectangle boundingBox = polygon.getBoundingRectangle();
            this.moveBox.setPosition(boundingBox.x - (this.moveBox.width) + (boundingBox.width / 2f),
                    boundingBox.y - (this.moveBox.height) + (boundingBox.height / 2f));
        }

        if(this.body != null)
            this.body.setTransform(this.polygon.getTransformedVertices()[0], this.polygon.getTransformedVertices()[1], (float) Math.toRadians(this.getRotation()));

        if(this.map.editor.fileMenu.toolPane.spriteGridColors.selected)
            this.map.updateLayerSpriteGrids();

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
    public float getScale()
    {
        if(this.attachedSprite == null)
            return 1;
        return this.attachedSprite.scale;
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
        map.editor.shapeRenderer.set(BBShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.ORANGE);
        float[] verts = this.polygon.getTransformedVertices();
        boolean isInScreen = isInScreen(verts);
        if(!isInScreen)
            return;
        map.editor.shapeRenderer.polygon(verts);
    }

    @Override
    public void drawSelectedOutline()
    {
        map.editor.shapeRenderer.set(BBShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.GREEN);

        float[] verts = this.polygon.getTransformedVertices();
        boolean isInScreen = isInScreen(verts);
        if(!isInScreen)
            return;
        map.editor.shapeRenderer.polygon(verts);

        drawGradientNodes();
    }

    @Override
    public void drawSelectedHoveredOutline()
    {
        map.editor.shapeRenderer.set(BBShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.YELLOW);

        float[] verts = this.polygon.getTransformedVertices();
        boolean isInScreen = isInScreen(verts);
        if(!isInScreen)
            return;
        map.editor.shapeRenderer.polygon(verts);
        drawGradientNodes();
    }

    private boolean isInScreen(float[] verts)
    {
        if(layer == null)
            return true;
        boolean isGround = Utils.isLayerGround(layer);
        Perspective perspective = (this.layer).perspective;
        float camHeight = isGround ? (float) (perspective.cameraHeight + Perspective.getExtraMatchingHeight(perspective.cameraHeight)) : 0;
        boolean overlaps = isGround && perspective.skew > 0;
        if(!overlaps)
        {
            float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
            float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE;

            for (int i = 0; i < verts.length; i += 2)
            {
                float x = verts[i];
                float y = verts[i + 1];
                if (x < minX) minX = x;
                if (x > maxX) maxX = x;
                if (y < minY) minY = y;
                if (y > maxY) maxY = y;
            }

            // camera rectangle
            float halfWidth = map.camera.viewportWidth * map.camera.zoom / 2f;
            float halfHeight = map.camera.viewportHeight * map.camera.zoom / 2f;
            float camX = map.camera.position.x;
            float camY = (map.camera.position.y - camHeight);

            float camLeft = camX - halfWidth;
            float camRight = camX + halfWidth;
            float camBottom = camY - halfHeight;
            float camTop = camY + halfHeight;

            // AABB overlap
            overlaps = maxX >= camLeft &&
                    minX <= camRight &&
                    maxY >= camBottom &&
                    minY <= camTop;
        }
        return overlaps;
    }

    private void drawGradientNodes()
    {
        if(Utils.containsProperty(properties, "dustGradientNodeSize"))
        {
            map.editor.shapeRenderer.set(BBShapeRenderer.ShapeType.Filled);
            float distance = 2;
            try
            {
                distance = Float.parseFloat(((FieldFieldPropertyValuePropertyField) Utils.getPropertyField(properties, "dustGradientNodeSize")).getValue());
            }catch (Exception e){}
            Rectangle rectangle = polygon.getBoundingRectangle();
            for(float x = rectangle.x; x < rectangle.x + rectangle.width; x += distance)
            {
                for(float y = rectangle.y; y < rectangle.y + rectangle.height; y += distance)
                {
                    if(polygon.contains(x, y))
                        map.editor.shapeRenderer.circle(x, y, .5f, 20);
                }
            }
            map.editor.shapeRenderer.set(BBShapeRenderer.ShapeType.Line);
        }
    }


    private void drawCentroidAndAngle(float angle)
    {
        map.editor.shapeRenderer.circle(centroidX - map.cameraX, centroidY - map.cameraY, .075f, 8);
        map.editor.shapeRenderer.line(centroidX - map.cameraX, centroidY - map.cameraY, (float) (centroidX - map.cameraX + Math.cos(angle) * .75), (float) (centroidY - map.cameraY + Math.sin(angle) * .75));
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
        x -= map.cameraX;
        y -= map.cameraY;
        return this.polygon.contains(x, y);
    }

    @Override
    public boolean isHoveredOver(float[] vertices)
    {
        polygon.setPosition(polygon.getX() + map.cameraX, polygon.getY() + map.cameraY);
        boolean isHoveredOver = Intersector.overlapConvexPolygons(polygon.getTransformedVertices(), vertices, null);
        polygon.setPosition(polygon.getX() - map.cameraX, polygon.getY() - map.cameraY);
        return isHoveredOver;
    }

    public void moveVertice(float x, float y)
    {
        float[] vertices = this.polygon.getVertices();
        vertices[indexOfSelectedVertice] = x - this.polygon.getX() - map.cameraX;
        vertices[indexOfSelectedVertice + 1] = y - this.polygon.getY() - map.cameraY;
        this.polygon.setVertices(vertices);
        setPosition(this.x, this.y);
        remakeBody();
    }

    public void moveVertice(int index, float x, float y)
    {
        float[] vertices = this.polygon.getVertices();
        vertices[index] = x - this.polygon.getX() - map.cameraX;
        vertices[index + 1] = y - this.polygon.getY() - map.cameraY;
        setPosition(this.x, this.y);
        remakeBody();
    }

    @Override
    public void drawMoveBox()
    {
        if(this.selected && (this.map.editor.fileMenu.toolPane.select.selected || (this.map.editor.fileMenu.toolPane.objectVerticeSelect.selected && this.indexOfSelectedVertice != -1)))
        {
            this.moveBox.setScale(this.map.zoom);
            this.moveBox.sprite.setPosition(this.moveBox.x - map.cameraX, this.moveBox.y - map.cameraY);
            this.moveBox.sprite.draw(this.map.editor.batch);
        }
    }

    public void createBody()
    {
        if(this.body != null)
            return;
//        System.out.println("start " + id);
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
//        System.out.println("end");
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
        return vertices[indexOfSelectedVertice] + polygon.getX() + map.cameraX;
    }

    public float getVerticeY()
    {
        if (indexOfSelectedVertice == -1)
            return -1;
        float[] vertices = this.polygon.getVertices();
        return vertices[indexOfSelectedVertice + 1] + polygon.getY() + map.cameraY;
    }

    public float getVerticeX(int index)
    {
        float[] vertices = this.polygon.getVertices();
        return vertices[index] + polygon.getX() + map.cameraX;
    }

    public float getVerticeY(int index)
    {
        float[] vertices = this.polygon.getVertices();
        return vertices[index + 1] + polygon.getY() + map.cameraY;
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

    @Override
    public float getArea()
    {
        return polygon.area();
    }

    private void computeCentroid()
    {
        centroidX = 0;
        centroidY = 0;
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
        mapPolygon.attachedId = this.attachedId;
        mapPolygon.attachedMapObjectManager = this.attachedMapObjectManager;
        mapPolygon.properties = this.attachedMapObjectManager.properties;
        if(this.body != null)
            mapPolygon.createBody();
        return mapPolygon;
    }
}
