package com.bamboo.bridgebuilder;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.bamboo.bridgebuilder.ui.fileMenu.Tool;
import com.bamboo.bridgebuilder.ui.fileMenu.Tools;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.*;

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

    public static float unprojectX(OrthographicCamera camera, float x, float y)
    {
        unprojector.set(x, y, 0);
        camera.unproject(unprojector);
        return unprojector.x;
    }

    public static float unprojectY(OrthographicCamera camera, float x, float y)
    {
        unprojector.set(x, y, 0);
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
}
