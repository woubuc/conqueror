package be.woubuc.conqueror.factions;

import be.woubuc.conqueror.map.Tile;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

import static be.woubuc.conqueror.Globals.*;

public final class Unit implements Pool.Poolable {
	
	// Pool unit instances
	private static Pool<Unit> pool = Pools.get(Unit.class);
	
	/**
	 * Gets a new unit instance from the pool
	 * @param tile Tile where the unit starts
	 * @param type The unit type
	 * @param size The initial size of the unit
	 */
	public static Unit get(Tile tile, UnitTypes type, int size) {
		Unit unit = pool.obtain();
		
		unit.tile = tile;
		unit.type = type;
		
		unit.size = Math.max(UNIT_SIZE_MAX, size);
		
		unit.action = UnitActions.FREE;
		
		return unit;
	}
	
	
	private int size;
	private Tile tile;
	
	private UnitTypes type;
	private UnitActions action;
	
	/**
	 * Gets the attack value of this unit
	 * @return The ATK
	 */
	public float getAtk() {
		switch (type) {
			case MILITIA: return size * UNIT_ATK_MILITIA;
			case SWORDSMEN: return size * UNIT_ATK_SWORDS;
			case BOWMEN: return size * UNIT_ATK_BOWS;
			case CANNONS: return size * UNIT_ATK_CANNONS;
			default: throw new RuntimeException("Unknown unit type");
		}
	}
	
	/**
	 * Gets the defense value of this unit
	 * @return The DEF
	 */
	public float getDef() {
		switch (type) {
			case MILITIA: return size * UNIT_DEF_MILITIA;
			case SWORDSMEN: return size * UNIT_DEF_SWORDS;
			case BOWMEN: return size * UNIT_DEF_BOWS;
			case CANNONS: return size * UNIT_DEF_CANNONS;
			default: throw new RuntimeException("Unknown unit type");
		}
	}
	
	/**
	 * Moves the unit to a tile
	 * @param destination The tile to move to
	 */
	public void moveTo(Tile destination) {
		tile.exit();
		destination.enter(this);
	}
	
	/**
	 * Runs the step for this unit
	 */
	public void step() {
	
	}
	
	@Override
	public void reset() {
		size = 1;
		
		tile = null;
		type = null;
		action = null;
	}
}
