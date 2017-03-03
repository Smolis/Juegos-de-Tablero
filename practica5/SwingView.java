package es.ucm.fdi.tp.practica5;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import es.ucm.fdi.tp.basecode.bgame.Utils;
import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.Game.State;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.practica5.Main.PlayerMode;

public abstract class SwingView extends JFrame implements GameObserver {
	private static final long serialVersionUID = 1L;

	private Controller ctrl;
	private Piece localPiece;
	private Piece turn;
	private Board board;
	private List<Piece> pieces;
	private Map<Piece, Color> pieceColors;
	protected Map<Piece, PlayerMode> playerTypes;
	private Player randPlayer;
	private Player aiPlayer;
	private Object[][] data;
	private String GameDesc;

	// General
	private JPanel ventanaDcha;
	private JPanel ventanaCentro;

	// Log
	private JTextArea textoConsola;

	// Players
	private Table table;
	private JTable tab;

	// Pieces
	JComboBox<String> listaPieceColors;

	// Moves
	JComboBox<String> listaPieces;
	JPanel panelMoves;
	JButton butRandom;
	JButton butIntelligent;

	// MenuBar
	private JMenuBar mb;
	JMenu help;
	private JMenuItem commands;
	private JMenuItem rules;
	JMenu appearance;
	private JMenuItem style1;
	private JMenuItem style2;
	private JMenuItem style3;

	public SwingView(Observable<GameObserver> g, Controller c, Piece localPiece, Player randPlayer, Player aiPlayer) {
		this.ctrl = c;
		this.localPiece = localPiece;
		this.randPlayer = randPlayer;
		this.aiPlayer = aiPlayer;
		pieceColors = new HashMap<>();
		playerTypes = new HashMap<>();

		initGUI();
		g.addObserver(this);
	}

	private void initGUI() {
		ventanaCentro = new JPanel();
		configurarVentana();
		configurarPanelDerecho();
		menuBar();
		this.add(ventanaCentro, BorderLayout.CENTER);
		this.add(ventanaDcha, BorderLayout.EAST);
		initBoardGui();
	}

	/**
	 * Inicializa una barra de menu Añade los submenus de appearance y help
	 * */
	private void menuBar() {
		mb = new JMenuBar();
		setJMenuBar(mb);
		appearance();
		help();
	}

	/**
	 * Se crea el submenu help Tiene las opciones Commands, que muestra la lista
	 * de comandos para la consola y su informacion y Rules que muestra las
	 * reglas del juego al que se esta jugando
	 * */
	public void help() {
		listenerCommands com = new listenerCommands();
		listenerRules rul = new listenerRules();
		ImageIcon imagenCom = new ImageIcon(getClass().getResource("/images/Icons/help2.png"));
		ImageIcon imagenRul = new ImageIcon(getClass().getResource("/images/Icons/infor.png"));
		help = new JMenu("Help");
		mb.add(help);
		mb.setVisible(true);
		commands = new JMenuItem("Commands", imagenCom);
		help.add(commands);
		commands.addActionListener(com);
		rules = new JMenuItem("Rules", imagenRul);
		rules.addActionListener(rul);
		help.add(rules);
	}

	/**
	 * Se crea el submenu appearance con los distintos estilos (se cambia el
	 * fondo, las fichas y los obstaculos) Al seleccionar un estilo se avisa al
	 * jugador de que se reiniciara el juego para implementar dicho estilo
	 * */
	public void appearance() {
		listenerStyle1 s1 = new listenerStyle1();
		listenerStyle2 s2 = new listenerStyle2();
		listenerStyle3 s3 = new listenerStyle3();
		ImageIcon imagenMush = new ImageIcon(getClass().getResource("/images/Icons/mushroom.png"));
		ImageIcon imagenMonkey = new ImageIcon(getClass().getResource("/images/Icons/monkeyicon.png"));
		ImageIcon imagenNemo = new ImageIcon(getClass().getResource("/images/Icons/icon-nemo.png"));
		appearance = new JMenu("Appearance");
		mb.add(appearance);
		style1 = new JMenuItem("Faces", imagenMonkey);
		style1.addActionListener(s1);
		appearance.add(style1);
		style2 = new JMenuItem("Super Mario", imagenMush);
		style2.addActionListener(s2);
		appearance.add(style2);
		style3 = new JMenuItem("Sea", imagenNemo);
		style3.addActionListener(s3);
		appearance.add(style3);
	}

