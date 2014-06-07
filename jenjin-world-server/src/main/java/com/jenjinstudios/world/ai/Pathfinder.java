package com.jenjinstudios.world.ai;

import com.jenjinstudios.world.Location;

import java.util.LinkedList;
import java.util.Stack;

/**
 * This class contains helper methods used to determine a list of Locations to follow to get from point to point around
 * obstacles.
 * @author Caleb Brinkman
 */
public class Pathfinder
{
	/** The maximum number of nodes to check before giving up and assuming the path cannot be found. */
	private static final int NODE_LIMIT = 1000;

	/**
	 * Find a path between the two locations.
	 * @param start The start location.
	 * @param end The end location.
	 * @return The Locations necessary to traverse in order to travel from A to B.
	 */
	public static LinkedList<Location> findPath(Location start, Location end) {
		LinkedList<Location> path = new LinkedList<>();

		LinkedList<Node> openList = new LinkedList<>();
		LinkedList<Node> closedList = new LinkedList<>();
		Node selectedNode = new Node(start, end);
		openList.add(selectedNode);

		while (selectedNode.location != end && !openList.isEmpty() && openList.size() < NODE_LIMIT)
		{
			int lowestF = Integer.MAX_VALUE;
			selectedNode = openList.peek();
			for (Node node : openList)
			{
				if (node.F < lowestF)
				{
					lowestF = node.F;
					selectedNode = node;
				}
			}
			openList.remove(selectedNode);
			closedList.add(selectedNode);
			for (Location adjacentLocation : selectedNode.location.getAdjacentWalkableLocations())
			{
				Node adjacentNode = new Node(selectedNode, adjacentLocation, end);
				if (closedList.contains(adjacentNode))
				{
					continue;
				}
				if (openList.contains(adjacentNode))
				{
					int indexOfOldNode = openList.indexOf(adjacentNode);
					Node oldNode = openList.get(indexOfOldNode);
					if (adjacentNode.G < oldNode.G)
					{
						oldNode.parent = selectedNode;
					}
				} else
				{
					openList.add(adjacentNode);
				}
			}
		}

		if(selectedNode.location == end)
		{
			Stack<Location> reversePath = new Stack<>();
			while(selectedNode.location != start)
			{
				reversePath.push(selectedNode.location);
				selectedNode = selectedNode.parent;
			}
			reversePath.push(selectedNode.location);
			while(!reversePath.isEmpty())
			{
				path.add(reversePath.pop());
			}
		}

		return path;
	}

	/** Used to represent a path finding node. */
	private static class Node
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
		public Node parent;

		/**
		 * Construct a new node with the given parent and representing the given location.
		 * @param parent The parent node.
		 * @param location The location.
		 * @param target The target location.
		 */
		public Node(Node parent, Location location, Location target) {
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
		public Node(Location location, Location target) {
			this(null, location, target);
		}

		public boolean equals(Object o) {
			return o != null && o instanceof Node && ((Node) o).location == location;
		}
	}
}
