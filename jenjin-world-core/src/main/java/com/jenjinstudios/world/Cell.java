package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Point;
import com.jenjinstudios.world.math.Vector;
import com.jenjinstudios.world.object.WorldObject;

import java.util.*;

/**
 * Represents a cell in the game world.
 *
 * @author Caleb Brinkman
 */
public class Cell extends Node
{
	/** The size in units of each cell on a side. */
	public static final int CELL_SIZE = 10;
	/** Half the size in units of each cell on a side. */
	public static final int HALF_CELL_SIZE = CELL_SIZE / 2;
	private final Point point;
	private final Map<String, String> properties;
	private final Collection<WorldObject> children;
	private final Zone parent;

	/**
	 * Construct a new cell with the given X, Y, and Z coordinates.
	 *
	 * @param point The grid point at which this Cell is placed.
	 * @param parent The parent node of this cell.
	 */
	public Cell(Point point, Zone parent) {
		this.point = point;
		this.parent = parent;
		properties = new HashMap<>(1);
		children = new ArrayList<>(10);
	}

	/**
	 * Get the point containing the coordinates of this cell.
	 *
	 * @return The point containing the coordinates of this cell.
	 */
	public Point getPoint() { return point; }

	/**
	 * Get the property of this cell with the given name.
	 *
	 * @param propertyName The name of the property to retrieve.
	 *
	 * @return The property with the given name.
	 */
	public String getProperty(String propertyName) { return properties.get(propertyName); }

	/**
	 * Set the property with the given name to the given value.
	 *
	 * @param propertyName The name of the property to set.
	 * @param property The value of the property to set.
	 *
	 * @return The previous value associated with key, or null if there was no mapping for key.
	 */
	public String setProperty(String propertyName, String property) { return properties.put(propertyName, property); }

	/**
	 * Remove the property with the specified name.
	 *
	 * @param propertyName The name of the property to be removed.
	 *
	 * @return The previous value associated with key, or null if there was no mapping for key.
	 */
	public String removeProperty(String propertyName) { return properties.remove(propertyName); }

	/**
	 * Get a collection of cells adjacent to this one.
	 *
	 * @return A collection of cells adjacent to this one.
	 */
	public Collection<Cell> getAdjacentCells() { return parent.getAdjacentCells(this); }

	/**
	 * Return whether this Cell is adjacent to the given cell.
	 *
	 * @param cell The cell to determine adjacence.
	 *
	 * @return Whether the cell is adjacent to this one.
	 */
	public boolean isAdjacentTo(Cell cell) { return getAdjacentCells().contains(cell); }

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
	 * Returns whether the given vector is contained within this Cell.
	 *
	 * @param vector The vector to evaluate and determine whether contained in this cell.
	 *
	 * @return Whether the given vector is contained within this cell.
	 */
	public boolean containsVector(Vector vector) {
		Vector center = getCenter();

		boolean contains = Math.abs(vector.getXValue() - center.getXValue()) < HALF_CELL_SIZE;
		contains &= Math.abs(vector.getYValue() - center.getYValue()) < HALF_CELL_SIZE;
		contains &= Math.abs(vector.getZValue() - center.getZValue()) < HALF_CELL_SIZE;

		return contains;
	}

	/**
	 * Get the Vector at the center of this Cell.
	 *
	 * @return The Vector at the center of this Cell.
	 */
	public Vector getCenter() {
		double centerX = (point.getXCoordinate() * CELL_SIZE) + HALF_CELL_SIZE;
		double centerY = (point.getYCoordinate() * CELL_SIZE) + HALF_CELL_SIZE;
		double centerZ = (point.getZCoordinate() * CELL_SIZE) + HALF_CELL_SIZE;

		return new Vector(centerX, centerY, centerZ);
	}

	@Override
	public Zone getParent() { return parent; }

	@Override
	public Collection<WorldObject> getChildren() { return Collections.unmodifiableCollection(children); }

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if ((obj == null) || (getClass() != obj.getClass())) return false;
		if (!super.equals(obj)) return false;

		Cell cell = (Cell) obj;

		return point.equals(cell.getPoint());

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = (31 * result) + point.hashCode();
		return result;
	}
}
