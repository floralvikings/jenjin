package com.jenjinstudios.demo.client.ui;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.world.client.ClientPlayer;
import com.jenjinstudios.world.client.WorldClient;
import com.jenjinstudios.world.math.Angle;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

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

	private void sendFireRequest() {
		Message message = MessageRegistry.getInstance().createMessage("FireRequest");
		worldClient.queueOutgoingMessage(message);
	}

	private boolean isFireKey(KeyEvent keyEvent) {
		KeyCode code = keyEvent.getCode();
		return code.equals(KeyCode.SPACE) && keyEvent.getEventType() == KeyEvent.KEY_PRESSED;
	}

	private boolean isMovementKey(KeyEvent keyEvent) {
		KeyCode code = keyEvent.getCode();
		boolean isMovementKey = code.isArrowKey();
		isMovementKey |= code.equals(KeyCode.W);
		isMovementKey |= code.equals(KeyCode.A);
		isMovementKey |= code.equals(KeyCode.S);
		isMovementKey |= code.equals(KeyCode.D);
		return isMovementKey;
	}

	private void setNewAngle() {
		Angle angle = clientPlayer.getAngle().asIdle();
		angle = getMoveAngle(angle);
		clientPlayer.setAngle(angle);
	}

	public Angle getMoveAngle(Angle angle) {
		if (upKeyNotDown())
		{
			angle = getUpKeyAngle(angle);
		} else if (downKeyNotUp())
		{
			angle = getDownKeyAngle(angle);
		} else if (leftKeyNotRight())
		{
			angle = getLeftKeyAngle();
		} else if (rightKeyNotLeft())
		{
			angle = getRightKeyAngle();
		}
		return angle;
	}

	private Angle getRightKeyAngle() {
		Angle angle;
		angle = new Angle(Angle.FRONT, Angle.FRONT);
		return angle;
	}

	private Angle getLeftKeyAngle() {
		Angle angle;
		angle = new Angle(Angle.BACK, Angle.FRONT);
		return angle;
	}

	private boolean rightKeyNotLeft() {return righKey && !leftKey;}

	private boolean leftKeyNotRight() {return leftKey && !righKey;}

	private boolean downKeyNotUp() {return downKey && !upKey;}

	private boolean upKeyNotDown() {return upKey && !downKey;}

	private Angle getDownKeyAngle(Angle angle) {
		if (leftKeyNotRight())
		{
			angle = new Angle(Angle.BACK_RIGHT, Angle.FRONT);
		} else if (rightKeyNotLeft())
		{
			angle = new Angle(Angle.FRONT_RIGHT, Angle.FRONT);
		} else if (!righKey)
		{
			angle = new Angle(Angle.RIGHT, Angle.FRONT);
		}
		return angle;
	}

	private Angle getUpKeyAngle(Angle angle) {
		if (leftKeyNotRight())
		{
			angle = new Angle(Angle.BACK_LEFT, Angle.FRONT);
		} else if (rightKeyNotLeft())
		{
			angle = new Angle(Angle.FRONT_LEFT, Angle.FRONT);
		} else if (!righKey)
		{
			angle = new Angle(Angle.LEFT, Angle.FRONT);
		}
		return angle;
	}

	public void setKeyFlags(KeyEvent keyEvent) {
		KeyCode keyCode = keyEvent.getCode();
		if (keyCode.equals(KeyCode.UP) || keyCode.equals(KeyCode.W))
		{
			setUpKeyFlag(keyEvent);
		}
		if (keyCode.equals(KeyCode.DOWN) || keyCode.equals(KeyCode.S))
		{
			setDownKeyFlag(keyEvent);
		}
		if (keyCode.equals(KeyCode.LEFT) || keyCode.equals(KeyCode.A))
		{
			setLeftKeyDown(keyEvent);
		}
		if (keyCode.equals(KeyCode.RIGHT) || keyCode.equals(KeyCode.D))
		{
			setRightKeyDown(keyEvent);
		}
	}

	private void setRightKeyDown(KeyEvent keyEvent) {
		if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED && !righKey)
		{
			righKey = true;
		} else if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED && righKey)
		{
			righKey = false;
		}
	}

	private void setLeftKeyDown(KeyEvent keyEvent) {
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
