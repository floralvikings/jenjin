package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.client.ClientActor;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.math.Angle;
import org.testng.annotations.Test;

import static com.jenjinstudios.world.math.Angle.*;
import static java.lang.Math.*;
import static org.mockito.Mockito.*;

/**
 * @author Caleb Brinkman
 */
public class ExecutableStateChangeMessageTest
{
	@Test
	public void testMessageExecution() throws Exception {
		MessageRegistry messageRegistry = new MessageRegistry();
		Message stateChangeMessage = messageRegistry.createMessage("StateChangeMessage");
		stateChangeMessage.setArgument("id", 100);
		stateChangeMessage.setArgument("relativeAngle", IDLE);
		stateChangeMessage.setArgument("absoluteAngle", PI);
		stateChangeMessage.setArgument("timeOfChange", System.nanoTime());
		stateChangeMessage.setArgument("xCoordinate", PI);
		stateChangeMessage.setArgument("yCoordinate", PI);

		WorldClient worldClient = mock(WorldClient.class);
		World world = mock(World.class);
		ClientActor clientActor = mock(ClientActor.class);
		when(worldClient.getWorld()).thenReturn(world);
		when(world.getObject(100)).thenReturn(clientActor);

		ExecutableStateChangeMessage message = new ExecutableStateChangeMessage(worldClient, stateChangeMessage);
		message.runImmediate();
		message.runDelayed();

		verify(world).getObject(100);
		verify(clientActor).setAngle(eq(new Angle(PI)));
	}
}
