package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
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
		MessageRegistry messageRegistry = MessageRegistry.getInstance();
		Message forceStateMessage = messageRegistry.createMessage("ForceStateMessage");
		forceStateMessage.setArgument("relativeAngle", IDLE);
		forceStateMessage.setArgument("absoluteAngle", PI);
		forceStateMessage.setArgument("xCoordinate", PI);
		forceStateMessage.setArgument("yCoordinate", PI);
		forceStateMessage.setArgument("timeOfForce", 12345l);

		WorldClient worldClient = mock(WorldClient.class);
		Actor clientPlayer = mock(Actor.class);
		World world = mock(World.class);
		when(clientPlayer.getWorld()).thenReturn(world);
		when(worldClient.getPlayer()).thenReturn(clientPlayer);

		ExecutableForceStateMessage message = new ExecutableForceStateMessage(worldClient, forceStateMessage);
		message.runImmediate();
		message.runDelayed();

		verify(clientPlayer).setAngle(eq(new Angle(PI, IDLE)));
		verify(clientPlayer).setVector2D(eq(new Vector2D(PI, PI)));
		verify(clientPlayer).forceIdle();
	}
}
