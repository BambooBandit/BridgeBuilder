package com.bamboo.bridgebuilder.map;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.EditorAssets;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.commands.*;
import com.bamboo.bridgebuilder.data.*;
import com.bamboo.bridgebuilder.ui.fileMenu.Tools;
import com.bamboo.bridgebuilder.ui.layerMenu.LayerField;
import com.bamboo.bridgebuilder.ui.layerMenu.LayerMenu;
import com.bamboo.bridgebuilder.ui.layerMenu.LayerTypes;
import com.bamboo.bridgebuilder.ui.propertyMenu.PropertyMenu;
import com.bamboo.bridgebuilder.ui.propertyMenu.PropertyToolPane;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.ColorPropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.FieldFieldPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.propertyMenu.propertyfield.LabelFieldPropertyValuePropertyField;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteMenu;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteMenuTools;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteSheet;
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
    public Layer secondarySelectedLayer;
    public Layer selectedLayerPriorToAttachedSpriteEditMode;
    public Layer selectedLayerPriorToGroupMode;
    public MapSprite editAttachedMapSprite = null;
    public LayerChild hoveredChild; // Whatever the mouse is hovering when SELECT tool is selected
    public Array<MapSprite> selectedSprites;
    public Array<MapObject> selectedObjects;
    public int randomSpriteIndex;
    public float zoom = 1;

    public MapInput input;

    public Skin skin;

    public File file = null;

    public MapSprite lastFencePlaced;

    public ObjectLayer groupPolygons; // Each group of map sprites has a polygon associated with it in this layer. Used for making multiple sprites do something when entering a polygon

    // For undo/redo
    private int undoRedoPointer = -1;
    private Stack<Command> commandStack = new Stack<>();
    private int stackThreshold = 75;

    public Map(BridgeBuilder editor, String name)
    {
        this.editor = editor;
        this.name = name;
        init();
        this.clearUndoRedo();
    }

    public Map(BridgeBuilder editor, MapData mapData)
    {
        this.editor = editor;
        init();
        loadMap(mapData, false);
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
        if(!editor.fileMenu.toolPane.spriteGridColors.selected)
            drawSpriteLayersAndLights();
        drawDepthIndexes();
        //spritebatch end
        this.editor.batch.end();

        this.editor.shapeRenderer.setProjectionMatrix(this.camera.combined);
        this.editor.shapeRenderer.setAutoShapeType(true);
        this.editor.shapeRenderer.setColor(Color.BLACK);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        this.editor.shapeRenderer.begin();
        //shaperenderer begin
        drawSpriteGrid();
        drawBlocked();
        drawLayerOutline();
        drawGrid();
        drawAttachedObjects();
        drawObjectLayers();
        drawSnapPreview();
        drawSnap();
        if(this.editor.fileMenu.toolPane.b2drender.selected)
        {
            this.editor.shapeRenderer.end();
            this.b2dr.render(this.world, this.camera.combined);
            this.editor.shapeRenderer.begin();
        }

        drawHoveredOutline();
        drawSelectedOutlines();
        drawUnfinishedMapPolygon();
        drawUnfinishedStairs();
        drawGradientLine();
        drawVerticeSelect();
        drawBoxSelect();
        //shaperenderer end
        this.editor.shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        this.editor.batch.begin();
        if(this.editor.fileMenu.toolPane.spriteGridColors.selected)
            printDustTypes();
        for (int i = 0; i < this.selectedSprites.size; i++)
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

    private void drawSnapPreview()
    {
        if(this.input.snapFromThisObject == null)
            return;
        editor.shapeRenderer.setColor(Color.GOLD);
        float fromX;
        float fromY;
        float toX;
        float toY;
        if(this.input.snapFromThisObject instanceof MapSprite)
        {
            MapSprite fromSprite = (MapSprite) this.input.snapFromThisObject;
            fromX = fromSprite.x + (fromSprite.width / 2f);
            fromY = fromSprite.y + (fromSprite.height / 2f);
        }
        else if(this.input.snapFromThisObject instanceof MapPolygon)
        {
            MapPolygon mapPolygon = (MapPolygon) this.input.snapFromThisObject;
            fromX = mapPolygon.centroidX;
            fromY = mapPolygon.centroidY;
        }
        else
        {
            MapPoint mapPoint = (MapPoint) this.input.snapFromThisObject;
            fromX = mapPoint.x;
            fromY = mapPoint.y;
        }
        if(this.hoveredChild != null && this.input.snapFromThisObject != this.hoveredChild)
        {
            if(this.hoveredChild instanceof MapSprite)
            {
                MapSprite hoveredSprite = (MapSprite) this.hoveredChild;
                toX = hoveredSprite.x + (hoveredSprite.width / 2f);
                toY = hoveredSprite.y + (hoveredSprite.height / 2f);
            }
            else if(this.hoveredChild instanceof MapPolygon)
            {
                MapPolygon hoveredPolygon = (MapPolygon) this.hoveredChild;
                toX = hoveredPolygon.centroidX;
                toY = hoveredPolygon.centroidY;
            }
            else
            {
                MapPoint hoveredPoint = (MapPoint) this.hoveredChild;
                toX = hoveredPoint.x;
                toY = hoveredPoint.y;
            }
        }
        else
        {
            toX = input.currentPos.x;
            toY = input.currentPos.y;
        }
        editor.shapeRenderer.line(fromX, fromY, toX, toY);
    }

    private void drawSnap()
    {
        for(int i = 0; i < layers.size; i ++)
        {
            Layer layer = layers.get(i);
            if(layer instanceof SpriteLayer)
            {
                SpriteLayer spriteLayer = (SpriteLayer) layer;
                for(int k = 0; k < spriteLayer.children.size; k ++)
                {
                    MapSprite from = spriteLayer.children.get(k);
                    drawSnapEdge(from);
                    drawSnapFlicker(from);
                }
            }
            else if(layer instanceof ObjectLayer)
            {
                ObjectLayer objectLayer = (ObjectLayer) layer;
                for(int k = 0; k < objectLayer.children.size; k ++)
                {
                    MapObject from = objectLayer.children.get(k);
                    drawSnapFlicker(from);
                }
            }
        }
    }

    private void drawSnapFlicker(LayerChild layerChild)
    {
        LayerChild from = layerChild;
        if(!from.selected && (from.toFlicker == null || !from.toFlicker.selected))
            return;

        // flicker
        if(from.toFlicker != null)
        {
            editor.shapeRenderer.setColor(Color.ORANGE);
            MapSprite to = from.toFlicker;
            float fromX;
            float fromY;
            if(from instanceof MapSprite)
            {
                MapSprite mapSprite = (MapSprite) from;
                fromX = mapSprite.x + mapSprite.width / 2f;
                fromY = mapSprite.y + mapSprite.height / 2f;
            }
            else if(from instanceof MapPolygon)
            {
                MapPolygon mapPolygon = (MapPolygon) from;
                fromX = mapPolygon.centroidX;
                fromY = mapPolygon.centroidY;
            }
            else
            {
                fromX = from.x;
                fromY = from.y;
            }
            float toX = to.x + (to.width / 2f);
            float toY = to.y + (to.height / 2f);
            editor.shapeRenderer.line(fromX, fromY, toX, toY);
            editor.shapeRenderer.circle(toX, toY, .2f, 5);
        }
    }

    private void drawSnapEdge(MapSprite mapSprite)
    {
        MapSprite from = mapSprite;
        if(!from.selected && (from.toEdgeSprite == null || !from.toEdgeSprite.selected))
            return;

        // edge
        if(from.toEdgeSprite != null)
        {
            editor.shapeRenderer.setColor(Color.YELLOW);
            MapSprite to = from.toEdgeSprite;
            editor.shapeRenderer.line(from.x + (from.width / 2f),
                    from.y + (from.height / 2f),
                    to.x + (to.width / 2f),
                    to.y + (to.height / 2f));
            editor.shapeRenderer.circle(to.x + (to.width / 2f),
                    to.y + (to.height / 2f), .2f, 5);
        }
    }

    private void drawAttachedObjects()
    {
        if(!layerMenu.toolPane.objectVisibility.isSelected)
            return;
        for(int i = 0; i < this.layers.size; i ++)
        {
            Layer layer = this.layers.get(i);
            if(layer instanceof SpriteLayer)
            {
                if (!layer.layerField.attachedVisibleImg.isVisible())
                    continue;
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
            this.camera.zoom = this.zoom;
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

    private void drawUnfinishedStairs()
    {
        if(!Utils.isFileToolThisType(this.editor, Tools.STAIRS) )
            return;
        int oldIndex = 0;
        float finalheight = editor.fileMenu.toolPane.stairsDialog.getFinalHeight();
        float initialheight = editor.fileMenu.toolPane.stairsDialog.getInitialHeight();
        if (this.input.stairVertices.size >= 2)
        {
            this.editor.shapeRenderer.circle(this.input.stairVertices.get(0) + this.input.stairVerticePosition.x, this.input.stairVertices.get(1) + this.input.stairVerticePosition.y, .1f, 7);
            for (int i = 2; i < this.input.stairVertices.size; i += 2)
            {
                this.editor.shapeRenderer.setColor(Color.WHITE);
                this.editor.shapeRenderer.line(this.input.stairVertices.get(oldIndex) + this.input.stairVerticePosition.x, this.input.stairVertices.get(oldIndex + 1) + this.input.stairVerticePosition.y + initialheight, this.input.stairVertices.get(i) + this.input.stairVerticePosition.x, this.input.stairVertices.get(i + 1) + this.input.stairVerticePosition.y + initialheight);
                this.editor.shapeRenderer.setColor(Color.GRAY);
                this.editor.shapeRenderer.line(this.input.stairVertices.get(oldIndex) + this.input.stairVerticePosition.x, this.input.stairVertices.get(oldIndex + 1) + this.input.stairVerticePosition.y, this.input.stairVertices.get(i) + this.input.stairVerticePosition.x, this.input.stairVertices.get(i + 1) + this.input.stairVerticePosition.y);
                if(i >= 6)
                {
                    this.editor.shapeRenderer.line(this.input.stairVertices.get(6) + this.input.stairVerticePosition.x, this.input.stairVertices.get(7) + this.input.stairVerticePosition.y, this.input.stairVertices.get(0) + this.input.stairVerticePosition.x, this.input.stairVertices.get(1) + this.input.stairVerticePosition.y);
                }
                oldIndex += 2;
            }
        }

        this.editor.shapeRenderer.setColor(Color.WHITE);
        oldIndex = 0;
        if (this.input.stairVertices.size >= 2)
        {
            this.editor.shapeRenderer.circle(this.input.stairVertices.get(0) + this.input.stairVerticePosition.x, this.input.stairVertices.get(1) + this.input.stairVerticePosition.y, .1f, 7);
            float fromHeight = 0;
            float toHeight = 0;
            for (int i = 2; i < this.input.stairVertices.size; i += 2)
            {
                if(i == 2)
                {
                    fromHeight = initialheight;
                    toHeight = finalheight;
                }
                else if(i == 4)
                {
                    fromHeight = finalheight;
                    toHeight = finalheight;
                }
                else
                {
                    fromHeight = finalheight;
                    toHeight = initialheight;
                }
                this.editor.shapeRenderer.line(this.input.stairVertices.get(oldIndex) + this.input.stairVerticePosition.x, this.input.stairVertices.get(oldIndex + 1) + this.input.stairVerticePosition.y + fromHeight, this.input.stairVertices.get(i) + this.input.stairVerticePosition.x, this.input.stairVertices.get(i + 1) + this.input.stairVerticePosition.y + toHeight);
                this.editor.shapeRenderer.line(this.input.stairVertices.get(0) + this.input.stairVerticePosition.x, this.input.stairVertices.get(1) + this.input.stairVerticePosition.y, this.input.stairVertices.get(0) + this.input.stairVerticePosition.x, this.input.stairVertices.get(1) + this.input.stairVerticePosition.y + initialheight);
                if(i >= 2)
                    this.editor.shapeRenderer.line(this.input.stairVertices.get(2) + this.input.stairVerticePosition.x, this.input.stairVertices.get(3) + this.input.stairVerticePosition.y, this.input.stairVertices.get(2) + this.input.stairVerticePosition.x, this.input.stairVertices.get(3) + this.input.stairVerticePosition.y + finalheight);
                if(i >= 4)
                    this.editor.shapeRenderer.line(this.input.stairVertices.get(4) + this.input.stairVerticePosition.x, this.input.stairVertices.get(5) + this.input.stairVerticePosition.y, this.input.stairVertices.get(4) + this.input.stairVerticePosition.x, this.input.stairVertices.get(5) + this.input.stairVerticePosition.y + finalheight);
                if(i >= 6)
                {
                    this.editor.shapeRenderer.line(this.input.stairVertices.get(6) + this.input.stairVerticePosition.x, this.input.stairVertices.get(7) + this.input.stairVerticePosition.y + toHeight, this.input.stairVertices.get(0) + this.input.stairVerticePosition.x, this.input.stairVertices.get(1) + this.input.stairVerticePosition.y + toHeight);
                    this.editor.shapeRenderer.line(this.input.stairVertices.get(6) + this.input.stairVerticePosition.x, this.input.stairVertices.get(7) + this.input.stairVerticePosition.y, this.input.stairVertices.get(6) + this.input.stairVerticePosition.x, this.input.stairVertices.get(7) + this.input.stairVerticePosition.y + initialheight);
                }
                oldIndex += 2;
            }
            if(Utils.isFileToolThisType(editor, Tools.DRAWRECTANGLE))
                this.editor.shapeRenderer.line(this.input.stairVertices.get(oldIndex) + this.input.stairVerticePosition.x, this.input.stairVertices.get(oldIndex + 1) + this.input.stairVerticePosition.y, this.input.stairVertices.get(0) + this.input.stairVerticePosition.x, this.input.stairVertices.get(1) + this.input.stairVerticePosition.y);
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
        if(editor.fileMenu.toolPane.selectAttachedSprites.selected && this.selectedLayer instanceof SpriteLayer)
        {
            for(int i = 0; i < this.selectedLayer.children.size; i ++)
            {
                MapSprite mapSprite = (MapSprite) this.selectedLayer.children.get(i);

                if(mapSprite.attachedSprites != null)
                {
                    for (int k = mapSprite.attachedSprites.children.size - 1; k >= 0; k--)
                    {
                        MapSprite attachedSprite = mapSprite.attachedSprites.children.get(k);
                        if (attachedSprite.isHoveredOver(this.input.boxSelect.getVertices()))
                        {
                            if(attachedSprite.selected)
                                attachedSprite.drawSelectedHoveredOutline();
                            else
                                attachedSprite.drawHoverOutline();
                        }
                    }
                }
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
                this.camera.zoom = this.zoom;
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

    private void drawDepthIndexes()
    {
        if(!editor.fileMenu.toolPane.depth.selected)
            return;
        this.editor.batch.setProjectionMatrix(this.editor.stage.getCamera().combined);
        for(int i = 0; i < layers.size; i ++)
        {
            Layer layer = layers.get(i);
            if(layer instanceof SpriteLayer)
            {
                SpriteLayer spriteLayer = (SpriteLayer) layer;
                sprite:
                for(int k = 0; k < spriteLayer.children.size; k ++)
                {
                    MapSprite mapSprite = spriteLayer.children.get(k);
                    if(mapSprite.attachedMapObjects == null)
                        continue sprite;
                    boolean collisionSort = false;
                    for(int s = 0; s < mapSprite.attachedMapObjects.size; s ++)
                    {
                        MapObject mapObject = mapSprite.attachedMapObjects.get(s);
                        if(Utils.getPropertyField(mapObject.properties, "collisionSort") != null || Utils.getPropertyField(mapObject.properties, "collisionSortBack") != null)
                            collisionSort = true;
                    }
                    if(!collisionSort)
                        continue sprite;

                    Vector3 project = Utils.project(camera, mapSprite.getX() + mapSprite.width / 2f, mapSprite.getY() + mapSprite.height / 2f);
                    Utils.centerPrint(editor.batch, "" + k, project.x, project.y);
                }
            }
        }
        this.editor.batch.setProjectionMatrix(this.camera.combined);
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
            m[Matrix4.M11] += this.camera.position.y / ((-10f * this.camera.zoom) / skew) - ((.097f * antiDepth) / (antiDepth + .086f));

            this.camera.invProjectionView.set(this.camera.combined);
            Matrix4.inv(this.camera.invProjectionView.val);
            this.camera.frustum.update(this.camera.invProjectionView);
        }

        if(layer == null || layer.layerField.visibleImg.isVisible())
        {
            this.rayHandler.setCombinedMatrix(this.camera.combined, this.camera.position.x, this.camera.position.y, this.camera.viewportWidth * this.camera.zoom * 2f, this.camera.viewportHeight * this.camera.zoom * 2f);
            this.rayHandler.updateAndRender();
        }

        if(this.editor.fileMenu.toolPane.perspective.selected)
            this.camera.update();
    }

    private void drawObjectLayers()
    {
        if(!layerMenu.toolPane.objectVisibility.isSelected)
            return;
        for(int i = 0; i < this.layers.size; i ++)
        {
            if(this.layers.get(i) instanceof ObjectLayer)
            {
                if (this.layers.get(i).layerField.visibleImg.isVisible() && this.layers.get(i).overrideSprite == null)
                    this.layers.get(i).draw();
            }
        }
        if(this.groupPolygons != null)
            this.groupPolygons.draw();
    }

    private void printDustTypes()
    {
        for(int i = 0; i < this.layers.size; i ++)
        {
            Layer layer = this.layers.get(i);
            if(layer instanceof ObjectLayer)
            {
                ObjectLayer objectLayer = (ObjectLayer) layer;
                if(objectLayer.spriteGrid != null)
                    objectLayer.spriteGrid.drawTypes();
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
                if (objectLayer.layerField.visibleImg.isVisible() && objectLayer.overrideSprite == null && this.editor.fileMenu.toolPane.blocked.selected && objectLayer.spriteGrid != null)
                    objectLayer.spriteGrid.drawBlocked();
            }
        }
    }

    private void drawSpriteGrid()
    {
        for(int i = 0; i < this.layers.size; i ++)
        {
            if(this.layers.get(i) instanceof ObjectLayer)
            {
                ObjectLayer objectLayer = (ObjectLayer) this.layers.get(i);
                if (objectLayer.layerField.visibleImg.isVisible() && objectLayer.overrideSprite == null && this.editor.fileMenu.toolPane.spriteGridColors.selected && objectLayer.spriteGrid != null)
                    objectLayer.spriteGrid.drawColor();
            }
        }
    }

    private void drawGrid()
    {
        if(this.selectedLayer != null)
        {
            this.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
            this.editor.shapeRenderer.setColor(Color.BLACK);
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
            this.editor.shapeRenderer.setColor(Color.BLACK);
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
            return null;
        if(this.editor.fileMenu.toolPane.random.selected && this.randomSpriteIndex < this.spriteMenu.selectedSpriteTools.size)
            return this.spriteMenu.selectedSpriteTools.get(this.randomSpriteIndex);
        return this.spriteMenu.selectedSpriteTools.first();
    }

    public void shuffleRandomSpriteTool(boolean ignoreFencePost)
    {
        if(getSpriteToolFromSelectedTools() == null)
            return;

        // Randomly pick a sprite from the selected sprites based on weighted probabilities
        float totalSum = 0;
        float partialSum = 0;
        for(int i = 0; i < getAllSelectedSpriteTools().size; i ++)
        {
            float probability = Float.parseFloat(Utils.getLockedPropertyField(getAllSelectedSpriteTools().get(i).lockedProperties, "Probability").value.getText());
            totalSum += probability;
        }
        float random = Utils.randomFloat(0, totalSum);
        for(int i = 0; i < getAllSelectedSpriteTools().size; i ++)
        {
            float probability = Float.parseFloat(Utils.getLockedPropertyField(getAllSelectedSpriteTools().get(i).lockedProperties, "Probability").value.getText());
            partialSum += probability;
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
                float randomRotation = this.editor.fileMenu.toolPane.minMaxDialog.randomRotationValue;
                float randomR = this.editor.fileMenu.toolPane.minMaxDialog.randomRValue;
                float randomG = this.editor.fileMenu.toolPane.minMaxDialog.randomGValue;
                float randomB = this.editor.fileMenu.toolPane.minMaxDialog.randomBValue;
                float randomA = this.editor.fileMenu.toolPane.minMaxDialog.randomAValue;
                Sprite previewSprite = spriteTool.previewSprites.get(i);
                if(this.editor.fileMenu.toolPane.perspective.selected && Utils.doesLayerHavePerspective(this, this.selectedLayer))
                {
                    x -= previewSprite.getWidth() / 2;
                    y -= previewSprite.getHeight() / 2;
                    camera.update();
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
                    if(antiDepth >= .1f)
                        skew /= antiDepth * 15;
                    m[Matrix4.M31] += skew;
                    m[Matrix4.M11] += this.camera.position.y / ((-10f * this.camera.zoom) / skew) - ((.097f * antiDepth) / (antiDepth + .086f));
                    this.camera.invProjectionView.set(this.camera.combined);
                    Matrix4.inv(this.camera.invProjectionView.val);
                    this.camera.frustum.update(this.camera.invProjectionView);

                    float yScaleDisplacement = 0;
                    float xScaleDisplacement = 0;
                    float spriteAtlasWidth = previewSprite.getRegionWidth() / 64;
                    float spriteAtlasHeight = previewSprite.getRegionHeight() / 64;
                    float whiteSpaceWidth = (previewSprite.getWidth() - spriteAtlasWidth);

                    xScaleDisplacement = previewSprite.getWidth() / 2;

                    Vector3 p = Utils.project(this.camera, x + xScaleDisplacement, y);
                    x = p.x;
                    y = Gdx.graphics.getHeight() - p.y;
                    this.camera.update();
                    p = Utils.unproject(this.camera, x, y);
                    x = p.x;
                    y = p.y;

                    yScaleDisplacement = ((spriteAtlasHeight * previewSprite.getScaleY()) - spriteAtlasHeight) / 2f;
                    xScaleDisplacement = -(spriteAtlasWidth / 2);
                    xScaleDisplacement -= (whiteSpaceWidth * previewSprite.getScaleX() / 2);

                    previewSprite.setPosition(x + xScaleDisplacement, y + yScaleDisplacement);

                    float perspectiveScale = Utils.getPerspectiveScaleFactor(this, selectedLayer, this.camera, y);

                    previewSprite.setScale(perspectiveScale);
                }
                else
                {
                    previewSprite.setScale(randomScale, randomScale);
                    previewSprite.setRotation(randomRotation);
                    previewSprite.setColor(randomR, randomG, randomB, randomA);
                    previewSprite.setPosition(coords.x - previewSprite.getWidth() / 2, coords.y - previewSprite.getHeight() / 2);
                }
            }
        }

        if(!ignoreFencePost && editor.fileMenu.toolPane.fence.selected){
            SpriteTool spriteTool = getSpriteToolFromSelectedTools();
            if(!Utils.canBuildFenceFromSelectedSpriteTools(this))
                return;
            if (!spriteTool.hasAttachedMapObjects()) {
                shuffleRandomSpriteTool(false);
                return;
            }
            boolean hasFencePost = false;
            for (int i = 0; i < spriteTool.attachedMapObjectManagers.size; i++) {
                AttachedMapObjectManager attachedMapObjectManager = spriteTool.attachedMapObjectManagers.get(i);
                if (Utils.getPropertyField(attachedMapObjectManager.properties, "fenceStart") != null)
                    hasFencePost = true;
            }
            if (!hasFencePost) {
                shuffleRandomSpriteTool(false);
                return;
            }
        }
    }

    public void updateLayerSpriteGrids()
    {
        for(int i = 0; i < this.layers.size; i ++)
        {
            Layer layer = this.layers.get(i);
            if(layer instanceof ObjectLayer)
            {
                ObjectLayer objectLayer = (ObjectLayer) layer;
                objectLayer.updateSpriteGrid();
            }
        }
    }

    public void executeCommand(Command command)
    {
        command.execute();
        Utils.println();
        Utils.println("execute " + command);
        pushCommand(command);
        setChanged(true);
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
                if (Command.shouldExecute(this, DeleteSelectedMapSprites.class))
                {
                    DeleteSelectedMapSprites deleteSelectedMapSprites = new DeleteSelectedMapSprites(this.selectedSprites, (SpriteLayer) this.selectedLayer);
                    executeCommand(deleteSelectedMapSprites);
                }
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

    public void colorizeGroup()
    {
        if(this.selectedLayer == null)
            return;

        if(this.groupPolygons == null)
            return;

        for(int i = 0; i < groupPolygons.children.size; i ++)
        {
            MapPolygon mapPolygon = (MapPolygon) groupPolygons.children.get(i);
            if(mapPolygon.mapSprites != null)
            {
                for(int k = 0; k < mapPolygon.mapSprites.size; k ++)
                {
                    MapSprite mapSprite = mapPolygon.mapSprites.get(k);
                    ColorPropertyField colorProperty = Utils.getLockedColorField("Tint", mapSprite.lockedProperties);
                    mapSprite.setColor(colorProperty.getR(), colorProperty.getG(), colorProperty.getB(), colorProperty.getA());
                }
            }
        }

        for(int i = 0; i < groupPolygons.children.size; i ++)
        {
            MapPolygon mapPolygon = (MapPolygon) groupPolygons.children.get(i);
            if(mapPolygon.selected && mapPolygon.mapSprites != null)
            {
                for(int k = 0; k < mapPolygon.mapSprites.size; k ++)
                    mapPolygon.mapSprites.get(k).sprite.setColor(0, 1, 0, 1);
            }
        }
    }

    public void loadMap(MapData mapData, boolean setDefaultsOnly)
    {
        MapSprite.resetIdCounter();
        if(!setDefaultsOnly)
        {
            this.name = mapData.name;

            // map properties
            int propSize = mapData.props.size();
            for (int s = 0; s < propSize; s++)
            {
                PropertyData propertyData = mapData.props.get(s);
                propertyMenu.newProperty(propertyData, propertyMenu.mapPropertyPanel.properties);
            }
            // map locked properties
            propSize = mapData.lProps.size();
            for (int s = 0; s < propSize; s++)
            {
                PropertyData propertyData = mapData.lProps.get(s);
                propertyMenu.changeLockedPropertyValue(propertyData, propertyMenu.mapPropertyPanel.lockedProperties);
            }
        }
        else
        {
            // delete all properties and things
            for(int i = 0; i < spriteMenu.spriteSheets.size; i ++)
            {
                SpriteSheet spriteSheet = spriteMenu.spriteSheets.get(i);
                for(int k = 0; k < spriteSheet.children.size; k ++)
                {
                    Table child = spriteSheet.children.get(k);
                    SpriteTool spriteTool = child.findActor("spriteTool");
                    if(spriteTool.hasAttachedMapObjects())
                    {
                        for (int s = 0; s < spriteTool.attachedMapObjectManagers.size; s++)
                        {
                            spriteTool.removeAttachedMapObject(spriteTool.attachedMapObjectManagers.get(s).attachedMapObjects.first());
                            s --;
                        }
                    }
                }
            }
        }

        // create sprite sheets
        int size = mapData.sheets.size();
        for(int i = 0; i < size; i ++)
        {
            SpriteSheetData spriteSheetData = mapData.sheets.get(i);
            String sheetName;
            if(Utils.isSpriteSheetInFolder("editor" + Utils.capitalize(spriteSheetData.name)))
                sheetName = "editor" + Utils.capitalize(spriteSheetData.name);
            else
                sheetName = spriteSheetData.name;
            spriteMenu.createSpriteSheet(sheetName);
            // sprite tool properties
            if(spriteSheetData.tools != null)
            {
                int toolSize = spriteSheetData.tools.size();
                for (int k = 0; k < toolSize; k++)
                {
                    ToolData toolData = spriteSheetData.tools.get(k);
                    SpriteTool spriteTool = spriteMenu.getSpriteTool(toolData.n, sheetName);
                    if (spriteTool == null)
                        continue;
                    spriteTool.properties.clear();

                    // properties
                    int propSize = toolData.props.size();
                    for (int s = 0; s < propSize; s++)
                    {
                        PropertyData propertyData = toolData.props.get(s);
                        propertyMenu.newProperty(propertyData, spriteTool.properties);
                    }
                    // locked properties
                    propSize = toolData.lProps.size();
                    for (int s = 0; s < propSize; s++)
                    {
                        PropertyData propertyData = toolData.lProps.get(s);
                        propertyMenu.changeLockedPropertyValue(propertyData, spriteTool.lockedProperties);
                    }

                    // attached map objects
                    if (toolData.objs != null)
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
                            mapObject.flickerId = mapObjectData.fId;
                            // attached manager
                            spriteTool.createAttachedMapObject(this, mapObject, mapObjectData.offsetX, mapObjectData.offsetY);
                            if (setDefaultsOnly)
                                mapObject.attachedMapObjectManager.addCopyOfMapObjectToAllMapSpritesOfThisSpriteTool(mapObject);
                            // object properties
                            propSize = mapObjectData.props.size();
                            mapObject.properties.clear();
                            for (int p = 0; p < propSize; p++)
                            {
                                PropertyData propertyData = mapObjectData.props.get(p);
                                propertyMenu.newProperty(propertyData, mapObject.properties);
                            }
                        }
                    }
                }
            }
        }

        if(!setDefaultsOnly)
        {
            // create layers
            for (int i = mapData.layers.size() - 1; i >= 0; i--)
            {
                LayerData layerData = mapData.layers.get(i);
                Layer layer;
                if (layerData instanceof SpriteLayerData)
                    layer = this.layerMenu.newLayer(LayerTypes.SPRITE);
                else
                    layer = this.layerMenu.newLayer(LayerTypes.OBJECT);
                layer.layerField.layerName.setText(layerData.name);
                layer.setPosition(layerData.x, layerData.y);
                layer.resize(layerData.w, layerData.h, false, false);
                layer.setZ(layerData.z);
                LayerField.createOrRemoveGrid(layer, layer.layerField.layerName);

                // layer properties
                int propSize = layerData.props.size();
                for (int p = 0; p < propSize; p++)
                {
                    PropertyData propertyData = layerData.props.get(p);
                    propertyMenu.newProperty(propertyData, layer.properties);
                }

                // create layer children
                if (layerData instanceof SpriteLayerData)
                {
                    SpriteLayerData spriteLayerData = (SpriteLayerData) layerData;
                    int childSize = spriteLayerData.children.size();
                    parent:
                    for (int k = 0; k < childSize; k++)
                    {
                        LayerChildData mapSpriteData = spriteLayerData.children.get(k);
                        if(mapSpriteData instanceof AttachedMapSpriteData)
                        {
                            AttachedMapSpriteData attachedMapSpriteData = (AttachedMapSpriteData) mapSpriteData;
                            MapSprite parentMapSprite = null;
                            for(int s = 0; s < attachedMapSpriteData.sprites.size(); s++)
                            {
                                MapSpriteData attachedData = attachedMapSpriteData.sprites.get(s);
                                if(attachedData.parent)
                                {
                                    parentMapSprite = loadMapSpriteData(attachedData, layer);
                                    if(parentMapSprite == null)
                                        continue parent;
                                    ((SpriteLayer) layer).addMapSprite(parentMapSprite);
                                    break;
                                }
                            }
                            parentMapSprite.attachedSprites = new SpriteLayer(editor, this, null);
                            child:
                            for(int s = 0; s < attachedMapSpriteData.sprites.size(); s++)
                            {
                                MapSpriteData attachedData = attachedMapSpriteData.sprites.get(s);
                                if (attachedData.parent)
                                {
                                    parentMapSprite.attachedSprites.addMapSprite(parentMapSprite);
                                    continue;
                                }
                                MapSprite childMapSprite = loadMapSpriteData(attachedData, parentMapSprite.attachedSprites);
                                if(childMapSprite == null)
                                    continue child;
                                parentMapSprite.attachedSprites.addMapSprite(childMapSprite);
                                childMapSprite.parentSprite = parentMapSprite;
                            }
                        }
                        else
                        {
                            MapSprite mapSprite = loadMapSpriteData((MapSpriteData) mapSpriteData, layer);
                            if(mapSprite != null)
                                ((SpriteLayer) layer).addMapSprite(mapSprite);
                        }
                    }
                } else if (layerData instanceof ObjectLayerData)
                {
                    ObjectLayerData objectLayerData = (ObjectLayerData) layerData;
                    int childSize = objectLayerData.children.size();
                    for (int k = 0; k < childSize; k++)
                    {
                        MapObjectData mapObjectData = objectLayerData.children.get(k);
                        MapObject mapObject;
                        if (mapObjectData instanceof MapPolygonData)
                        {
                            MapPolygonData mapPolygonData = (MapPolygonData) mapObjectData;
                            MapPolygon mapPolygon = new MapPolygon(this, layer, mapPolygonData.verts, mapPolygonData.x, mapPolygonData.y);
                            mapObject = mapPolygon;
                            ((ObjectLayer) layer).addMapObject(mapPolygon);
                        } else
                        {
                            MapPointData mapPointData = (MapPointData) mapObjectData;
                            MapPoint mapPoint = new MapPoint(this, layer, mapPointData.x, mapPointData.y);
                            mapObject = mapPoint;
                            ((ObjectLayer) layer).addMapObject(mapPoint);
                        }
                        mapObject.flickerId = mapObjectData.fId;
                        // object properties
                        propSize = mapObjectData.props.size();
                        for (int s = 0; s < propSize; s++)
                        {
                            PropertyData propertyData = mapObjectData.props.get(s);
                            propertyMenu.newProperty(propertyData, mapObject.properties);
                        }
                    }
                    layer.children.sort();
                }
            }

            // edge sprites and ID's
            for(int i = 0; i < layers.size; i ++)
            {
                Layer layer = layers.get(i);
                if (layer instanceof SpriteLayer)
                {
                    // ID
                    for (int s = 0; s < layer.children.size; s++)
                    {
                        MapSprite mapSprite = (MapSprite) layer.children.get(s);
                        if (mapSprite.attachedSprites != null)
                        {
                            for (int k = 0; k < mapSprite.attachedSprites.children.size; k++)
                            {
                                MapSprite attached = mapSprite.attachedSprites.children.get(k);
                                attached.setID(attached.getAndIncrementId());
                            }
                        } else
                        {
                            mapSprite.setID(MapSprite.getAndIncrementId());
                        }
                    }
                }
            }
            for(int i = 0; i < layers.size; i ++)
            {
                Layer layer = layers.get(i);
                if(layer instanceof SpriteLayer)
                {
                    // Edge
                    edge:
                    for (int s = 0; s < layer.children.size; s++)
                    {
                        MapSprite mapSprite = (MapSprite) layer.children.get(s);
                        long edgeId = mapSprite.edgeId;
                        if (edgeId <= 0)
                            continue edge;
                        for(int m = 0; m < layers.size; m ++)
                        {
                            Layer toLayer = layers.get(m);
                            if(toLayer instanceof SpriteLayer)
                            {
                                for (int k = 0; k < toLayer.children.size; k++)
                                {
                                    MapSprite edge = (MapSprite) toLayer.children.get(k);
                                    int id = edge.id;
                                    if (edgeId == id)
                                    {
                                        SnapMapSpriteEdge snapMapSpriteEdge = new SnapMapSpriteEdge(mapSprite, edge);
                                        executeCommand(snapMapSpriteEdge);
                                        continue edge;
                                    }
                                }
                            }
                        }
                    }

                    // Flicker
                    flicker:
                    for (int s = 0; s < layer.children.size; s++)
                    {
                        MapSprite mapSprite = (MapSprite) layer.children.get(s);
                        long flickerId = mapSprite.flickerId;
                        if (flickerId <= 0)
                            continue flicker;
                        for(int m = 0; m < layers.size; m ++)
                        {
                            Layer toLayer = layers.get(m);
                            if (toLayer instanceof SpriteLayer)
                            {
                                for (int k = 0; k < toLayer.children.size; k++)
                                {
                                    MapSprite flicker = (MapSprite) toLayer.children.get(k);
                                    int id = flicker.id;
                                    if (flickerId == id)
                                    {
                                        SnapMapSpriteFlicker snapMapSpriteFlicker = new SnapMapSpriteFlicker(mapSprite, flicker);
                                        executeCommand(snapMapSpriteFlicker);
                                        continue flicker;
                                    }
                                }
                            }
                        }
                    }
                }
                else if(layer instanceof ObjectLayer)
                {
                    // Flicker
                    flicker:
                    for (int s = 0; s < layer.children.size; s++)
                    {
                        MapObject mapObject = (MapObject) layer.children.get(s);
                        long flickerId = mapObject.flickerId;
                        if (flickerId <= 0)
                            continue flicker;
                        for(int m = 0; m < layers.size; m++)
                        {
                            Layer toLayer = layers.get(m);
                            if(toLayer instanceof SpriteLayer)
                            {
                                for (int k = 0; k < toLayer.children.size; k++)
                                {
                                    MapSprite flicker = (MapSprite) toLayer.children.get(k);
                                    int id = flicker.id;
                                    if (flickerId == id)
                                    {
                                        SnapMapSpriteFlicker snapMapSpriteFlicker = new SnapMapSpriteFlicker(mapObject, flicker);
                                        executeCommand(snapMapSpriteFlicker);
                                        continue flicker;
                                    }
                                }
                            }
                        }
                    }
                }

            }

            // re-override the layers
            for(int i = 0; i < layers.size; i ++)
            {
                Layer layer = layers.get(i);
                if(!(layer instanceof SpriteLayer))
                    continue;
                SpriteLayer spriteLayer = (SpriteLayer) layer;
                for(int k = 0; k < spriteLayer.children.size; k ++)
                {
                    MapSprite mapSprite = spriteLayer.children.get(k);
                    if(mapSprite.layerOverrideIndex > 0)
                    {
                        mapSprite.layerOverride = layers.get(mapSprite.layerOverrideIndex - 1);
                        mapSprite.layerOverride.overrideSprite = mapSprite;
                    }
                }
            }



            // groups
            if(mapData.groups != null)
            {
                groupPolygons = new ObjectLayer(editor, this, null);
                for(int i = 0; i < mapData.groups.size(); i++)
                {
                    GroupMapPolygonData mapPolygonData = mapData.groups.get(i);
                    MapPolygon mapPolygon = new MapPolygon(this, groupPolygons, mapPolygonData.verts, mapPolygonData.x, mapPolygonData.y);
                    (groupPolygons).addMapObject(mapPolygon);
                    mapPolygon.mapSprites = new Array<>();
                    // object properties
                    int propSize = mapPolygonData.props.size();
                    for (int s = 0; s < propSize; s++)
                    {
                        PropertyData propertyData = mapPolygonData.props.get(s);
                        propertyMenu.newProperty(propertyData, mapPolygon.properties);
                    }
                }

                for(int i = 0; i < layers.size; i ++)
                {
                    Layer layer = layers.get(i);
                    if(!(layer instanceof SpriteLayer))
                        continue;
                    for (int s = 0; s < layer.children.size; s++)
                    {
                        MapSprite mapSprite = (MapSprite) layer.children.get(s);
                        if(mapSprite.attachedSprites != null)
                        {
                            for (int m = 0; m < mapSprite.attachedSprites.children.size; m++)
                            {
                                MapSprite attachedMapSprite = mapSprite.attachedSprites.children.get(m);
                                for (int k = 0; k < mapData.groups.size(); k++)
                                {
                                    if (mapData.groups.get(k).mapSpriteIDs != null && mapData.groups.get(k).mapSpriteIDs.contains(attachedMapSprite.id))
                                    {
                                        MapPolygon mapPolygon = (MapPolygon) groupPolygons.children.get(k);
                                        mapPolygon.mapSprites.add(attachedMapSprite);
                                    }
                                }
                            }
                        }
                        else
                        {
                            for (int k = 0; k < mapData.groups.size(); k++)
                            {
                                if (mapData.groups.get(k).mapSpriteIDs != null && mapData.groups.get(k).mapSpriteIDs.contains(mapSprite.id))
                                {
                                    MapPolygon mapPolygon = (MapPolygon) groupPolygons.children.get(k);
                                    mapPolygon.mapSprites.add(mapSprite);
                                }
                            }
                        }
                    }
                }
            }



        }
        PropertyToolPane.apply(this);
        propertyMenu.mapPropertyPanel.apply();
    }

    public void loadMap(MapData mapData, String defaultSheet, String currentSheet)
    {
        MapSprite.resetIdCounter();


        SpriteSheet spriteSheet = null;
        // delete all properties and things
        for(int i = 0; i < spriteMenu.spriteSheets.size; i ++)
        {
            if(spriteMenu.spriteSheets.get(i).name.equals(currentSheet))
                spriteSheet = spriteMenu.spriteSheets.get(i);
        }
        if(spriteSheet != null)
        {
            for (int k = 0; k < spriteSheet.children.size; k++)
            {
                Table child = spriteSheet.children.get(k);
                SpriteTool spriteTool = child.findActor("spriteTool");
                if (spriteTool.hasAttachedMapObjects())
                {
                    for (int s = 0; s < spriteTool.attachedMapObjectManagers.size; s++)
                    {
                        spriteTool.removeAttachedMapObject(spriteTool.attachedMapObjectManagers.get(s).attachedMapObjects.first());
                        s--;
                    }
                }
            }
        }

        // create sprite sheets
        int size = mapData.sheets.size();
        SpriteSheetData spriteSheetData = null;
        for(int i = 0; i < size; i ++)
        {
            if(mapData.sheets.get(i).name.equals(defaultSheet))
                spriteSheetData = mapData.sheets.get(i);
        }

        String sheetName;
        if(Utils.isSpriteSheetInFolder("editor" + Utils.capitalize(currentSheet)))
            sheetName = "editor" + Utils.capitalize(currentSheet);
        else
            sheetName = currentSheet;
        spriteMenu.createSpriteSheet(sheetName);
        // sprite tool properties
        if(spriteSheetData.tools != null)
        {
            int toolSize = spriteSheetData.tools.size();
            for (int k = 0; k < toolSize; k++)
            {
                ToolData toolData = spriteSheetData.tools.get(k);
                SpriteTool spriteTool = spriteMenu.getSpriteTool(toolData.n, sheetName);
                if (spriteTool == null)
                    continue;
                spriteTool.properties.clear();

                // properties
                int propSize = toolData.props.size();
                for (int s = 0; s < propSize; s++)
                {
                    PropertyData propertyData = toolData.props.get(s);
                    propertyMenu.newProperty(propertyData, spriteTool.properties);
                }
                // locked properties
                propSize = toolData.lProps.size();
                for (int s = 0; s < propSize; s++)
                {
                    PropertyData propertyData = toolData.lProps.get(s);
                    propertyMenu.changeLockedPropertyValue(propertyData, spriteTool.lockedProperties);
                }

                // attached map objects
                if (toolData.objs != null)
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
                        mapObject.flickerId = mapObjectData.fId;
                        // attached manager
                        spriteTool.createAttachedMapObject(this, mapObject, mapObjectData.offsetX, mapObjectData.offsetY);
                        mapObject.attachedMapObjectManager.addCopyOfMapObjectToAllMapSpritesOfThisSpriteTool(mapObject);
                        // object properties
                        propSize = mapObjectData.props.size();
                        mapObject.properties.clear();
                        for (int p = 0; p < propSize; p++)
                        {
                            PropertyData propertyData = mapObjectData.props.get(p);
                            propertyMenu.newProperty(propertyData, mapObject.properties);
                        }
                    }
                }
            }
        }

        PropertyToolPane.apply(this);
        propertyMenu.mapPropertyPanel.apply();
    }

    private MapSprite loadMapSpriteData(MapSpriteData mapSpriteData, Layer layer)
    {
        String sheetName;
        if (Utils.isSpriteSheetInFolder("editor" + Utils.capitalize(mapSpriteData.sN)))
            sheetName = "editor" + Utils.capitalize(mapSpriteData.sN);
        else
            sheetName = mapSpriteData.sN;
        SpriteTool spriteTool = spriteMenu.getSpriteTool(mapSpriteData.n, sheetName);
        if(spriteTool == null)
            return null;
        MapSprite mapSprite = new MapSprite(this, layer, spriteTool, mapSpriteData.x, mapSpriteData.y);
        mapSprite.edgeId = mapSpriteData.eId;
        mapSprite.flickerId = mapSpriteData.fId;
        LabelFieldPropertyValuePropertyField fenceProperty = Utils.getLockedPropertyField(mapSprite.lockedProperties, "Fence");
        if(mapSpriteData.fence)
            fenceProperty.value.setText("true");
        else
            fenceProperty.value.setText("false");

        LabelFieldPropertyValuePropertyField ignoreProperty = Utils.getLockedPropertyField(mapSprite.lockedProperties, "IgnoreProps");
        if(mapSpriteData.ignoreProps)
            ignoreProperty.value.setText("true");
        else
            ignoreProperty.value.setText("false");

        mapSprite.setZ(mapSpriteData.z);
        mapSprite.setScale(mapSpriteData.scl + MapSpriteData.defaultScaleValue);
        mapSprite.setColor(mapSpriteData.r + MapSpriteData.defaultColorValue, mapSpriteData.g + MapSpriteData.defaultColorValue, mapSpriteData.b + MapSpriteData.defaultColorValue, mapSpriteData.a + MapSpriteData.defaultColorValue);
        mapSprite.setPosition(mapSpriteData.x, mapSpriteData.y);
        Utils.setCenterOrigin(mapSprite.getX(), mapSprite.getY());
        mapSprite.setRotation(mapSpriteData.rot);
        mapSprite.layerOverrideIndex = mapSpriteData.loi;
        mapSprite.x1Offset = mapSpriteData.x1;
        mapSprite.y1Offset = mapSpriteData.y1;
        mapSprite.x2Offset = mapSpriteData.x2;
        mapSprite.y2Offset = mapSpriteData.y2;
        mapSprite.x3Offset = mapSpriteData.x3;
        mapSprite.y3Offset = mapSpriteData.y3;
        mapSprite.x4Offset = mapSpriteData.x4;
        mapSprite.y4Offset = mapSpriteData.y4;
        float[] spriteVertices = mapSprite.sprite.getVertices();
        mapSprite.offsetMovebox1.setPosition(spriteVertices[SpriteBatch.X2] + mapSprite.x1Offset - mapSprite.offsetMovebox1.width / 2f * mapSprite.offsetMovebox1.scale, spriteVertices[SpriteBatch.Y2] + mapSprite.y1Offset - mapSprite.offsetMovebox1.height / 2f * mapSprite.offsetMovebox1.scale);
        mapSprite.offsetMovebox2.setPosition(spriteVertices[SpriteBatch.X3] + mapSprite.x2Offset - mapSprite.offsetMovebox2.width / 2f * mapSprite.offsetMovebox2.scale, spriteVertices[SpriteBatch.Y3] + mapSprite.y2Offset - mapSprite.offsetMovebox2.height / 2f * mapSprite.offsetMovebox2.scale);
        mapSprite.offsetMovebox3.setPosition(spriteVertices[SpriteBatch.X4] + mapSprite.x3Offset - mapSprite.offsetMovebox3.width / 2f * mapSprite.offsetMovebox3.scale, spriteVertices[SpriteBatch.Y4] + mapSprite.y3Offset - mapSprite.offsetMovebox3.height / 2f * mapSprite.offsetMovebox3.scale);
        mapSprite.offsetMovebox4.setPosition(spriteVertices[SpriteBatch.X1] + mapSprite.x4Offset - mapSprite.offsetMovebox4.width / 2f * mapSprite.offsetMovebox4.scale, spriteVertices[SpriteBatch.Y1] + mapSprite.y4Offset - mapSprite.offsetMovebox4.height / 2f * mapSprite.offsetMovebox4.scale);
        mapSprite.polygon.setOffset(mapSprite.x1Offset, mapSprite.x2Offset, mapSprite.x3Offset, mapSprite.x4Offset, mapSprite.y1Offset, mapSprite.y2Offset, mapSprite.y3Offset, mapSprite.y4Offset);

        if(mapSpriteData.props != null)
        {
            for (int i = 0; i < mapSpriteData.props.size(); i++)
                propertyMenu.newProperty(mapSpriteData.props.get(i), mapSprite.instanceSpecificProperties);
        }

        // attached map objects
        if (mapSpriteData.objs != null)
        {
            int objSize = mapSpriteData.objs.size();
            for (int s = 0; s < objSize; s++)
            {
                MapObjectData mapObjectData = mapSpriteData.objs.get(s);
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
                mapObject.flickerId = mapObjectData.fId;
                // attached manager
                mapSprite.createAttachedMapObject(this, mapObject, mapObjectData.offsetX, mapObjectData.offsetY, false);
                // object properties
                int propSize = mapObjectData.props.size();
                mapObject.properties.clear();
                for (int p = 0; p < propSize; p++)
                {
                    PropertyData propertyData = mapObjectData.props.get(p);
                    propertyMenu.newProperty(propertyData, mapObject.properties);
                }
            }
        }
        return mapSprite;
    }
}
