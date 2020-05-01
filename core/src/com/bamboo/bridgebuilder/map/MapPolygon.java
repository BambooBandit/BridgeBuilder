package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.bamboo.bridgebuilder.EditorPolygon;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.FieldFieldPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.PropertyField;

public class MapPolygon extends MapObject
{
    public float[] vertices;
    float centroidX, centroidY;
    public EditorPolygon polygon;

    public int indexOfSelectedVertice = -1; // x index. y is + 1
    public int indexOfHoveredVertice = -1; // x index. y is + 1

    public MapPolygon(Map map, Layer layer, float[] vertices, float x, float y)
    {
        super(map, layer, x, y);
        this.vertices = vertices;
        this.polygon = new EditorPolygon(vertices);
        this.polygon.setPosition(x, y);
        computeCentroid();
    }

    @Override
    public void draw()
    {
        map.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        map.editor.shapeRenderer.setColor(Color.CYAN);
        map.editor.shapeRenderer.polygon(this.polygon.getTransformedVertices());

        PropertyField propertyField = Utils.getPropertyField(properties, "angle");
        if(propertyField != null)
        {
            FieldFieldPropertyValuePropertyField angleProperty = (FieldFieldPropertyValuePropertyField) propertyField;
            try
            {
                float angle = (float) Math.toRadians(Float.parseFloat(angleProperty.getValue()));
                drawCentroidAndAngle(angle);
                return;
            }
            catch (NumberFormatException e){return;}
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
    public void setPosition(float x, float y)
    {
        super.setPosition(x, y);
        this.polygon.setPosition(x, y);
        if(this.attachedSprite != null && this.attachedSprite instanceof MapSprite)
        {
            MapSprite mapSprite = this.attachedSprite;
            polygon.setRotation(mapSprite.rotation);
        }
        computeCentroid();

        if(indexOfSelectedVertice != -1)
            this.moveBox.setPosition(polygon.getTransformedVertices()[indexOfSelectedVertice], polygon.getTransformedVertices()[indexOfSelectedVertice + 1]);
        else
            this.moveBox.setPosition(x, y);
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

    public void moveVertice(float x, float y)
    {
        float[] vertices = this.polygon.getVertices();
        vertices[indexOfSelectedVertice] = x - this.polygon.getX();
        vertices[indexOfSelectedVertice + 1] = y - this.polygon.getY();
        this.polygon.setVertices(vertices);
        setPosition(polygon.getX(), polygon.getY());
    }

    public float getVerticeX()
    {
        float[] vertices = this.polygon.getVertices();
        return vertices[indexOfSelectedVertice] + this.polygon.getX();
    }

    public float getVerticeY()
    {
        float[] vertices = this.polygon.getVertices();
        return vertices[indexOfSelectedVertice + 1] + this.polygon.getY();
    }

    public void drawSelectedVertices()
    {
        if(indexOfSelectedVertice != -1)
            map.editor.shapeRenderer.circle(polygon.getTransformedVertices()[indexOfSelectedVertice], polygon.getTransformedVertices()[indexOfSelectedVertice + 1], 5);
    }

    public void drawHoveredVertices()
    {
        if(indexOfHoveredVertice != -1)
            map.editor.shapeRenderer.circle(polygon.getTransformedVertices()[indexOfHoveredVertice], polygon.getTransformedVertices()[indexOfHoveredVertice + 1], 5);
    }
}
