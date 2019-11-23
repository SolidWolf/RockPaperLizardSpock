import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



class RPLSTest {
	Client client;
	Client client2;
	Client client3;
	Client client4;

	@BeforeEach 
	void init() {
		client = new Client(null, "127.0.0.1", 5555);
		client2 = new Client(null, "127.0.0.2", 6);
		client3 = new Client(null, "127.0.0.3", 918);
		client4 = new Client(null, "127.0.0.4", 289);
	}

	@Test
	void testClient(){
		assertNotNull(client, "client was not initialized");
		assertNotNull(client2, "client was not initialized");
		assertNotNull(client3, "client was not initialized");
		assertNotNull(client4, "client was not initialized");
	}
	
	@Test
	void testClientClassName() {
		assertEquals("Client", client.getClass().getName(), "incorrect class name");
		assertEquals("Client", client2.getClass().getName(), "incorrect class name");
		assertEquals("Client", client3.getClass().getName(), "incorrect class name");
		assertEquals("Client", client4.getClass().getName(), "incorrect class name");
	}
	
	@Test
	void testClientConstructor() {
		assertEquals("127.0.0.1", client.ip, "Incorrect IP address");
		assertEquals(5555, client.port, "Incorrect port");
		assertEquals("127.0.0.2", client2.ip, "Incorrect IP address");
		assertEquals(6, client2.port, "Incorrect port");
		assertEquals("127.0.0.3", client3.ip, "Incorrect IP address");
		assertEquals(918, client3.port, "Incorrect port");
		assertEquals("127.0.0.4", client4.ip, "Incorrect IP address");
		assertEquals(289, client4.port, "Incorrect port");
	}
	
	@Test
	void testGameInfo1() {
		GameInfo gameInfo = new GameInfo();
		assertEquals("GameInfo", gameInfo.getClass().getName(), "The class name does not match");
	}

	@Test
	void testGameInfo2() {
		GameInfo gameInfo = new GameInfo();
		assertNotNull(gameInfo,"not initialized");
	}

	@Test
	void testGameInfoVar1() {
		GameInfo gameInfo = new GameInfo();
		assertEquals("N/A",gameInfo.roundWinner,"wrong data value");
		assertEquals("N/A",gameInfo.gameWinner,"wrong data value");
		assertEquals(false,gameInfo.winnerFound,"wrong data value");
	}

	@Test
	void testGameInfoVar2() {
		GameInfo gameInfo = new GameInfo();
		assertEquals(false,gameInfo.isMessage,"wrong data value");
		assertEquals(false,gameInfo.isDisconnect,"wrong data value");
		assertEquals("N/A",gameInfo.message,"wrong data value");
		assertEquals(0,gameInfo.playerCount,"wrong data value");
	}

	@Test
	void testGameInfoVar3() {
		GameInfo gameInfo = new GameInfo();
		assertEquals(false,gameInfo.p1Played,"wrong data value");
		assertEquals(false,gameInfo.p2Played,"wrong data value");
		assertEquals(false,gameInfo.updateServerUI,"wrong data value");
		assertEquals(false,gameInfo.updateClientUI,"wrong data value");
	}

	@Test
	void testGameInfoVar4() {
		GameInfo gameInfo = new GameInfo();
		assertEquals(false,gameInfo.newPlayer,"wrong data value");
		assertEquals(false,gameInfo.isChallenge,"wrong data value");
		assertEquals(false,gameInfo.isPlayed,"wrong data value");
		assertEquals(false,gameInfo.challengeAccepted,"wrong data value");
	}

	@Test
	void testPlayerInfo() {
		GameInfo gameInfo = new GameInfo();
		assertEquals(0,gameInfo.playerinfo.size(),"wrong size");
	}
}
