package es.ucm.fdi.tp.practica5.ataxx;

import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.practica5.RectBoardSwingView;

public class ATAXXSwingView extends RectBoardSwingView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ATAXXSwingPlayer player;
	private int click = 0;

	public ATAXXSwingView(Observable<GameObserver> g, Controller c,
			Piece localPiece, Player randPlayer, Player aiPlayer) {
		super(g, c, localPiece, randPlayer, aiPlayer);
		player = new ATAXXSwingPlayer();

	}

	public void sumarClick() {
		this.click++;
	}

	public void reiniciarClick() {
		this.click = 0;
	}

	protected void handleMouseClick(int row, int col, int mouseButton) {
		try {
			if (click == 1) {
				if ((getBoard().getPosition(row, col) != null)) {
					player.setMove1(row, col);
				} else {
					reiniciarClick();
				}
			} else if (click == 2) {
				if (getBoard().getPosition(row, col) == null) {
					player.setMove2(row, col);
					reiniciarClick();
					decideMakeManualMove(player);
				} else {
					reiniciarClick();
				}

			}
		} catch (GameError e) {
			reiniciarClick();
		}
	}

	@Override
	protected void activateBoard() {
		noBlockBoard();

	}

	@Override
	protected void deActivateBoard() {
		yesBlockBoard();

	}
}
