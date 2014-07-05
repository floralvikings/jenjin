package com.jenjinstudios.world;

import com.jenjinstudios.world.ai.Pathfinder;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;

import java.util.*;

/**
 * This class represents a Non-Player Character.
 * @author Caleb Brinkman
 */
public class NPC extends Actor
{
	/** Dictates how close an actor must be to a target before the actor is considered to have "reached" it. */
	private static final double TARGET_DISTANCE = 0.2;
	/** The list of targets to which a wandering NPC will move. */
	private final List<Location> wanderTargets;
	/** The behavior flags associated with this NPC. */
	private final TreeMap<String, Boolean> behaviorFlags;
	/** The Location at which the NPC began following a player. */
	private Location startLocation;
	/** The location toward which this NPC is to move. */
	private Location targetLocation;
	/** The player currently being targeted. */
	private Player targetPlayer;
	/** The index of the current wander target. */
	private int wanderTargetIndex;
	/** The path currently being followed by this actor. */
	private final Queue<Vector2D> currentPath;

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
		if (behaviorFlags.get("aggressive") != null && behaviorFlags.get("aggressive"))
		{
			doAggressiveBehavior();
		}
		if (behaviorFlags.get("wanders") != null && behaviorFlags.get("wanders"))
		{
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
		if (target != null)
		{
			if (getAngle().isIdle())
			{
				double angle = getVector2D().getAngleToVector(target);
				Angle newAngle = new Angle(angle, Angle.FRONT);
				setAngle(newAngle);
				return;
			}
			double distance = getVector2D().getDistanceToVector(target);
			if (distance <= TARGET_DISTANCE)
			{
				currentPath.remove();
				Vector2D newTarget = currentPath.peek();
				if (newTarget != null)
				{
					double angle = getVector2D().getAngleToVector(newTarget);
					Angle newAngle = new Angle(angle, Angle.FRONT);
					setAngle(newAngle);
				} else
				{
					setAngle(getAngle().asIdle());
				}
			}
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
		currentPath.clear();
		LinkedList<Location> path = Pathfinder.findPath(getLocation(), target);
		// Start will be current location.
		if (path.isEmpty())
		{
			return;
		}

		while (!path.isEmpty())
		{
			// TODO save angle and refrain from resetting state if angle is the same.  Save bandwidth.
			Vector2D nextCenter = LocationUtil.getCenter(path.pop());
			currentPath.add(nextCenter);
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
		if (currentPath.isEmpty())
		{
			plotPath(wanderTargets.get(wanderTargetIndex));
			wanderTargetIndex++;
			if (wanderTargetIndex == wanderTargets.size())
			{
				wanderTargetIndex = 0;
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
		} else if (getLocation() == targetLocation && targetPlayer != null)
		{
			if (!targetLocation.getObjects().contains(targetPlayer))
			{
				if (getVisibleObjects().get(targetPlayer.getId()) != null)
				{
					targetLocation = targetPlayer.getLocation();
					plotPath(targetLocation);
				} else
				{
					targetLocation = startLocation;
					targetPlayer = null;
					plotPath(targetLocation);
				}
			} else
			{
				targetPlayer = null;
				targetLocation = startLocation;
				plotPath(targetLocation);
			}
		} else if (getLocation() == startLocation && targetLocation == startLocation)
		{
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
