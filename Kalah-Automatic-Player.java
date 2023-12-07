package gj.kalah.player.sirgiovanni;

import java.util.ArrayList;

import gj.kalah.player.Player;

public class SirgiovanniPlayer implements Player {

	private int[][] board;
	private int side;

	public void start(boolean isFirst) {
		if (isFirst) {
			this.side = 1;
		} else {
			this.side = 0;
		}
	}

	public int move() {
		endGame();
		int move = -1;
		int j = safeOrCatch(side);
		int i = moveToMancala();
		if (j == controlEnemyMoves((side - 1) * (-1))) {
			if (i != -1) {
				move = i;
			} else if (j != -1) {
				move = j;
			}
		}
		if (move == -1) {
			if (j != -1 && i != -1) {
				if (i > 5 - (reduce((j + board[side][j]))[1]) && 5 - (reduce((j + board[side][j]))[1]) > j) {
					move = i;
				} else if (5 - (reduce((j + board[side][j]))[1]) > i) {
					move = j;
				} else {
					move = i;
				}
			} else if (j != -1 && i == -1) {
				move = j;
			} else if (controlEnemyMancala(side) != -1) {
				move = controlEnemyMancala(side);
			} else {
				move = moveMajorConch(side);
			}
		}
		updateBoard(move, side);
		return move;

	}

	public void tellMove(int m) {
		endGame();
		updateEnemy(m, (side - 1) * (-1));
	}

	// Constructor
	public SirgiovanniPlayer() {
		this.board = createBoard();
		updateDefaultBoard();
	}

	public int[][] createBoard() {
		int[][] board = new int[2][7];
		return board;
	}

	public boolean itsMancala(int line, int column) {
		boolean mancala = false;
		if ((line == 0 && column == 6) || ((line == 1 && column == 6))) {
			mancala = true;
		}
		return mancala;
	}

	////////// Update Methods////////////

	public void updateDefaultBoard() {
		int columns = 0;
		int lines = 0;
		while (lines < 2) {
			while (columns < 7) {
				if (!itsMancala(lines, columns)) {
					board[lines][columns] = 4;
					columns = columns + 1;
				} else {
					board[lines][columns] = 0;
					columns = columns + 1;
				}
			}
			columns = 0;
			lines = lines + 1;
		}
	}

	public void updateBoard(int myMove, int line) {
		int i = myMove + 1;
		int stones = board[line][myMove];
		board[line][myMove] = 0;
		updateBoardWithStones(stones, line, i);
		if (5 - reduce(myMove + stones)[1] < 6 && 5 - reduce(myMove + stones)[1] > -1) {
			updateCatch((5 - reduce(myMove + stones)[1]), line);
		}
	}

	public void updateBoardWithStones(int stones, int line, int i) {
		int conch = 0;
		if (line != side) {
			conch = 5;
		} else {
			conch = 6;
		}
		while (stones > 0 && i <= conch) {
			board[line][i] = board[line][i] + 1;
			i = i + 1;
			stones = stones - 1;
		}
		if (stones > 0) {
			i = 0;
			line = (line - 1) * (-1);
			updateBoardWithStones(stones, line, i);
		}
	}

	public void updateEnemy(int myMove, int line) {
		int i = myMove + 1;
		int stones = board[line][myMove];
		board[line][myMove] = 0;
		updateEnemyBoard(stones, line, i);
		if (5 - reduce(myMove + stones)[1] < 6 && 5 - reduce(myMove + stones)[1] > -1) {
			updateCatch(5 - reduce(myMove + stones)[1], line);
		}
	}

	public void updateEnemyBoard(int stones, int line, int i) {
		int conch = 0;
		if (line != this.side) {
			conch = 6;
		} else {
			conch = 5;
		}
		while (stones > 0 && i <= conch) {
			board[line][i] = board[line][i] + 1;
			i = i + 1;
			stones = stones - 1;

		}
		if (stones > 0) {
			i = 0;
			line = (line - 1) * (-1);
			updateEnemyBoard(stones, line, i);
		}
	}

	public void updateCatch(int i, int line) {
		if (board[line][i] == 1) {
			board[line][i] = 0;
			board[(line - 1) * (-1)][5 - i] = 0;
		}
	}

	public void endGame() {
		if (checkEndGame1() || checkEndGame0()) {
			updateDefaultBoard();
		}
	}

	public boolean checkEndGame1() {
		boolean endGame1 = true;
		int i = 0;
		while (i < 6) {
			if (board[1][i] != 0) {
				endGame1 = false;
				break;
			}
			i = i + 1;
		}
		return endGame1;
	}

	public boolean checkEndGame0() {
		boolean endGame0 = true;
		int i = 0;
		while (i < 6) {
			if (board[0][i] != 0) {
				endGame0 = false;
				break;
			}
			i = i + 1;
		}
		return endGame0;
	}

	///////// Moves Methods//////////////
	public boolean checkZeroes(int line) {
		boolean thereAreZeroes = false;
		int i = 0;
		while (i < 6) {
			if (board[line][i] == 0) {
				thereAreZeroes = true;
				break;
			}
			i = i + 1;
		}
		return thereAreZeroes;
	}

	public ArrayList<Integer> zeroesPosition(int line) {
		ArrayList<Integer> position = new ArrayList<>();
		int i = 0;
		while (i < 6) {
			if (board[line][i] == 0) {
				position.add(i);
			}
			i = i + 1;
		}
		return position;
	}

