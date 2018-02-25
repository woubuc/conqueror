package be.woubuc.conqueror.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import be.woubuc.conqueror.Game;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.width = 776;
		config.height = 576;
		config.resizable = false;
		config.samples = 0;
		config.title = "Conqueror";
		
		new LwjglApplication(new Game(), config);
	}
}
