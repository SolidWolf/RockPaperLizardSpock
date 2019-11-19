import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



class RPLSTest {
	
	Client c;

	@BeforeEach 
	void init() {
		c = new Client(null, "127.0.0.1", 6);
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


}
