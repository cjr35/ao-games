package com.atomicobject.connectfour;

import java.util.Arrays;
import java.util.LinkedList;

public class GameState {

	private int player;
	private int[][] board;
	private int maxTurnTime;
	private String key;

	public GameState() {
	}

	public int getPlayer() {
		return player;
	}

	public void setPlayer(int player) {
		this.player = player;
	}

	public int[][] getBoard() {
		return board;
	}

	public void setBoard(int[][] board) {
		this.board = board;
		createKeyIfAbsent();
	}

	public void updateBoard(int row, int col, int player) {
		board[row][col] = player;
	}

	private void createKeyIfAbsent() {
		if (key == null) {
			key = "";
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board[0].length; j++) {
					key += String.valueOf(board[i][j]);
				}
			}
		}
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public int getMaxTurnTime() {
		return maxTurnTime;
	}

	public void setMaxTurnTime(int maxTurnTime) {
		this.maxTurnTime = maxTurnTime;
	}

	public GameState simulate(int move) {
		int row = getDepth(move);
		if (row == -1) {
			throw new IllegalArgumentException();
		}
		GameState next = new GameState();

		int keyIndex = row * 7 + move;
		String nextKey = key.substring(0, keyIndex) + String.valueOf(player) + key.substring(keyIndex + 1);
		next.setKey(nextKey);

		next.setBoard(board);
		next.updateBoard(player, row, move);

		next.setPlayer(player == 1 ? 2 : 1);
		next.setMaxTurnTime(maxTurnTime);

		return next;
	}

	private int getDepth(int col) {
		int row = -1;

		try {
			while (board[row + 1][col] == 0) {
				row++;
			}
		}
		catch (IndexOutOfBoundsException ioobx) {
			// we have reached the bottom of the column
		}

		return row;
	}

	public boolean[] getMoveMatrix() {
		boolean[] moveMatrix = new boolean[board[0].length];
		for (int i = 0; i < board[0].length; i++) {
			moveMatrix[i] = board[0][i] == 0;
		}
		return moveMatrix;
	}

	public boolean isTerminal() {
		for (int i : board[0]) {
			if (i == 0) {
				return false;
			}
		}
		return true;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Player: ");
		builder.append(player);
		builder.append(" board: ");
		builder.append("[");
		for (int i = 0; i < board.length; i++) {
			builder.append(Arrays.toString(board[i]));
			if (i < board.length - 1) builder.append(",");
		}
		builder.append("]");
		builder.append(" maxTurnTime: ");
		builder.append(maxTurnTime);
		return builder.toString();
	}

}
