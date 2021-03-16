package com.bamboo.bridgebuilder.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bamboo.bridgebuilder.Utils;

public class Perspective
{
    public float skew;
    public OrthographicCamera camera;
    public OrthographicCamera perspectiveCamera;
    private Vector3 projector;
    public Vector3 vanishingPoint;
    public Vector2 vanishingPointInScreenCoords;
    private float screenWidth;
    private float screenHeight;
    public float cameraHeight;
    public boolean useSkewWithHeight;
    public Map map;
    public Layer layer;

    public Perspective(Map map, Layer layer, OrthographicCamera camera, float cameraHeight, boolean useSkewWithHeight)
    {
        this.map = map;
        this.layer = layer;

        this.cameraHeight = cameraHeight;
        this.useSkewWithHeight = useSkewWithHeight;

        this.camera = camera;
        this.perspectiveCamera = new OrthographicCamera();

        this.projector = new Vector3();

        this.vanishingPoint = new Vector3();
        this.vanishingPointInScreenCoords = new Vector2();
    }

    public void setCamera(OrthographicCamera camera)
    {
        this.camera = camera;
    }

    public void matchOrthogonalCamera()
    {
        perspectiveCamera.up.set(0, 1, 0);
        perspectiveCamera.direction.set(0, 0, -1);
        perspectiveCamera.viewportWidth = camera.viewportWidth;
        perspectiveCamera.viewportHeight = camera.viewportHeight;
        perspectiveCamera.position.set(camera.position);
        perspectiveCamera.zoom = camera.zoom;
//        perspectiveCamera.rotate(GameWorld.cameraRotation);
        perspectiveCamera.update();
    }

    public void setZoom(float zoom)
    {
        perspectiveCamera.zoom = zoom;
        perspectiveCamera.update();
    }

    public void update()
    {
        matchOrthogonalCamera();
        setZoom(map.zoom);

        if(Gdx.graphics.getWidth() != 0)
            this.screenWidth = Gdx.graphics.getWidth();
        if(Gdx.graphics.getHeight() != 0)
            this.screenHeight = Gdx.graphics.getHeight();

        float cameraPerspectiveZoom = this.map.perspectiveZoom;
        this.skew = (float) (cameraPerspectiveZoom / (.25f + Math.pow(perspectiveCamera.position.y, .25f)));

        float[] m = perspectiveCamera.combined.getValues();
//        if (antiDepth >= .1f)
//            skew /= antiDepth * 15;
//        if(cameraHeight > 0 && useSkewWithHeight)
//            skew /= (Math.pow(cameraHeight, .045f));
        m[Matrix4.M31] += skew;
        m[Matrix4.M11] += 20 / ((-10f * perspectiveCamera.zoom) / skew);
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
            m[Matrix4.M01] = 2 * skew / perspectiveCamera.zoom;
        else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            m[Matrix4.M01] = -2 * skew / perspectiveCamera.zoom;
        perspectiveCamera.near = -100f;
        if(!this.useSkewWithHeight)
            m[Matrix4.M13] += (cameraHeight / perspectiveCamera.zoom);
        else
            m[Matrix4.M13] += ((cameraHeight * ((skew) / perspectiveCamera.zoom)));
        m[Matrix4.M13] += ((cameraHeight / 8f) / ((Math.pow(skew, 1.25f) * 21f) + 1)) / perspectiveCamera.zoom;
        perspectiveCamera.invProjectionView.set(perspectiveCamera.combined);
        Matrix4.inv(perspectiveCamera.invProjectionView.val);
        perspectiveCamera.frustum.update(perspectiveCamera.invProjectionView);

        projectWorldToPerspectiveSky(layer.width / 2f, 99999);
        this.vanishingPoint.set(projector.x, projector.y, 0);
        Vector3 coords = Utils.project(camera, this.vanishingPoint.x, this.vanishingPoint.y);
        this.vanishingPointInScreenCoords.set(coords.x, coords.y);
    }

    public Vector3 projectScreenToPerspective(float x, float y)
    {
        float vanishingPointInScreenY = screenHeight - vanishingPointInScreenCoords.y;
        if(y < vanishingPointInScreenY)
            y = vanishingPointInScreenY;
        projector.x = x;
        projector.y = y;
        projector.z = 0;

        unproject(perspectiveCamera, projector);

        projector.x += this.map.cameraX;
        projector.y += this.map.cameraY;

        return projector;
    }

