package com.jenjinstudios.world;

import com.jenjinstudios.world.event.NodeObserver;
import com.jenjinstudios.world.task.NodeTask;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.UUID;

/**
 * Represents a node in the World tree.  The World node should be the root, with Zone children, which in turn have Cell
 * children, which have WorldObject children.
 *
 * @author Caleb Brinkman
 */
public abstract class Node
{
	private final Collection<NodeTask> tasks;
	private final Collection<NodeObserver> observers;
	private final String id;

	/**
	 * Construct a new Node with a random id.
	 */
	protected Node() {
		this(UUID.randomUUID().toString());
	}

	/**
	 * Construct a new Node with the given id.
	 *
	 * @param id The ID of the node.
	 */
	protected Node(String id) {
		if (id == null) {
			throw new IllegalArgumentException("ID of Node may not be null.");
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
	 * Add a task to be executed each update cycle on this world object.
	 *
	 * @param task The task to be executed.
	 */
	public void addTask(NodeTask task) { tasks.add(task); }

	/**
	 * Get the tasks to be executed on this Node each update cycle.
	 *
	 * @return The tasks to be executed.
	 */
	public Iterable<NodeTask> getTasks() { return Collections.unmodifiableCollection(tasks); }

	/**
	 * Add an observer to this world object.
	 *
	 * @param observer The observer to add.
	 */
	public void addObserver(NodeObserver observer) { observers.add(observer); }

	/**
	 * Remove an observer from this Node.
	 *
	 * @param observer The observer to remove.
	 */
	public void removeObserver(NodeObserver observer) { observers.remove(observer); }

	/**
	 * Get the observers obersving this world object.
	 *
	 * @return The observers.
	 */
	public Iterable<NodeObserver> getObservers() { return Collections.unmodifiableCollection(observers); }

	/** Used to "set up" a node at the beginning of the update cycle. */
	public void preUpdate() {
		getTasks().forEach(t -> t.executePreUpdate(this));
		getObservers().forEach(t -> t.onPreUpdate(this));
		getChildren().forEach(Node::preUpdate);
	}

	/** Used to update a node. */
	public void update() {
		getTasks().forEach(t -> t.executeUpdate(this));
		getObservers().forEach(t -> t.onUpdate(this));
		getChildren().forEach(Node::update);
	}

	/** Used to "clean up" an node at the end of the update cycle. */
	public void postUpdate() {
		getTasks().forEach(t -> t.executePostUpdate(this));
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