	public int[] indexConvenientZeroMinor(int line) {
		int[] max = new int[2];
		max[0] = -1;
		for (int i : reachZeroFromMinorIndex(line)) {
			if (board[(line - 1) * (-1)][5 - (i + board[line][i])] > max[1]) {
				max[1] = board[(line - 1) * (-1)][5 - (i + board[line][i])];
				max[0] = i;
			}
		}
		return max;
	}

	public int[] indexConvenientZeroMajor(int line) {
		int[] max = new int[2];
		max[0] = -1;
		for (int i : reachZeroFromMajorIndex(line)) {
			if (board[(line - 1) * (-1)][5 - (i - (13 - board[line][i]))] > max[1]) {
				max[1] = board[(line - 1) * (-1)][5 - (i - (13 - board[line][i]))] + 1;
				max[0] = i;
			}
		}
		return max;
	}

	public int indexConvenientZero(int line) {
		int c;
		int[] a = indexConvenientZeroMinor(line);
		int[] b = indexConvenientZeroMajor(line);
		if (a[1] > b[1]) {
			c = a[0];
		} else {
			c = b[0];
		}
		return c;
	}

	public ArrayList<Integer> reachZeroFromMinorIndex(int line) {
		ArrayList<Integer> reachZero = new ArrayList<>();
		if (checkZeroes(line)) {
			for (int i : zeroesPosition(line)) {
				if (i != 0) {
					int j = i - 1;
					while (j >= 0) {
						if (board[line][j] == i - j) {
							reachZero.add(j);
						}
						j = j - 1;
					}
				}
			}

		}
		return reachZero;
	}

	public ArrayList<Integer> reachZeroFromMajorIndex(int line) {
		ArrayList<Integer> reachZero = new ArrayList<>();
		if (checkZeroes(line)) {
			for (int i : zeroesPosition(line)) {
				int j = i + 1;
				while (j <= 5) {
					if (board[line][j] == 13 - (j - i)) {
						reachZero.add(j);
					}
					j = j + 1;
				}
			}
		}
		return reachZero;
	}

	public int safeOrCatch(int line) {
		int myMove = -1;
		int numberCatchStones = -1;
		int numberSafeStones = -1;
		int safeStones = moveToZeroes((line - 1) * (-1));
		int catchStones = moveToZeroes(line);
		if (catchStones != -1) {
			int j = catchStones + board[line][catchStones];
			numberCatchStones = orCatch(j, line);
		}
		if (safeStones != -1) {

			numberSafeStones = board[line][controlEnemyMoves((line - 1) * (-1))];
		}
		if (numberCatchStones >= numberSafeStones) {
			myMove = catchStones;
		} else {
			myMove = controlEnemyMoves((line - 1) * (-1));
		}
		return myMove;
	}

	public int orCatch(int j, int line) {
		int numberCatchStones = -1;
		if (reduce(j)[0] == line) {
			numberCatchStones = board[line][reduce(j)[1]];
		} else {
			numberCatchStones = board[(line - 1) * (-1)][reduce(j)[1]];
		}
		return numberCatchStones;

	}

	public int[] reduce(int i) {
		int[] f = new int[2];
		if (i > 26) {
			f[1] = 5 - (13 - (39 - i));
			f[0] = -1;
		} else if (i > 13) {
			f[1] = 5 - (13 - (26 - i));
		} else if (i > 5) {
			f[1] = 5 - (13 - i);
			f[0] = -1;
		} else {
			f[1] = 5 - i;
		}
		return f;
	}

	public int moveToZeroes(int line) {
		int i = -1;
		if (!reachZeroFromMinorIndex(line).isEmpty() || !reachZeroFromMajorIndex(line).isEmpty()) {
			i = indexConvenientZero(line);
		}
		return i;
	}

	public int moveMajorConch(int line) {
		int i = 5;
		while (i >= 0) {
			if (board[line][i] != 0) {
				break;
			}
			i = i - 1;
		}
		return i;
	}

	public ArrayList<Integer> reachMancala(int line) {
		ArrayList<Integer> reachMancala = new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			if (board[line][i] + i == 6) {
				reachMancala.add(i);
			}
		}
		return reachMancala;
	}

	public int moveToMancala() {
		int j = -1;
		if (!reachMancala(side).isEmpty()) {
			j = reachMancala(side).remove(reachMancala(side).size() - 1);
		}
		return j;
	}

	////////////// Enemy Moves//////////////
	public int controlEnemyMoves(int line) {
		int myMove = -1;
		int enemyMove;
		if (!reachZeroFromMinorIndex(line).isEmpty() || !reachZeroFromMajorIndex(line).isEmpty()) {
			enemyMove = indexConvenientZero(line);
			if (enemyMove != -1) {
				myMove = safeStones(enemyMove);
			}
		}
		return myMove;
	}

	public int safeStones(int enemyMove) {
		int n = board[(side - 1) * (-1)][enemyMove];
		int u = enemyMove + n;
		int safeZone = reduce(u)[1];
		return safeZone;
	}

	public int controlEnemyMancala(int line) {
		int myMove = -1;
		int enemyMove;
		if (!reachMancala(line).isEmpty()) {
			enemyMove = reachMancala(line).remove(reachMancala(line).size() - 1);
			myMove = reachEnemy(enemyMove);
		}
		return myMove;

	}

	public int reachEnemy(int enemyMove) {
		int myMove = -1;
		int i = 0;
		while (i < 6 && myMove == -1) {
			if (board[side][i] >= (5 - i) + enemyMove) {
				myMove = i;
			}
			i = i + 1;
		}
		return myMove;
	}

}
