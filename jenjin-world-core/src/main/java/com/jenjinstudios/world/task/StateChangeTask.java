package com.jenjinstudios.world.task;

import com.jenjinstudios.world.World;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.object.Actor;
import com.jenjinstudios.world.object.WorldObject;
import com.jenjinstudios.world.state.MoveState;

import java.util.LinkedList;
import java.util.List;

/**
 * Tracks and reports state changes.
 *
 * @author Caleb Brinkman
 */
public class StateChangeTask extends WorldObjectTaskAdapter
{
	private final List<MoveState> stateChanges = new LinkedList<>();
	private Angle preUpdateAngle;

	public List<MoveState> getStateChanges() {
		synchronized (stateChanges) {
			return new LinkedList<>(stateChanges);
		}
	}

	@Override
	public void onPreUpdate(World world, WorldObject worldObject) {
		synchronized (stateChanges) {
			stateChanges.clear();
		}
	}

	@Override
	public void onPostUpdate(World world, WorldObject worldObject) {
		Angle postAngle = worldObject.getGeometry2D().getOrientation();

		boolean stateChanged = (preUpdateAngle == null) ? (postAngle != null) : !preUpdateAngle.equals(postAngle);

		if (worldObject instanceof Actor) {
			if (((Actor) worldObject).getForcedState() != null) {
				stateChanged = true;
			}
		}

		if (stateChanged) {
			Vector2D vector2D = worldObject.getGeometry2D().getPosition();
			long timeOfChange = worldObject.getWorld().getLastUpdateCompleted();
			synchronized (stateChanges) {
				stateChanges.add(new MoveState(postAngle, vector2D, timeOfChange));
			}
		}

		preUpdateAngle = worldObject.getGeometry2D().getOrientation();
	}
}
