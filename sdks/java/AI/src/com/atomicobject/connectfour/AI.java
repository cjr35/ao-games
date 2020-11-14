package com.atomicobject.connectfour;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class AI {
	private Random random;
	private int player;
	int[][] board;
	List<Integer> playableColumns;

	public AI() {
		playableColumns = new LinkedList();
		for (int i = 0; i < 7; i++) {
			playableColumns.add(i);
		}
		random = new Random();
	}

	public void setPlayer(int player) {
		this.player = player;
	}

	public int computeMove(GameState state) {
		board = state.getBoard();
		updatePlayableColumns();
		int i = random.nextInt(playableColumns.size());
		return playableColumns.get(i);
	}

	/**
	 * Update the list of playable columns by filtering full columns
	 * A column is full if its corresponding place in the first row is full
	 */
	private void updatePlayableColumns() {
		playableColumns = playableColumns.stream()	// stream over current playable columns
				.filter(col -> board[0][col] == 0)	// retain columns with an empty top slot
				.collect(Collectors.toList());		// replace old value
	}
}
