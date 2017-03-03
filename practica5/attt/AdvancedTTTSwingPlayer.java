package es.ucm.fdi.tp.practica5.attt;

import java.util.List;

import es.ucm.fdi.tp.basecode.attt.AdvancedTTTMove;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.GameMove;
import es.ucm.fdi.tp.basecode.bgame.model.GameRules;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

public class AdvancedTTTSwingPlayer extends Player{

	private static final long serialVersionUID = 1L;

	private int row;
	private int col;
	private int rowDest;
	private int colDest;
	
	
	public void setMove1(int row, int col) {
		this.row = row;
		this.col = col;	
	}
	
	public void setMove2(int rowDest, int colDest){
		this.rowDest = rowDest;
		this.colDest= colDest;
	
	}

	public GameMove requestMove(Piece p, Board board, List<Piece> pieces, GameRules rules) {
			return new AdvancedTTTMove(row, col, rowDest, colDest, p);
	}

}
