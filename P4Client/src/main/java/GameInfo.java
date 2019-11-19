import java.io.Serializable;
import java.util.ArrayList;


public class GameInfo implements Serializable {
			
	private static final long serialVersionUID = 1L;
	
	//Variable to store what the players play
	int p1Points = 0;
	int p2Points = 0;
		
	//Variable to store what the player currently played
	String p1Plays = "N/A";
	String p2Plays = "N/A";
		
	//Stores who won the round
	String roundWinner = "N/A";
	
	//Stores who won the game
	String gameWinner = "N/A";
		
	//Store if 2 players are connected
	boolean have2players = false;
		
	//Store if either player won 
	boolean winnerFound = false;
		
	//Store if what is sent is 
	boolean isMessage = false;
	
	//Check if any player disconnected
	boolean isDisconnect = false;
	
	//Store which player disconnected
	int disconnectID;
			
	//Store the message
	String message = "N/A";;
		
	//Store which player it is
	int playerID;
		
	int playerCount = 0;
		
	//Variable to store if the players has played
	boolean p1Played = false;
	boolean p2Played = false;
	
	//Check if the server UI should be updated
	boolean updateServerUI = false;
		
	//Check if the client UI should be updated
	boolean updateClientUI = false;
	
	//Store if p1 or p2 chose to play again
	boolean p1PlayAgain = false;
	boolean p2PlayAgain = false;
	
	//Store if the player just connected
	boolean newPlayer = false;
	
	//Arraylist to store player action
	ArrayList<PlayerInfo> playerinfo = new ArrayList<PlayerInfo>(); 
	
	//Determine if what is sent is a challenge
	boolean isChallenge = false;
		
	//Determine if what is sent is a play selection
	boolean isPlayed = false;
		
	//Determine who sent the Object
	int sentBy;
	
	//Determine who the challenge is for
	int challengeFor;
	
	//Check if the challenge was accepted
	boolean challengeAccepted = false;
		
	//Store the player information
	class PlayerInfo implements Serializable {
		
		private static final long serialVersionUID = 1L;
	
		boolean hasDisconnected = false;
		boolean hasPlayed = false;
		boolean isPlaying = false;
		int clientID;
		String playerPlayed = "N/A";
		
		//Constructor
		PlayerInfo(int clientID) {
			this.clientID = clientID;
		}
	}
	
}
