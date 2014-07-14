package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.client.ClientActor;
import com.jenjinstudios.world.client.ClientPlayer;
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

	public ExecutableForceStateMessage(WorldClient client, Message message) { super(client, message); }

	@Override
	public void runDelayed() {
		ClientPlayer player = getClient().getPlayer();
		player.setAngle(angle);
		player.setVector2D(vector2D);
		player.setLastStepTime(System.nanoTime());

		player.forcePosition();
	}

	@Override
	public void runImmediate() {
		double x = (double) getMessage().getArgument("xCoordinate");
		double y = (double) getMessage().getArgument("yCoordinate");
		double relativeAngle = (double) getMessage().getArgument("relativeAngle");
		double absoluteAngle = (double) getMessage().getArgument("absoluteAngle");
		long timeOfForce = (long) getMessage().getArgument("timeOfForce");
		angle = new Angle(absoluteAngle, relativeAngle);
		Vector2D oldPos = new Vector2D(x, y);
		long timeOfStep = System.nanoTime();
		double dist = ClientActor.MOVE_SPEED * ((double) (timeOfStep - timeOfForce) / 100000000d);
		vector2D = oldPos.getVectorInDirection(dist, angle.getStepAngle());
	}
}
