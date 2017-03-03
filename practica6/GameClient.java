package es.ucm.fdi.tp.practica6;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.GameFactory;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.control.commands.Command;
import es.ucm.fdi.tp.basecode.bgame.control.commands.PlayCommand;
import es.ucm.fdi.tp.basecode.bgame.control.commands.QuitCommand;
import es.ucm.fdi.tp.basecode.bgame.control.commands.RestartCommand;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.Game.State;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.practica6.responses.Response;

public class GameClient extends Controller implements Observable<GameObserver>{
	//No basta con parar a todos los clientes(transparencias)
	//Tenemos una lista de conexiones, cada conexion deberia desconectarla y eliminarla de la lista de conexiones
	//Conexions.clear() para vaciarla entera

	//ipconfig wlan()  lo que cambia es el ultimo numero dentro de una red local
	//en vez de localhost podemos usar esas direcciones, entre comillas
	//si el otro esta en otra casa, podemos usar uno de los dos como servidor, el router tiene una ip visible, se puede ver
	//whatsmyipaddress (en google, la primera pagina)
	//asi que en modo servidor podemos poner el puerto que queramos y en modo cliente usamos la direccion del router
	//falta un pequeño paso, si damos la direccion accedemos al router, falta redirigir las peticiones al ordenador
	//que estoy utilizando, redireccion de puertos (buscarlo en internet)

	private String host;
	private int port;
	private List<GameObserver> observers;
	private Piece localPiece;
	private GameFactory gameFactory;
	private Connection connectionToServer;
	private boolean gameOver;

	public GameClient(String host, int port) {
		super(null, null);
		this.host = host;
		this.port = port;
		this.gameOver = false;
		this.observers = new ArrayList<>();
		this.connectionToServer = null;
		try {
			connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public GameFactory getGameFactory() {
		return this.gameFactory;
	}

	public Piece getPlayerPiece() {
		return this.localPiece;

	}

	public void start(){
		this.observers.add(
				new GameObserver(){

					public void onGameStart(Board board, String gameDesc, List<Piece> pieces, Piece turn){}

					public void onGameOver(Board board, State state,	Piece winner) {
						gameOver = true;
						//onGameOver(board, state, winner);
					}

					public void onMoveStart(Board board, Piece turn) {}

					public void onMoveEnd(Board board, Piece turn,	boolean success) {}

					public void onChangeTurn(Board board, Piece turn) {}

					public void onError(String msg) {	}
				}); 

		gameOver = false;
		while (!gameOver){
			try{
				Response res = null;
				try {
					res = (Response) connectionToServer.getObject();
					
					
				} catch (IOException e) {
					
				}
				for(GameObserver o: observers){
					res.run(o);
				}
			}catch(ClassNotFoundException e){

			}
		}
	}
	public void makeMove(Player p) {
		forwardCommand(new PlayCommand(p));
	}
	public void stop() {
		forwardCommand(new QuitCommand());
	}
	public void restart() {
		forwardCommand(new RestartCommand());
	}
	private void forwardCommand(Command cmd) {
		// if the game is over do nothing, otherwise
		// send the object cmd to the server
		try {
			if (!gameOver){
				connectionToServer.sendObject(cmd);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Enviar el String a connect para expresar su interés en jugar
	//Leer el primer objeto en la respuesta del servidor
	private void connect() throws Exception {
		try {
				connectionToServer = new Connection(new Socket(this.host, this.port));
				connectionToServer.sendObject(new String("Connect")); 
				Object response = connectionToServer.getObject();
				if (response instanceof Exception) {
					throw (Exception) response;
				}
				try {
					gameFactory = (GameFactory) connectionToServer.getObject();
					localPiece = (Piece) connectionToServer.getObject();
				} catch (Exception e) {
					throw new GameError("Unknown server response: "+e.getMessage());
				}
		
		}catch (UnknownHostException e) {
			
		} catch (IOException e) {
		
		}
	}
	public void addObserver(GameObserver o) {
		observers.add(o);
	}

	public void removeObserver(GameObserver o) {
		observers.remove(o);
	}
	
}
