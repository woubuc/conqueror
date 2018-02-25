package be.woubuc.conqueror;

import be.woubuc.conqueror.util.ColourUtils;
import com.badlogic.gdx.graphics.Color;

/**
 * Contains the "magic numbers", configuration variables that make the game work
 */
public final class Globals {
	
	// The three colours
	static final Color COLOUR_PLAYER = ColourUtils.getColour(79, 164, 247);
	static final Color COLOUR_ENEMY_ONE = ColourUtils.getColour(56, 184, 110);
	static final Color COLOUR_ENEMY_TWO = ColourUtils.getColour(238, 123, 88);
	
	// Interface colours
	public static final Color COLOUR_BACKGROUND = ColourUtils.getColour(244, 244, 244);
	public static final Color COLOUR_PANEL = ColourUtils.getColour(85, 113, 133);
	public static final Color COLOUR_PANEL_ACTIVE = ColourUtils.getColour(50, 64, 86);
	
	// Game & Balance settings
	public static final float UNIT_ATK_SWORDS = 3.2f;
	public static final float UNIT_DEF_SWORDS = 2.2f;
	
	public static final float UNIT_ATK_BOWS = 4.6f;
	public static final float UNIT_DEF_BOWS = 2f;
	
	public static final float UNIT_ATK_CANNONS = 7.2f;
	public static final float UNIT_DEF_CANNONS = 9.5f;
	
	public static final float UNIT_ATK_MILITIA = 1f;
	public static final float UNIT_DEF_MILITIA = 1.4f;
	
	public static final int MAX_UNITS = 20;
	
	public static final int MIN_UNITS = 2;
	public static final int MIN_UNITS_FRONTLINE = 8;
	public static final float MIN_UNITS_DEFEND_MULTIPLIER = 1.25f;
	public static final float MIN_UNITS_FORTIFY_MULTIPLIER = 1.6f;
	
	public static final float UNIT_INCREASE_MIN = 2;
	public static final float UNIT_INCREASE_MAX = 36;
	public static final float UNIT_INCREASE_PER_TURN = 0.1f;
	
	public static final float UNIT_INCREASE_BOWS_MULTIPLIER = 0.9f;
	public static final float UNIT_INCREASE_CANNONS_MULTIPLIER = 0.35f;
	public static final float UNIT_INCREASE_MILITIA_MULTIPLIER = 2.6f;
	
	// Map settings
	public static final int MAP_SIZE = 20;
	public static final int TILE_SIZE = 36;
	
	// Clock settings
	public static final float TIME_PER_STEP = 0.6f;
	public static final int STEPS_PER_TURN = 16;
}
