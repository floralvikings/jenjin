package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.client.ClientActor;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Caleb Brinkman
 */
public class ExecutableStateChangeMessageTest extends WorldClientExecutableMessageTest
{
	@Test(timeOut = 5000)
	@Override
	public void testMessageExecution() throws Exception {
		ClientActor clientActor = new ClientActor(100, "Bob");

		worldClient.blockingStart();
		worldClient.sendBlockingWorldFileRequest();
		worldClient.sendBlockingLoginRequest();
		worldClient.getWorld().addObject(clientActor, 100);

		Message stateChangeMessage = messageRegistry.createMessage("StateChangeMessage");
		stateChangeMessage.setArgument("id", 100);
		stateChangeMessage.setArgument("relativeAngle", Angle.IDLE);
		stateChangeMessage.setArgument("absoluteAngle", Math.PI);
		stateChangeMessage.setArgument("timeOfChange", System.nanoTime());
		stateChangeMessage.setArgument("xCoordinate", Math.PI);
		stateChangeMessage.setArgument("yCoordinate", Math.PI);
		inStreamReadMessage.thenReturn(stateChangeMessage, blankMessageSpam);
		Thread.sleep(500); // Sleep to allow client to "catch up"

		assertEquals(clientActor.getVector2D(), new Vector2D(Math.PI, Math.PI));
		assertEquals(clientActor.getAngle(), new Angle(Math.PI, Angle.IDLE));
	}
}
