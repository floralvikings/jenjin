package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.collections.WorldObjectList;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * @author Caleb Brinkman
 */
public class ExecutableObjectVisibleMessageTest
{
	@Test
	public void testMessageExecution() throws Exception {
		MessageRegistry messageRegistry = MessageRegistry.getInstance();
		Message actorVisibleMessage = messageRegistry.createMessage("ObjectVisibleMessage");
		actorVisibleMessage.setArgument("name", "a1b2c3d4e5f6890");
		actorVisibleMessage.setArgument("id", 100);
		actorVisibleMessage.setArgument("resourceID", 100);
		actorVisibleMessage.setArgument("xCoordinate", 1.0);
		actorVisibleMessage.setArgument("yCoordinate", 1.0);

		WorldClient worldClient = mock(WorldClient.class);
		World world = mock(World.class);
		WorldObjectList worldObjectMap = mock(WorldObjectList.class);
		when(world.getWorldObjects()).thenReturn(worldObjectMap);
		when(worldClient.getWorld()).thenReturn(world);

		ExecutableObjectVisibleMessage message = new ExecutableObjectVisibleMessage(worldClient, actorVisibleMessage);
		message.runImmediate();
		message.runDelayed();

		verify(worldObjectMap).set(eq(100), any());
	}
}
