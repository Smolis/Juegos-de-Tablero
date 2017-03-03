package es.ucm.fdi.tp.practica5;

import java.awt.Color;

import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

public abstract class RectBoardSwingView extends SwingView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private BoardComponent boardcomp;

	public RectBoardSwingView(Observable<GameObserver> g, Controller c, Piece localPiece, Player randPlayer, Player aiPlayer) {
		super(g, c, localPiece, randPlayer, aiPlayer);

	}

	@Override
	protected void initBoardGui() {
		boardcomp = new BoardComponent() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void mouseClicked(int row, int col, int mouseButton) {
				if (!getBlockBoard()) {
					sumarClick();
					handleMouseClick(row, col, mouseButton);
				} else {
					addMsg("No es  tu turno!");
				}
			}

			@Override
			protected Color getPieceColor(Piece p) {
				// get the color from the colours table, and if not
				// available (e.g., for obstacles) set it to have a color
				return RectBoardSwingView.this.getPieceColor(p);

			};

			@Override
			protected boolean isPlayerPiece(Piece p) {
				// return true if p is a player piece, false if not (e.g, an
				// obstacle)
				return getPieces().contains(p);

			}
		};
		setBoardArea(boardcomp); // install the board in the view
	}

	protected void yesBlockBoard() {
		boardcomp.setBlockBoard(true);
	}

	protected void noBlockBoard() {
		boardcomp.setBlockBoard(false);
	}

	@Override
	protected void redrawBoard() {
		// ask boardComp to redraw the board
		boardcomp.redraw(getBoard());
		boardcomp.piecesSize = this.getPiecesSize();
	}

	public void setStyle(int style) {
		boardcomp.setStyle(style);
	}

	protected abstract void handleMouseClick(int row, int col, int mouseButton);

	protected abstract void sumarClick();
}
