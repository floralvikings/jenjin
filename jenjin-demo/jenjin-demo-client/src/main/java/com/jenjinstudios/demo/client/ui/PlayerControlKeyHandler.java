package com.jenjinstudios.demo.client.ui;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.math.Angle;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

import static com.jenjinstudios.demo.client.ui.PlayerKeyFlags.isFireKey;
import static com.jenjinstudios.demo.client.ui.PlayerKeyFlags.isMovementKey;
import static com.jenjinstudios.world.math.Angle.*;

/**
 * Handles key events, queueing up requests to the server as need be.
 *
 * @author Caleb Brinkman
 */
public class PlayerControlKeyHandler implements EventHandler<KeyEvent>
{
	private final WorldClient worldClient;
	private final Actor clientPlayer;
	private final PlayerKeyFlags flags;

	/**
	 * Construct a new handler that will send requests via the supplied client.
	 *
	 * @param worldClient The client through which to send action requests.
	 */
	public PlayerControlKeyHandler(WorldClient worldClient) {
		this.worldClient = worldClient;
		this.clientPlayer = worldClient.getPlayer();
		this.flags = new PlayerKeyFlags();
	}

	@Override
	public void handle(KeyEvent event) {
		if (isMovementKey(event))
		{
			flags.setKeyFlags(event);
			setNewAngle();
		} else if (isFireKey(event))
		{
			sendFireRequest();
		}
		event.consume();
	}

	private Angle getMoveAngle(Angle angle) {
		if (flags.upKey())
		{
			double absAngle;
			if (flags.rightKey())
			{
				absAngle = flags.leftKey() ? BACK_LEFT : FRONT_LEFT;
			} else
			{
				absAngle = flags.leftKey() ? BACK_LEFT : LEFT;
			}
			angle = new Angle(absAngle, FRONT);
		} else if (flags.downKey())
		{
			double absAngle;
			if (flags.leftKey())
			{
				absAngle = BACK_RIGHT;
			} else
			{
				absAngle = flags.rightKey() ? FRONT_RIGHT : RIGHT;
			}
			angle = new Angle(absAngle, FRONT);
		} else if (flags.leftKey())
		{
			angle = new Angle(BACK, FRONT);
		} else if (flags.rightKey())
		{
			angle = new Angle(FRONT, FRONT);
		}
		return angle;
	}

	private void sendFireRequest() {
		Message message = MessageRegistry.getGlobalRegistry().createMessage("FireRequest");
		worldClient.enqueueMessage(message);
	}

	private void setNewAngle() {
		Angle angle = clientPlayer.getGeometry2D().getOrientation().asIdle();
		angle = getMoveAngle(angle);
		clientPlayer.getGeometry2D().setOrientation(angle);
	}

}
