package com.jenjinstudios.world.client.message;

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
public class ExecutableObjectInvisibleMessageTest
{
	@Test
	public void testMessageExecution() throws Exception {
		Message msg = mock(Message.class);
		when(msg.getArgument("id")).thenReturn(100);

		WorldClient worldClient = mock(WorldClient.class);
		World world = spy(new World());
		WorldClientMessageContext context = mock(WorldClientMessageContext.class);
		WorldObjectList worldObjectMap = mock(WorldObjectList.class);
		when(world.getWorldObjects()).thenReturn(worldObjectMap);
		when(context.getWorld()).thenReturn(world);

		ExecutableObjectInvisibleMessage message = new ExecutableObjectInvisibleMessage(worldClient, msg, context);
		message.execute();
		world.update();

		verify(worldObjectMap).remove(100);
	}
}
