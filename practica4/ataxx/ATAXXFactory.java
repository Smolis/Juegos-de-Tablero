package es.ucm.fdi.tp.practica4.ataxx;

import java.util.ArrayList;
import java.util.Scanner;

import es.ucm.fdi.tp.basecode.bgame.control.ConsolePlayer;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameMove;
import es.ucm.fdi.tp.basecode.bgame.model.GameRules;
import es.ucm.fdi.tp.basecode.connectn.ConnectNFactory;

public class ATAXXFactory extends ConnectNFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int dim;
	private int obstacles;

	public ATAXXFactory() {
		this.dim = 5;
		this.obstacles = 1;
	}

	public ATAXXFactory(int dim, int obstacles) {
		if ((dim < 5) || (dim % 2 == 0)) {
			throw new GameError(
					"Dimension must be at least 5 and odd. Your dimension was: "
							+ dim);
		} else {
			this.dim = dim;
			this.obstacles = obstacles;
		}
	}

	public ATAXXFactory(int dim) {
		if ((dim < 5) || (dim % 2 == 0)) {
			throw new GameError(
					"Dimension must be at least 5 and odd. Your dimension was: "
							+ dim);
		} else {
			this.dim = dim;
			this.obstacles = 2;
		}
	}

	@Override
	public GameRules gameRules() {
		return new ATAXXRules(dim, obstacles);
	}

	@Override
	public Player createConsolePlayer() {
		ArrayList<GameMove> possibleMoves = new ArrayList<GameMove>();
		possibleMoves.add(new ATAXXMove());
		return new ConsolePlayer(new Scanner(System.in), possibleMoves);
	}

	@Override
	public Player createRandomPlayer() {
		return new ATAXXRandomPlayer();
	}

}
