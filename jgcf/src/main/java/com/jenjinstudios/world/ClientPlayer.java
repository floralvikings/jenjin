package com.jenjinstudios.world;

import com.jenjinstudios.math.Vector2D;
import com.jenjinstudios.world.state.MoveState;

import java.util.LinkedList;

/**
 * The Client-Side representation of a player.
 * @author Caleb Brinkman
 */
public class ClientPlayer extends ClientObject
{
	/** The queue of states saved by this player. */
	private final LinkedList<MoveState> savedStates;
	/** The player's angle relative to the origin. */
	private double absoluteAngle;
	/** The player's angle relative to {@code absoluteAngle}. */
	private double relativeAngle;
	/** The player's true, combined movement angle. */
	private double trueAngle;
	/** Flags whether the player is idle. */
	private boolean isIdle;
	/** The number of steps taken by this player since the last change of trueAngle. */
	private int stepsTaken;
	/** Flags whether this player was forced during the most recent update. */
	private boolean isForced;
	/** The recent forced state. */
	private MoveState forcedState;
	/** The new absolute angle. */
	private double newAbsoluteAngle;
	/** The new relative angle. */
	private double newRelativeAngle;
	/** Flags whether a new absolute angle has been set. */
	private boolean isNewAbsolute;
	/** Flags whether a new relative angle has been set. */
	private boolean isNewRelative;

	/**
	 * Construct an Actor with the given name.
	 * @param id The Actor's ID.
	 * @param name The name.
	 */
	public ClientPlayer(int id, String name) {
		super(id, name);
		savedStates = new LinkedList<>();
		setVector2D(0, 0);
		setRelativeAngle(MoveState.IDLE);
	}

	/**
	 * Get the absolute angle of this player.
	 * @return The absolute angle of this player.
	 */
	public double getAbsoluteAngle() { return absoluteAngle; }

	/**
	 * Set the absolute angle of this player.
	 * @param absoluteAngle The new absolute angle of this player.
	 */
	private void setAbsoluteAngle(double absoluteAngle) {
		if (forcedState != null && absoluteAngle == forcedState.absoluteAngle) { return; }
		forcedState = null;
		this.absoluteAngle = absoluteAngle;
		calculateTrueAngle();
		saveState();
	}

	/**
	 * Get the queue of states saved so far.  The states queue will be emptied after this call.
	 * @return The queue of states saved.
	 */
	public LinkedList<MoveState> getSavedStates() {
		synchronized (savedStates)
		{
			LinkedList<MoveState> temp = new LinkedList<>(savedStates);
			savedStates.clear();
			return temp;
		}
	}

	/**
	 * Get the relative angle of this player.
	 * @return The relative angle of this player.
	 */
	public double getRelativeAngle() { return relativeAngle; }

	/**
	 * Set the relative angle of the player.
	 * @param relativeAngle The new relative angle of this player.
	 */
	private void setRelativeAngle(double relativeAngle) {
		if (forcedState != null && relativeAngle == forcedState.relativeAngle) { return; }
		forcedState = null;
		this.relativeAngle = relativeAngle;
		isIdle = (relativeAngle == MoveState.IDLE);
		calculateTrueAngle();
		saveState();
	}

	@Override
	public void update() {
		setAngles();
		resetFlags();
		step();
	}

	/** Calculate and set the player's "true" movement angle. */
	private void calculateTrueAngle() {
		double sAngle = relativeAngle + absoluteAngle;
		trueAngle = (sAngle < 0) ? (sAngle + MoveState.TWO_PI) : (sAngle % MoveState.TWO_PI);
	}

	/** Add this players previous state to the queue. */
	private void saveState() {
		if (stepsTaken == 0 || isForced) { return; }
		MoveState toBeSaved = new MoveState(relativeAngle, stepsTaken, absoluteAngle);
		synchronized (savedStates) { savedStates.add(toBeSaved); }
		stepsTaken = 0;
	}

	/** Set the angles if new angles have been added. */
	private void setAngles() {
		if (isNewAbsolute) { setAbsoluteAngle(newAbsoluteAngle); }
		if (isNewRelative) { setRelativeAngle(newRelativeAngle); }
	}

	/**
	 * Set a new relative angle.
	 * @param newRelativeAngle The new angle.
	 */
	public void setNewRelativeAngle(double newRelativeAngle) {
		this.newRelativeAngle = newRelativeAngle;
		isNewRelative = true;
	}

	/**
	 * Set a new absolute angle.
	 * @param newAbsoluteAngle The new absolute angle.
	 */
	public void setNewAbsoluteAngle(double newAbsoluteAngle) {
		this.newAbsoluteAngle = newAbsoluteAngle;
		isNewAbsolute = true;
	}

	/**
	 * Force the actor to the given position and angles, then take the number of necessary steps to match {@code
	 * stepsAtForce}.
	 * @param position The position to which to force the player.
	 * @param relativeAngle The relative angle to which to force the player.
	 * @param absoluteAngle The absolute angle to which to force the player.
	 * @param stepsToTake The number of steps needed to take.
	 */
	public void forcePosition(Vector2D position, double relativeAngle, double absoluteAngle, int stepsToTake) {
		isForced = true;
		forcedState = new MoveState(this.relativeAngle, 0, this.absoluteAngle);
		resetState(position, relativeAngle, absoluteAngle);
		for (int i = 0; i < stepsToTake; i++)
			step();
	}

	/**
	 * Reset the player's state.
	 * @param position The new position.
	 * @param relativeAngle The relative angle.
	 * @param absoluteAngle The absolte angle.
	 */
	private void resetState(Vector2D position, double relativeAngle, double absoluteAngle) {
		setVector2D(position);
		synchronized (savedStates) { savedStates.clear(); }
		this.relativeAngle = relativeAngle;
		this.absoluteAngle = absoluteAngle;
		stepsTaken = 0;
		isIdle = relativeAngle == MoveState.IDLE;
	}

	/** Reset the flags associated with this player. */
	private void resetFlags() {
		isForced = false;
		isNewAbsolute = false;
		isNewRelative = false;
	}

	/**
	 * Get the number of steps taken in the current movement state.
	 * @return The number of steps taken in the current movement state.
	 */
	public int getStepsTaken() { return stepsTaken; }

	/**
	 * Get whether this player's state was forced during the most recent update.
	 * @return Whether this player's state was forced during the most recent update.
	 */
	public boolean isForcedState() { return isForced; }


	/** Move one step forward. */
	private void step() {
		stepsTaken++;
		if (!isIdle) { setVector2D(getVector2D().getVectorInDirection(ClientActor.STEP_LENGTH, trueAngle)); }
	}


	/**
	 * Set this object's current position.
	 * @param vector2D The new position.
	 */
	public void setVector2D(Vector2D vector2D) { super.setVector2D(vector2D); }

	/**
	 * Set this object' current position.
	 * @param x The new x coordinate.
	 * @param z The new z coordinate.
	 */
	public void setVector2D(double x, double z) { this.setVector2D(new Vector2D(x, z)); }
}
