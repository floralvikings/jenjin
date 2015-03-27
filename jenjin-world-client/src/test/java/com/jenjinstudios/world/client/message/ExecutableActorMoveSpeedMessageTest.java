package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.client.WorldClient;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.when;

/**
 * @author Caleb Brinkman
 */
public class ExecutableActorMoveSpeedMessageTest
{
	@Test
	public void testMessageExecution() throws Exception {
		Message msg = Mockito.mock(Message.class);
		when(msg.getArgument("moveSpeed")).thenReturn(123.45);
		WorldClient worldClient = Mockito.mock(WorldClient.class);

		ExecutableActorMoveSpeedMessage message =
			  new ExecutableActorMoveSpeedMessage(worldClient, msg);
		message.runImmediate();

		Assert.assertEquals(Actor.DEFAULT_MOVE_SPEED, 123.45);
	}
}
