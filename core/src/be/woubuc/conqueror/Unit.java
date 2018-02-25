package be.woubuc.conqueror;

import be.woubuc.conqueror.focus.Training;

public class Unit {
	
	private int amount;
	private Training type;
	
	public Unit () {
	
	}
	
	public int get() {
		return amount;
	}
	
	public void add(int add) {
		amount += add;
		if (amount > Globals.MAX_UNITS) throw new RuntimeException();
	}
	
	public void remove(int remove) {
		amount -= remove;
		if (amount < 0) throw new RuntimeException();
	}
}
