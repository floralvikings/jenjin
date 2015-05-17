package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.concurrency.ExecutableMessage;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.client.WorldClientMessageContext;
import com.jenjinstudios.world.collections.WorldObjectList;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Geometry2D;
import com.jenjinstudios.world.object.Actor;
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

		Geometry2D geometry2D = spy(new Geometry2D());
		World world = spy(new World());
		WorldObjectList worldObjectMap = mock(WorldObjectList.class);
		WorldClientMessageContext context = mock(WorldClientMessageContext.class);
		when(world.getWorldObjects()).thenReturn(worldObjectMap);
		Actor clientActor = mock(Actor.class);
		when(clientActor.getGeometry2D()).thenReturn(geometry2D);
		when(context.getWorld()).thenReturn(world);
		when(worldObjectMap.get(100)).thenReturn(clientActor);

		ExecutableMessage message = new ExecutableStateChangeMessage(stateChangeMessage, context);
		message.execute();
		world.update();

		verify(worldObjectMap).get(100);
		verify(geometry2D).setOrientation(eq(new Angle(PI)));
	}
}
