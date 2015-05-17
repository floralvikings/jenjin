package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.client.WorldClientMessageContext;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Geometry2D;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.object.Actor;
import com.jenjinstudios.world.object.Timing;
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

		Actor clientPlayer = mock(Actor.class);
		World world = new World();
		WorldClientMessageContext context = mock(WorldClientMessageContext.class);
		Geometry2D geometry2D = mock(Geometry2D.class);
		when(geometry2D.getOrientation()).thenReturn(new Angle());
		when(context.getWorld()).thenReturn(world);
		when(clientPlayer.getWorld()).thenReturn(world);
		when(context.getPlayer()).thenReturn(clientPlayer);
		when(clientPlayer.getGeometry2D()).thenReturn(geometry2D);
		when(clientPlayer.getTiming()).thenReturn(new Timing());

		ExecutableForceStateMessage message = new ExecutableForceStateMessage(forceStateMessage, context);
		message.execute();
		world.update();

		verify(geometry2D).setOrientation(eq(new Angle(PI, IDLE)));
		verify(geometry2D).setPosition(eq(new Vector2D(PI, PI)));
	}
}
