package com.jenjinstudios.world.actor;

import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.event.PreUpdateEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Caleb Brinkman
 */
public class Vision implements PreUpdateEvent
{
	public static final String EVENT_NAME = "visionEvent";
	private final Set<WorldObject> visibleObjects = new HashSet<>();
	private final Set<WorldObject> newlyVisibleObjects = new HashSet<>();
	private final Set<WorldObject> newlyInvisibleObjects = new HashSet<>();
	private final Set<WorldObject> visibleLastSetUp = new HashSet<>();

	@Override
	public void onPreUpdate() {
		setUpVisibleObjects();
	}

	private void setUpVisibleObjects() {
		synchronized (newlyVisibleObjects)
		{
			newlyVisibleObjects.clear();
			getVisibleObjects().stream().filter(o -> !visibleLastSetUp.contains(o)).forEach(newlyVisibleObjects::add);
		}
		synchronized (newlyInvisibleObjects)
		{
			newlyInvisibleObjects.clear();
			newlyInvisibleObjects.addAll(visibleLastSetUp.stream().filter(o ->
				  !getVisibleObjects().contains(o)).collect(Collectors.toList()));
		}
		visibleLastSetUp.clear();
		visibleLastSetUp.addAll(getVisibleObjects());
	}

	public Set<WorldObject> getVisibleObjects() {
		synchronized (visibleObjects)
		{
			return new HashSet<>(visibleObjects);
		}
	}

	public void addVisibleObject(WorldObject visible) {
		synchronized (visibleObjects)
		{
			visibleObjects.add(visible);
		}
	}

	public void clearVisibleObjects() {
		synchronized (visibleObjects)
		{
			visibleObjects.clear();
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
}
