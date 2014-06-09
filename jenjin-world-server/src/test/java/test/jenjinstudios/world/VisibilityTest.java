package test.jenjinstudios.world;

import com.jenjinstudios.world.*;
import com.jenjinstudios.world.math.Vector2D;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Caleb Brinkman
 */
public class VisibilityTest extends WorldServerTest
{
	private static final Logger LOGGER = Logger.getLogger(VisibilityTest.class.getName());

	/**
	 * Test the actor visibility after player and actor movement.
	 * @throws Exception If there's an exception.
	 */
	@Test(timeOut = 10000)
	public static void testActorVisibility() throws Exception {
		WorldServer server = WorldServerTest.initWorldServer(WorldServerTest.port);
		WorldClient client = WorldServerTest.initWorldClient(WorldServerTest.port);

		double edge = Location.SIZE * (SightedObject.VIEW_RADIUS + 1);
		Vector2D startPos = new Vector2D(0, edge + 1);
		// Should be (0, 109)
		Vector2D targetPos = new Vector2D(0, edge - 1);
		Actor serverActor = new Actor("TestActor");
		serverActor.setVector2D(startPos);
		server.getWorld().addObject(serverActor);

		LOGGER.log(Level.INFO, "Moving serverActor to {0}", targetPos);
		WorldServerTest.moveServerActorToVector(serverActor, targetPos);

		LOGGER.log(Level.INFO, "Asserting that clientPlayer can see serverPlayer");
		WorldObject clientActor = client.getPlayer().getVisibleObjects().get(serverActor.getId());
		Assert.assertEquals(1, client.getPlayer().getVisibleObjects().size());
		Assert.assertNotNull(clientActor);
		Thread.sleep(500);
		WorldServerTest.assertClientAndServerInSamePosition(serverActor, clientActor);

		LOGGER.log(Level.INFO, "Moving serverActor out of visible range.");
		WorldServerTest.moveServerActorToVector(serverActor, startPos);
		Assert.assertEquals(0, client.getPlayer().getVisibleObjects().size());

		LOGGER.log(Level.INFO, "Moving clientPlayer into visible range.");
		WorldServerTest.movePlayerToVector(client, server, new Vector2D(0, Location.SIZE + 1));
		Assert.assertEquals(1, client.getPlayer().getVisibleObjects().size());
		clientActor = client.getPlayer().getVisibleObjects().get(serverActor.getId());
		WorldServerTest.assertClientAndServerInSamePosition(serverActor, clientActor);

		LOGGER.log(Level.INFO, "Moving clientPlayer back to origin.");
		WorldServerTest.movePlayerToVector(client, server, Vector2D.ORIGIN);
		Assert.assertEquals(0, client.getPlayer().getVisibleObjects().size());

		WorldServerTest.tearDown(client, server);
	}
}