    /** Similar to projectWorldToPerspectiveSky, but doesn't fix sprites that are too south of the camera. */
    public Vector3 projectWorldToPerspective(float x, float y)
    {
        projector.x = x - this.map.cameraX;
        projector.y = y - this.map.cameraY;
        projector.z = 0;

        project(perspectiveCamera, projector);
        if(projector.x != projector.x && projector.y != projector.y) // NaN
        {
            projector.x = x;
            projector.y = y;
            return projector;
        }

        float xP = projector.x;
        float yP = screenHeight - projector.y;
        projector.x = xP;
        projector.y = yP;
        projector.z = 0;
        float vanishingPointInScreenY = screenHeight - vanishingPointInScreenCoords.y;
        boolean overflow = false;
        if(projector.y < vanishingPointInScreenY)
            overflow = true;
        unproject(camera, projector);

        float cap = this.map.cameraY - .95f / skew;
        if(overflow && y < cap)
        {
            float excess = (float) Math.pow(((cap - y) * .5f), 3.5f);
            if(excess > 1000)
            {
                projector.y = -200000;
                return projector;
            }
            projector.x *= -excess;
            projector.y *= -excess;
        }

        return projector;
    }

    public Vector3 projectWorldToPerspectiveSky(float x, float y)
    {
        projector.x = x - this.map.cameraX;
        projector.y = y - this.map.cameraY;
        projector.z = 0;

        float cap = this.map.cameraY - .95f / skew;
        if(y < cap)
        {
            projector.y = -20000;
            return projector;
        }

        project(perspectiveCamera, projector);
        if(projector.x != projector.x && projector.y != projector.y) // NaN
        {
            projector.x = x;
            projector.y = y;
            return projector;
        }

        float xP = projector.x;
        float yP = screenHeight - projector.y;
        projector.x = xP;
        projector.y = yP;
        projector.z = 0;
        unproject(camera, projector);

        return projector;
    }

    public float getScaleFactor(float y)
    {
        Vector3 p = projectWorldToPerspective(0, y);
        float xBotLeft = p.x;
        p = projectWorldToPerspective(100, y);
        float xBotRight = p.x;
        float botDist = xBotRight - xBotLeft;

        float scale = botDist / 100f;

        if(scale < 0)
            scale = 1;

        return scale;
    }

    public float getScaleFactorSky(float y)
    {
        Vector3 p = projectWorldToPerspectiveSky(0, y);
        float xBotLeft = p.x;
        p = projectWorldToPerspectiveSky(100, y);
        float xBotRight = p.x;
        float botDist = xBotRight - xBotLeft;

        float scale = botDist / 100f;

        if(scale < 0)
            scale = 1;

        return scale;
    }

    public float getScreenScaleFactor(float y)
    {
        Vector3 p = projectScreenToPerspective(0, y);
        float xBotLeft = p.x;
        p = projectScreenToPerspective(100, y);
        float xBotRight = p.x;
        float botDist = xBotRight - xBotLeft;

        float scale = botDist / 100f;

        if(scale < 0)
            scale = 1;

        return scale;
    }

    /** project an angle in radians to perspective. */
    public float getAngle(float angle, float x, float y)
    {
        Vector3 p = projectWorldToPerspective(x, y);
        float x0 = p.x, y0 = p.y;
        p = projectWorldToPerspective(x + ((float) Math.cos(angle)), y + ((float) Math.sin(angle)));
        float x1 = p.x, y1 = p.y;
        return (float) Math.toRadians(Utils.getAngleDegree(x0, y0, x1, y1));
    }

    /** Screen to world. */
    private Vector3 unproject(OrthographicCamera camera, Vector3 unprojector)
    {
        float x = unprojector.x, y = unprojector.y;
        y = screenHeight - y - 1;
        unprojector.x = (2 * x) / screenWidth - 1;
        unprojector.y = (2 * y) / screenHeight - 1;
        prj(camera.invProjectionView, unprojector);
        return unprojector;
    }

    /** World to screen. */
    private Vector3 project(OrthographicCamera camera, Vector3 projector)
    {
        prj(camera.combined, projector);
        projector.x = screenWidth * (projector.x + 1) / 2;
        projector.y = screenHeight * (projector.y + 1) / 2;
        return projector;
    }

    private Vector3 prj (final Matrix4 matrix, Vector3 unprojector) {
        final float l_mat[] = matrix.val;
        final float l_w = 1f / (unprojector.x * l_mat[Matrix4.M30] + unprojector.y * l_mat[Matrix4.M31] + l_mat[Matrix4.M33]);
        float x = (unprojector.x * l_mat[Matrix4.M00] + unprojector.y * l_mat[Matrix4.M01] + l_mat[Matrix4.M03]) * l_w;
        float y = (unprojector.x * l_mat[Matrix4.M10] + unprojector.y * l_mat[Matrix4.M11] + l_mat[Matrix4.M13]) * l_w;
        return unprojector.set(x, y, 0);
    }
}
