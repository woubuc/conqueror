package be.woubuc.conqueror.map;

public class TileMap {
	
	public final Tile[][] tiles;
	
	/**
	 * Creates the tilemap
	 * @param size Size of the tilemap
	 */
	public TileMap(int size) {
		tiles = new Tile[size][size];
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				tiles[x][y] = new Tile(x, y);
			}
		}
	}
	
	/**
	 * Gets a tile from the tile map
	 * @param x The X coordinate
	 * @param y The Y coordinate
	 * @return The tile, or null if no tile exists at the given coordinates
	 */
	public Tile getTile(int x, int y) {
		if (x < 0 || y < 0) return null;
		if (x >= tiles.length) return null;
		if (y >= tiles[x].length) return null;
		return tiles[x][y];
	}
}
