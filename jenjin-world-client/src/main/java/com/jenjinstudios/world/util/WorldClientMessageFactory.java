package com.jenjinstudios.world.util;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.util.ClientMessageFactory;
import com.jenjinstudios.world.WorldClient;
import com.jenjinstudios.world.state.MoveState;

/**
 * Generates messages for the client.
 * @author Caleb Brinkman
 */
public class WorldClientMessageFactory extends ClientMessageFactory
{
	private final WorldClient worldClient;

	public WorldClientMessageFactory(WorldClient client) {
		super(client);
		worldClient = client;
	}

	/**
	 * Generate a state change request for the given move state.
	 * @param moveState The state used to generate a state change request.
	 * @return The generated message.
	 */
	public Message generateStateChangeRequest(MoveState moveState) {
		Message stateChangeRequest = new Message(worldClient, "StateChangeRequest");
		stateChangeRequest.setArgument("relativeAngle", moveState.relativeAngle);
		stateChangeRequest.setArgument("absoluteAngle", moveState.absoluteAngle);
		stateChangeRequest.setArgument("stepsUntilChange", moveState.stepsUntilChange);
		// TODO Set these properly.
		stateChangeRequest.setArgument("timeOfChange", moveState.time);
		stateChangeRequest.setArgument("xCoord", moveState.position.getXCoordinate());
		stateChangeRequest.setArgument("yCoord", moveState.position.getYCoordinate());
		return stateChangeRequest;
	}

	/**
	 * Generate a LoginRequest message.
	 * @param username The username.
	 * @param password The password.
	 * @return The LoginRequest message.
	 */
	public Message generateLoginRequest(String username, String password) {
		Message loginRequest = new Message(worldClient, "WorldLoginRequest");
		loginRequest.setArgument("username", username);
		loginRequest.setArgument("password", password);
		return loginRequest;
	}

	public Message generateWorldLogoutRequest() {return new Message(worldClient, "WorldLogoutRequest");}

	public Message generateWorldFileRequest() {return new Message(worldClient, "WorldFileRequest");}

	public Message generateWorldChecksumRequest() {return new Message(worldClient, "WorldChecksumRequest");}
}
