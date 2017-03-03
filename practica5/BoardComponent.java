package es.ucm.fdi.tp.practica5;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

public abstract class BoardComponent extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int _CELL_HEIGHT = 50;
	private int _CELL_WIDTH = 50;
	private int rows;
	private int cols;
	private Board board;
	protected boolean blockBoard;
	protected List<Piece> piecesSize;
	private int style;

	// Styles
	private ImageIcon background;
	private ImageIcon player1;
	private ImageIcon player2;
	private ImageIcon player3;
	private ImageIcon player4;
	private ImageIcon wall;

	public BoardComponent() {
		blockBoard = true;
		this.style = 0;
		initGUI();
	}

	public void redraw(Board b) {
		this.cols = b.getCols();
		this.rows = b.getRows();
		this.board = b;
		repaint();
	}

	private void initGUI() {
		addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent me) {
				int x;
				int y;
				int button;
				x = me.getX() / _CELL_WIDTH;
				y = me.getY() / _CELL_HEIGHT;
				button = me.getButton();
				BoardComponent.this.mouseClicked(y, x, button);
			}

			public void mouseEntered(MouseEvent me) {
			}

			public void mouseExited(MouseEvent me) {
			}

			public void mousePressed(MouseEvent me) {
			}

			public void mouseReleased(MouseEvent me) {
			}
		});

	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (board == null) {
			return;
		}

		_CELL_WIDTH = this.getWidth() / cols;
		_CELL_HEIGHT = this.getHeight() / rows;

		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				drawCell(i, j, g);
	}

	/**
	 * Creamos las distintas fichas,muros,celdas del tablero según el juego que
	 * queramos jugar y la apariencia seleccionada
	 * */
	private void drawCell(int row, int col, Graphics g) {
		int x = col * _CELL_WIDTH;
		int y = row * _CELL_HEIGHT;
		Piece piece = board.getPosition(row, col);

		if (style == 0) {
			styleFaces();
		} else if (style == 1) {
			styleSuperMario();
		} else if (style == 2) {
			styleSea();
		}

		if (board.getPosition(row, col) == null) {
			g.drawImage(background.getImage(), x + 2, y + 2, _CELL_WIDTH - 4, _CELL_HEIGHT - 4, getPieceColor(piece), null);
		}

		else if (board.getPosition(row, col).getId() != "*") {
			int piec = 0;
			for (int i = 0; i < piecesSize.size(); i++) {
				if (board.getPosition(row, col).equals(piecesSize.get(i))) {
					piec = i;
				}

			}
			if (piec == 0) {
				g.setColor(getPieceColor(piece));
				g.drawImage(player1.getImage(), x + 2, y + 2, _CELL_WIDTH - 4, _CELL_HEIGHT - 4, getPieceColor(piece), null);

			} else if (piec == 1) {
				g.setColor(getPieceColor(piece));
				g.drawImage(player2.getImage(), x + 2, y + 2, _CELL_WIDTH - 4, _CELL_HEIGHT - 4, getPieceColor(piece), null);

			} else if (piec == 2) {
				g.setColor(getPieceColor(piece));
				g.drawImage(player3.getImage(), x + 2, y + 2, _CELL_WIDTH - 4, _CELL_HEIGHT - 4, getPieceColor(piece), null);

			} else {
				g.setColor(getPieceColor(piece));
				g.drawImage(player4.getImage(), x + 2, y + 2, _CELL_WIDTH - 4, _CELL_HEIGHT - 4, getPieceColor(piece), null);
			}

		} if(!piecesSize.contains(board.getPosition(row, col)) && board.getPosition(row, col) != null){
			g.drawImage(wall.getImage(), x + 2, y + 2, _CELL_WIDTH - 4, _CELL_HEIGHT - 4, null);
			setOpaque(false);
			super.paintComponent(g);
		}
	}

	/**
	 * Guarda los directorios de la apariencia Faces.
	 */
	private void styleFaces() {
		background = new ImageIcon(getClass().getResource("/images/Faces/roble.jpg"));
		player1 = new ImageIcon(getClass().getResource("/images/Faces/bigotes.png"));
		player2 = new ImageIcon(getClass().getResource("/images/Faces/monkey.png"));
		player3 = new ImageIcon(getClass().getResource("/images/Faces/spiderpig.png"));
		player4 = new ImageIcon(getClass().getResource("/images/Faces/tomato.png"));
		wall = new ImageIcon(getClass().getResource("/images/Faces/wall.png"));
	}

	/**
	 * Guarda los directorios de la apariencia Super Mario.
	 */
	private void styleSuperMario() {
		background = new ImageIcon(getClass().getResource("/images/SuperMario/cesped.jpg"));
		player1 = new ImageIcon(getClass().getResource("/images/SuperMario/mario.png"));
		player2 = new ImageIcon(getClass().getResource("/images/SuperMario/peach.png"));
		player3 = new ImageIcon(getClass().getResource("/images/SuperMario/yoshi.png"));
		player4 = new ImageIcon(getClass().getResource("/images/SuperMario/seta.png"));
		wall = new ImageIcon(getClass().getResource("/images/SuperMario/muro.jpeg"));
	}

	/**
	 * Guarda los directorios de la apariencia Sea.
	 */
	private void styleSea() {
		background = new ImageIcon(getClass().getResource("/images/Sea/sea.jpg"));
		player1 = new ImageIcon(getClass().getResource("/images/Sea/boat.png"));
		player2 = new ImageIcon(getClass().getResource("/images/Sea/boat.png"));
		player3 = new ImageIcon(getClass().getResource("/images/Sea/boat.png"));
		player4 = new ImageIcon(getClass().getResource("/images/Sea/boat.png"));
		wall = new ImageIcon(getClass().getResource("/images/Sea/shark.png"));
	}

	public boolean getBlockBoard() {
		return blockBoard;
	}

	public void setBlockBoard(boolean blockBoard) {
		this.blockBoard = blockBoard;
	}

	public void setStyle(int style) {
		this.style = style;
	}

	protected abstract Color getPieceColor(Piece p);

	protected abstract boolean isPlayerPiece(Piece p);

	protected abstract void mouseClicked(int row, int col, int mouseButton);

}
