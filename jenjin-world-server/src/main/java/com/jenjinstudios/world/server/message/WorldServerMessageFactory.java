package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.state.MoveState;

import java.util.LinkedList;
import java.util.List;

/**
 * Used to generate Message objects that are relevant to the World and WorldClientHandler classes.
 *
 * @author Caleb Brinkman
 */
public class WorldServerMessageFactory
{
	public static Message generateNewlyVisibleMessage(WorldObject object) {
		Message newlyVisibleMessage;
		if (object instanceof Actor)
		{
			newlyVisibleMessage = generateActorVisibleMessage((Actor) object);
		} else
		{
			newlyVisibleMessage = generateObjectVisibleMessage(object);
		}
		return newlyVisibleMessage;
	}

	public static List<Message> generateChangeStateMessages(Actor changedActor) {
		List<Message> messages = new LinkedList<>();
		for (MoveState m : changedActor.getStateChanges()) {
			Message newState = MessageRegistry.getGlobalRegistry().createMessage("StateChangeMessage");
			newState.setArgument("id", changedActor.getId());
			newState.setArgument("relativeAngle", m.angle.getRelativeAngle());
			newState.setArgument("absoluteAngle", m.angle.getAbsoluteAngle());
			newState.setArgument("timeOfChange", m.timeOfChange);
			newState.setArgument("xCoordinate", m.position.getXCoordinate());
			newState.setArgument("yCoordinate", m.position.getYCoordinate());
			messages.add(newState);
		}

		return messages;
	}

	public static Message generateForcedStateMessage(MoveState forcedState) {
		Message forcedStateMessage = MessageRegistry.getGlobalRegistry().createMessage("ForceStateMessage");
		forcedStateMessage.setArgument("relativeAngle", forcedState.angle.getRelativeAngle());
		forcedStateMessage.setArgument("absoluteAngle", forcedState.angle.getAbsoluteAngle());
		forcedStateMessage.setArgument("xCoordinate", forcedState.position.getXCoordinate());
		forcedStateMessage.setArgument("yCoordinate", forcedState.position.getYCoordinate());
		forcedStateMessage.setArgument("timeOfForce", forcedState.timeOfChange);
		return forcedStateMessage;
	}

	public static Message generateActorMoveSpeedMessage(double moveSpeed) {
		Message stepLengthMessage = MessageRegistry.getGlobalRegistry().createMessage("ActorMoveSpeed");
		stepLengthMessage.setArgument("moveSpeed", moveSpeed);
		return stepLengthMessage;
	}

	public static Message generateNewlyInvisibleMessage(WorldObject object) {
		Message newlyInvisibleMessage = MessageRegistry.getGlobalRegistry().createMessage("ObjectInvisibleMessage");
		newlyInvisibleMessage.setArgument("id", object.getId());
		return newlyInvisibleMessage;
	}

	public static Message generateWorldLoginResponse() {
		return MessageRegistry.getGlobalRegistry().createMessage("WorldLoginResponse");
	}

	public static Message generateWorldFileResponse(byte[] worldFileBytes) {
		Message response = MessageRegistry.getGlobalRegistry().createMessage("WorldFileResponse");
		response.setArgument("fileBytes", worldFileBytes);
		return response;
	}

	public static Message generateWorldChecksumResponse(byte[] checkSum) {
		Message response = MessageRegistry.getGlobalRegistry().createMessage("WorldChecksumResponse");
		response.setArgument("checksum", checkSum);
		return response;
	}

	private static Message generateActorVisibleMessage(Actor newlyVisible) {
		Message newlyVisibleMessage;
		newlyVisibleMessage = MessageRegistry.getGlobalRegistry().createMessage("ActorVisibleMessage");
		newlyVisibleMessage.setArgument("name", newlyVisible.getName());
		newlyVisibleMessage.setArgument("id", newlyVisible.getId());
		newlyVisibleMessage.setArgument("resourceID", newlyVisible.getResourceID());
		newlyVisibleMessage.setArgument("xCoordinate", newlyVisible.getPosition().getXCoordinate());
		newlyVisibleMessage.setArgument("yCoordinate", newlyVisible.getPosition().getYCoordinate());
		newlyVisibleMessage.setArgument("relativeAngle", newlyVisible.getAngle().getRelativeAngle());
		newlyVisibleMessage.setArgument("absoluteAngle", newlyVisible.getAngle().getAbsoluteAngle());
		newlyVisibleMessage.setArgument("timeOfVisibility", newlyVisible.getWorld().getLastUpdateStarted());
		newlyVisibleMessage.setArgument("moveSpeed", newlyVisible.getMoveSpeed());
		return newlyVisibleMessage;
	}

	private static Message generateObjectVisibleMessage(WorldObject object) {
		Message newlyVisibleMessage;
		newlyVisibleMessage = MessageRegistry.getGlobalRegistry().createMessage("ObjectVisibleMessage");
		newlyVisibleMessage.setArgument("name", object.getName());
		newlyVisibleMessage.setArgument("id", object.getId());
		newlyVisibleMessage.setArgument("resourceID", object.getResourceID());
		newlyVisibleMessage.setArgument("xCoordinate", object.getPosition().getXCoordinate());
		newlyVisibleMessage.setArgument("yCoordinate", object.getPosition().getYCoordinate());
		return newlyVisibleMessage;
	}
}