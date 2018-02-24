package be.woubuc.conqueror.map;

import java.util.function.Consumer;

/**
 * Holds and manages the tiles on the map
 */
public class TileMap {
	
	private final Tile[][] tiles;
	
	public int playerScore = 1;
	public int enemyOneScore = 1;
	public int enemyTwoScore = 1;
	
	private int size;
	
	public TileMap(int size) {
		this.size = size;
		
		System.out.println("Generating " + size + "x" + size + " tile map");
		tiles = new Tile[size][size];
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				tiles[x][y] = new Tile(x, y);
			}
		}
	}
	
	public Tile getTile(int x, int y) {
		if (x < 0 || y < 0) return null;
		if (x >= tiles.length) return null;
		if (y >= tiles[x].length) return null;
		return tiles[x][y];
	}
	
	public void conquer(int x, int y) {
	
	}
	
	public void each(Consumer<Tile> each) {
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				each.accept(tiles[x][y]);
			}
		}
	}
}
