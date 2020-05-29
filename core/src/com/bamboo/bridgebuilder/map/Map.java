package com.bamboo.bridgebuilder.map;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.EditorAssets;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.commands.Command;
import com.bamboo.bridgebuilder.commands.DeleteMapObjects;
import com.bamboo.bridgebuilder.commands.DeleteSelectedMapSprites;
import com.bamboo.bridgebuilder.data.*;
import com.bamboo.bridgebuilder.ui.fileMenu.Tools;
import com.bamboo.bridgebuilder.ui.layerMenu.LayerMenu;
import com.bamboo.bridgebuilder.ui.layerMenu.LayerTypes;
import com.bamboo.bridgebuilder.ui.propertyMenu.PropertyMenu;
import com.bamboo.bridgebuilder.ui.propertyMenu.PropertyToolPane;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.FieldFieldPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteMenu;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteMenuTools;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

import java.io.File;
import java.util.Stack;

public class Map implements Screen
{
    public BridgeBuilder editor;

    public static int untitledCount = 0; // For new map name

    // Background color
    public float r = Utils.randomFloat(.25f, .75f);
    public float g = Utils.randomFloat(.25f, .75f);
    public float b = Utils.randomFloat(.25f, .75f);

    // Game window size
    public static float virtualHeight;
    public static float virtualWidth;

    public String name; // Map name
    public TextButton mapPaneButton; // Shown in file menu
    public boolean changed = false; // Any changes since the last save/opening/creating the file?

    public Stage stage;
    public SpriteMenu spriteMenu;
    public PropertyMenu propertyMenu;
    public LayerMenu layerMenu;
    public OrthographicCamera camera;

    // Optional libgdx box2d/lights
    public World world;
    public Box2DDebugRenderer b2dr;
    public RayHandler rayHandler;

    public Array<Layer> layers;
    public Layer selectedLayer;
    public LayerChild hoveredChild; // Whatever the mouse is hovering when SELECT tool is selected
    public Array<MapSprite> selectedSprites;
    public Array<MapObject> selectedObjects;
    public int randomSpriteIndex;
    public float zoom = 1;

    public MapInput input;

    public Skin skin;

    public File file = null;

    // For undo/redo
    private int undoRedoPointer = -1;
    private Stack<Command> commandStack = new Stack<>();
    private int stackThreshold = 75;

    public Map(BridgeBuilder editor, String name)
    {
        this.editor = editor;
        this.name = name;
        init();
        // sprite sheets TODO remove when default BBM is added
        this.spriteMenu.createSpriteSheet("editorMap");
        this.spriteMenu.createSpriteSheet("flatMap");
        this.spriteMenu.createSpriteSheet("canyonMap");
        this.spriteMenu.createSpriteSheet("canyonBackdrop");
        this.spriteMenu.createSpriteSheet("mesaMap");
        this.clearUndoRedo();
    }

    public Map(BridgeBuilder editor, MapData mapData)
    {
        this.editor = editor;
        init();
        loadMap(mapData);
    }

