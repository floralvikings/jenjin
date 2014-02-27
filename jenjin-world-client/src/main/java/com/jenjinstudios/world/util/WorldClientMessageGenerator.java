package com.jenjinstudios.world.util;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.net.Connection;
import com.jenjinstudios.world.state.MoveState;

/**
 * Generates messages for the client.
 * @author Caleb Brinkman
 */
public class WorldClientMessageGenerator
{
	/**
	 * Generate a state change request for the given move state.
	 * @param connection The connection generating this message.
	 * @param moveState The state used to generate a state change request.
	 * @return The generated message.
	 */
	public static Message generateStateChangeRequest(Connection connection, MoveState moveState) {
		Message stateChangeRequest = new Message(connection, "StateChangeRequest");
		stateChangeRequest.setArgument("relativeAngle", moveState.relativeAngle);
		stateChangeRequest.setArgument("absoluteAngle", moveState.absoluteAngle);
		stateChangeRequest.setArgument("stepsUntilChange", moveState.stepsUntilChange);
		return stateChangeRequest;
	}

	/**
	 * Generate a LoginRequest message.
	 * @param connection The connection generating this message.
	 * @param username The username.
	 * @param password The password.
	 * @return The LoginRequest message.
	 */
	public static Message generateLoginRequest(Connection connection, String username, String password) {
		Message loginRequest = new Message(connection, "WorldLoginRequest");
		loginRequest.setArgument("username", username);
		loginRequest.setArgument("password", password);
		return loginRequest;
	}
}
