package com.jenjinstudios.world;

/**
 * Used to update all nodes in a tree, starting with a given root node and completing an update cycle in a depth-first
 * manner.
 *
 * @author Caleb Brinkman
 */
public class NodeUpdater
{
	private final Node rootNode;

	/**
	 * Construct a NodeUpdater that will run through the update cycle of the specified node and all of its descendants.
	 *
	 * @param rootNode The root node of the node tree to update.
	 */
	public NodeUpdater(Node rootNode) { this.rootNode = rootNode; }

	/**
	 * Run the full pre-update, update, and post-update cycle.
	 */
	public void runUpdateCycle() {
		recursivePreUpdate(rootNode);
		recursiveUpdate(rootNode);
		recursivePostUpdate(rootNode);
	}

	private void recursivePreUpdate(Node node) {
		node.getTasks().forEach(t -> t.executePreUpdate(node));
		node.getObservers().forEach(o -> o.onPreUpdate(node));
		node.getChildren().forEach(this::recursivePreUpdate);
	}

	private void recursiveUpdate(Node node) {
		node.getTasks().forEach(t -> t.executeUpdate(node));
		node.getObservers().forEach(o -> o.onUpdate(node));
		node.getChildren().forEach(this::recursiveUpdate);
	}

	private void recursivePostUpdate(Node node) {
		node.getTasks().forEach(t -> t.executePostUpdate(node));
		node.getObservers().forEach(o -> o.onPostUpdate(node));
		node.getChildren().forEach(this::recursivePostUpdate);
	}
}
