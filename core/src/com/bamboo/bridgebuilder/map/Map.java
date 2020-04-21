package com.bamboo.bridgebuilder.map;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

public class Map implements Screen
{
    public BridgeBuilder editor;
    public OrthographicCamera camera;
    public Viewport viewport;
    public static int untitledCount = 0;

    public float r = Utils.randomFloat(0, .5f);
    public float g = Utils.randomFloat(0, .5f);
    public float b = Utils.randomFloat(0, .5f);

    public String name;

    public TextButton mapPaneButton;

    public boolean changed = false; // Any changes since the last save/opening/creating the file?

    public float zoom = 1;


    public Stage stage;


    public World world;
    public Box2DDebugRenderer b2dr;
    public RayHandler rayHandler;

    public Map(BridgeBuilder editor, String name)
    {
        this.editor = editor;
        this.name = name;
        init();
    }

    private void init()
    {
        b2dr = new Box2DDebugRenderer();

        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.zoom = this.zoom;
        this.viewport = new ScreenViewport(this.camera);
        this.viewport.apply();
        this.camera.position.x = 160;
        this.camera.position.y = 150;

        this.stage = new Stage(new ScreenViewport());

        this.world = new World(new Vector2(0, 0), false);
        this.rayHandler = new RayHandler(this.world);
        this.rayHandler.setAmbientLight(1);
    }

    @Override
    public void show()
    {
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(r, g, b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    }

    public void setChanged(boolean changed)
    {
        if(mapPaneButton == null)
            return;
        if(this.changed != changed)
        {
            if(changed)
                mapPaneButton.setText(name + "*");
            else
                mapPaneButton.setText(name);
        }
        this.changed = changed;
    }

    @Override
    public void resize(int width, int height)
    {
        this.stage.getViewport().update(width, height, true);

        this.camera.viewportWidth = width;
        this.camera.viewportHeight = height;
        this.viewport.update(width, height);
    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override
    public void hide()
    {

    }

    @Override
    public void dispose()
    {

    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
        mapPaneButton.setText(name);
    }
}
