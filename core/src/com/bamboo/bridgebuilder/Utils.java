package com.bamboo.bridgebuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.bamboo.bridgebuilder.map.*;
import com.bamboo.bridgebuilder.ui.fileMenu.Tool;
import com.bamboo.bridgebuilder.ui.fileMenu.Tools;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.*;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteSheet;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;
import org.poly2tri.Poly2Tri;
import org.poly2tri.geometry.polygon.PolygonPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Utils
{
    private static RandomXS128 random = new RandomXS128();
    private static Vector3 unprojector = new Vector3();
    public static Vector2 centerOrigin = new Vector2();
    public static Vector2 positionDifference = new Vector2();
    public static Vector2 spritePositionCopy = new Vector2();
    public static boolean print = true;
    public static FloatArray triangles = new FloatArray();

    public static float[] boxSelectCommandVertices = new float[8];

    public static float degreeAngleFix(float angle)
    {
        angle = ((int) angle % 360) + (angle - ((int)angle));
        if(angle > 0.0)
            return angle;
        else
            return angle + 360f;
    }

    public static boolean containsProperty(Array<PropertyField> properties, String property)
    {
        if(properties != null)
        {
            for (int i = 0; i < properties.size; i++)
            {
                if (properties.get(i) instanceof FieldFieldPropertyValuePropertyField)
                {
                    FieldFieldPropertyValuePropertyField propertyData = (FieldFieldPropertyValuePropertyField) properties.get(i);
                    if (propertyData.getProperty().equals(property))
                        return true;
                }
            }
        }
        return false;
    }

//    public static FloatArray triangleFan(float[] polygon)
//    {
//        int start = 0;
//        int curr = 2;
//        int prev = polygon.length - 2;
//        FloatArray out = triangles;
//        out.clear();
//
//        while(start != curr)
//        {
//            out.add(polygon[start]);
//            out.add(polygon[start + 1]);
//            out.add(polygon[curr]);
//            out.add(polygon[curr + 1]);
//            out.add(polygon[prev]);
//            out.add(polygon[prev + 1]);
//            prev = curr;
//            curr = curr + 2 == polygon.length ? 0 : curr + 2;
//        }
//
//        return out;
//    }

    public static boolean randomChance(float randomChance)
    {
        float random = randomFloat(0, 1);
        return random < randomChance;
    }

    public static FloatArray triangleFan(float[] polygon)
    {
        //Create the polygon passing a List of PolygonPoints
        ArrayList<PolygonPoint> verts = new ArrayList<>();
        for(int i = 0; i < polygon.length; i += 2)
            verts.add(new PolygonPoint(polygon[i], polygon[i + 1]));
        org.poly2tri.geometry.polygon.Polygon poly2TriPolygon = new org.poly2tri.geometry.polygon.Polygon(verts);
        poly2TriPolygon.clearTriangulation();
        //Next, proceed to calculate the triangulation of the polygon
        Poly2Tri.triangulate(poly2TriPolygon);
        //Finally, obtain the resulting triangles
        List<DelaunayTriangle> poly2TriTriangles = poly2TriPolygon.getTriangles();


        FloatArray out = triangles;
        out.clear();

        for(int i = 0; i < poly2TriTriangles.size(); i ++)
        {
            DelaunayTriangle triangle = poly2TriTriangles.get(i);
            for(int k = 0; k < triangle.points.length; k ++)
            {
                out.add(triangle.points[k].getXf());
                out.add(triangle.points[k].getYf());
            }
        }


//        int start = 0;
//        int curr = 2;
//        int prev = polygon.length - 2;
//        FloatArray out = triangles;
//        out.clear();
//
//        while(start != curr)
//        {
//            out.add(polygon[start]);
//            out.add(polygon[start + 1]);
//            out.add(polygon[curr]);
//            out.add(polygon[curr + 1]);
//            out.add(polygon[prev]);
//            out.add(polygon[prev + 1]);
//            prev = curr;
//            curr = curr + 2 == polygon.length ? 0 : curr + 2;
//        }

        return out;
    }

    public static FloatArray weighTriangles(FloatArray triangleVertices)
    {
        for(int i = 0; i < triangleVertices.size; i += 7)
        {
            float area = Math.abs(((triangleVertices.get(i) * (triangleVertices.get(i + 3) - triangleVertices.get(i + 5)) + triangleVertices.get(i + 2) * (triangleVertices.get(i + 5) - triangleVertices.get(i + 1)) + triangleVertices.get(i + 4) * (triangleVertices.get(i + 1) - triangleVertices.get(i + 3))))) / 2;
            if(i + 6 >= triangleVertices.size - 1)
                triangleVertices.add(area);
            else
                triangleVertices.insert(i + 6, area);
        }
        return triangleVertices;
    }

    public static void print(String string)
    {
        if(print)
            System.out.print(string);
    }
    public static void println(String string)
    {
        if(print)
            System.out.println(string);
    }
    public static void println()
    {
        if(print)
            System.out.println();
    }

    public static float radianAngleFix(float angle)
    {
        angle = (float) (((int) angle % (Math.PI * 2)) + (angle - ((int)angle)));
        if(angle > 0.0)
            return angle;
        else
            return (float) (angle + (Math.PI * 2));
    }

    public static float randomFloat(float minRange, float maxRange) { return minRange + random.nextFloat() * (maxRange - minRange); }

    public static int randomInt(int minRange, int maxRange) { return random.nextInt(maxRange - minRange + 1) + minRange; }

    public static float unprojectX(OrthographicCamera camera, float x)
    {
        unprojector.set(x, 0, 0);
        camera.unproject(unprojector);
        return unprojector.x;
    }

    public static float unprojectY(OrthographicCamera camera, float y)
    {
        unprojector.set(0, y, 0);
        camera.unproject(unprojector);
        return unprojector.y;
    }

    public static Vector3 unproject(Camera camera, float x, float y)
    {
        unprojector.set(x, y, 0);
        camera.unproject(unprojector);
        return unprojector;
    }

    public static Vector3 project(Camera camera, float x, float y)
    {
        unprojector.set(x, y, 0);
        camera.project(unprojector);
        return unprojector;
    }

    static Vector2 centerVector = new Vector2();
    static Vector2 startVector = new Vector2();
    static Vector2 endVector = new Vector2();
    public static boolean overlaps(float[] vertices, float x, float y, float radius)
    {
        centerVector.set(x, y);
        float squareRadius = radius * radius;
        for (int i = 0; i < vertices.length; i += 2)
        {
            if (i == 0)
                startVector.set(vertices[vertices.length - 2], vertices[vertices.length - 1]);
            else
                startVector.set(vertices[i - 2], vertices[i - 1]);
            endVector.set(vertices[i], vertices[i + 1]);
            if (Intersector.intersectSegmentCircle(startVector, endVector, centerVector, squareRadius))
                return true;
        }
        return Intersector.isPointInPolygon(vertices, 0, vertices.length, x, y);
    }

    public static boolean areTwoNumbersWithinNumbers(float num1, float num2, float num3, float num4)
    {
        return num1 >= num3 && num1 <= num4 && num2 >= num3 && num2 <= num4;
    }

    public static boolean isLayerGround(Layer layer)
    {
        if(layer == null)
            return false;
        PropertyField groundProperty = Utils.getPropertyField(layer.properties, "ground");
        return groundProperty != null;
    }

    /** Gets the perspective property from the map. Gets it from the layer if present there. */
    public static FieldFieldPropertyValuePropertyField getSkewPerspectiveProperty(Map map, Layer layer)
    {
        PropertyField mapSkew = Utils.getPropertyField(map.propertyMenu.mapPropertyPanel.properties, "skew");
        PropertyField layerSkew = null;
        if(layer != null)
            layerSkew = Utils.getPropertyField(layer.properties, "skew");
        if(layerSkew == null)
            return (FieldFieldPropertyValuePropertyField) mapSkew;
        return (FieldFieldPropertyValuePropertyField) layerSkew;
    }
    /** Gets the perspective property from the map. Gets it from the layer if present there. */
    public static FieldFieldPropertyValuePropertyField getAntiDepthPerspectiveProperty(Map map, Layer layer)
    {
        PropertyField mapAntiDepth = Utils.getPropertyField(map.propertyMenu.mapPropertyPanel.properties, "antiDepth");
        PropertyField layerAntiDepth = null;
        if(layer != null)
            layerAntiDepth = Utils.getPropertyField(layer.properties, "antiDepth");
        if(layerAntiDepth == null)
            return (FieldFieldPropertyValuePropertyField) mapAntiDepth;
        return (FieldFieldPropertyValuePropertyField) layerAntiDepth;
    }

    public static int setBit(int bit, int target)
    {
        // Create mask
        int mask = 1 << bit;
        // Set bit
        return target | mask;
    }

    public static int turnBitOn(int value, int pos)
    {
        return value | (1 << pos);
    }
    public static int turnBitOff(int value, int pos)
    {
        return value & ~(1 << pos);
    }


    public static Vector2 setCenterOrigin(float x, float y)
    {
        centerOrigin.set(x, y);
        return centerOrigin;
    }

    public static boolean coordInRect(float x, float y, float rectX, float rectY, int rectWidth, int rectHeight)
    {
        return x >= rectX && x <= rectX + rectWidth && y >= rectY && y <= rectY + rectHeight;
    }

    public static void centerPrint(SpriteBatch batch, String string, float x, float y)
    {
        EditorAssets.getGlyph().setText(EditorAssets.getFont(), string);
        EditorAssets.getFont().getData().setScale(1, 1);
        float w = EditorAssets.getGlyph().width;
        float h = EditorAssets.getGlyph().height;
        EditorAssets.getFont().draw(batch, string, x - w / 2, y + h / 2, w, Align.center, false);
        EditorAssets.getFont().getData().setScale(1, 1);
    }

    public static boolean isSpriteSheetInFolder(String path)
    {
        File tempFile = new File(path + ".atlas");
        boolean exists = tempFile.exists();

        boolean valid;

        conditional:
        if(exists)
            valid = true;
        else
            valid = false;

        return valid;
    }

    public static boolean canSpriteSheetBeCreated(Map map, String path)
    {
        File tempFile = new File(path + ".atlas");
        boolean exists = tempFile.exists();

        boolean valid;

        conditional:
        if(exists)
        {
            for(int i = 0; i < map.spriteMenu.spriteSheets.size; i ++)
            {
                SpriteSheet spriteSheet = map.spriteMenu.spriteSheets.get(i);
                if(spriteSheet.name.equals(path))
                {
                    valid = false;
                    break conditional;
                }
            }
            valid = true;
        }
        else
            valid = false;

        return valid;
    }

    public static float getDistance(float x1, float x2, float y1, float y2)
    {
        return (float) Math.hypot(x1-x2, y1-y2);
    }

    public static boolean canBuildFenceFromSelectedSpriteTools(Map map)
    {
        boolean hasFencePost = false;
        boolean hasConnector = false;
        for (int k = 0; k < map.getAllSelectedSpriteTools().size; k++)
        {
            SpriteTool spriteTool1 = map.getAllSelectedSpriteTools().get(k);
            if (!spriteTool1.hasAttachedMapObjects())
                hasConnector = true;
            else {
                boolean postTest = false;
                for (int i = 0; i < spriteTool1.attachedMapObjectManagers.size; i++) {
                    AttachedMapObjectManager attachedMapObjectManager = spriteTool1.attachedMapObjectManagers.get(i);
                    if (Utils.getPropertyField(attachedMapObjectManager.properties, "fenceStart") != null && Utils.getPropertyField(attachedMapObjectManager.properties, "fenceEnd") != null)
                        hasFencePost = true;
                    else
                        postTest = true;
                }
                if(postTest)
                    hasConnector = true;
            }
        }
        if (!hasFencePost)
            return false;
        return true;
    }

    public static boolean hasPropertyFieldWithValue(Array<PropertyField> properties, String propertyName, String valueName)
    {
        for(int i = 0; i < properties.size; i ++)
        {
            PropertyField property = properties.get(i);
            if(property instanceof FieldFieldPropertyValuePropertyField)
            {
                FieldFieldPropertyValuePropertyField fieldFieldProperty = (FieldFieldPropertyValuePropertyField) property;
                if(fieldFieldProperty.getProperty() != null && fieldFieldProperty.getProperty().equals(propertyName) && fieldFieldProperty.getValue().equals(valueName))
                    return true;
            }
            if(property instanceof LabelFieldPropertyValuePropertyField)
            {
                LabelFieldPropertyValuePropertyField labelFieldProperty = (LabelFieldPropertyValuePropertyField) property;
                if(labelFieldProperty.getProperty() != null && labelFieldProperty.getProperty().equals(propertyName) && labelFieldProperty.getValue().equals(valueName))
                    return true;
            }
        }
        return false;
    }

    public static float snapSpriteVerticeX(float coordsX, float coordsY, Map map)
    {
        if(!Gdx.input.isKeyPressed(Input.Keys.S))
            return coordsX;
        if(map.selectedLayer == null)
            return coordsX;
        if(!(map.selectedLayer instanceof SpriteLayer))
            return coordsX;

        float smallestDistance = Float.MAX_VALUE;
        float snapDistance = 5;
        SpriteLayer layer = (SpriteLayer) map.selectedLayer;
        float closestX = coordsX;
        for(int k = 0; k < layer.children.size; k ++)
        {
            MapSprite child = layer.children.get(k);

            float[] spriteVertices = child.sprite.getVertices();
            float x1 = spriteVertices[SpriteBatch.X2] + map.cameraX + child.x1Offset;
            float x2 = spriteVertices[SpriteBatch.X3] + map.cameraX + child.x2Offset;
            float x3 = spriteVertices[SpriteBatch.X4] + map.cameraX + child.x3Offset;
            float x4 = spriteVertices[SpriteBatch.X1] + map.cameraX + child.x4Offset;
            float y1 = spriteVertices[SpriteBatch.Y2] + map.cameraY + child.y1Offset;
            float y2 = spriteVertices[SpriteBatch.Y3] + map.cameraY + child.y2Offset;
            float y3 = spriteVertices[SpriteBatch.Y4] + map.cameraY + child.y3Offset;
            float y4 = spriteVertices[SpriteBatch.Y1] + map.cameraY + child.y4Offset;

            float distance1 = Utils.getDistance(coordsX, x1, coordsY, y1);
            float distance2 = Utils.getDistance(coordsX, x2, coordsY, y2);
            float distance3 = Utils.getDistance(coordsX, x3, coordsY, y3);
            float distance4 = Utils.getDistance(coordsX, x4, coordsY, y4);
            if(distance1 < snapDistance && distance1 < smallestDistance)
            {
                closestX = x1;
                smallestDistance = distance1;
            }
            if(distance2 < snapDistance && distance2 < smallestDistance)
            {
                closestX = x2;
                smallestDistance = distance2;
            }
            if(distance3 < snapDistance && distance3 < smallestDistance)
            {
                closestX = x3;
                smallestDistance = distance3;
            }
            if(distance4 < snapDistance && distance4 < smallestDistance)
            {
                closestX = x4;
                smallestDistance = distance4;
            }
        }
        return closestX;
    }

    public static float snapSpriteVerticeY(float coordsX, float coordsY, Map map)
    {
        if(!Gdx.input.isKeyPressed(Input.Keys.S))
            return coordsY;
        if(map.selectedLayer == null)
            return coordsY;
        if(!(map.selectedLayer instanceof SpriteLayer))
            return coordsY;

        float smallestDistance = Float.MAX_VALUE;
        float snapDistance = 5;
        SpriteLayer layer = (SpriteLayer) map.selectedLayer;
        float closestY = coordsY;
        for(int k = 0; k < layer.children.size; k ++)
        {
            MapSprite child = layer.children.get(k);

            float[] spriteVertices = child.sprite.getVertices();
            float x1 = spriteVertices[SpriteBatch.X2] + map.cameraX + child.x1Offset;
            float x2 = spriteVertices[SpriteBatch.X3] + map.cameraX + child.x2Offset;
            float x3 = spriteVertices[SpriteBatch.X4] + map.cameraX + child.x3Offset;
            float x4 = spriteVertices[SpriteBatch.X1] + map.cameraX + child.x4Offset;
            float y1 = spriteVertices[SpriteBatch.Y2] + map.cameraY + child.y1Offset;
            float y2 = spriteVertices[SpriteBatch.Y3] + map.cameraY + child.y2Offset;
            float y3 = spriteVertices[SpriteBatch.Y4] + map.cameraY + child.y3Offset;
            float y4 = spriteVertices[SpriteBatch.Y1] + map.cameraY + child.y4Offset;

            float distance1 = Utils.getDistance(coordsX, x1, coordsY, y1);
            float distance2 = Utils.getDistance(coordsX, x2, coordsY, y2);
            float distance3 = Utils.getDistance(coordsX, x3, coordsY, y3);
            float distance4 = Utils.getDistance(coordsX, x4, coordsY, y4);
            if(distance1 < snapDistance && distance1 < smallestDistance)
            {
                closestY = y1;
                smallestDistance = distance1;
            }
            if(distance2 < snapDistance && distance2 < smallestDistance)
            {
                closestY = y2;
                smallestDistance = distance2;
            }
            if(distance3 < snapDistance && distance3 < smallestDistance)
            {
                closestY = y3;
                smallestDistance = distance3;
            }
            if(distance4 < snapDistance && distance4 < smallestDistance)
            {
                closestY = y4;
                smallestDistance = distance4;
            }
        }
        return closestY;
    }

    public static LayerChild snapObject(float coordsX, float coordsY, Map map)
    {
        if(!Gdx.input.isKeyPressed(Input.Keys.S))
            return null;

        float smallestDistance = Float.MAX_VALUE;
        LayerChild closestChild = null;
        float snapDistance = 5;
        for(int i = 0; i < map.layers.size; i ++)
        {
            Layer layer = map.layers.get(i);
            for(int k = 0; k < layer.children.size; k ++)
            {
                LayerChild child = (LayerChild) layer.children.get(k);
                float distance = Utils.getDistance(coordsX, child.x, coordsY, child.y);
                if(distance < snapDistance)
                {
                    if(distance < smallestDistance)
                    {
                        closestChild = child;
                        smallestDistance = distance;
                    }
                }
            }
        }
        return closestChild;
    }

    public static float perpendicularCoordX(float coordsX, float coordsY, Map map)
    {
        if(!Gdx.input.isKeyPressed(Input.Keys.A))
            return coordsX;
        if(map.lastFencePlaced == null)
            return coordsX;
        float lastPlacedX = map.lastFencePlaced.x + map.lastFencePlaced.width / 2f;
        float lastPlacedY = map.lastFencePlaced.y + map.lastFencePlaced.height / 2f;
        float lastAngle = MathUtils.degreesToRadians * map.lastFencePlacedAngle;

        // Direction vector of the last placed segment
        float dirX = (float) Math.cos(lastAngle);
        float dirY = (float) Math.sin(lastAngle);

        // Perpendicular vector to the direction (90 degrees rotation)
        float perpX = -dirY;
        float perpY = dirX;

        // Vector from last placed point to current point
        float vecX = coordsX - lastPlacedX;
        float vecY = coordsY - lastPlacedY;

        // Projection onto the perpendicular direction
        float dotProductPerpendicular = vecX * perpX + vecY * perpY;
        float closestPerpX = lastPlacedX + dotProductPerpendicular * perpX;
        float closestPerpY = lastPlacedY + dotProductPerpendicular * perpY;

        // Projection onto the parallel direction (0° or 180°)
        float dotProductParallel = vecX * dirX + vecY * dirY;
        float closestParallelX = lastPlacedX + dotProductParallel * dirX;
        float closestParallelY = lastPlacedY + dotProductParallel * dirY;

        // Calculate the distances to the current point
        float distancePerpendicular = getDistance(coordsX, closestPerpX, coordsY, closestPerpY);
        float distanceParallel = getDistance(coordsX, closestParallelX, coordsY, closestParallelY);

        // Return the X coordinate of the closest point
        if (distancePerpendicular < distanceParallel) {
            return closestPerpX;
        } else {
            return closestParallelX;
        }
    }

    public static float perpendicularCoordY(float coordsX, float coordsY, Map map)
    {
        if(!Gdx.input.isKeyPressed(Input.Keys.A))
            return coordsY;
        if(map.lastFencePlaced == null)
            return coordsY;
        float lastPlacedX = map.lastFencePlaced.x + map.lastFencePlaced.width / 2f;
        float lastPlacedY = map.lastFencePlaced.y + map.lastFencePlaced.height / 2f;
        float lastAngle = MathUtils.degreesToRadians * map.lastFencePlacedAngle;

        // Direction vector of the last placed segment
        float dirX = (float) Math.cos(lastAngle);
        float dirY = (float) Math.sin(lastAngle);

        // Perpendicular vector to the direction (90 degrees rotation)
        float perpX = -dirY;
        float perpY = dirX;

        // Vector from last placed point to current point
        float vecX = coordsX - lastPlacedX;
        float vecY = coordsY - lastPlacedY;

        // Projection onto the perpendicular direction
        float dotProductPerpendicular = vecX * perpX + vecY * perpY;
        float closestPerpX = lastPlacedX + dotProductPerpendicular * perpX;
        float closestPerpY = lastPlacedY + dotProductPerpendicular * perpY;

        // Projection onto the parallel direction (0° or 180°)
        float dotProductParallel = vecX * dirX + vecY * dirY;
        float closestParallelX = lastPlacedX + dotProductParallel * dirX;
        float closestParallelY = lastPlacedY + dotProductParallel * dirY;

        // Calculate the distances to the current point
        float distancePerpendicular = getDistance(coordsX, closestPerpX, coordsY, closestPerpY);
        float distanceParallel = getDistance(coordsX, closestParallelX, coordsY, closestParallelY);

        // Return the X coordinate of the closest point
        if (distancePerpendicular < distanceParallel) {
            return closestPerpY;
        } else {
            return closestParallelY;
        }
    }

    public static float distanceCoordX(float coordsX, float coordsY, Map map)
    {
        if (!Gdx.input.isKeyPressed(Input.Keys.W))
            return coordsX;
        if(map.lastFencePlaced == null)
            return coordsX;

        float lastPlacedX = map.lastFencePlaced.x + map.lastFencePlaced.width / 2f;
        float lastPlacedY = map.lastFencePlaced.y + map.lastFencePlaced.height / 2f;
        float angle = MathUtils.degreesToRadians * Utils.getAngleDegree(lastPlacedX, lastPlacedY, coordsX, coordsY);
        float lastDistance = map.lastFencePlacedDistance;

        return lastPlacedX + (MathUtils.cos(angle) * lastDistance);
    }

    public static float distanceCoordY(float coordsX, float coordsY, Map map)
    {
        if (!Gdx.input.isKeyPressed(Input.Keys.W))
            return coordsY;
        if(map.lastFencePlaced == null)
            return coordsY;

        float lastPlacedX = map.lastFencePlaced.x + map.lastFencePlaced.width / 2f;
        float lastPlacedY = map.lastFencePlaced.y + map.lastFencePlaced.height / 2f;
        float angle = MathUtils.degreesToRadians * Utils.getAngleDegree(lastPlacedX, lastPlacedY, coordsX, coordsY);
        float lastDistance = map.lastFencePlacedDistance;

        return lastPlacedY + (MathUtils.sin(angle) * lastDistance);
    }

    public LightPropertyField getLockedLightField(Array<PropertyField> lockedProperties)
    {
        for(int i = 0; i < lockedProperties.size; i ++)
        {
            PropertyField propertyField = lockedProperties.get(i);
            if (propertyField instanceof LightPropertyField)
                return (LightPropertyField) propertyField;
        }
        return null;
    }

    public static ColorPropertyField getLockedColorField(String property, Array<PropertyField> lockedProperties)
    {
        for(int i = 0; i < lockedProperties.size; i ++)
        {
            PropertyField propertyField = lockedProperties.get(i);
            if (propertyField instanceof ColorPropertyField)
            {
                ColorPropertyField colorPropertyField = (ColorPropertyField) propertyField;
                if(colorPropertyField.property.getText().toString().equals(property))
                    return colorPropertyField;
            }
        }
        return null;
    }

    public static OpaqueColorPropertyField getLockedOpaqueColorField(String property, Array<PropertyField> lockedProperties)
    {
        for(int i = 0; i < lockedProperties.size; i ++)
        {
            PropertyField propertyField = lockedProperties.get(i);
            if (propertyField instanceof OpaqueColorPropertyField && !(propertyField instanceof ColorPropertyField))
            {
                OpaqueColorPropertyField opaqueColorPropertyField = (OpaqueColorPropertyField) propertyField;
                if(opaqueColorPropertyField.property.getText().toString().equals(property))
                    return opaqueColorPropertyField;
            }
        }
        return null;
    }

    public static LightPropertyField getLightField(Array<PropertyField> properties)
    {
        for(int i = 0; i < properties.size; i ++)
        {
            PropertyField propertyField = properties.get(i);
            if (propertyField instanceof LightPropertyField)
                return (LightPropertyField) propertyField;
        }
        return null;
    }

    public static boolean isFileToolThisType(BridgeBuilder editor, Tools toolType)
    {
        Tool fileTool = editor.getFileTool();
        if(toolType == null && fileTool == null)
            return true;
        if(fileTool == null)
            return false;
        if(fileTool.tool == toolType)
            return true;
        return false;
    }

    public static float matchAngles(float angle1, float angle2) {
        // Normalize angles to the range [0, 360)
        angle1 = (angle1 % 360 + 360) % 360;
        angle2 = (angle2 % 360 + 360) % 360;

        // Calculate the absolute difference
        float difference = Math.abs(angle1 - angle2);

        // Normalize the difference to the range [0, 180]
        if (difference > 180) {
            difference = 360 - difference;
        }

        // Convert the difference to a value between 0 and 1
        return 1 - (difference / 180f);
    }

    public static float getAngleDegree(float originX, float originY, float targetX, float targetY)
    {
        return Utils.degreeAngleFix((float) Math.toDegrees((float) Math.atan2((targetY - originY), (targetX - originX))));
    }

    public static boolean containsEquivalentPropertyField (Array<PropertyField> propertyFields, PropertyField propertyField) {
        int i = propertyFields.size - 1;
        {
            while (i >= 0)
                if (propertyField.equals(propertyFields.get(i--))) return true;
        }
        return false;
    }

    public static int indexOfEquivalentProperty (Array<PropertyField> propertyFields, PropertyField propertyField) {
        for (int i = 0, n = propertyFields.size; i < n; i++)
            if (propertyField.equals(propertyFields.get(i))) return i;
        return -1;
    }

    public static LabelFieldPropertyValuePropertyField getLockedPropertyField(Array<PropertyField> lockedProperties, String propertyName)
    {
        for(int i = 0; i < lockedProperties.size; i ++)
        {
            PropertyField property = lockedProperties.get(i);
            if(property instanceof LabelFieldPropertyValuePropertyField)
            {
                LabelFieldPropertyValuePropertyField labelFieldProperty = (LabelFieldPropertyValuePropertyField) property;
                if(labelFieldProperty.getProperty() != null && labelFieldProperty.getProperty().equals(propertyName))
                    return labelFieldProperty;
            }
        }
        return null;
    }

    public static PropertyField getPropertyField(Array<PropertyField> properties, Array<PropertyField> lockedProperties, String propertyName)
    {
        for(int i = 0; i < lockedProperties.size; i ++)
        {
            PropertyField property = lockedProperties.get(i);
            if(property instanceof FieldFieldPropertyValuePropertyField)
            {
                FieldFieldPropertyValuePropertyField fieldFieldProperty = (FieldFieldPropertyValuePropertyField) property;
                if(fieldFieldProperty.getProperty() != null && fieldFieldProperty.getProperty().equals(propertyName))
                    return fieldFieldProperty;
            }
            if(property instanceof LabelFieldPropertyValuePropertyField)
            {
                LabelFieldPropertyValuePropertyField labelFieldProperty = (LabelFieldPropertyValuePropertyField) property;
                if(labelFieldProperty.getProperty() != null && labelFieldProperty.getProperty().equals(propertyName))
                    return labelFieldProperty;
            }
        }

        for(int i = 0; i < properties.size; i ++)
        {
            PropertyField property = properties.get(i);
            if(property instanceof FieldFieldPropertyValuePropertyField)
            {
                FieldFieldPropertyValuePropertyField fieldFieldProperty = (FieldFieldPropertyValuePropertyField) property;
                if(fieldFieldProperty.getProperty() != null && fieldFieldProperty.getProperty().equals(propertyName))
                    return fieldFieldProperty;
            }
            if(property instanceof LabelFieldPropertyValuePropertyField)
            {
                LabelFieldPropertyValuePropertyField labelFieldProperty = (LabelFieldPropertyValuePropertyField) property;
                if(labelFieldProperty.getProperty() != null && labelFieldProperty.getProperty().equals(propertyName))
                    return labelFieldProperty;
            }
        }
        return null;
    }

    public static MapObject getAttachedMapObjectWithProperty(MapSprite mapSprite, String propertyName)
    {
        if(mapSprite.attachedMapObjects == null)
            return null;
        for(int i = 0; i < mapSprite.attachedMapObjects.size; i ++)
        {
            MapObject mapObject = mapSprite.attachedMapObjects.get(i);
            if(getPropertyField(mapObject.properties, propertyName) != null)
                return mapObject;
        }
        return null;
    }

    public static PropertyField getPropertyField(Array<PropertyField> properties, String propertyName)
    {
        for(int i = 0; i < properties.size; i ++)
        {
            PropertyField property = properties.get(i);
            if(property instanceof FieldFieldPropertyValuePropertyField)
            {
                FieldFieldPropertyValuePropertyField fieldFieldProperty = (FieldFieldPropertyValuePropertyField) property;
                if(fieldFieldProperty.getProperty() != null && fieldFieldProperty.getProperty().equals(propertyName))
                    return fieldFieldProperty;
            }
            if(property instanceof LabelFieldPropertyValuePropertyField)
            {
                LabelFieldPropertyValuePropertyField labelFieldProperty = (LabelFieldPropertyValuePropertyField) property;
                if(labelFieldProperty.getProperty() != null && labelFieldProperty.getProperty().equals(propertyName))
                    return labelFieldProperty;
            }
        }
        return null;
    }

    public static PropertyField getPropertyField(MapSprite mapSprite, String propertyName)
    {
        if(Utils.containsProperty(mapSprite.instanceSpecificProperties, propertyName))
            return Utils.getPropertyField(mapSprite.instanceSpecificProperties, propertyName);
        return getPropertyField(mapSprite.tool.properties, propertyName);
    }

    public static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }
}
