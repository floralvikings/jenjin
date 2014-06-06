package com.jenjinstudios.world.message;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.world.ClientActor;
import com.jenjinstudios.world.WorldClient;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.math.MathUtil;
import com.jenjinstudios.world.math.Vector2D;

/**
 * Process a StateChangeMessage.
 * @author Caleb Brinkman
 */
public class ExecutableStateChangeMessage extends WorldClientExecutableMessage
{
	/** The ID of the actor to which to add the state. */
	private int actorID;
	private double relativeAngle;
	private double absoluteAngle;
	private Vector2D position;

	/**
	 * Construct an ExecutableMessage with the given Message.
	 * @param client The client invoking this message.
	 * @param message The Message.
	 */
	public ExecutableStateChangeMessage(WorldClient client, Message message) {
		super(client, message);
	}

	@Override
	public void runSynced() {
		WorldObject obj = getClient().getWorld().getObject(actorID);
		if (obj != null && obj instanceof ClientActor)
		{
			ClientActor actor = (ClientActor) obj;
			actor.setAbsoluteAngle(absoluteAngle);
			actor.setRelativeAngle(relativeAngle);
			actor.setLastStepTime(System.nanoTime());
			actor.setVector2D(position);
		}
	}

	@Override
	public void runASync() {
		actorID = (int) getMessage().getArgument("id");
		relativeAngle = (double) getMessage().getArgument("relativeAngle");
		absoluteAngle = (double) getMessage().getArgument("absoluteAngle");
		long time = (long) getMessage().getArgument("timeOfChange");
		double x = (double) getMessage().getArgument("xCoord");
		double y = (double) getMessage().getArgument("yCoord");
		Vector2D oldVector = new Vector2D(x, y);
		double angle = MathUtil.calcStepAngle(absoluteAngle, relativeAngle);
		double dist = ClientActor.MOVE_SPEED *
				((double) (System.nanoTime() - time) / 1000000000d);
		position = oldVector.getVectorInDirection(dist, angle);
	}
}
