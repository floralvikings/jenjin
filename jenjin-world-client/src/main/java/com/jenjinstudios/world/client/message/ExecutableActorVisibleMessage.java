package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Process an ActorVisibleMessage.
 * @author Caleb Brinkman
 */
public class ExecutableActorVisibleMessage extends WorldClientExecutableMessage
{
	private static final Logger LOGGER = Logger.getLogger(ExecutableActorVisibleMessage.class.getName());
	private Actor newlyVisible;

	public ExecutableActorVisibleMessage(WorldClient client, Message message) { super(client, message); }

	@Override
	public void runDelayed() {
		int id = newlyVisible.getId();
		World world = getClient().getWorld();
		try
		{
			world.getWorldObjects().set(id, newlyVisible);
		} catch (Exception ex)
		{
			WorldObject existing = world.getWorldObjects().get(id);
			LOGGER.log(Level.WARNING, "Received message for already extant object ID:  {0}, {1}; Current: {2}",
				  new Object[]{id, newlyVisible, existing});

			world.getWorldObjects().set(id, newlyVisible);
		}

	}

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
		double moveSpeed = (double) message.getArgument("moveSpeed");

		newlyVisible = new Actor(name);
		newlyVisible.setId(id);
		newlyVisible.setResourceID(resourceID);
		double dist = moveSpeed * ((double) (System.currentTimeMillis() - timeOfVisibility) / 1000d);
		Angle angle = new Angle(absoluteAngle, relativeAngle);
		Vector2D oldVector = new Vector2D(xCoordinate, yCoordinate);
		Vector2D newVector = oldVector.getVectorInDirection(dist, angle.getStepAngle());
		newlyVisible.setVector2D(newVector);
		newlyVisible.setAngle(new Angle(absoluteAngle, relativeAngle));
		newlyVisible.setMoveSpeed(moveSpeed);
	}
}
