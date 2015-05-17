package com.jenjinstudios.world.object;

import com.jenjinstudios.world.World;
import com.jenjinstudios.world.math.Geometry2D;
import com.jenjinstudios.world.state.MoveState;
import com.jenjinstudios.world.task.StateChangeTask;
import com.jenjinstudios.world.task.TimingTask;
import com.jenjinstudios.world.task.WorldObjectTask;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents an object that exists in the game world.
 * @author Caleb Brinkman
 */
public class WorldObject
{
	private final Collection<WorldObjectTask> tasks = new LinkedList<>();
	private final StateChangeTask stateChangeTask = new StateChangeTask();
	private final Vision vision = new Vision();
	private final Timing timing = new Timing();
	private final String name;
	private Geometry2D geometry2D = new Geometry2D();
	private int zoneID;
	private int resourceID;
	private int id = Integer.MIN_VALUE;
	private World world;

	public WorldObject(String name) {
		this.name = name;
		addTask(new TimingTask());
		addTask(stateChangeTask);
		addTask(vision.getVisionTask());
	}

	public List<MoveState> getStateChanges() { return stateChangeTask.getStateChanges(); }

	public void addTask(WorldObjectTask task) { tasks.add(task); }

	public Collection<WorldObjectTask> getTasks() { return Collections.unmodifiableCollection(tasks); }

	public int getResourceID() { return resourceID; }

	public void setResourceID(int resourceID) { this.resourceID = resourceID; }

	public int getId() { return id; }

	public void setId(int id) { this.id = id; }

	public World getWorld() { return world; }

	public void setWorld(World world) {
		if (this.world != null)
			throw new IllegalArgumentException("The world has already been set for this object.");
		this.world = world;
	}

	public int getZoneID() { return zoneID; }

	public void setZoneID(int zoneID) { this.zoneID = zoneID; }

	public String getName() { return name; }

	public Geometry2D getGeometry2D() { return geometry2D; }

	public void setGeometry2D(Geometry2D geometry) { this.geometry2D = geometry; }

	public Vision getVision() { return vision; }

	public Timing getTiming() { return timing; }

	@Override
	public String toString() { return name + ": " + id; }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof WorldObject)) return false;

		WorldObject that = (WorldObject) o;

		return id == that.id && name.equals(that.name);

	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + id;
		return result;
	}
}
