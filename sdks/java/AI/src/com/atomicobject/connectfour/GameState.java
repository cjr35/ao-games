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

	public float evaluate(int player) {
		if (isWin(player)) {
			return 100.0f;
		}
		else if (isLoss(player)) {
			return -100.0f;
		}
		else {
			return 0.0f;
		}
//		float evaluateShapes(player, winShapes, true);
	}

	private boolean isWin(int player) {
		return evaluateShapes(player, winShapes, true) == 1.0f;
	}

	private boolean isLoss(int player) {
		int opponent = player == 1 ? 2 : 1;
		return evaluateShapes(opponent, winShapes, true) == 1.0f;
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
			{	{ 0, 0, 1 },
				{ 1, 0, 1 },
				{ 2, 0, 1 },
				{ 3, 0, 1 }	},	// vertical		|

			{	{ 0, 0, 1 },
				{ 1, 1, 1 },
				{ 2, 2, 1 },
				{ 3, 3, 1 }	},	// diagonal		\

			{	{ 0, 0, 1 },
				{ 0, 1, 1 },
				{ 0, 2, 1 },
				{ 0, 3, 1 }	},	// across		-

			{	{ 0, 0, 1 },
				{ 1, -1, 1 },
				{ 2, -2, 1 },
				{ 3, -3, 1 }	},	// diagonal		/
	};

	private static int[][][] sevenShapes = {
			{	{ 0, 0, 1 },	//
				{ 0, 1, 1 },	//	(@)(@)(@)( )
				{ 0, 2, 1 },	//	   (@)
				{ 0, 3, 0 },	//	(@)
				{ 1, 1, 1 },	//
				{ 2, 0, 1 } },	//

			{	{ 0, 0, 0 },	//
				{ 0, 1, 1 },	//	( )(@)(@)(@)
				{ 0, 2, 1 },	//	      (@)
				{ 0, 3, 1 },	//           (@)
				{ 1, 2, 1 },	//
				{ 2, 3, 1 } },	//

			{	{ 0, 0, 1 },	//
				{ 1, 1, 1 },	//	(@)
				{ 2, 0, 1 },	//	   (@)
				{ 2, 1, 1 },	//	(@)(@)(@)
				{ 2, 2, 1 },	//	         ( )
				{ 3, 3, 0 } },	//

			{	{ 0, 3, 1 },	//
				{ 1, 2, 1 },	//	         (@)
				{ 2, 1, 1 },	//	      (@)
				{ 2, 2, 1 },	//	   (@)(@)(@)
				{ 2, 3, 1 },	//	( )
				{ 0, 3, 0 } },	//
	};

	private float evaluateShapes(int player, int[][][] shapeList, boolean shortCircuit) {
		float totalEvaluation = 0.0f;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				for (int[][] shape : shapeList) {
					float shapeCompletion = evaluateShapeAt(i, j, player, shape, shortCircuit);
					if (shortCircuit && shapeCompletion == 1.0f) {
						return 1.0f;
					}
					else {
						totalEvaluation += shapeCompletion;
					}
				}
			}
		}
		return totalEvaluation / (float) (board.length * board[0].length);
	}

	private float evaluateShapeAt(int row, int col, int player, int[][] shape, boolean strict) {
		int size = shape.length;
		int cellsFound = 0;
		for (int[] coordinate : shape) {
			int rOffset = row + coordinate[0];
			int cOffset = col + coordinate[1];

			try {
				int cell = board[rOffset][cOffset];
				if (cell == player) {
					// record finding one correct cell
					cellsFound++;
				}
				else if (cell != 0 || strict) {
					// either a cell was the wrong color or the user wants only complete shapes
					return 0.0f;
				}
			}
			catch (IndexOutOfBoundsException ioobx) {
				// if the shape would extend out of the board, it can't be completed
				return 0.0f;
			}
		}
		// return the percent completion of the shape
		return (float) cellsFound / (float) size;
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
