package be.woubuc.conqueror;

import be.woubuc.conqueror.util.ColourUtils;
import com.badlogic.gdx.graphics.Color;

/**
 * Contains the "magic numbers", configuration variables that make the game work
 */
public class Globals {
	
	// The three colours
	static final Color COLOUR_PLAYER = ColourUtils.getColour(79, 164, 247);
	static final Color COLOUR_ENEMY_ONE = ColourUtils.getColour(56, 184, 110);
	static final Color COLOUR_ENEMY_TWO = ColourUtils.getColour(238, 123, 88);
	
	// Interface colours
	static final Color COLOUR_BACKGROUND = ColourUtils.getColour(244, 244, 244);
	static final Color COLOUR_PANEL = ColourUtils.getColour(85, 113, 133);
	static final Color COLOUR_PANEL_ACTIVE = ColourUtils.getColour(50, 64, 86);
	
	// Game settings
	public static final int MAX_FORCE = 20;
	
	// Map settings
	static final int MAP_SIZE = 35;
	static final int TILE_SIZE = 12;
	
	// Clock settings
	static final float TIME_PER_STEP = 0.25f;
	static final int STEPS_PER_TURN = 25;
}
