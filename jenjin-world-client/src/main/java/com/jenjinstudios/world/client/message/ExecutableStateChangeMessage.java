package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.client.WorldClientMessageContext;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.object.WorldObject;

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
		String actorID = (String) getMessage().getArgument("id");
		double relativeAngle = (double) getMessage().getArgument("relativeAngle");
        double absoluteAngle = (double) getMessage().getArgument("absoluteAngle");
		long time = (long) getMessage().getArgument("timeOfChange");
		double x = (double) getMessage().getArgument("xCoordinate");
        double y = (double) getMessage().getArgument("yCoordinate");
		// TODO Change to 3D vector
		Vector oldVector = new Vector2D(x, y);
		Angle angle = new Angle(absoluteAngle, relativeAngle);

		World world = getContext().getWorld();
		world.scheduleUpdateTask(() -> {
			WorldObject obj = (WorldObject) world.findChild(actorID);
			double dist = obj.getGeometry().getSpeed() * ((System.currentTimeMillis() - time) / MS_TO_S);
			Vector position = oldVector.getVectorInDirection(dist, angle.getStepAngle());
			obj.getGeometry().setOrientation(angle);
			obj.getGeometry().setPosition(position);
		});
		return null;
	}
}
