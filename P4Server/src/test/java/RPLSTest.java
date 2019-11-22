import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



class RPLSTest {
	Client c;
	static GameLogic gameLogic;
	static RPLS testR;
	static GameInfo testGameInfo;
	static Server testS;


	@BeforeEach
	void init() {
		c = new Client(null, "127.0.0.1", 6);
		gameLogic = new GameLogic();
		testR = new RPLS();
		testGameInfo = new GameInfo();
		testS = new Server(null, 0);
	}

	@Test
	void testClientClassName() {
		assertEquals("Client", c.getClass().getName(), "The class name does not match");
	}

	@Test
	void testClientConstructor() {
		assertEquals("127.0.0.1", c.ip, "The ip address does not match");
		assertEquals(6, c.port, "The port does not match");
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
	void testHave2players() {
		assertFalse(testGameInfo.have2players, "have2players should be false");
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
	void testp2Played(){
		assertFalse(testGameInfo.p2Played, "p2Played should be false");
	}

	@Test
	void testp1Played() {
		assertFalse(testGameInfo.p1Played, "p1Played should be false");
	}

	@Test
	void testClients() {
		assertEquals(testS.clients.size(),0,"Clients array list should be 0");

	}


}
