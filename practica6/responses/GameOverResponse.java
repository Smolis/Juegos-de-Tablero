package es.ucm.fdi.tp.practica6.responses;

import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.Game;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

@SuppressWarnings("serial")
public class GameOverResponse implements Response {
	
	private Board board;
	private Piece turn;
	private Game.State state;
	public GameOverResponse(Board board, Game.State state, Piece turn) {
		this.board = board;
		this.state = state;
		this.turn = turn;
	}


	public void run(GameObserver o) {
		o.onGameOver(board, state, turn);
	}
}
