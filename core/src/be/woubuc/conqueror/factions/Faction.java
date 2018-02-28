package be.woubuc.conqueror.factions;

import be.woubuc.conqueror.Game;
import be.woubuc.conqueror.focus.Movement;
import be.woubuc.conqueror.focus.Strategy;
import be.woubuc.conqueror.focus.Training;
import be.woubuc.conqueror.map.Tile;
import com.badlogic.gdx.graphics.Color;

import java.util.*;

import static be.woubuc.conqueror.Globals.*;

public class Faction {
	
	private Set<Unit> units = new HashSet<>();
	
	public final String name;
	public final Color colour;
	private final boolean playerControlled;
	
	public Training training = Training.CANNONS;
	public Movement movement = Movement.EXPLORE;
	public Strategy strategy = Strategy.AVOID;
	
	private final Set<Tile> ownedTiles = new HashSet<>();
	private final Set<Tile> frontlineTiles = new HashSet<>();
	
	private final List<Tile> neighbourList = new ArrayList<>(5);
	
	private float unitsToAdd = 0;
	
	public Faction(String name, Color colour, boolean playerControlled) {
		this.name = name;
		this.colour = colour;
		this.playerControlled = playerControlled;
		
		System.out.println("Faction " + name + " created");
	}
	
	public void addTile(Tile tile) {
		ownedTiles.add(tile);
		updateFrontline(tile);
		
		for (Tile t : tile.getSurrounding()) {
			updateFrontline(t);
		}
	}
	
	public void removeTile(Tile tile) {
		ownedTiles.remove(tile);
		frontlineTiles.remove(tile);
		
		for (Tile t : tile.getSurrounding()) {
			updateFrontline(t);
		}
	}
	
	/**
	 * @return True if this faction is eliminated (has lost all their territory)
	 */
	public boolean isEliminated() {
		return ownedTiles.size() == 0;
	}
	
	/**
	 * @return The score of this faction
	 */
	public int getScore() {
		return ownedTiles.size();
	}
	
	/**
	 * @return The total attack value of all tiles owned by this faction
	 */
	public float getAttack() {
		float attack = 0;
		for (Tile tile : ownedTiles) {
			attack += tile.getAttacked();
		}
		return attack;
	}
	
	/**
	 * @return The total defense value of all tiles owned by this faction
	 */
	public float getDefense() {
		float defense = 0;
		for (Tile tile : ownedTiles) {
			defense += tile.getDefense();
		}
		return defense;
	}
	
	/**
	 * @return The total number of units across all tiles owned by this faction
	 */
	private int getUnits() {
		int units = 0;
		for (Tile tile : ownedTiles) {
			units += tile.getUnits();
		}
		return units;
	}
	
	/**
	 * @param tile The tile to check
	 * @return True if the given tile is on the frontline of this faction
	 */
	public boolean isFrontline(Tile tile) {
		return frontlineTiles.contains(tile);
	}
	
	/**
	 * @return True if all tiles of this faction are almost full
	 */
	private boolean isNearUnitLimit() {
		return getUnits() >= ownedTiles.size() * (UNIT_SIZE_MAX - 1);
	}
	
	/**
	 * Goes through all frontline tiles and pulls units from non-frontline tiles
	 * until the frontline is filled up or there are no more units left to pull.
	 */
	public void pullToFrontline() {
		if (isEliminated()) return;
		
		/* First we need to build up a set of tiles, to prevent concurrent modification
		 * exceptions that would occur if we operate on these tiles directly.
		 */
		Set<Tile> tiles = new HashSet<>();
		for (Tile tile : frontlineTiles) {
			if (tile.getUnits() >= UNIT_SIZE_MAX) continue; // Don't pull to full tiles
			tiles.add(tile);
		}
		
		for (Tile tile : tiles) {
			for (Tile adjacentTile : tile.getSurrounding()) {
				if (adjacentTile.getOwner() != this) continue;
				
				if (adjacentTile.isFrontline()) {
					/* Only pull from other frontline tiles if there are more units than
					 * on the current tile, and even then only equalise the number of units
					 * on both tiles, instead of moving all excess units.
					 */
					if (adjacentTile.getUnits() < tile.getUnits()) continue;
					
					int unitsToMove = (adjacentTile.getUnits() - tile.getUnits()) / 2;
					if (unitsToMove == 0) continue;
					
					adjacentTile.moveUnits(unitsToMove, tile);
				} else {
					int unitsToMove = adjacentTile.getUnits() - adjacentTile.getMinimumUnits();
					if (unitsToMove < 1) continue;
					
					if (tile.getUnits() + unitsToMove > UNIT_SIZE_MAX) unitsToMove = UNIT_SIZE_MAX - tile.getUnits();
					
					adjacentTile.moveUnits(unitsToMove, tile);
				}
			}
		}
	}
	
