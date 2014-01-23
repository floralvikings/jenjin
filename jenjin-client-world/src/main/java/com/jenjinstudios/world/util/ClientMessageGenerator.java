package com.jenjinstudios.world.util;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.world.state.MoveState;

/** @author Caleb Brinkman */
public class ClientMessageGenerator
{
	/**
	 * Generate a state change request for the given move state.
	 * @param moveState The state used to generate a state change request.
	 * @return The generated message.
	 */
	public static Message generateStateChangeRequest(MoveState moveState) {
		Message stateChangeRequest = new Message("StateChangeRequest");
		stateChangeRequest.setArgument("relativeAngle", moveState.relativeAngle);
		stateChangeRequest.setArgument("absoluteAngle", moveState.absoluteAngle);
		stateChangeRequest.setArgument("stepsUntilChange", moveState.stepsUntilChange);
		return stateChangeRequest;
	}

	/**
	 * Generate a LoginRequest message.
	 * @return The LoginRequest message.
	 */
	public static Message generateLoginRequest(String username, String password) {
		Message loginRequest = new Message("WorldLoginRequest");
		loginRequest.setArgument("username", username);
		loginRequest.setArgument("password", password);
		return loginRequest;
	}
}
