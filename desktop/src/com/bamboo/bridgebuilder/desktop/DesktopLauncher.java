package com.bamboo.bridgebuilder.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.bamboo.bridgebuilder.BridgeBuilder;

public class DesktopLauncher
{
	public static void main (String[] arg)
	{
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("BridgeBuilder");
		config.setWindowedMode(1920, 1080);
		config.useVsync(true); // Setting to false disables vertical sync
		config.setIdleFPS(0); // Setting to 0 disables background fps throttling
		new Lwjgl3Application(new BridgeBuilder(), config);
	}
}
