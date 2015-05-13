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
	private static final double DEFAULT_SIZE = Location.SIZE / 2.0;
	private final HashMap<String, Object> properties = new HashMap<>(10);
	private final Collection<WorldObjectTask> tasks = new LinkedList<>();
	private final StateChangeTask stateChangeTask = new StateChangeTask();
	private final VisionTask visionTask = new VisionTask();
	private final String name;
	private double visionRadius;
	private long lastUpdateStartTime;
	private long lastUpdateEndTime;
	private int zoneID;
	private int resourceID;
	private int id = Integer.MIN_VALUE;
	private Angle orientation;
	private Vector2D position;
	private Vector2D size;
	private World world;

	public WorldObject(String name) {
		position = Vector2D.ORIGIN;
		size = new Vector2D(DEFAULT_SIZE, DEFAULT_SIZE);
		this.name = name;
		orientation = new Angle();
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

	public Angle getOrientation() { return orientation; }

	public void setOrientation(Angle orientation) { this.orientation = orientation; }

	public Vector2D getPosition() { return position; }

	public void setPosition(Vector2D position) {
		this.position = position;
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

	public Vector2D getSize() { return size; }

	public void setSize(Vector2D size) { this.size = size; }

	public double getVisionRadius() { return visionRadius; }

	public void setVisionRadius(double visionRadius) { this.visionRadius = visionRadius; }

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