	/**
	 * Goes through all non-frontline tiles and tries to equalise the amount of
	 * units on all tiles, so that the inner territory ends up being equally
	 * defended everywhere and excess units will eventually find their way to
	 * the frontline.
	 */
	public void equaliseUnits() {
		if (isEliminated()) return;
		
		Set<Tile> innerTiles = new HashSet<>();
		for (Tile tile : ownedTiles) {
			if (tile.isFrontline()) continue; // Don't equalise frontline tiles
			innerTiles.add(tile);
		}
		
		for (Tile tile : innerTiles) {
			neighbourList.clear();
			neighbourList.add(tile);
			for (Tile t : tile.getSurrounding()) {
				if (!t.isFrontline()) neighbourList.add(t);
			}
			
			// Find the tile with the most and least force and move force between them
			// Rinse and repeat until it's approximately equalised
			int maxTries = 10;
			while (maxTries > 0) {
				maxTries--;
				
				int max = 0;
				int min = UNIT_SIZE_MAX;
				
				Tile maxTile = null;
				Tile minTile = null;
				
				for (Tile t : neighbourList) {
					int units = t.getUnits();
					
					if (units > max) {
						max = units;
						maxTile = t;
					}
					
					if (units < min) {
						min = units;
						minTile = t;
					}
				}
				
				if (minTile == null || maxTile == null) break;
				if (minTile == maxTile) break;
				if (max - min < 2) break;
				
				maxTile.moveUnits(1, minTile);
			}
		}
	}
	
	/**
	 * Increases the number of units in random inner territory tiles.
	 */
	public void recruitUnits() {
		if (isEliminated()) return;
		
		// Number of units to add this step
		float unitsToAdd = ownedTiles.size() * UNIT_INCREASE_PER_TURN;
		if (unitsToAdd < UNIT_INCREASE_MIN) unitsToAdd = UNIT_INCREASE_MIN;
		
		switch(training) {
			case BOWS: unitsToAdd *= UNIT_INCREASE_BOWS_MULTIPLIER; break;
			case CANNONS: unitsToAdd *= UNIT_INCREASE_CANNONS_MULTIPLIER; break;
			case MILITIA: unitsToAdd *= UNIT_INCREASE_MILITIA_MULTIPLIER; break;
		}
		
		if (unitsToAdd > UNIT_INCREASE_MAX) unitsToAdd = UNIT_INCREASE_MAX;
		
		/* Keep track of the units to add between steps, otherwise we lose precision
		 * and 1.9 units would be the same as 1.0 units due to float->int conversion
		 * dropping everything after the decimal point. This way, that remaining
		 * 0.9 units can be added next time around.
		 */
		this.unitsToAdd += unitsToAdd;
		int unitsAdd = (int) this.unitsToAdd;
		this.unitsToAdd -= unitsAdd;
		
		List<Tile> ownedTilesList = new ArrayList<>(ownedTiles);
		
		int tries = 0;
		int maxTries = unitsAdd * 2;
		while (unitsAdd > 0) {
			if (tries++ > maxTries) break;
			
			Tile tile = ownedTilesList.get(Game.random.nextInt(ownedTilesList.size()));
			if (tile.getUnits() >= UNIT_SIZE_MAX) continue;
			
			switch(training) {
				case SWORDS: tile.swords++; break;
				case BOWS: tile.bows++; break;
				case CANNONS: tile.cannons++; break;
				case MILITIA: tile.militia++; break;
			}
			
			unitsAdd--;
		}
	}
	
