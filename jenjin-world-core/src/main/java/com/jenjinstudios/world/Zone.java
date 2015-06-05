package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Dimension;
import com.jenjinstudios.world.math.Point;
import com.jenjinstudios.world.math.Vector;
import com.jenjinstudios.world.object.WorldObject;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Represents a 3-dimensional grid of Cell nodes.  These are not meant to be constructed programmatically; they should
 * instead by constructed from a JSON representation.
 *
 * @author Caleb Brinkman
 */
public class Zone extends Node
{
	private static final Logger LOGGER = Logger.getLogger(Zone.class.getName());
	private static final int EXPECTED_ADJACENT_CELLS = 26;
	private final DirectedMultigraph<Cell, CellEdge> children;
	private World parent;

	/**
	 * Construct a new Zone with the specified dimensions and parent.
	 *
	 * @param dimension The size of the zone.
	 */
	public Zone(Dimension dimension) {
		this(UUID.randomUUID().toString(), dimension);
	}

	/**
	 * Construct a new Zone with the specified id, dimensions and parent.
	 *
	 * @param id The id of the zone.
	 * @param dimension The size of the zone.
	 */
	public Zone(String id, Dimension dimension) {
		super(id);
		children = new DirectedMultigraph<>(new CellEdgeFactory());
		populateVertices(dimension.getWidth(), dimension.getDepth(), dimension.getHeight());
		populateEdges(dimension.getWidth(), dimension.getDepth(), dimension.getHeight());
	}

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
	public Cell getCell(int x, int y, int z) {
		Cell match = null;
		Cell test = new Cell(new Point(x, y, z), this);
		Set<Cell> vertices = children.vertexSet();
		for (Cell cell : vertices) {
			if (cell.equals(test)) {
				match = cell;
			}
		}
		return match;
	}

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
		gridX = (gridX < 0) ? -1 : gridX;
		gridY = (gridY < 0) ? -1 : gridY;
		gridZ = (gridZ < 0) ? -1 : gridZ;
		return getCell((int) gridX, (int) gridY, (int) gridZ);
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
		Set<CellEdge> edges = children.outgoingEdgesOf(cell);
		Collection<Cell> adjacents = new ArrayList<>(EXPECTED_ADJACENT_CELLS);
		adjacents.addAll(edges.stream().map(CellEdge::getDestination).collect(Collectors.toList()));
		return adjacents;
	}

	/**
	 * Returns the WorldObjects contained in the child Cells of this Zone.  Note that this only returns "first
	 * generation" world objects; i.e. objects that are direct children of the Cells.
	 *
	 * @return WorldObjects contained in the child Cells of this Zone.
	 */
	public Collection<WorldObject> getWorldObjects() {
		Collection<WorldObject> worldObjects = new LinkedList<>();

		children.vertexSet().forEach(cell -> worldObjects.addAll(cell.getChildren()));

		return worldObjects;
	}

	/**
	 * Set the parent of this zone.
	 *
	 * @param parent The parent of this zone.
	 */
	public void setParent(World parent) {
		this.parent = parent;
	}

	private void populateVertices(int width, int depth, int height) {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < depth; z++) {
					Cell cell = new Cell(new Point(x, y, z), this);
					children.addVertex(cell);
				}
			}
		}
	}

	private void populateEdges(int width, int depth, int height) {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < depth; z++) {
					Cell origin = getCell(x, y, z);
					Collection<Cell> adjacents = calculateAdjacentCells(x, y, z);
					adjacents.forEach(destination -> {
						if (destination != null) {
							children.addEdge(origin, destination);
						}
					});
				}
			}
		}
	}

	private Collection<Cell> calculateAdjacentCells(int x, int y, int z) {
		Collection<Cell> adjacentCells = new LinkedList<>();
		adjacentCells.add(getCell(x - 1, y - 1, z - 1));
		adjacentCells.add(getCell(x - 1, y - 1, z));
		adjacentCells.add(getCell(x - 1, y - 1, z + 1));
		adjacentCells.add(getCell(x - 1, y, z - 1));
		adjacentCells.add(getCell(x - 1, y, z));
		adjacentCells.add(getCell(x - 1, y, z + 1));
		adjacentCells.add(getCell(x - 1, y + 1, z - 1));
		adjacentCells.add(getCell(x - 1, y + 1, z));
		adjacentCells.add(getCell(x - 1, y + 1, z + 1));
		adjacentCells.add(getCell(x, y - 1, z - 1));
		adjacentCells.add(getCell(x, y - 1, z));
		adjacentCells.add(getCell(x, y - 1, z + 1));
		adjacentCells.add(getCell(x, y, z - 1));
		adjacentCells.add(getCell(x, y, z + 1));
		adjacentCells.add(getCell(x, y + 1, z - 1));
		adjacentCells.add(getCell(x, y + 1, z));
		adjacentCells.add(getCell(x, y + 1, z + 1));
		adjacentCells.add(getCell(x + 1, y - 1, z - 1));
		adjacentCells.add(getCell(x + 1, y - 1, z));
		adjacentCells.add(getCell(x + 1, y - 1, z + 1));
		adjacentCells.add(getCell(x + 1, y, z - 1));
		adjacentCells.add(getCell(x + 1, y, z));
		adjacentCells.add(getCell(x + 1, y, z + 1));
		adjacentCells.add(getCell(x + 1, y + 1, z - 1));
		adjacentCells.add(getCell(x + 1, y + 1, z));
		adjacentCells.add(getCell(x + 1, y + 1, z + 1));
		return adjacentCells;
	}

	@Override
	public World getParent() { return parent; }

	@Override
	public Collection<Cell> getChildren() { return children.vertexSet(); }
}
