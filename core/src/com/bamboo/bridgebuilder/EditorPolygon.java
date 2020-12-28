package com.bamboo.bridgebuilder;

import com.badlogic.gdx.math.*;

/** Encapsulates a 2D polygon defined by it's vertices relative to an origin point (default of 0, 0). */
public class EditorPolygon implements Shape2D
{
    private float[] localVertices;
    private float[] scaledVertices;
    private float[] worldVertices;
    private float x, y;
    private float originX, originY;
    private float rotation;
    private float scaleX = 1, scaleY = 1;
    private boolean dirty = true;
    private Rectangle bounds;

    private float x1Offset = 0, y1Offset = 0, x2Offset = 0, y2Offset = 0, x3Offset = 0, y3Offset = 0, x4Offset = 0, y4Offset = 0;

    /** Constructs a new polygon with no vertices. */
    public EditorPolygon() {
        this.localVertices = new float[0];
    }

    /** Constructs a new polygon from a float array of parts of vertex points.
     *
     * @param vertices an array where every even element represents the horizontal part of a point, and the following element
     *           representing the vertical part
     *
     * @throws IllegalArgumentException if less than 6 elements, representing 3 points, are provided */
    public EditorPolygon(float[] vertices) {
        if (vertices.length < 6) throw new IllegalArgumentException("polygons must contain at least 3 points.");
        this.localVertices = vertices;
    }

    /** Returns the polygon's local vertices without scaling or rotation and without being offset by the polygon position. */
    public float[] getVertices () {
        return localVertices;
    }

    /** Calculates and returns the vertices of the polygon after scaling, rotation, and positional translations have been applied,
     * as they are position within the world.
     *
     * @return vertices scaled, rotated, and offset by the polygon position. */
    public float[] getTransformedVertices () {
        if (!dirty) return worldVertices;
        dirty = false;

        final float[] localVertices = this.localVertices;
        if (worldVertices == null || worldVertices.length != localVertices.length) worldVertices = new float[localVertices.length];

        final float[] worldVertices = this.worldVertices;
        final float positionX = x;
        final float positionY = y;
        final float originX = this.originX;
        final float originY = this.originY;
        final float scaleX = this.scaleX;
        final float scaleY = this.scaleY;
        final boolean scale = scaleX != 1 || scaleY != 1;
        final float rotation = this.rotation;
        final float cos = MathUtils.cosDeg(rotation);
        final float sin = MathUtils.sinDeg(rotation);

        for (int i = 0, n = localVertices.length; i < n; i += 2) {
            float x = localVertices[i] - originX;
            float y = localVertices[i + 1] - originY;

            // scale if needed
            if (scale) {
                x *= scaleX;
                y *= scaleY;
            }

            // rotate if needed
            if (rotation != 0) {
                float oldX = x;
                x = cos * x - sin * y;
                y = sin * oldX + cos * y;
            }

            worldVertices[i] = positionX + x + originX;
            worldVertices[i + 1] = positionY + y + originY;
        }
        if(worldVertices.length > 7)
        {
            worldVertices[0] += x4Offset;
            worldVertices[1] += y4Offset;
            worldVertices[2] += x3Offset;
            worldVertices[3] += y3Offset;
            worldVertices[4] += x2Offset;
            worldVertices[5] += y2Offset;
            worldVertices[6] += x1Offset;
            worldVertices[7] += y1Offset;
        }
        return worldVertices;
    }

    /** Calculates and returns the vertices of the polygon after scaling and positional translations have been applied,
     * as they are position within the world.
     *
     * @return vertices scaled. */
    public float[] getScaledVertices () {
        final float[] localVertices = this.localVertices;
        if (scaledVertices == null || scaledVertices.length != localVertices.length) scaledVertices = new float[localVertices.length];

        final float[] scaledVertices = this.scaledVertices;
        final float originX = this.originX;
        final float originY = this.originY;
        final float scaleX = this.scaleX;
        final float scaleY = this.scaleY;
        final boolean scale = scaleX != 1 || scaleY != 1;

        for (int i = 0, n = localVertices.length; i < n; i += 2) {
            float x = localVertices[i] - originX;
            float y = localVertices[i + 1] - originY;

            // scale if needed
            if (scale) {
                x *= scaleX;
                y *= scaleY;
            }

            scaledVertices[i] = x + originX;
            scaledVertices[i + 1] = y + originY;
        }
        return scaledVertices;
    }

    /** Sets the origin point to which all of the polygon's local vertices are relative to. */
    public void setOrigin (float originX, float originY) {
        this.originX = originX;
        this.originY = originY;
        dirty = true;
    }

