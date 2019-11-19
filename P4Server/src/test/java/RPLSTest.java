import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;



class RPLSTest {
	
	static GameLogic gameLogic;
	
	@BeforeAll
	static void init() {
		gameLogic = new GameLogic();
	}

	@Test
	void testServerName() {
		Server s = new Server(null, 5555);
		assertEquals("Server", s.getClass().getName(), "Class name does not match");
	}
	
	@Test
	void testServerConstructor() {
		Server s = new Server(null, 5555);
		assertEquals(5555, s.serverPort, "Port does not match the constructor");
	}
	
	@Test
	void testGameLogicRoundWinner() {
		assertEquals("draw", gameLogic.roundWinner("paper", "paper"), "Winner was not draw");
		assertEquals("p1", gameLogic.roundWinner("paper", "rock"),"Winner was not p1");
		assertEquals("p2", gameLogic.roundWinner("spock", "lizard"),"Winner was not p2");
	}
	
	@Test
	void testServerWinnerFound() {
		assertEquals(false, gameLogic.winnerFound(2, 1), "Winner was found");
		assertEquals(true, gameLogic.winnerFound(2, 3), "Winner was not found");
	}
	
	@Test
	void testServerWhoWon() {
		assertEquals("p1", gameLogic.whoWon(3, 1), "p2 won");
		assertEquals("p2", gameLogic.whoWon(2, 3), "p1 won");
	}
	
	@Test
	void testPlayerFound() {
		assertEquals(true, gameLogic.playersFound(2), "Player not found");
		assertEquals(false, gameLogic.playersFound(1), "Player found");
	}

}
