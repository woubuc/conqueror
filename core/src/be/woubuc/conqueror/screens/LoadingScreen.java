package be.woubuc.conqueror.screens;

import be.woubuc.conqueror.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;

/**
 * Shows the loading progress while the game assets are loading
 */
public final class LoadingScreen implements Screen {
	
	private Game game;
	private BitmapFont font;
	private Batch batch;
	
	@Override
	public void show() {
		System.out.println("Showing loadingScreen");
		
		game = Game.get();
		
		font = game.assets.get("font-large.fnt");
		batch = game.batch;
	}
	
	@Override
	public void render(float delta) {
		game.assets.update();
		
		if (game.assets.getProgress() >= 1) {
			game.setScreen(game.gameScreen);
			return;
		}
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		
		font.draw(batch, "Loading", 0, (Gdx.graphics.getHeight() / 2) + 20, Gdx.graphics.getWidth(), Align.center, false);
		font.draw(batch, Math.round(game.assets.getProgress() * 100) + "%", 0, Gdx.graphics.getHeight() / 2, Gdx.graphics.getWidth(), Align.center, false);
		
		batch.end();
	}
	
	@Override
	public void hide() {
		System.out.println("Hiding loadingScreen");
		
		font = null;
		batch = null;
		game = null;
	}
	
	@Override public void pause() { }
	@Override public void resume() { }
	@Override public void resize(int width, int height) { }
	@Override public void dispose() { }
}