	/**
	 * Attempts to conquer adjacent enemy tiles.
	 */
	public void attackEnemies() {
		if (isEliminated()) return;
		
		// Don't attack if the choices dictates not to attack
		if (strategy == Strategy.AVOID && !isNearUnitLimit()) return;
		if (strategy == Strategy.DEFEND && !isNearUnitLimit()) return;
		if (movement == Movement.RETREAT && isNearUnitLimit()) return;
		
		// Find all targets along the frontline
		Map<Tile, List<Tile>> potentialAttackTargets = new HashMap<>();
		
		for (Tile tile : frontlineTiles) {
			List<Tile> attackTargets = new ArrayList<>();
			
			for (Tile target : tile.getSurrounding()) {
				if (target.getOwner() == null) continue;
				if (target.getOwner() == this) continue;
				
				if (target.getDefense() <= tile.getAttacked()) attackTargets.add(target);
				else if (tile.getUnits() >= UNIT_SIZE_MAX - UNIT_SIZE_MIN) attackTargets.add(target);
				else if (Game.random.nextInt(5) < (strategy == Strategy.CHARGE ? 3 : 1)) attackTargets.add(target);
			}
			
			if (attackTargets.size() > 0) potentialAttackTargets.put(tile, attackTargets);
		}
		
		potentialAttackTargets.forEach((from, targets) -> {
			// Determine whether or not to attack
			int chance = Game.random.nextInt(4);
			
			int limit = 1;
			if (strategy == Strategy.CHARGE) limit += 2;
			if (movement == Movement.RETREAT) limit--;
			if (chance > limit) return;
			
			Tile target = targets.get(Game.random.nextInt(targets.size()));
			from.attack(target);
		});
	}
	
	/**
	 * Fifth action of a game step. Expands territory into unclaimed
	 * tiles, but only from frontline tiles that don't border enemy tiles.
	 */
	public void expandTerritory() {
		if (isEliminated()) return;
		
		if (strategy == Strategy.DEFEND && !isNearUnitLimit() && movement != Movement.EXPLORE) return;
		
		Map<Tile, List<Tile>> potentialExploreTargets = new HashMap<>();
		
		for (Tile tile : frontlineTiles) {
			List<Tile> exploreTargets = new ArrayList<>();
			
			for (Tile t : tile.getSurrounding()) {
				if (t.getOwner() == null) exploreTargets.add(t);
			}
			
			if (exploreTargets.size() < 1) continue;
			
			potentialExploreTargets.put(tile, exploreTargets);
		}
		
		potentialExploreTargets.forEach((originTile, targets) -> {
			int toMove = originTile.getUnits() - originTile.getMinimumUnits();
			if (toMove < 1) return;
			
			if (movement == Movement.EXPLORE) {
				while (toMove > 0) {
					for (Tile target : targets) {
						target.claim(this);
						originTile.moveUnits(1, target);
						
						toMove--;
						if (toMove <= 0) break;
					}
				}
			} else {
				Tile target = targets.get(Game.random.nextInt(targets.size()));
				target.claim(this);
				originTile.moveUnits(toMove, target);
			}
		});
	}
	
	/**
	 * Updates the frontline status for a given tile and its
	 * surrounding tiles.
	 *
	 * @param tile The tile to check
	 */
	private void updateFrontline(Tile tile) {
		updateFrontlineTile(tile);
		
		for (Tile t : tile.getSurrounding()) {
			updateFrontlineTile(t);
		}
	}
	
	private void updateFrontlineTile(Tile tile) {
		if (tile.getOwner() != this) return;
		
		boolean frontline = false;
		for (Tile t : tile.getSurrounding()) {
			if (t == null) continue;
			if (t.getOwner() != this) {
				frontline = true;
				break;
			}
		}
		
		if (frontline) frontlineTiles.add(tile);
		else frontlineTiles.remove(tile);
	}
	
	/**
	 * Makes AI enemies randomly change one of their choices on every turn.
	 */
	public void turn() {
		if (isEliminated()) return;
		
		if (!playerControlled) {
			int chance = Game.random.nextInt(3);
			int i = Game.random.nextInt(4);
			
			switch (chance) {
				case 0: movement = Movement.values()[i]; break;
				case 1: strategy = Strategy.values()[i]; break;
				case 2: training = Training.values()[i]; break;
			}
		}
	}
}
