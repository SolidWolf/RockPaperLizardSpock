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
	
	private int newIDNumber = 0;
	
	//Receive information from the client
	private Consumer<Serializable> callback;
	
	ServerThread mainServer;
	
	//The port the server is running on
	int serverPort;
	
	GameInfo gameState;
		
	GameLogic gameLogic = new GameLogic();
	
	//Constructor for the server class
	Server(Consumer<Serializable> call, int port) {
		callback = call;
		serverPort = port;
		gameState = new GameInfo();			//Get a new gameInfo state
		mainServer = new ServerThread();
		mainServer.start();
	}
	
	class ServerThread extends Thread {
		public void run() {
			try(ServerSocket serverSocket = new ServerSocket(serverPort)) {
				System.out.println("Server waiting at port:" + serverPort);
				
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
		
		//Send the game state to all the clients on the server
		public void updatePlayingClients(GameInfo state, int client1, int client2) {
			int numClients = clients.size();
			for(int i = 0; i < numClients; i++) {
				ClientThread c = clients.get(i);
				
				if (c.threadNum == client1 || c.threadNum == client2) {
					try {
						state.playerID = c.threadNum;			//Get the id of the thread
						c.output.reset();
						c.output.writeObject(state);
					}
					catch(Exception e) {}
				}
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
						
			gameState.isMessage = true;
			gameState.newPlayer = true;
			gameState.p1PlayAgain = false;
			gameState.p2PlayAgain = false;
			gameState.isDisconnect = false;
			gameState.playerCount = clients.size();
			if (clients.size() < 2) {
				gameState.message = "Waiting for opponent to connect...";
				gameState.have2players = false;
			}
			else {
				gameState.message = "Opponent Found. Select what to play";		
				gameState.have2players = true;
			}
			updateClients(gameState);
			callback.accept(gameState);
			
			while(true) {
				try {
					gameState = (GameInfo) input.readObject();
					
					gameState.newPlayer = false;
					gameState.isMessage = false;
					gameState.updateClientUI = false;
					if (gameState.isChallenge == true) {
						
						int challengeForIndex = 0;
						int sentFromIndex = 0;
						
						//Remove the ID from the playerinfo arrayList
						for (int i = 0; i < gameState.playerinfo.size(); i++) {
							if (gameState.playerinfo.get(i).clientID == gameState.challengeFor) 
								challengeForIndex = i;
							else if (gameState.playerinfo.get(i).clientID == gameState.sentBy)
								sentFromIndex = i;
						}
						
						gameState.playerinfo.get(challengeForIndex).isPlaying = true;
						gameState.playerinfo.get(sentFromIndex).isPlaying = true;
						
						updateClients(gameState);
					}
					/*		
					if (gameState.p1Played == true && gameState.p2Played == true) {
						
						gameState.roundWinner = gameLogic.roundWinner(gameState.p1Plays, gameState.p2Plays);
						
						//Update point if necessary
						if (gameState.roundWinner.equals("p1")) 
							gameState.p1Points = gameState.p1Points + 1;
						else if (gameState.roundWinner.equals("p2"))
							gameState.p2Points = gameState.p2Points + 1;
						
						//Get who won the game 
						if (gameState.winnerFound = gameLogic.winnerFound(gameState.p1Points, gameState.p2Points) == true) 
							gameState.gameWinner = gameLogic.whoWon(gameState.p1Points, gameState.p2Points);
						
						//Change is the player played back to false
						gameState.p1Played = false;
						gameState.p2Played = false;
						
						//Allow GUIs to update
						gameState.updateServerUI = true;
						gameState.updateClientUI = true;
						
						callback.accept(gameState);			//Send to the application thread
					}
					else if (gameState.p1PlayAgain == true || gameState.p2PlayAgain == true) {
						
						gameState.isDisconnect = false;
						
						callback.accept(gameState);
						
						if (gameState.p1PlayAgain == true && gameState.p2PlayAgain == true) {
							gameState = new GameInfo();
							gameState.isMessage = true;
							gameState.playerCount = clients.size();
							gameState.have2players = true;
							gameState.message = "Opponent has reconnected!";
						}
						else {
							gameState.isMessage = true;
							gameState.message = "Waiting for opponent...";
						}
					} */
					updateClients(gameState);

				} catch(Exception e) {
										
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
