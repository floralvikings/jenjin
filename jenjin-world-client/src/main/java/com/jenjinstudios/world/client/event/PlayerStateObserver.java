package com.jenjinstudios.world.client.event;

import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.world.Node;
import com.jenjinstudios.world.event.WorldObjectObserver;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector;
import com.jenjinstudios.world.object.WorldObject;
import com.jenjinstudios.world.state.MoveState;

/**
 * Watches the client player for a change in state and dispatches it as an
 * event.
 *
 * @author Caleb Brinkman
 */
public class PlayerStateObserver extends WorldObjectObserver<PlayerStateEvent>
{
	private final MessageContext context;
	private Angle lastOrientation;

	/**
	 * Construct a new PlayerStateChangeObserver that will dispatch events with
	 * the given context.
	 *
	 * @param context The context that will be dispatched to StateChangeEvents.
	 */
	public PlayerStateObserver(MessageContext context) {
		this.context = context;
	}

	@Override
	protected PlayerStateEvent observePreUpdate(Node node) { return null; }

	@Override
	protected PlayerStateEvent observeUpdate(Node node) { return null; }

	@Override
	protected PlayerStateEvent observePostUpdate(Node node) {
		PlayerStateEvent stateChangeEvent = null;
		if(node instanceof WorldObject) {
			WorldObject obj = (WorldObject) node;
			Angle postAngle = obj.getGeometry().getOrientation();
			boolean changed = (lastOrientation == null) ? (postAngle != null) : !lastOrientation.equals(postAngle);
			if(changed) {
				Vector vector = obj.getGeometry().getPosition();
				long timeOfChange = obj.getTiming().getLastUpdateEndTime();
				MoveState state = new MoveState(postAngle, vector, timeOfChange);
				stateChangeEvent = new PlayerStateEvent(state, context);
			}
			lastOrientation = obj.getGeometry().getOrientation();
		}
		return stateChangeEvent;
	}
}
