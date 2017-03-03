import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class SimpleClient {

	public void sendToServer() throws UnknownHostException, IOException, InterruptedException, ClassNotFoundException{
		
		Random r = new Random();
		Socket s = new Socket("localhost", 2000);
		ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
		ObjectInputStream in = new ObjectInputStream(s.getInputStream());
		
		for(int i = 0; i < 10; i++){
			out.writeObject(new MyNumber(r.nextInt(1000)));
			out.flush();
			out.reset();
			MyNumber n = (MyNumber)in.readObject();
			System.out.println("Result received: " + n.getNumber());
			Thread.sleep(300);
		}
		
		out.writeObject(new MyNumber(-1));
		out.flush();
		out.reset();
		in.close();
		s.close();
	}
	
		
}
