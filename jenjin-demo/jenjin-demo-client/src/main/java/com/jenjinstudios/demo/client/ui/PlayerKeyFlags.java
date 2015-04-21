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
	private boolean up;
	private boolean down;
	private boolean left;
	private boolean right;

	static boolean isFireKey(KeyEvent keyEvent) {
		return keyEvent.getCode() == SPACE && keyEvent.getEventType() == KEY_PRESSED;
	}

	public static boolean isMovementKey(KeyEvent keyEvent) {
		KeyCode c = keyEvent.getCode();
		return c.isArrowKey() || c == W || c == A || c == S || c == D;
	}

	protected boolean rightKey() {return right && !left;}

	protected boolean leftKey() {return left && !right;}

	protected boolean downKey() {return down && !up;}

	protected boolean upKey() {return up && !down;}

	protected void setKeyFlags(KeyEvent keyEvent) {
		KeyCode keyCode = keyEvent.getCode();
		up = ((keyCode == UP || keyCode == W) ? (keyEvent.getEventType() == KEY_PRESSED) : up);
		down = ((keyCode == DOWN || keyCode == S) ? (keyEvent.getEventType() == KEY_PRESSED) : down);
		left = ((keyCode == LEFT || keyCode == A) ? (keyEvent.getEventType() == KEY_PRESSED) : left);
		right = ((keyCode == RIGHT || keyCode == D) ? (keyEvent.getEventType() == KEY_PRESSED) : right);
	}
}
