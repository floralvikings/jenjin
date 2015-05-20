package com.jenjinstudios.world.object;

import com.jenjinstudios.world.event.WorldObjectObserver;
import com.jenjinstudios.world.math.Geometry2D;
import com.jenjinstudios.world.task.TimingTask;
import com.jenjinstudios.world.task.WorldObjectTask;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Represents an object that exists in the game world.
 * @author Caleb Brinkman
 */
public class WorldObject
{
	private final Collection<WorldObjectTask> tasks;
	private final Collection<WorldObjectObserver> observers;
	private final Identification identification;
	private final Vision vision;
	private final Timing timing;
	private final String name;
	private Geometry2D geometry2D;
	private int zoneID;

	public WorldObject(String name) {
		this.name = name;
		tasks = new LinkedList<>();
		observers = new LinkedList<>();
		identification = new Identification();
		geometry2D = new Geometry2D();
		vision = new Vision();
		timing = new Timing();
		addTask(new TimingTask());
		addObserver(vision.getNewlyVisibleObserver());
		addObserver(vision.getNewlyInvisibleObserver());
	}

	public void addTask(WorldObjectTask task) { tasks.add(task); }

	public Collection<WorldObjectTask> getTasks() { return Collections.unmodifiableCollection(tasks); }

	public void addObserver(WorldObjectObserver observer) {
		observers.add(observer);
	}

	public void removeObserver(WorldObjectObserver observer) {
		observers.remove(observer);
	}

	public Collection<WorldObjectObserver> getObservers() {
		return Collections.unmodifiableCollection(observers);
	}

	public int getZoneID() { return zoneID; }

	public void setZoneID(int zoneID) { this.zoneID = zoneID; }

	public String getName() { return name; }

	public Geometry2D getGeometry2D() { return geometry2D; }

	public void setGeometry2D(Geometry2D geometry) { this.geometry2D = geometry; }

	public Vision getVision() { return vision; }

	public Timing getTiming() { return timing; }

	public Identification getIdentification() { return identification; }

	@Override
	public String toString() { return name + ": " + identification.getId(); }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof WorldObject)) return false;

		WorldObject that = (WorldObject) o;

		return (identification.getId() == that.getIdentification().getId()) &&
			  name.equals(that.getName());

	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + identification.getId();
		return result;
	}
}
