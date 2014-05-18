package com.jenjinstudios.world;

import com.jenjinstudios.world.ai.Pathfinder;
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
	/** The path currently being followed by this actor. */
	private final LinkedList<Vector2D> currentPath;

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
		currentPath = new LinkedList<>();
	}

	@Override
	public void setUp() {
		super.setUp();
		if (behaviorFlags.get("aggressive") != null && behaviorFlags.get("aggressive")) {
			doAggressiveBehavior();
		}
		if (behaviorFlags.get("wanders") != null && behaviorFlags.get("wanders")) {
			doWandersBehavior();
		}

	}

	@Override
	public void update() {
		super.update();
		followPath();
	}

	/** Continue following the current path by choosing a new target if necessary. */
	private void followPath() {
		Vector2D target = currentPath.peek();
		if (target != null) {
			if (getCurrentMoveState().relativeAngle == MoveState.IDLE) {
				double angle = getVector2D().getAngleToVector(target);
				addMoveState(new MoveState(MoveState.FRONT, getStepsTaken(), angle));
				return;
			}
			double distance = getVector2D().getDistanceToVector(target);
			if (distance <= Actor.STEP_LENGTH) {
				currentPath.pop();
				Vector2D newTarget = currentPath.peek();
				if (newTarget != null) {
					double angle = getVector2D().getAngleToVector(newTarget);
					addMoveState(new MoveState(MoveState.FRONT, getStepsTaken(), angle));
				} else {
					addMoveState(new MoveState(MoveState.IDLE, getStepsTaken(), getCurrentMoveState().absoluteAngle));
				}
			}
		}
	}

	/**
	 * Plot a path to the given Location, and begin following it immediately.
	 * @param target The target location.
	 */
	public void plotPath(Location target) {
		if (target == null) {
			return;
		}
		clearMoveStates();
		currentPath.clear();
		LinkedList<Location> path = Pathfinder.findPath(getLocation(), target);
		// Start will be current location.
		if (path.isEmpty()) {
			return;
		}

		while (!path.isEmpty()) {
			// TODO save angle and refrain from resetting state if angle is the same.  Save bandwidth.
			Vector2D nextCenter = path.pop().getCenter();
			currentPath.add(nextCenter);
		}
	}

	/**
	 * Add the specified Location to the list of possible wandering targets.
	 * @param newTarget The Location to add to the target.
	 */
	public void addWanderTarget(Location newTarget) {
		synchronized (wanderTargets) {
			wanderTargets.add(newTarget);
		}
	}

	/** Perform the behavior of an NPC that "wanders". */
	private void doWandersBehavior() {
		if (targetPlayer == null && (targetLocation == getLocation() || targetLocation == null) && !wanderTargets.isEmpty()) {
			if (getCurrentMoveState().relativeAngle == MoveState.IDLE && getNextState() == null) {
				/* The amount of steps for which the NPC should idle in between reaching targets. */
				int idleTimeBetweenTargets = 100;
				if (getStepsTaken() >= idleTimeBetweenTargets) {
					targetLocation = wanderTargets.get(wanderTargetIndex);
					plotPath(targetLocation);
					if (++wanderTargetIndex >= wanderTargets.size()) {
						wanderTargetIndex = 0;
					}
				}
			}
		}
	}

	/** Perform the behavior signature of an NPC that is "aggressive". */
	private void doAggressiveBehavior() {
		if (targetPlayer == null && (targetLocation == null || targetLocation != startLocation)) {
			targetPlayer = findPlayer();
			targetLocation = targetPlayer != null ? targetPlayer.getLocation() : startLocation;
			startLocation = targetLocation != null ? getLocation() : null;
			plotPath(targetLocation);
		} else if (getLocation() == targetLocation && targetPlayer != null) {
			if (!targetLocation.getObjects().contains(targetPlayer)) {
				if (getVisibleObjects().get(targetPlayer.getId()) != null) {
					targetLocation = targetPlayer.getLocation();
					plotPath(targetLocation);
				} else {
					targetLocation = startLocation;
					targetPlayer = null;
					plotPath(targetLocation);
				}
			} else {
				targetPlayer = null;
				targetLocation = startLocation;
				plotPath(targetLocation);
			}
		} else if (getLocation() == startLocation && targetLocation == startLocation) {
			targetLocation = null;
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
