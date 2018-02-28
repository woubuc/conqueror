package be.woubuc.conqueror.map;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.PathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

public class TileMap implements IndexedGraph {
	
	public final List<Tile> tiles;
	private final PathFinder pathFinder;
	private final int size;
	
	/**
	 * Creates the tilemap
	 * @param size Size of the tilemap
	 */
	@SuppressWarnings("unchecked")
	public TileMap(int size) {
		this.size = size;
		
		tiles = new ArrayList<>(size * size);
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				int i = (x * size) + y;
				tiles.add(i, new Tile(this, x, y));
			}
		}
		
		for (Tile t : tiles) {
			t.init();
		}
		
		pathFinder = new IndexedAStarPathFinder(this);
	}
	
	/**
	 * Gets a tile from the tile map
	 * @param x The X coordinate
	 * @param y The Y coordinate
	 * @return The tile, or null if no tile exists at the given coordinates
	 */
	public Tile getTile(int x, int y) {
		if (x < 0 || y < 0) return null;
		if (x > size || y > size) return null;
		
		int i = (x * size) + y;
		if (i >= tiles.size()) return null;
		return tiles.get(i);
	}
	
	@Override
	public int getIndex(Object node) {
		if (!(node instanceof Tile)) throw new RuntimeException("Cannot get index of non-tile object in tile map");
		return tiles.indexOf(node);
	}
	
	@Override
	public int getNodeCount() {
		return tiles.size();
	}
	
	@Override
	public Array<Connection> getConnections(Object fromNode) {
		if (!(fromNode instanceof Tile)) throw new RuntimeException("Cannot get connections of non-tile object in tile map");
		return ((Tile) fromNode).getConnections();
	}
}
