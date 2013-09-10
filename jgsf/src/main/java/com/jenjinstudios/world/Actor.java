package com.jenjinstudios.world;

import com.jenjinstudios.world.state.MoveState;

import java.util.ArrayList;
import java.util.LinkedList;

import static com.jenjinstudios.world.state.MoveDirection.IDLE;

/**
 * Implement a WorldObject which is capable of movement.
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
public class Actor extends WorldObject
{
	/** The radius of the square of visible locations. */
	public static final int VIEW_RADIUS = 4;
	/** The length of each step. */
	public static final float STEP_LENGTH = 5;
	/** The default name of this actor. */
	public static final String DEFAULT_NAME = "Actor";
	/** The next move. */
	private final LinkedList<MoveState> nextMoveStates;
	/** The array of visible locations. */
	private final ArrayList<Location> visibleLocations;
	/** The container for visible objects. */
	private final ArrayList<WorldObject> visibleObjects;
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
		visibleObjects = new ArrayList<>();
		visibleLocations = new ArrayList<>();
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
		// Store the current location (before step)
		Location oldLocation = getLocation();
		// Take a step.
		step();
		// If we're in a new locations after stepping, update the visible array.
		if (oldLocation != getLocation() || visibleLocations.isEmpty())
			resetVisibleLocations();
		// Reset the array of visible actors.
		resetVisibleObjects();
	}

	/** Resets the array of currently visible location. */
	private void resetVisibleLocations()
	{
		visibleLocations.clear();
		visibleLocations.addAll(getWorld().getLocationArea(getLocation(), VIEW_RADIUS));
	}

	/** Reset the current list of visible objects. */
	private void resetVisibleObjects()
	{
		ArrayList<WorldObject> currentlyVisible = new ArrayList<>();
		for (Location loc : visibleLocations)
			currentlyVisible.addAll(loc.getObjects());
		visibleObjects.clear();
		visibleObjects.addAll(currentlyVisible);
	}

	/** Take a step using the current movement state. */
	private void step()
	{
		stepsTaken++;
		MoveState nextState;

		synchronized (nextMoveStates)
		{
			nextState = nextMoveStates.peek();
		}

		if (nextState != null)
			changeState();

		// Get the angle in which the player will be moving.
		double stepAngle = calculateStepAngle();

		if (currentMoveState.direction == IDLE) return;

		// TODO This will be used a lot; could it be optimized?
		double twoPi = (2 * Math.PI);
		stepAngle = stepAngle < 0 ? twoPi + stepAngle : stepAngle % twoPi;
		setVector2D(getVector2D().getVectorInDirection(STEP_LENGTH, stepAngle));
	}

	/**
	 * Calculate the angle of the direction in which the actor should step.
	 *
	 * @return The angle of the direction in which the actor should step.
	 */
	private double calculateStepAngle()
	{
		double stepAngle = getDirection();

		switch (currentMoveState.direction)
		{
			case IDLE:
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
		return stepAngle;
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

	/**
	 * The container for visible objects.
	 *
	 * @return An ArrayList containing all objects visible to this actor.
	 */
	public ArrayList<WorldObject> getVisibleObjects()
	{
		return visibleObjects;
	}
}
