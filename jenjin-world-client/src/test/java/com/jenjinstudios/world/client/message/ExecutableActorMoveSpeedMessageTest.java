package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.client.ClientActor;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class ExecutableActorMoveSpeedMessageTest extends WorldClientExecutableMessageTest
{
	@Test(timeOut = 5000)
	@Override
	public void testMessageExecution() throws Exception {
		Message actorMoveSpeedMessage = messageRegistry.createMessage("ActorMoveSpeed");
		actorMoveSpeedMessage.setArgument("moveSpeed", 123.45);
		inStreamReadMessage.thenReturn(actorMoveSpeedMessage, blankMessageSpam);

		worldClient.blockingStart();
		worldClient.sendBlockingWorldFileRequest();
		worldClient.sendBlockingLoginRequest();
		Thread.sleep(500); // Sleep to allow client to "catch up"

		Assert.assertEquals(ClientActor.MOVE_SPEED, 123.45);
	}
}