    private void init()
    {
        // Game screen size
        virtualHeight = 20f;
        virtualWidth = virtualHeight * Gdx.graphics.getWidth() / Gdx.graphics.getHeight();

        this.b2dr = new Box2DDebugRenderer();

        this.input = new MapInput(this.editor, this);

        this.layers = new Array<>();
        this.selectedSprites = new Array<>();
        this.selectedObjects = new Array<>();

        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.setToOrtho(false, virtualHeight * Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight(), virtualHeight);
        this.camera.zoom = this.zoom;
        this.camera.position.x = 2.5f;
        this.camera.position.y = 2.5f;
        this.camera.update();


        this.stage = new Stage(new ScreenViewport(), this.editor.batch);
        this.skin = EditorAssets.getUISkin();
        // spriteMenu
        this.spriteMenu = new SpriteMenu(EditorAssets.getUISkin(), this.editor, this);
        this.spriteMenu.setVisible(true);
        this.stage.addActor(this.spriteMenu);

        // propertyMenu
        this.propertyMenu = new PropertyMenu(EditorAssets.getUISkin(), this.editor, this);
        this.propertyMenu.setVisible(true);
        this.stage.addActor(this.propertyMenu);

        // layerMenu
        this.layerMenu = new LayerMenu(EditorAssets.getUISkin(), this.editor, this);
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
        this.editor.inputMultiplexer.addProcessor(this.editor.shortcutProcessor);
        Gdx.input.setInputProcessor(this.editor.inputMultiplexer);
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(this.r, this.g, this.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.world.step(delta, 1, 1);

        this.camera.zoom = this.zoom;
        this.camera.update();

        this.editor.batch.setProjectionMatrix(this.camera.combined);
        this.editor.batch.begin();
        //spritebatch begin
        drawSpriteLayersAndLights();
        //spritebatch end
        this.editor.batch.end();

        this.editor.shapeRenderer.setProjectionMatrix(this.camera.combined);
        this.editor.shapeRenderer.setAutoShapeType(true);
        this.editor.shapeRenderer.setColor(Color.BLACK);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        this.editor.shapeRenderer.begin();
        //shaperenderer begin
        drawLayerOutline();
        drawGrid();
        drawAttachedObjects();
        drawObjectLayers();
        drawBlocked();
        if(this.editor.fileMenu.toolPane.b2drender.selected)
        {
            this.editor.shapeRenderer.end();
            this.b2dr.render(this.world, this.camera.combined);
            this.editor.shapeRenderer.begin();
        }
        drawHoveredOutline();
        drawSelectedOutlines();
        drawUnfinishedMapPolygon();
        drawGradientLine();
        drawVerticeSelect();
        drawBoxSelect();
        //shaperenderer end
        this.editor.shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        this.editor.batch.begin();
        for(int i = 0; i < this.selectedSprites.size; i ++)
        {
            this.selectedSprites.get(i).drawMoveBox();
            this.selectedSprites.get(i).drawRotationBox();
            this.selectedSprites.get(i).drawScaleBox();
        }
        for(int i = 0; i < this.selectedObjects.size; i ++)
            this.selectedObjects.get(i).drawMoveBox();
        this.editor.batch.end();

        this.stage.getBatch().enableBlending();
        this.stage.act();
        this.stage.draw();
    }

    private void drawAttachedObjects()
    {
        for(int i = 0; i < this.layers.size; i ++)
        {
            Layer layer = this.layers.get(i);
            if(this.zoom < layer.z)
                continue;
            if(layer instanceof SpriteLayer)
            {
                layer.setCameraZoomToThisLayer();
                SpriteLayer spriteLayer = (SpriteLayer) layer;
                for(int k = 0; k < spriteLayer.children.size; k ++)
                {
                    MapSprite mapSprite = spriteLayer.children.get(k);
                    if(mapSprite.tool.hasAttachedMapObjects())
                    {
                        for(int s = 0; s < mapSprite.attachedMapObjects.size; s ++)
                            mapSprite.attachedMapObjects.get(s).draw();
                    }
                }
            }
        }

        if(this.selectedLayer == null)
            return;
        if(editor.fileMenu.toolPane.parallax.selected)
        {
            this.camera.zoom = this.zoom - this.selectedLayer.z;
            this.camera.update();
            this.editor.batch.setProjectionMatrix(this.camera.combined);
            this.editor.shapeRenderer.setProjectionMatrix(this.camera.combined);
        }
        PropertyToolPane.updatePerspective(this);
    }

    private void drawSelectedOutlines()
    {
        if(!Utils.isFileToolThisType(this.editor, Tools.SELECT) && !Utils.isFileToolThisType(this.editor, Tools.BOXSELECT) && !Utils.isFileToolThisType(this.editor, Tools.OBJECTVERTICESELECT) && !Utils.isFileToolThisType(this.editor, Tools.DRAWPOINT) && !Utils.isFileToolThisType(this.editor, Tools.DRAWOBJECT) && !Utils.isFileToolThisType(this.editor, Tools.DRAWRECTANGLE) && !Utils.isFileToolThisType(this.editor, Tools.GRADIENT))
            return;
        if(Utils.isFileToolThisType(this.editor, Tools.DRAWPOINT) || Utils.isFileToolThisType(this.editor, Tools.DRAWOBJECT) || Utils.isFileToolThisType(this.editor, Tools.DRAWRECTANGLE))
        {
            if(this.selectedLayer instanceof SpriteLayer && this.selectedSprites.size != 1)
                return;
        }

        for(int i = 0; i < this.selectedSprites.size; i ++)
        {
            MapSprite selectedSprite = this.selectedSprites.get(i);
            if(selectedSprite == this.hoveredChild)
                selectedSprite.drawSelectedHoveredOutline();
            else
                selectedSprite.drawSelectedOutline();
        }

        for(int i = 0; i < this.selectedObjects.size; i ++)
        {
            MapObject selectedObject = this.selectedObjects.get(i);
            if(selectedObject == this.hoveredChild)
                selectedObject.drawSelectedHoveredOutline();
            else
                selectedObject.drawSelectedOutline();
        }
    }

    private void drawUnfinishedMapPolygon()
    {
        if(!Utils.isFileToolThisType(this.editor, Tools.DRAWOBJECT) && !Utils.isFileToolThisType(this.editor, Tools.DRAWRECTANGLE))
            return;
        this.editor.shapeRenderer.setColor(Color.GRAY);
        int oldIndex = 0;
        if(Utils.isFileToolThisType(this.editor, Tools.DRAWRECTANGLE) && this.input.mapPolygonVertices.size == 8)
        {
            this.input.mapPolygonVertices.removeIndex(2);
            this.input.mapPolygonVertices.insert(2, this.input.currentPos.x - this.input.objectVerticePosition.x);
            this.input.mapPolygonVertices.removeIndex(3);
            this.input.mapPolygonVertices.insert(3, this.input.mapPolygonVertices.get(1));
            this.input.mapPolygonVertices.removeIndex(4);
            this.input.mapPolygonVertices.insert(4, this.input.currentPos.x - this.input.objectVerticePosition.x);
            this.input.mapPolygonVertices.removeIndex(5);
            this.input.mapPolygonVertices.insert(5, this.input.currentPos.y - this.input.objectVerticePosition.y);
            this.input.mapPolygonVertices.removeIndex(6);
            this.input.mapPolygonVertices.insert(6, this.input.mapPolygonVertices.get(0));
            this.input.mapPolygonVertices.removeIndex(7);
            this.input.mapPolygonVertices.insert(7, this.input.currentPos.y - this.input.objectVerticePosition.y);
        }
        if (this.input.mapPolygonVertices.size >= 2)
        {
            this.editor.shapeRenderer.circle(this.input.mapPolygonVertices.get(0) + this.input.objectVerticePosition.x, this.input.mapPolygonVertices.get(1) + this.input.objectVerticePosition.y, .1f, 7);
            for (int i = 2; i < this.input.mapPolygonVertices.size; i += 2)
            {
                this.editor.shapeRenderer.line(this.input.mapPolygonVertices.get(oldIndex) + this.input.objectVerticePosition.x, this.input.mapPolygonVertices.get(oldIndex + 1) + this.input.objectVerticePosition.y, this.input.mapPolygonVertices.get(i) + this.input.objectVerticePosition.x, this.input.mapPolygonVertices.get(i + 1) + this.input.objectVerticePosition.y);
                oldIndex += 2;
            }
            if(Utils.isFileToolThisType(editor, Tools.DRAWRECTANGLE))
                this.editor.shapeRenderer.line(this.input.mapPolygonVertices.get(oldIndex) + this.input.objectVerticePosition.x, this.input.mapPolygonVertices.get(oldIndex + 1) + this.input.objectVerticePosition.y, this.input.mapPolygonVertices.get(0) + this.input.objectVerticePosition.x, this.input.mapPolygonVertices.get(1) + this.input.objectVerticePosition.y);
        }
    }

    private void drawGradientLine()
    {
        if(!this.editor.fileMenu.toolPane.gradient.selected || !this.input.draggingGradient)
            return;
        this.editor.shapeRenderer.setColor(Color.BLACK);
        this.editor.shapeRenderer.line(this.input.gradientX, this.input.gradientY, this.input.currentPos.x, this.input.currentPos.y);

    }

    private void drawVerticeSelect()
    {
        for(int i = 0; i < this.selectedObjects.size; i ++)
        {
            MapObject mapObject = this.selectedObjects.get(i);
            if(mapObject instanceof MapPolygon)
            {
                MapPolygon mapPolygon = (MapPolygon) mapObject;
                mapPolygon.drawHoveredVertices();
                mapPolygon.drawSelectedVertices();
            }
        }
    }

    private void drawBoxSelect()
    {
        if(!Utils.isFileToolThisType(this.editor, Tools.BOXSELECT) || !this.input.boxSelect.isDragging || this.selectedLayer == null)
            return;

        for(int i = 0; i < this.selectedLayer.children.size; i ++)
        {
            LayerChild layerChild = (LayerChild) this.selectedLayer.children.get(i);
            if(layerChild.isHoveredOver(this.input.boxSelect.getVertices()))
            {
                if(layerChild.selected)
                    layerChild.drawSelectedHoveredOutline();
                else
                    layerChild.drawHoverOutline();
            }
        }

        this.editor.shapeRenderer.setColor(Color.CYAN);
        this.editor.shapeRenderer.rect(this.input.boxSelect.rectangle.x, this.input.boxSelect.rectangle.y, this.input.boxSelect.rectangle.width, this.input.boxSelect.rectangle.height);
    }

    private void drawHoveredOutline()
    {
        if(!Utils.isFileToolThisType(editor, Tools.SELECT))
            return;

        if(this.hoveredChild == null)
            return;
        this.hoveredChild.drawHoverOutline();
    }

    private void drawSpriteLayersAndLights()
    {
        boolean renderedRayhandler = false;
        for(int i = 0; i < this.layers.size; i ++)
        {
            Layer layer = this.layers.get(i);
            if(layer instanceof SpriteLayer)
            {
                SpriteLayer spriteLayer = (SpriteLayer) layer;
                if (spriteLayer.layerField.visibleImg.isVisible() && spriteLayer.overrideSprite == null)
                    spriteLayer.draw();
            }
            else if(Utils.getPropertyField(layer.properties, "rayhandler") != null)
            {
                layer.setCameraZoomToThisLayer();
                renderedRayhandler = true;
                this.editor.batch.end();
                renderlights(layer);
                this.editor.batch.begin();
            }
        }
        if(!renderedRayhandler)
        {
            if(editor.fileMenu.toolPane.parallax.selected && this.layers.size > 0)
            {
                this.camera.zoom = this.zoom - this.layers.peek().z;
                this.camera.update();
                this.editor.batch.setProjectionMatrix(this.camera.combined);
                this.editor.shapeRenderer.setProjectionMatrix(this.camera.combined);
            }
            PropertyToolPane.updatePerspective(this);

            this.editor.batch.end();
            renderlights(null);
            this.editor.batch.begin();
        }
    }

    private void renderlights(Layer layer)
    {
        if(this.editor.fileMenu.toolPane.perspective.selected && Utils.doesLayerHavePerspective(this, layer))
        {
            this.camera.update();
            float[] m = this.camera.combined.getValues();
            float skew = 0;
            float antiDepth = 0;
            try
            {
                FieldFieldPropertyValuePropertyField property = Utils.getSkewPerspectiveProperty(this, layer);
                skew = Float.parseFloat(property.value.getText());
                property = Utils.getAntiDepthPerspectiveProperty(this, layer);
                antiDepth = Float.parseFloat(property.value.getText());
            }
            catch (NumberFormatException e){}
            if(antiDepth >= .1f)
                skew /= antiDepth * 15;
            m[Matrix4.M31] += skew;
            m[Matrix4.M11] += this.camera.position.y / (-8f / skew) - ((.097f * antiDepth) / (antiDepth + .086f));

            this.camera.invProjectionView.set(this.camera.combined);
            Matrix4.inv(this.camera.invProjectionView.val);
            this.camera.frustum.update(this.camera.invProjectionView);
        }

        this.rayHandler.setCombinedMatrix(this.camera.combined, this.camera.position.x, this.camera.position.y, this.camera.viewportWidth * this.camera.zoom * 2f, this.camera.viewportHeight * this.camera.zoom * 2f);
        this.rayHandler.updateAndRender();

        if(this.editor.fileMenu.toolPane.perspective.selected)
            this.camera.update();
    }

    private void drawObjectLayers()
    {
        for(int i = 0; i < this.layers.size; i ++)
        {
            if(this.layers.get(i) instanceof ObjectLayer)
            {
                if (this.layers.get(i).layerField.visibleImg.isVisible() && this.layers.get(i).overrideSprite == null)
                    this.layers.get(i).draw();
            }
        }
    }

    private void drawBlocked()
    {
        for(int i = 0; i < this.layers.size; i ++)
        {
            if(this.layers.get(i) instanceof ObjectLayer)
            {
                ObjectLayer objectLayer = (ObjectLayer) this.layers.get(i);
                if (objectLayer.layerField.visibleImg.isVisible() && objectLayer.overrideSprite == null && this.editor.fileMenu.toolPane.blocked.selected && objectLayer.graph != null)
                    objectLayer.graph.drawBlocked();
            }
        }
    }

    private void drawGrid()
    {
        if(this.selectedLayer != null)
        {
            this.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
            int layerWidth = this.selectedLayer.width;
            int layerHeight = this.selectedLayer.height;
            if (this.editor.fileMenu.toolPane.lines.selected)
            {
                for (int y = 1; y < layerHeight; y++)
                    this.editor.shapeRenderer.line(this.selectedLayer.x, this.selectedLayer.y + y, this.selectedLayer.x + layerWidth, this.selectedLayer.y + y);
                for (int x = 1; x < layerWidth; x++)
                    this.editor.shapeRenderer.line(this.selectedLayer.x + x, this.selectedLayer.y, this.selectedLayer.x + x, this.selectedLayer.y + layerHeight);
            }
        }
    }

    private void drawLayerOutline()
    {
        if(this.selectedLayer != null)
        {
            this.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
            int layerWidth = this.selectedLayer.width;
            int layerHeight = this.selectedLayer.height;
            this.editor.shapeRenderer.line(this.selectedLayer.x, this.selectedLayer.y, this.selectedLayer.x, this.selectedLayer.y + layerHeight);
            this.editor.shapeRenderer.line(this.selectedLayer.x, this.selectedLayer.y, this.selectedLayer.x + layerWidth, this.selectedLayer.y);
            this.editor.shapeRenderer.line(this.selectedLayer.x, this.selectedLayer.y + layerHeight, this.selectedLayer.x + layerWidth, this.selectedLayer.y + layerHeight);
            this.editor.shapeRenderer.line(this.selectedLayer.x + layerWidth, this.selectedLayer.y, this.selectedLayer.x + layerWidth, this.selectedLayer.y + layerHeight);
        }
    }

    public void setChanged(boolean changed)
    {
        if(this.mapPaneButton == null)
            return;
        if(this.changed != changed)
        {
            if(changed)
                this.mapPaneButton.setText(this.name + "*");
            else
                this.mapPaneButton.setText(this.name);
        }
        this.changed = changed;
    }

    @Override
    public void resize(int width, int height)
    {
        virtualWidth = virtualHeight * this.stage.getWidth() / this.stage.getHeight();
        this.camera.viewportWidth = virtualHeight * width / (float) height;
        this.camera.viewportHeight = virtualHeight;
        this.camera.update();

        this.stage.getViewport().update(width, height, true);
        this.spriteMenu.setSize(Gdx.graphics.getWidth() / 6, (Gdx.graphics.getHeight() - this.editor.fileMenu.getHeight()) / 2);
        this.spriteMenu.setPosition(Gdx.graphics.getWidth() - this.spriteMenu.getWidth(), 0);

        this.propertyMenu.setSize(Gdx.graphics.getWidth() / 6, Gdx.graphics.getHeight() - this.editor.fileMenu.getHeight());
        this.propertyMenu.setPosition(0, 0);

        this.layerMenu.setSize(Gdx.graphics.getWidth() / 6, (Gdx.graphics.getHeight() - this.editor.fileMenu.getHeight()) / 2);
        this.layerMenu.setPosition(Gdx.graphics.getWidth() - this.spriteMenu.getWidth(), this.spriteMenu.getHeight());
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
        this.mapPaneButton.setText(name);
    }

    public Array<SpriteTool> getAllSelectedSpriteTools()
    {
        if(this.spriteMenu.selectedSpriteTools.size > 0)
            if(this.spriteMenu.selectedSpriteTools.first().tool != SpriteMenuTools.SPRITE)
                return  null;
        return this.spriteMenu.selectedSpriteTools;
    }

    public SpriteTool getSpriteToolFromSelectedTools()
    {
        if(this.spriteMenu.selectedSpriteTools.size == 0)
            return null;
        if(this.spriteMenu.selectedSpriteTools.first().tool != SpriteMenuTools.SPRITE)
            return  null;
        if(this.editor.fileMenu.toolPane.random.selected && this.randomSpriteIndex < this.spriteMenu.selectedSpriteTools.size)
            return this.spriteMenu.selectedSpriteTools.get(this.randomSpriteIndex);
        return this.spriteMenu.selectedSpriteTools.first();
    }

    public void shuffleRandomSpriteTool()
    {
        if(getSpriteToolFromSelectedTools() == null)
            return;

        // Randomly pick a sprite from the selected sprites based on weighted probabilities
        float totalSum = 0;
        float partialSum = 0;
        for(int i = 0; i < getAllSelectedSpriteTools().size; i ++)
        {
            totalSum += Float.parseFloat(Utils.getLockedPropertyField(getAllSelectedSpriteTools().get(i).lockedProperties, "Probability").value.getText());
        }
        float random = Utils.randomFloat(0, totalSum);
        for(int i = 0; i < getAllSelectedSpriteTools().size; i ++)
        {
            partialSum += Float.parseFloat(Utils.getLockedPropertyField(getAllSelectedSpriteTools().get(i).lockedProperties, "Probability").value.getText());
            if(partialSum >= random)
            {
                this.randomSpriteIndex = i;
                break;
            }
        }
        this.editor.fileMenu.toolPane.minMaxDialog.generateRandomValues();
        if(getSpriteToolFromSelectedTools() != null)
        {
            SpriteTool spriteTool = getSpriteToolFromSelectedTools();
            Vector3 coords = Utils.unproject(this.camera, Gdx.input.getX(), Gdx.input.getY());
            float x = coords.x;
            float y = coords.y;
            for (int i = 0; i < getSpriteToolFromSelectedTools().previewSprites.size; i++)
            {
                float randomScale = this.editor.fileMenu.toolPane.minMaxDialog.randomSizeValue;
                Sprite previewSprite = spriteTool.previewSprites.get(i);
                if(this.editor.fileMenu.toolPane.perspective.selected && Utils.doesLayerHavePerspective(this, this.selectedLayer))
                {
                    previewSprite.setPosition(x - previewSprite.getWidth() * previewSprite.getScaleX() / 2, y - previewSprite.getHeight() * previewSprite.getScaleY() / 2);

                    float perspective = 0;
                    FieldFieldPropertyValuePropertyField topPropertyField = Utils.getTopScalePerspectiveProperty(this, this.selectedLayer);
                    FieldFieldPropertyValuePropertyField bottomPropertyField = Utils.getBottomScalePerspectiveProperty(this, this.selectedLayer);
                    float perspectiveTop = Float.parseFloat(topPropertyField.value.getText());
                    float perspectiveBottom = Float.parseFloat(bottomPropertyField.value.getText());

                    float mapHeight = this.selectedLayer.height;
                    float positionY = previewSprite.getY() + previewSprite.getHeight() / 2;

                    float coeff = positionY / mapHeight;
                    float delta = perspectiveTop - perspectiveBottom;

                    perspective = (perspectiveBottom + coeff * delta) - 1;

                    float perspectiveScale = randomScale + perspective;
                    previewSprite.setOriginCenter();
                    previewSprite.setScale(perspectiveScale);

                    Vector3 p = Utils.project(this.camera, coords.x, coords.y);
                    x = p.x;
                    y = Gdx.graphics.getHeight() - p.y;
                    float[] m = this.camera.combined.getValues();
                    float skew = 0;
                    float antiDepth = 0;
                    try
                    {
                        FieldFieldPropertyValuePropertyField property = Utils.getSkewPerspectiveProperty(this, this.selectedLayer);
                        skew = Float.parseFloat(property.value.getText());
                        property = Utils.getAntiDepthPerspectiveProperty(this, this.selectedLayer);
                        antiDepth = Float.parseFloat(property.value.getText());
                    }
                    catch (NumberFormatException e){}
                    m[Matrix4.M31] -= skew;
                    m[Matrix4.M11] += (this.camera.position.y / 10) * (skew / this.camera.zoom) + (antiDepth / this.camera.zoom);
                    this.camera.invProjectionView.set(this.camera.combined);
                    Matrix4.inv(this.camera.invProjectionView.val);
                    this.camera.frustum.update(this.camera.invProjectionView);
                    p = Utils.unproject(this.camera, x, y);
                    x = p.x;
                    y = p.y;
                    this.camera.update();

                    previewSprite.setPosition(x - previewSprite.getWidth() * previewSprite.getScaleX() / 2, y - previewSprite.getHeight() * previewSprite.getScaleY() / 2);
                }
                else
                {
                    previewSprite.setScale(randomScale, randomScale);
                    previewSprite.setPosition(coords.x - previewSprite.getWidth() / 2, coords.y - previewSprite.getHeight() / 2);
                }
            }
        }
    }

    public void updateLayerGraphs()
    {
        for(int i = 0; i < this.layers.size; i ++)
        {
            Layer layer = this.layers.get(i);
            if(layer instanceof ObjectLayer)
            {
                ObjectLayer objectLayer = (ObjectLayer) layer;
                objectLayer.updateGraph();
            }
        }
    }

    public void executeCommand(Command command)
    {
        command.execute();
        Utils.println();
        Utils.println("execute " + command);
        pushCommand(command);
    }

    public void pushCommand(Command command)
    {
        deleteElementsAfterPointer(this.undoRedoPointer);
        this.commandStack.push(command);
        this.undoRedoPointer ++;
        Utils.println("push " + command);
        Utils.println();
        if(this.commandStack.size() > this.stackThreshold)
        {
            this.undoRedoPointer --;
            this.commandStack.remove(0);
        }
    }

    private void deleteElementsAfterPointer(int undoRedoPointer)
    {
        if(this.commandStack.size() < 1)
            return;
        for(int i = this.commandStack.size() - 1; i > undoRedoPointer; i --)
            this.commandStack.remove(i);
    }

    public void clearUndoRedo()
    {
        this.undoRedoPointer = -1;
        this.commandStack.clear();
    }

    public void undo()
    {
        if(this.undoRedoPointer < 0)
            return;
        Command command = this.commandStack.get(this.undoRedoPointer);
        Utils.println("undo " + command);
        command.undo();
        this.undoRedoPointer --;
    }

    public void redo()
    {
        if(this.undoRedoPointer == this.commandStack.size() - 1)
            return;
        this.undoRedoPointer ++;
        Command command = this.commandStack.get(this.undoRedoPointer);
        Utils.println("redo " + command);
        command.execute();
    }

    public void deleteSelected()
    {
        if(this.selectedLayer != null)
        {
            if(this.selectedObjects.size > 0)
            {
                DeleteMapObjects deleteMapObjects = new DeleteMapObjects(this.selectedObjects, this.selectedLayer);
                executeCommand(deleteMapObjects);
            }
            else if(this.selectedSprites.size > 0)
            {
                DeleteSelectedMapSprites deleteSelectedMapSprites = new DeleteSelectedMapSprites(this.selectedSprites, (SpriteLayer) this.selectedLayer);
                executeCommand(deleteSelectedMapSprites);
            }
        }
    }

    public void colorizeDepth()
    {
        if(this.selectedLayer == null)
            return;

        PropertyToolPane.apply(this);

        Array<MapSprite> children;
        if(this.selectedSprites.size > 1)
            children = this.selectedSprites;
        else
            children = this.selectedLayer.children;

        for(int i = 0; i < children.size; i ++)
        {
            float norm = MathUtils.norm(0, children.size, i);
            MapSprite mapSprite = children.get(i);
            mapSprite.sprite.setColor(1f - norm, .25f, norm, 1);
        }
    }

    private void loadMap(MapData mapData)
    {
        this.name = mapData.name;

        // map properties
        int propSize = mapData.props.size();
        for(int s = 0; s < propSize; s++)
        {
            PropertyData propertyData = mapData.props.get(s);
            propertyMenu.newProperty(propertyData, propertyMenu.mapPropertyPanel.properties);
        }
        // map locked properties
        propSize = mapData.lProps.size();
        for(int s = 0; s < propSize; s++)
        {
            PropertyData propertyData = mapData.lProps.get(s);
            propertyMenu.changeLockedPropertyValue(propertyData, propertyMenu.mapPropertyPanel.lockedProperties);
        }

        // create sprite sheets
        int size = mapData.sheets.size();
        for(int i = 0; i < size; i ++)
        {
            SpriteSheetData spriteSheetData = mapData.sheets.get(i);
            spriteMenu.createSpriteSheet(spriteSheetData.name);

            // sprite tool properties
            int toolSize = spriteSheetData.tools.size();
            for(int k = 0; k < toolSize; k ++)
            {
                ToolData toolData = spriteSheetData.tools.get(k);
                SpriteTool spriteTool = spriteMenu.getSpriteTool(toolData.n, spriteSheetData.name);

                // properties
                propSize = toolData.props.size();
                for(int s = 0; s < propSize; s++)
                {
                    PropertyData propertyData = toolData.props.get(s);
                    propertyMenu.newProperty(propertyData, spriteTool.properties);
                }
                // locked properties
                propSize = toolData.lProps.size();
                for(int s = 0; s < propSize; s++)
                {
                    PropertyData propertyData = toolData.lProps.get(s);
                    propertyMenu.changeLockedPropertyValue(propertyData, spriteTool.lockedProperties);
                }

                // attached map objects
                if(toolData.objs != null)
                {
                    int objSize = toolData.objs.size();
                    for (int s = 0; s < objSize; s++)
                    {
                        MapObjectData mapObjectData = toolData.objs.get(s);
                        MapObject mapObject;
                        if (mapObjectData instanceof MapPolygonData)
                        {
                            MapPolygonData mapPolygonData = (MapPolygonData) mapObjectData;
                            MapPolygon mapPolygon = new MapPolygon(this, mapPolygonData.verts, mapPolygonData.x, mapPolygonData.y);
                            mapObject = mapPolygon;
                        } else
                        {
                            MapPointData mapPointData = (MapPointData) mapObjectData;
                            MapPoint mapPoint = new MapPoint(this, mapPointData.x, mapPointData.y);
                            mapObject = mapPoint;
                        }
                        // attached manager
                        spriteTool.createAttachedMapObject(this, mapObject, mapObjectData.offsetX, mapObjectData.offsetY);
                        // object properties
                        propSize = mapObjectData.props.size();
                        for (int p = 0; p < propSize; p++)
                        {
                            PropertyData propertyData = mapObjectData.props.get(p);
                            propertyMenu.newProperty(propertyData, mapObject.properties);
                        }
                    }
                }
            }
        }

        // create layers
        for(int i = mapData.layers.size() - 1; i >= 0; i --)
        {
            LayerData layerData = mapData.layers.get(i);
            Layer layer;
            if(layerData instanceof SpriteLayerData)
                layer = this.layerMenu.newLayer(LayerTypes.SPRITE);
            else
                layer = this.layerMenu.newLayer(LayerTypes.OBJECT);
            layer.layerField.layerName.setText(layerData.name);
            layer.setPosition(layerData.x, layerData.y);
            layer.resize(layerData.w, layerData.h, false, false);
            layer.setZ(layerData.z);

            // layer properties
            propSize = layerData.props.size();
            for (int p = 0; p < propSize; p++)
            {
                PropertyData propertyData = layerData.props.get(p);
                propertyMenu.newProperty(propertyData, layer.properties);
            }

            // create layer children
            if(layerData instanceof SpriteLayerData)
            {
                SpriteLayerData spriteLayerData = (SpriteLayerData) layerData;
                int childSize = spriteLayerData.children.size();
                for(int k = 0; k < childSize; k ++)
                {
                    MapSpriteData mapSpriteData = spriteLayerData.children.get(k);
                    SpriteTool spriteTool = spriteMenu.getSpriteTool(mapSpriteData.n, mapSpriteData.sN);
                    MapSprite mapSprite = new MapSprite(this, layer, spriteTool, mapSpriteData.x, mapSpriteData.y);
                    mapSprite.setZ(mapSpriteData.z);
                    mapSprite.setScale(mapSpriteData.scl + MapSpriteData.defaultScaleValue);
                    mapSprite.setColor(mapSpriteData.r + MapSpriteData.defaultColorValue, mapSpriteData.g + MapSpriteData.defaultColorValue, mapSpriteData.b + MapSpriteData.defaultColorValue, mapSpriteData.a + MapSpriteData.defaultColorValue);
                    mapSprite.setPosition(mapSpriteData.x, mapSpriteData.y);
                    mapSprite.setRotation(mapSpriteData.rot);
                    mapSprite.setID(mapSpriteData.id);
                    ((SpriteLayer) layer).addMapSprite(mapSprite);

                    // locked properties
                    propSize = mapSpriteData.lProps.size();
                    for(int s = 0; s < propSize; s++)
                    {
                        PropertyData propertyData = mapSpriteData.lProps.get(s);
                        propertyMenu.changeLockedPropertyValue(propertyData, spriteTool.lockedProperties);
                    }
                }
            }
            else if(layerData instanceof ObjectLayerData)
            {
                ObjectLayerData objectLayerData = (ObjectLayerData) layerData;
                int childSize = objectLayerData.children.size();
                for (int k = 0; k < childSize; k++)
                {
                    MapObjectData mapObjectData = objectLayerData.children.get(k);
                    MapObject mapObject;
                    if(mapObjectData instanceof MapPolygonData)
                    {
                        MapPolygonData mapPolygonData = (MapPolygonData) mapObjectData;
                        MapPolygon mapPolygon = new MapPolygon(this, layer, mapPolygonData.verts, mapPolygonData.x, mapPolygonData.y);
                        mapObject = mapPolygon;
                        ((ObjectLayer)layer).addMapObject(mapPolygon);
                    }
                    else
                    {
                        MapPointData mapPointData = (MapPointData) mapObjectData;
                        MapPoint mapPoint = new MapPoint(this, layer, mapPointData.x, mapPointData.y);
                        mapObject = mapPoint;
                        ((ObjectLayer)layer).addMapObject(mapPoint);
                    }
                    // object properties
                    propSize = mapObjectData.props.size();
                    for(int s = 0; s < propSize; s++)
                    {
                        PropertyData propertyData = mapObjectData.props.get(s);
                        propertyMenu.newProperty(propertyData, mapObject.properties);
                    }
                }
            }
        }
        PropertyToolPane.apply(this);
    }
}
