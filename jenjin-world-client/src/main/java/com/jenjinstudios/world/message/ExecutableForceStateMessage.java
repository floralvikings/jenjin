package com.jenjinstudios.world.message;

import com.jenjinstudios.world.ClientActor;
import com.jenjinstudios.world.math.MathUtil;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.io.Message;
import com.jenjinstudios.world.ClientPlayer;
import com.jenjinstudios.world.WorldClient;

/**
 * Process a ForceStateMessage.
 * @author Caleb Brinkman
 */
public class ExecutableForceStateMessage extends WorldClientExecutableMessage
{
	/** The position to which to force the player. */
	private Vector2D vector2D;
	/** The relative angle to which to force the player. */
	private double relativeAngle;
	/** The absolute angle to which to force the player. */
	private double absoluteAngle;

	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param client The client invoking this message.
	 * @param message The Message.
	 */
	public ExecutableForceStateMessage(WorldClient client, Message message) {
		super(client, message);
	}

	@Override
	public void runSynced() {
		System.out.println("Forcing Position: " + vector2D);
		ClientPlayer player = getClient().getPlayer();
		player.setAbsoluteAngle(absoluteAngle);
		player.setRelativeAngle(relativeAngle);
		player.setVector2D(vector2D);
		player.setLastStepTime(System.nanoTime());

		player.forcePosition();
	}

	@Override
	public void runASync() {
		double x = (double) getMessage().getArgument("xCoordinate");
		double y = (double) getMessage().getArgument("yCoordinate");
		relativeAngle = (double) getMessage().getArgument("relativeAngle");
		absoluteAngle = (double) getMessage().getArgument("absoluteAngle");
		long timeOfForce = (long) getMessage().getArgument("timeOfForce");
		double angle = MathUtil.calcStepAngle(absoluteAngle,relativeAngle);
		Vector2D oldPos = new Vector2D(x, y);
		long timeOfStep = System.nanoTime();
		double dist = ClientActor.MOVE_SPEED * ((double)(timeOfStep - timeOfForce) / 100000000d);
		vector2D = oldPos.getVectorInDirection(dist,angle);
	}
}
