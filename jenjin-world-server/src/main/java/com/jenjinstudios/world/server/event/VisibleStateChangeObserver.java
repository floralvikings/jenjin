package com.jenjinstudios.world.server.event;

import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.event.WorldObjectObserver;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.object.Actor;
import com.jenjinstudios.world.object.WorldObject;
import com.jenjinstudios.world.state.MoveState;

/**
 * Watches visible objects, dispatching an event when they change state.
 *
 * @author Caleb Brinkman
 */
public class VisibleStateChangeObserver
	  extends WorldObjectObserver<VisibleStateChangeEvent>
{
	private final MessageContext context;
	private Angle lastOrientation;

	/**
	 * Construct a new VisibleStateChangeObserver that will utilize the given
	 * message context.
	 *
	 * @param context The message context.
	 */
	public VisibleStateChangeObserver(MessageContext context) {
		this.context = context;
	}

	@Override
	public VisibleStateChangeEvent observePreUpdate(World world,
													WorldObject obj)
	{
		return null;
	}

	@Override
	public VisibleStateChangeEvent observeUpdate(World world,
												 WorldObject obj)
	{
		return null;
	}

	@Override
	public VisibleStateChangeEvent observePostUpdate(World world,
													 WorldObject obj)
	{
		Angle postAngle = obj.getGeometry2D().getOrientation();

		boolean stateChanged = (lastOrientation == null)
			  ? (postAngle != null)
			  : !lastOrientation.equals(postAngle);

		if (obj instanceof Actor) {
			if (((Actor) obj).getForcedState() != null) {
				stateChanged = true;
			}
		}

		VisibleStateChangeEvent stateChangeEvent = null;

		if (stateChanged) {
			Vector2D vector2D = obj.getGeometry2D().getPosition();
			long timeOfChange = obj.getTiming().getLastUpdateEndTime();
			MoveState state = new MoveState(postAngle, vector2D, timeOfChange);
			stateChangeEvent =
				  new VisibleStateChangeEvent(obj, state, context);
		}

		lastOrientation = obj.getGeometry2D().getOrientation();
		return stateChangeEvent;
	}
}
