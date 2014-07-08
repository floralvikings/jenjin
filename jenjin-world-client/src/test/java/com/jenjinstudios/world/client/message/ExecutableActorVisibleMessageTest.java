package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.client.ClientActor;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Caleb Brinkman
 */
public class ExecutableActorVisibleMessageTest extends WorldClientExecutableMessageTest
{
	@Override
	public void testMessageExecution() throws Exception {
		Message actorVisibleMessage = messageRegistry.createMessage("ActorVisibleMessage");
		actorVisibleMessage.setArgument("name", "a1b2c3d4e5f6890");
		actorVisibleMessage.setArgument("id", 100);
		actorVisibleMessage.setArgument("resourceID", 100);
		actorVisibleMessage.setArgument("xCoordinate", 1.0);
		actorVisibleMessage.setArgument("yCoordinate", 1.0);
		actorVisibleMessage.setArgument("relativeAngle", Angle.IDLE);
		actorVisibleMessage.setArgument("absoluteAngle", 0.0);
		actorVisibleMessage.setArgument("timeOfVisibility", 100l);
		inStreamReadMessage.thenReturn(actorVisibleMessage, blankMessageSpam);

		worldClient.blockingStart();
		worldClient.sendBlockingWorldFileRequest();
		worldClient.sendBlockingLoginRequest();
		Thread.sleep(100); // Sleep to allow client to "catch up"

		ClientActor clientActor = (ClientActor) worldClient.getWorld().getObject(100);
		assertNotNull(clientActor);
		assertEquals(clientActor.getResourceID(), 100);
		assertEquals(clientActor.getVector2D(), new Vector2D(1.0, 1.0));
		assertEquals(clientActor.getAngle(), new Angle(0.0, Angle.IDLE));
	}
}
