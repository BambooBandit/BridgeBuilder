package com.bamboo.bridgebuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.map.Layer;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.ui.fileMenu.Tool;
import com.bamboo.bridgebuilder.ui.fileMenu.Tools;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.*;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteSheet;

import java.io.File;

public class Utils
{
    private static RandomXS128 random = new RandomXS128();
    private static Vector3 unprojector = new Vector3();
    public static Vector2 centerOrigin = new Vector2();
    public static Vector2 positionDifference = new Vector2();
    public static Vector2 spritePositionCopy = new Vector2();
    public static boolean print = true;

    public static float[] boxSelectCommandVertices = new float[8];

    public static float degreeAngleFix(float angle)
    {
        angle = ((int) angle % 360) + (angle - ((int)angle));
        if(angle > 0.0)
            return angle;
        else
            return angle + 360f;
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

    public static ColorPropertyField getLockedColorField(Array<PropertyField> lockedProperties)
    {
        for(int i = 0; i < lockedProperties.size; i ++)
        {
            PropertyField propertyField = lockedProperties.get(i);
            if (propertyField instanceof ColorPropertyField)
                return (ColorPropertyField) propertyField;
        }
        return null;
    }

    public static OpaqueColorPropertyField getLockedOpaqueColorField(Array<PropertyField> lockedProperties)
    {
        for(int i = 0; i < lockedProperties.size; i ++)
        {
            PropertyField propertyField = lockedProperties.get(i);
            if (propertyField instanceof OpaqueColorPropertyField && !(propertyField instanceof ColorPropertyField))
                return (OpaqueColorPropertyField) propertyField;
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

    public static Vector3 projector = new Vector3();
    public static OrthographicCamera perspectiveCamera = new OrthographicCamera();
    public static Vector3 projectWorldToPerspective(Map map, Layer layer, OrthographicCamera camera, float x, float y)
    {
        projector.set(x, y, 0);

        updatePerspectiveCamera(map, layer, camera);

        perspectiveCamera.project(projector);
        float xP = projector.x;
        float yP = Gdx.graphics.getHeight() - projector.y;
        projector.set(xP, yP, 0);
        camera.unproject(projector);

        return projector;
    }

    private static void updatePerspectiveCamera(Map map, Layer layer, OrthographicCamera camera)
    {
        perspectiveCamera.viewportWidth = camera.viewportWidth;
        perspectiveCamera.viewportHeight = camera.viewportHeight;
        perspectiveCamera.position.set(camera.position);
        perspectiveCamera.zoom = camera.zoom;
        perspectiveCamera.update();

        float skew = 0;
        float antiDepth = 0;
        try
        {
            skew = Float.parseFloat(Utils.getSkewPerspectiveProperty(map, layer).value.getText());
            antiDepth = Float.parseFloat(Utils.getAntiDepthPerspectiveProperty(map, layer).value.getText());
        }catch (Exception e){}

        float[] m = perspectiveCamera.combined.getValues();
        if (antiDepth >= .1f)
            skew /= antiDepth * 15;
        m[Matrix4.M31] += skew;
        m[Matrix4.M11] += perspectiveCamera.position.y / ((-10f * perspectiveCamera.zoom) / skew) - ((.097f * antiDepth) / (antiDepth + .086f));
        perspectiveCamera.invProjectionView.set(perspectiveCamera.combined);
        Matrix4.inv(perspectiveCamera.invProjectionView.val);
        perspectiveCamera.frustum.update(perspectiveCamera.invProjectionView);
    }

    private static void setPerspectiveZoom(float zoom)
    {
        perspectiveCamera.zoom = zoom;
        perspectiveCamera.update();
    }

    public static float getPerspectiveScaleFactor(Map map, Layer layer, OrthographicCamera camera, float y)
    {
        Vector3 p = projectWorldToPerspective(map, layer, camera, 0, y);
        float xBotLeft = p.x;
        float yBotLeft = p.y;
        p = projectWorldToPerspective(map, layer, camera, 1, y);
        float xBotRight = p.x;
        float yBotRight = p.y;
        p = projectWorldToPerspective(map, layer, camera, 0, 1);
        float xTopLeft = p.x;
        float yTopLeft = p.y;
        p = projectWorldToPerspective(map, layer, camera, 1, 1);
        float xTopRight = p.x;
        float yTopRight = p.y;
        float botDist = Utils.getDistance(xBotLeft, xBotRight, yBotLeft, yBotRight);
        float topDist = Utils.getDistance(xTopLeft, xTopRight, yTopLeft, yTopRight);

        return botDist / topDist;
    }
}
