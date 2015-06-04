package com.jenjinstudios.world;

import com.jenjinstudios.world.event.WorldObjectObserver;
import com.jenjinstudios.world.task.WorldObjectTask;

import java.util.*;

/**
 * Represents a node in the World tree.  The World node should be the root, with Zone children, which in turn have Cell
 * children, which have WorldObject children.
 *
 * @author Caleb Brinkman
 */
public abstract class Node
{
	private final Collection<WorldObjectTask> tasks;
	private final Collection<WorldObjectObserver> observers;
	private final String id;

	/**
	 * Construct a new WorldNode with a random id.
	 */
	protected Node() {
		this(UUID.randomUUID().toString());
	}

	/**
	 * Construct a new WorldNode with the given id.
	 *
	 * @param id The ID of the node.
	 */
	protected Node(String id) {
		if (id == null) {
			throw new IllegalArgumentException("ID of WorldNode may not be null.");
		}
		this.id = id;
		tasks = new LinkedList<>();
		observers = new LinkedList<>();
	}

	/**
	 * Get the parent of this node.
	 *
	 * @return The parent of this node.
	 */
	public abstract Node getParent();

	/**
	 * Get the children of this node.
	 *
	 * @return The children of this node.
	 */
	public abstract Collection<? extends Node> getChildren();

	/**
	 * Get the unique identifier of this Node.
	 *
	 * @return The unique identifier of this Node.
	 */
	public String getId() { return id; }

	/**
	 * Convenience method to determine if this node is a root node; equivalent to {@code getParent() == null}.
	 *
	 * @return Whether this node is a root node (has no parent).
	 */
	public boolean isRoot() { return getParent() == null; }

	/**
	 * Convenience method to traverse up the tree until the root node is found.
	 *
	 * @return The root node of the tree containing this node.
	 */
	public Node getRoot() {
		Node node = this;
		while (!node.isRoot()) {
			node = node.getParent();
		}
		return node;
	}

	/**
	 * Find the child of this node (inclusive) with the given id, if it exists.  This utilizes a depth-first search.
	 *
	 * @param childId The id of the chile node to find.
	 *
	 * @return The child node, if it is found; null otherwise.
	 */
	public Node findChild(String childId) {
		Node node = null;

		if (childId.equals(id)) {
			node = this;
		} else {
			Iterator<? extends Node> iterator = getChildren().iterator();
			while (iterator.hasNext() && (node == null)) {
				node = iterator.next().findChild(childId);
			}
		}

		return node;
	}

	/**
	 * Remove the child with the given id from this node tree, if it is present anywhere in the tree below this node.
	 *
	 * @param childId The id of the child to remove.
	 *
	 * @return The child that was removed; null if no change was made to the tree.
	 */
	public Node removeChildRecursively(String childId) {
		Node node = findChild(childId);
		return removeChildRecursively(node);
	}

	/**
	 * Remove the specified node from the node tree, if it is present anywhere in the tree below this node.
	 *
	 * @param child The child to remove from the tree.
	 *
	 * @return The child removed, if any; null otherwise.
	 */
	public abstract Node removeChildRecursively(Node child);

	/**
	 * Add a task to be executed each update cycle on this world object.
	 *
	 * @param task The task to be executed.
	 */
	public void addTask(WorldObjectTask task) { tasks.add(task); }

	/**
	 * Get the tasks to be executed on this WorldObject each update cycle.
	 *
	 * @return The tasks to be executed.
	 */
	public Iterable<WorldObjectTask> getTasks() { return Collections.unmodifiableCollection(tasks); }

	/**
	 * Add an observer to this world object.
	 *
	 * @param observer The observer to add.
	 */
	public void addObserver(WorldObjectObserver observer) { observers.add(observer); }

	/**
	 * Remove an observer from this WorldObject.
	 *
	 * @param observer The observer to remove.
	 */
	public void removeObserver(WorldObjectObserver observer) { observers.remove(observer); }

	/**
	 * Get the observers obersving this world object.
	 *
	 * @return The observers.
	 */
	public Iterable<WorldObjectObserver> getObservers() { return Collections.unmodifiableCollection(observers); }

	/** Used to "set up" a node at the beginning of the update cycle. */
	public final void preUpdate() {
		getTasks().forEach(t -> t.onPreUpdate(this));
		getObservers().forEach(t -> t.onPreUpdate(this));
		getChildren().forEach(Node::preUpdate);
	}

	/** Used to update a node. */
	public final void update() {
		getTasks().forEach(t -> t.onUpdate(this));
		getObservers().forEach(t -> t.onUpdate(this));
		getChildren().forEach(Node::update);
	}

	/** Used to "clean up" an node at the end of the update cycle. */
	public final void postUpdate() {
		getTasks().forEach(t -> t.onPostUpdate(this));
		getObservers().forEach(t -> t.onPostUpdate(this));
		getChildren().forEach(Node::postUpdate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if ((obj == null) || (getClass() != obj.getClass())) return false;

		Node node = (Node) obj;

		return id.equals(node.getId());

	}

	@Override
	public String toString() {
		return "Node{" +
			  "id='" + id + '\'' +
			  '}';
	}

	@Override
	public int hashCode() { return id.hashCode(); }
}
