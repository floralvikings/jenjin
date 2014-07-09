package com.jenjinstudios.world.server.ai;

import com.jenjinstudios.world.Location;

/** Used to represent a path finding node. */
class PathNode
{
	/** The x coordinate of this node. */
	public final int x;
	/** The y coordinate of this node. */
	public final int y;
	/** The location represented in this node. */
	public final Location location;
	/** The G-Score of this node. */
	public final int G;
	/** The H-Score of this node. */
	public final int H;
	/** The F-Score of this node. */
	public final int F;
	/** The parent of this node. */
	public PathNode parent;

	/**
	 * Construct a new node with the given parent and representing the given location.
	 * @param parent The parent node.
	 * @param location The location of the node.
	 * @param target The target location.
	 */
	public PathNode(PathNode parent, Location location, Location target) {
		this.parent = parent;
		this.location = location;
		x = location.X_COORDINATE;
		y = location.Y_COORDINATE;
		G = this.parent == null ? 0 : parent.G + (parent.y == y || parent.x == x ? 10 : 14);
		H = 10 * (Math.abs(x - target.X_COORDINATE) + Math.abs(y - target.Y_COORDINATE));
		F = G + H;
	}

	/**
	 * Construct a new, parent less node.
	 * @param location The location represented by this node.
	 * @param target The target location.
	 */
	public PathNode(Location location, Location target) {
		this(null, location, target);
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 89 * hash + location.hashCode();
		return hash;
	}

	public boolean equals(Object o) {
		return o != null && o instanceof PathNode && ((PathNode) o).location == location;
	}
}
