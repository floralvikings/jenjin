package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.WorldObjectMap;
import com.jenjinstudios.world.client.WorldClient;
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
		MessageRegistry messageRegistry = MessageRegistry.getInstance();
		Message stateChangeMessage = messageRegistry.createMessage("StateChangeMessage");
		stateChangeMessage.setArgument("id", 100);
		stateChangeMessage.setArgument("relativeAngle", IDLE);
		stateChangeMessage.setArgument("absoluteAngle", PI);
		stateChangeMessage.setArgument("timeOfChange", System.currentTimeMillis());
		stateChangeMessage.setArgument("xCoordinate", PI);
		stateChangeMessage.setArgument("yCoordinate", PI);

		WorldClient worldClient = mock(WorldClient.class);
		World world = mock(World.class);
		WorldObjectMap worldObjectMap = mock(WorldObjectMap.class);
		when(world.getWorldObjects()).thenReturn(worldObjectMap);
		Actor clientActor = mock(Actor.class);
		when(worldClient.getWorld()).thenReturn(world);
		when(worldObjectMap.getObject(100)).thenReturn(clientActor);

		ExecutableStateChangeMessage message = new ExecutableStateChangeMessage(worldClient, stateChangeMessage);
		message.runImmediate();
		message.runDelayed();

		verify(worldObjectMap).getObject(100);
		verify(clientActor).setAngle(eq(new Angle(PI)));
	}
}
