package com.jenjinstudios.demo.client.ui;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.world.client.ClientPlayer;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.math.Angle;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import static javafx.scene.input.KeyCode.*;

/**
 * @author Caleb Brinkman
 */
public class PlayerControlKeyHandler implements EventHandler<KeyEvent>
{
	private final WorldClient worldClient;
	private final ClientPlayer clientPlayer;
	private boolean up, down, left, right;

	public PlayerControlKeyHandler(WorldClient worldClient) {
		this.worldClient = worldClient;
		this.clientPlayer = worldClient.getPlayer();
	}

	@Override
	public void handle(KeyEvent keyEvent) {
		if (isMovementKey(keyEvent))
		{
			setKeyFlags(keyEvent);
			setNewAngle();
		} else if (isFireKey(keyEvent))
		{
			sendFireRequest();
		}
		keyEvent.consume();
	}

	protected Angle getMoveAngle(Angle angle) {
		if (upKey())
		{
			double absAngle = leftKey() ? Angle.BACK_LEFT : (rightKey() ? Angle.FRONT_LEFT : Angle.LEFT);
			angle = new Angle(absAngle, Angle.FRONT);
		} else if (downKey())
		{
			double absAngle = leftKey() ? Angle.BACK_RIGHT : (rightKey() ? Angle.FRONT_RIGHT : Angle.RIGHT);
			angle = new Angle(absAngle, Angle.FRONT);
		} else if (leftKey())
		{
			angle = new Angle(Angle.BACK, Angle.FRONT);
		} else if (rightKey())
		{
			angle = new Angle(Angle.FRONT, Angle.FRONT);
		}
		return angle;
	}

	protected void setKeyFlags(KeyEvent keyEvent) {
		KeyCode keyCode = keyEvent.getCode();
		up = keyCode.equals(UP) || keyCode.equals(W) ? keyEvent.getEventType() == KeyEvent.KEY_PRESSED : up;
		down = keyCode.equals(DOWN) || keyCode.equals(S) ? keyEvent.getEventType() == KeyEvent.KEY_PRESSED : down;
		left = keyCode.equals(LEFT) || keyCode.equals(A) ? keyEvent.getEventType() == KeyEvent.KEY_PRESSED : left;
		right = keyCode.equals(RIGHT) || keyCode.equals(D) ? keyEvent.getEventType() == KeyEvent.KEY_PRESSED : right;
	}

	private void sendFireRequest() {
		Message message = MessageRegistry.getInstance().createMessage("FireRequest");
		worldClient.queueOutgoingMessage(message);
	}

	private boolean isFireKey(KeyEvent keyEvent) {
		return keyEvent.getCode().equals(SPACE) && keyEvent.getEventType() == KeyEvent.KEY_PRESSED;
	}

	private boolean isMovementKey(KeyEvent keyEvent) {
		KeyCode c = keyEvent.getCode();
		return c.isArrowKey() || c.equals(W) || c.equals(A) || c.equals(S) || c.equals(D);
	}

	private void setNewAngle() {
		Angle angle = clientPlayer.getAngle().asIdle();
		angle = getMoveAngle(angle);
		clientPlayer.setAngle(angle);
	}

	private boolean rightKey() {return right && !left;}

	private boolean leftKey() {return left && !right;}

	private boolean downKey() {return down && !up;}

	private boolean upKey() {return up && !down;}

}
