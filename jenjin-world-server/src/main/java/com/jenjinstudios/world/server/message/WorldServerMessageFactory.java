package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.actor.StateChangeStack;
import com.jenjinstudios.world.event.EventStack;
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
	public Message generateNewlyVisibleMessage(WorldObject object) {
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
		EventStack eventStack = changedActor.getEventStack(StateChangeStack.STACK_NAME);
		if (eventStack != null && eventStack instanceof StateChangeStack)
		{
			StateChangeStack stateChangeStack = (StateChangeStack) eventStack;
			for (MoveState m : stateChangeStack.getStateChanges())
			{
				Message newState = MessageRegistry.getInstance().createMessage("StateChangeMessage");
				newState.setArgument("id", changedActor.getId());
				newState.setArgument("relativeAngle", m.angle.getRelativeAngle());
				newState.setArgument("absoluteAngle", m.angle.getAbsoluteAngle());
				newState.setArgument("timeOfChange", m.timeOfChange);
				newState.setArgument("xCoordinate", m.position.getXCoordinate());
				newState.setArgument("yCoordinate", m.position.getYCoordinate());
				messages.add(newState);
			}
		}
		return messages;
	}

	public static Message generateForcedStateMessage(MoveState forcedState) {
		Message forcedStateMessage = MessageRegistry.getInstance().createMessage("ForceStateMessage");
		forcedStateMessage.setArgument("relativeAngle", forcedState.angle.getRelativeAngle());
		forcedStateMessage.setArgument("absoluteAngle", forcedState.angle.getAbsoluteAngle());
		forcedStateMessage.setArgument("xCoordinate", forcedState.position.getXCoordinate());
		forcedStateMessage.setArgument("yCoordinate", forcedState.position.getYCoordinate());
		forcedStateMessage.setArgument("timeOfForce", forcedState.timeOfChange);
		return forcedStateMessage;
	}

	public static Message generateActorMoveSpeedMessage(double moveSpeed) {
		Message stepLengthMessage = MessageRegistry.getInstance().createMessage("ActorMoveSpeed");
		stepLengthMessage.setArgument("moveSpeed", moveSpeed);
		return stepLengthMessage;
	}

	public static Message generateNewlyInvisibleMessage(WorldObject object) {
		Message newlyInvisibleMessage = MessageRegistry.getInstance().createMessage("ObjectInvisibleMessage");
		newlyInvisibleMessage.setArgument("id", object.getId());
		return newlyInvisibleMessage;
	}

	public Message generateWorldLoginResponse() {
		return MessageRegistry.getInstance().createMessage("WorldLoginResponse");
	}

	public Message generateWorldFileResponse(byte[] worldFileBytes) {
		Message response = MessageRegistry.getInstance().createMessage("WorldFileResponse");
		response.setArgument("fileBytes", worldFileBytes);
		return response;
	}

	public Message generateWorldChecksumResponse(byte[] checkSum) {
		Message response = MessageRegistry.getInstance().createMessage("WorldChecksumResponse");
		response.setArgument("checksum", checkSum);
		return response;
	}

	private Message generateActorVisibleMessage(Actor newlyVisible) {
		Message newlyVisibleMessage;
		newlyVisibleMessage = MessageRegistry.getInstance().createMessage("ActorVisibleMessage");
		newlyVisibleMessage.setArgument("name", newlyVisible.getName());
		newlyVisibleMessage.setArgument("id", newlyVisible.getId());
		newlyVisibleMessage.setArgument("resourceID", newlyVisible.getResourceID());
		newlyVisibleMessage.setArgument("xCoordinate", newlyVisible.getVector2D().getXCoordinate());
		newlyVisibleMessage.setArgument("yCoordinate", newlyVisible.getVector2D().getYCoordinate());
		newlyVisibleMessage.setArgument("relativeAngle", newlyVisible.getAngle().getRelativeAngle());
		newlyVisibleMessage.setArgument("absoluteAngle", newlyVisible.getAngle().getAbsoluteAngle());
		newlyVisibleMessage.setArgument("timeOfVisibility", newlyVisible.getWorld().getLastUpdateStarted());
		newlyVisibleMessage.setArgument("moveSpeed", newlyVisible.getMoveSpeed());
		return newlyVisibleMessage;
	}

	private Message generateObjectVisibleMessage(WorldObject object) {
		Message newlyVisibleMessage;
		newlyVisibleMessage = MessageRegistry.getInstance().createMessage("ObjectVisibleMessage");
		newlyVisibleMessage.setArgument("name", object.getName());
		newlyVisibleMessage.setArgument("id", object.getId());
		newlyVisibleMessage.setArgument("resourceID", object.getResourceID());
		newlyVisibleMessage.setArgument("xCoordinate", object.getVector2D().getXCoordinate());
		newlyVisibleMessage.setArgument("yCoordinate", object.getVector2D().getYCoordinate());
		return newlyVisibleMessage;
	}
}