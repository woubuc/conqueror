package be.woubuc.conqueror.screens;

import be.woubuc.conqueror.Clock;
import be.woubuc.conqueror.Faction;
import be.woubuc.conqueror.Game;
import be.woubuc.conqueror.map.Tile;
import be.woubuc.conqueror.util.ColourUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import static be.woubuc.conqueror.Globals.*;

public final class GameScreen implements Screen {
	
	private Game game;
	private Batch batch;
	
	private BitmapFont smallFont;
	private BitmapFont largeFont;
	
	private Clock clock = new Clock();
	
	@Override
	public void show() {
		System.out.println("Showing gameScreen");
		
		game = Game.get();
		batch = game.batch;
		
		largeFont = game.assets.get("font-large.fnt");
		smallFont = game.assets.get("font-small.fnt");
	}
	
	@Override
	public void render(float delta) {
		if (clock.tick(Gdx.graphics.getDeltaTime())) return;
		
		if (game.player.getScore() == MAP_SIZE * MAP_SIZE) {
			game.setScreen(game.victoryScreen);
			return;
		}
		
		Gdx.gl.glClearColor(COLOUR_BACKGROUND.r, COLOUR_BACKGROUND.g, COLOUR_BACKGROUND.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		
		// Draw the map tiles
		for (Tile[] row : game.map.tiles) {
			for (Tile tile : row) {
				if (tile.getOwner() == null) continue;
				
				batch.draw(ColourUtils.getTexture(tile.getColour(0.15f)), tile.x * TILE_SIZE, tile.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
				
				if (tile.wasAttacked()) batch.draw(game.assets.get("icon_fight.png", Texture.class), tile.x * TILE_SIZE, tile.y * TILE_SIZE);
				
				if (tile.isFrontline()) {
					TextureRegion colour = ColourUtils.getTexture(tile.getColour(0.4f));
					
					Tile left = tile.getRelative(-1, 0);
					if (left != null && left.getOwner() != tile.getOwner()) batch.draw(colour, tile.x * TILE_SIZE, tile.y * TILE_SIZE, 1, TILE_SIZE);
					
					Tile right = tile.getRelative(1, 0);
					if (right != null && right.getOwner() != tile.getOwner()) batch.draw(colour, (tile.x + 1) * TILE_SIZE - 1, tile.y * TILE_SIZE, 1, TILE_SIZE);
					
					Tile top = tile.getRelative(0, 1);
					if (top != null && top.getOwner() != tile.getOwner()) batch.draw(colour, tile.x * TILE_SIZE, (tile.y + 1) * TILE_SIZE - 1, TILE_SIZE, 1);
					
					Tile bottom = tile.getRelative(0, -1);
					if (bottom != null && bottom.getOwner() != tile.getOwner()) batch.draw(colour, tile.x * TILE_SIZE, tile.y * TILE_SIZE, TILE_SIZE, 1);
				}
				
				//smallFont.draw(batch, tile.getAttacked() + "," + tile.getDefense() + "\n" + tile.getUnits(), tile.x * TILE_SIZE + 2, tile.y * TILE_SIZE + 18);
			}
		}
		
		// Draw the info
		int mapSizePx = MAP_SIZE * TILE_SIZE;
		batch.draw(ColourUtils.getTexture(COLOUR_PANEL), mapSizePx, 0, 200, mapSizePx);
		
		batch.draw(ColourUtils.getTexture(COLOUR_PANEL_ACTIVE), mapSizePx, 0, 200, 8);
		batch.draw(ColourUtils.getTexture(Color.WHITE), mapSizePx, 0, 200 - (clock.getProgress() * 200), 8);
		
		float maxScore = (float) (MAP_SIZE * MAP_SIZE);
		
		float maxFactionAtk = 0;
		float maxFactionDef = 0;
		for (Faction faction : game.factions) {
			float atk = faction.getAttack();
			if (atk > maxFactionAtk) maxFactionAtk = atk;
			
			float def = faction.getDefense();
			if (def > maxFactionDef) maxFactionDef = def;
		}
		
		for (Faction faction : game.factions) {
			largeFont.draw(batch, "Territory", mapSizePx + 20, mapSizePx - 15);
			
			int i = game.factions.indexOf(faction) + 1;
			float score = (float) faction.getScore() / maxScore * 160;
			batch.draw(ColourUtils.getTexture(COLOUR_PANEL_ACTIVE), mapSizePx + 20, mapSizePx - 20 - (40 * i), 160, 20);
			batch.draw(ColourUtils.getTexture(faction.colour), mapSizePx + 20, mapSizePx - 20 - (40 * i), score, 20);
			
			smallFont.draw(batch, faction.name, mapSizePx + 20, mapSizePx - 20 - (40 * i) + 26);
			smallFont.draw(batch, Integer.toString(faction.getScore()), mapSizePx + 25, mapSizePx - 20 - (40 * i) + 11);
			
			
			largeFont.draw(batch, "Attack", mapSizePx + 20, mapSizePx - 175);
			largeFont.draw(batch, "Defense", mapSizePx + 110, mapSizePx - 175);
			
			float attack = faction.getAttack() / maxFactionAtk * 70;
			float defense = faction.getDefense() / maxFactionDef * 70;
			batch.draw(ColourUtils.getTexture(COLOUR_PANEL_ACTIVE), mapSizePx + 20, mapSizePx - 180 - (40 * i), 70, 20);
			batch.draw(ColourUtils.getTexture(COLOUR_PANEL_ACTIVE), mapSizePx + 110, mapSizePx - 180 - (40 * i), 70, 20);
			batch.draw(ColourUtils.getTexture(faction.colour), mapSizePx + 20, mapSizePx - 180 - (40 * i), attack, 20);
			batch.draw(ColourUtils.getTexture(faction.colour), mapSizePx + 110, mapSizePx - 180 - (40 * i), defense, 20);
			
			smallFont.draw(batch, faction.name, mapSizePx + 20, mapSizePx - 180 - (40 * i) + 26);
			smallFont.draw(batch, Integer.toString(Math.round(faction.getAttack())), mapSizePx + 25, mapSizePx - 180 - (40 * i) + 11);
			smallFont.draw(batch, Integer.toString(Math.round(faction.getDefense())), mapSizePx + 115, mapSizePx - 180 - (40 * i) + 11);
		}
		
		batch.end();
	}
	
	@Override public void hide() {
		System.out.println("Hiding gameScreen");
		
		game = null;
		batch = null;
		
		largeFont = null;
		smallFont = null;
	}
	
	@Override public void pause() { }
	@Override public void resume() { }
	@Override public void resize(int width, int height) { }
	@Override public void dispose() { }
}
