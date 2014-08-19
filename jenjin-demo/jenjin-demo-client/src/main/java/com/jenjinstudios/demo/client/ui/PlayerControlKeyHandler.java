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
	private boolean upKey, downKey, leftKey, righKey;

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
		if (upKeyNotDown())
		{
			angle = getUpKeyAngle();
		} else if (downKeyNotUp())
		{
			angle = getDownKeyAngle();
		} else if (leftKeyNotRight())
		{
			angle = getLeftKeyAngle();
		} else if (rightKeyNotLeft())
		{
			angle = getRightKeyAngle();
		}
		return angle;
	}

	protected void setKeyFlags(KeyEvent keyEvent) {
		KeyCode keyCode = keyEvent.getCode();
		if (keyCode.equals(UP) || keyCode.equals(W))
		{
			setUpKeyFlag(keyEvent);
		}
		if (keyCode.equals(DOWN) || keyCode.equals(S))
		{
			setDownKeyFlag(keyEvent);
		}
		if (keyCode.equals(LEFT) || keyCode.equals(A))
		{
			setLeftKeyFlag(keyEvent);
		}
		if (keyCode.equals(RIGHT) || keyCode.equals(D))
		{
			setRightKeyFlag(keyEvent);
		}
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

	private Angle getDownKeyAngle() {
		double absAngle = leftKeyNotRight() ? Angle.BACK_RIGHT : (rightKeyNotLeft() ? Angle.FRONT_RIGHT : Angle.RIGHT);
		return new Angle(absAngle, Angle.FRONT);
	}

	private Angle getUpKeyAngle() {
		double absAngle = leftKeyNotRight() ? Angle.BACK_LEFT : (rightKeyNotLeft() ? Angle.FRONT_LEFT : Angle.LEFT);
		return new Angle(absAngle, Angle.FRONT);
	}

	private Angle getRightKeyAngle() { return new Angle(Angle.FRONT, Angle.FRONT); }

	private Angle getLeftKeyAngle() { return new Angle(Angle.BACK, Angle.FRONT); }

	private boolean rightKeyNotLeft() {return righKey && !leftKey;}

	private boolean leftKeyNotRight() {return leftKey && !righKey;}

	private boolean downKeyNotUp() {return downKey && !upKey;}

	private boolean upKeyNotDown() {return upKey && !downKey;}

	private void setRightKeyFlag(KeyEvent keyEvent) {
		if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED && !righKey)
		{
			righKey = true;
		} else if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED && righKey)
		{
			righKey = false;
		}
	}

	private void setLeftKeyFlag(KeyEvent keyEvent) {
		if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED && !leftKey)
		{
			leftKey = true;
		} else if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED && leftKey)
		{
			leftKey = false;
		}
	}

	private void setDownKeyFlag(KeyEvent keyEvent) {
		if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED && !downKey)
		{
			downKey = true;
		} else if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED && downKey)
		{
			downKey = false;
		}
	}

	private void setUpKeyFlag(KeyEvent keyEvent) {
		if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED && !upKey)
		{
			upKey = true;
		} else if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED && upKey)
		{
			upKey = false;
		}
	}
}
