package es.ucm.fdi.tp.practica6.responses;

import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

@SuppressWarnings("serial")
public class MoveEndResponse implements Response {
	private Board board;
	private Piece turn;
	private boolean gameOver;
	public MoveEndResponse(Board board, Piece turn, boolean gameOver) {
		this.board = board;
		this.turn = turn;
		this.gameOver = gameOver;

	}

	public void run(GameObserver o) {
		o.onMoveEnd(board,turn, gameOver);
	}
}
