package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.state.MoveState;
import com.jenjinstudios.world.task.StateChangeTask;
import com.jenjinstudios.world.task.TimingTask;
import com.jenjinstudios.world.task.VisionTask;
import com.jenjinstudios.world.task.WorldObjectTask;

import java.util.*;

/**
 * Represents an object that exists in the game world.
 * @author Caleb Brinkman
 */
public class WorldObject
{
	private final HashMap<String, Object> properties = new HashMap<>(10);
	private final Collection<WorldObjectTask> tasks = new LinkedList<>();
	private final StateChangeTask stateChangeTask = new StateChangeTask();
	private final VisionTask visionTask = new VisionTask();
	private final String name;
	private long lastUpdateStartTime;
	private long lastUpdateEndTime;
	private int zoneID;
	private int resourceID;
	private int id = Integer.MIN_VALUE;
	private Angle angle;
	private Vector2D vector2D;
	private World world;

	public WorldObject(String name) {
		vector2D = Vector2D.ORIGIN;
		this.name = name;
		angle = new Angle();
		addTask(new TimingTask());
		addTask(stateChangeTask);
		addTask(visionTask);
	}

	public Set<WorldObject> getVisibleObjects() { return visionTask.getVisibleObjects(); }

	public Set<WorldObject> getNewlyVisibleObjects() { return visionTask.getNewlyVisibleObjects(); }

	public Set<WorldObject> getNewlyInvisibleObjects() { return visionTask.getNewlyInvisibleObjects(); }

	public List<MoveState> getStateChanges() { return stateChangeTask.getStateChanges(); }

	public void addTask(WorldObjectTask task) { tasks.add(task); }

	public Collection<WorldObjectTask> getTasks() { return Collections.unmodifiableCollection(tasks); }

	public Angle getAngle() { return angle; }

	public void setAngle(Angle angle) { this.angle = angle; }

	public Vector2D getVector2D() { return vector2D; }

	public void setVector2D(Vector2D vector2D) {
		this.vector2D = vector2D;
	}

	public int getResourceID() { return resourceID; }

	public void setResourceID(int resourceID) { this.resourceID = resourceID; }

	public int getId() { return id; }

	public void setId(int id) { this.id = id; }

	public HashMap<String, Object> getProperties() { return properties; }

	public World getWorld() { return world; }

	public void setWorld(World world) {
		if (this.world != null)
			throw new IllegalArgumentException("The world has already been set for this object.");
		this.world = world;
	}

	public int getZoneID() { return zoneID; }

	public void setZoneID(int zoneID) { this.zoneID = zoneID; }

	public String getName() { return name; }

	public void setLastUpdateStartTime(long lastUpdateStartTime) { this.lastUpdateStartTime = lastUpdateStartTime; }

	public long getLastUpdateStartTime() { return lastUpdateStartTime; }

	public void setLastUpdateEndTime(long lastUpdateEndTime) { this.lastUpdateEndTime = lastUpdateEndTime; }

	public long getLastUpdateEndTime() { return lastUpdateEndTime; }

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
