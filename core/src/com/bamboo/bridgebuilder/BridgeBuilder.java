package com.bamboo.bridgebuilder;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.ui.fileMenu.FileMenu;
import com.bamboo.bridgebuilder.ui.fileMenu.Tool;

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
	}

	@Override
	public void render ()
	{
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		try{
			fileMenu.toolPane.fps.setText(Gdx.graphics.getFramesPerSecond());
			handleShortcutKeys();

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

	private void handleShortcutKeys()
	{
		if(Gdx.input.isKeyJustPressed(Input.Keys.N) && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
		{
			this.fileMenu.newMap();
		}
		else if(Gdx.input.isKeyJustPressed(Input.Keys.Z) && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
		{
			Map map = (Map) getScreen();
			if(map != null)
				map.undo();
		}
		else if(Gdx.input.isKeyJustPressed(Input.Keys.R) && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
		{
			Map map = (Map) getScreen();
			if(map != null)
				map.redo();
		}
		else if(Gdx.input.isKeyJustPressed(Input.Keys.B))
			this.fileMenu.toolPane.selectTool(this.fileMenu.toolPane.brush);
		else if(Gdx.input.isKeyJustPressed(Input.Keys.E))
			this.fileMenu.toolPane.selectTool(this.fileMenu.toolPane.eraser);
		else if(Gdx.input.isKeyJustPressed(Input.Keys.G))
			this.fileMenu.toolPane.selectTool(this.fileMenu.toolPane.fill);
		else if(Gdx.input.isKeyJustPressed(Input.Keys.V))
			this.fileMenu.toolPane.selectTool(this.fileMenu.toolPane.select);
		else if(Gdx.input.isKeyJustPressed(Input.Keys.H))
			this.fileMenu.toolPane.selectTool(this.fileMenu.toolPane.grab);
		else if(Gdx.input.isKeyJustPressed(Input.Keys.R))
			this.fileMenu.toolPane.selectTool(this.fileMenu.toolPane.random);
		else if(Gdx.input.isKeyJustPressed(Input.Keys.X))
			this.fileMenu.toolPane.selectTool(this.fileMenu.toolPane.b2drender);
		else if(Gdx.input.isKeyJustPressed(Input.Keys.M))
			this.fileMenu.toolPane.selectTool(this.fileMenu.toolPane.boxSelect);
		else if(Gdx.input.isKeyJustPressed(Input.Keys.T))
			this.fileMenu.toolPane.selectTool(this.fileMenu.toolPane.top);
		else if(Gdx.input.isKeyJustPressed(Input.Keys.O))
			this.fileMenu.toolPane.selectTool(this.fileMenu.toolPane.drawObject);
		else if(Gdx.input.isKeyJustPressed(Input.Keys.P))
			this.fileMenu.toolPane.selectTool(this.fileMenu.toolPane.drawPoint);
		else if(Gdx.input.isKeyJustPressed(Input.Keys.I))
			this.fileMenu.toolPane.selectTool(this.fileMenu.toolPane.objectVerticeSelect);
		else if(Gdx.input.isKeyJustPressed(Input.Keys.S))
			this.fileMenu.toolPane.selectTool(this.fileMenu.toolPane.stamp);
		else if(Gdx.input.isKeyJustPressed(Input.Keys.D))
			this.fileMenu.toolPane.selectTool(this.fileMenu.toolPane.bind);
		else if(Gdx.input.isKeyJustPressed(Input.Keys.K))
			this.fileMenu.toolPane.selectTool(this.fileMenu.toolPane.blocked);
		else if(Gdx.input.isKeyJustPressed(Input.Keys.L))
			this.fileMenu.toolPane.selectTool(this.fileMenu.toolPane.parallax);
		else if(Gdx.input.isKeyJustPressed(Input.Keys.N))
			this.fileMenu.toolPane.selectTool(this.fileMenu.toolPane.lines);
		else if(Gdx.input.isKeyJustPressed(Input.Keys.C))
			this.fileMenu.toolPane.selectTool(this.fileMenu.toolPane.perspective);
	}
}
