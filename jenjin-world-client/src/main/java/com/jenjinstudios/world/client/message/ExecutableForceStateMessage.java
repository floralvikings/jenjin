package com.jenjinstudios.world.client.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.client.WorldClientMessageContext;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.util.ActorUtils;

/**
 * Process a ForceStateMessage.
 *
 * @author Caleb Brinkman
 */
public class ExecutableForceStateMessage extends WorldClientExecutableMessage<WorldClientMessageContext>
{
	private static final float MS_TO_S = 1000.0F;
	private Vector2D vector2D;
	private Angle angle;
    private long timeOfForce;

	public ExecutableForceStateMessage(Message message, WorldClientMessageContext context) {
		super(message, context);
	}

    @Override
	public Message execute() {
		double x = (double) getMessage().getArgument("xCoordinate");
        double y = (double) getMessage().getArgument("yCoordinate");
        double relativeAngle = (double) getMessage().getArgument("relativeAngle");
        double absoluteAngle = (double) getMessage().getArgument("absoluteAngle");
        timeOfForce = (long) getMessage().getArgument("timeOfForce");
        angle = new Angle(absoluteAngle, relativeAngle);
        vector2D = new Vector2D(x, y);

		World world = getContext().getWorld();
		world.scheduleUpdateTask(() -> {
			Actor player = getContext().getPlayer();
			double dist = ((world.getLastUpdateCompleted() - timeOfForce) / MS_TO_S) * player.getGeometry2D()
				  .getSpeed();
			Vector2D corrected = vector2D.getVectorInDirection(dist, angle.getStepAngle());
			player.getGeometry2D().setPosition(corrected);
			player.getGeometry2D().setOrientation(angle);

			ActorUtils.forceIdle(player);
		});
		return null;
	}
}
