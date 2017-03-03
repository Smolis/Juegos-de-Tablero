import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleServer {

	public void startServer() throws IOException {
		
		ServerSocket server = new ServerSocket(2000);
		System.out.println("Waiting for clients...");
		int numConnections = 0;
		
		while(numConnections < 5){
			numConnections++;
			Socket s = server.accept();
			handleRequestInThread(s);
		}
		server.close();
		System.out.println("Server not accepting any more connections");
		
	}

	private void handleRequestInThread(final Socket s) {
		new Thread(){
			public void run(){
				try{
					handleRequest(s);
				}
				catch(IOException | ClassNotFoundException e){}
			}
		}.start();
		
	}

	private void handleRequest(Socket s) throws IOException, ClassNotFoundException {
		int i;
		ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
		ObjectInputStream in = new ObjectInputStream(s.getInputStream());
		do{
			MyNumber n = (MyNumber)in.readObject();
			i = n.getNumber();
			System.out.println("Received: " + i);
			out.writeObject(new MyNumber(complexCalculation(i)));
		} while(i != -1);
		in.close();
	}
	
	private int complexCalculation(int i){
		return 2*i;
	}
	
}
