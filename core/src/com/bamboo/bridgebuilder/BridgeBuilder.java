package com.bamboo.bridgebuilder;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.bamboo.bridgebuilder.commands.CreateLayer;
import com.bamboo.bridgebuilder.commands.MoveMapSpriteIndex;
import com.bamboo.bridgebuilder.commands.SelectLayer;
import com.bamboo.bridgebuilder.map.Layer;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.map.MapSprite;
import com.bamboo.bridgebuilder.ui.fileMenu.FileMenu;
import com.bamboo.bridgebuilder.ui.fileMenu.Tool;
import com.bamboo.bridgebuilder.ui.layerMenu.LayerTypes;

public class BridgeBuilder extends Game
{
	public static final int buttonHeight = 35;
	public static final int tabHeight = 25;
	public static final int toolHeight = 35;

	public Array<Map> maps; // All maps open in the program.
	public Map activeMap; // Map currently being viewed

	public Stage stage;
	public SpriteBatch batch;
	public ShapeRenderer shapeRenderer;
	public InputMultiplexer inputMultiplexer;
	public static Preferences prefs;

	public FileMenu fileMenu;

	public InputProcessor shortcutProcessor;

	public static boolean fileChooserOpen = false;

	@Override
	public void create ()
	{
		EditorAssets.get();

		this.inputMultiplexer = new InputMultiplexer();

		this.maps = new Array<>();

		this.batch = new SpriteBatch();
		this.shapeRenderer = new ShapeRenderer();
		this.stage = new Stage(new ScreenViewport());

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
						activeMap.shuffleRandomSpriteTool();
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
				else if(keycode == Input.Keys.I)
					fileMenu.toolPane.selectTool(fileMenu.toolPane.objectVerticeSelect);
				else if(keycode == Input.Keys.K)
					fileMenu.toolPane.selectTool(fileMenu.toolPane.blocked);
				else if(keycode == Input.Keys.L)
					fileMenu.toolPane.selectTool(fileMenu.toolPane.parallax);
				else if(keycode == Input.Keys.N)
					fileMenu.toolPane.selectTool(fileMenu.toolPane.lines);
				else if(keycode == Input.Keys.C)
					fileMenu.toolPane.selectTool(fileMenu.toolPane.perspective);
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
							Layer layer = activeMap.layers.get(layerIndex);
							SelectLayer selectLayer = new SelectLayer(activeMap, activeMap.selectedLayer, layer, true);
							activeMap.executeCommand(selectLayer);
							return true;
						}
						layerIndex --;
					}
					return true;
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

			stage.act();
			stage.draw();
		} catch(Exception e){
			e.printStackTrace();
			Gdx.app.exit();
			System.exit(0);
//			crashRecovery();
		}
	}

	@Override
	public void resize(int width, int height)
	{
		if(width == 0 || height == 0) // undo's the weird lwjgl3 change (minimize window calls this with 0, 0)
			return;
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
}
