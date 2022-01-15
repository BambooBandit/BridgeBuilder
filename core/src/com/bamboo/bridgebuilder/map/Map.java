package com.bamboo.bridgebuilder.map;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.LongArray;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.bamboo.bridgebuilder.BridgeBuilder;
import com.bamboo.bridgebuilder.EditorAssets;
import com.bamboo.bridgebuilder.Utils;
import com.bamboo.bridgebuilder.commands.*;
import com.bamboo.bridgebuilder.data.*;
import com.bamboo.bridgebuilder.ui.BBShapeRenderer;
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
import java.util.HashSet;
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

    // camera
    public float zoom = 1;
    public float perspectiveZoom = 0;
    public float cameraX;
    public float cameraY;

    public MapInput input;

    public Skin skin;

    public File file = null;

    public MapSprite lastFencePlaced;
    public MapPoint lastBranchPlaced;
    public float lastFencePlacedDistance;

    public ObjectLayer groupPolygons; // Each group of map sprites has a polygon associated with it in this layer. Used for making multiple sprites do something when entering a polygon
    public Array<MapObject> mergedPolygonPreview = null;

    // For undo/redo
    private int undoRedoPointer = -1;
    private Stack<Command> commandStack = new Stack<>();
    private int stackThreshold = 75;

    public long idCounter = 1;

    public PolygonMerger polygonMerger;

    private Array<FloatArray> mergedPolygons;

    public SpriteTool nextPreviousTool = null;

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
        for(int i = 0; i < layers.size; i ++)
        {
            Layer layer = layers.get(i);
            if(Utils.getPropertyField(layer.properties, "playableFloor") != null)
            {
                cameraX = layer.width / 2f;
                cameraY = layer.height / 2f;
                break;
            }
        }
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
        camera.position.set(0, 0, 0);
        this.camera.zoom = this.zoom;
        this.cameraX = 2.5f;
        this.cameraY = 2.5f;
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

