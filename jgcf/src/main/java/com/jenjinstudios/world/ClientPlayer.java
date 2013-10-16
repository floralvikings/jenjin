package com.jenjinstudios.world;

import com.jenjinstudios.math.Vector2D;
import com.jenjinstudios.world.state.MoveState;

import java.util.LinkedList;

/**
 * The Client-Side representation of a player.
 *
 * @author Caleb Brinkman
 */
public class ClientPlayer extends ClientActor
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

	/**
	 * Construct an Actor with the given name.
	 *
	 * @param id   The Actor's ID.
	 * @param name The name.
	 */
	public ClientPlayer(int id, String name)
	{
		super(id, name);
		savedStates = new LinkedList<>();
		setVector2D(0, 0);
	}

	@Override
	public void update()
	{
		step();
	}

	@Override
	public void setStepsTaken(int stepsTaken)
	{
		this.stepsTaken = stepsTaken;
	}

	/** Move one step forward. */
	private void step()
	{
		stepsTaken++;
		if (!isIdle)
		{
			setVector2D(getVector2D().getVectorInDirection(STEP_LENGTH, trueAngle));
		}
	}

	/**
	 * Get the absolute angle of this player.
	 *
	 * @return The absolute angle of this player.
	 */
	public double getAbsoluteAngle()
	{
		return absoluteAngle;
	}

	/**
	 * Set the absolute angle of this player.
	 *
	 * @param absoluteAngle The new absolute angle of this player.
	 */
	public void setAbsoluteAngle(double absoluteAngle)
	{
		this.absoluteAngle = absoluteAngle;
		calculateTrueAngle();
		saveState();
	}

	/** Add this players previous state to the queue. */
	private void saveState()
	{
		MoveState toBeSaved = new MoveState(relativeAngle, stepsTaken, absoluteAngle);
		synchronized (savedStates)
		{
			savedStates.add(toBeSaved);
		}
		stepsTaken = 0;
	}

	/** Calculate and set the player's "true" movement angle. */
	private void calculateTrueAngle()
	{
		double sAngle = relativeAngle + absoluteAngle;
		trueAngle = (sAngle < 0) ? (sAngle + MoveState.TWO_PI) : (sAngle % MoveState.TWO_PI);
	}

	/**
	 * Get the queue of states saved so far.  The states queue will be emptied after this call.
	 *
	 * @return The queue of states saved.
	 */
	public LinkedList<MoveState> getSavedStates()
	{
		synchronized (savedStates)
		{
			LinkedList<MoveState> temp = new LinkedList<>(savedStates);
			savedStates.clear();
			return temp;
		}
	}

	/**
	 * Get the relative angle of this player.
	 *
	 * @return The relative angle of this player.
	 */
	public double getRelativeAngle()
	{
		return relativeAngle;
	}

	/**
	 * Set the relative angle of the player.
	 *
	 * @param relativeAngle The new relative angle of this player.
	 */
	public void setRelativeAngle(double relativeAngle)
	{
		this.relativeAngle = relativeAngle;
		isIdle = (relativeAngle == MoveState.IDLE);
		calculateTrueAngle();
		saveState();
	}

	/**
	 * Force the actor to the given position and angles, then take the number of necessary steps to match {@code
	 * stepsToTake}.
	 *
	 * @param position      The position to which to force the player.
	 * @param relativeAngle The relative angle to which to force the player.
	 * @param absoluteAngle The absolute angle to which to force the player.
	 * @param stepsToTake   The number of steps that must be taken after forcing position.
	 */
	public void forcePosition(Vector2D position, double relativeAngle, double absoluteAngle, double stepsToTake)
	{
		synchronized (savedStates) { savedStates.clear(); }
		setVector2D(position);
		this.relativeAngle = relativeAngle;
		this.absoluteAngle = absoluteAngle;
		setStepsTaken(0);

		while (this.stepsTaken < stepsToTake)
			step();
	}
}
