package com.jenjinstudios.world.task;

import com.jenjinstudios.world.Node;
import com.jenjinstudios.world.reflection.DynamicInvocationException;
import com.jenjinstudios.world.reflection.DynamicMethodSelector;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a task to be executed during the pre-update, in-update, or post-update world loop cycle of a WorldObject.
 *
 * @author Caleb Brinkman
 */
public abstract class NodeTask
{
	private static final Logger LOGGER = Logger.getLogger(NodeTask.class.getName());

	/**
	 * Execute the onPreUpdate method most specific to the supplied node.
	 *
	 * @param node The node against which to execute this task.
	 */
	public final void executePreUpdate(Node node) {
		DynamicMethodSelector methodSelector = new DynamicMethodSelector(this);
		try {
			methodSelector.invokeMostSpecificMethod("onPreUpdate", node);
		} catch (DynamicInvocationException e) {
			LOGGER.log(Level.WARNING, "Exception when executing pre-update task", e);
		}
	}

	/**
	 * Execute the onUpdate method most specific to the supplied node.
	 *
	 * @param node The node against which to execute this task.
	 */
	public final void executeUpdate(Node node) {
		DynamicMethodSelector methodSelector = new DynamicMethodSelector(this);
		try {
			methodSelector.invokeMostSpecificMethod("onUpdate", node);
		} catch (DynamicInvocationException e) {
			LOGGER.log(Level.WARNING, "Exception when executing update task", e);
		}
	}

	/**
	 * Execute the onPostUpdate method most specific to the supplied node.
	 *
	 * @param node The node against which to execute this task.
	 */
	public final void executePostUpdate(Node node) {
		DynamicMethodSelector methodSelector = new DynamicMethodSelector(this);
		try {
			methodSelector.invokeMostSpecificMethod("onPostUpdate", node);
		} catch (DynamicInvocationException e) {
			LOGGER.log(Level.WARNING, "Exception when executing post-update task", e);
		}
	}
}