//        if(Gdx.input.isKeyJustPressed(Input.Keys.Q))
//        {
//            mergedPolygons = mergePolygons();
//        }

        this.world.step(delta, 1, 1);

        this.camera.zoom = this.zoom;
        this.camera.update();

        if(groupPolygons != null)
            groupPolygons.update();
        for(int i = 0; i < layers.size; i ++)
            layers.get(i).update();

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
        drawLayerOutline();
        drawGrid();
        drawAttachedObjects();
        drawObjectLayers();
        drawSnapPreview();
        drawLayerOverridePreview();
        drawSnap();
        drawC();
        drawMergePolygonPreview();
        drawFailedMergedPolygons();
        if(mergedPolygons != null)
        {
            for (int i = 0; i < mergedPolygons.size; i++)
            {
                editor.shapeRenderer.setColor(Color.RED);
                FloatArray polygon = mergedPolygons.get(i);
                if(polygon.size == 0)
                    continue;
                editor.shapeRenderer.polygon(polygon.toArray());
            }
        }

        if(this.editor.fileMenu.toolPane.b2drender.selected)
        {
            this.editor.shapeRenderer.end();
            this.b2dr.render(this.world, this.camera.combined);
            this.editor.shapeRenderer.begin();
        }

        if(selectedLayer != null)
            this.editor.shapeRenderer.setProjectionMatrix(selectedLayer.perspective.camera.combined);
        if(selectedLayer != null)
            drawHoveredOutline();
        if(selectedLayer != null)
            this.editor.shapeRenderer.setProjectionMatrix(selectedLayer.perspective.camera.combined);
        drawSelectedOutlines();
        if(selectedLayer != null)
            this.editor.shapeRenderer.setProjectionMatrix(selectedLayer.perspective.camera.combined);
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

    private void drawMergePolygonPreview()
    {
        if(mergedPolygonPreview == null)
            return;
        editor.shapeRenderer.setColor(Color.RED);
        editor.shapeRenderer.set(BBShapeRenderer.ShapeType.Filled);

        for(int i = 0; i < mergedPolygonPreview.size; i ++)
        {
            MapPolygon mapPolygon = (MapPolygon) mergedPolygonPreview.get(i);
            mapPolygon.polygon.setPosition(mapPolygon.x - cameraX, mapPolygon.y - cameraY);
            float[] vertices = mapPolygon.polygon.getTransformedVertices();
            editor.shapeRenderer.polygon(vertices, 0, vertices.length);
//            float[] vertices = mapPolygon.polygon.getTransformedVertices();
//            FloatArray triangles = Utils.triangleFan(vertices);
//            for(int k = 0; k < triangles.size; k += 6)
//            {
//                editor.shapeRenderer.triangle(triangles.get(k), triangles.get(k + 1), triangles.get(k + 2), triangles.get(k + 3), triangles.get(k + 4), triangles.get(k + 5));
//            }
        }
        editor.shapeRenderer.set(BBShapeRenderer.ShapeType.Line);
    }

    private void drawFailedMergedPolygons()
    {
        if(polygonMerger != null && polygonMerger.failedPolygon2.size > 0 && polygonMerger.failedPolygon1.size > 0)
        {
            editor.shapeRenderer.setColor(Color.RED);
            float[] toArray1 = polygonMerger.failedPolygon1.toArray();
            float[] toArray2 = polygonMerger.failedPolygon2.toArray();
            for(int i = 0; i < toArray1.length; i += 2)
            {
                toArray1[i] -= cameraX;
                toArray1[i + 1] -= cameraY;
            }
            for(int i = 0; i < toArray2.length; i += 2)
            {
                toArray2[i] -= cameraX;
                toArray2[i + 1] -= cameraY;
            }
            editor.shapeRenderer.polygon(toArray1);
            editor.shapeRenderer.polygon(toArray2);
        }
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
        editor.shapeRenderer.line(fromX - cameraX, fromY - cameraY, toX - cameraX, toY - cameraY);
    }

    private void drawLayerOverridePreview()
    {
        if(input.overrideLayer == null)
            return;

        editor.shapeRenderer.setColor(Color.GOLD);
        editor.shapeRenderer.circle(input.currentPos.x - cameraX, input.currentPos.y - cameraY, 2);
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
                objects:
                for(int k = 0; k < objectLayer.children.size; k ++)
                {
                    if(!objectLayer.layerField.visibleImg.isVisible())
                        continue objects;
                    MapObject from = objectLayer.children.get(k);
                    drawSnapFlicker(from);
                    if(from instanceof MapPoint)
                        drawSnapBranch((MapPoint) from);
                }
            }
        }
    }

    private void drawC()
    {
        for(int i = 0; i < layers.size; i ++)
        {
            Layer layer = layers.get(i);
            if(layer instanceof SpriteLayer)
            {
                SpriteLayer spriteLayer = (SpriteLayer) layer;
                for(int k = 0; k < spriteLayer.children.size; k ++)
                {
                    MapSprite mapSprite = spriteLayer.children.get(k);
                    if(mapSprite.c1 != null)
                    {
                        editor.shapeRenderer.setColor(Color.OLIVE);
                        editor.shapeRenderer.line(
                                mapSprite.c1.x - cameraX,
                                mapSprite.c1.y - cameraY,
                                mapSprite.c2.x - cameraX,
                                mapSprite.c2.y - cameraY);
                        editor.shapeRenderer.circle(mapSprite.c2.x - cameraX,
                                mapSprite.c2.y - cameraY, .2f, 5);
                    }
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
            editor.shapeRenderer.line(from.x - cameraX + (from.width / 2f),
                    from.y - cameraY + (from.height / 2f),
                    to.x - cameraX + (to.width / 2f),
                    to.y - cameraY + (to.height / 2f));
            editor.shapeRenderer.circle(to.x - cameraX + (to.width / 2f),
                    to.y - cameraY + (to.height / 2f), .2f, 5);
        }
    }

    private void drawSnapBranch(MapPoint mapPoint)
    {
        MapPoint from = mapPoint;
        if((from.toBranchPoints == null))
            return;

        editor.shapeRenderer.set(BBShapeRenderer.ShapeType.Filled);
        for(int i = 0; i < from.toBranchPoints.size; i ++)
        {
            editor.shapeRenderer.setColor(Color.ROYAL);
            MapPoint to = from.toBranchPoints.get(i);
            editor.shapeRenderer.rectLine(from.x - cameraX,
                    from.y - cameraY,
                    to.x - cameraX,
                    to.y - cameraY,
                    camera.zoom * .1f);

            editor.shapeRenderer.setColor(Color.NAVY);
            double dy = to.y - from.y;
            double dx = to.x - from.x;
            double theta = Math.atan2(dy, dx);
            float barb = (.6f + (.6f * camera.zoom)) / 2f;
            double phi = Math.toRadians(20);
            double x, y, rho = theta + phi;
            for(int j = 0; j < 2; j++)
            {
                x = to.x - barb * Math.cos(rho);
                y = to.y - barb * Math.sin(rho);

                editor.shapeRenderer.rectLine(to.x - cameraX,
                        to.y - cameraY,
                        (float)x - cameraX,
                        (float)y - cameraY,
                        camera.zoom * .1f);
                rho = theta - phi;
            }
        }
        editor.shapeRenderer.set(BBShapeRenderer.ShapeType.Line);
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
                editor.shapeRenderer.setProjectionMatrix(layer.perspective.perspectiveCamera.combined);
                SpriteLayer spriteLayer = (SpriteLayer) layer;
                for(int k = 0; k < spriteLayer.children.size; k ++)
                {
                    MapSprite mapSprite = spriteLayer.children.get(k);
                    if(mapSprite.attachedMapObjects != null)
                    {
                        for(int s = 0; s < mapSprite.attachedMapObjects.size; s ++)
                        {
                            mapSprite.attachedMapObjects.get(s).draw();
                            mapSprite.attachedMapObjects.get(s).drawOutline();
                        }
                    }
                    if(mapSprite.attachedSprites != null)
                    {
                        for(int s = 0; s < mapSprite.attachedSprites.children.size; s ++)
                        {
                            MapSprite attachedMapSprite = mapSprite.attachedSprites.children.get(s);
                            if(attachedMapSprite.attachedMapObjects != null)
                            {
                                for(int m = 0; m < attachedMapSprite.attachedMapObjects.size; m ++)
                                {
                                    attachedMapSprite.attachedMapObjects.get(m).draw();
                                    attachedMapSprite.attachedMapObjects.get(m).drawOutline();
                                }
                            }
                        }
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
            if(Utils.isLayerGround(selectedSprite.layer))
                this.editor.shapeRenderer.setProjectionMatrix(selectedSprite.layer.perspective.perspectiveCamera.combined);
            else
                this.editor.shapeRenderer.setProjectionMatrix(selectedSprite.layer.perspective.camera.combined);
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
        this.editor.shapeRenderer.setProjectionMatrix(selectedLayer.perspective.perspectiveCamera.combined);
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
            this.editor.shapeRenderer.circle(this.input.mapPolygonVertices.get(0) + cameraX + this.input.objectVerticePosition.x, this.input.mapPolygonVertices.get(1) + cameraY + this.input.objectVerticePosition.y, .1f, 7);
            for (int i = 2; i < this.input.mapPolygonVertices.size; i += 2)
            {
                this.editor.shapeRenderer.line(this.input.mapPolygonVertices.get(oldIndex) - cameraX + this.input.objectVerticePosition.x, this.input.mapPolygonVertices.get(oldIndex + 1) - cameraY + this.input.objectVerticePosition.y, this.input.mapPolygonVertices.get(i) - cameraX + this.input.objectVerticePosition.x, this.input.mapPolygonVertices.get(i + 1) - cameraY + this.input.objectVerticePosition.y);
                oldIndex += 2;
            }
            if(Utils.isFileToolThisType(editor, Tools.DRAWRECTANGLE))
                this.editor.shapeRenderer.line(this.input.mapPolygonVertices.get(oldIndex) + cameraX + this.input.objectVerticePosition.x, this.input.mapPolygonVertices.get(oldIndex + 1) + cameraY + this.input.objectVerticePosition.y, this.input.mapPolygonVertices.get(0) + cameraX + this.input.objectVerticePosition.x, this.input.mapPolygonVertices.get(1) + cameraY + this.input.objectVerticePosition.y);
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
            this.editor.shapeRenderer.circle(this.input.stairVertices.get(0) + this.input.stairVerticePosition.x - cameraX, this.input.stairVertices.get(1) + this.input.stairVerticePosition.y - cameraY, .1f, 7);
            for (int i = 2; i < this.input.stairVertices.size; i += 2)
            {
                this.editor.shapeRenderer.setColor(Color.WHITE);
                this.editor.shapeRenderer.line(this.input.stairVertices.get(oldIndex) + this.input.stairVerticePosition.x - cameraX, this.input.stairVertices.get(oldIndex + 1) + this.input.stairVerticePosition.y + initialheight - cameraY, this.input.stairVertices.get(i) + this.input.stairVerticePosition.x - cameraX, this.input.stairVertices.get(i + 1) + this.input.stairVerticePosition.y + initialheight - cameraY);
                this.editor.shapeRenderer.setColor(Color.GRAY);
                this.editor.shapeRenderer.line(this.input.stairVertices.get(oldIndex) + this.input.stairVerticePosition.x - cameraX, this.input.stairVertices.get(oldIndex + 1) + this.input.stairVerticePosition.y - cameraY, this.input.stairVertices.get(i) + this.input.stairVerticePosition.x - cameraX, this.input.stairVertices.get(i + 1) + this.input.stairVerticePosition.y - cameraY);
                if(i >= 6)
                {
                    this.editor.shapeRenderer.line(this.input.stairVertices.get(6) + this.input.stairVerticePosition.x - cameraX, this.input.stairVertices.get(7) + this.input.stairVerticePosition.y - cameraY, this.input.stairVertices.get(0) + this.input.stairVerticePosition.x - cameraX, this.input.stairVertices.get(1) + this.input.stairVerticePosition.y - cameraY);
                }
                oldIndex += 2;
            }
        }

        this.editor.shapeRenderer.setColor(Color.WHITE);
        oldIndex = 0;
        if (this.input.stairVertices.size >= 2)
        {
            this.editor.shapeRenderer.circle(this.input.stairVertices.get(0) + this.input.stairVerticePosition.x - cameraX, this.input.stairVertices.get(1) + this.input.stairVerticePosition.y - cameraY, .1f, 7);
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
                this.editor.shapeRenderer.line(this.input.stairVertices.get(oldIndex) + this.input.stairVerticePosition.x - cameraX, this.input.stairVertices.get(oldIndex + 1) + this.input.stairVerticePosition.y + fromHeight - cameraY, this.input.stairVertices.get(i) + this.input.stairVerticePosition.x - cameraX, this.input.stairVertices.get(i + 1) + this.input.stairVerticePosition.y + toHeight - cameraY);
                this.editor.shapeRenderer.line(this.input.stairVertices.get(0) + this.input.stairVerticePosition.x - cameraX, this.input.stairVertices.get(1) + this.input.stairVerticePosition.y - cameraY, this.input.stairVertices.get(0) + this.input.stairVerticePosition.x - cameraX, this.input.stairVertices.get(1) + this.input.stairVerticePosition.y + initialheight - cameraY);
                if(i >= 2)
                    this.editor.shapeRenderer.line(this.input.stairVertices.get(2) + this.input.stairVerticePosition.x - cameraX, this.input.stairVertices.get(3) + this.input.stairVerticePosition.y - cameraY, this.input.stairVertices.get(2) + this.input.stairVerticePosition.x - cameraX, this.input.stairVertices.get(3) + this.input.stairVerticePosition.y + finalheight - cameraY);
                if(i >= 4)
                    this.editor.shapeRenderer.line(this.input.stairVertices.get(4) + this.input.stairVerticePosition.x - cameraX, this.input.stairVertices.get(5) + this.input.stairVerticePosition.y - cameraY, this.input.stairVertices.get(4) + this.input.stairVerticePosition.x - cameraX, this.input.stairVertices.get(5) + this.input.stairVerticePosition.y + finalheight - cameraY);
                if(i >= 6)
                {
                    this.editor.shapeRenderer.line(this.input.stairVertices.get(6) + this.input.stairVerticePosition.x - cameraX, this.input.stairVertices.get(7) + this.input.stairVerticePosition.y + toHeight - cameraY, this.input.stairVertices.get(0) + this.input.stairVerticePosition.x - cameraX, this.input.stairVertices.get(1) + this.input.stairVerticePosition.y + toHeight - cameraY);
                    this.editor.shapeRenderer.line(this.input.stairVertices.get(6) + this.input.stairVerticePosition.x - cameraX, this.input.stairVertices.get(7) + this.input.stairVerticePosition.y - cameraY, this.input.stairVertices.get(6) + this.input.stairVerticePosition.x - cameraX, this.input.stairVertices.get(7) + this.input.stairVerticePosition.y + initialheight - cameraY);
                }
                oldIndex += 2;
            }
            if(Utils.isFileToolThisType(editor, Tools.DRAWRECTANGLE))
                this.editor.shapeRenderer.line(this.input.stairVertices.get(oldIndex) + this.input.stairVerticePosition.x - cameraX, this.input.stairVertices.get(oldIndex + 1) + this.input.stairVerticePosition.y - cameraY, this.input.stairVertices.get(0) + this.input.stairVerticePosition.x - cameraX, this.input.stairVertices.get(1) + this.input.stairVerticePosition.y - cameraY);
        }
    }

    private void drawGradientLine()
    {
        if(!this.editor.fileMenu.toolPane.gradient.selected || !this.input.draggingGradient)
            return;
        this.editor.shapeRenderer.setColor(Color.BLACK);
        this.editor.shapeRenderer.line(this.input.gradientX - cameraX, this.input.gradientY - cameraY, this.input.currentPos.x - cameraX, this.input.currentPos.y - cameraY);

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
        this.editor.shapeRenderer.rect(this.input.boxSelect.rectangle.x - cameraX, this.input.boxSelect.rectangle.y - cameraY, this.input.boxSelect.rectangle.width, this.input.boxSelect.rectangle.height);
    }

    private void drawHoveredOutline()
    {
        for(int i = 0; i < layers.size; i ++)
        {
            Layer layer = layers.get(i);
            if(Utils.isLayerGround(layer))
                this.editor.shapeRenderer.setProjectionMatrix(layer.perspective.perspectiveCamera.combined);
            else
                this.editor.shapeRenderer.setProjectionMatrix(layer.perspective.camera.combined);
            for (int k = 0; k < layer.layerField.layerName.getListeners().size; k++)
            {
                EventListener layerListener = layer.layerField.layerName.getListeners().get(k);
                if (layerListener instanceof ClickListener)
                {
                    ClickListener clickListener = (ClickListener) layerListener;
                    if (clickListener.isOver())
                    {
                        for(int s = 0; s < layer.children.size; s ++)
                        {

                            LayerChild layerChild = (LayerChild) layer.children.get(s);
                            layerChild.drawHoverOutline();
                        }
                        return;
                    }
                }
            }
        }
        if(!Utils.isFileToolThisType(editor, Tools.SELECT))
            return;

        if(this.hoveredChild == null)
            return;
        if(Utils.isLayerGround(selectedLayer))
            this.editor.shapeRenderer.setProjectionMatrix(selectedLayer.perspective.perspectiveCamera.combined);
        else
            this.editor.shapeRenderer.setProjectionMatrix(selectedLayer.perspective.camera.combined);
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
        if(layer == null || layer.layerField.visibleImg.isVisible())
        {
            if(layer != null && layer.perspective != null)
            {
                Perspective perspective = layer.perspective;
                this.rayHandler.setCombinedMatrix(perspective.perspectiveCamera.combined, perspective.perspectiveCamera.position.x, perspective.perspectiveCamera.position.y, perspective.perspectiveCamera.viewportWidth * perspective.perspectiveCamera.zoom * 2f, perspective.perspectiveCamera.viewportHeight * perspective.perspectiveCamera.zoom * 2f);
            }
            else
            {
                this.rayHandler.setCombinedMatrix(this.camera.combined, this.camera.position.x, this.camera.position.y, this.camera.viewportWidth * this.camera.zoom * 2f, this.camera.viewportHeight * this.camera.zoom * 2f);
            }
            this.rayHandler.updateAndRender();
        }
    }

    private void drawObjectLayers()
    {
        if(!layerMenu.toolPane.objectVisibility.isSelected)
            return;
        for(int i = 0; i < this.layers.size; i ++)
        {
            Layer layer = this.layers.get(i);
            if(layer instanceof ObjectLayer)
            {
                editor.shapeRenderer.setProjectionMatrix(layer.perspective.perspectiveCamera.combined);
                if (layer.layerField.visibleImg.isVisible() && layer.overrideSprite == null)
                    layer.draw();
            }
        }
        if(this.groupPolygons != null && editor.fileMenu.toolPane.groupPolygons.selected)
        {
            editor.shapeRenderer.setProjectionMatrix(groupPolygons.perspective.perspectiveCamera.combined);
            this.groupPolygons.draw();
        }
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
            this.editor.shapeRenderer.set(BBShapeRenderer.ShapeType.Line);
            this.editor.shapeRenderer.setColor(Color.BLACK);
            int layerWidth = this.selectedLayer.width;
            int layerHeight = this.selectedLayer.height;
            if (this.editor.fileMenu.toolPane.lines.selected)
            {
                for (int y = 1; y < layerHeight; y++)
                    this.editor.shapeRenderer.line(this.selectedLayer.x - cameraX, this.selectedLayer.y - cameraY + y, this.selectedLayer.x - cameraX + layerWidth, this.selectedLayer.y - cameraY + y);
                for (int x = 1; x < layerWidth; x++)
                    this.editor.shapeRenderer.line(this.selectedLayer.x + x - cameraX, this.selectedLayer.y - cameraY, this.selectedLayer.x - cameraX + x, this.selectedLayer.y - cameraY + layerHeight);
            }
        }
    }

    private void drawLayerOutline()
    {
        if(this.selectedLayer != null)
        {
            this.editor.shapeRenderer.set(BBShapeRenderer.ShapeType.Line);
            this.editor.shapeRenderer.setColor(Color.BLACK);
            int layerWidth = this.selectedLayer.width;
            int layerHeight = this.selectedLayer.height;
            this.editor.shapeRenderer.line(this.selectedLayer.x - cameraX, this.selectedLayer.y - cameraY, this.selectedLayer.x - cameraX, this.selectedLayer.y - cameraY + layerHeight);
            this.editor.shapeRenderer.line(this.selectedLayer.x - cameraX, this.selectedLayer.y - cameraY, this.selectedLayer.x - cameraX + layerWidth, this.selectedLayer.y - cameraY);
            this.editor.shapeRenderer.line(this.selectedLayer.x - cameraX, this.selectedLayer.y - cameraY + layerHeight, this.selectedLayer.x - cameraX + layerWidth, this.selectedLayer.y - cameraY + layerHeight);
            this.editor.shapeRenderer.line(this.selectedLayer.x - cameraX + layerWidth, this.selectedLayer.y - cameraY, this.selectedLayer.x - cameraX + layerWidth, this.selectedLayer.y - cameraY + layerHeight);
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
        if(this.nextPreviousTool != null)
        {
            boolean contains = false;
            for(int i = 0; i < this.spriteMenu.selectedSpriteTools.size; i ++)
            {
                SpriteTool spriteTool = this.spriteMenu.selectedSpriteTools.get(i);
                if(spriteTool.nextTool == this.nextPreviousTool || spriteTool.previousTool == this.nextPreviousTool)
                {
                    contains = true;
                    break;
                }
            }
            if(!contains)
                this.nextPreviousTool = null;
        }
        if(this.nextPreviousTool != null)
            return this.nextPreviousTool;
        if(this.editor.fileMenu.toolPane.random.selected && this.randomSpriteIndex < this.spriteMenu.selectedSpriteTools.size)
            return this.spriteMenu.selectedSpriteTools.get(this.randomSpriteIndex);
        return this.spriteMenu.selectedSpriteTools.first();
    }

    public void shuffleRandomSpriteTool(boolean ignoreFencePost, float stack)
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
            for (int i = 0; i < getSpriteToolFromSelectedTools().previewSprites.size; i++)
            {
                float randomRotation = this.editor.fileMenu.toolPane.minMaxDialog.randomRotationValue;
                float randomR = this.editor.fileMenu.toolPane.minMaxDialog.randomRValue;
                float randomG = this.editor.fileMenu.toolPane.minMaxDialog.randomGValue;
                float randomB = this.editor.fileMenu.toolPane.minMaxDialog.randomBValue;
                float randomA = this.editor.fileMenu.toolPane.minMaxDialog.randomAValue;
                TextureAtlas.AtlasSprite previewSprite = (TextureAtlas.AtlasSprite) spriteTool.previewSprites.get(i);
                previewSprite.setRotation(randomRotation);
                previewSprite.setColor(randomR, randomG, randomB, randomA);
                input.handlePreviewSpritePositionUpdate(coords.x + cameraX, coords.y + cameraY);
            }
        }

        if(!ignoreFencePost && editor.fileMenu.toolPane.fence.selected){
            SpriteTool spriteTool = getSpriteToolFromSelectedTools();
            if(!Utils.canBuildFenceFromSelectedSpriteTools(this))
                return;
            if (!spriteTool.hasAttachedMapObjects()) {
                shuffleRandomSpriteTool(false, stack);
                return;
            }
            boolean hasFencePost = false;
            for (int i = 0; i < spriteTool.attachedMapObjectManagers.size; i++) {
                AttachedMapObjectManager attachedMapObjectManager = spriteTool.attachedMapObjectManagers.get(i);
                if (Utils.getPropertyField(attachedMapObjectManager.properties, "fenceStart") != null)
                    hasFencePost = true;
            }
            if (!hasFencePost) {
                shuffleRandomSpriteTool(false, stack);
                return;
            }
        }

        if(ignoreFencePost && stack != -1 && editor.fileMenu.toolPane.fence.selected)
        {
            FieldFieldPropertyValuePropertyField stackProperty;
            Array<SpriteTool> spriteTools = getAllSelectedSpriteTools();

            boolean contains = false;
            for (int i = 0; i < spriteTools.size; i++)
            {
                SpriteTool tool = spriteTools.get(i);
                stackProperty = (FieldFieldPropertyValuePropertyField) Utils.getPropertyField(tool.properties, "stack");
                if(stackProperty == null || Integer.parseInt(stackProperty.value.getText()) == stack)
                    contains = true;
            }
            if(!contains)
                return;

            SpriteTool spriteTool = getSpriteToolFromSelectedTools();
            stackProperty = (FieldFieldPropertyValuePropertyField) Utils.getPropertyField(spriteTool.properties, "stack");
            if(stackProperty == null || Integer.parseInt(stackProperty.value.getText()) == stack)
                return;

            for (int i = 0; i < spriteTools.size; i++)
            {
                SpriteTool tool = spriteTools.get(i);
                stackProperty = (FieldFieldPropertyValuePropertyField) Utils.getPropertyField(tool.properties, "stack");
                if(stackProperty != null && Integer.parseInt(stackProperty.value.getText()) == stack)
                    shuffleRandomSpriteTool(ignoreFencePost, stack);
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
        if(!setDefaultsOnly)
        {
            this.name = mapData.name;

            // map properties
            if(mapData.props != null)
            {
                int propSize = mapData.props.size();
                for (int s = 0; s < propSize; s++)
                {
                    PropertyData propertyData = mapData.props.get(s);
                    propertyMenu.newProperty(propertyData, propertyMenu.mapPropertyPanel.properties);
                }
            }
            // map locked properties
            int propSize = mapData.lProps.size();
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

                    if(toolData.nT != null)
                        spriteTool.nextTool = spriteMenu.getSpriteTool(toolData.nT, sheetName);
                    if(toolData.pT != null)
                        spriteTool.previousTool = spriteMenu.getSpriteTool(toolData.pT, sheetName);

                    // properties
                    int propSize = 0;
                    if(toolData.p != null)
                        propSize = toolData.p.size();
                    for (int s = 0; s < propSize; s++)
                    {
                        PropertyData propertyData = toolData.p.get(s);
                        propertyMenu.newProperty(propertyData, spriteTool.properties);
                    }
                    // locked properties
                    propSize = 0;
                    if(toolData.lP != null)
                        propSize = toolData.lP.size();
                    for (int s = 0; s < propSize; s++)
                    {
                        PropertyData propertyData = toolData.lP.get(s);
                        propertyMenu.changeLockedPropertyValue(propertyData, spriteTool.lockedProperties);
                    }

                    // attached map objects
                    if (toolData.o != null)
                    {
                        int objSize = toolData.o.size();
                        for (int s = 0; s < objSize; s++)
                        {
                            MapObjectData mapObjectData = toolData.o.get(s);
                            MapObject mapObject;
                            if (mapObjectData instanceof MapPolygonData)
                            {
                                MapPolygonData mapPolygonData = (MapPolygonData) mapObjectData;
                                MapPolygon mapPolygon = new MapPolygon(this, mapPolygonData.v, mapPolygonData.x, mapPolygonData.y);
                                mapObject = mapPolygon;
                            } else
                            {
                                MapPointData mapPointData = (MapPointData) mapObjectData;
                                MapPoint mapPoint = new MapPoint(this, mapPointData.x, mapPointData.y);
                                mapObject = mapPoint;
                            }
                            mapObject.flickerId = mapObjectData.fId;
                            mapObject.setID(mapObject.id);
                            // attached manager
                            spriteTool.createAttachedMapObject(this, mapObject, mapObjectData.oX, mapObjectData.oY);
                            if (setDefaultsOnly)
                                mapObject.attachedMapObjectManager.addCopyOfMapObjectToAllMapSpritesOfThisSpriteTool(mapObject);
                            // object properties
                            propSize = 0;
                            if(mapObjectData.p != null)
                                propSize = mapObjectData.p.size();
                            mapObject.properties.clear();
                            for (int p = 0; p < propSize; p++)
                            {
                                PropertyData propertyData = mapObjectData.p.get(p);
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
                int propSize = 0;
                if(layerData.props != null)
                    propSize = layerData.props.size();
                for (int p = 0; p < propSize; p++)
                {
                    PropertyData propertyData = layerData.props.get(p);
                    propertyMenu.newProperty(propertyData, layer.properties);
                }

                // create layer children
                if (layerData instanceof SpriteLayerData)
                {
                    SpriteLayerData spriteLayerData = (SpriteLayerData) layerData;
                    int childSize = 0;
                    if(spriteLayerData.children != null)
                        childSize = spriteLayerData.children.size();
                    parent:
                    for (int k = 0; k < childSize; k++)
                    {
                        LayerChildData mapSpriteData = spriteLayerData.children.get(k);
                        if(mapSpriteData instanceof AttachedMapSpriteData)
                        {
                            AttachedMapSpriteData attachedMapSpriteData = (AttachedMapSpriteData) mapSpriteData;
                            MapSprite parentMapSprite = null;
                            for(int s = 0; s < attachedMapSpriteData.s.size(); s++)
                            {
                                MapSpriteData attachedData = attachedMapSpriteData.s.get(s);
                                if(attachedData.pa)
                                {
                                    parentMapSprite = loadMapSpriteData(attachedData, layer);
                                    if(parentMapSprite == null)
                                        continue parent;
                                    ((SpriteLayer) layer).addMapSprite(parentMapSprite, -1);
                                    break;
                                }
                            }
                            parentMapSprite.attachedSprites = new SpriteLayer(editor, this, null);
                            parentMapSprite.attachedSprites.perspective = ((SpriteLayer)parentMapSprite.layer).perspective;
                            child:
                            for(int s = 0; s < attachedMapSpriteData.s.size(); s++)
                            {
                                MapSpriteData attachedData = attachedMapSpriteData.s.get(s);
                                if (attachedData.pa)
                                {
                                    parentMapSprite.attachedSprites.addMapSprite(parentMapSprite, -1);
                                    continue;
                                }
                                MapSprite childMapSprite = loadMapSpriteData(attachedData, parentMapSprite.attachedSprites);
                                if(childMapSprite == null)
                                    continue child;
                                parentMapSprite.attachedSprites.addMapSprite(childMapSprite, -1);
                                childMapSprite.parentSprite = parentMapSprite;
                            }
                        }
                        else
                        {
                            MapSprite mapSprite = loadMapSpriteData((MapSpriteData) mapSpriteData, layer);
                            if(mapSprite != null)
                                ((SpriteLayer) layer).addMapSprite(mapSprite, -1);
                        }
                    }
                } else if (layerData instanceof ObjectLayerData)
                {
                    ObjectLayerData objectLayerData = (ObjectLayerData) layerData;
                    int childSize = 0;
                    if(objectLayerData.children != null)
                        childSize = objectLayerData.children.size();
                    for (int k = 0; k < childSize; k++)
                    {
                        MapObjectData mapObjectData = objectLayerData.children.get(k);
                        MapObject mapObject;
                        if (mapObjectData instanceof MapPolygonData)
                        {
                            MapPolygonData mapPolygonData = (MapPolygonData) mapObjectData;
                            MapPolygon mapPolygon = new MapPolygon(this, layer, mapPolygonData.v, mapPolygonData.x, mapPolygonData.y);
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
                        if(mapObject instanceof MapPoint)
                        {
                            MapPointData mapPointData = (MapPointData) mapObjectData;
                            MapPoint mapPoint = (MapPoint) mapObject;
                            if(mapPointData.bId != null)
                            {
                                mapPoint.toBranchIds = new LongArray();
                                for(int s = 0; s < mapPointData.bId.size(); s ++)
                                    mapPoint.toBranchIds.add(mapPointData.bId.get(s));
                            }
                        }
                        mapObject.setID(mapObjectData.i);
                        // object properties
                        propSize = 0;
                        if(mapObjectData.p != null)
                            propSize = mapObjectData.p.size();
                        for (int s = 0; s < propSize; s++)
                        {
                            PropertyData propertyData = mapObjectData.p.get(s);
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
                                    long id = edge.id;
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
                                    long id = flicker.id;
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
                                    long id = flicker.id;
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

                    // Branch
                    branch:
                    for (int s = 0; s < layer.children.size; s++)
                    {
                        MapObject mapObject = (MapObject) layer.children.get(s);
                        if(!(mapObject instanceof MapPoint))
                            continue branch;
                        MapPoint mapPoint = (MapPoint) mapObject;
                        if (mapPoint.toBranchIds == null || mapPoint.toBranchIds.size == 0)
                            continue branch;
                        for(int m = 0; m < layers.size; m++)
                        {
                            Layer toLayer = layers.get(m);
                            if(toLayer instanceof ObjectLayer)
                            {
                                toMapPoint:
                                for (int k = 0; k < toLayer.children.size; k++)
                                {
                                    MapObject toMapObject = (MapObject) toLayer.children.get(k);
                                    if(!(toMapObject instanceof MapPoint))
                                        continue toMapPoint;
                                    MapPoint branch = (MapPoint) toMapObject;
                                    long id = branch.id;
                                    if (mapPoint.toBranchIds.contains(id))
                                    {
                                        SnapMapPointBranch snapMapSpriteFlicker = new SnapMapPointBranch(mapPoint, branch);
                                        executeCommand(snapMapSpriteFlicker);
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
                    if(mapSprite.layerOverrideIndexBack > 0)
                    {
                        mapSprite.layerOverrideBack = layers.get(mapSprite.layerOverrideIndexBack - 1);
                        mapSprite.layerOverrideBack.overrideSpriteBack = mapSprite;
                    }
                }
            }



            // groups
            if(mapData.groups != null)
            {
                groupPolygons = new ObjectLayer(editor, this, null);
                for (int i = 0; i < mapData.groups.size(); i++)
                {
                    GroupMapPolygonData mapPolygonData = mapData.groups.get(i);
                    MapPolygon mapPolygon = new MapPolygon(this, groupPolygons, mapPolygonData.v, mapPolygonData.x, mapPolygonData.y);
                    (groupPolygons).addMapObject(mapPolygon);
                    mapPolygon.mapSprites = new Array<>();
                    // object properties
                    if(mapPolygonData.p != null)
                    {
                        int propSize = mapPolygonData.p.size();
                        for (int s = 0; s < propSize; s++)
                        {
                            PropertyData propertyData = mapPolygonData.p.get(s);
                            propertyMenu.newProperty(propertyData, mapPolygon.properties);
                        }
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
                groupPolygons.children.sort();
            }



        }
        PropertyToolPane.apply(this);
        propertyMenu.mapPropertyPanel.apply();
        setIdCounter(mapData.idCounter);

        fixDuplicateIDs();
    }

    public void loadMap(MapData mapData, String defaultSheet, String currentSheet)
    {
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
                int propSize = toolData.p.size();
                for (int s = 0; s < propSize; s++)
                {
                    PropertyData propertyData = toolData.p.get(s);
                    propertyMenu.newProperty(propertyData, spriteTool.properties);
                }
                // locked properties
                propSize = toolData.lP.size();
                for (int s = 0; s < propSize; s++)
                {
                    PropertyData propertyData = toolData.lP.get(s);
                    propertyMenu.changeLockedPropertyValue(propertyData, spriteTool.lockedProperties);
                }

                // attached map objects
                if (toolData.o != null)
                {
                    int objSize = toolData.o.size();
                    for (int s = 0; s < objSize; s++)
                    {
                        MapObjectData mapObjectData = toolData.o.get(s);
                        MapObject mapObject;
                        if (mapObjectData instanceof MapPolygonData)
                        {
                            MapPolygonData mapPolygonData = (MapPolygonData) mapObjectData;
                            MapPolygon mapPolygon = new MapPolygon(this, mapPolygonData.v, mapPolygonData.x, mapPolygonData.y);
                            mapObject = mapPolygon;
                        } else
                        {
                            MapPointData mapPointData = (MapPointData) mapObjectData;
                            MapPoint mapPoint = new MapPoint(this, mapPointData.x, mapPointData.y);
                            mapObject = mapPoint;
                        }
                        mapObject.flickerId = mapObjectData.fId;
                        mapObject.setID(mapObjectData.i);
                        // attached manager
                        spriteTool.createAttachedMapObject(this, mapObject, mapObjectData.oX, mapObjectData.oY);
                        mapObject.attachedMapObjectManager.addCopyOfMapObjectToAllMapSpritesOfThisSpriteTool(mapObject);
                        // object properties
                        propSize = mapObjectData.p.size();
                        mapObject.properties.clear();
                        for (int p = 0; p < propSize; p++)
                        {
                            PropertyData propertyData = mapObjectData.p.get(p);
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
        mapSprite.edgeId = mapSpriteData.e;
        mapSprite.flickerId = mapSpriteData.fId;
        mapSprite.setID(mapSpriteData.i);
        LabelFieldPropertyValuePropertyField fenceProperty = Utils.getLockedPropertyField(mapSprite.lockedProperties, "Fence");
        if(mapSpriteData.f)
            fenceProperty.value.setText("true");
        else
            fenceProperty.value.setText("false");

        LabelFieldPropertyValuePropertyField ignoreProperty = Utils.getLockedPropertyField(mapSprite.lockedProperties, "IgnoreProps");
        if(mapSpriteData.iP)
            ignoreProperty.value.setText("true");
        else
            ignoreProperty.value.setText("false");

        mapSprite.setZ(mapSpriteData.z);
        mapSprite.setScale(mapSpriteData.s + MapSpriteData.defaultScaleValue);
        mapSprite.setColor(mapSpriteData.r + MapSpriteData.defaultColorValue, mapSpriteData.g + MapSpriteData.defaultColorValue, mapSpriteData.b + MapSpriteData.defaultColorValue, mapSpriteData.a + MapSpriteData.defaultColorValue);
        mapSprite.setPosition(mapSpriteData.x, mapSpriteData.y);
        Utils.setCenterOrigin(mapSprite.getX(), mapSprite.getY());
        // attached map objects
        // instance map objects
        if (mapSpriteData.o != null)
        {
            int objSize = mapSpriteData.o.size();
            for (int s = 0; s < objSize; s++)
            {
                MapObjectData mapObjectData = mapSpriteData.o.get(s);
                MapObject mapObject;
                if (mapObjectData instanceof MapPolygonData)
                {
                    MapPolygonData mapPolygonData = (MapPolygonData) mapObjectData;
                    MapPolygon mapPolygon = new MapPolygon(this, mapPolygonData.v, mapPolygonData.x, mapPolygonData.y);
                    mapObject = mapPolygon;
                } else
                {
                    MapPointData mapPointData = (MapPointData) mapObjectData;
                    MapPoint mapPoint = new MapPoint(this, mapPointData.x, mapPointData.y);
                    mapObject = mapPoint;
                }
                mapObject.flickerId = mapObjectData.fId;
                mapObject.setID(mapObjectData.i);
                // attached manager
                mapSprite.createAttachedMapObject(this, mapObject, mapObjectData.oX, mapObjectData.oY, false);
                // object properties
                mapObject.properties.clear();
                if(mapObjectData.p != null)
                {
                    int propSize = mapObjectData.p.size();
                    for (int p = 0; p < propSize; p++)
                    {
                        PropertyData propertyData = mapObjectData.p.get(p);
                        propertyMenu.newProperty(propertyData, mapObject.properties);
                    }

                }
            }
        }
        // tool attached map object ids
        if(mapSpriteData.to != null)
        {
            for(int i = 0; i < mapSpriteData.to.size(); i ++)
            {
                long ID = mapSpriteData.to.get(i);
                mapSprite.tool.attachedMapObjectManagers.get(i).getMapObjectByParent(mapSprite).setID(ID);
            }
        }

        mapSprite.setRotation(mapSpriteData.ro);
        mapSprite.layerOverrideIndex = mapSpriteData.loi;
        mapSprite.layerOverrideIndexBack = mapSpriteData.loiB;
        mapSprite.x1Offset = mapSpriteData.x1;
        mapSprite.y1Offset = mapSpriteData.y1;
        mapSprite.x2Offset = mapSpriteData.x2;
        mapSprite.y2Offset = mapSpriteData.y2;
        mapSprite.x3Offset = mapSpriteData.x3;
        mapSprite.y3Offset = mapSpriteData.y3;
        mapSprite.x4Offset = mapSpriteData.x4;
        mapSprite.y4Offset = mapSpriteData.y4;
        float[] spriteVertices = mapSprite.sprite.getVertices();
        mapSprite.offsetMovebox1.setPosition(spriteVertices[SpriteBatch.X2] + mapSprite.map.cameraX + mapSprite.x1Offset - (mapSprite.offsetMovebox1.scale * mapSprite.offsetMovebox1.width / 2f), spriteVertices[SpriteBatch.Y2] + mapSprite.map.cameraY + mapSprite.y1Offset - (mapSprite.offsetMovebox1.scale * mapSprite.offsetMovebox1.height / 2f));
        mapSprite.offsetMovebox2.setPosition(spriteVertices[SpriteBatch.X3] + mapSprite.map.cameraX + mapSprite.x2Offset - (mapSprite.offsetMovebox2.scale * mapSprite.offsetMovebox2.width / 2f), spriteVertices[SpriteBatch.Y3] + mapSprite.map.cameraY + mapSprite.y2Offset - (mapSprite.offsetMovebox2.scale * mapSprite.offsetMovebox2.height / 2f));
        mapSprite.offsetMovebox3.setPosition(spriteVertices[SpriteBatch.X4] + mapSprite.map.cameraX + mapSprite.x3Offset - (mapSprite.offsetMovebox3.scale * mapSprite.offsetMovebox3.width / 2f), spriteVertices[SpriteBatch.Y4] + mapSprite.map.cameraY + mapSprite.y3Offset - (mapSprite.offsetMovebox3.scale * mapSprite.offsetMovebox3.height / 2f));
        mapSprite.offsetMovebox4.setPosition(spriteVertices[SpriteBatch.X1] + mapSprite.map.cameraX + mapSprite.x4Offset - (mapSprite.offsetMovebox4.scale * mapSprite.offsetMovebox4.width / 2f), spriteVertices[SpriteBatch.Y1] + mapSprite.map.cameraY + mapSprite.y4Offset - (mapSprite.offsetMovebox4.scale * mapSprite.offsetMovebox4.height / 2f));
        mapSprite.polygon.setOffset(mapSprite.x1Offset, mapSprite.x2Offset, mapSprite.x3Offset, mapSprite.x4Offset, mapSprite.y1Offset, mapSprite.y2Offset, mapSprite.y3Offset, mapSprite.y4Offset);

        if(mapSpriteData.p != null)
        {
            for (int i = 0; i < mapSpriteData.p.size(); i++)
                propertyMenu.newProperty(mapSpriteData.p.get(i), mapSprite.instanceSpecificProperties);
        }

        return mapSprite;
    }

    public Array<MapPolygon> mergePolygons(Array<MapObject> objects)
    {
        if(objects == null || objects.size == 0)
            return null;
        if(polygonMerger == null)
            polygonMerger = new PolygonMerger(this);
        return polygonMerger.convertToMapPolygons(polygonMerger.merge(objects));
    }

    /** Sorts sprite layers based on c1's and c2's. */
    public void sort()
    {
        if(selectedLayer == null)
        {
            for(int i = 0; i < layers.size; i ++)
            {
                Layer layer = layers.get(i);
                if(layer instanceof SpriteLayer)
                {
                    SpriteLayer spriteLayer = (SpriteLayer) layer;
                    spriteLayer.sort();
                }
            }
            return;
        }

        if(selectedLayer != null && selectedLayer instanceof SpriteLayer)
        {
            SpriteLayer spriteLayer = (SpriteLayer) selectedLayer;
            spriteLayer.sort();
            return;
        }
    }

    public long getAndIncrementId()
    {
        return idCounter ++;
    }

    public void setIdCounter(long id)
    {
        idCounter = id;
    }

    public void fixDuplicateIDs()
    {
        // Fix the idCounter
        long largestID = 0;
        for (int i = 0; i < layers.size; i++)
        {
            Layer layer = layers.get(i);
            if (layer instanceof SpriteLayer)
            {
                SpriteLayer spriteLayer = (SpriteLayer) layer;
                for (int s = 0; s < spriteLayer.children.size; s++)
                {
                    MapSprite mapSprite = spriteLayer.children.get(s);
                    if (mapSprite.id > largestID)
                        largestID = mapSprite.id;
                    if(mapSprite.attachedMapObjects != null)
                    {
                        for(int k = 0; k < mapSprite.attachedMapObjects.size; k ++)
                        {
                            MapObject mapObject = mapSprite.attachedMapObjects.get(k);
                            if(mapObject.id > largestID)
                                largestID = mapObject.id;
                        }
                    }
                    if(mapSprite.attachedSprites != null)
                    {
                        for(int k = 0; k < mapSprite.attachedSprites.children.size; k ++)
                        {
                            MapSprite attachedSprite = mapSprite.attachedSprites.children.get(k);
                            if(attachedSprite.id > largestID)
                                largestID = attachedSprite.id;
                        }
                    }
                }
            } else if (layer instanceof ObjectLayer)
            {
                ObjectLayer objectLayer = (ObjectLayer) layer;
                for (int s = 0; s < objectLayer.children.size; s++)
                {
                    LayerChild mapObject = objectLayer.children.get(s);
                    if (mapObject.id > largestID)
                        largestID = mapObject.id;
                }
            }
        }
        if(this.groupPolygons != null)
        {
            for (int i = 0; i < this.groupPolygons.children.size; i++)
            {
                MapObject mapObject = this.groupPolygons.children.get(i);
                if (mapObject.id > largestID)
                    largestID = mapObject.id;
            }
        }
        this.idCounter = largestID + 1;



        // Find collisions and use the idCounter to reset them
        HashSet<Long> hashSet = new HashSet<>();

        for (int i = 0; i < layers.size; i++)
        {
            Layer layer = layers.get(i);
            if (layer instanceof SpriteLayer)
            {
                SpriteLayer spriteLayer = (SpriteLayer) layer;
                for (int s = 0; s < spriteLayer.children.size; s++)
                {
                    MapSprite mapSprite = spriteLayer.children.get(s);
                    if(!hashSet.add(mapSprite.id))
                        mapSprite.setID(getAndIncrementId());
                    if(mapSprite.attachedMapObjects != null)
                    {
                        for(int k = 0; k < mapSprite.attachedMapObjects.size; k ++)
                        {
                            MapObject mapObject = mapSprite.attachedMapObjects.get(k);
                            if(!hashSet.add(mapObject.id))
                                mapObject.setID(getAndIncrementId());
                        }
                    }
                    if(mapSprite.attachedSprites != null)
                    {
                        for(int k = 0; k < mapSprite.attachedSprites.children.size; k ++)
                        {
                            MapSprite attachedSprite = mapSprite.attachedSprites.children.get(k);
                            if(attachedSprite == mapSprite)
                                continue;
                            if(!hashSet.add(attachedSprite.id))
                                attachedSprite.setID(getAndIncrementId());
                            if(attachedSprite.attachedMapObjects != null)
                            {
                                for(int t = 0; t < attachedSprite.attachedMapObjects.size; t ++)
                                {
                                    MapObject mapObject = attachedSprite.attachedMapObjects.get(t);
                                    if(!hashSet.add(mapObject.id))
                                        mapObject.setID(getAndIncrementId());
                                }
                            }
                        }
                    }
                }
            } else if (layer instanceof ObjectLayer)
            {
                ObjectLayer objectLayer = (ObjectLayer) layer;
                for (int s = 0; s < objectLayer.children.size; s++)
                {
                    LayerChild mapObject = objectLayer.children.get(s);
                    if(!hashSet.add(mapObject.id))
                        mapObject.setID(getAndIncrementId());
                }
            }
        }
        if(this.groupPolygons != null)
        {
            for (int i = 0; i < this.groupPolygons.children.size; i++)
            {
                MapObject mapObject = this.groupPolygons.children.get(i);
                if (!hashSet.add(mapObject.id))
                    mapObject.setID(getAndIncrementId());
            }
        }
    }
}
