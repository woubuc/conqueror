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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
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
	
	private Batch batch;
	private Stage stage;
	
	private BitmapFont largeFont;
	private BitmapFont smallFont;
	
	boolean isTurn = false;
	
	private int lastChoice = 0;
	private boolean gameStart = true;
	
	@Override
	public void create () {
		assets = new AssetManager();
		assets.load("font-large.fnt", BitmapFont.class);
		assets.load("font-small.fnt", BitmapFont.class);
		assets.finishLoading();
		
		largeFont = assets.get("font-large.fnt");
		smallFont = assets.get("font-small.fnt");
		
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
		
		System.out.println("Creating factions");
		player = new Faction("Player", COLOUR_PLAYER, true);
		Tile playerStart = map.getTile(random.nextInt(MAP_SIZE), random.nextInt(MAP_SIZE));
		playerStart.claim(player);
		playerStart.swords = MAX_UNITS;
		factions.add(player);
		
		Faction enemyOne = new Faction("Enemy One", COLOUR_ENEMY_ONE, false);
		Tile enemyOneStart = map.getTile(random.nextInt(MAP_SIZE), random.nextInt(MAP_SIZE));
		enemyOneStart.claim(enemyOne);
		enemyOneStart.swords = MAX_UNITS;
		factions.add(enemyOne);
		
		Faction enemyTwo = new Faction("Enemy Two", COLOUR_ENEMY_TWO, false);
		Tile enemyTwoStart = map.getTile(random.nextInt(MAP_SIZE), random.nextInt(MAP_SIZE));
		enemyTwoStart.claim(enemyTwo);
		enemyTwoStart.swords = MAX_UNITS;
		factions.add(enemyTwo);
		
		choices = new Choices(this);
		clock = new Clock();
		
		clock.onStep(() -> {
			long time = System.currentTimeMillis();
			
			factions.forEach(Faction::pullToFrontline);
			factions.forEach(Faction::attackEnemies);
			
			factions.forEach(Faction::pullToFrontline);
			factions.forEach(Faction::equaliseUnits);
			
			factions.forEach(Faction::recruitUnits);
			factions.forEach(Faction::expandTerritory);
			
			System.out.println("Completed step in " + (System.currentTimeMillis() - time) + "ms");
		});
		clock.onTurn(() -> {
			factions.forEach(Faction::turn);
			
			int choice = lastChoice;
			if (gameStart) {
				choice = 0;
				gameStart = false;
			} else {
				while (choice == lastChoice) {
					choice = Game.random.nextInt(3);
				}
			}
			
			switch(choice) {
				case 0: choices.createTrainingChoice(); break;
				case 1: choices.createStrategyChoice(); break;
				default: choices.createMovementChoice(); break;
			}
			
			lastChoice = choice;
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
		
		if (isTurn) {
			stage.act();
			stage.draw();
			return;
		}
		
		clock.tick(Gdx.graphics.getDeltaTime());
		
		Gdx.gl.glClearColor(COLOUR_BACKGROUND.r, COLOUR_BACKGROUND.g, COLOUR_BACKGROUND.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		
		// Draw the map tiles
		for (Tile[] row : map.tiles) {
			for (Tile tile : row) {
				if (tile.getOwner() == null) continue;
				
				batch.draw(ColourUtils.getTexture(tile.getColour(0.2f)), tile.x * TILE_SIZE, tile.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
				
				if (tile.wasAttacked()) batch.draw(assets.get("icon_fight.png", Texture.class), tile.x * TILE_SIZE, tile.y * TILE_SIZE);
				
				if (tile.isFrontline()) {
					TextureRegion colour = ColourUtils.getTexture(tile.getColour(0.5f));
					
					Tile left = tile.getRelative(-1, 0);
					if (left != null && left.getOwner() != tile.getOwner()) batch.draw(colour, tile.x * TILE_SIZE, tile.y * TILE_SIZE, 1, TILE_SIZE);
					
					Tile right = tile.getRelative(1, 0);
					if (right != null && right.getOwner() != tile.getOwner()) batch.draw(colour, (tile.x + 1) * TILE_SIZE - 1, tile.y * TILE_SIZE, 1, TILE_SIZE);
					
					Tile top = tile.getRelative(0, 1);
					if (top != null && top.getOwner() != tile.getOwner()) batch.draw(colour, tile.x * TILE_SIZE, (tile.y + 1) * TILE_SIZE - 1, TILE_SIZE, 1);
					
					Tile bottom = tile.getRelative(0, -1);
					if (bottom != null && bottom.getOwner() != tile.getOwner()) batch.draw(colour, tile.x * TILE_SIZE, tile.y * TILE_SIZE, TILE_SIZE, 1);
				}
				
				smallFont.draw(batch, tile.getAttacked() + "," + tile.getDefense() + "\n" + tile.getUnits(), tile.x * TILE_SIZE + 2, tile.y * TILE_SIZE + 18);
			}
		}
		
		// Draw the info
		batch.draw(ColourUtils.getTexture(COLOUR_PANEL), MAP_DRAW_SIZE, 0, 200, MAP_DRAW_SIZE);
		
		batch.draw(ColourUtils.getTexture(COLOUR_PANEL_ACTIVE), MAP_DRAW_SIZE, 0, 200, 8);
		batch.draw(ColourUtils.getTexture(Color.WHITE), MAP_DRAW_SIZE, 0, 200 - (clock.getProgress() * 200), 8);
		
		int startX = MAP_DRAW_SIZE;
		int topY = MAP_DRAW_SIZE;
		float maxScore = (float) (MAP_SIZE * MAP_SIZE);
		
		float maxFactionAtk = 0;
		float maxFactionDef = 0;
		for (Faction faction : factions) {
			float atk = faction.getAttack();
			if (atk > maxFactionAtk) maxFactionAtk = atk;
			
			float def = faction.getDefense();
			if (def > maxFactionDef) maxFactionDef = def;
		}
		
		for (Faction faction : factions) {
			largeFont.draw(batch, "Territory", startX + 20, topY - 15);
			
			int i = factions.indexOf(faction) + 1;
			float score = (float) faction.getScore() / maxScore * 160;
			batch.draw(ColourUtils.getTexture(COLOUR_PANEL_ACTIVE), startX + 20, topY - 20 - (40 * i), 160, 20);
			batch.draw(ColourUtils.getTexture(faction.colour), startX + 20, topY - 20 - (40 * i), score, 20);
			
			smallFont.draw(batch, faction.name, startX + 20, topY - 20 - (40 * i) + 26);
			smallFont.draw(batch, Integer.toString(faction.getScore()), startX + 25, topY - 20 - (40 * i) + 11);
			
			
			largeFont.draw(batch, "Attack", startX + 20, topY - 175);
			largeFont.draw(batch, "Defense", startX + 110, topY - 175);
			
			float attack = faction.getAttack() / maxFactionAtk * 70;
			float defense = faction.getDefense() / maxFactionDef * 70;
			batch.draw(ColourUtils.getTexture(COLOUR_PANEL_ACTIVE), startX + 20, topY - 180 - (40 * i), 70, 20);
			batch.draw(ColourUtils.getTexture(COLOUR_PANEL_ACTIVE), startX + 110, topY - 180 - (40 * i), 70, 20);
			batch.draw(ColourUtils.getTexture(faction.colour), startX + 20, topY - 180 - (40 * i), attack, 20);
			batch.draw(ColourUtils.getTexture(faction.colour), startX + 110, topY - 180 - (40 * i), defense, 20);
			
			smallFont.draw(batch, faction.name, startX + 20, topY - 180 - (40 * i) + 26);
			smallFont.draw(batch, Integer.toString(Math.round(faction.getAttack())), startX + 25, topY - 180 - (40 * i) + 11);
			smallFont.draw(batch, Integer.toString(Math.round(faction.getDefense())), startX + 115, topY - 180 - (40 * i) + 11);
		}
		
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		assets.dispose();
	}
}
