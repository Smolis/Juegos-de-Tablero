package es.ucm.fdi.tp.practica4.ataxx;

import java.util.List;

import es.ucm.fdi.tp.basecode.bgame.Utils;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameMove;
import es.ucm.fdi.tp.basecode.bgame.model.GameRules;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

public class ATAXXRandomPlayer extends Player {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public GameMove requestMove(Piece p, Board board, List<Piece> pieces,
			GameRules rules) {
		GameMove mov = null;
		Piece pieceOrigin;
		if (board.isFull()) {
			throw new GameError("The board is full, cannot make random move!");
		}
		int rows = board.getRows();
		int cols = board.getCols();
		boolean valido = false, move = false;
		int currRow;
		int currCol;
		int rowDest;
		int colDest;

		currRow = Utils.randomInt(rows);
		currCol = Utils.randomInt(cols);

		pieceOrigin = board.getPosition(currRow, currCol);

		while (!move) {

			while ((valido != true)) {
				if ((pieceOrigin != null) && (!pieceOrigin.equals(pieces))) {
					valido = true;

				} else {
					currCol = (currCol + 1) % cols;
					if (currCol == 0) {
						currRow = (currRow + 1) % rows;
					}
					valido = false;
					pieceOrigin = board.getPosition(currRow, currCol);

				}
			}

			rowDest = setRandomDestinationRow(rows, currRow);
			colDest = setRandomDestinationCol(cols, currCol);

			if ((pieceOrigin != null) && (pieceOrigin.equals(p))) {
				if ((board.getPosition(rowDest, colDest) == null)) {
					if (!rules.validMoves(board, pieces, p).isEmpty())
						move = true;
					mov = createMove(currRow, currCol, rowDest, colDest, p);
				}
			}

			currRow = Utils.randomInt(rows);
			currCol = Utils.randomInt(cols);

			pieceOrigin = board.getPosition(currRow, currCol);
		}
		return mov;
	}

	protected int setRandomDestinationRow(int rows, int currRow) {
		int rowDest = -1;
		while ((rowDest < 0) || (rowDest >= rows) || (rowDest > currRow + 2)
				|| (rowDest < currRow - 2)) {

			rowDest = (int) Math.abs(Math.floor(Math.random() * (currRow + 3)));
		}
		return rowDest;
	}

	protected int setRandomDestinationCol(int cols, int currCol) {
		int colDest = -1;
		while ((colDest < 0) || (colDest >= cols) || (colDest > currCol + 2)
				|| (colDest < currCol - 2)) {

			colDest = (int) Math.abs(Math.floor(Math.random() * (currCol + 3)));
		}
		return colDest;
	}

	/**
	 * Creates the actual move to be returned by the player. Separating this
	 * method from {@link #requestMove(Piece, Board, List, GameRules)} allows us
	 * to reuse it for other similar games by overriding this method.
	 * 
	 * <p>
	 * Crea el movimiento concreto que sera devuelto por el jugador. Se separa
	 * este metodo de {@link #requestMove(Piece, Board, List, GameRules)} para
	 * permitir la reutilizacion de esta clase en otros juegos similares,
	 * sobrescribiendo este metodo.
	 * 
	 * @param row
	 *            origin row number.
	 * @param col
	 *            origin column number.
	 * @param rowDest
	 *            destination row number.
	 * @param colDest
	 *            destination column number.
	 * @param p
	 *            Piece ({@code row},{@code col}) to place at ({@code rowDest},
	 *            {@code colDest}).
	 * @return
	 */
	protected GameMove createMove(int row, int col, int rowDest, int colDest,
			Piece p) {
		return new ATAXXMove(row, col, rowDest, colDest, p);
	}

}
