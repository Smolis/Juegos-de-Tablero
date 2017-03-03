package es.ucm.fdi.tp.practica4.ataxx;

import java.util.List;

import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameMove;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

/**
 * A Class representing a move for ConnectN.
 * 
 * <p>
 * Clase para representar un movimiento del juego attx.
 * 
 */
public class ATAXXMove extends GameMove {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int row;
	protected int col;
	protected int rowDest;
	protected int colDest;

	public ATAXXMove() {
	}

	/**
	 * Constructs a move for placing a piece of the type referenced by {@code p}
	 * and with the position ({@code row},{@code col}) at position (
	 * {@code rowDest},{@code colDest}).
	 * 
	 * <p>
	 * Construye un movimiento para colocar una ficha del tipo referenciado por
	 * {@code p} situada en ({@code row},{@code col}) en la posicion (
	 * {@code rowDest},{@code colDest}).
	 * 
	 * @param row
	 *            Current number of row.
	 *            <p>
	 *            Numero de fila actual.
	 * @param col
	 *            Current number of column.
	 *            <p>
	 *            Numero de columna actual.
	 * @param p
	 *            A piece to be place at ({@code row},{@code col}).
	 *            <p>
	 *            Ficha a colocar en ({@code row},{@code col}).
	 * @param rowDest
	 *            Next number of row.
	 *            <p>
	 *            Numero de fila siguiente.
	 * @param colDest
	 *            Next number of col.
	 *            <p>
	 *            Numero de columna siguiente.
	 */
	public ATAXXMove(int row, int col, int rowDest, int colDest, Piece p) {
		super(p);
		this.row = row;
		this.col = col;
		this.rowDest = rowDest;
		this.colDest = colDest;

	}

	@Override
	public void execute(Board board, List<Piece> pieces) {
		int absRows, absCols, max, count;

		absRows = Math.abs(this.row - this.rowDest);
		absCols = Math.abs(this.col - this.colDest);
		max = Math.max(absRows, absCols);
		count = board.getPieceCount(getPiece());

		if (!board.getPosition(row, col).equals(getPiece())) {
			throw new GameError("The piece of the position (" + row + "," + col
					+ ") is not yours!!");

		}
		if (board.getPosition(row, col) == null) {
			throw new GameError("the position (" + row + "," + col
					+ ") that you are trying to move is empty!");
		} else if (board.getPosition(this.rowDest, this.colDest) != null) {
			throw new GameError("position (" + rowDest + "," + colDest
					+ ") is already occupied!");
		} else if (max > 2) {
			throw new GameError(
					"Move not possible! (You tried to move 3 or more positions) ");

		} else if (max == 1) {
			// Crea nueva pieza en la posicion de destino y mantiene la
			// anterior
			board.setPosition(this.rowDest, this.colDest, getPiece());
			board.setPieceCount(getPiece(), count + 1);
			switchCel(this.rowDest, this.colDest, board, pieces);

		} else if (max == 2) {
			// Creo nueva pieza en la posicion de destino
			board.setPosition(this.rowDest, this.colDest, getPiece());
			// Elimino la pieza de la posicion anterior
			board.setPosition(this.row, this.col, null);
			switchCel(this.rowDest, this.colDest, board, pieces);
		}
	}

	@Override
	public GameMove fromString(Piece p, String str) {
		String[] words = str.split(" ");
		if (words.length != 4) {
			return null;
		}

		try {
			int row, col, rowDest, colDest;
			row = Integer.parseInt(words[0]);
			col = Integer.parseInt(words[1]);
			rowDest = Integer.parseInt(words[2]);
			colDest = Integer.parseInt(words[3]);
			return createMove(row, col, rowDest, colDest, p);
		} catch (NumberFormatException e) {
			return null;
		}
		// return null;
	}

	protected GameMove createMove(int row, int col, int rowDest, int colDest,
			Piece p) {
		GameMove m = new ATAXXMove(row, col, rowDest, colDest, p);

		return m;
	}

	public void switchCel(int rowDest, int colDest, Board board,
			List<Piece> pieces) {
		int countPlay;
		Piece turn, newP;
		turn = board.getPosition(rowDest, colDest);

		for (int i = Math.max(0, rowDest - 1); i <= Math.min(
				board.getRows() - 1, rowDest + 1); i++) {

			for (int j = Math.max(0, colDest - 1); j <= Math.min(
					board.getCols() - 1, colDest + 1); j++) {
				newP = board.getPosition(i, j);

				if (newP != null) {
					if (newP.getId() != "*") {
						if (!turn.equals(newP)) {
							countPlay = board.getPieceCount(getPiece());
							int countNeigbPiece = board.getPieceCount(board
									.getPosition(i, j));
							board.setPieceCount(newP, countNeigbPiece - 1);
							board.setPosition(i, j, getPiece());
							board.setPieceCount(turn, countPlay + 1);

						}

					}

				}
			}
		}
	}

	@Override
	public String help() {
		return "'row column rowDestiny columnDestiny ', to move a piece at the destiny position.";
	}

	@Override
	public String toString() {
		if (getPiece() == null) {
			return help();
		} else {
			return "Place the piece '" + getPiece() + "' from (" + row + ","
					+ col + ") to (" + rowDest + "," + colDest + ")";
		}
	}

}
