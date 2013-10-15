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
	private Vector2D vector2D;
	private double relativeAngle;
	private double absoluteAngle;
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
		// Set player angle
		// Set player direction
		// Set steps taken
		// Correct player position for steps taken based on movestate.
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
