package com.jenjinstudios.jgsf.world.actor;

import com.jenjinstudios.jgcf.world.state.MoveState;
import com.jenjinstudios.jgsf.world.GameObject;

import java.util.LinkedList;

import static com.jenjinstudios.jgcf.world.state.MoveDirection.IDLE;

/**
 * Implement a GameObject which is capable of movement.
 * </p>
 * Actors start with a {@code MoveState} with {@code MoveiDirection.IDLE}.  Each update, the Actor checks to see
 * if there are any MoveStates in the queue.  If there are, it checks the first state in line for the number of steps
 * needed before the state changes.  Once the number of steps has been reached, the state switches to that of the first
 * position in the queue, and the Actor's step counter is reset.  If an Actor "oversteps," which is determined if the
 * Actor has taken more than the required number of steps to change state, the Actor is moved back by the "overstepped"
 * number of states, the Actor's state is updated, and the Actor then takes the number of extra steps in the correct
 * direction.
 * </p>
 * An Actor's state is considered "changed" when the Actor is facing a new direction or moving in a new direction.
 *
 * @author Caleb Brinkman
 */
public class Actor extends GameObject
{
	/** The length of each step. */
	public static final float STEP_LENGTH = 5;
	/** The default name of this actor. */
	public static final String DEFAULT_NAME = "Actor";
	/** The next move. */
	private final LinkedList<MoveState> nextMoveStates;
	/** The current move. */
	private MoveState currentMoveState;
	/** The number of steps taken since the last move. */
	private int stepsTaken = 0;
	/** The name of this actor. */
	private String name;

	/** Construct a new Actor. */
	public Actor()
	{
		this(DEFAULT_NAME);

	}

	/**
	 * Construct an Actor with the given name.
	 *
	 * @param name The name.
	 */
	public Actor(String name)
	{
		this.name = name;
		currentMoveState = new MoveState(IDLE, 0, 0);
		nextMoveStates = new LinkedList<>();
	}

	/**
	 * Add a new MoveState to the actor's queue.
	 *
	 * @param newState The MoveState to add.
	 */
	public void addMoveState(MoveState newState)
	{
		synchronized (nextMoveStates)
		{
			nextMoveStates.add(newState);
		}
	}

	@Override
	public void update()
	{
		step();
	}

	/** Take a step using the current movement state. */
	private void step()
	{
		stepsTaken++;
		MoveState nextState;
		double stepAngle = getDirection();
		boolean isIdle = false;
		synchronized (nextMoveStates)
		{
			nextState = nextMoveStates.peek();
		}

		if (nextState != null)
			changeState();

		switch (currentMoveState.direction)
		{
			case IDLE:
				isIdle = true;
				break;
			case FRONT:
				break;
			case FRONT_RIGHT:
				stepAngle = (getDirection() - Math.PI * 0.25);
				break;
			case RIGHT:
				stepAngle = (getDirection() - Math.PI * 0.5);
				break;
			case BACK_RIGHT:
				stepAngle = (getDirection() - Math.PI * 0.75);
				break;
			case BACK:
				stepAngle = (getDirection() + Math.PI);
				break;
			case BACK_LEFT:
				stepAngle = (getDirection() + Math.PI * 0.75);
				break;
			case LEFT:
				stepAngle = (getDirection() + Math.PI * 0.5);
				break;
			case FRONT_LEFT:
				stepAngle = (getDirection() + Math.PI * 0.25);
				break;
		}

		if (!isIdle)
		{
			double twoPi = (2 * Math.PI);
			stepAngle = stepAngle < 0 ? twoPi + stepAngle : stepAngle % twoPi;
			setVector2D(getVector2D().getVectorInDirection(STEP_LENGTH, stepAngle));
		}


	}

	/** Change to the next state. */
	private void changeState()
	{
		if (stepsTaken >= currentMoveState.stepsInLastMove)
		{
			currentMoveState = nextMoveStates.remove();
			stepsTaken = 0;
		}
	}

	/**
	 * Get the name of this actor.
	 *
	 * @return The name of this actor.
	 */
	public String getName()
	{
		return name;
	}
}
