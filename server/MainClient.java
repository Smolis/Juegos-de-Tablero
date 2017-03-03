import java.io.IOException;

public class MainClient {

	public static void main(String[] args){
		SimpleClient client = new SimpleClient();
		try {
			client.sendToServer();
		} catch (IOException | InterruptedException | ClassNotFoundException e) {
			System.out.println("Error in the connection");
			e.printStackTrace();
		}
	}
	
}
