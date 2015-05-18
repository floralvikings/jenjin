package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.client.WorldClientMessageContext;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.object.Actor;
import com.jenjinstudios.world.object.WorldObject;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Process an ActorVisibleMessage.
 *
 * @author Caleb Brinkman
 */
public class ExecutableActorVisibleMessage extends WorldClientExecutableMessage<WorldClientMessageContext>
{
    private static final Logger LOGGER = Logger.getLogger(ExecutableActorVisibleMessage.class.getName());
    private Actor newlyVisible;

	public ExecutableActorVisibleMessage(Message message, WorldClientMessageContext context) {
		super(message, context);
	}

    @Override
	public Message execute() {
		Message message = getMessage();
        String name = (String) message.getArgument("name");
        int id = (int) message.getArgument("id");
		int typeId = (int) message.getArgument("typeId");
		double xCoordinate = (double) message.getArgument("xCoordinate");
		double yCoordinate = (double) message.getArgument("yCoordinate");
        double relativeAngle = (double) message.getArgument("relativeAngle");
        double absoluteAngle = (double) message.getArgument("absoluteAngle");
        long timeOfVisibility = (long) message.getArgument("timeOfVisibility");
        double moveSpeed = (double) message.getArgument("moveSpeed");

        newlyVisible = new Actor(name);
		newlyVisible.getIdentification().setId(id);
		newlyVisible.getIdentification().setTypeId(typeId);
		double dist = moveSpeed * ((double) (System.currentTimeMillis() - timeOfVisibility) / 1000d);
        Angle angle = new Angle(absoluteAngle, relativeAngle);
        Vector2D oldVector = new Vector2D(xCoordinate, yCoordinate);
        Vector2D newVector = oldVector.getVectorInDirection(dist, angle.getStepAngle());
		newlyVisible.getGeometry2D().setPosition(newVector);
		newlyVisible.getGeometry2D().setOrientation(new Angle(absoluteAngle, relativeAngle));
		newlyVisible.getGeometry2D().setSpeed(moveSpeed);

		World world = getContext().getWorld();
		world.scheduleUpdateTask(() -> {
			try
			{
				world.getWorldObjects().set(id, newlyVisible);
			} catch (RuntimeException ex)
			{
				WorldObject existing = world.getWorldObjects().get(id);
				LOGGER.log(Level.WARNING, "Received message for already extant object ID: " +
					  id + ", " + newlyVisible + "; Current: " + existing, ex);

				world.getWorldObjects().set(id, newlyVisible);
			}
		});
		return null;
	}
}
