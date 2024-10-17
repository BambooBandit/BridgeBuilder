package com.bamboo.bridgebuilder;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.bamboo.bridgebuilder.commands.Command;
import com.bamboo.bridgebuilder.commands.CreateLayer;
import com.bamboo.bridgebuilder.commands.MoveMapSpriteIndex;
import com.bamboo.bridgebuilder.commands.SelectLayer;
import com.bamboo.bridgebuilder.map.*;
import com.bamboo.bridgebuilder.ui.BBShapeRenderer;
import com.bamboo.bridgebuilder.ui.fileMenu.FileMenu;
import com.bamboo.bridgebuilder.ui.fileMenu.Tool;
import com.bamboo.bridgebuilder.ui.fileMenu.Tooltip;
import com.bamboo.bridgebuilder.ui.fileMenu.YesNoDialog;
import com.bamboo.bridgebuilder.ui.layerMenu.LayerTypes;

public class BridgeBuilder extends Game
{
	public static int buttonHeight = 35;
	public static int tabHeight = 25;
	public static int toolHeight = 35;

	public static BridgeBuilder bridgeBuilder;

	public Array<Map> maps; // All maps open in the program.
	public Map activeMap; // Map currently being viewed

	public Stage stage;
	public SpriteBatch batch;
	public BBShapeRenderer shapeRenderer;
	public InputMultiplexer inputMultiplexer;
	public static Preferences prefs;

	public static Tooltip mouseCoordTooltip;
	public static Tooltip selectedCountTooltip;
	public static Tooltip fenceDistanceTooltip;

	public FileMenu fileMenu;

	public InputProcessor shortcutProcessor;

	public static boolean fileChooserOpen = false;

	private boolean crashed = false;

	public Array<LayerChild> copiedItems = new Array<>();
	public static float copyColorR = -1;
	public static float copyColorG = -1;
	public static float copyColorB = -1;
	public static float copyColorA = -1;

