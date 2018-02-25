package be.woubuc.conqueror.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility methods for working with colours
 */
public class ColourUtils {
	
	// Colour texture cache
	private static final Map<Color, TextureRegion> colourTextures = new HashMap<>();
	
	/**
	 * Creates a colour from the RGB values
	 * @param r Red value between 0-255
	 * @param g Green value between 0-255
	 * @param b Blue value between 0-255
	 * @param a The alpha value between 0-1, defaults to 1
	 * @return The colour
	 */
	private static Color getColour(int r, int g, int b, float a) {
		return new Color(r / 255f, g / 255f, b / 255f, a);
	}
	
	public static Color getColour(int r, int g, int b) {
		return getColour(r, g, b, 1);
	}
	
	/**
	 * Returns a new colour based on the given colour but with a changed alpha value
	 * @param colour The colour
	 * @param a The alpha value between 0-1
	 * @return The new colour
	 */
	public static Color alpha(Color colour, float a) {
		colour = new Color(colour);
		colour.a = a;
		return colour;
	}
	
	/**
	 * Generates a 1x1px texture region from a given colour
	 * @param colour The colour
	 * @return The texture region
	 */
	public static TextureRegion getTexture(Color colour) {
		// Once generated, the textures are cached, so we only need to draw each pixel once
		// and not make a hundred textures with the same colour
		if (colourTextures.containsKey(colour)) return colourTextures.get(colour);
		
		Pixmap colourFill = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		colourFill.setColor(colour);
		colourFill.fill();
		
		TextureRegion texture = new TextureRegion(new Texture(colourFill));
		colourFill.dispose(); // Dispose the pixmap once we have generated the texture
		
		colourTextures.put(colour, texture);
		return texture;
	}
	
}
