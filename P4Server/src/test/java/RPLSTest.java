import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



class RPLSTest {
	static GameLogic gameLogic;
	static GameInfo testGameInfo;
	static Server testS;


	@BeforeEach
	void init() {
		gameLogic = new GameLogic();
		testGameInfo = new GameInfo();
		testS = new Server(null, 0);
	}

	@Test
	void testGameInfo() {
		GameInfo gameInfo = new GameInfo();
		assertEquals("GameInfo", gameInfo.getClass().getName(), "The class name does not match");
	}

	@Test
	void testChallengeAccepted() {
		assertFalse(testGameInfo.challengeAccepted, "challengeAccepted should be false");
	}

	@Test
	void testnewPlayer() {
		assertFalse(testGameInfo.newPlayer,"newPlayer should be false;");
	}

	@Test
	void testPlayerInfo() {
		assertFalse(testGameInfo.isPlayed, "isPlayed should be false");
	}

	@Test
	void testUpdateServerUI() {
		assertFalse(testGameInfo.updateServerUI, "updateServerUI should be false");
	}

	@Test
	void testClients() {
		assertEquals(testS.clients.size(),0,"Clients array list should be 0");

	}


}