	@Override
	public void create ()
	{
		EditorAssets.get();

		bridgeBuilder = this;
		this.inputMultiplexer = new InputMultiplexer();

		this.maps = new Array<>();

		this.batch = new SpriteBatch();
		this.shapeRenderer = new BBShapeRenderer();
		this.stage = new Stage(new ScreenViewport());

		mouseCoordTooltip = new Tooltip(this, "(0, 0) ", " (0, 0)", EditorAssets.getUISkin(), false, 0);
		fenceDistanceTooltip = new Tooltip(this, "(Fence Distance) last: 0. current: 0", "", EditorAssets.getUISkin(), false, 1);
		selectedCountTooltip = new Tooltip(this, "0 selected", "", EditorAssets.getUISkin(), false, 2);
		this.stage.addActor(mouseCoordTooltip);
		this.stage.addActor(fenceDistanceTooltip);
		this.stage.addActor(selectedCountTooltip);

		// fileMenu
		this.fileMenu = new FileMenu(EditorAssets.getUISkin(), this);
		this.fileMenu.setVisible(true);
		this.stage.addActor(this.fileMenu);

		this.inputMultiplexer.addProcessor(this.stage);

		Gdx.input.setInputProcessor(this.inputMultiplexer);

		prefs = Gdx.app.getPreferences("Editor preferences");

		this.shortcutProcessor = new InputProcessor()
		{
			@Override
			public boolean keyDown(int keycode)
			{
				if(keycode == Input.Keys.F3)
				{
					if(activeMap != null)
					{
						Layer layer = null;
						if(activeMap.selectedLayer != null && activeMap.secondarySelectedLayer == null)
							layer = activeMap.selectedLayer;
						else if(activeMap.secondarySelectedLayer != null)
							layer = activeMap.secondarySelectedLayer;
						if(layer != null)
						{
							layer.layerField.visibleImg.setVisible(!layer.layerField.visibleImg.isVisible());
							layer.layerField.notVisibleImg.setVisible(!layer.layerField.notVisibleImg.isVisible());
						}
					}
				}
				if(keycode == Input.Keys.N && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
					fileMenu.newMap();
				if(keycode == Input.Keys.S && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
				{
					if(activeMap != null)
						fileMenu.save(activeMap, false, false);
				}
				else if(keycode == Input.Keys.Z && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
				{
					if(activeMap != null)
						activeMap.undo();
				}
				else if(keycode == Input.Keys.R && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
				{
					if(activeMap != null)
						activeMap.redo();
				}
				else if(keycode == Input.Keys.S && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
				{
					if(activeMap != null)
					{
						CreateLayer createLayer = new CreateLayer(activeMap, LayerTypes.SPRITE);
						activeMap.executeCommand(createLayer);
					}
				}
				else if(keycode == Input.Keys.O && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
				{
					if(activeMap != null)
					{
						CreateLayer createLayer = new CreateLayer(activeMap, LayerTypes.OBJECT);
						activeMap.executeCommand(createLayer);
					}
				}
				else if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && keycode == Input.Keys.LEFT)
				{
					if(activeMap != null)
					{
						if(activeMap.selectedLayer != null)
						{
							for(int i = 0; i < activeMap.selectedLayer.children.size; i ++)
							{
								LayerChild child = (LayerChild) activeMap.selectedLayer.children.get(i);
								child.setPosition(child.x - 10, child.y);
							}
						}
						else
						{
							for(int i = 0; i < activeMap.layers.size; i ++)
							{
								Layer layer = activeMap.layers.get(i);
								for(int k = 0; k < layer.children.size; k ++)
								{
									LayerChild child = (LayerChild) layer.children.get(k);
									child.setPosition(child.x - 10, child.y);
								}
							}
						}
					}
				}
				else if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && keycode == Input.Keys.RIGHT)
				{
					if(activeMap != null)
					{
						if(activeMap.selectedLayer != null)
						{
							for(int i = 0; i < activeMap.selectedLayer.children.size; i ++)
							{
								LayerChild child = (LayerChild) activeMap.selectedLayer.children.get(i);
								child.setPosition(child.x + 10, child.y);
							}
						}
						else
						{
							for(int i = 0; i < activeMap.layers.size; i ++)
							{
								Layer layer = activeMap.layers.get(i);
								for(int k = 0; k < layer.children.size; k ++)
								{
									LayerChild child = (LayerChild) layer.children.get(k);
									child.setPosition(child.x + 10, child.y);
								}
							}
						}
					}
				}
				else if(keycode == Input.Keys.LEFT)
				{
					if(activeMap != null && activeMap.getSpriteToolFromSelectedTools() != null && activeMap.getSpriteToolFromSelectedTools().previousTool != null)
					{
						Vector3 coords = Utils.unproject(activeMap.camera, Gdx.input.getX(), Gdx.input.getY());
						float coordsX = coords.x + activeMap.cameraX;
						float coordsY = coords.y + activeMap.cameraY;
						activeMap.nextPreviousTool = activeMap.getSpriteToolFromSelectedTools().previousTool;
						activeMap.input.handlePreviewSpritePositionUpdate(coordsX, coordsY);
					}
				}
				else if(keycode == Input.Keys.RIGHT)
				{
					if(activeMap != null && activeMap.getSpriteToolFromSelectedTools() != null && activeMap.getSpriteToolFromSelectedTools().nextTool != null)
					{
						Vector3 coords = Utils.unproject(activeMap.camera, Gdx.input.getX(), Gdx.input.getY());
						float coordsX = coords.x + activeMap.cameraX;
						float coordsY = coords.y + activeMap.cameraY;
						activeMap.nextPreviousTool = activeMap.getSpriteToolFromSelectedTools().nextTool;
						activeMap.input.handlePreviewSpritePositionUpdate(coordsX, coordsY);
					}
				}
				else if(keycode == Input.Keys.UP && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
				{
					if(activeMap != null && activeMap.selectedSprites.size == 1)
					{
						MapSprite selectedSprite = activeMap.selectedSprites.first();
						MoveMapSpriteIndex moveMapSpriteIndex = new MoveMapSpriteIndex(activeMap, selectedSprite, true, true);
						activeMap.executeCommand(moveMapSpriteIndex);
					}
				}
				else if(keycode == Input.Keys.DOWN && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
				{
					if(activeMap != null && activeMap.selectedSprites.size == 1)
					{
						MapSprite selectedSprite = activeMap.selectedSprites.first();
						MoveMapSpriteIndex moveMapSpriteIndex = new MoveMapSpriteIndex(activeMap, selectedSprite, false, true);
						activeMap.executeCommand(moveMapSpriteIndex);
					}
				}
				else if(keycode == Input.Keys.UP && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
				{
					if(activeMap != null && activeMap.selectedSprites.size == 1)
					{
						MapSprite selectedSprite = activeMap.selectedSprites.first();
						MoveMapSpriteIndex moveMapSpriteIndex = new MoveMapSpriteIndex(activeMap, selectedSprite, true, false);
						activeMap.executeCommand(moveMapSpriteIndex);
					}
				}
				else if(keycode == Input.Keys.DOWN && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
				{
					if(activeMap != null && activeMap.selectedSprites.size == 1)
					{
						MapSprite selectedSprite = activeMap.selectedSprites.first();
						MoveMapSpriteIndex moveMapSpriteIndex = new MoveMapSpriteIndex(activeMap, selectedSprite, false, false);
						activeMap.executeCommand(moveMapSpriteIndex);
					}
				}
				else if(keycode == Input.Keys.B)
					fileMenu.toolPane.selectTool(fileMenu.toolPane.brush);
				else if(keycode == Input.Keys.V)
					fileMenu.toolPane.selectTool(fileMenu.toolPane.select);
				else if(keycode == Input.Keys.H)
					fileMenu.toolPane.selectTool(fileMenu.toolPane.grab);
				else if(keycode == Input.Keys.R)
				{
					if(activeMap != null)
						activeMap.shuffleRandomSpriteTool(false, -1);
				}
				else if(keycode == Input.Keys.X)
					fileMenu.toolPane.selectTool(fileMenu.toolPane.b2drender);
				else if(keycode == Input.Keys.M)
					fileMenu.toolPane.selectTool(fileMenu.toolPane.boxSelect);
				else if(keycode == Input.Keys.T)
					fileMenu.toolPane.selectTool(fileMenu.toolPane.top);
				else if(keycode == Input.Keys.O)
					fileMenu.toolPane.selectTool(fileMenu.toolPane.drawObject);
				else if(keycode == Input.Keys.P)
					fileMenu.toolPane.selectTool(fileMenu.toolPane.drawPoint);
				else if(keycode == Input.Keys.E)
					fileMenu.toolPane.selectTool(fileMenu.toolPane.objectVerticeSelect);
				else if(keycode == Input.Keys.L)
					fileMenu.toolPane.selectTool(fileMenu.toolPane.parallax);
				else if(keycode == Input.Keys.N)
					fileMenu.toolPane.selectTool(fileMenu.toolPane.lines);
				else if(keycode == Input.Keys.I)
					fileMenu.toolPane.selectTool(fileMenu.toolPane.eyedropper);
				else if(keycode == Input.Keys.DEL || keycode == Input.Keys.FORWARD_DEL)
				{
					if(activeMap == null)
						return true;
					activeMap.deleteSelected();
				}
				else if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
				{
					if(activeMap == null)
						return true;
					int layerIndex = activeMap.layers.size - 1;
					for(int i = Input.Keys.NUM_0; i <= Input.Keys.NUM_9; i ++)
					{
						if(Gdx.input.isKeyPressed(i))
						{
							if(layerIndex < 0)
								return true;
							if(Command.shouldExecute(activeMap, SelectLayer.class))
							{
								Layer layer = activeMap.layers.get(layerIndex);
								SelectLayer selectLayer = new SelectLayer(activeMap, activeMap.selectedLayer, layer, true);
								activeMap.executeCommand(selectLayer);
							}
							return true;
						}
						layerIndex --;
					}
					return true;
				}
				else if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && keycode == Input.Keys.UP)
				{
					if(activeMap != null)
					{
						if(activeMap.selectedLayer != null)
						{
							for(int i = 0; i < activeMap.selectedLayer.children.size; i ++)
							{
								LayerChild child = (LayerChild) activeMap.selectedLayer.children.get(i);
								child.setPosition(child.x, child.y + 10);
							}
						}
						else
						{
							for(int i = 0; i < activeMap.layers.size; i ++)
							{
								Layer layer = activeMap.layers.get(i);
								for(int k = 0; k < layer.children.size; k ++)
								{
									LayerChild child = (LayerChild) layer.children.get(k);
									child.setPosition(child.x, child.y + 10);
								}
							}
						}
					}
				}
				else if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && keycode == Input.Keys.DOWN)
				{
					if(activeMap != null)
					{
						if(activeMap.selectedLayer != null)
						{
							for(int i = 0; i < activeMap.selectedLayer.children.size; i ++)
							{
								LayerChild child = (LayerChild) activeMap.selectedLayer.children.get(i);
								child.setPosition(child.x, child.y - 10);
							}
						}
						else
						{
							for(int i = 0; i < activeMap.layers.size; i ++)
							{
								Layer layer = activeMap.layers.get(i);
								for(int k = 0; k < layer.children.size; k ++)
								{
									LayerChild child = (LayerChild) layer.children.get(k);
									child.setPosition(child.x, child.y - 10);
								}
							}
						}
					}
				}

				return false;
			}

			@Override
			public boolean keyUp(int keycode)
			{
				return false;
			}

			@Override
			public boolean keyTyped(char character)
			{
				return false;
			}

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button)
			{
				return false;
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button)
			{
				return false;
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer)
			{
				return false;
			}

			@Override
			public boolean mouseMoved(int screenX, int screenY)
			{
				return false;
			}

			@Override
			public boolean scrolled(int amount)
			{
				return false;
			}
		};

		this.inputMultiplexer.addProcessor(this.shortcutProcessor);
	}

	@Override
	public void render ()
	{
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		try{
			fileMenu.toolPane.fps.setText(Gdx.graphics.getFramesPerSecond());

			if(activeMap == null)
			{
				// The map clears the screen, but no map is active so manually clear the screen here
				Gdx.gl.glClearColor(0, 0, 0, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			}
			else // Render the active map
				super.render();

			boolean cut = false;
			boolean paste = false;
			if(activeMap != null)
			{
				if(activeMap.selectedLayer != null && (activeMap.selectedSprites.size != 0 || activeMap.selectedObjects.size != 0))
					cut = true;
				if(copiedItems.size != 0 && activeMap.selectedLayer != null)
                {
                    if((copiedItems.first() instanceof MapSprite && activeMap.selectedLayer instanceof SpriteLayer) || (copiedItems.first() instanceof MapObject && activeMap.selectedLayer instanceof ObjectLayer))
                    paste = true;
                }
			}
			fileMenu.cutButton.setChecked(!cut);
			fileMenu.pasteButton.setChecked(!paste);


			stage.act();
			stage.draw();
		} catch(Exception e){
			crashRecovery(e);
		}
	}

	@Override
	public void resize(int width, int height)
	{
		if(width == 0 || height == 0) // undo's the weird lwjgl3 change (minimize window calls this with 0, 0)
			return;
		int multiplier = height / 720;
		buttonHeight = 35 * multiplier;
		tabHeight = 25 * multiplier;
		toolHeight = 35 * multiplier;

		this.stage.getViewport().update(width, height, true);
		this.fileMenu.setSize(Gdx.graphics.getWidth(), buttonHeight, tabHeight, toolHeight);
		this.fileMenu.setPosition(0, Gdx.graphics.getHeight() - this.fileMenu.getHeight());

		if(this.getScreen() != null)
			this.getScreen().resize(width, height);
	}
	
	@Override
	public void dispose ()
	{
	}

	/** Stores the map in the maps array to allow for switching between map tabs.
	 * Creates the tab for the map.*/
	public void addToMaps(Map map)
	{
		this.fileMenu.mapTabPane.addMap(map);
	}

	public Tool getFileTool()
	{
		return this.fileMenu.toolPane.getTool();
	}

    public void crashRecovery(Exception e)
    {
    	if(this.crashed)
		{
			this.stage.act();
			this.stage.draw();
			return;
		}

    	this.crashed = true;

        e.printStackTrace();

        if(this.maps.size == 0)
        {
            Gdx.app.exit();
            System.exit(0);
        }

		for(int i = 0; i < this.maps.size; i ++)
        {
            final int finalI = i;
            new YesNoDialog("Editor encountered error. Save before closing " + this.maps.get(finalI).name + "?", this.maps.get(finalI).editor.stage, "", EditorAssets.getUISkin(), true)
			{
				@Override
				public void yes()
				{
					boolean closeApplicationAfterSaving = (maps.size == 1);
					fileMenu.saveAs(maps.get(finalI), true, closeApplicationAfterSaving);
				}

				@Override
				public void no()
				{
					fileMenu.mapTabPane.removeMap(maps.get(finalI));
					if (maps.size == 0)
					{
						Gdx.app.exit();
						System.exit(0);
					}
				}
            };
        }

		this.stage.act();
		this.stage.draw();
    }

	public void copyColor(float r, float g, float b, float a)
	{
		copyColorR = r;
		copyColorG = g;
		copyColorB = b;
		copyColorA = a;
	}
}
