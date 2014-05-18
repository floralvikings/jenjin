package com.jenjinstudios.world.util;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.net.Connection;
import com.jenjinstudios.world.state.MoveState;

/**
 * Generates messages for the client.
 * @author Caleb Brinkman
 */
public class WorldClientMessageFactory
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
		// TODO Set these properly.
		stateChangeRequest.setArgument("timeOfChange", 0l);
		stateChangeRequest.setArgument("xCoord", 0.0d);
		stateChangeRequest.setArgument("yCoord", 0.0d);
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

	public static Message generateWorldLogoutRequest(Connection conn) {return new Message(conn, "WorldLogoutRequest");}

	public static Message generateWorldFileRequest(Connection conn) {return new Message(conn, "WorldFileRequest");}

	public static Message generateWorldChecksumRequest(Connection conn) {return new Message(conn, "WorldChecksumRequest");}
}
