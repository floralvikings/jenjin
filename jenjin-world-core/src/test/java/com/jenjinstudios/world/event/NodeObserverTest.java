package com.jenjinstudios.world.event;

import com.jenjinstudios.world.Cell;
import com.jenjinstudios.world.Node;
import com.jenjinstudios.world.object.WorldObject;
import com.jenjinstudios.world.reflection.DynamicMethod;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * Test the NodeObserver class.
 *
 * @author Caleb Brinkman
 */
public class NodeObserverTest
{
	/**
	 * Test the registration and unregistration of event handlers.
	 */
	@Test
	public void testEventHandlerRegistration() {
		NodeEventHandler nodeEventHandler = mock(NodeEventHandler.class);

		TestNodeObserver testNodeObserver = new TestNodeObserver();
		Assert.assertTrue(testNodeObserver.getEventHandlers().isEmpty(), "Event handlers should be empty");
		testNodeObserver.registerEventHandler(nodeEventHandler);
		Assert.assertEquals(testNodeObserver.getEventHandlers().size(), 1, "One handler should be registered.");
		testNodeObserver.registerEventHandler(nodeEventHandler);
		Assert.assertEquals(testNodeObserver.getEventHandlers().size(), 1, "Dupe handler shouldn't be registered");
		testNodeObserver.unregisterEventHandler(nodeEventHandler);
		Assert.assertTrue(testNodeObserver.getEventHandlers().isEmpty(), "Event handlers should be empty");
	}

	/**
	 * Test the onPreUpdate method.
	 */
	@Test
	public void testOnPreUpdate() {
		Node node = mock(Node.class);
		Cell cell = mock(Cell.class);

		TestNodeObserver testNodeObserver = new TestNodeObserver();
		testNodeObserver.onPreUpdate(node);
		testNodeObserver.onPreUpdate(cell);

		verify(node, times(0)).getChildren();
		verify(cell, times(1)).getChildren();
	}

	/**
	 * Test the onPreUpdate method.
	 */
	@Test
	public void testOnUpdate() {
		Node node = mock(Node.class);
		Cell cell = mock(Cell.class);

		TestNodeObserver testNodeObserver = new TestNodeObserver();
		testNodeObserver.onUpdate(node);
		testNodeObserver.onUpdate(cell);

		verify(node, times(0)).getObservers();
		verify(cell, times(1)).getObservers();
	}

	/**
	 * Test the onPreUpdate method.
	 */
	@Test
	public void testOnPostUpdate() {
		Node node = mock(Node.class);
		Cell cell = mock(Cell.class);

		TestNodeObserver testNodeObserver = new TestNodeObserver();
		testNodeObserver.onPostUpdate(node);
		testNodeObserver.onPostUpdate(cell);

		verify(node, times(0)).getTasks();
		verify(cell, times(1)).getTasks();
	}

	/**
	 * Stub used for testing the NodeObserver class and reflective method dispatch.
	 */
	public static class TestNodeObserver extends NodeObserver
	{
		/**
		 * Used for verification.
		 *
		 * @param worldObject getChildren is called on this node for verification purposes.
		 *
		 * @return a mocked event.
		 */
		@DynamicMethod
		public NodeEvent observePreUpdate(WorldObject worldObject) {
			worldObject.getChildren();
			return mock(NodeEvent.class);
		}

		/**
		 * Used for verification.
		 *
		 * @param worldObject getObservers is called on this node for verification purposes.
		 *
		 * @return a mocked event.
		 */
		@DynamicMethod
		public NodeEvent observeUpdate(WorldObject worldObject) {
			worldObject.getObservers();
			return mock(NodeEvent.class);
		}

		/**
		 * Used for verification.
		 *
		 * @param worldObject getTasks is called on this node for verification purposes.
		 *
		 * @return a mocked event.
		 */
		@DynamicMethod
		public NodeEvent observePostUpdate(WorldObject worldObject) {
			worldObject.getTasks();
			return mock(NodeEvent.class);
		}

		/**
		 * Used for verification.
		 *
		 * @param cell getChildren is called on this node for verification purposes.
		 *
		 * @return a mocked event.
		 */
		@DynamicMethod
		public NodeEvent observePreUpdate(Cell cell) {
			cell.getChildren();
			return mock(NodeEvent.class);
		}

		/**
		 * Used for verification.
		 *
		 * @param cell getObservers is called on this node for verification purposes.
		 *
		 * @return a mocked event.
		 */
		@DynamicMethod
		public NodeEvent observeUpdate(Cell cell) {
			cell.getObservers();
			return mock(NodeEvent.class);
		}

		/**
		 * Used for verification.
		 *
		 * @param cell getTasks is called on this node for verification purposes.
		 *
		 * @return a mocked event.
		 */
		@DynamicMethod
		public NodeEvent observePostUpdate(Cell cell) {
			cell.getTasks();
			return mock(NodeEvent.class);
		}
	}
}
