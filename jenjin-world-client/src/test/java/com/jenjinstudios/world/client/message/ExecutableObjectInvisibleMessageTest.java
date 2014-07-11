package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.client.WorldClient;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * @author Caleb Brinkman
 */
public class ExecutableObjectInvisibleMessageTest
{
	@Test
	public void testMessageExecution() throws Exception {
		MessageRegistry messageRegistry = MessageRegistry.getInstance();
		Message objectInvisibleMessage = messageRegistry.createMessage("ObjectInvisibleMessage");
		objectInvisibleMessage.setArgument("id", 100);

		WorldClient worldClient = mock(WorldClient.class);
		World world = mock(World.class);
		when(worldClient.getWorld()).thenReturn(world);

		ExecutableObjectInvisibleMessage message =
			  new ExecutableObjectInvisibleMessage(worldClient, objectInvisibleMessage);
		message.runImmediate();
		message.runDelayed();

		verify(world).removeObject(100);
	}
}
