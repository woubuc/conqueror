package be.woubuc.conqueror.screens;

import be.woubuc.conqueror.Choices;
import be.woubuc.conqueror.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;

import static be.woubuc.conqueror.Globals.COLOUR_PANEL;

public final class ChoiceScreen implements Screen {
	
	private Game game;
	private Stage stage;
	
	public Choices choices;
	
	@Override
	public void show() {
		System.out.println("Showing choiceScreen");
		
		game = Game.get();
		stage = game.stage;
		
		choices = new Choices(game);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(COLOUR_PANEL.r, COLOUR_PANEL.g, COLOUR_PANEL.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act();
		stage.draw();
	}
	
	@Override
	public void hide() {
		System.out.println("Hiding choiceScreen");
		
		stage.clear();
		
		game = null;
		stage = null;
		choices = null;
	}
	
	@Override public void pause() { }
	@Override public void resume() { }
	@Override public void resize(int width, int height) { }
	@Override public void dispose() { }
}
