package com.bamboo.bridgebuilder;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.bamboo.bridgebuilder.map.*;
import com.bamboo.bridgebuilder.ui.fileMenu.Tool;
import com.bamboo.bridgebuilder.ui.fileMenu.Tools;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.*;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteSheet;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

import java.io.File;

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

    public static FloatArray triangleFan(float[] polygon)
    {
        int start = 0;
        int curr = 2;
        int prev = polygon.length - 2;
        FloatArray out = triangles;
        out.clear();

        while(start != curr)
        {
            out.add(polygon[start]);
            out.add(polygon[start + 1]);
            out.add(polygon[curr]);
            out.add(polygon[curr + 1]);
            out.add(polygon[prev]);
            out.add(polygon[prev + 1]);
            prev = curr;
            curr = curr + 2 == polygon.length ? 0 : curr + 2;
        }

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

    public static boolean areTwoNumbersWithinNumbers(float num1, float num2, float num3, float num4)
    {
        return num1 >= num3 && num1 <= num4 && num2 >= num3 && num2 <= num4;
    }

    public static boolean doesLayerHavePerspective(Map map, Layer layer)
    {
        Array<PropertyField> mapProperties = map.propertyMenu.mapPropertyPanel.properties;
        PropertyField mapSkew = Utils.getPropertyField(mapProperties, "skew");
        PropertyField mapAntiDepth = Utils.getPropertyField(mapProperties, "antiDepth");

        PropertyField layerDisablePerspective = null;
        PropertyField layerSkew = null;
        PropertyField layerAntiDepth = null;

        if(layer != null)
        {
            Array<PropertyField> layerProperties = layer.properties;
            layerDisablePerspective = Utils.getPropertyField(layerProperties, "disablePerspective");
            layerSkew = Utils.getPropertyField(layerProperties, "skew");
            layerAntiDepth = Utils.getPropertyField(layerProperties, "antiDepth");
        }

        if(layerDisablePerspective != null)
            return false;
        if(mapSkew == null && mapAntiDepth == null)
        {
            if(layerSkew == null && layerAntiDepth == null)
                return false;
            return true;
        }
        return true;
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

    public static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }
}
