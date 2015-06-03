package com.jenjinstudios.world.server.event;

import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.world.Node;
import com.jenjinstudios.world.event.WorldObjectObserver;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector;
import com.jenjinstudios.world.object.WorldObject;
import com.jenjinstudios.world.state.MoveState;

/**
 * Watches visible objects, dispatching an event when they change state.
 *
 * @author Caleb Brinkman
 */
public class VisibleStateChangeObserver extends WorldObjectObserver<VisibleStateChangeEvent>
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
	protected VisibleStateChangeEvent observePreUpdate(Node node) { return null; }

	@Override
	protected VisibleStateChangeEvent observeUpdate(Node node) { return null; }

	@Override
	protected VisibleStateChangeEvent observePostUpdate(Node node) {
		VisibleStateChangeEvent stateChangeEvent = null;
		if(node instanceof WorldObject) {
			WorldObject obj = (WorldObject) node;
			Angle postAngle = obj.getGeometry().getOrientation();
			boolean changed = (lastOrientation == null) ? (postAngle != null) : !lastOrientation.equals(postAngle);

			if(changed) {
				Vector vector = obj.getGeometry().getPosition();
				long timeOfChange = obj.getTiming().getLastUpdateEndTime();
				MoveState state = new MoveState(postAngle, vector, timeOfChange);
				stateChangeEvent = new VisibleStateChangeEvent(obj, state, context);
			}

			lastOrientation = obj.getGeometry().getOrientation();
		}
		return stateChangeEvent;
	}
}
