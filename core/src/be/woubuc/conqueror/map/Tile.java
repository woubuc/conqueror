package be.woubuc.conqueror.map;

import be.woubuc.conqueror.Faction;
import be.woubuc.conqueror.Game;
import be.woubuc.conqueror.util.ColourUtils;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import static be.woubuc.conqueror.Globals.MAX_FORCE;

/**
 * A single tile on the game map
 */
public class Tile {
	
	// Strength of each faction on this tile
	private Faction owner;
	
	private float currentForce = 0;
	private int force = 0;
	
	public final int x;
	public final int y;
	
	Tile(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	Tile getRelative(int x, int y) {
		return Game.map.getTile(this.x + x, this.y + y);
	}
	
	public Color getColour() {
		if (owner == null) return null;
		
		Color colour = owner.getColour();
		return ColourUtils.alpha(colour, currentForce / (float) MAX_FORCE);
	}
	
	public List<Tile> getSurrounding() {
		List<Tile> tiles = new ArrayList<>();
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				if (x == y) continue;
				Tile t = getRelative(x, y);
				if (t != null) tiles.add(t);
			}
		}
		return tiles;
	}
	
	public void setForce(Faction owner, int force) {
		owner.addTile(this);
		this.owner = owner;
		this.force = force;
	}
	
	public boolean hasOwner() {
		return owner != null;
	}
	
	public Faction getOwner() {
		return owner;
	}
	
	public int getForce() {
		return force;
	}
	
	public void increaseForce() {
		if (force < MAX_FORCE) {
			if (Game.random.nextInt(MAX_FORCE) >= force) force++;
		}
	}
	
	public void update() {
		if (currentForce < force) currentForce += 0.05f;
		else if (currentForce > force) currentForce -= 0.05f;
	}
}