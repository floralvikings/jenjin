package com.jenjinstudios.world.server.ai;

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
	private final Location start;
	private final Location end;

	public Pathfinder(Location start, Location end) {

		this.start = start;
		this.end = end;
	}

	/**
	 * Find a path between the two locations.
	 * @return The Locations necessary to traverse in order to travel from A to B.
	 */
	public LinkedList<Location> findPath() {
		LinkedList<PathNode> openList = new LinkedList<>();
		LinkedList<PathNode> closedList = new LinkedList<>();
		PathNode selectedPathNode = new PathNode(start, end);
		openList.add(selectedPathNode);

		while (selectedPathNode.location != end && !openList.isEmpty() && openList.size() < NODE_LIMIT)
		{
			selectedPathNode = getLowestFNode(openList);
			moveNodeFromOpenToClosed(openList, closedList, selectedPathNode);
			addAdjacentToCorrectList(openList, closedList, selectedPathNode);
		}

		LinkedList<Location> path = new LinkedList<>();
		if (selectedPathNode.location == end)
		{
			path = getReverseNodeTrace(selectedPathNode);
		}

		return path;
	}

	private void moveNodeFromOpenToClosed(LinkedList<PathNode> openList, LinkedList<PathNode> closedList,
										  PathNode selectedPathNode)
	{
		openList.remove(selectedPathNode);
		closedList.add(selectedPathNode);
	}

	private PathNode getLowestFNode(LinkedList<PathNode> openList) {
		PathNode selectedPathNode;
		int lowestF = Integer.MAX_VALUE;
		selectedPathNode = openList.peek();
		for (PathNode pathNode : openList)
		{
			if (pathNode.F < lowestF)
			{
				lowestF = pathNode.F;
				selectedPathNode = pathNode;
			}
		}
		return selectedPathNode;
	}

	private void addAdjacentToCorrectList(LinkedList<PathNode> open, LinkedList<PathNode> closed, PathNode selected) {
		for (Location adjacentLocation : selected.location.getAdjacentWalkableLocations())
		{
			PathNode adjacentPathNode = new PathNode(selected, adjacentLocation, end);
			if (closed.contains(adjacentPathNode))
			{
				continue;
			}
			if (open.contains(adjacentPathNode))
			{
				int indexOfOldNode = open.indexOf(adjacentPathNode);
				PathNode oldPathNode = open.get(indexOfOldNode);
				if (adjacentPathNode.G < oldPathNode.G)
				{
					oldPathNode.parent = selected;
				}
			} else
			{
				open.add(adjacentPathNode);
			}
		}
	}

	private LinkedList<Location> getReverseNodeTrace(PathNode selectedPathNode) {
		Stack<Location> reversePath = getNodeStack(selectedPathNode);
		return reverseNodeStack(reversePath);
	}

	private LinkedList<Location> reverseNodeStack(Stack<Location> reversePath) {
		LinkedList<Location> path;
		path = new LinkedList<>();
		while (!reversePath.isEmpty())
		{
			path.add(reversePath.pop());
		}
		return path;
	}

	private Stack<Location> getNodeStack(PathNode selectedPathNode) {
		Stack<Location> reversePath = new Stack<>();
		while (selectedPathNode.location != start)
		{
			reversePath.push(selectedPathNode.location);
			selectedPathNode = selectedPathNode.parent;
		}
		reversePath.push(selectedPathNode.location);
		return reversePath;
	}

}
