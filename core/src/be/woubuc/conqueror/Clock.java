package be.woubuc.conqueror;

import java.util.List;

import static be.woubuc.conqueror.Globals.STEPS_PER_TURN;
import static be.woubuc.conqueror.Globals.TIME_PER_STEP;

/**
 * Manages the time in the game
 */
public class Clock {
	
	private float time = TIME_PER_STEP; // Set these to max so we immediately get a first turn
	private int steps = STEPS_PER_TURN;
	
	private boolean firstStep = true;
	private int lastChoice = 0;
	
	/**
	 * Gets the progress until the next turn
	 * @return The progress between 0-1
	 */
	public float getProgress() {
		if (steps == 0) return 0;
		return (float) steps / (float) STEPS_PER_TURN;
	}
	
	/**
	 * Updates the clock timer
	 * @param deltaTime Time elapsed since the last call to this method
	 * @return True if a turn occured
	 */
	public boolean tick(float deltaTime) {
		System.out.println(System.currentTimeMillis() + " Tick");
		time += deltaTime;
		if (time > TIME_PER_STEP) {
			time -= TIME_PER_STEP;
			return step();
		}
		
		return false;
	}
	
	/**
	 * Updates the step and runs the turn if necessary
	 * @return True if a turn occured
	 */
	private boolean step() {
		if (steps >= STEPS_PER_TURN) {
			steps = 0;
			onTurn();
			return true;
		} else {
			steps++;
			onStep();
			return false;
		}
	}
	
	private void onStep() {
		List<Faction> factions = Game.get().factions;
		
		factions.forEach(Faction::pullToFrontline);
		factions.forEach(Faction::attackEnemies);
		
		factions.forEach(Faction::pullToFrontline);
		factions.forEach(Faction::equaliseUnits);
		
		factions.forEach(Faction::recruitUnits);
		factions.forEach(Faction::expandTerritory);
	}
	
	private void onTurn() {
		Game game = Game.get();
		List<Faction> factions = game.factions;
		
		factions.forEach(Faction::turn);
		
		int choice = lastChoice;
		if (firstStep) {
			choice = 0;
			firstStep = false;
		} else {
			while (choice == lastChoice) {
				choice = Game.random.nextInt(3);
			}
		}
		
		game.setScreen(game.choiceScreen);
		
		switch(choice) {
			case 0: game.choiceScreen.choices.createTrainingChoice(); break;
			case 1: game.choiceScreen.choices.createStrategyChoice(); break;
			default: game.choiceScreen.choices.createMovementChoice(); break;
		}
		
		lastChoice = choice;
	}
}
