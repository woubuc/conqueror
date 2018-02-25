package be.woubuc.conqueror.screens;

import be.woubuc.conqueror.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;

import static be.woubuc.conqueror.Globals.COLOUR_PANEL;
import static com.badlogic.gdx.Input.Keys.ANY_KEY;

public final class DefeatScreen implements Screen {
	
	private BitmapFont font;
	private BitmapFont smallFont;
	private Batch batch;
	
	@Override
	public void show() {
		System.out.println("Showing defeatScreen");
		
		Game game = Game.get();
		batch = game.batch;
		
		font = game.assets.get("font-large.fnt");
		smallFont = game.assets.get("font-small.fnt");
		
		game.gameScreen = null;
		game.choiceScreen = null;
	}
	
	@Override
	public void render(float delta) {
		if (Gdx.input.isKeyPressed(ANY_KEY)) {
			Gdx.app.exit();
			return;
		}
		
		Gdx.gl.glClearColor(COLOUR_PANEL.r, COLOUR_PANEL.g, COLOUR_PANEL.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		
		font.draw(batch, "You were defeated", 0, (Gdx.graphics.getHeight() / 2) + 20, Gdx.graphics.getWidth(), Align.center, false);
		smallFont.draw(batch, "Your kingdom mourns you.. Or at least, it would if it wasn't completely destroyed in these foolish wars.", 100, Gdx.graphics.getHeight() / 2, Gdx.graphics.getWidth() - 200, Align.center, false);
		
		font.draw(batch, "Press any key to exit", 0, 50, Gdx.graphics.getWidth(), Align.center, false);
		
		batch.end();
	}
	
	@Override
	public void hide() {
		System.out.println("Hiding defeatScreen");
		
		font = null;
		smallFont = null;
		batch = null;
	}
	
	@Override public void pause() { }
	@Override public void resume() { }
	@Override public void resize(int width, int height) { }
	@Override public void dispose() { }
}
