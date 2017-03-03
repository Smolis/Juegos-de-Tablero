import java.io.IOException;

public class MainServer {

	public static void main(String[] args) {

		SimpleServer server = new SimpleServer();
		try {
			server.startServer();
		} catch (IOException e) {
			System.out.println("An error ocurred in the server");
			e.printStackTrace();
		}

	}

}
