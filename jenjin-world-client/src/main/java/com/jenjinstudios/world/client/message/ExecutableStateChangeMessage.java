package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;

/**
 * Process a StateChangeMessage.
 *
 * @author Caleb Brinkman
 */
public class ExecutableStateChangeMessage extends WorldClientExecutableMessage
{
	private static final double MS_TO_S = 1000.0d;

	/**
	 * Construct an ExecutableMessage with the given Message.
     *
     * @param client The client invoking this message.
     * @param message The Message.
     */
    public ExecutableStateChangeMessage(WorldClient client, Message message) {
        super(client, message);
    }

    @Override
	public void execute() {
		int actorID = (int) getMessage().getArgument("id");
		double relativeAngle = (double) getMessage().getArgument("relativeAngle");
        double absoluteAngle = (double) getMessage().getArgument("absoluteAngle");
		long time = (long) getMessage().getArgument("timeOfChange");
		double x = (double) getMessage().getArgument("xCoordinate");
        double y = (double) getMessage().getArgument("yCoordinate");
		Vector2D oldVector = new Vector2D(x, y);
		Angle angle = new Angle(absoluteAngle, relativeAngle);

		World world = getThreadPool().getWorld();
		world.scheduleUpdateTask(() -> {
			WorldObject obj = world.getWorldObjects().get(actorID);
			if (obj instanceof Actor)
			{
				Actor actor = (Actor) obj;
				double dist = actor.getMoveSpeed() * ((System.currentTimeMillis() - time) / MS_TO_S);
				Vector2D position = oldVector.getVectorInDirection(dist, angle.getStepAngle());
				actor.setAngle(angle);
				actor.setVector2D(position);
			}
		});
	}
}
