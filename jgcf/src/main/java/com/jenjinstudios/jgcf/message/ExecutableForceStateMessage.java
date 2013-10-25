package com.jenjinstudios.jgcf.message;

import com.jenjinstudios.jgcf.WorldClient;
import com.jenjinstudios.math.Vector2D;
import com.jenjinstudios.message.Message;
import com.jenjinstudios.world.ClientPlayer;

/**
 * Process a ForceStateMessage.
 *
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
	/** The time of the start of the server update during which the state was forced. */
	private long timeOfForce;

	/**
	 * Construct an ExecutableMessage with the given Message.
	 *
	 * @param client  The client invoking this message.
	 * @param message The Message.
	 */
	public ExecutableForceStateMessage(WorldClient client, Message message)
	{
		super(client, message);
	}

	@Override
	public void runSynced()
	{
		ClientPlayer player = getClient().getPlayer();
		double timeSinceForce = System.nanoTime() - timeOfForce;
		double periodInNanos = getClient().getPeriod() * 1000000;
		double exactStepsTaken = timeSinceForce / periodInNanos;

		int stepsSinceForce = (int) exactStepsTaken;
		/* The amount of "leftover" steps taken. */
		double leftovers = exactStepsTaken - stepsSinceForce;

		if (leftovers > 0.5)
		{
			stepsSinceForce++;
		}

		player.forcePosition(vector2D, relativeAngle, absoluteAngle, stepsSinceForce);
	}

	@Override
	public void runASync()
	{
		double x = (double) getMessage().getArgument("xCoordinate");
		double z = (double) getMessage().getArgument("zCoordinate");
		vector2D = new Vector2D(x, z);
		relativeAngle = (double) getMessage().getArgument("direction");
		absoluteAngle = (double) getMessage().getArgument("angle");
		timeOfForce = (long) getMessage().getArgument("timeOfForce");
	}
}
