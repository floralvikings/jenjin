package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.Cell;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.client.WorldClientMessageContext;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector;
import com.jenjinstudios.world.math.Vector2D;
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
	private WorldObject newlyVisible;

	public ExecutableActorVisibleMessage(Message message, WorldClientMessageContext context) {
		super(message, context);
	}

	@Override
	public Message execute() {
		Message message = getMessage();
		String name = (String) message.getArgument("name");
		String id = (String) message.getArgument("id");
		int typeId = (int) message.getArgument("typeId");
		double xCoordinate = (double) message.getArgument("xCoordinate");
		double yCoordinate = (double) message.getArgument("yCoordinate");
		double relativeAngle = (double) message.getArgument("relativeAngle");
		double absoluteAngle = (double) message.getArgument("absoluteAngle");
		long timeOfVisibility = (long) message.getArgument("timeOfVisibility");
		double moveSpeed = (double) message.getArgument("moveSpeed");

		newlyVisible = new WorldObject(name);
		newlyVisible.getIdentification().setId(id);
		newlyVisible.getIdentification().setTypeId(typeId);
		double dist = moveSpeed * ((double) (System.currentTimeMillis() - timeOfVisibility) / 1000d);
		Angle angle = new Angle(absoluteAngle, relativeAngle);
		// TODO Switch to 3D Vectors
		Vector oldVector = new Vector2D(xCoordinate, yCoordinate);
		Vector newVector = oldVector.getVectorInDirection(dist, angle.getStepAngle());
		newlyVisible.getGeometry().setPosition(newVector);
		newlyVisible.getGeometry().setOrientation(new Angle(absoluteAngle, relativeAngle));
		newlyVisible.getGeometry().setSpeed(moveSpeed);

		World world = getContext().getWorld();
		world.scheduleUpdateTask(() -> {
			try {
				Cell parent = getContext().getPlayer().getParent();
				parent.addChild(newlyVisible);
			} catch (RuntimeException ex) {
				WorldObject existing = (WorldObject) world.findChild(id);
				LOGGER.log(Level.WARNING, "Received message for already extant object ID: " +
					  id + ", " + newlyVisible + "; Current: " + existing, ex);
			}
		});
		return null;
	}

}
