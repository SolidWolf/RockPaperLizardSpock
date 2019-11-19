
public class GameLogic {
	
	//Determine who won the round
	String roundWinner (String p1Played, String p2Played) {
		
		String winner;			//String to store who won
		if (p1Played.equals("rock")) {
			if (p2Played.equals("lizard") || p2Played.equals("scissors")) {
				winner = "p1";
			}
			else if (p2Played.equals("paper") || p2Played.equals("spock")) {
				winner = "p2";
			}
			else {
				winner = "draw";
			}
		}
		else if (p1Played.equals("lizard")) {
			if (p2Played.equals("paper") || p2Played.equals("spock")) {
				winner = "p1";
			}
			else if (p2Played.equals("rock") || p2Played.equals("scissors")) {
				winner = "p2";
			}
			else {
				winner = "draw";
			}
		}
		else if (p1Played.equals("spock")) {
			if (p2Played.equals("rock") | p2Played.equals("scissors")) {
				winner = "p1";
			}
			else if (p2Played.equals("paper") || p2Played.equals("lizard")) {
				winner = "p2";
			}
			else {
				winner = "draw";
			}	
		}
		else if (p1Played.equals("scissors")) {
			if (p2Played.equals("paper") || p2Played.equals("lizard")) {
				winner = "p1";
			}
			else if (p2Played.equals ("rock") || p2Played.equals("spock")) {
				winner = "p2";
			}
			else {
				winner = "draw";
			}
		}
		else {
			if (p2Played.equals("rock") || p2Played.equals("spock")) {
				winner = "p1";
			}
			else if (p2Played.equals("lizard") || p2Played.equals("scissors")) {
				winner = "p2";
			}
			else {
				winner = "draw";
			}
		}
		
		return winner;			//Return who won the round
	}
	
	//Check if any of the players won
	boolean winnerFound (int p1Points, int p2Points) {
		
		if (p1Points >= 1 || p2Points >= 1) {
			return true;
		}
	
		return false;
	}
	
	//Check who won the game
	String whoWon (int p1Points, int p2Points) {
		
		String winner;			//Store the winner
		
		//Check which player has 1 or more points
		if (p1Points >= 1) {
			winner = "p1";
		}
		else {
			winner = "p2";
		}
		
		return winner;			//Return the winner
	}
	
	//Check if 2 players are found
	boolean playersFound (int players) {
		
		//Check if there are 2 or more players
		if (players >= 2) {
			return true;
		}
		return false;
	}
	
}
