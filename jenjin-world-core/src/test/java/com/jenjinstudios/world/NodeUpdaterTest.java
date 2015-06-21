package com.jenjinstudios.world;

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;

/**
 * Test the NodeUpdater class.
 *
 * @author Caleb Brinkman
 */
public class NodeUpdaterTest
{
	/**
	 * Test the runUpdateCycle method.
	 */
	@Test
	public void testRunUpdateCycle() {
		Node root = mock(Node.class);
		Node genTwoParent = mock(Node.class);
		Node genTwoSibling = mock(Node.class);
		Node genThreeChild = mock(Node.class);
		doReturn(Arrays.asList(genTwoParent, genTwoSibling)).when(root).getChildren();
		doReturn(Collections.singletonList(genThreeChild)).when(genTwoParent).getChildren();

		NodeUpdater nodeUpdater = new NodeUpdater(root);
		nodeUpdater.runUpdateCycle();

		verify(genThreeChild, times(3)).getTasks();
		verify(genThreeChild, times(3)).getObservers();
	}
}
