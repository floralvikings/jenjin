package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Point;
import com.jenjinstudios.world.object.WorldObject;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * Represents a cell in the game world.
 *
 * @author Caleb Brinkman
 */
public class Cell extends Node
{
	/** The size in units of each cell on a side. */
	public static final int CELL_SIZE = 10;
	private final Collection<WorldObject> children;
	private final Point point;
	private final Zone parent;
	private short primaryProperty;
	private short secondaryProperty;
	private short tertiaryProperty;
	private short quaternaryProperty;

	/**
	 * Construct a new cell with the given X, Y, and Z coordinates.
	 *
	 * @param point The grid point at which this Cell is placed.
	 * @param parent The parent node of this cell.
	 */
	public Cell(Point point, Zone parent) {
		this.point = point;
		this.parent = parent;
		children = new HashSet<>(10);
	}

	/**
	 * Get the point containing the coordinates of this cell.
	 *
	 * @return The point containing the coordinates of this cell.
	 */
	public Point getPoint() { return point; }

	/**
	 * Add the specified child WorldObject to this Cell.
	 *
	 * @param child The WorldObject to add to this Cell's children.
	 */
	public void addChild(WorldObject child) {
		children.add(child);
		child.setParent(this);
	}

	/**
	 * Remove the specified child WorldObject from this Cell.
	 *
	 * @param child The WorldObject to remove from this Cell's children.
	 */
	public void removeChild(WorldObject child) {
		children.remove(child);
		child.setParent(null);
	}

	/**
	 * Get the primary property of this cell.
	 *
	 * @return The short representing the primary property of this cell.
	 */
	public short getPrimaryProperty() { return primaryProperty; }

	/**
	 * Set the primary property of this cell.
	 *
	 * @param primaryProperty The short representing the new primary property of this cell.
	 */
	public void setPrimaryProperty(short primaryProperty) { this.primaryProperty = primaryProperty; }

	/**
	 * Get the secondary property of this cell.
	 *
	 * @return The short representing the secondary property of this cell.
	 */
	public short getSecondaryProperty() { return secondaryProperty; }

	/**
	 * Set the secondary property of this cell.
	 *
	 * @param secondaryProperty The short representing the new secondary property of this cell.
	 */
	public void setSecondaryProperty(short secondaryProperty) { this.secondaryProperty = secondaryProperty; }

	/**
	 * Get the tertiary property of this cell.
	 *
	 * @return The short representing the tertiary property of this cell.
	 */
	public short getTertiaryProperty() { return tertiaryProperty; }

	/**
	 * Set the tertiary property of this cell.
	 *
	 * @param tertiaryProperty The short representing the new tertiary property of this cell.
	 */
	public void setTertiaryProperty(short tertiaryProperty) { this.tertiaryProperty = tertiaryProperty; }

	/**
	 * Get the quaternary property of this cell.
	 *
	 * @return The short representing the quaternary property of this cell.
	 */
	public short getQuaternaryProperty() { return quaternaryProperty; }

	/**
	 * Set the quaternary property of this cell.
	 *
	 * @param quaternaryProperty The short representing the new primary property of this cell.
	 */
	public void setQuaternaryProperty(short quaternaryProperty) { this.quaternaryProperty = quaternaryProperty; }

	@Override
	public Zone getParent() { return parent; }

	@Override
	public Collection<WorldObject> getChildren() { return Collections.unmodifiableCollection(children); }

	@Override
	public String toString() {
		return "Cell{" +
			  "id=" + getId() +
			  '}';
	}
}
