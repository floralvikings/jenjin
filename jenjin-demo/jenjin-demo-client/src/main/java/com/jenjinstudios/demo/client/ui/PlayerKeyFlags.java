package com.jenjinstudios.demo.client.ui;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.Objects;

import static javafx.scene.input.KeyCode.*;
import static javafx.scene.input.KeyEvent.KEY_PRESSED;

/**
 * Keeps track of which control keys are pressed.
 *
 * @author Caleb Brinkman
 */
public class PlayerKeyFlags
{
	private boolean up;
	private boolean down;
	private boolean left;
	private boolean right;

	static boolean isFireKey(KeyEvent keyEvent) {
		return (keyEvent.getCode() == SPACE) && Objects.equals(keyEvent.getEventType(), KEY_PRESSED);
	}

	public static boolean isMovementKey(KeyEvent keyEvent) {
		KeyCode keyCode = keyEvent.getCode();
		boolean isMovementKey = keyCode.isArrowKey();
		isMovementKey |= keyCode == W;
		isMovementKey |= keyCode == A;
		isMovementKey |= keyCode == S;
		isMovementKey |= keyCode == D;
		return isMovementKey;
	}

	protected boolean rightKey() {return right && !left;}

	protected boolean leftKey() {return left && !right;}

	protected boolean downKey() {return down && !up;}

	protected boolean upKey() {return up && !down;}

	protected void setKeyFlags(KeyEvent keyEvent) {
		KeyCode keyCode = keyEvent.getCode();
		up = ((keyCode == UP) || (keyCode == W)) ? Objects.equals(keyEvent.getEventType(), KEY_PRESSED) : up;
		down = ((keyCode == DOWN) || (keyCode == S)) ? Objects.equals(keyEvent.getEventType(), KEY_PRESSED) : down;
		left = ((keyCode == LEFT) || (keyCode == A)) ? Objects.equals(keyEvent.getEventType(), KEY_PRESSED) : left;
		right = ((keyCode == RIGHT) || (keyCode == D)) ? Objects.equals(keyEvent.getEventType(), KEY_PRESSED) : right;
	}
}
