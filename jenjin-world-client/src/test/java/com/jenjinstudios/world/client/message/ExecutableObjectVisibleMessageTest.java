package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.math.Vector2D;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Caleb Brinkman
 */
public class ExecutableObjectVisibleMessageTest extends WorldClientExecutableMessageTest
{
	@Test(timeOut = 5000)
	@Override
	public void testMessageExecution() throws Exception {
		Message actorVisibleMessage = messageRegistry.createMessage("ObjectVisibleMessage");
		actorVisibleMessage.setArgument("name", "a1b2c3d4e5f6890");
		actorVisibleMessage.setArgument("id", 100);
		actorVisibleMessage.setArgument("resourceID", 100);
		actorVisibleMessage.setArgument("xCoordinate", 1.0);
		actorVisibleMessage.setArgument("yCoordinate", 1.0);
		inStreamReadMessage.thenReturn(actorVisibleMessage, blankMessageSpam);

		worldClient.blockingStart();
		worldClient.sendBlockingWorldFileRequest();
		worldClient.sendBlockingLoginRequest();
		Thread.sleep(500); // Sleep to allow client to "catch up"

		WorldObject worldObject = worldClient.getWorld().getObject(100);
		assertNotNull(worldObject);
		assertEquals(worldObject.getResourceID(), 100);
		assertEquals(worldObject.getVector2D(), new Vector2D(1.0, 1.0));
	}
}
