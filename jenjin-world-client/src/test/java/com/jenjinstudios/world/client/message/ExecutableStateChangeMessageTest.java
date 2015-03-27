package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.collections.WorldObjectList;
import com.jenjinstudios.world.math.Angle;
import org.testng.annotations.Test;

import static com.jenjinstudios.world.math.Angle.IDLE;
import static java.lang.Math.PI;
import static org.mockito.Mockito.*;

/**
 * @author Caleb Brinkman
 */
public class ExecutableStateChangeMessageTest
{
	@Test
	public void testMessageExecution() throws Exception {
		Message stateChangeMessage = mock(Message.class);
		when(stateChangeMessage.getArgument("id")).thenReturn(100);
		when(stateChangeMessage.getArgument("relativeAngle")).thenReturn(IDLE);
		when(stateChangeMessage.getArgument("absoluteAngle")).thenReturn(PI);
		when(stateChangeMessage.getArgument("timeOfChange")).thenReturn(System.currentTimeMillis());
		when(stateChangeMessage.getArgument("xCoordinate")).thenReturn(PI);
		when(stateChangeMessage.getArgument("yCoordinate")).thenReturn(PI);

		WorldClient worldClient = mock(WorldClient.class);
		World world = spy(new World());
		WorldObjectList worldObjectMap = mock(WorldObjectList.class);
		when(world.getWorldObjects()).thenReturn(worldObjectMap);
		Actor clientActor = mock(Actor.class);
		when(worldClient.getWorld()).thenReturn(world);
		when(worldObjectMap.get(100)).thenReturn(clientActor);

		ExecutableStateChangeMessage message = new ExecutableStateChangeMessage(worldClient, stateChangeMessage);
		message.runImmediate();
		world.update();

		verify(worldObjectMap).get(100);
		verify(clientActor).setAngle(eq(new Angle(PI)));
	}
}
