import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

public class Client extends Thread{
	
	//Socket for the client
	Socket socketClient;
	
	//Data sent and received
	ObjectOutputStream output;
	ObjectInputStream input;
	
	//Store the user selected ip and the port
	String ip;
	int port;
	
	GameInfo gameState = new GameInfo();
	private Consumer<Serializable> callback;

	Client(Consumer<Serializable> call, String ip, int port) {
		callback = call;
		this.ip = ip;
		this.port = port;
	}
	
	public void run() {
		try {
			socketClient= new Socket(ip,port);
		    output = new ObjectOutputStream(socketClient.getOutputStream());
		    input = new ObjectInputStream(socketClient.getInputStream());
		    socketClient.setTcpNoDelay(true);
		}
		catch(Exception e) {}
		
		while(true) {
			 
			try {
				this.gameState = (GameInfo) input.readObject();
				callback.accept(gameState); 
			}
			catch(Exception e) {}
		}
	
    }
	
	public void send(GameInfo gameState) {
		
		try {
			output.writeObject(gameState);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
