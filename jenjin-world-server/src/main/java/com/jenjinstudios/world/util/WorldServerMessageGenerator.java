package com.jenjinstudios.world.util;

import com.jenjinstudios.io.Message;
import com.jenjinstudios.net.Connection;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.WorldServer;

/**
 * Used to generate Message objects that are relevant to the World and WorldClientHandler classes.
 * @author Caleb Brinkman
 */
public class WorldServerMessageGenerator
{
	/**
	 * Generate an appropriate message for a newly visible object.
	 * @param connection The connection generating the message.
	 * @param object The object.
	 * @return The message.
	 */
	public static Message generateNewlyVisibleMessage(Connection connection, WorldObject object) {
		Message newlyVisibleMessage;
		if (object instanceof Actor)
		{
			newlyVisibleMessage = generateActorVisibleMessage(connection, (Actor) object);
		} else
		{
			newlyVisibleMessage = generateObjectVisibleMessage(connection, object);
		}
		return newlyVisibleMessage;
	}

	/**
	 * Generate an ActorVisibleMessage using the given actor.
	 * @param connection The connection generating this message.
	 * @param newlyVisible The Actor used to generate the message.
	 * @return A {@code Message} for the newly visible actor.
	 */
	public static Message generateActorVisibleMessage(Connection connection, Actor newlyVisible) {
		Message newlyVisibleMessage;
		newlyVisibleMessage = new Message(connection, "ActorVisibleMessage");
		newlyVisibleMessage.setArgument("name", newlyVisible.getName());
		newlyVisibleMessage.setArgument("id", newlyVisible.getId());
		newlyVisibleMessage.setArgument("resourceID", newlyVisible.getResourceID());
		newlyVisibleMessage.setArgument("xCoordinate", newlyVisible.getVector2D().getXCoordinate());
		newlyVisibleMessage.setArgument("yCoordinate", newlyVisible.getVector2D().getYCoordinate());
		newlyVisibleMessage.setArgument("relativeAngle", newlyVisible.getCurrentMoveState().relativeAngle);
		newlyVisibleMessage.setArgument("absoluteAngle", newlyVisible.getMoveAngle());
		newlyVisibleMessage.setArgument("stepsTaken", newlyVisible.getStepsTaken());
		newlyVisibleMessage.setArgument("stepsUntilChange", newlyVisible.getCurrentMoveState().stepsUntilChange);
		newlyVisibleMessage.setArgument("timeOfVisibility", 0l); // TODO Set this properly.
		return newlyVisibleMessage;
	}

	/**
	 * Generate an ObjectVisibleMessage using the given actor.
	 * @param connection The connection generating this message.
	 * @param object The Actor used to generate the message.
	 * @return A {@code Message} for the newly visible object.
	 */
	public static Message generateObjectVisibleMessage(Connection connection, WorldObject object) {
		Message newlyVisibleMessage;
		newlyVisibleMessage = new Message(connection, "ObjectVisibleMessage");
		newlyVisibleMessage.setArgument("name", object.getName());
		newlyVisibleMessage.setArgument("id", object.getId());
		newlyVisibleMessage.setArgument("resourceID", object.getResourceID());
		newlyVisibleMessage.setArgument("xCoordinate", object.getVector2D().getXCoordinate());
		newlyVisibleMessage.setArgument("yCoordinate", object.getVector2D().getYCoordinate());
		return newlyVisibleMessage;
	}

	/**
	 * Generate a state change message for the given actor.
	 * @param connection The connection generating this message.
	 * @param changedActor The actor with a new state.
	 * @return The state change message.
	 */
	public static Message generateChangeStateMessage(Connection connection, Actor changedActor) {
		Message newState = new Message(connection, "StateChangeMessage");
		newState.setArgument("id", changedActor.getId());
		newState.setArgument("relativeAngle", changedActor.getCurrentMoveState().relativeAngle);
		newState.setArgument("absoluteAngle", changedActor.getCurrentMoveState().absoluteAngle);
		newState.setArgument("stepsUntilChange", changedActor.getCurrentMoveState().stepsUntilChange);
		// TODO Set these properly
		newState.setArgument("timeOfChange", 0l);
		newState.setArgument("xCoord", 0.0d);
		newState.setArgument("yCoord", 0.0d);
		return newState;
	}

	/**
	 * Generate a forced state message.
	 * @param connection The connection generating this message.
	 * @param actor The actor who's state has been forced.
	 * @param server The server in which the world is running.
	 * @return A forced state message for the actor's state at the beginning of this server "tick".
	 */
	public static Message generateForcedStateMessage(Connection connection, Actor actor, WorldServer server) {
		Message forcedStateMessage = new Message(connection, "ForceStateMessage");
		forcedStateMessage.setArgument("relativeAngle", actor.getMoveDirection());
		forcedStateMessage.setArgument("absoluteAngle", actor.getMoveAngle());
		forcedStateMessage.setArgument("xCoordinate", actor.getVector2D().getXCoordinate());
		forcedStateMessage.setArgument("yCoordinate", actor.getVector2D().getYCoordinate());
		forcedStateMessage.setArgument("timeOfForce", server.getCycleStartTime());
		return forcedStateMessage;
	}

	/**
	 * Generate a step length message.
	 * @param connection The connection generating this message.
	 * @return The message.
	 */
	public static Message generateActorStepLengthMessage(Connection connection) {
		Message stepLengthMessage = new Message(connection, "ActorStepMessage");
		stepLengthMessage.setArgument("stepLength", Actor.STEP_LENGTH);
		return stepLengthMessage;
	}

	/**
	 * Generate a NewlyInvisibleObjectMessage for the given object.
	 * @param connection The connection generating this message.
	 * @param object The {@code WorldObject} that is newly invisible.
	 * @return A {@code Message} for the newly invisible object.
	 */
	public static Message generateNewlyInvisibleMessage(Connection connection, WorldObject object) {
		Message newlyInvisibleMessage = new Message(connection, "ObjectInvisibleMessage");
		newlyInvisibleMessage.setArgument("id", object.getId());
		return newlyInvisibleMessage;
	}
}