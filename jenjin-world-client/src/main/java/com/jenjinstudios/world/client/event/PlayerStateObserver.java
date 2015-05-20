package com.jenjinstudios.world.client.event;

import com.jenjinstudios.core.concurrency.MessageContext;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.event.WorldObjectObserver;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.object.Actor;
import com.jenjinstudios.world.object.WorldObject;
import com.jenjinstudios.world.state.MoveState;

/**
 * Watches the client player for a change in state and dispatches it as an
 * event.
 *
 * @author Caleb Brinkman
 */
public class PlayerStateObserver
	  extends WorldObjectObserver<PlayerStateEvent>
{
	private final MessageContext context;
	private Angle lastUpdateOrientation;

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
	public PlayerStateEvent observePreUpdate(World world, WorldObject obj) {
		return null;
	}

	@Override
	public PlayerStateEvent observeUpdate(World world, WorldObject obj) {
		return null;
	}

	@Override
	public PlayerStateEvent observePostUpdate(World world, WorldObject obj) {
		Angle postAngle = obj.getGeometry2D().getOrientation();

		boolean stateChanged = (lastUpdateOrientation == null)
			  ? (postAngle != null)
			  : !lastUpdateOrientation.equals(postAngle);

		if (obj instanceof Actor) {
			if (((Actor) obj).getForcedState() != null) {
				stateChanged = true;
			}
		}

		PlayerStateEvent stateChangeEvent = null;
		if (stateChanged) {
			Vector2D vector2D = obj.getGeometry2D().getPosition();
			long timeOfChange = obj.getTiming().getLastUpdateEndTime();
			MoveState state = new MoveState(postAngle, vector2D, timeOfChange);
			stateChangeEvent = new PlayerStateEvent(state, context);
		}
		lastUpdateOrientation = obj.getGeometry2D().getOrientation();
		return stateChangeEvent;
	}
}
