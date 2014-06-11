package com.jenjinstudios.world.message;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.message.ServerMessageFactory;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.WorldClientHandler;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.WorldServer;
import com.jenjinstudios.world.state.MoveState;

import java.util.LinkedList;
import java.util.List;

/**
 * Used to generate Message objects that are relevant to the World and WorldClientHandler classes.
 * @author Caleb Brinkman
 */
public class WorldServerMessageFactory extends ServerMessageFactory
{
	/** The WorldClientHandler for which this message factory works. */
	private final WorldClientHandler worldClientHandler;

	/**
	 * Construct a new WorldServerMessageFactory.
	 * @param conn The WorldClientHandler for which this message factory works.
	 */
	public WorldServerMessageFactory(WorldClientHandler conn) {
		super(conn);
		this.worldClientHandler = conn;
	}

	/**
	 * Generate an appropriate message for a newly visible object.
	 * @param object The object.
	 * @return The message.
	 */
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

	/**
	 * Generate an ActorVisibleMessage using the given actor.
	 * @param newlyVisible The Actor used to generate the message.
	 * @return A {@code Message} for the newly visible actor.
	 */
	public Message generateActorVisibleMessage(Actor newlyVisible) {
		Message newlyVisibleMessage;
		newlyVisibleMessage = new Message(worldClientHandler, "ActorVisibleMessage");
		newlyVisibleMessage.setArgument("name", newlyVisible.getName());
		newlyVisibleMessage.setArgument("id", newlyVisible.getId());
		newlyVisibleMessage.setArgument("resourceID", newlyVisible.getResourceID());
		newlyVisibleMessage.setArgument("xCoordinate", newlyVisible.getVector2D().getXCoordinate());
		newlyVisibleMessage.setArgument("yCoordinate", newlyVisible.getVector2D().getYCoordinate());
		newlyVisibleMessage.setArgument("relativeAngle", newlyVisible.getRelativeAngle());
		newlyVisibleMessage.setArgument("absoluteAngle", newlyVisible.getAbsoluteAngle());
		newlyVisibleMessage.setArgument("timeOfVisibility", newlyVisible.getWorld().getLastUpdateStarted());
		return newlyVisibleMessage;
	}

	/**
	 * Generate an ObjectVisibleMessage using the given actor.
	 * @param object The Actor used to generate the message.
	 * @return A {@code Message} for the newly visible object.
	 */
	public Message generateObjectVisibleMessage(WorldObject object) {
		Message newlyVisibleMessage;
		newlyVisibleMessage = new Message(worldClientHandler, "ObjectVisibleMessage");
		newlyVisibleMessage.setArgument("name", object.getName());
		newlyVisibleMessage.setArgument("id", object.getId());
		newlyVisibleMessage.setArgument("resourceID", object.getResourceID());
		newlyVisibleMessage.setArgument("xCoordinate", object.getVector2D().getXCoordinate());
		newlyVisibleMessage.setArgument("yCoordinate", object.getVector2D().getYCoordinate());
		return newlyVisibleMessage;
	}

	/**
	 * Generate a state change message for the given actor.
	 * @param changedActor The actor with a new state.
	 * @return The state change message.
	 */
	public List<Message> generateChangeStateMessage(Actor changedActor) {
		List<Message> messages = new LinkedList<>();
		for (MoveState m : changedActor.getStateChanges())
		{
			Message newState = new Message(worldClientHandler, "StateChangeMessage");
			newState.setArgument("id", changedActor.getId());
			newState.setArgument("relativeAngle", m.relativeAngle);
			newState.setArgument("absoluteAngle", m.absoluteAngle);
			newState.setArgument("timeOfChange", m.timeOfChange);
			newState.setArgument("xCoordinate", m.position.getXCoordinate());
			newState.setArgument("yCoordinate", m.position.getYCoordinate());
			messages.add(newState);
		}
		return messages;
	}

	/**
	 * Generate a forced state message.
	 * @param forcedState The state to which the player was forced.
	 * @param server The server in which the world is running.
	 * @return A forced state message for the actor's state at the beginning of this server "tick".
	 */
	public Message generateForcedStateMessage(MoveState forcedState, WorldServer server) {
		Message forcedStateMessage = new Message(worldClientHandler, "ForceStateMessage");
		forcedStateMessage.setArgument("relativeAngle", forcedState.relativeAngle);
		forcedStateMessage.setArgument("absoluteAngle", forcedState.absoluteAngle);
		forcedStateMessage.setArgument("xCoordinate", forcedState.position.getXCoordinate());
		forcedStateMessage.setArgument("yCoordinate", forcedState.position.getYCoordinate());
		forcedStateMessage.setArgument("timeOfForce", server.getCycleStartTime());
		return forcedStateMessage;
	}

	/**
	 * Generate a step length message.
	 * @return The message.
	 */
	public Message generateActorMoveSpeedMessage() {
		Message stepLengthMessage = new Message(worldClientHandler, "ActorMoveSpeed");
		stepLengthMessage.setArgument("moveSpeed", Actor.MOVE_SPEED);
		return stepLengthMessage;
	}

	/**
	 * Generate a NewlyInvisibleObjectMessage for the given object.
	 * @param object The {@code WorldObject} that is newly invisible.
	 * @return A {@code Message} for the newly invisible object.
	 */
	public Message generateNewlyInvisibleMessage(WorldObject object) {
		Message newlyInvisibleMessage = new Message(worldClientHandler, "ObjectInvisibleMessage");
		newlyInvisibleMessage.setArgument("id", object.getId());
		return newlyInvisibleMessage;
	}

	/**
	 * Generate a WorldLoginResponse.
	 * @return The WorldLoginResponse.
	 */
	public Message generateWorldLoginResponse() {
		return new Message(worldClientHandler, "WorldLoginResponse");
	}

	/**
	 * Generate a response to a WorldFileRequest.
	 * @param worldFileBytes The bytes of the world file.
	 * @return The WorldFileResponse.
	 */
	public Message generateWorldFileResponse(byte[] worldFileBytes) {
		Message response = new Message(worldClientHandler, "WorldFileResponse");
		response.setArgument("fileBytes", worldFileBytes);
		return response;
	}

	/**
	 * Generate a response to a WorldChecksumRequest.
	 * @param checkSum The bytes of the world file's checksum.
	 * @return The WorldChecksumResponse.
	 */
	public Message generateWorldChecksumResponse(byte[] checkSum) {
		Message response = new Message(worldClientHandler, "WorldChecksumResponse");
		response.setArgument("checksum", checkSum);
		return response;
	}
}