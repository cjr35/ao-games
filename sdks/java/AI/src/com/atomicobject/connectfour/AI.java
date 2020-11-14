package com.atomicobject.connectfour;

import java.util.Random;

public class AI {
	private Random random;

	public AI() {
		random = new Random();
	}

	public int computeMove(GameState state) {
		System.out.println("AI returning random move for game state - " + state);
		return random.nextInt(7);
	}
}
