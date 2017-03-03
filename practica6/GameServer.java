package es.ucm.fdi.tp.practica6;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.GameFactory;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.control.commands.Command;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.Game;
import es.ucm.fdi.tp.basecode.bgame.model.Game.State;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.practica6.responses.ChangeTurnResponse;
import es.ucm.fdi.tp.practica6.responses.ErrorResponse;
import es.ucm.fdi.tp.practica6.responses.GameOverResponse;
import es.ucm.fdi.tp.practica6.responses.GameStartResponse;
import es.ucm.fdi.tp.practica6.responses.MoveEndResponse;
import es.ucm.fdi.tp.practica6.responses.MoveStartResponse;
import es.ucm.fdi.tp.practica6.responses.Response;

public class GameServer  extends Controller implements GameObserver {
	private int port;
	private int numPlayers;
	private int numOfConnectedPlayers;
	private GameFactory gameFactory;
	private List<Connection> clients;
	volatile private ServerSocket server;
	volatile private boolean stopped;
	volatile private boolean gameOver;

	JTextArea infoArea;
	JButton quitButton;

	public GameServer(GameFactory gameFactory, List<Piece> pieces, int port) {
		super(new Game(gameFactory.gameRules()), pieces);
		this.gameFactory = gameFactory;
		this.pieces = pieces;
		this.numOfConnectedPlayers = 0;
		this.numPlayers = pieces.size();
		this.port = port;
		this.clients = new ArrayList<>();
		game.addObserver(this);
	}

	public synchronized void makeMove(Player player){
		try{
			super.makeMove(player);
		}
		catch(GameError e){
			log("Error making a move");
		}
	}

	public synchronized void stop(){
		try{
			super.stop();
		}
		catch(GameError e){
			log("Error stoping the game");
		}
	}

	public synchronized void restart(){
		try{
			super.restart();
			log("The game has been restarted");
		}
		catch(GameError e){
			log("Error restarting the game");
		}
	}

	public void start() {
		controlGUI();
		startServer();

	}

	private void startServer() {
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			log("Error starting the server");
		}

		this.stopped = false;
		while(!stopped){
			try{
				Socket s = server.accept();
				log("Connection accepted");
				handleRequest(s);
			}catch(IOException e){
				if(!stopped){
					log("Error while waiting for a connection: " + e.getMessage());
				}
			}
		}
	}

	private void controlGUI() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() { constructGUI(); }
			});
		} catch (InvocationTargetException | InterruptedException e) {
			throw new GameError("Something went wrong when constructing the GUI");
		}
	}

	public void setStopped(boolean stopped){
		this.stopped = stopped;
	}

	public void setGameOver(boolean gameOver){
		this.gameOver = gameOver;
	}

	private void constructGUI() {
		JFrame window = new JFrame("Game Server");
		infoArea = new JTextArea();
		infoArea.setEditable(false);
		infoArea.setVisible(true);
		JScrollPane scroll = new JScrollPane(infoArea);
		Border borderStatus = new TitledBorder("Status message");
		scroll.setBorder(borderStatus);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		quitButton = new JButton("Stop Sever");

		quitButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				setStopped(true);

				if(!gameOver){
					if(game.getState() == State.InPlay || game.getState() == State.Starting){
						stop();
					}else{
						stop();
						setGameOver(true);
					}
				}

				for (Connection c: clients){
					try {
						c.stop();
					} catch (IOException e1) {
						
					}
				}

				try {
					server.close();
					log("The server has been closed");
				} catch (IOException e1) {
					log("Error closing the server");
				}
			}
		});	

		window.setLayout(new BorderLayout());
		window.add(scroll, BorderLayout.CENTER);
		window.add(quitButton, BorderLayout.SOUTH);
		window.setPreferredSize(new Dimension(500, 500));
		if(game.getState().equals(State.InPlay)){
			window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		}else{
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}

		window.pack();
		window.setVisible(true);
	}
	private void log(String msg) {
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run() {
						infoArea.setText(infoArea.getText()+"\n" +msg);
					}
				});		
	} 

	private void handleRequest(Socket s) {
		try {
			Connection c = new Connection(s);
			Object clientRequest = c.getObject();
			if ( !(clientRequest instanceof String) && !((String) clientRequest).equalsIgnoreCase("Connect") ) {
				c.sendObject(new GameError("Invalid Request"));
				c.stop();
				return;
			}
			if(numPlayers <= numOfConnectedPlayers){
				c.sendObject(new GameError("Game completed"));
				return;
			}
			else{
				numOfConnectedPlayers++;
				clients.add(c);
				log("Num of connected players: "+numOfConnectedPlayers);

				c.sendObject(new String("Ok"));
				c.sendObject(gameFactory);
				c.sendObject(pieces.get(numOfConnectedPlayers-1));
			}
			if(numPlayers == numOfConnectedPlayers){
				if(game.getState() == State.Starting){
					super.start();
				}
				else{
					restart();
				}
			}
			startClientListener(c);
		} catch (IOException | ClassNotFoundException _e) { 
			log("An error has been occured");
		}
	}

	private void startClientListener(Connection c) throws ClassNotFoundException {
		gameOver = false;
		Thread t = new Thread(new Runnable() {
			public void run() {
				while (!stopped && !gameOver) {
					try {
						Command cmd;
						// 1. read a Command
						cmd = (Command) c.getObject();
						// 2. execute the command
						cmd.execute(GameServer.this);
					} catch (ClassNotFoundException | IOException e) {
						if (!stopped && !gameOver) {
							GameServer.this.gameOver = true;
							GameServer.this.stop();

							try {
								c.stop();
								numOfConnectedPlayers = 0;
						
							} catch (IOException e1) {
								log("There's a problem with the connections");
							}
						}
					}
				}
					clients.remove(c);
			}
		});
		t.start();
	}

	public void onGameStart(Board board, String gameDesc, List<Piece> pieces, Piece turn) {
		try {
			forwardNotification(new GameStartResponse(board, gameDesc, pieces, turn));
			log("Starting the game");
		} catch (IOException e) {
			log("Has been occured a problem starting the game");
		}
	}

	public void onGameOver(Board board, State state, Piece winner) {
		try {
			forwardNotification(new GameOverResponse(board, state, winner));
			clients.clear();
		} catch (IOException e) {
			log("The server has been stopped");
		}

	}

	public void onMoveStart(Board board, Piece turn) {
		try {
			forwardNotification(new MoveStartResponse(board, turn));
		} catch (IOException e) {
		}
	}

	public void onMoveEnd(Board board, Piece turn, boolean gameOver) {
		try {
			forwardNotification(new MoveEndResponse(board, turn, gameOver));
		} catch (IOException e) {

		}
	}

	public void onChangeTurn(Board board, Piece turn) {
		try {
			forwardNotification(new ChangeTurnResponse(board, turn));
			log("Turn for "+turn);
		} catch (IOException e) {
			log("Error changing the turn");
		}

	}

	public void onError(String msg) {
		try {
			forwardNotification(new ErrorResponse(msg));
			log(msg);
		} catch (IOException e) {
			
		}
	}

	void forwardNotification(Response r) throws IOException {
		for(Connection c: clients){
			c.sendObject(r);
		}
	}

}
