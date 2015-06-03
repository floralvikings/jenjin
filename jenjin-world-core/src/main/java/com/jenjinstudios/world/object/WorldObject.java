package com.jenjinstudios.world.object;

import com.jenjinstudios.world.Cell;
import com.jenjinstudios.world.Node;
import com.jenjinstudios.world.math.Geometry;
import com.jenjinstudios.world.math.Geometry2D;
import com.jenjinstudios.world.task.TimingTask;

import java.util.Collection;

/**
 * Represents an object within a Cell in the game world.
 *
 * @author Caleb Brinkman
 */
public class WorldObject extends Node
{
	private final String name;
	private final Identification identification;
	private final Vision vision;
	private final Timing timing;
	private Geometry geometry;
	private Cell parent;

	/**
	 * Construct a new WorldObject with the given name.
	 *
	 * @param name The name of the object.
	 */
	public WorldObject(String name) {
		this.name = name;
		identification = new Identification();
		geometry = new Geometry2D();
		vision = new Vision();
		timing = new Timing();
		addTask(new TimingTask());
		addObserver(vision.getNewlyVisibleObserver());
		addObserver(vision.getNewlyInvisibleObserver());
	}

	/**
	 * Get the name of this WorldObject.
	 *
	 * @return The name of this WorldObject.
	 */
	public String getName() { return name; }

	@Override
	public Cell getParent() { return parent; }

	/**
	 * Set the parent of this WorldObject.
	 *
	 * @param parent The Cell that contains this world object.
	 */
	public void setParent(Cell parent) { this.parent = parent; }

	@Override
	public Collection<? extends Node> getChildren() { return null; }

	@Override
	public Node removeChildRecursively(Node child) { return null; }

	/**
	 * Get the identification values of this object.
	 *
	 * @return The identification values of this object.
	 */
	public Identification getIdentification() { return identification; }

	/**
	 * Get the Vision of this object.
	 *
	 * @return The Vision of this object.
	 */
	public Vision getVision() { return vision; }

	/**
	 * Get the timing of this object.
	 *
	 * @return The timing of this object.
	 */
	public Timing getTiming() { return timing; }

	/**
	 * Get the geometry of this object.
	 *
	 * @return The geometry of this object.
	 */
	public Geometry getGeometry() { return geometry; }

	/**
	 * Set the geometry of this object.
	 *
	 * @param geometry The new geometry.
	 */
	public void setGeometry(Geometry geometry) { this.geometry = geometry; }
}
