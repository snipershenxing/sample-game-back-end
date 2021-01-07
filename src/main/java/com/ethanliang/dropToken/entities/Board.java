package com.ethanliang.dropToken.entities;

import com.ethanliang.dropToken.exceptions.ApiException;

public class Board {

	enum Marker { RED, BLUE, BLANK }

	public Marker[][] board;

	private final String player1;
	private String curPlayer = "";
	private int round;

	//initialize game board
	public Board(String player1) {
		this.board = new Marker[4][4];
		this.player1 = player1;
		this.round = 1;
		for(int r = 0;  r < 4;  ++r ) {
			for(int c = 0;  c < 4;  ++c) {
				board[r][c] = Marker.BLANK;
			}
		}
	}

	//set move into column
	public void markAt(int col) {
		col -= 1;
		if (board[0][col] != Marker.BLANK) {
			throw new ApiException.IllegalMoveException("Malformed input. Illegal move");
		} else {
			for (int i = 3; i >= 0; i--) {
				if (board[i][col] == Marker.BLANK) {
					if (curPlayer.equals(player1)) {
						board[i][col] = Marker.RED;
					} else {
						board[i][col] = Marker.BLUE;
					}
					round++;
					break;
				}
			}
		}
	}

	//check if there is a winner
	public boolean checkWin() {
		Marker flag;
		//x, y
		for (int i = 0; i < 4; i++) {
			flag = board[i][i];
			if (flag == Marker.BLANK) {
				continue;
			}
			boolean xFull = true;
			boolean yFull = true;
			//x dir
			for (int j = 0; j < 4; j++) {
				if (board[i][j] != flag || board[i][j] == Marker.BLANK) {
					xFull = false;
				}
				if (board[j][i] != flag || board[j][i] == Marker.BLANK) {
					yFull = false;
				}
			}
			if (xFull || yFull) {
				return true;
			}
		}
		//diagnol 1
		boolean diagonal1 = true;
		boolean diagonal2 = true;
		flag = board[0][0];
		if (board[0][0] == Marker.BLANK) {
			diagonal1 = false;
		} else {
			for (int i = 3; i >= 0; i--) {
				if (board[i][i] != flag) {
					diagonal1 = false;
					break;
				}
			}
		}

		//diagnol 2
		flag = board[3][0];
		if (board[3][0] == Marker.BLANK) {
			diagonal2 = false;
		} else {
			for (int i = 3; i >= 0; i--) {
				if (board[i][3 - i] != flag) {
					diagonal2 = false;
					break;
				}
			}
		}

		return diagonal1 || diagonal2;
	}

	public String getWinner() {
		return curPlayer;
	}

	public boolean isDraw() {
		// If all squares are filled, and a winner not declared, it's a draw pure and simple
		return round == 16 && !checkWin();
	}

	public void setCurPlayer(String curPlayer) {
		if (this.curPlayer.equals(curPlayer)) {
			throw new ApiException.WrongTurnException("Player tried to post when it's not their turn");
		}
		this.curPlayer = curPlayer;
	}
}
