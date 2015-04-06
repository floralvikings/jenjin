package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.concurrency.ExecutableMessage;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.client.WorldClientMessageContext;
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
		Message actorVisibleMessage = mock(Message.class);
		when(actorVisibleMessage.getArgument("name")).thenReturn("a1b2c3d4e5f6890");
		when(actorVisibleMessage.getArgument("id")).thenReturn(100);
		when(actorVisibleMessage.getArgument("resourceID")).thenReturn(100);
		when(actorVisibleMessage.getArgument("xCoordinate")).thenReturn(1.0);
		when(actorVisibleMessage.getArgument("yCoordinate")).thenReturn(1.0);

		WorldClient worldClient = mock(WorldClient.class);
		World world = spy(new World());
		WorldObjectList worldObjectMap = mock(WorldObjectList.class);
		WorldClientMessageContext context = mock(WorldClientMessageContext.class);
		when(world.getWorldObjects()).thenReturn(worldObjectMap);
		when(context.getWorld()).thenReturn(world);

		ExecutableMessage message = new ExecutableObjectVisibleMessage(actorVisibleMessage, context);
		message.execute();
		world.update();

		verify(worldObjectMap).set(eq(100), any());
	}
}
