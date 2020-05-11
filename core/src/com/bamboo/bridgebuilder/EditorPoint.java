package com.bamboo.bridgebuilder;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;

/** Encapsulates a 2D polygon defined by it's vertices relative to an origin point (default of 0, 0). */
public class EditorPoint implements Shape2D
{
    private float localX, localY, worldX, worldY;
    private float originX, originY;
    private float rotation;
    private float scaleX = 1, scaleY = 1;
    private boolean dirty = true;

    /** Constructs a new polygon with no vertices. */
    public EditorPoint() {
    }

    public EditorPoint(float localX, float localY) {
        this.localX = localX;
        this.localY = localY;
        this.worldX = localX;
        this.worldY = localY;
    }

    /** Calculates and returns the vertices of the polygon after scaling, rotation, and positional translations have been applied,
     * as they are position within the world.
     *
     * @return vertices scaled, rotated, and offset by the polygon position. */
    public float getTransformedX () {
        if (!dirty) return worldX;
        dirty = false;

        final float localX = this.localX;
        final float localY = this.localY;

        final float originX = this.originX;
        final float originY = this.originY;
        final float scaleX = this.scaleX;
        final float scaleY = this.scaleY;
        final boolean scale = scaleX != 1 || scaleY != 1;
        final float rotation = this.rotation;
        final float cos = MathUtils.cosDeg(rotation);
        final float sin = MathUtils.sinDeg(rotation);

        float x = -originX;
        float y = -originY;

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

        this.worldX = localX + x + originX;
        this.worldY = localY + y + originY;
        return this.worldX;
    }

    public float getTransformedY () {
        if (!dirty) return worldY;
        dirty = false;

        final float localX = this.localX;
        final float localY = this.localY;

        final float originX = this.originX;
        final float originY = this.originY;
        final float scaleX = this.scaleX;
        final float scaleY = this.scaleY;
        final boolean scale = scaleX != 1 || scaleY != 1;
        final float rotation = this.rotation;
        final float cos = MathUtils.cosDeg(rotation);
        final float sin = MathUtils.sinDeg(rotation);

        float x = -originX;
        float y = -originY;

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

        this.worldX = localX + x + originX;
        this.worldY = localY + y + originY;
        return this.worldY;
    }

    /** Sets the origin point to which all of the polygon's local vertices are relative to. */
    public void setOrigin (float originX, float originY) {
        this.originX = originX;
        this.originY = originY;
        dirty = true;
    }

    /** Sets the polygon's position within the world. */
    public void setPosition (float x, float y) {
        this.localX = x;
        this.localY = y;
        dirty = true;
    }

    /** Translates the polygon's position by the specified horizontal and vertical amounts. */
    public void translate (float x, float y) {
        this.localX += x;
        this.localY += y;
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

    public void dirty () {
        dirty = true;
    }

    @Override
    public boolean contains (Vector2 point) {
        return contains(point.x, point.y);
    }

    @Override
    public boolean contains(float x, float y)
    {
        return false;
    }

    /** Returns the x-coordinate of the polygon's position within the world. */
    public float getX () {
        return this.getTransformedX();
    }

    /** Returns the y-coordinate of the polygon's position within the world. */
    public float getY () {
        return this.getTransformedY();
    }

    public float getLocalX () {
        return this.localX;
    }

    public float getLocalY () {
        return this.localY;
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
