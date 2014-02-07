package com.jenjinstudios.world;

import com.jenjinstudios.world.ai.Pathfinder;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.state.MoveState;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * A PathFollower is an Actor with a list of target Locations, from which it randomly chooses targets to which to
 * travel.  After reaching a target, it will wait a specified amount of time before travelling to another.  This time
 * can be specified to 0 if it is desired for the actor to immediately walk to another location, or randomized to create
 * the illusion of AI.
 * @author Caleb Brinkman
 */
public class PathFollower extends Actor
{
	/** The default name for PathFollowers. */
	public static final String DEFAULT_NAME = "Path Follower";
	/** The list of potential target locations. */
	private final ArrayList<Location> targetLocations;
	/** The coordinates to which to move next. */
	private Vector2D targetCoordinates;
	/** The current target location. */
	private Location currentTargetLocation;
	/** The location previously targeted. */
	private Location previousTargetLocation;
	/** The current path. */
	private LinkedList<Location> currentPath;
	/** The time to idle before starting movement again. */
	private int idleTime = 50;

	/** Construct a default PathFollower. */
	public PathFollower() {
		this(DEFAULT_NAME);
	}

	/**
	 * Construct a PathFollower with the given name.
	 * @param name The name.
	 */
	public PathFollower(String name) {
		super(name);
		targetLocations = new ArrayList<>();
	}

	/**
	 * Construct a new PathFollower with the given name and ID.
	 * @param name The name.
	 * @param id The id.
	 */
	public PathFollower(String name, int id) {
		super(name, id);
		targetLocations = new ArrayList<>();
	}

	/**
	 * Add a target to the list.
	 * @param target The target.
	 */
	public void addTargetLocation(Location target) {
		synchronized (targetLocations)
		{
			targetLocations.add(target);
		}
	}

	public void update() {
		super.update();
		if (currentTargetLocation == null)
		{
			synchronized (targetLocations)
			{
				int targetIndex = (int) (Math.random() * targetLocations.size());
				currentTargetLocation = targetLocations.get(targetIndex);
			}
			if(currentTargetLocation == previousTargetLocation)
			{
				return;
			}
			currentPath = Pathfinder.findPath(getLocation(), currentTargetLocation);
			targetCoordinates = currentPath.pop().getCenter();
			double targetAngle = getVector2D().getAngleToVector(targetCoordinates);
			MoveState nextState = new MoveState(MoveState.FRONT, idleTime, targetAngle);
			addMoveState(nextState);
		}
		if (getVector2D().getDistanceToVector(targetCoordinates) < Actor.STEP_LENGTH)
		{
			if (currentTargetLocation == getLocation())
			{
				MoveState idleState = new MoveState(MoveState.IDLE, getStepsTaken(), getCurrentMoveState().absoluteAngle);
				addMoveState(idleState);
				previousTargetLocation = currentTargetLocation;
				currentTargetLocation = null;
			} else
			{
				targetCoordinates = currentPath.pop().getCenter();
				double targetAngle = getVector2D().getAngleToVector(targetCoordinates);
				MoveState nextState = new MoveState(MoveState.FRONT, getStepsTaken(), targetAngle);
				addMoveState(nextState);
			}
		}
	}
}
