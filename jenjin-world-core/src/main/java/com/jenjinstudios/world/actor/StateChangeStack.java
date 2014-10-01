package com.jenjinstudios.world.actor;

import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.event.EventStack;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.state.MoveState;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Caleb Brinkman
 */
public class StateChangeStack implements EventStack
{
	public static final String STACK_NAME = "stateChangeStack";
	private final List<MoveState> stateChanges;
	private final WorldObject worldObject;
	private Angle preUpdateAngle;

	public StateChangeStack(WorldObject worldObject) {
		this.worldObject = worldObject;
		stateChanges = new LinkedList<>();
	}

	public List<MoveState> getStateChanges() { synchronized (stateChanges) { return new LinkedList<>(stateChanges); } }

	@Override
	public void onPreUpdate() {
		synchronized (stateChanges)
		{
			stateChanges.clear();
		}
		preUpdateAngle = worldObject.getAngle();
	}

	@Override
	public void onPostUpdate() {
		Angle postAngle = worldObject.getAngle();
		boolean stateChanged = preUpdateAngle == null ? postAngle != null : !preUpdateAngle.equals(postAngle);
		if (stateChanged)
		{
			Vector2D vector2D = worldObject.getVector2D();
			long timeOfChange = worldObject.getWorld().getLastUpdateCompleted();
			synchronized (stateChanges)
			{
				stateChanges.add(new MoveState(postAngle, vector2D, timeOfChange));
			}
		}
	}

	@Override
	public void onUpdate() { }

	@Override
	public void onInitialize() { }
}
