package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.world.client.ClientActor;
import com.jenjinstudios.world.client.WorldClient;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class ExecutableActorMoveSpeedMessageTest
{
	@Test
	public void testMessageExecution() throws Exception {
		MessageRegistry messageRegistry = MessageRegistry.getInstance();
		Message actorMoveSpeedMessage = messageRegistry.createMessage("ActorMoveSpeed");
		actorMoveSpeedMessage.setArgument("moveSpeed", 123.45);
		WorldClient worldClient = Mockito.mock(WorldClient.class);

		ExecutableActorMoveSpeedMessage message =
			  new ExecutableActorMoveSpeedMessage(worldClient, actorMoveSpeedMessage);
		message.runImmediate();
		message.runDelayed();

		Assert.assertEquals(ClientActor.MOVE_SPEED, 123.45);
	}
}
