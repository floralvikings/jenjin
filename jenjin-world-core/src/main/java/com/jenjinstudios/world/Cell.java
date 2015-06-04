package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Point;
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
	private final int xCoordinate;
	private final int yCoordinate;
	private final int zCoordinate;
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
		this.parent = parent;
		this.xCoordinate = point.getxCoordinate();
		this.yCoordinate = point.getyCoordinate();
		this.zCoordinate = point.getzCoordinate();
		properties = new HashMap<>(1);
		children = new ArrayList<>(10);
	}

	/**
	 * Get the X coordinate of this cell.
	 *
	 * @return The X coordinate of this cell.
	 */
	public int getXCoordinate() { return xCoordinate; }

	/**
	 * Get the Y coordinate of this cell.
	 *
	 * @return The Y coordinate of this cell.
	 */
	public int getYCoordinate() { return yCoordinate; }

	/**
	 * Get the Z coordinate of this cell.
	 *
	 * @return The Z coordinate of this cell.
	 */
	public int getZCoordinate() { return zCoordinate; }

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

	@Override
	public int hashCode() {
		int result = xCoordinate;
		result = (31 * result) + yCoordinate;
		result = (31 * result) + zCoordinate;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if ((obj == null) || (getClass() != obj.getClass())) return false;

		Cell cell = (Cell) obj;

		if (xCoordinate != cell.getXCoordinate()) return false;
		if (yCoordinate != cell.getYCoordinate()) return false;
		return zCoordinate == cell.getZCoordinate();

	}

	@Override
	public Zone getParent() { return parent; }

	@Override
	public Collection<WorldObject> getChildren() { return Collections.unmodifiableCollection(children); }

	@Override
	public Node removeChildRecursively(Node child) {
		Node r = null;

		if (children.remove(child)) {
			r = child;
		} else {
			Iterator<WorldObject> iterator = children.iterator();
			while (iterator.hasNext() && (r == null)) {
				r = iterator.next().removeChildRecursively(child);
			}
		}

		return r;
	}

}
