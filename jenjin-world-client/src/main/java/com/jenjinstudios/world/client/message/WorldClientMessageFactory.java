package com.jenjinstudios.world.client.message;

import com.jenjinstudios.client.message.ClientMessageFactory;
import com.jenjinstudios.client.net.ClientUser;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
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
		Message stateChangeRequest = getMessageRegistry().createMessage("StateChangeRequest");
		stateChangeRequest.setArgument("relativeAngle", moveState.angle.getRelativeAngle());
		stateChangeRequest.setArgument("absoluteAngle", moveState.angle.getAbsoluteAngle());
		stateChangeRequest.setArgument("timeOfChange", moveState.timeOfChange);
		stateChangeRequest.setArgument("xCoordinate", moveState.position.getXCoordinate());
		stateChangeRequest.setArgument("yCoordinate", moveState.position.getYCoordinate());
		return stateChangeRequest;
	}

	/**
	 * Generate a LoginRequest message.
	 * @return The LoginRequest message.
	 */
	@Override
	public Message generateLoginRequest(ClientUser user) {
		Message loginRequest = getMessageRegistry().createMessage("WorldLoginRequest");
		loginRequest.setArgument("username", user.getUsername());
		loginRequest.setArgument("password", user.getPassword());
		return loginRequest;
	}

	/**
	 * Generate a world logout request.
	 * @return The world logout request.
	 */
	@Override
	public Message generateLogoutRequest() {return getMessageRegistry().createMessage("WorldLogoutRequest");}

	/**
	 * Generate a world file request.
	 * @return The file request.
	 */
	public Message generateWorldFileRequest() {return getMessageRegistry().createMessage("WorldFileRequest");}

	/**
	 * Generate a request for the checksum of the world file.
	 * @return The request for the checksum.
	 */
	public Message generateWorldChecksumRequest() {return getMessageRegistry().createMessage("WorldChecksumRequest");}
}
