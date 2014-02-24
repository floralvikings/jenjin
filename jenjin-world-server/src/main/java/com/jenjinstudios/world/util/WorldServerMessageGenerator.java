package com.jenjinstudios.world.util;

import com.jenjinstudios.io.Message;
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
	 * @param object The object.
	 * @return The message.
	 */
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

	/**
	 * Generate an ActorVisibleMessage using the given actor.
	 * @param newlyVisible The Actor used to generate the message.
	 * @return A {@code Message} for the newly visible actor.
	 */
	public static Message generateActorVisibleMessage(Actor newlyVisible) {
		Message newlyVisibleMessage;
		newlyVisibleMessage = new Message("ActorVisibleMessage");
		newlyVisibleMessage.setArgument("name", newlyVisible.getName());
		newlyVisibleMessage.setArgument("id", newlyVisible.getId());
		newlyVisibleMessage.setArgument("resourceID", newlyVisible.getResourceID());
		newlyVisibleMessage.setArgument("xCoordinate", newlyVisible.getVector2D().getXCoordinate());
		newlyVisibleMessage.setArgument("yCoordinate", newlyVisible.getVector2D().getYCoordinate());
		newlyVisibleMessage.setArgument("relativeAngle", newlyVisible.getCurrentMoveState().relativeAngle);
		newlyVisibleMessage.setArgument("absoluteAngle", newlyVisible.getMoveAngle());
		newlyVisibleMessage.setArgument("stepsTaken", newlyVisible.getStepsTaken());
		newlyVisibleMessage.setArgument("stepsUntilChange", newlyVisible.getCurrentMoveState().stepsUntilChange);
		return newlyVisibleMessage;
	}

	/**
	 * Generate an ObjectVisibleMessage using the given actor.
	 * @param object The Actor used to generate the message.
	 * @return A {@code Message} for the newly visible object.
	 */
	public static Message generateObjectVisibleMessage(WorldObject object) {
		Message newlyVisibleMessage;
		newlyVisibleMessage = new Message("ObjectVisibleMessage");
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
	public static Message generateChangeStateMessage(Actor changedActor) {
		Message newState = new Message("StateChangeMessage");
		newState.setArgument("id", changedActor.getId());
		newState.setArgument("relativeAngle", changedActor.getCurrentMoveState().relativeAngle);
		newState.setArgument("absoluteAngle", changedActor.getCurrentMoveState().absoluteAngle);
		newState.setArgument("stepsUntilChange", changedActor.getCurrentMoveState().stepsUntilChange);
		return newState;
	}

	/**
	 * Generate a forced state message.
	 * @param actor The actor who's state has been forced.
	 * @param server The server in which the world is running.
	 * @return A forced state message for the actor's state at the beginning of this server "tick".
	 */
	public static Message generateForcedStateMessage(Actor actor, WorldServer server) {
		Message forcedStateMessage = new Message("ForceStateMessage");
		forcedStateMessage.setArgument("relativeAngle", actor.getMoveDirection());
		forcedStateMessage.setArgument("absoluteAngle", actor.getMoveAngle());
		forcedStateMessage.setArgument("xCoordinate", actor.getVector2D().getXCoordinate());
		forcedStateMessage.setArgument("yCoordinate", actor.getVector2D().getYCoordinate());
		forcedStateMessage.setArgument("timeOfForce", server.getCycleStartTime());
		return forcedStateMessage;
	}

	/**
	 * Generate a step length message.
	 * @return The message.
	 */
	public static Message generateActorStepLengthMessage() {
		Message stepLengthMessage = new Message("ActorStepMessage");
		stepLengthMessage.setArgument("stepLength", Actor.STEP_LENGTH);
		return stepLengthMessage;
	}

	/**
	 * Generate a NewlyInvisibleObjectMessage for the given object.
	 * @param object The {@code WorldObject} that is newly invisible.
	 * @return A {@code Message} for the newly invisible object.
	 */
	public static Message generateNewlyInvisibleMessage(WorldObject object) {
		Message newlyInvisibleMessage = new Message("ObjectInvisibleMessage");
		newlyInvisibleMessage.setArgument("id", object.getId());
		return newlyInvisibleMessage;
	}
}