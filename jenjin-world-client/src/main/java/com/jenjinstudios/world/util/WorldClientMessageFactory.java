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
	/** The client for which this factory generates messages. */
	private final WorldClient worldClient;

	/**
	 * Construct a new WorldClientMessageFactory working for the given client.
	 * @param client The client.
	 */
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
		stateChangeRequest.setArgument("timeOfChange", moveState.time);
		stateChangeRequest.setArgument("xCoordinate", moveState.position.getXCoordinate());
		stateChangeRequest.setArgument("yCoordinate", moveState.position.getYCoordinate());
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

	/**
	 * Generate a world logout request.
	 * @return The world logout request.
	 */
	public Message generateWorldLogoutRequest() {return new Message(worldClient, "WorldLogoutRequest");}

	/**
	 * Generate a world file request.
	 * @return The file request.
	 */
	public Message generateWorldFileRequest() {return new Message(worldClient, "WorldFileRequest");}

	/**
	 * Generate a request for the checksum of the world file.
	 * @return The request for the checksum.
	 */
	public Message generateWorldChecksumRequest() {return new Message(worldClient, "WorldChecksumRequest");}
}
