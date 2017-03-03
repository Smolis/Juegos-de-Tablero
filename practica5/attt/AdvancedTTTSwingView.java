package es.ucm.fdi.tp.practica5.attt;
import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.practica5.RectBoardSwingView;

public class AdvancedTTTSwingView extends RectBoardSwingView {

	private static final long serialVersionUID = 1L;
	private AdvancedTTTSwingPlayer player;
	private int click;
	
	public AdvancedTTTSwingView(Observable<GameObserver> g, Controller c,	Piece localPiece, Player randPlayer, Player aiPlayer) {
		super(g, c, localPiece, randPlayer, aiPlayer);
		player = new AdvancedTTTSwingPlayer();
	}

	public void sumarClick(){
		this.click++;
	}
	
	public void reiniciarClick(){
		this.click = 0;
	}

	protected void handleMouseClick(int row, int col, int mouseButton) {
		try{
			if(click == 1){
				if(getBoard().getPosition(row, col) != null){
					player.setMove1(row, col);
				}
				else if(getBoard().getPosition(row, col) == null){
					player.setMove1(row,col);
					player.setMove2(row,col);
					decideMakeManualMove(player);
					reiniciarClick();
				}
			}
			else if(click == 2){
				player.setMove2(row, col);
				reiniciarClick();
				decideMakeManualMove(player);
			}
		}catch(GameError e){
			reiniciarClick();
		}
	}	

	protected void activateBoard() {
		noBlockBoard();
		
	}

	protected void deActivateBoard() {
		yesBlockBoard();
		
	}

}
