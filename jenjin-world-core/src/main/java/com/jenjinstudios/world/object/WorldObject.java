package com.jenjinstudios.world.object;

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
	private final Identification identification = new Identification();
	private final Vision vision = new Vision();
	private final Timing timing = new Timing();
	private final String name;
	private Geometry2D geometry2D = new Geometry2D();
	private int zoneID;

	public WorldObject(String name) {
		this.name = name;
		addTask(new TimingTask());
		addTask(stateChangeTask);
		addTask(vision.getVisionTask());
	}

	public List<MoveState> getStateChanges() { return stateChangeTask.getStateChanges(); }

	public void addTask(WorldObjectTask task) { tasks.add(task); }

	public Collection<WorldObjectTask> getTasks() { return Collections.unmodifiableCollection(tasks); }

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
