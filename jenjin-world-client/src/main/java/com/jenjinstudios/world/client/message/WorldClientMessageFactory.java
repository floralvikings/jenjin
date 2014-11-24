package com.jenjinstudios.world.client.message;

import com.jenjinstudios.client.message.ClientMessageFactory;
import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.world.state.MoveState;

/**
 * Generates messages for the client.
 *
 * @author Caleb Brinkman
 */
public class WorldClientMessageFactory extends ClientMessageFactory
{

	/**
	 * Generate a state change request for the given move state.
	 *
	 * @param moveState The state used to generate a state change request.
	 *
	 * @return The generated message.
	 */
	public Message generateStateChangeRequest(MoveState moveState) {
		Message stateChangeRequest = MessageRegistry.getInstance().createMessage("StateChangeRequest");
		stateChangeRequest.setArgument("relativeAngle", moveState.angle.getRelativeAngle());
		stateChangeRequest.setArgument("absoluteAngle", moveState.angle.getAbsoluteAngle());
		stateChangeRequest.setArgument("timeOfChange", moveState.timeOfChange);
		stateChangeRequest.setArgument("xCoordinate", moveState.position.getXCoordinate());
		stateChangeRequest.setArgument("yCoordinate", moveState.position.getYCoordinate());
		return stateChangeRequest;
	}

	/**
	 * Generate a world file request.
	 *
	 * @return The file request.
	 */
	public Message generateWorldFileRequest() {
		return MessageRegistry.getInstance().createMessage
			  ("WorldFileRequest");
	}

	/**
	 * Generate a request for the checksum of the world file.
	 *
	 * @return The request for the checksum.
	 */
	public Message generateWorldChecksumRequest() {
		return MessageRegistry.getInstance().createMessage
			  ("WorldChecksumRequest");
	}
}
