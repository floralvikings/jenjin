package com.jenjinstudios.demo.client.ui;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import static javafx.scene.input.KeyCode.*;
import static javafx.scene.input.KeyEvent.KEY_PRESSED;

/**
 * @author Caleb Brinkman
 */
public class PlayerKeyFlags
{
	private boolean up, down, left, right;

	static boolean isFireKey(KeyEvent keyEvent) {
		return keyEvent.getCode().equals(SPACE) && keyEvent.getEventType() == KEY_PRESSED;
	}

	public static boolean isMovementKey(KeyEvent keyEvent) {
		KeyCode c = keyEvent.getCode();
		return c.isArrowKey() || c.equals(W) || c.equals(A) || c.equals(S) || c.equals(D);
	}

	protected boolean rightKey() {return right && !left;}

	protected boolean leftKey() {return left && !right;}

	protected boolean downKey() {return down && !up;}

	protected boolean upKey() {return up && !down;}

	protected void setKeyFlags(KeyEvent keyEvent) {
		KeyCode keyCode = keyEvent.getCode();
		up = ((keyCode.equals(UP) || keyCode.equals(W)) ? (keyEvent.getEventType() == KEY_PRESSED) : up);
		down = ((keyCode.equals(DOWN) || keyCode.equals(S)) ? (keyEvent.getEventType() == KEY_PRESSED) : down);
		left = ((keyCode.equals(LEFT) || keyCode.equals(A)) ? (keyEvent.getEventType() == KEY_PRESSED) : left);
		right = ((keyCode.equals(RIGHT) || keyCode.equals(D)) ? (keyEvent.getEventType() == KEY_PRESSED) : right);
	}
}
