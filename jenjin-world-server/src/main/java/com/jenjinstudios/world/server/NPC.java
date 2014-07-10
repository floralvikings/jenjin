package com.jenjinstudios.world.server;

import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.LocationUtil;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.server.ai.Pathfinder;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TreeMap;

/**
 * This class represents a Non-Player Character.
 * @author Caleb Brinkman
 */
public class NPC extends Actor
{
	private static final double TARGET_DISTANCE = 0.2;
	private final List<Location> wanderTargets;
	private final TreeMap<String, Boolean> behaviorFlags;
	private final Queue<Vector2D> currentPath;
	private Location startLocation;
	private Location currentTargetLocation;
	private Player currentTargetPlayer;
	private int wanderTargetIndex;

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

	public void addWanderTarget(Location newTarget) {
		synchronized (wanderTargets)
		{
			wanderTargets.add(newTarget);
		}
	}

	/**
	 * Plot a path to the given Location, and begin following it immediately.
	 * @param target The target location.
	 */
	private void plotPath(Location target) {
		if (target != null)
		{
			currentPath.clear();
			Pathfinder pathfinder = new Pathfinder(getLocation(), target);
			LinkedList<Location> path = pathfinder.findPath();
			// Start will be current location.
			while (!path.isEmpty())
			{
				Vector2D nextCenter = LocationUtil.getCenter(path.pop());
				currentPath.add(nextCenter);
			}
		}
	}

	private void followPath() {
		Vector2D target = currentPath.peek();
		if (target != null)
		{
			if (getAngle().isIdle())
			{
				startMovingToTarget(target);
			} else
			{
				changeTargetIfNecessary(target);
			}
		}
	}

	private void changeTargetIfNecessary(Vector2D target) {
		double distance = getVector2D().getDistanceToVector(target);
		if (distance <= TARGET_DISTANCE)
		{
			currentPath.remove();
			Vector2D newTarget = currentPath.peek();
			if (newTarget != null)
			{
				startMovingToTarget(newTarget);
			} else
			{
				setAngle(getAngle().asIdle());
			}
		}
	}

	private void startMovingToTarget(Vector2D target) {
		double angle = getVector2D().getAngleToVector(target);
		Angle newAngle = new Angle(angle, Angle.FRONT);
		setAngle(newAngle);
	}

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

	private void doAggressiveBehavior() {
		if (shouldFindNewTargetPlayer())
		{
			findNewTargetPlayer();
		} else if (isAtPlayersPreviousLocation())
		{
			if (atPlayerLocation())
			{
				returnToStartLocationAfterReachingPlayer();
			} else
			{
				if (canSeeTargetPlayer())
				{
					targetNewPlayerLocation();
				} else
				{
					targetStartLocation();
				}
			}
		} else if (reachedStartLocation())
		{
			currentTargetLocation = null;
		}
	}

	private boolean atPlayerLocation() {return currentTargetLocation.getObjects().contains(currentTargetPlayer);}

	private void targetStartLocation() {
		currentTargetLocation = startLocation;
		currentTargetPlayer = null;
		plotPath(currentTargetLocation);
	}

	private void targetNewPlayerLocation() {
		currentTargetLocation = currentTargetPlayer.getLocation();
		plotPath(currentTargetLocation);
	}

	private boolean canSeeTargetPlayer() {return getVisibleObjects().get(currentTargetPlayer.getId()) != null;}

	private boolean reachedStartLocation() {
		return getLocation() == startLocation && currentTargetLocation == startLocation;
	}

	private void returnToStartLocationAfterReachingPlayer() {
		currentTargetPlayer = null;
		currentTargetLocation = startLocation;
		plotPath(currentTargetLocation);
	}

	private boolean isAtPlayersPreviousLocation() {
		return getLocation() == currentTargetLocation && currentTargetPlayer != null;
	}

	private void findNewTargetPlayer() {
		currentTargetPlayer = findNewPlayer();
		currentTargetLocation = currentTargetPlayer != null ? currentTargetPlayer.getLocation() : startLocation;
		startLocation = currentTargetLocation != null ? getLocation() : null;
		plotPath(currentTargetLocation);
	}

	private boolean shouldFindNewTargetPlayer() {
		return currentTargetPlayer == null && (currentTargetLocation == null ||
			  currentTargetLocation != startLocation);
	}

	private Player findNewPlayer() {
		Player player = null;
		for (WorldObject object : getVisibleObjects().values())
			if (object instanceof Player) player = (Player) object;
		return player;
	}
}
