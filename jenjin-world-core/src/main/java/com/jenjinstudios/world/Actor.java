package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.state.MoveState;

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
public class Actor extends SightedObject
{
	public static final double MOVE_SPEED = 10.0d;
	private final LinkedList<MoveState> stateChanges;
	private boolean newState;
	private MoveState forcedState;
	private long lastStepTime;
	private Angle newAngle;
	private Vector2D vectorBeforeUpdate;

	public Actor(String name) {
		super(name);
		stateChanges = new LinkedList<>();
	}

	@Override
	public void setUp() {
		super.setUp();
		vectorBeforeUpdate = getVector2D();
		forcedState = null;
		synchronized (stateChanges)
		{
			stateChanges.clear();
		}
	}

	@Override
	public void reset() {
		super.reset();
		if (vectorBeforeUpdate == null) vectorBeforeUpdate = getVector2D();
		if (newState)
		{
			newState = false;
			resetAngles();
			Vector2D beforeStep = new Vector2D(vectorBeforeUpdate);
			synchronized (stateChanges)
			{
				stateChanges.add(new MoveState(getAngle(), beforeStep, getLastStepTime()));
			}
		}
	}

	@Override
	public void setAngle(Angle angle) {
		if (!getAngle().equals(angle))
		{
			newState = true;
			this.newAngle = angle;
		}
	}

	@Override
	public void update() {
		if (getLastStepTime() == 0)
		{
			setLastStepTime(getWorld().getLastUpdateCompleted());
		}
		step();
		setLastStepTime(System.nanoTime());
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
	}

	public double calcStepLength() {
		return ((System.nanoTime() - (double) getLastStepTime()) / 1000000000) * Actor.MOVE_SPEED;
	}

	public long getLastStepTime() { return lastStepTime; }

	public void setLastStepTime(long lastStepTime) { this.lastStepTime = lastStepTime; }

	public Vector2D getVectorBeforeUpdate() { return vectorBeforeUpdate; }

	private boolean stepForward(double stepLength) {
		boolean didStep;
		if (!getAngle().isIdle())
		{
			Vector2D newVector = getVector2D().getVectorInDirection(stepLength, getAngle().getStepAngle());
			Location newLocation = getWorld().getLocationForCoordinates(getZoneID(), newVector);
			if (newLocation != null)
			{
				boolean walkable = !"false".equals(newLocation.getProperties().getProperty("walkable"));
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
