package be.woubuc.conqueror.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import be.woubuc.conqueror.Game;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.width = 620;
		config.height = 420;
		config.resizable = false;
		config.samples = 0;
		
		new LwjglApplication(new Game(), config);
	}
}
