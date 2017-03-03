package es.ucm.fdi.tp.practica4.ataxx;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.FiniteRectBoard;
import es.ucm.fdi.tp.basecode.bgame.model.Game.State;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameMove;
import es.ucm.fdi.tp.basecode.bgame.model.Pair;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.basecode.connectn.ConnectNRules;

public class ATAXXRules extends ConnectNRules {

	private int dim;
	private int obstacles;

	public ATAXXRules(int dim, int obstacles) {
		super(dim);

		if ((dim < 5) || (dim % 2 == 0)) {
			throw new GameError(
					"Dimension must be at least 5 and odd. Your dimension was: "
							+ dim);
		} else {
			this.dim = dim;
			this.obstacles = obstacles;
		}
	}

	// Crea el tablero inicial del juego según los parámetros insertados
	@Override
	public Board createBoard(List<Piece> pieces) {
		int obstacles = this.obstacles;
		int contM = 0;
		int tamano = pieces.size();
		Board b = new FiniteRectBoard(dim, dim);
		switch (tamano) {
		case (4): {
			Piece r = pieces.get(3);
			b.setPosition(0, dim / 2, r);
			b.setPosition(dim - 1, dim / 2, r);
			b.setPieceCount(r, 2);
		}
		case (3): {
			Piece m = pieces.get(2);
			b.setPosition(dim / 2, 0, m);
			b.setPosition(dim / 2, dim - 1, m);
			b.setPieceCount(m, 2);
		}
		case (2): {
			Piece x = pieces.get(0);
			Piece o = pieces.get(1);
			b.setPosition(0, 0, x);
			b.setPosition(dim - 1, dim - 1, x);
			b.setPosition(0, dim - 1, o);
			b.setPosition(dim - 1, 0, o);
			b.setPieceCount(x, 2);
			b.setPieceCount(o, 2);
		}
		}

		Piece muro = new Piece("*");
		if (obstacles < (((dim * dim) / 4) - tamano)) {
			while (contM < obstacles) {
				int r = (int) (Math.random() * dim);
				int c = (int) (Math.random() * dim);

				if (b.getPosition(r, c) == null) {
					b.setPosition(r, c, muro);
					b.setPosition(r, (dim - c - 1), muro);
					b.setPosition((dim - r - 1), c, muro);
					b.setPosition((dim - r - 1), (dim - c - 1), muro);
					contM++;
				}
			}
		} else {
			throw new GameError("Too many obstacles.!");
		}
		return b;
	}

	// Comprueba los movimientos validos de cada jugador
	@Override
	public List<GameMove> validMoves(Board board, List<Piece> playersPieces,
			Piece turn) {
		List<GameMove> moves = new ArrayList<GameMove>();

		for (int i = 0; i < board.getRows(); i++) {
			for (int j = 0; j < board.getCols(); j++) {

				if (board.getPosition(i, j) != null) {
					if (board.getPosition(i, j).equals(turn)) {
						for (int x = Math.max(0, i - 2); x <= Math.min(
								board.getRows() - 1, i + 2); x++) {
							for (int y = Math.max(0, j - 2); y <= Math.min(
									board.getCols() - 1, j + 2); y++) {

								if (board.getPosition(x, y) == null) {

									moves.add(new ATAXXMove(i, j, x, y, turn));

								}
							}
						}
					}
				}
			}
		}
		return moves;
	}

	// Controla el estado del juego y lo actualiza (controla draw, won o seguir
	// jugando)
	@Override
	public Pair<State, Piece> updateState(Board board,
			List<Piece> playersPieces, Piece turn) {
		int max = 0, nPieces, cont = 0;
		Piece winner = null, player;
		boolean otro = false;

		if (board.isFull()) {
			for (int i = 0; i < playersPieces.size(); i++) {
				player = playersPieces.get(i);
				nPieces = board.getPieceCount(player);

				if (max <= nPieces) {
					winner = player;
					if ((i > 0) && (max == nPieces)) {
						cont++;
					}
					max = nPieces;
				}
			}
			if (cont == 0) {
				return new Pair<State, Piece>(State.Won, winner);
			} else if (cont >= 1) {
				return new Pair<State, Piece>(State.Draw, winner);
			}
		} else {

			player = this.nextPlayer(board, playersPieces, turn);

			if (player == turn) {

				for (int i = 0; i < board.getRows(); i++) {
					for (int j = 0; j < board.getCols(); j++) {
						if (board.getPosition(i, j) != null) {
							if ((!board.getPosition(i, j).equals(player))
									&& (board.getPosition(i, j)
											.equals(playersPieces))) {
								otro = true;

							}
						}

					}
				}
				if (!otro) {
					return new Pair<State, Piece>(State.Won, turn);
				}
			}

		}

		return gameInPlayResult;
	}

	// Método para obtener el siguiente jugador (calcula tambien si un jugador
	// tiene fichas pero no movimientos)
	@Override
	public Piece nextPlayer(Board board, List<Piece> playersPieces,
			Piece lastPlayer) {
		List<Piece> pieces = playersPieces;
		Piece p = null;
		int i = pieces.indexOf(lastPlayer);
		boolean find = false;

		for (int k = 1; k < playersPieces.size() && !find; k++) {

			if (board.getPieceCount(pieces.get((i + k) % pieces.size())) != 0) {
				p = pieces.get((i + k) % pieces.size());

				if (!validMoves(board, pieces, p).isEmpty())
					find = true;
			}
		}
		if (!find) {
			p = lastPlayer;
		}
		return p;
	}

	// Jugador inicial
	@Override
	public Piece initialPlayer(Board board, List<Piece> playersPieces) {
		return playersPieces.get(0);
	}

	// Descripción general del juego
	@Override
	public String gameDesc() {
		return "ATAXX " + dim + "x" + dim + "with " + obstacles * 4
				+ " obstacles.";
	}

}
