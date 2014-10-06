package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;

/**
 * Process a StateChangeMessage.
 * @author Caleb Brinkman
 */
public class ExecutableStateChangeMessage extends WorldClientExecutableMessage
{
	/** The ID of the actor to which to add the state. */
	private int actorID;
	private Angle angle;
	private Vector2D oldVector;
	private long time;

	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param client The client invoking this message.
	 * @param message The Message.
	 */
	public ExecutableStateChangeMessage(WorldClient client, Message message) {
		super(client, message);
	}

	@Override
	public void runDelayed() {
		WorldObject obj = getClient().getWorld().getWorldObjects().get(actorID);
		if (obj != null && obj instanceof Actor)
		{
			Actor actor = (Actor) obj;
			double dist = actor.getMoveSpeed() * ((double) (System.currentTimeMillis() - time) / 1000d);
			Vector2D position = oldVector.getVectorInDirection(dist, angle.getStepAngle());
			actor.setAngle(angle);
			actor.setVector2D(position);
		}
	}

	@Override
	public void runImmediate() {
		actorID = (int) getMessage().getArgument("id");
		double relativeAngle = (double) getMessage().getArgument("relativeAngle");
		double absoluteAngle = (double) getMessage().getArgument("absoluteAngle");
		time = (long) getMessage().getArgument("timeOfChange");
		double x = (double) getMessage().getArgument("xCoordinate");
		double y = (double) getMessage().getArgument("yCoordinate");
		oldVector = new Vector2D(x, y);
		angle = new Angle(absoluteAngle, relativeAngle);
	}
}