    public void setOffset (float x1Offset, float x2Offset, float x3Offset, float x4Offset, float y1Offset, float y2Offset, float y3Offset, float y4Offset) {
        this.x1Offset = x1Offset;
        this.x2Offset = x2Offset;
        this.x3Offset = x3Offset;
        this.x4Offset = x4Offset;
        this.y1Offset = y1Offset;
        this.y2Offset = y2Offset;
        this.y3Offset = y3Offset;
        this.y4Offset = y4Offset;
        dirty = true;
    }

    /** Sets the polygon's position within the world. */
    public void setPosition (float x, float y) {
        this.x = x;
        this.y = y;
        dirty = true;
    }

    /** Sets the polygon's local vertices relative to the origin point, without any scaling, rotating or translations being applied.
     *
     * @param vertices float array where every even element represents the x-coordinate of a vertex, and the proceeding element
     *           representing the y-coordinate.
     * @throws IllegalArgumentException if less than 6 elements, representing 3 points, are provided */
    public void setVertices (float[] vertices) {
        if (vertices.length < 6) throw new IllegalArgumentException("polygons must contain at least 3 points.");
        localVertices = vertices;
        dirty = true;
    }

    /** Translates the polygon's position by the specified horizontal and vertical amounts. */
    public void translate (float x, float y) {
        this.x += x;
        this.y += y;
        dirty = true;
    }

    /** Sets the polygon to be rotated by the supplied degrees. */
    public void setRotation (float degrees) {
        this.rotation = degrees;
        dirty = true;
    }

    /** Applies additional rotation to the polygon by the supplied degrees. */
    public void rotate (float degrees) {
        rotation += degrees;
        dirty = true;
    }

    /** Sets the amount of scaling to be applied to the polygon. */
    public void setScale (float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        dirty = true;
    }

    /** Applies additional scaling to the polygon by the supplied amount. */
    public void scale (float amount) {
        this.scaleX += amount;
        this.scaleY += amount;
        dirty = true;
    }

    /** Sets the polygon's world vertices to be recalculated when calling {@link #getTransformedVertices() getTransformedVertices}. */
    public void dirty () {
        dirty = true;
    }

    /** Returns the area contained within the polygon. */
    public float area () {
        float[] vertices = getTransformedVertices();
        return GeometryUtils.polygonArea(vertices, 0, vertices.length);
    }

    /** Returns an axis-aligned bounding box of this polygon.
     *
     * Note the returned Rectangle is cached in this polygon, and will be reused if this Polygon is changed.
     *
     * @return this polygon's bounding box {@link Rectangle} */
    public Rectangle getBoundingRectangle () {
        float[] vertices = getTransformedVertices();

        float minX = vertices[0];
        float minY = vertices[1];
        float maxX = vertices[0];
        float maxY = vertices[1];

        final int numFloats = vertices.length;
        for (int i = 2; i < numFloats; i += 2) {
            minX = minX > vertices[i] ? vertices[i] : minX;
            minY = minY > vertices[i + 1] ? vertices[i + 1] : minY;
            maxX = maxX < vertices[i] ? vertices[i] : maxX;
            maxY = maxY < vertices[i + 1] ? vertices[i + 1] : maxY;
        }

        if (bounds == null) bounds = new Rectangle();
        bounds.x = minX;
        bounds.y = minY;
        bounds.width = maxX - minX;
        bounds.height = maxY - minY;

        return bounds;
    }

    /** Returns whether an x, y pair is contained within the polygon. */
    @Override
    public boolean contains (float x, float y) {
        final float[] vertices = getTransformedVertices();
        final int numFloats = vertices.length;
        int intersects = 0;

        for (int i = 0; i < numFloats; i += 2) {
            float x1 = vertices[i];
            float y1 = vertices[i + 1];
            float x2 = vertices[(i + 2) % numFloats];
            float y2 = vertices[(i + 3) % numFloats];
            if (((y1 <= y && y < y2) || (y2 <= y && y < y1)) && x < ((x2 - x1) / (y2 - y1) * (y - y1) + x1)) intersects++;
        }
        return (intersects & 1) == 1;
    }

    @Override
    public boolean contains (Vector2 point) {
        return contains(point.x, point.y);
    }

    /** Returns the x-coordinate of the polygon's position within the world. */
    public float getX () {
        return x;
    }

    /** Returns the y-coordinate of the polygon's position within the world. */
    public float getY () {
        return y;
    }

    /** Returns the x-coordinate of the polygon's origin point. */
    public float getOriginX () {
        return originX;
    }

    /** Returns the y-coordinate of the polygon's origin point. */
    public float getOriginY () {
        return originY;
    }

    /** Returns the total rotation applied to the polygon. */
    public float getRotation () {
        return rotation;
    }

    /** Returns the total horizontal scaling applied to the polygon. */
    public float getScaleX () {
        return scaleX;
    }

    /** Returns the total vertical scaling applied to the polygon. */
    public float getScaleY () {
        return scaleY;
    }
}
