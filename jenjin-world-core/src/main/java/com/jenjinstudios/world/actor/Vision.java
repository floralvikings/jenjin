package com.jenjinstudios.world.actor;

import com.jenjinstudios.world.WorldObject;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Caleb Brinkman
 */
public class Vision
{
	public static final String PROPERTY_NAME = "visionEvent";
	private final Set<WorldObject> visibleObjects = new HashSet<>();
	private final Set<WorldObject> newlyVisibleObjects = new HashSet<>();
	private final Set<WorldObject> newlyInvisibleObjects = new HashSet<>();

	public Set<WorldObject> getVisibleObjects() {
		synchronized (visibleObjects)
		{
			return new HashSet<>(visibleObjects);
		}
	}

	public Set<WorldObject> getNewlyVisibleObjects() {
		synchronized (newlyVisibleObjects)
		{
			return new HashSet<>(newlyVisibleObjects);
		}
	}

	public Set<WorldObject> getNewlyInvisibleObjects() {
		synchronized (newlyInvisibleObjects)
		{
			return new HashSet<>(newlyInvisibleObjects);
		}
	}

	public void setVisibleObjects(Set<WorldObject> currentlyVisible) {
		synchronized (newlyVisibleObjects)
		{
			newlyVisibleObjects.clear();
			currentlyVisible.stream().
				  filter(o -> !getVisibleObjects().contains(o)).
				  forEach(newlyVisibleObjects::add);
		}
		synchronized (newlyInvisibleObjects)
		{
			newlyInvisibleObjects.clear();
			getVisibleObjects().stream().
				  filter(o -> !currentlyVisible.contains(o)).
				  forEach(newlyInvisibleObjects::add);
		}
		synchronized (visibleObjects)
		{
			visibleObjects.clear();
			visibleObjects.addAll(currentlyVisible);
		}
	}

}
