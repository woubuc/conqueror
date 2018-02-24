package be.woubuc.conqueror;

import be.woubuc.conqueror.map.Tile;
import be.woubuc.conqueror.map.TileMap;
import be.woubuc.conqueror.util.ColourUtils;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static be.woubuc.conqueror.Globals.*;

public class Game extends ApplicationAdapter {
	
	public static final Random random = new Random();
	public static final TileMap map = new TileMap(MAP_SIZE);
	
	public static final int MAP_DRAW_SIZE = MAP_SIZE * TILE_SIZE;
	public static final int MAX_SCORE = MAP_SIZE * MAP_SIZE;
	
	public static AssetManager assets;
	
	static Drawable getDrawable(String id) {
		return new TextureRegionDrawable(new TextureRegion(assets.get(id, Texture.class)));
	}
	
	private final List<Faction> factions = new ArrayList<>();;
	Faction player;
	
	private Clock clock;
	private Choices choices;
	private int lastChoice = 1;
	
	private Batch batch;
	private Stage stage;
	
	private BitmapFont largeFont;
	private BitmapFont smallFont;
	
	private Button optionOne;
	private Button optionTwo;
	private Button optionThree;
	
	boolean isTurn = false;
	
	@Override
	public void create () {
		assets = new AssetManager();
		assets.load("font-large.fnt", BitmapFont.class);
		assets.load("font-small.fnt", BitmapFont.class);
		assets.finishLoading();
		
		largeFont = assets.get("font-large.fnt");
		smallFont = assets.get("font-small.fnt");
		
		assets.load("movement_explore.png", Texture.class);
		assets.load("movement_fortify.png", Texture.class);
		assets.load("movement_provoke.png", Texture.class);
		assets.load("movement_retreat.png", Texture.class);
		assets.load("strategy_avoid.png", Texture.class);
		assets.load("strategy_charge.png", Texture.class);
		assets.load("strategy_defend.png", Texture.class);
		assets.load("strategy_regroup.png", Texture.class);
		assets.load("training_bows.png", Texture.class);
		assets.load("training_cannons.png", Texture.class);
		assets.load("training_militia.png", Texture.class);
		assets.load("training_swords.png", Texture.class);
		
		System.out.println("Creating factions");
		player = new Faction("Player", COLOUR_PLAYER, true);
		map.getTile(random.nextInt(MAP_SIZE), random.nextInt(MAP_SIZE)).setForce(player, MAX_FORCE);
		factions.add(player);
		
		Faction enemyOne = new Faction("Enemy One", COLOUR_ENEMY_ONE, false);
		map.getTile(random.nextInt(MAP_SIZE), random.nextInt(MAP_SIZE)).setForce(enemyOne, MAX_FORCE);
		factions.add(enemyOne);
		
		Faction enemyTwo = new Faction("Enemy Two", COLOUR_ENEMY_TWO, false);
		map.getTile(random.nextInt(MAP_SIZE), random.nextInt(MAP_SIZE)).setForce(enemyTwo, MAX_FORCE);
		factions.add(enemyTwo);
		
		choices = new Choices(this);
		clock = new Clock();
		
		clock.onStep(() -> {
			factions.sort(Comparator.comparingInt(Faction::getScore));
			factions.forEach(Faction::step);
		});
		clock.onTurn(() -> {
			factions.forEach(Faction::turn);
			
			if (lastChoice == 3) choices.createMovementChoice(player.getMovement());
			else if (lastChoice == 2) choices.createStrategyChoice(player.getStrategy());
			else choices.createTrainingChoice(player.getTraining());
			
			lastChoice++;
			if (lastChoice > 3) lastChoice = 1;
			
			isTurn = true;
		});
		
		batch = new SpriteBatch();
		
		// Initialise UI
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
	}
	
	public Stage getStage() {
		return stage;
	}
	
	public BitmapFont getLargeFont() {
		return largeFont;
	}
	
	public BitmapFont getSmallFont() {
		return smallFont;
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void render () {
		// Show loading screen until the game is loaded
		if (assets.getProgress() < 1) {
			assets.update();
			
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			batch.begin();
			
			largeFont.draw(batch, "Loading", 0, (Gdx.graphics.getHeight() / 2) + 20, Gdx.graphics.getWidth(), Align.center, false);
			largeFont.draw(batch, Math.round(assets.getProgress() * 100) + "%", 0, Gdx.graphics.getHeight() / 2, Gdx.graphics.getWidth(), Align.center, false);
			
			batch.end();
			return;
		}
		
		if (!isTurn) clock.tick(Gdx.graphics.getDeltaTime());
		map.each(Tile::update);
		
		Gdx.gl.glClearColor(COLOUR_BACKGROUND.r, COLOUR_BACKGROUND.g, COLOUR_BACKGROUND.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		
		// Draw the map tiles
		map.each(tile -> {
			Color tileColour = tile.getColour();
			if (tileColour == null) return;
			
			batch.draw(ColourUtils.getTexture(tileColour), tile.x * TILE_SIZE, tile.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
			smallFont.draw(batch, Integer.toString(tile.getForce()), tile.x * TILE_SIZE + 5, tile.y * TILE_SIZE + 7);
		});
		
		// Draw the info
		batch.draw(ColourUtils.getTexture(COLOUR_PANEL), MAP_DRAW_SIZE, 0, 200, MAP_DRAW_SIZE);
		
		batch.draw(ColourUtils.getTexture(Color.BLACK), MAP_DRAW_SIZE + 20, 0, 200, 10);
		batch.draw(ColourUtils.getTexture(Color.WHITE), MAP_DRAW_SIZE, 0, 200 - (clock.getProgress() * 200), 10);
		
		factions.forEach((faction) -> {
			int i = factions.indexOf(faction) + 1;
			largeFont.draw(batch, i + ". " + faction.getName() + ": " + faction.getScore(), MAP_DRAW_SIZE + 20, MAP_DRAW_SIZE - (20 * i));
		});
		
		batch.end();
		
		stage.act();
		stage.draw();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		assets.dispose();
	}
}
