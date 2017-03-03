package es.ucm.fdi.tp.practica5.ataxx;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.practica4.ataxx.ATAXXFactory;

public class ATAXXFactoryExt extends ATAXXFactory {
	private static final long serialVersionUID = 1L;

	public ATAXXFactoryExt(Integer dim) {
		super(dim);
	}

	public ATAXXFactoryExt(Integer dimRows, Integer obstacles) {
		super(dimRows, obstacles);
	}

	public ATAXXFactoryExt() {
		super();
	}

	@Override
	public void createSwingView(final Observable<GameObserver> g, final Controller c, final Piece viewPiece, Player random, Player ai) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					new ATAXXSwingView(g, c, viewPiece, random, ai);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			throw new GameError("The game couldn't be started.");
		}
	}

}
