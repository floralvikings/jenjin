package com.jenjinstudios.world.object;

import com.jenjinstudios.world.Cell;
import com.jenjinstudios.world.Node;
import com.jenjinstudios.world.math.Geometry;
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

	/**
	 * Set the geometry of this object.
	 *
	 * @param geometry The new geometry.
	 */
	public void setGeometry(Geometry geometry) { this.geometry = geometry; }

	@Override
	public Cell getParent() { return parent; }

	@Override
	public Collection<? extends Node> getChildren() { return null; }

	@Override
	public void preUpdate() {
		getTasks().forEach(t -> t.onPreUpdate(this));
		getObservers().forEach(t -> t.onPreUpdate(this));
		getChildren().forEach(Node::preUpdate);
	}

	@Override
	public void update() {
		getTasks().forEach(t -> t.onUpdate(this));
		getObservers().forEach(t -> t.onUpdate(this));
		getChildren().forEach(Node::update);
	}

	@Override
	public void postUpdate() {
		getTasks().forEach(t -> t.onPostUpdate(this));
		getObservers().forEach(t -> t.onPostUpdate(this));
		getChildren().forEach(Node::postUpdate);
	}
}
