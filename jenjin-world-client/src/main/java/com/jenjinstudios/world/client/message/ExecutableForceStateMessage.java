package com.jenjinstudios.world.client.message;

import com.jenjinstudios.client.net.ClientMessageContext;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.util.ActorUtils;

/**
 * Process a ForceStateMessage.
 *
 * @author Caleb Brinkman
 */
public class ExecutableForceStateMessage extends WorldClientExecutableMessage<ClientMessageContext>
{
	private static final float MS_TO_S = 1000.0F;
	private Vector2D vector2D;
	private Angle angle;
    private long timeOfForce;

	public ExecutableForceStateMessage(WorldClient client, Message message, ClientMessageContext context) {
		super(client,
		  message, context); }

    @Override
	public Message execute() {
		double x = (double) getMessage().getArgument("xCoordinate");
        double y = (double) getMessage().getArgument("yCoordinate");
        double relativeAngle = (double) getMessage().getArgument("relativeAngle");
        double absoluteAngle = (double) getMessage().getArgument("absoluteAngle");
        timeOfForce = (long) getMessage().getArgument("timeOfForce");
        angle = new Angle(absoluteAngle, relativeAngle);
        vector2D = new Vector2D(x, y);

		World world = getWorldClient().getWorld();
		world.scheduleUpdateTask(() -> {
			Actor player = getWorldClient().getPlayer();
			double dist = ((world.getLastUpdateCompleted() - timeOfForce) / MS_TO_S) * player.getMoveSpeed();
			Vector2D corrected = vector2D.getVectorInDirection(dist, angle.getStepAngle());
			player.setVector2D(corrected);
			player.setAngle(angle);

			ActorUtils.forceIdle(player);
		});
		return null;
	}
}
