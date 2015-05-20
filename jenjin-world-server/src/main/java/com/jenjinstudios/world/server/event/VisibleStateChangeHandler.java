package com.jenjinstudios.world.server.event;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.event.WorldEventHandler;
import com.jenjinstudios.world.object.WorldObject;
import com.jenjinstudios.world.state.MoveState;

import static com.jenjinstudios.core.io.MessageRegistry.getGlobalRegistry;

/**
 * Handles a change in state of a visible object.
 *
 * @author Caleb Brinkman
 */
public class VisibleStateChangeHandler
	  extends WorldEventHandler<VisibleStateChangeEvent>
{
	private static final String MESSAGE_NAME = "StateChangeMessage";

	@Override
	public void handle(VisibleStateChangeEvent event) {
		MoveState state = event.getState();
		WorldObject source = event.getChanged();
		Message newState = getGlobalRegistry().createMessage(MESSAGE_NAME);
		newState.setArgument("id", source.getIdentification().getId());
		newState.setArgument("relativeAngle", state.angle.getRelativeAngle());
		newState.setArgument("absoluteAngle", state.angle.getAbsoluteAngle());
		newState.setArgument("timeOfChange", state.timeOfChange);
		newState.setArgument("xCoordinate", state.position.getXValue());
		newState.setArgument("yCoordinate", state.position.getYValue());
		event.getContext().enqueue(newState);
	}
}
