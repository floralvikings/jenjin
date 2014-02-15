package com.jenjinstudios.world;

import com.jenjinstudios.world.ai.Pathfinder;
import com.jenjinstudios.world.math.MathUtil;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.state.MoveState;

import java.util.LinkedList;
import java.util.TreeMap;

/**
 * This class represents a Non-Player Character.
 * @author Caleb Brinkman
 */
public class NPC extends Actor
{
	/** The list of targets to which a wandering NPC will move. */
	private final LinkedList<Location> wanderTargets;
	/** The behavior flags associated with this NPC. */
	private TreeMap<String, Boolean> behaviorFlags;
	/** The Location at which the NPC began following a player. */
	private Location startLocation;
	/** The location toward which this NPC is to move. */
	private Location targetLocation;
	/** The player currently being targeted. */
	private Player targetPlayer;
	/** The index of the current wander target. */
	private int wanderTargetIndex;

	/**
	 * Construct an NPC with the given name.
	 * @param name The name.
	 */
	public NPC(String name) {
		this(name, new TreeMap<String, Boolean>());
	}

	/**
	 * Construct an NPC with the given name and behavior flags.
	 * @param name The name of the player.
	 * @param behaviorFlags The behavior flags.
	 */
	public NPC(String name, TreeMap<String, Boolean> behaviorFlags) {
		super(name);
		this.behaviorFlags = behaviorFlags;
		wanderTargets = new LinkedList<>();
	}

	@Override
	public void setUp() {
		super.setUp();
		if (behaviorFlags.get("aggressive") != null && behaviorFlags.get("aggressive"))
		{
			doAggressiveBehavior();
		}
		if (behaviorFlags.get("wanders") != null && behaviorFlags.get("wanders"))
		{
			doWandersBehavior();
		}

	}

	/**
	 * Plot a path to the given Location, and begin following it immediately.
	 * @param target The target location.
	 */
	public void plotPath(Location target) {
		if (target == null)
		{
			return;
		}
		clearMoveStates();
		LinkedList<Location> path = Pathfinder.findPath(getLocation(), target);
		// Start will be current location.
		Location prev = path.pop();
		Vector2D start = getVector2D();
		Vector2D prevCenter = prev.getCenter();
		double absAngle = getVector2D().getAngleToVector(prevCenter);
		// Create a move state to start moving forward, right now, toward the target vector.
		MoveState moveState = new MoveState(MoveState.FRONT, getStepsTaken(), absAngle);
		addMoveState(moveState);

		while (!path.isEmpty())
		{
			Vector2D nextCenter = path.pop().getCenter();
			int stepsToTake = (int) MathUtil.round(start.getDistanceToVector(prevCenter) / Actor.STEP_LENGTH, 0);
			absAngle = prevCenter.getAngleToVector(nextCenter);

			MoveState nextState = new MoveState(MoveState.FRONT, stepsToTake, absAngle);
			addMoveState(nextState);

			start = prevCenter;
			prevCenter = nextCenter;

			if (path.isEmpty())
			{
				stepsToTake = (int) MathUtil.round(start.getDistanceToVector(prevCenter) / Actor.STEP_LENGTH, 0);
				MoveState idleState = new MoveState(MoveState.IDLE, stepsToTake, getCurrentMoveState().absoluteAngle);
				addMoveState(idleState);
			}
		}
	}

	/**
	 * Add the specified Location to the list of possible wandering targets.
	 * @param newTarget The Location to add to the target.
	 */
	public void addWanderTarget(Location newTarget) {
		synchronized (wanderTargets)
		{
			wanderTargets.add(newTarget);
		}
	}

	/** Perform the behavior of an NPC that "wanders". */
	private void doWandersBehavior() {
		if (targetPlayer == null && (targetLocation == getLocation() || targetLocation == null) && !wanderTargets.isEmpty())
		{
			if (getCurrentMoveState().relativeAngle == MoveState.IDLE && getNextState() == null)
			{
				/* The amount of steps for which the NPC should idle in between reaching targets. */
				int idleTimeBetweenTargets = 100;
				if (getStepsTaken() >= idleTimeBetweenTargets)
				{
					targetLocation = wanderTargets.get(wanderTargetIndex);
					plotPath(targetLocation);
					if (++wanderTargetIndex >= wanderTargets.size())
					{
						wanderTargetIndex = 0;
					}
				}
			}
		}
	}

	/** Perform the behavior signature of an NPC that is "aggressive". */
	private void doAggressiveBehavior() {
		if (targetPlayer == null && (targetLocation == null || targetLocation != startLocation))
		{
			targetPlayer = findPlayer();
			targetLocation = targetPlayer != null ? targetPlayer.getLocation() : startLocation;
			startLocation = targetLocation != null ? getLocation() : null;
			plotPath(targetLocation);
		} else if (targetPlayer != null)
		{
			if (getLocation() == targetLocation)
			{
				targetPlayer = null;
				targetLocation = startLocation;
				plotPath(targetLocation);
			} else if (!targetLocation.getObjects().contains(targetPlayer))
			{
				if (getVisibleObjects().get(targetPlayer.getId()) != null)
				{
					targetLocation = targetPlayer.getLocation();
					plotPath(targetLocation);
				} else
				{
					targetLocation = startLocation;
					plotPath(targetLocation);
				}
			}
		}
	}

	/**
	 * Get a player from the map of visible objects.
	 * @return The player, or null if none is found.
	 */
	private Player findPlayer() {
		for (WorldObject object : getVisibleObjects().values())
			if (object instanceof Player) return (Player) object;
		return null;
	}
}
