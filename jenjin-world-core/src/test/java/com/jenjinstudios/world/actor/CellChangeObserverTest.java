package com.jenjinstudios.world.actor;

import com.jenjinstudios.world.Cell;
import com.jenjinstudios.world.math.Point;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the CellChangeObserver class.
 *
 * @author Caleb Brinkman
 */
public class CellChangeObserverTest
{
	/**
	 * Test the ObservePostUpdate class.
	 */
	@Test
	public void testObservePostUpdate() {
		Actor actor = mock(Actor.class);
		Point p1 = mock(Point.class);
		Point p2 = mock(Point.class);
		when(p2.getXCoordinate()).thenReturn((short) 1);
		Cell cell1 = mock(Cell.class);
		when(cell1.getPoint()).thenReturn(p1);
		Cell cell2 = mock(Cell.class);
		when(cell2.getPoint()).thenReturn(p2);
		when(actor.getParent()).thenReturn(cell1).thenReturn(cell1).thenReturn(cell2);

		CellChangeObserver cellChangeObserver = new CellChangeObserver();

		CellChangeEvent event1 = cellChangeObserver.observePostUpdate(actor);
		Assert.assertNotNull(event1, "First event should not be null (first entry into cell).");

		CellChangeEvent event2 = cellChangeObserver.observePostUpdate(actor);
		Assert.assertNull(event2, "Second event should be null (cell did not change).");

		CellChangeEvent event3 = cellChangeObserver.observePostUpdate(actor);
		Assert.assertNotNull(event3, "Third event should not be null (cell changed).");

		Assert.assertEquals(event3.getActor(), actor, "Actors should be equal");
		Assert.assertEquals(event3.getPrevious(), cell1, "Previous Cell should be cell1");
		Assert.assertEquals(event3.getCurrent(), cell2, "Current Cell should be cell2");
	}
}
