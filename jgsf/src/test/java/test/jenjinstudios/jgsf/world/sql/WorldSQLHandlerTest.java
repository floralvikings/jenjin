package test.jenjinstudios.jgsf.world.sql;

import com.jenjinstudios.math.Vector2D;
import com.jenjinstudios.sql.WorldSQLHandler;
import com.jenjinstudios.world.Actor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Test the WorldSQLHandler.
 *
 * @author Caleb Brinkman
 */
public class WorldSQLHandlerTest
{
	/**
	 * Test logging the player into and out of the world, including updating coordinates.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testLoginLogout() throws Exception
	{
		WorldSQLHandler worldSQLHandler = new WorldSQLHandler("localhost", "jenjin_test", "jenjin_user",
				"jenjin_password");

		assertTrue(worldSQLHandler.isConnected());

		Actor player = worldSQLHandler.logInPlayer("TestAccount01", "testPassword");
		Vector2D origin = new Vector2D(0, 0);
		Vector2D secondVector = new Vector2D(50, 50);

		assertEquals(origin, player.getVector2D());

		player.setVector2D(secondVector);
		assertTrue(worldSQLHandler.logOutPlayer(player));

		player = worldSQLHandler.logInPlayer("TestAccount01", "testPassword");
		assertEquals(secondVector, player.getVector2D());

		player.setVector2D(origin);
		assertTrue(worldSQLHandler.logOutPlayer(player));

		player = worldSQLHandler.logInPlayer("TestAccount01", "testPassword");
		assertEquals(origin, player.getVector2D());

		assertTrue(worldSQLHandler.logOutPlayer(player));
	}
}
