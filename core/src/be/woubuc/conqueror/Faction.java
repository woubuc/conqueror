package be.woubuc.conqueror;

import be.woubuc.conqueror.focus.Movement;
import be.woubuc.conqueror.focus.Strategy;
import be.woubuc.conqueror.focus.Training;
import be.woubuc.conqueror.map.Tile;
import com.badlogic.gdx.graphics.Color;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Faction {
	
	private final String name;
	private final Color colour;
	private boolean playerControlled;
	
	private Training training = Training.CANNONS;
	private Movement movement = Movement.RETREAT;
	private Strategy strategy = Strategy.REGROUP;
	
	private Set<Tile> ownedTiles = new HashSet<>();
	private Set<Tile> frontLine = new HashSet<>();
	
	Faction(String name, Color colour, boolean playerControlled) {
		this.name = name;
		this.colour = colour;
		this.playerControlled = playerControlled;
	}
	
	public void addTile(Tile t) {
		ownedTiles.add(t);
	}
	
	public boolean isPlayerControlled() {
		return playerControlled;
	}
	
	String getName() {
		return name;
	}
	
	int getScore() {
		return ownedTiles.size();
	}
	
	public Color getColour() {
		return colour;
	}
	
	public Training getTraining() {
		return training;
	}
	
	public void setTraining(Training training) {
		this.training = training;
	}
	
	public Movement getMovement() {
		return movement;
	}
	
	public void setMovement(Movement movement) {
		this.movement = movement;
	}
	
	public Strategy getStrategy() {
		return strategy;
	}
	
	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}
	
	void step() {
		Set<List<Tile>> emptyAdjacentTiles = new HashSet<>();
		ownedTiles.forEach(tile -> {
			// If force is low, increase it
			if (tile.getForce() <= 1) {
				tile.increaseForce();
				return;
			}
			
			int inceaseThreshold = 0;
			if (training == Training.MILITIA) inceaseThreshold = 3;
			if (Game.random.nextInt(10) <= inceaseThreshold) tile.increaseForce();
			
			// Get all empty tiles surrounding this tile
			List<Tile> tiles = tile.getSurrounding();
			tiles.removeIf(Tile::hasOwner);
			if (tiles.size() > 0) emptyAdjacentTiles.add(tiles);
		});
		
		emptyAdjacentTiles.forEach(tiles -> {
			// Randomly select an empty tile and proceed
			int id = 0;
			if (tiles.size() > 1) id = Game.random.nextInt(tiles.size() - 1);
			tiles.get(id).setForce(this, 1);
		});
	}
	
	void turn() {
		if (!playerControlled) {
		
		}
	}
}
