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

	/**
	 * Return whether the given key event was caused by a movement key.
	 *
	 * @param keyEvent The key event.
	 *
	 * @return Whether the even was caused by a movement key; arrow keys, or WASD.
	 */
	public static boolean isMovementKey(KeyEvent keyEvent) {
		KeyCode keyCode = keyEvent.getCode();
		boolean isMovementKey = keyCode.isArrowKey();
		isMovementKey |= keyCode == W;
		isMovementKey |= keyCode == A;
		isMovementKey |= keyCode == S;
		isMovementKey |= keyCode == D;
		return isMovementKey;
	}

	/**
	 * Whether the right key (and not the left) is presesd.
	 *
	 * @return Whether the right but not left key is pressed.
	 */
	protected boolean rightKey() {return right && !left;}

	/**
	 * Get whether the left key and not the right is pressed.
	 *
	 * @return Whether the left key and not the right is pressed.
	 */
	protected boolean leftKey() {return left && !right;}

	/**
	 * Get whether the down key and not the up key is pressed.
	 *
	 * @return Whether the down key and not the up key is pressed.
	 */
	protected boolean downKey() {return down && !up;}

	/**
	 * Get whether the up key and not the down key is pressed.
	 *
	 * @return Wehther the up key and not the down key is pressed.
	 */
	protected boolean upKey() {return up && !down;}

	/**
	 * Set key flags based on the received key event.
	 *
	 * @param keyEvent The key event.
	 */
	protected void setKeyFlags(KeyEvent keyEvent) {
		KeyCode keyCode = keyEvent.getCode();
		up = ((keyCode == UP) || (keyCode == W)) ? Objects.equals(keyEvent.getEventType(), KEY_PRESSED) : up;
		down = ((keyCode == DOWN) || (keyCode == S)) ? Objects.equals(keyEvent.getEventType(), KEY_PRESSED) : down;
		left = ((keyCode == LEFT) || (keyCode == A)) ? Objects.equals(keyEvent.getEventType(), KEY_PRESSED) : left;
		right = ((keyCode == RIGHT) || (keyCode == D)) ? Objects.equals(keyEvent.getEventType(), KEY_PRESSED) : right;
	}
}
