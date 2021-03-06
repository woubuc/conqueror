package be.woubuc.conqueror.map;

import be.woubuc.conqueror.Game;
import be.woubuc.conqueror.factions.Faction;
import be.woubuc.conqueror.factions.Unit;
import be.woubuc.conqueror.focus.Movement;
import be.woubuc.conqueror.focus.Strategy;
import be.woubuc.conqueror.util.ColourUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

import static be.woubuc.conqueror.Globals.*;

/**
 * A single tile on the game map
 */
public class Tile {
	
	private Faction owner;
	
	public int swords = 0;
	public int bows = 0;
	public int cannons = 0;
	public int militia = 0;
	
	private final TileMap map;
	public final int x;
	public final int y;
	
	private float attacked = 0;
	private float unitsEase = 0;
	
	private Unit unit;
	
	private Tile[] neighbours;
	private TileConnection[] connections;
	private final Array<Connection> connectionArray = new Array<>(4);
	
	/**
	 * Creates the tile
	 * @param x The X coordinate on the map
	 * @param y The Y coordinate on the map
	 */
	Tile(TileMap map, int x, int y) {
		this.map = map;
		this.x = x;
		this.y = y;
	}
	
	public TileMap getMap() {
		return map;
	}
	
	/**
	 * Have a unit enter this tile
	 * @param unit The unit
	 */
	public boolean enter(Unit unit) {
		if (this.unit != null) return false;
		this.unit = unit;
		return true;
	}
	
	/**
	 * Remove the current unit from this tile
	 */
	public void exit() {
		unit = null;
	}
	
	/**
	 * Initialises the tile. Should be called after all tiles are created.
	 */
	void init() {
		// Find neighbours
		Tile above = map.getTile(x, y + 1);
		Tile right = map.getTile(x + 1, y);
		Tile below = map.getTile(x, y - 1);
		Tile left = map.getTile(x - 1, y);
		
		List<Tile> tiles = new ArrayList<>(4);
		if (above != null) tiles.add(above);
		if (right != null) tiles.add(right);
		if (below != null) tiles.add(below);
		if (left != null) tiles.add(left);
		
		neighbours = new Tile[tiles.size()];
		connections = new TileConnection[tiles.size()];
		
		for (int i = 0; i < tiles.size(); i++) {
			neighbours[i] = tiles.get(i);
			connections[i] = new TileConnection(this, tiles.get(i));
		}
		
		tiles.clear();
	}
	
	/**
	 * Gets a tile relative to this tile
	 * @param x The X offset
	 * @param y The Y offset
	 * @return The tile, or null if no tile was found
	 */
	public Tile getRelative(int x, int y) {
		return Game.get().map.getTile(this.x + x, this.y + y);
	}
	
	/**
	 * @return The 4 tiles directly adjacent to this tile
	 */
	public Tile[] getSurrounding() {
		return neighbours;
	}
	
	/**
	 * Gets the colour of the tile, with easing to fade between unit values
	 * @param offset The base offset (minimum alpha of the colour)
	 * @return The colour
	 */
	public Color getColour(float offset) {
		int units = getUnits();
		if (unitsEase != units) {
			unitsEase += (units - unitsEase) / 15;
			if (Math.abs(unitsEase - units) < 0.05f) unitsEase = units;
		}
		
		float colourAlpha = offset + (unitsEase / (float) UNIT_SIZE_MAX) * (1f - offset);
		return ColourUtils.alpha(getOwner().colour, colourAlpha);
	}
	
	/**
	 * Gets the connections for the pathfinder
	 * @return An array containing the connecting tiles
	 */
	public Array<Connection> getConnections() {
		connectionArray.clear();
		for (TileConnection connection : connections) {
			if (connection.isValid()) connectionArray.add(connection);
		}
		return connectionArray;
	}
	
	/**
	 * @return True if this tile was attacked during the last step
	 */
	public boolean wasAttacked() {
		if (attacked <= 0) return false;
		attacked -= Gdx.graphics.getDeltaTime();
		return true;
	}
	
	/**
	 * Sets the attacked flag
	 */
	private void setAttacked() {
		attacked = TIME_PER_STEP;
	}
	
	/**
	 * @return The current owner of this tile
	 */
	public Faction getOwner() {
		return owner;
	}
	
	/**
	 * Adds a new owner to the tile
	 */
	public void claim(Faction faction) {
		owner = faction;
		owner.addTile(this);
	}
	
	/**
	 * Removes the current owner from this tile
	 */
	private void abandon() {
		swords = 0;
		bows = 0;
		cannons = 0;
		militia = 0;
		
		Faction owner = this.owner;
		this.owner = null;
		
		owner.removeTile(this);
	}
	
	/**
	 * @return The total number of units stationed on this tile
	 */
	public int getUnits() {
		return swords + bows + cannons + militia;
	}
	
