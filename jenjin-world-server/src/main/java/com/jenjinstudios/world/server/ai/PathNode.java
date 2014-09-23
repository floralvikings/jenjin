package com.jenjinstudios.world.server.ai;

import com.jenjinstudios.world.Location;

/** Used to represent a path finding node. */
class PathNode
{
	/** The location represented in this node. */
	public final Location location;
	/** The G-Score of this node. */
	public final int G;
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
		/* The x coordinate of this node. */
		int x = location.getXCoordinate();
		/* The y coordinate of this node. */
		int y = location.getYCoordinate();
		boolean parentNull = this.parent == null;
		boolean diagonal = !parentNull && (parent.location.getYCoordinate() == y || parent.location.getXCoordinate()
			  == x);
		G = parentNull ? 0 : parent.G + (diagonal ? 10 : 14);
		int h = 10 * (Math.abs(x - target.getXCoordinate()) + Math.abs(y - target.getYCoordinate()));
		F = G + h;
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

	@Override
	public boolean equals(Object o) {
		return o != null && o instanceof PathNode && ((PathNode) o).location == location;
	}
}