	/**
	 * Listeners correspondientes a cada submenu
	 * */
	public class listenerCommands implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String help = leerArchivo("commands.txt");
			JOptionPane.showMessageDialog(commands, help, "Commands", getDefaultCloseOperation());
		}
	}

	public class listenerRules implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String[] archivo = GameDesc.split(" ");
			String game = archivo[0] + ".txt";
			String help = leerArchivo(game);
			JOptionPane.showMessageDialog(rules, help, archivo[0] + " rules", getDefaultCloseOperation());
		}
	}

	public class listenerStyle1 implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			setStyle(0);
			repaint();
			redrawBoard();
		}
	}

	public class listenerStyle2 implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			setStyle(1);
			repaint();
			redrawBoard();

		}
	}

	public class listenerStyle3 implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			setStyle(2);
			repaint();
			redrawBoard();
		}
	}

	public String leerArchivo(String nomFich) {
		Scanner in = null;
		String cadena = "";

		try {
			in = new Scanner(new FileReader(nomFich));
			in.useLocale(Locale.ENGLISH);
			while (in.hasNext()) {
				String palabra = in.nextLine();
				cadena += palabra;
				cadena += "\n";
			}
		} catch (FileNotFoundException e) {
			System.out.println("Can't open the file:  " + nomFich);
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return cadena;
	}

	private void configurarVentana() {
		this.setTitle(" Board Games: ");
		this.setSize(1200, 600);
		this.setMinimumSize(getSize());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.setVisible(true);

	}

	private void configurarPanelDerecho() {
		ventanaDcha = new JPanel();
		panelLog();
		panelPlayer();
		panelPieces();
		panelMoves();
		panelQuit();
		BoxLayout bl = new BoxLayout(ventanaDcha, BoxLayout.Y_AXIS);
		ventanaDcha.setLayout(bl);
		this.add(ventanaDcha);
	}

	private void panelLog() {
		textoConsola = new JTextArea(12, 20);
		textoConsola.setLineWrap(false);
		textoConsola.setEditable(false);
		textoConsola.setVisible(true);
		textoConsola.scrollRectToVisible(getMaximizedBounds());
		JScrollPane scroll = new JScrollPane(textoConsola);
		Border borderStatus = new TitledBorder("Status Message");
		scroll.setBorder(borderStatus);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		ventanaDcha.add(scroll);
	}

	private void panelPlayer() {
		JPanel panelPlayer = new JPanel();
		Border borderPlayer = new TitledBorder("Player Information");
		tab = new JTable() {
			private static final long serialVersionUID = 1L;

			public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
				Component comp = super.prepareRenderer(renderer, row, col);
				comp.setBackground(pieceColors.get(pieces.get(row)));
				return comp;
			};
		};

		JScrollPane scrollTbl = new JScrollPane(tab);

		panelPlayer.setLayout(new BoxLayout(panelPlayer, BoxLayout.Y_AXIS));

		tab.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		panelPlayer.setBorder(borderPlayer);

		panelPlayer.add(scrollTbl);
		ventanaDcha.add(panelPlayer);
	}

	public class Table extends AbstractTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String[] columnNames = { "Player", "Mode", "#Pieces" };
		private Object[][] data;

		public Table(Object[][] data) {
			super();
			this.data = data;
		}

		public int getRowCount() {
			return data.length;
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public Object getValueAt(int row, int col) {
			if (col == 0) {
				return pieces.get(row);
			} else if (col == 1) {
				if (localPiece == pieces.get(row)) {
					return playerTypes.get(pieces.get(row)).getDesc();
				}

				else if (localPiece == null) {
					return playerTypes.get(pieces.get(row)).getDesc();
				} else {
					return null;
				}

			} else {
				return board.getPieceCount(pieces.get(row));
			}
		}

		public String getColumnName(int col) {
			return columnNames[col].toString();
		}

		public void refresh() {
			fireTableDataChanged();
		}

	}

	private void panelPieces() {
		pieceColor();
		playerModes();
	}

	private void pieceColor() {
		// Panel Piece Colors
		Border borderPiece = new TitledBorder("Piece Colors");
		JPanel panelPieces = new JPanel();
		panelPieces.setBorder(borderPiece);

		// ComboBox pieceColors
		JButton chooseColorButt = new JButton("Choose Color");
		listaPieceColors = new JComboBox<String>();



		chooseColorButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int pieceSelect = listaPieceColors.getSelectedIndex();
				Color color;

				color = JColorChooser.showDialog(null, "Choose a color for the player ", Color.gray);
				if(localPiece == null){
					addMsg("You have modified the color of the player: " + pieces.get(pieceSelect).toString());
					repaint();
					if (color != null)
						setPieceColor(pieces.get(pieceSelect), color);
				}
				else{
					addMsg("You have modified the color of the player: " + localPiece);
					repaint();
					if (color != null)
						setPieceColor(localPiece, color);
				}

			}
		});

		panelPieces.add(listaPieceColors);
		panelPieces.add(chooseColorButt);
		panelPieces.setLayout(new BoxLayout(panelPieces, BoxLayout.LINE_AXIS));
		ventanaDcha.add(panelPieces);

	}

	private void playerModes() {
		Border borderModes = new TitledBorder("Player Modes");
		JPanel modes = new JPanel();
		modes.setBorder(borderModes);
		JButton set = new JButton("Set");
		String[] fichas = {};
		String[] modos = { "Manual", "Random", "Intelligent" };
		listaPieces = new JComboBox<String>(fichas);
		JComboBox<String> modeList = new JComboBox<String>(modos);
		BoxLayout mod = new BoxLayout(modes, BoxLayout.LINE_AXIS);
		modes.setLayout(mod);
		modes.add(listaPieces);
		modes.add(modeList);
		modes.add(set);
		set.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int pieceSelect = listaPieces.getSelectedIndex();
				int modeSelect = modeList.getSelectedIndex();

				if ((localPiece == null && pieces.get(listaPieces.getSelectedIndex()).equals(turn)) /*|| pieces.get(listaPieces.getSelectedIndex()).equals(localPiece)*/) {
					switch (modeSelect) {
					case 0:
						playerTypes.put(pieces.get(pieceSelect), PlayerMode.MANUAL);
						addMsg("You have modified the player mode: " + pieces.get(pieceSelect));
						butIntelligent.setEnabled(true);
						butRandom.setEnabled(true);
						break;
					case 1:
						playerTypes.put(pieces.get(pieceSelect), PlayerMode.RANDOM);
						addMsg("You have modified the player mode: " + pieces.get(pieceSelect));
						decideMakeAutomaticMove(turn);
						break;
					case 2:
						playerTypes.put(pieces.get(pieceSelect), PlayerMode.AI);
						addMsg("You have modified the player mode: " + pieces.get(pieceSelect));
						decideMakeAutomaticMove(turn);

						break;
					}
				}

				else if(localPiece == null && !pieces.get(listaPieces.getSelectedIndex()).equals(turn)) {
					addMsg("You can't modify other player's mode");
				}
				else{
					if(!localPiece.equals(turn)){
						butIntelligent.setEnabled(false);
						butRandom.setEnabled(false);
						switch (modeSelect) {
						case 0:
							addMsg("You can't change the mode if it is not your turn!!");
							break;
						case 1:
							addMsg("You can't change the mode if it is not your turn!!");
							break;
						case 2:
							playerTypes.put(localPiece, PlayerMode.AI);
							addMsg("You have modified the player mode: " + localPiece.toString());
							decideMakeAutomaticMove(turn);
							break;
						}

					}else{
						switch (modeSelect) {
						case 0:
							playerTypes.put(localPiece, PlayerMode.MANUAL);
							addMsg("You have modified the player mode: " + localPiece.toString());
							butIntelligent.setEnabled(true);
							butRandom.setEnabled(true);
							break;
						case 1:
							playerTypes.put(localPiece, PlayerMode.RANDOM);
							addMsg("You have modified the player mode: " + localPiece.toString());
							decideMakeAutomaticMove(turn);
							break;
						case 2:
							playerTypes.put(localPiece, PlayerMode.AI);
							addMsg("You have modified the player mode: " + localPiece.toString());
							decideMakeAutomaticMove(turn);
							break;
						}
					}
				}
				table.refresh();

			}
		});
		ventanaDcha.add(modes);
	}

	private void panelMoves() {
		panelMoves = new JPanel();
		butRandom = new JButton("Random");
		butIntelligent = new JButton("Intelligent");
		Border borderMoves = new TitledBorder("Automatic Moves");

		panelMoves.setBorder(borderMoves);

		panelMoves.add(butRandom);

		butRandom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ctrl.makeMove(randPlayer);
			}
		});

		panelMoves.add(butIntelligent);

		butIntelligent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ctrl.makeMove(randPlayer);
			}
		});

		ventanaDcha.add(panelMoves);
	}

	private void panelQuit() {
		JPanel panelQuit = new JPanel();
		JButton butQuit = new JButton("Quit");
		JButton butRestart = new JButton("Restart");
		Border borderQuit = new TitledBorder("");

		panelQuit.setBorder(borderQuit);
		butQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(butQuit, "Do you wish to exit?", null, JOptionPane.YES_NO_OPTION);

				if (JOptionPane.YES_OPTION == result)
					ctrl.stop();
					System.exit(0);
				
			}
		});
		panelQuit.add(butQuit);
		butRestart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(butRestart, "Do you want to restart?", null, JOptionPane.YES_NO_OPTION);

				if (JOptionPane.YES_OPTION == result) {
					restartGame();
				}
			}
		});
		if (localPiece == null) {
			panelQuit.add(butRestart);
		}
		ventanaDcha.add(panelQuit);
	}

	/**
	 * Reinicia el juego, para ello elimina los jugadores de los combobox,
	 * activa de nuevo los botones de random e intelligent, y reinicia el
	 * controlador
	 * */
	private void restartGame() {
		butIntelligent.setEnabled(true);
		butRandom.setEnabled(true);
		listaPieces.removeAllItems();
		listaPieceColors.removeAllItems();
		ctrl.restart();
		textoConsola.setText("");
	}

	final protected Piece getTurn() {
		return turn;
	}

	final protected Board getBoard() {
		return board;
	}

	final protected List<Piece> getPieces() {
		return pieces;
	}

	final protected Color getPieceColor(Piece p) {
		return pieceColors.get(p);
	}

	final protected Color setPieceColor(Piece p, Color c) {
		return pieceColors.put(p, c);
	}

	final protected void setBoardArea(JComponent c) {
		this.getContentPane().add(c, BorderLayout.CENTER);
	}

	final protected void addMsg(String msg) {
		textoConsola.append(msg + "\n");
	}

	final protected void decideMakeManualMove(Player manualPlayer) {
		// subclasses use this method when they are ready to make a manual move
		ctrl.makeMove(manualPlayer);
	}

	protected void decideMakeAutomaticMove(Piece turn) {
		// P es el jugador random o ai
		// hacer movimiento automático si es necesario
		// turn es automatico y juega en esta vista

		if (playerTypes.get(turn).equals(PlayerMode.RANDOM)) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					ctrl.makeMove(randPlayer);
				}
			});
		} else if (playerTypes.get(turn).equals(PlayerMode.AI)) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					ctrl.makeMove(aiPlayer);
				}
			});

		}
	}

	public void onGameStart(final Board board, final String gameDesc, final List<Piece> pieces, final Piece turn) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				handleOnGameStart(board, gameDesc, pieces, turn);
			}
		});
	}

	public void onGameOver(final Board board, final State state, final Piece winner) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				handleOnGameOver(board, state, winner);
			}
		});
	}

	public void onMoveEnd(final Board board, final Piece turn, final boolean success) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				handleOnMoveEnd(board, turn, success);
			}
		});
	}

	public void onMoveStart(Board board, Piece turn) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				handleOnMoveStart(board, turn);
			}
		});
	}

	public void onChangeTurn(Board board, final Piece turn) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				handleOnChangeTurn(board, turn);
			}
		});
	}

	public void onError(String msg) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				handleOnError(msg);
			}
		});
	}

	protected void handleOnGameStart(final Board board, final String gameDesc, final List<Piece> pieces, final Piece turn) {
		this.board = board;
		this.pieces = pieces;
		this.turn = turn;
		this.GameDesc = gameDesc;

		data = new Object[pieces.size()][3];

		for (int k = 0; k < pieces.size(); ++k) {
			setPieceColor(pieces.get(k), Utils.randomColor());
			playerTypes.put(pieces.get(k), PlayerMode.MANUAL);
			if(localPiece == null){
				listaPieceColors.addItem(pieces.get(k).toString());
				listaPieces.addItem(pieces.get(k).toString());
			}

		}
		if(localPiece!=null){
			listaPieceColors.addItem(localPiece.toString());
			listaPieces.addItem(localPiece.toString());
		}
		table = new Table(data);
		tab.setModel(table);

		if (localPiece != null) {
			if (turn.equals(localPiece)) {
				activateBoard();
			} else {
				deActivateBoard();
				butRandom.setEnabled(false);
				butIntelligent.setEnabled(false);
			}
			this.setTitle(gameDesc + "Player: (" + localPiece.getId() + ")");
		} else {
			activateBoard();
			this.setTitle(gameDesc);
		}

		decideMakeAutomaticMove(turn);
		addMsg("Turn for player: " + turn);

		redrawBoard();
	}

	protected void handleOnGameOver(final Board board, final State state, final Piece winner) {
		this.board = board;
		String mensaje = "";
	
		if (winner == null) {
			mensaje = "Draw";
			addMsg(mensaje);
			
		} else {
			mensaje = "The winner is: " + winner;
			addMsg(mensaje);
		}
		repaint();
		JOptionPane.showMessageDialog(this, mensaje);

		butIntelligent.setEnabled(false);
		butRandom.setEnabled(false);
	}

	protected void handleOnMoveStart(Board board, Piece turn) {
	}

	protected void handleOnMoveEnd(Board board, Piece turn, boolean success) {
		this.board = board;
		redrawBoard();
		repaint();
	}

	protected void handleOnError(String msg) {
		addMsg(msg);
	}

	protected void handleOnChangeTurn(Board board, final Piece turn) {
		this.board = board;
		this.turn = turn;
		addMsg("Turn for player: " + turn);

		if (localPiece != null) {
			if (localPiece.equals(turn)) {
				activateBoard();
				butRandom.setEnabled(true);
				butIntelligent.setEnabled(true);
			} else {
				deActivateBoard();
				butRandom.setEnabled(false);
				butIntelligent.setEnabled(false);
			}
		} else {
			activateBoard();
		}

		if (playerTypes.get(turn) != PlayerMode.MANUAL) {
			decideMakeAutomaticMove(turn);
		}

		repaint();
		redrawBoard();
	}

	protected List<Piece> getPiecesSize() {
		return pieces;
	}

	protected abstract void setStyle(int style);

	protected abstract void initBoardGui();

	protected abstract void activateBoard();

	protected abstract void deActivateBoard();

	protected abstract void redrawBoard();
}
