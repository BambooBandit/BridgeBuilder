package com.bamboo.bridgebuilder.map;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import com.bamboo.bridgebuilder.commands.DeleteMapSprites;
import com.bamboo.bridgebuilder.ui.fileMenu.Tools;
import com.bamboo.bridgebuilder.ui.layerMenu.LayerMenu;
import com.bamboo.bridgebuilder.ui.propertyMenu.PropertyMenu;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteMenu;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteMenuTools;
import com.bamboo.bridgebuilder.ui.spriteMenu.SpriteTool;

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

    // For undo/redo
    private int undoRedoPointer = -1;
    private Stack<Command> commandStack = new Stack<>();
    private int stackThreshold = 75;

    public Map(BridgeBuilder editor, String name)
    {
        this.editor = editor;
        this.name = name;
        init();
    }

    private void init()
    {
        // Game screen size
        virtualHeight = 20f;
        virtualWidth = virtualHeight * Gdx.graphics.getWidth() / Gdx.graphics.getHeight();

        b2dr = new Box2DDebugRenderer();

        this.input = new MapInput(editor, this);

        this.layers = new Array<>();
        this.selectedSprites = new Array<>();
        this.selectedObjects = new Array<>();

        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, virtualHeight * Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight(), virtualHeight);
        this.camera.zoom = this.zoom;
        this.camera.position.x = 2.5f;
        this.camera.position.y = 2.5f;
        camera.update();


        this.stage = new Stage(new ScreenViewport(), editor.batch);
        this.skin = EditorAssets.getUISkin();
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
        this.editor.inputMultiplexer.addProcessor(this.editor.shortcutProcessor);
        Gdx.input.setInputProcessor(this.editor.inputMultiplexer);
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(r, g, b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.world.step(delta, 1, 1);

        this.camera.zoom = this.zoom;
        this.camera.update();

        this.editor.batch.setProjectionMatrix(camera.combined);
        this.editor.batch.begin();
        //spritebatch begin
        drawSpriteLayers();
        //spritebatch end
        this.editor.batch.end();

        this.editor.shapeRenderer.setProjectionMatrix(camera.combined);
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
        drawHoveredOutline();
        drawSelectedOutlines();
        drawUnfinishedMapPolygon();
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
            if(layer instanceof SpriteLayer)
            {
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
    }

    private void drawSelectedOutlines()
    {
        if(!Utils.isFileToolThisType(editor, Tools.SELECT) && !Utils.isFileToolThisType(editor, Tools.BOXSELECT) && !Utils.isFileToolThisType(editor, Tools.OBJECTVERTICESELECT) && !Utils.isFileToolThisType(editor, Tools.DRAWPOINT) && !Utils.isFileToolThisType(editor, Tools.DRAWOBJECT) && !Utils.isFileToolThisType(editor, Tools.DRAWRECTANGLE))
            return;
        if(Utils.isFileToolThisType(editor, Tools.DRAWPOINT) || Utils.isFileToolThisType(editor, Tools.DRAWOBJECT) || Utils.isFileToolThisType(editor, Tools.DRAWRECTANGLE))
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
        if(!Utils.isFileToolThisType(editor, Tools.DRAWOBJECT) && !Utils.isFileToolThisType(editor, Tools.DRAWRECTANGLE))
            return;
        this.editor.shapeRenderer.setColor(Color.GRAY);
        int oldIndex = 0;
        if(Utils.isFileToolThisType(editor, Tools.DRAWRECTANGLE) && input.mapPolygonVertices.size == 8)
        {
            input.mapPolygonVertices.removeIndex(2);
            input.mapPolygonVertices.insert(2, input.currentPos.x - this.input.objectVerticePosition.x);
            input.mapPolygonVertices.removeIndex(3);
            input.mapPolygonVertices.insert(3, input.mapPolygonVertices.get(1));
            input.mapPolygonVertices.removeIndex(4);
            input.mapPolygonVertices.insert(4, input.currentPos.x - this.input.objectVerticePosition.x);
            input.mapPolygonVertices.removeIndex(5);
            input.mapPolygonVertices.insert(5, input.currentPos.y - this.input.objectVerticePosition.y);
            input.mapPolygonVertices.removeIndex(6);
            input.mapPolygonVertices.insert(6, input.mapPolygonVertices.get(0));
            input.mapPolygonVertices.removeIndex(7);
            input.mapPolygonVertices.insert(7, input.currentPos.y - this.input.objectVerticePosition.y);
        }
        if (input.mapPolygonVertices.size >= 2)
        {
            this.editor.shapeRenderer.circle(input.mapPolygonVertices.get(0) + input.objectVerticePosition.x, input.mapPolygonVertices.get(1) + input.objectVerticePosition.y, .1f, 7);
            for (int i = 2; i < input.mapPolygonVertices.size; i += 2)
            {
                this.editor.shapeRenderer.line(input.mapPolygonVertices.get(oldIndex) + input.objectVerticePosition.x, input.mapPolygonVertices.get(oldIndex + 1) + input.objectVerticePosition.y, input.mapPolygonVertices.get(i) + input.objectVerticePosition.x, input.mapPolygonVertices.get(i + 1) + input.objectVerticePosition.y);
                oldIndex += 2;
            }
            if(Utils.isFileToolThisType(editor, Tools.DRAWRECTANGLE))
                this.editor.shapeRenderer.line(input.mapPolygonVertices.get(oldIndex) + input.objectVerticePosition.x, input.mapPolygonVertices.get(oldIndex + 1) + input.objectVerticePosition.y, input.mapPolygonVertices.get(0) + input.objectVerticePosition.x, input.mapPolygonVertices.get(1) + input.objectVerticePosition.y);
        }
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
        if(!Utils.isFileToolThisType(editor, Tools.BOXSELECT) || !this.input.boxSelect.isDragging || this.selectedLayer == null)
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
        editor.shapeRenderer.rect(input.boxSelect.rectangle.x, input.boxSelect.rectangle.y, input.boxSelect.rectangle.width, input.boxSelect.rectangle.height);
    }

    private void drawHoveredOutline()
    {
        if(!Utils.isFileToolThisType(editor, Tools.SELECT))
            return;

        if(this.hoveredChild == null)
            return;
        this.hoveredChild.drawHoverOutline();
    }

    private void drawSpriteLayers()
    {
        for(int i = 0; i < this.layers.size; i ++)
        {
            if(this.layers.get(i) instanceof SpriteLayer)
            {
                if (this.layers.get(i).layerField.visibleImg.isVisible() && this.layers.get(i).overrideSprite == null)
                    this.layers.get(i).draw();
            }
        }
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

    private void drawGrid()
    {
        if(selectedLayer != null)
        {
            this.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
            int layerWidth = selectedLayer.width;
            int layerHeight = selectedLayer.height;
            if (editor.fileMenu.toolPane.lines.selected)
            {
                for (int y = 1; y < layerHeight; y++)
                    this.editor.shapeRenderer.line(selectedLayer.x, selectedLayer.y + y, selectedLayer.x + layerWidth, selectedLayer.y + y);
                for (int x = 1; x < layerWidth; x++)
                    this.editor.shapeRenderer.line(selectedLayer.x + x, selectedLayer.y, selectedLayer.x + x, selectedLayer.y + layerHeight);
            }
        }
    }

    private void drawLayerOutline()
    {
        if(selectedLayer != null)
        {
            this.editor.shapeRenderer.set(ShapeRenderer.ShapeType.Line);
            int layerWidth = selectedLayer.width;
            int layerHeight = selectedLayer.height;
            this.editor.shapeRenderer.line(selectedLayer.x, selectedLayer.y, selectedLayer.x, selectedLayer.y + layerHeight);
            this.editor.shapeRenderer.line(selectedLayer.x, selectedLayer.y, selectedLayer.x + layerWidth, selectedLayer.y);
            this.editor.shapeRenderer.line(selectedLayer.x, selectedLayer.y + layerHeight, selectedLayer.x + layerWidth, selectedLayer.y + layerHeight);
            this.editor.shapeRenderer.line(selectedLayer.x + layerWidth, selectedLayer.y, selectedLayer.x + layerWidth, selectedLayer.y + layerHeight);
        }
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
        virtualWidth = virtualHeight * stage.getWidth() / stage.getHeight();
        camera.viewportWidth = virtualHeight * width / (float) height;
        camera.viewportHeight = virtualHeight;
        camera.update();

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
            totalSum += Float.parseFloat(Utils.getLockedPropertyField(getAllSelectedSpriteTools().get(i).lockedProperties, "Probability").value.getText());
        float random = Utils.randomFloat(0, totalSum);
        for(int i = 0; i < getAllSelectedSpriteTools().size; i ++)
        {
            partialSum += Float.parseFloat(Utils.getLockedPropertyField(getAllSelectedSpriteTools().get(i).lockedProperties, "Probability").value.getText());
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

    public void executeCommand(Command command)
    {
        command.execute();
        Utils.println();
        Utils.println("execute " + command);
        pushCommand(command);
    }

    public void pushCommand(Command command)
    {
        deleteElementsAfterPointer(undoRedoPointer);
        commandStack.push(command);
        undoRedoPointer ++;
        Utils.println("push " + command);
        Utils.println();
        if(commandStack.size() > stackThreshold)
        {
            undoRedoPointer --;
            commandStack.remove(0);
        }
    }

    private void deleteElementsAfterPointer(int undoRedoPointer)
    {
        if(commandStack.size() < 1)
            return;
        for(int i = commandStack.size() - 1; i > undoRedoPointer; i --)
            commandStack.remove(i);
    }

    public void undo()
    {
        if(undoRedoPointer < 0)
            return;
        Command command = commandStack.get(undoRedoPointer);
        Utils.println("undo " + command);
        command.undo();
        undoRedoPointer --;
    }

    public void redo()
    {
        if(undoRedoPointer == commandStack.size() - 1)
            return;
        undoRedoPointer ++;
        Command command = commandStack.get(undoRedoPointer);
        Utils.println("redo " + command);
        command.execute();
    }

    public void deleteSelected()
    {
        if(this.selectedLayer != null)
        {
            if(this.selectedObjects.size > 0)
            {
                DeleteMapObjects deleteMapObjects = new DeleteMapObjects(this.selectedObjects, selectedLayer);
                executeCommand(deleteMapObjects);
            }
            else if(this.selectedSprites.size > 0)
            {
                DeleteMapSprites deleteMapSprites = new DeleteMapSprites(this.selectedSprites, (SpriteLayer) selectedLayer);
                executeCommand(deleteMapSprites);
            }
        }
    }
}
