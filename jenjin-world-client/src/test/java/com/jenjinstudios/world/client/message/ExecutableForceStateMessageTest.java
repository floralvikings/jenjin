package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
import org.testng.annotations.Test;

import static com.jenjinstudios.world.math.Angle.IDLE;
import static java.lang.Math.PI;
import static org.mockito.Mockito.*;

/**
 * @author Caleb Brinkman
 */
public class ExecutableForceStateMessageTest
{
	@Test(timeOut = 5000)
	public void testMessageExecution() throws Exception {
		Message forceStateMessage = mock(Message.class);
		when(forceStateMessage.getArgument("relativeAngle")).thenReturn(IDLE);
		when(forceStateMessage.getArgument("absoluteAngle")).thenReturn(PI);
		when(forceStateMessage.getArgument("xCoordinate")).thenReturn(PI);
		when(forceStateMessage.getArgument("yCoordinate")).thenReturn(PI);
		when(forceStateMessage.getArgument("timeOfForce")).thenReturn(12345L);

		WorldClient worldClient = mock(WorldClient.class);
		Actor clientPlayer = mock(Actor.class);
		World world = new World();
		when(worldClient.getWorld()).thenReturn(world);
		when(clientPlayer.getWorld()).thenReturn(world);
		when(worldClient.getPlayer()).thenReturn(clientPlayer);
		when(clientPlayer.getAngle()).thenReturn(new Angle());

		ExecutableForceStateMessage message = new ExecutableForceStateMessage(worldClient, forceStateMessage, null);
		message.execute();
		world.update();

		verify(clientPlayer).setAngle(eq(new Angle(PI, IDLE)));
		verify(clientPlayer).setVector2D(eq(new Vector2D(PI, PI)));
	}
}
