package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;

/**
 * Process a ForceStateMessage.
 * @author Caleb Brinkman
 */
public class ExecutableForceStateMessage extends WorldClientExecutableMessage
{
	private Vector2D vector2D;
	private Angle angle;
	private long timeOfForce;

	public ExecutableForceStateMessage(WorldClient client, Message message) { super(client, message); }

	@Override
	public void runDelayed() {
		Actor player = getClient().getPlayer();
		player.setVector2D(vector2D);
		player.setAngle(angle);
		player.setLastStepTime(timeOfForce);

		player.forcePosition();
	}

	@Override
	public void runImmediate() {
		double x = (double) getMessage().getArgument("xCoordinate");
		double y = (double) getMessage().getArgument("yCoordinate");
		double relativeAngle = (double) getMessage().getArgument("relativeAngle");
		double absoluteAngle = (double) getMessage().getArgument("absoluteAngle");
		timeOfForce = (long) getMessage().getArgument("timeOfForce");
		angle = new Angle(absoluteAngle, relativeAngle);
		vector2D = new Vector2D(x, y);
	}
}
