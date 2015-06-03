package com.jenjinstudios.world.server.message;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.world.object.WorldObject;
import com.jenjinstudios.world.state.MoveState;

/**
 * Used to generate Message objects that are relevant to the World and WorldClientHandler classes.
 *
 * @author Caleb Brinkman
 */
public class WorldServerMessageFactory
{
	public static Message generateNewlyVisibleMessage(WorldObject object) {
		Message newlyVisibleMessage = MessageRegistry.getGlobalRegistry().createMessage("ActorVisibleMessage");
		newlyVisibleMessage.setArgument("name", object.getName());
		newlyVisibleMessage.setArgument("id", object.getIdentification().getId());
		newlyVisibleMessage.setArgument("typeId", object.getIdentification().getTypeId());
		newlyVisibleMessage.setArgument("xCoordinate", object.getGeometry().getPosition().getXValue());
		newlyVisibleMessage.setArgument("yCoordinate", object.getGeometry().getPosition().getYValue());
		newlyVisibleMessage.setArgument("relativeAngle", object.getGeometry().getOrientation().getRelativeAngle());
		newlyVisibleMessage.setArgument("absoluteAngle", object.getGeometry().getOrientation().getAbsoluteAngle());
		newlyVisibleMessage.setArgument("timeOfVisibility", object.getTiming().getLastUpdateStartTime());
		newlyVisibleMessage.setArgument("moveSpeed", object.getGeometry().getSpeed());
		return newlyVisibleMessage;
	}

	public static Message generateForcedStateMessage(MoveState forcedState) {
		Message forcedStateMessage = MessageRegistry.getGlobalRegistry().createMessage("ForceStateMessage");
		forcedStateMessage.setArgument("relativeAngle", forcedState.angle.getRelativeAngle());
		forcedStateMessage.setArgument("absoluteAngle", forcedState.angle.getAbsoluteAngle());
		forcedStateMessage.setArgument("xCoordinate", forcedState.position.getXValue());
		forcedStateMessage.setArgument("yCoordinate", forcedState.position.getYValue());
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
		newlyInvisibleMessage.setArgument("id", object.getIdentification().getId());
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

}