	/**
	 * Moves units from this tile to another tile. If defensive strategies
	 * were chosen, defense units will be moved first.
	 * @param units The number of units to move
	 * @param target The target tile
	 */
	public void moveUnits(int units, Tile target) {
		while (units > 0) {
			if (getUnits() <= getMinimumUnits()) break;
			
			if (militia > 0) {
				militia--;
				target.militia++;
			} else {
				int chance = Game.random.nextInt(3);
				boolean defend = owner.strategy == Strategy.DEFEND || owner.movement == Movement.FORTIFY;
				
				if (bows > 0 && chance < (defend ? 1 : 2)) {
					bows--;
					target.bows++;
				} else if (cannons > 0 && chance < (defend ? 2 : 1)) {
					cannons--;
					target.cannons++;
				} else if (swords > 0 && chance < 2) {
					swords--;
					target.swords++;
				} else if (bows > 0) {
					bows--;
					target.bows++;
				} else if (swords > 0) {
					swords--;
					target.swords++;
				} else if (cannons > 0) {
					cannons--;
					target.cannons++;
				}
			}
			
			units--;
			if (target.getUnits() >= UNIT_SIZE_MAX) break;
		}
	}
	
	/**
	 * @return The minimum unit count that should be on this tile
	 */
	public int getMinimumUnits() {
		if (owner == null) return UNIT_SIZE_MIN;
		
		int min = UNIT_SIZE_MIN;
		if (isFrontline() && owner.movement != Movement.EXPLORE) min = MIN_UNITS_FRONTLINE;
		
		if (isFrontline() && owner.movement == Movement.FORTIFY) min *= MIN_UNITS_FORTIFY_MULTIPLIER;
		else if (owner.strategy == Strategy.DEFEND) min *= MIN_UNITS_DEFEND_MULTIPLIER;
		
		return min;
	}
	
	/**
	 * @return The total attack force of this tile
	 */
	public float getAttacked() {
		return (swords * UNIT_ATK_SWORDS) +
				(bows * UNIT_ATK_BOWS) +
				(cannons * UNIT_ATK_CANNONS) +
				(militia * UNIT_ATK_MILITIA);
	}
	
	/**
	 * @return The total defense force of this tile
	 */
	public float getDefense() {
		return (swords * UNIT_DEF_SWORDS) +
				(bows * UNIT_DEF_BOWS) +
				(cannons * UNIT_DEF_CANNONS) +
				(militia * UNIT_DEF_MILITIA);
	}
	
	/**
	 * @return True if this tile is on the frontline of its faction,
	 *         false if it's an inner territory or unowned.
	 */
	public boolean isFrontline() {
		if (owner == null) return false;
		return owner.isFrontline(this);
	}
	
	/**
	 * Attack another tile from this tile
	 * @param target The target tile
	 */
	public void attack(Tile target) {
		if (target.getOwner() == null) return;
		if (target.getOwner() == owner) return;
		if (owner == null) return;
		
		target.setAttacked();
		
		int atk = (int) (Game.random.nextFloat() * getAttacked());
		int def = (int) (Game.random.nextFloat() * target.getDefense());
		
		if (atk < 2) atk = 2;
		if (def < 2) def = 2;
		
		if (target.owner.movement == Movement.FORTIFY) def *= FORTIFY_MULTIPLIER;
		if (target.owner.strategy == Strategy.DEFEND) def *= FORTIFY_MULTIPLIER;
		if (owner.strategy == Strategy.CHARGE) atk *= CHARGE_MULTIPLIER;
		
		// Remove units from both sides based on atk and def values
		// Attacker loses based on def roll, defender loses based on atk roll
		while (true) {
			if (militia > 0) {
				militia -= Game.random.nextInt(def);
				if (militia < 0) militia = 0;
			} else if (swords > 0) {
				swords -= Game.random.nextInt(def);
				if (swords < 0) swords = 0;
			} else if (cannons > 0) {
				cannons -= Game.random.nextInt(def);
				if (cannons < 0) cannons = 0;
			} else if (bows > 0) {
				bows -= Game.random.nextInt(def);
				if (bows < 0) bows = 0;
			} else {
				break;
			}
			
			if (target.militia > 0) {
				target.militia -= Game.random.nextInt(atk);
				if (target.militia < 0) target.militia = 0;
			} else if (target.swords > 0) {
				target.swords -= Game.random.nextInt(atk);
				if (target.swords < 0) target.swords = 0;
			} else if (target.bows > 0) {
				target.bows -= Game.random.nextInt(atk);
				if (target.bows < 0) target.bows = 0;
			} else if (target.cannons > 0) {
				target.cannons -= Game.random.nextInt(atk);
				if (target.cannons < 0) target.cannons = 0;
			} else {
				break;
			}
		}
		
		// If any of the tile's units reach 0, that tile is abandoned
		if (getUnits() < 1) abandon();
		if (target.getUnits() < 1) target.abandon();
		
		// If we have not enough attacking units left to move to a new tile, don't continue the attack
		if (getUnits() <= 1) return;
		
		// Move our units to the newly conquered tile
		if (target.owner == null) {
			target.claim(owner);
			int move = getUnits() - 1;
			moveUnits(move, target);
		}
	}
	
	private class TileConnection implements Connection {
		
		private final Tile from;
		private final Tile to;
		
		TileConnection(Tile from, Tile to) {
			this.from = from;
			this.to = to;
		}
		
		/** @return True if both tiles are owned by the same faction */
		boolean isValid() { return from.getOwner() == to.getOwner(); }
		
		@Override public float getCost() { return 1; }
		@Override public Object getFromNode() { return from; }
		@Override public Object getToNode() { return to; }
	}
}