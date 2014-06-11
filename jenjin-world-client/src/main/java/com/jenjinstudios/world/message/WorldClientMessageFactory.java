package com.jenjinstudios.world.message;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.io.MessageRegistry;
import com.jenjinstudios.message.ClientMessageFactory;
import com.jenjinstudios.world.state.MoveState;

/**
 * Generates messages for the client.
 * @author Caleb Brinkman
 */
public class WorldClientMessageFactory extends ClientMessageFactory
{

	/**
	 * Construct a new WorldClientMessageFactory working for the given client.
	 */
	public WorldClientMessageFactory(MessageRegistry messageRegistry) {
		super(messageRegistry);
	}

	/**
	 * Generate a state change request for the given move state.
	 * @param moveState The state used to generate a state change request.
	 * @return The generated message.
	 */
	public Message generateStateChangeRequest(MoveState moveState) {
		Message stateChangeRequest = new Message("StateChangeRequest", getMessageRegistry());
		stateChangeRequest.setArgument("relativeAngle", moveState.relativeAngle);
		stateChangeRequest.setArgument("absoluteAngle", moveState.absoluteAngle);
		stateChangeRequest.setArgument("timeOfChange", moveState.timeOfChange);
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
		Message loginRequest = new Message("WorldLoginRequest", getMessageRegistry());
		loginRequest.setArgument("username", username);
		loginRequest.setArgument("password", password);
		return loginRequest;
	}

	/**
	 * Generate a world logout request.
	 * @return The world logout request.
	 */
	public Message generateWorldLogoutRequest() {return new Message("WorldLogoutRequest", getMessageRegistry());}

	/**
	 * Generate a world file request.
	 * @return The file request.
	 */
	public Message generateWorldFileRequest() {return new Message("WorldFileRequest", getMessageRegistry());}

	/**
	 * Generate a request for the checksum of the world file.
	 * @return The request for the checksum.
	 */
	public Message generateWorldChecksumRequest() {return new Message("WorldChecksumRequest", getMessageRegistry());}
}
