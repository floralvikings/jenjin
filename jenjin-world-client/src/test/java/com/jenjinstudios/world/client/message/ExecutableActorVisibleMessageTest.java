package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.concurrency.ExecutableMessage;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.client.WorldClientMessageContext;
import com.jenjinstudios.world.collections.WorldObjectList;
import com.jenjinstudios.world.math.Angle;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * @author Caleb Brinkman
 */
public class ExecutableActorVisibleMessageTest
{
	@Test
	public void testMessageExecution() throws Exception {
		Message actorVisibleMessage = mock(Message.class);
		when(actorVisibleMessage.getArgument("name")).thenReturn("a1b2c3d4e5f6890");
		when(actorVisibleMessage.getArgument("id")).thenReturn(100);
		when(actorVisibleMessage.getArgument("resourceID")).thenReturn(100);
		when(actorVisibleMessage.getArgument("xCoordinate")).thenReturn(1.0);
		when(actorVisibleMessage.getArgument("yCoordinate")).thenReturn(1.0);
		when(actorVisibleMessage.getArgument("relativeAngle")).thenReturn(Angle.IDLE);
		when(actorVisibleMessage.getArgument("absoluteAngle")).thenReturn(0.0);
		when(actorVisibleMessage.getArgument("timeOfVisibility")).thenReturn(100L);
		when(actorVisibleMessage.getArgument("moveSpeed")).thenReturn(10.0d);

		WorldClient worldClient = mock(WorldClient.class);
		World world = mock(World.class);
		WorldObjectList worldObjectMap = mock(WorldObjectList.class);
		WorldClientMessageContext context = mock(WorldClientMessageContext.class);
		when(world.getWorldObjects()).thenReturn(worldObjectMap);
		when(worldClient.getWorld()).thenReturn(world);

		ExecutableMessage message = new ExecutableActorVisibleMessage(worldClient, actorVisibleMessage, context);
		message.execute();

		verify(world).scheduleUpdateTask(any());
	}
}
