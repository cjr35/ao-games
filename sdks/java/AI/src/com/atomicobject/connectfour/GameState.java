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
		createKeyIfAbsent();
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
		createKeyIfAbsent();
		String nextKey = key.substring(0, keyIndex) + player + key.substring(keyIndex + 1);
		next.setKey(nextKey);

		int[][] nextBoard = new int[board.length][board[0].length];
		for (int i = 0; i < board.length; i++) {
			nextBoard[i] = Arrays.copyOf(board[i], 7);
		}
		nextBoard[row][move] = player;
		next.setBoard(nextBoard);

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
		return isWin(1) || isWin(2) || isDraw();
	}

	public int evaluate(int player) {
		if (isWin(player)) {
			return 100;
		}
		else if (isLoss(player)) {
			return -100;
		}
		else {
			return 0;
		}
	}

	private boolean isWin(int player) {
		return findShapes(player, winShapes);
	}

	private boolean isLoss(int player) {
		int opponent = player == 1 ? 2 : 1;
		return findShapes(opponent, winShapes);
	}

	private boolean isDraw() {
		for (int i : board[0]) {
			if (i == 0) {
				return false; // if any top space is available
			}
		}
		return true;
	}

	private static int[][][] winShapes = {
			{	{ 0, 0 },
				{ 1, 0 },
				{ 2, 0 },
				{ 3, 0 }	},	// vertical		|

			{	{ 0, 0 },
				{ 1, 1 },
				{ 2, 2 },
				{ 3, 3 }	},	// diagonal		\

			{	{ 0, 0 },
				{ 0, 1 },
				{ 0, 2 },
				{ 0, 3 }	},	// across		-

			{	{ 0, 0 },
				{ 1, -1 },
				{ 2, -2 },
				{ 3, -3 }	},	// diagonal		/
	};

	private boolean findShapes(int player, int[][][] shapeList) {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				for (int[][] shape : shapeList) {
					if (findShapeAt(i, j, shape, player)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean findShapeAt(int row, int col, int[][] shape, int player) {
		for (int[] coordinate : shape) {
			int rOffset = row + coordinate[0];
			int cOffset = col + coordinate[1];

			try {
				int cell = board[rOffset][cOffset];
				if (cell != player) {
					return false; // if any cell in the shape is the wrong color
				}
			}
			catch (IndexOutOfBoundsException ioobx) {
				return false;
			}
		}
		return true; // if all cells in the shape were player's color
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
