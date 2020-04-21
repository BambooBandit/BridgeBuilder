package com.bamboo.bridgebuilder;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.bamboo.bridgebuilder.map.Map;
import com.bamboo.bridgebuilder.ui.fileMenu.FileMenu;

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
}
