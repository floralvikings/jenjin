package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.client.ClientActor;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;

/**
 * Process an ActorVisibleMessage.
 * @author Caleb Brinkman
 */
public class ExecutableActorVisibleMessage extends WorldClientExecutableMessage
{
	private ClientActor newlyVisible;

	public ExecutableActorVisibleMessage(WorldClient client, Message message) { super(client, message); }

	@Override
	public void runDelayed() { getClient().getWorld().addObject(newlyVisible, newlyVisible.getId()); }

	@Override
	public void runImmediate() {
		Message message = getMessage();
		String name = (String) message.getArgument("name");
		int id = (int) message.getArgument("id");
		int resourceID = (int) message.getArgument("resourceID");
		double xCoordinate = (double) message.getArgument("xCoordinate");
		double yCoordinate = (double) message.getArgument("yCoordinate");
		double relativeAngle = (double) message.getArgument("relativeAngle");
		double absoluteAngle = (double) message.getArgument("absoluteAngle");
		long timeOfVisibility = (long) message.getArgument("timeOfVisibility");

		newlyVisible = new ClientActor(id, name);
		newlyVisible.setResourceID(resourceID);
		double dist = ClientActor.MOVE_SPEED *
			  ((double) (System.currentTimeMillis() - timeOfVisibility) / 1000d);
		Angle angle = new Angle(absoluteAngle, relativeAngle);
		Vector2D oldVector = new Vector2D(xCoordinate, yCoordinate);
		Vector2D newVector = oldVector.getVectorInDirection(dist, angle.getStepAngle());
		newlyVisible.setVector2D(newVector);
		newlyVisible.setAngle(new Angle(absoluteAngle, relativeAngle));
	}
}
