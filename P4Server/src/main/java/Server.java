import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;


public class Server {
	
	//Store the clients in the arraylist
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	
	private ArrayList<Integer> reuseID = new ArrayList<Integer>();
	
	private int newIDNumber = 1;
	
	//Receive information from the client
	private Consumer<Serializable> callback;
	
	ServerThread mainServer;
	
	//The port the server is running on
	int serverPort;
	
	GameInfo gameState;
		
	GameLogic gameLogic = new GameLogic();
	
	//Constructor for the server class
	Server(Consumer<Serializable> call, int port) {
		callback = call;					//Set the call
		serverPort = port;					//Get the port
		gameState = new GameInfo();			//Get a new gameInfo state
		mainServer = new ServerThread();	//Create the server thread
		mainServer.start();					//Start the server thread
	}
	
	class ServerThread extends Thread {
		public void run() {
			try(ServerSocket serverSocket = new ServerSocket(serverPort)) {
				
				while(true) {
					
					ClientThread client = new ClientThread(serverSocket.accept());
					clients.add(client);
					client.start();
					
					//Check which value was used for the ID
					if (reuseID.size() != 0)
						reuseID.remove(0);			//Remove from the arraylist
					else
						newIDNumber++;				//Increment ID
				}
				
			}catch(Exception e) {
				
			}
		}
	}
	
	class ClientThread extends Thread {
		
		//Set the socket to connect 
		Socket connection;
		
		//Set the input and the output streams
		ObjectInputStream input;
		ObjectOutputStream output;
		
		//Determine which thread it is
		int threadNum;
		
		ClientThread(Socket server) {

			this.connection = server;
			if (reuseID.size() == 0) 
				this.threadNum = newIDNumber;
			else 
				this.threadNum = reuseID.get(0);
		}
		
		//Send the game state to all the clients on the server
		public void updateClients(GameInfo state) {
			int numClients = clients.size();
			for(int i = 0; i < numClients; i++) {
				ClientThread c = clients.get(i);
				try {
					state.playerID = c.threadNum;				//Get the id of the thread
					c.output.reset();
					c.output.writeObject(state);
				}
				catch(Exception e) {}
			}
		}
		
		public void run() {
				
			try {
				//Initialize the input and output streams
				input = new ObjectInputStream(connection.getInputStream());
				output = new ObjectOutputStream(connection.getOutputStream());
				connection.setTcpNoDelay(true);	
			}catch( Exception e) {}
			
			//Create a new instance of PlayerInfo with the ID and add to the arraylist
			GameInfo.PlayerInfo newPlayerInfo = gameState.new PlayerInfo(threadNum); 
			gameState.playerinfo.add(newPlayerInfo);
						
			//gameState.isMessage = true;
			gameState.newPlayer = true;
			gameState.isDisconnect = false;
			gameState.playerCount = clients.size();
			updateClients(gameState);
			callback.accept(gameState);
			
			while(true) {
				
				try {
						
					gameState = (GameInfo) input.readObject();
					
					synchronized(gameState) {
					
						gameState.newPlayer = false;
						gameState.isMessage = false;
						gameState.updateClientUI = false;
						gameState.updateServerUI = false;
						gameState.isDisconnect = false;
		
						int challengeForIndex = 0;
						int sentFromIndex = 0;
						
						//Get the ID from the playerinfo arrayList
						for (int i = 0; i < gameState.playerinfo.size(); i++) {
							if (gameState.playerinfo.get(i).clientID == gameState.sentFor)
								challengeForIndex = i;
							else if (gameState.playerinfo.get(i).clientID == gameState.sentBy)
								sentFromIndex = i;
						}
						
						//Check if what is sent is a challenge
						if (gameState.isChallenge == true) {
							
							//Set the isPlaying values of the player to true
							gameState.playerinfo.get(challengeForIndex).isPlaying = true;
							gameState.playerinfo.get(sentFromIndex).isPlaying = true;
							gameState.challengeAccepted = true;
	
						}
						
						else if (gameState.isPlayed == true) {
							if(gameState.playerinfo.get(challengeForIndex).hasPlayed == true || gameState.playerinfo.get(sentFromIndex).hasPlayed == true) {
								gameState.challengeAccepted = false;
							}
							
							//check if both clients have picked a move so to calculate winner
							if(gameState.playerinfo.get(challengeForIndex).hasPlayed == true && gameState.playerinfo.get(sentFromIndex).hasPlayed == true) {
								
								//calculate who won the round
								gameState.roundWinner = gameLogic.roundWinner(gameState.playerinfo.get(challengeForIndex).playerPlayed, gameState.playerinfo.get(sentFromIndex).playerPlayed);
								
								//Check which player won the game
								if(gameState.roundWinner.equals("p1")) {
									gameState.roundWinner = String.valueOf(gameState.playerinfo.get(challengeForIndex).clientID);
								}
								else if(gameState.roundWinner.equals("p2")) {
									gameState.roundWinner = String.valueOf(gameState.playerinfo.get(sentFromIndex).clientID);
								}
								
								//Allow GUIs to update
								gameState.updateServerUI = true;
								gameState.updateClientUI = true;
								gameState.playerinfo.get(challengeForIndex).hasPlayed = false;
								gameState.playerinfo.get(sentFromIndex).hasPlayed = false;
								gameState.playerinfo.get(challengeForIndex).isPlaying = false;
								gameState.playerinfo.get(sentFromIndex).isPlaying = false;
							}
						}
						
						//Update the clients and the server
						updateClients(gameState);
						callback.accept(gameState);
					}
					
				} catch(Exception e) {
								
					synchronized(gameState) {

						//Remove the client from the arrayList
						clients.remove(this);
						
						//Add the ID to the list of available used IDs
						reuseID.add(this.threadNum);	
						
						//Remove the ID from the playerinfo arrayList
						for (int i = 0; i < gameState.playerinfo.size(); i++) {
							if (gameState.playerinfo.get(i).clientID == this.threadNum) {
								gameState.playerinfo.remove(i);
								break;
							}
						}
						
						//Determine who disconnected and sent it to the GUI
						gameState.newPlayer = false;
						gameState.disconnectID = this.threadNum;
						gameState.isDisconnect = true;
						
						gameState.playerCount = clients.size();		//update number of players
						
						//Update the server GUI
						callback.accept(gameState);	
						
						//Update the clients
						updateClients(gameState);
										
				    	break;				//End the loop
					}
				}
			}
		}
	}
	
}
