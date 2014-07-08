package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.client.ClientPlayer;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;

import static com.jenjinstudios.world.math.Angle.IDLE;
import static java.lang.Math.PI;
import static org.testng.Assert.assertEquals;

/**
 * @author Caleb Brinkman
 */
public class ExecutableForceStateMessageTest extends WorldClientExecutableMessageTest
{
	@Override
	public void testMessageExecution() throws Exception {
		Message forceStateMessage = messageRegistry.createMessage("ForceStateMessage");
		forceStateMessage.setArgument("relativeAngle", IDLE);
		forceStateMessage.setArgument("absoluteAngle", PI);
		forceStateMessage.setArgument("xCoordinate", PI);
		forceStateMessage.setArgument("yCoordinate", PI);
		forceStateMessage.setArgument("timeOfForce", 12345l);
		inStreamReadMessage.thenReturn(forceStateMessage, blankMessageSpam);

		worldClient.blockingStart();
		worldClient.sendBlockingWorldFileRequest();
		worldClient.sendBlockingLoginRequest();
		Thread.sleep(100); // Sleep to allow client to "catch up"

		ClientPlayer player = worldClient.getPlayer();
		assertEquals(player.getAngle(), new Angle(PI, IDLE));
		assertEquals(player.getVector2D(), new Vector2D(PI, PI));
	}
}
