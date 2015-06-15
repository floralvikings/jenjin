package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Dimensions;
import com.jenjinstudios.world.math.Point;
import com.jenjinstudios.world.math.Vector;

import java.util.*;

/**
 * Represents a 3-dimensional grid of Cell nodes.  These are not meant to be constructed programmatically; they should
 * instead by constructed from a JSON representation.
 *
 * @author Caleb Brinkman
 */
public class Zone extends Node
{
	private static final int EXPECTED_ADJACENT_CELLS = 26;
	private final Map<Point, Cell> children;
	private final Dimensions dimensions;
	private World parent;

	/**
	 * Construct a new Zone with the specified dimensions and parent.
	 *
	 * @param dimensions The size of the zone.
	 */
	public Zone(Dimensions dimensions) { this(UUID.randomUUID().toString(), dimensions); }

	/**
	 * Construct a new Zone with the specified id, dimensions and parent.
	 *
	 * @param id The id of the zone.
	 * @param dimensions The size of the zone.
	 */
	public Zone(String id, Dimensions dimensions) {
		super(id);
		this.dimensions = dimensions;
		int maxSize = dimensions.getDepth() * dimensions.getHeight() * dimensions.getWidth();
		if (maxSize <= 0) {
			maxSize = Integer.MAX_VALUE;
		}
		children = new HashMap<>(maxSize);
	}

	/**
	 * Get the cell at the given point.
	 *
	 * @param p The point.
	 *
	 * @return The cell at {@code p}.
	 */
	public Cell getCell(Point p) { return children.containsKey(p) ? children.get(p) : populateCell(p); }

	/**
	 * Get the cell with the specified coordinates.
	 *
	 * @param x The X coordinate of the cell to retrieve.
	 * @param y The Y coordinate of the cell to retrieve.
	 * @param z The Z coordinate of the cell to retrieve.
	 *
	 * @return The retrieved cell; if the cell with the specified coordinates doesn't exist in this zone, returns
	 * {@code
	 * null}.
	 */
	public Cell getCell(short x, short y, short z) { return getCell(Point.getPoint(x, y, z)); }

	/**
	 * Get the cell containing the specific vector.
	 *
	 * @param vector The vector.
	 *
	 * @return The cell containing the specific vector.
	 */
	public Cell getCell(Vector vector) {
		double gridX = vector.getXValue() / Cell.CELL_SIZE;
		double gridY = vector.getYValue() / Cell.CELL_SIZE;
		double gridZ = vector.getZValue() / Cell.CELL_SIZE;
		gridX = ((gridX < 0) && (gridX >= -1)) ? -1 : gridX;
		gridY = ((gridY < 0) && (gridY >= -1)) ? -1 : gridY;
		gridZ = ((gridZ < 0) && (gridZ >= -1)) ? -1 : gridZ;
		return getCell((short) gridX, (short) gridY, (short) gridZ);
	}

	/**
	 * Get all the cells adjacent to the one specified; note that this will only return cells which have an edge from
	 * this cell into the adjacent one.  Cells which are "adjacent" but which do not have an edge from the specified
	 * cell into them are not included.
	 *
	 * @param cell The cell for which to return adjacent cells.
	 *
	 * @return The list of cells adjacent to the one specified.
	 */
	public Collection<Cell> getAdjacentCells(Cell cell) {
		Collection<Cell> adjacents = new ArrayList<>(EXPECTED_ADJACENT_CELLS);
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					Point p = cell.getPoint();
					short aX = (short) (p.getXCoordinate() + x);
					short aY = (short) (p.getYCoordinate() + y);
					short aZ = (short) (p.getZCoordinate() + z);
					Cell adjacent = getCell(aX, aY, aZ);
					if ((adjacent != null) && !Objects.equals(adjacent, cell)) {
						adjacents.add(adjacent);
					}
				}
			}
		}
		return adjacents;
	}

	/**
	 * Returns true if the two cells specified are adjacent to one another, and neither is null.
	 *
	 * @param cell1 The first cell to test for adjacence.
	 * @param cell2 The second cell to test for adjacence.
	 *
	 * @return Whether the two cells are adjacent.
	 */
	public boolean areAdjacent(Cell cell1, Cell cell2) {
		boolean adjacent;
		if ((cell1 == null) || (cell2 == null)) {
			adjacent = false;
		} else {
			adjacent = this.equals(cell1.getParent()) && this.equals(cell2.getParent());
			adjacent &= cell1.getPoint().isAdjacentTo(cell2.getPoint());
		}
		return adjacent;
	}

	/**
	 * Set the parent of this zone.
	 *
	 * @param parent The parent of this zone.
	 */
	public void setParent(World parent) {
		this.parent = parent;
	}

	/**
	 * Get the dimensions of this Zone.
	 *
	 * @return The dimensions of this Zone.
	 */
	public Dimensions getDimensions() { return dimensions; }

	/**
	 * Get the count of cells that have been populated into this zone.  Note that this is different from the maximum
	 * number of cells; this method only returns cells which have actually been initialized and added to the cell map.
	 * This does include cells which have been initialized to {@code null}
	 *
	 * @return The number of cells that have been populated into this zone.
	 */
	public long populatedCellCount() { return children.size(); }

	private Cell populateCell(Point p) {
		Cell cell = new Cell(p, this);
		children.put(p, cell);
		populateAdjacentCells(cell);
		return cell;
	}

	private void populateAdjacentCells(Cell cell) {
		for (short x = -1; x <= 1; x++) {
			for (short y = -1; y <= 1; y++) {
				for (short z = -1; z <= 1; z++) {
					short xAdj = (short) (cell.getPoint().getXCoordinate() + x);
					short yAdj = (short) (cell.getPoint().getYCoordinate() + y);
					short zAdj = (short) (cell.getPoint().getZCoordinate() + z);
					Point p = Point.getPoint(xAdj, yAdj, zAdj);
					if (!children.containsKey(p) && withinBounds(p)) {
						Cell adjCell = new Cell(p, this);
						children.put(p, adjCell);
					}
				}
			}
		}
	}

	private boolean withinBounds(Point p) {
		boolean bigEnough = (p.getXCoordinate() >= 0) && (p.getYCoordinate() >= 0) && (p.getZCoordinate() >= 0);
		boolean smallEnough = (p.getXCoordinate() < dimensions.getWidth())
			  && (p.getYCoordinate() < dimensions.getHeight())
			  && (p.getZCoordinate() < dimensions.getDepth());
		return bigEnough && smallEnough;
	}

	@Override
	public World getParent() { return parent; }

	@Override
	public Collection<Cell> getChildren() { return children.values(); }
}
