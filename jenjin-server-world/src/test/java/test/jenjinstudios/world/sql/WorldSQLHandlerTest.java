package test.jenjinstudios.world.sql;

import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.sql.WorldSQLHandler;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Test the WorldSQLHandler.
 * @author Caleb Brinkman
 */
public class WorldSQLHandlerTest
{
	/**
	 * Test logging the player into and out of the world, including updating coordinates.
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testLoginLogout() throws Exception {
		WorldSQLHandler worldSQLHandler = new WorldSQLHandler("localhost", "jenjin_test", "jenjin_user",
				"jenjin_password");

		assertTrue(worldSQLHandler.isConnected());

		Actor player = worldSQLHandler.logInPlayer("TestAccount1", "testPassword");
		Vector2D origin = player.getVector2D();
		Vector2D secondVector = new Vector2D(50, 50);

		assertEquals(origin, player.getVector2D());

		player.setVector2D(secondVector);
		assertTrue(worldSQLHandler.logOutPlayer(player));

		player = worldSQLHandler.logInPlayer("TestAccount1", "testPassword");
		assertEquals(secondVector, player.getVector2D());

		player.setVector2D(origin);
		assertTrue(worldSQLHandler.logOutPlayer(player));

		player = worldSQLHandler.logInPlayer("TestAccount1", "testPassword");
		assertEquals(origin, player.getVector2D());

		assertTrue(worldSQLHandler.logOutPlayer(player));
	}
}
