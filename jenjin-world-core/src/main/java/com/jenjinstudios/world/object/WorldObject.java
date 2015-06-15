package com.jenjinstudios.world.object;

import com.jenjinstudios.world.Cell;
import com.jenjinstudios.world.Node;
import com.jenjinstudios.world.math.Geometry;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Represents an object within a Cell in the game world.
 *
 * @author Caleb Brinkman
 */
public class WorldObject extends Node
{
	private final String name;
	private final Timing timing;
	private final Geometry geometry;
	private Cell parent;

	/**
	 * Construct a new WorldObject with the given name.
	 *
	 * @param name The name of the object.
	 */
	public WorldObject(String name) {
		this.name = name;
		geometry = new Geometry();
		timing = new Timing();
		addTask(new TimingTask());
	}

	/**
	 * Get the name of this WorldObject.
	 *
	 * @return The name of this WorldObject.
	 */
	public String getName() { return name; }

	/**
	 * Set the parent of this WorldObject.
	 *
	 * @param parent The Cell that contains this world object.
	 */
	public void setParent(Cell parent) { this.parent = parent; }

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

	@Override
	public Cell getParent() { return parent; }

	@Override
	public Collection<? extends Node> getChildren() { return new LinkedList<>(); }
}
