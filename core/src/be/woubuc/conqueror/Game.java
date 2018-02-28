package be.woubuc.conqueror;

import be.woubuc.conqueror.factions.Faction;
import be.woubuc.conqueror.map.Tile;
import be.woubuc.conqueror.map.TileMap;
import be.woubuc.conqueror.screens.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static be.woubuc.conqueror.Globals.*;

public final class Game extends com.badlogic.gdx.Game {
	
	// Keep a static reference to the main instance
	private static Game game;
	public static Game get() { return game; }
	
	// Random number generator
	public static final Random random = new Random();
	
	static Drawable getDrawable(String id) {
		return new TextureRegionDrawable(new TextureRegion(get().assets.get(id, Texture.class)));
	}
	
	public final List<Faction> factions = new ArrayList<>();
	public Faction player;
	
	public final TileMap map = new TileMap(MAP_SIZE);
	
	public AssetManager assets;
	public SpriteBatch batch;
	public Stage stage;
	
	private final Screen loadingScreen = new LoadingScreen();
	public Screen gameScreen = new GameScreen();
	public ChoiceScreen choiceScreen = new ChoiceScreen();
	
	public final Screen victoryScreen = new VictoryScreen();
	public final Screen defeatScreen = new DefeatScreen();
	
	@Override
	public void create () {
		System.out.println("Initialising game");
		
		game = this;
		
		// Preload the loading screen font, since we need it right away
		assets = new AssetManager();
		assets.load("font-large.fnt", BitmapFont.class);
		assets.finishLoading();
		
		// Start loading the rest of the assets
		assets.load("font-small.fnt", BitmapFont.class);
		
		assets.load("icon_fight.png", Texture.class);
		
		assets.load("movement_explore.png", Texture.class);
		assets.load("movement_fortify.png", Texture.class);
		assets.load("movement_regroup.png", Texture.class);
		assets.load("movement_retreat.png", Texture.class);
		
		assets.load("strategy_avoid.png", Texture.class);
		assets.load("strategy_charge.png", Texture.class);
		assets.load("strategy_defend.png", Texture.class);
		assets.load("strategy_provoke.png", Texture.class);
		
		assets.load("training_bows.png", Texture.class);
		assets.load("training_cannons.png", Texture.class);
		assets.load("training_militia.png", Texture.class);
		assets.load("training_swords.png", Texture.class);
		
		// Prepare rendering
		batch = new SpriteBatch();
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		
		setScreen(loadingScreen);
		
		initialiseFactions();
	}
	
	private void initialiseFactions() {
		player = new Faction("Player", COLOUR_PLAYER, true);
		Tile playerStart = map.getTile(random.nextInt(MAP_SIZE), random.nextInt(MAP_SIZE));
		playerStart.claim(player);
		playerStart.swords = UNIT_SIZE_MAX;
		factions.add(player);
		
		Faction enemyOne = new Faction("Enemy One", COLOUR_ENEMY_ONE, false);
		Tile enemyOneStart = map.getTile(random.nextInt(MAP_SIZE), random.nextInt(MAP_SIZE));
		enemyOneStart.claim(enemyOne);
		enemyOneStart.swords = UNIT_SIZE_MAX;
		factions.add(enemyOne);
		
		Faction enemyTwo = new Faction("Enemy Two", COLOUR_ENEMY_TWO, false);
		Tile enemyTwoStart = map.getTile(random.nextInt(MAP_SIZE), random.nextInt(MAP_SIZE));
		enemyTwoStart.claim(enemyTwo);
		enemyTwoStart.swords = UNIT_SIZE_MAX;
		factions.add(enemyTwo);
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		stage.getViewport().update(width, height, true);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		assets.dispose();
	}
}
