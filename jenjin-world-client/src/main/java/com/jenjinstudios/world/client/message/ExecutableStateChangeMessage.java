package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.client.WorldClientMessageContext;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;

/**
 * Process a StateChangeMessage.
 *
 * @author Caleb Brinkman
 */
public class ExecutableStateChangeMessage extends WorldClientExecutableMessage<WorldClientMessageContext>
{
	private static final double MS_TO_S = 1000.0d;

	/**
	 * Construct an ExecutableMessage with the given Message.
     *
     * @param message The Message.
	 * @param context The context in which to execute the message.
	 */
	public ExecutableStateChangeMessage(Message message, WorldClientMessageContext context) {
		super(message, context);
	}

    @Override
	public Message execute() {
		int actorID = (int) getMessage().getArgument("id");
		double relativeAngle = (double) getMessage().getArgument("relativeAngle");
        double absoluteAngle = (double) getMessage().getArgument("absoluteAngle");
		long time = (long) getMessage().getArgument("timeOfChange");
		double x = (double) getMessage().getArgument("xCoordinate");
        double y = (double) getMessage().getArgument("yCoordinate");
		Vector2D oldVector = new Vector2D(x, y);
		Angle angle = new Angle(absoluteAngle, relativeAngle);

		World world = getContext().getWorld();
		world.scheduleUpdateTask(() -> {
			WorldObject obj = world.getWorldObjects().get(actorID);
			if (obj instanceof Actor)
			{
				Actor actor = (Actor) obj;
				double dist = actor.getGeometry2D().getSpeed() * ((System.currentTimeMillis() - time) / MS_TO_S);
				Vector2D position = oldVector.getVectorInDirection(dist, angle.getStepAngle());
				actor.getGeometry2D().setOrientation(angle);
				actor.getGeometry2D().setPosition(position);
			}
		});
		return null;
	}
}
