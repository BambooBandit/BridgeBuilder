package com.bamboo.bridgebuilder.map;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.EditorAssets;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.ui.layerMenu.LayerMenu;
import com.bamboo.bridgebuilder.ui.propertyMenu.PropertyMenu;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteMenu;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteMenuTools;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

public class Map implements Screen
{
    public BridgeBuilder editor;
    public static int untitledCount = 0;
    public float r = Utils.randomFloat(.25f, .75f);
    public float g = Utils.randomFloat(.25f, .75f);
    public float b = Utils.randomFloat(.25f, .75f);
    public String name;
    public TextButton mapPaneButton;
    public boolean changed = false; // Any changes since the last save/opening/creating the file?

    public Stage stage;
    public SpriteMenu spriteMenu;
    public PropertyMenu propertyMenu;
    public LayerMenu layerMenu;
    public World world;
    public Box2DDebugRenderer b2dr;
    public RayHandler rayHandler;
    public OrthographicCamera camera;
    public Viewport viewport;

    public Array<Layer> layers;
    public Layer selectedLayer;
    public Array<MapSprite> selectedSprites;
    public Array<MapObject> selectedObjects;
    public int randomSpriteIndex;
    public float zoom = 1;

    public MapInput input;

    public Map(BridgeBuilder editor, String name)
    {
        this.editor = editor;
        this.name = name;
        init();
    }

    private void init()
    {
        b2dr = new Box2DDebugRenderer();

        this.input = new MapInput(editor, this);

        this.layers = new Array<>();
        this.selectedSprites = new Array<>();
        this.selectedObjects = new Array<>();

        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.zoom = this.zoom;
        this.viewport = new ScreenViewport(this.camera);
        this.viewport.apply();
        this.camera.position.x = 160;
        this.camera.position.y = 150;

        this.stage = new Stage(new ScreenViewport());
        // spriteMenu
        this.spriteMenu = new SpriteMenu(EditorAssets.getUISkin(), editor, this);
        this.spriteMenu.setVisible(true);
        this.stage.addActor(this.spriteMenu);

        // propertyMenu
        this.propertyMenu = new PropertyMenu(EditorAssets.getUISkin(), editor, this);
        this.propertyMenu.setVisible(true);
        this.stage.addActor(this.propertyMenu);

        // layerMenu
        this.layerMenu = new LayerMenu(EditorAssets.getUISkin(), editor, this);
        this.layerMenu.setVisible(true);
        this.stage.addActor(this.layerMenu);

        this.world = new World(new Vector2(0, 0), false);
        this.rayHandler = new RayHandler(this.world);
        this.rayHandler.setAmbientLight(1);
    }

    @Override
    public void show()
    {
        this.editor.inputMultiplexer.clear();
        this.editor.inputMultiplexer.addProcessor(this.editor.stage);
        this.editor.inputMultiplexer.addProcessor(this.stage);
        this.editor.inputMultiplexer.addProcessor(this.input);
        Gdx.input.setInputProcessor(this.editor.inputMultiplexer);
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(r, g, b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.world.step(delta, 1, 1);

        if(!editor.fileMenu.toolPane.parallax.selected)
            this.camera.zoom = this.zoom;
        this.camera.update();
        this.editor.batch.setProjectionMatrix(camera.combined);

        this.stage.act();
        this.stage.draw();
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
        this.spriteMenu.setSize(Gdx.graphics.getWidth() / 6, (Gdx.graphics.getHeight() - this.editor.fileMenu.getHeight()) / 2);
        this.spriteMenu.setPosition(Gdx.graphics.getWidth() - this.spriteMenu.getWidth(), 0);

        this.propertyMenu.setSize(Gdx.graphics.getWidth() / 6, Gdx.graphics.getHeight() - this.editor.fileMenu.getHeight());
        this.propertyMenu.setPosition(0, 0);

        this.layerMenu.setSize(Gdx.graphics.getWidth() / 6, (Gdx.graphics.getHeight() - this.editor.fileMenu.getHeight()) / 2);
        this.layerMenu.setPosition(Gdx.graphics.getWidth() - this.spriteMenu.getWidth(), this.spriteMenu.getHeight());

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

    public Array<SpriteTool> getAllSelectedSpriteTools()
    {
        if(spriteMenu.selectedSpriteTools.size > 0)
            if(spriteMenu.selectedSpriteTools.first().tool != SpriteMenuTools.SPRITE)
                return  null;
        return spriteMenu.selectedSpriteTools;
    }

    public SpriteTool getSpriteToolFromSelectedTools()
    {
        if(spriteMenu.selectedSpriteTools.size == 0)
            return null;
        if(spriteMenu.selectedSpriteTools.first().tool != SpriteMenuTools.SPRITE)
            return  null;
        if(editor.fileMenu.toolPane.random.selected && randomSpriteIndex < spriteMenu.selectedSpriteTools.size)
            return spriteMenu.selectedSpriteTools.get(randomSpriteIndex);
        return spriteMenu.selectedSpriteTools.first();
    }

    public void shuffleRandomSpriteTool()
    {
        if(getSpriteToolFromSelectedTools() == null)
            return;

        // Randomly pick a sprite from the selected sprites based on weighted probabilities
        float totalSum = 0;
        float partialSum = 0;
        for(int i = 0; i < getAllSelectedSpriteTools().size; i ++)
            totalSum += Float.parseFloat(getAllSelectedSpriteTools().get(i).getPropertyField("Probability").value.getText());
        float random = Utils.randomFloat(0, totalSum);
        for(int i = 0; i < getAllSelectedSpriteTools().size; i ++)
        {
            partialSum += Float.parseFloat(getAllSelectedSpriteTools().get(i).getPropertyField("Probability").value.getText());
            if(partialSum >= random)
            {
                randomSpriteIndex = i;
                break;
            }
        }
        editor.fileMenu.toolPane.minMaxDialog.generateRandomValues();
        if(getSpriteToolFromSelectedTools() != null)
        {
            SpriteTool spriteTool = getSpriteToolFromSelectedTools();
            Vector3 coords = Utils.unproject(camera, Gdx.input.getX(), Gdx.input.getY());
            for (int i = 0; i < getSpriteToolFromSelectedTools().previewSprites.size; i++)
            {
                float randomScale = editor.fileMenu.toolPane.minMaxDialog.randomSizeValue;
                spriteTool.previewSprites.get(i).setScale(randomScale, randomScale);
                spriteTool.previewSprites.get(i).setPosition(coords.x - spriteTool.previewSprites.get(i).getWidth() / 2, coords.y - spriteTool.previewSprites.get(i).getHeight() / 2);
            }
        }
    }
}
