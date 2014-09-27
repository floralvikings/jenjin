package com.jenjinstudios.world;

import com.jenjinstudios.world.actor.VisionOnPreUpdate;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.state.MoveState;
import com.jenjinstudios.world.util.ZoneUtils;

import java.util.LinkedList;


/**
 * Implement a WorldObject which is capable of movement. </p> Actors start with a {@code MoveState} with {@code
 * MoveState.IDLE}.  Each update, the Actor checks to see if there are any MoveStates in the queue.  If there are, it
 * checks the first state in line for the number of steps needed before the state changes.  Once the number of steps has
 * been reached, the state switches to that of the first position in the queue, and the Actor's step counter is reset.
 * If an Actor "oversteps," which is determined if the Actor has taken more than the required number of steps to change
 * state, the Actor is moved back by the "overstepped" number of states, the Actor's state is updated, and the Actor
 * then takes the number of extra steps in the correct relativeAngle. </p> An Actor's state is considered "changed" when
 * the Actor is facing a new relativeAngle or moving in a new relativeAngle. An actor's state is considered "forced"
 * when the Actor attempts to make an illegal move, and the world forces the actor to halt.  The actor's forced state
 * will always be facing the angle of the most recently added move state (even if the state causes an illegal move) and
 * IDLE. The "steps until change" value is determined from the number of steps that were taken until the state was
 * forced.
 * @author Caleb Brinkman
 */
public class Actor extends WorldObject
{
	public static double DEFAULT_MOVE_SPEED = 30.0d;
	private final LinkedList<MoveState> stateChanges;
	private double moveSpeed;
	private boolean newState;
	private MoveState forcedState;
	private long lastStepTime;
	private Angle newAngle;
	private Vector2D vectorBeforeUpdate;

	public Actor(String name) {
		super(name);
		newAngle = getAngle();
		stateChanges = new LinkedList<>();
		setMoveSpeed(DEFAULT_MOVE_SPEED);
		addPreUpdateEvent(VisionOnPreUpdate.EVENT_NAME, new VisionOnPreUpdate());
	}

	@Override
	public void preUpdate() {
		super.preUpdate();
		vectorBeforeUpdate = getVector2D();
		forcedState = null;
		synchronized (stateChanges)
		{
			stateChanges.clear();
		}
	}

	@Override
	public void postUpdate() {
		super.postUpdate();
		if (vectorBeforeUpdate == null) vectorBeforeUpdate = getVector2D();
		if (newState)
		{
			newState = false;
			resetAngles();
			//Vector2D beforeStep = new Vector2D(vectorBeforeUpdate);
			synchronized (stateChanges)
			{
				stateChanges.add(new MoveState(getAngle(), getVector2D(), getLastStepTime()));
			}
		}
	}

	@Override
	public void setAngle(Angle angle) {
		boolean forcedStateMatch = getForcedState() != null && angle.equals(getForcedState().angle);
		if ((newState || !getAngle().equals(angle)) && !forcedStateMatch)
		{
			setForcedState(null);
			newState = true;
			this.newAngle = angle;
		}
	}

	@Override
	public void initialize() {
		super.initialize();
		resetAngles();
	}

	@Override
	public void update() {
		if (getLastStepTime() == 0)
		{
			setLastStepTime(getWorld().getLastUpdateCompleted());
		}
		step();
		setLastStepTime(System.currentTimeMillis());
	}

	public LinkedList<MoveState> getStateChanges() {
		synchronized (stateChanges) { return new LinkedList<>(stateChanges); }
	}

	public MoveState getForcedState() { return forcedState; }

	public void setForcedState(MoveState forcedState) { this.forcedState = forcedState; }

	/** Mark that the actor has been forced to its current position. */
	public void forcePosition() {
		MoveState forcedMoveState = new MoveState(getAngle(), getVector2D(), getLastStepTime());
		setForcedState(forcedMoveState);
		setVector2D(getVector2D());
		setAngle(getAngle());
		newState = true;
	}

	@SuppressWarnings("WeakerAccess")
	public void forceIdle() {
		Angle idle = getAngle().asIdle();
		MoveState forcedMoveState = new MoveState(idle, getVector2D(), getLastStepTime());
		setForcedState(forcedMoveState);
		setVector2D(getVector2D());
		setAngle(idle);
		newState = true;
	}

	public long getLastStepTime() { return lastStepTime; }

	public void setLastStepTime(long lastStepTime) { this.lastStepTime = lastStepTime; }

	public Vector2D getVectorBeforeUpdate() { return vectorBeforeUpdate; }

	public double getMoveSpeed() {
		return moveSpeed;
	}

	public void setMoveSpeed(double moveSpeed) {
		this.moveSpeed = moveSpeed;
	}

	protected double calcStepLength() {
		return ((System.currentTimeMillis() - (double) getLastStepTime()) / 1000) * getMoveSpeed();
	}

	private boolean stepForward(double stepLength) {
		boolean didStep;
		if (getAngle().isNotIdle())
		{
			Vector2D newVector = getVector2D().getVectorInDirection(stepLength, getAngle().getStepAngle());
			Location newLocation = ZoneUtils.getLocationForCoordinates(getWorld(), getZoneID(), newVector);
			if (newLocation != null)
			{
				boolean walkable = !"false".equals(newLocation.getProperties().get("walkable"));
				if (walkable) { setVector2D(newVector); }
				didStep = walkable;
			} else
			{
				didStep = false;
			}
		} else
		{
			didStep = true;
		}
		return didStep;
	}

	private void resetAngles() { super.setAngle(newAngle); }

	void step() {
		double stepLength = calcStepLength();
		if (!stepForward(stepLength))
		{
			setForcedState(new MoveState(getAngle().asIdle(), getVector2D(), lastStepTime));
			setAngle(getAngle().asIdle());
		}
	}
}
