package be.woubuc.conqueror;

import static be.woubuc.conqueror.Globals.STEPS_PER_TURN;
import static be.woubuc.conqueror.Globals.TIME_PER_STEP;

/**
 * Manages the time in the game
 */
public class Clock {
	
	private float time = TIME_PER_STEP; // Set these to max so we immediately get a first turn
	private int steps = STEPS_PER_TURN;
	
	private Runnable onStep;
	private Runnable onTurn;
	
	void onStep(Runnable onStep) { this.onStep = onStep; }
	void onTurn(Runnable onTurn) { this.onTurn = onTurn; }
	
	/**
	 * Gets the progress until the next turn
	 * @return The progress between 0-1
	 */
	float getProgress() {
		if (steps == 0) return 0;
		return (float) steps / (float) STEPS_PER_TURN;
	}
	
	/**
	 * Updates the clock timer
	 * @param deltaTime Time elapsed since the last call to this method
	 */
	void tick(float deltaTime) {
		time += deltaTime;
		if (time > TIME_PER_STEP) {
			time -= TIME_PER_STEP;
			step();
		}
	}
	
	/**
	 * Updates the step and runs the turn if necessary
	 */
	private void step() {
		if (steps >= STEPS_PER_TURN) {
			steps = 0;
			onTurn.run();
		} else {
			steps++;
			onStep.run();
		}
	}
}
