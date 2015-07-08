package com.jenjinstudios.world.math;

import com.jenjinstudios.world.Cell;
import com.jenjinstudios.world.Zone;
import com.jenjinstudios.world.actor.Actor;
import com.jenjinstudios.world.actor.Vision;
import com.jenjinstudios.world.object.WorldObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the BasicSightCalculator class.
 *
 * @author Caleb Brinkman
 */
public class BasicSightCalculatorTest
{
	/**
	 * Test the getVisibleCells method.
	 */
	@Test
	public void testGetVisibleCells() {
		// Would be better to use mocks here, but it's a large collection so it's cleaner to just use the real objects
		Zone zone = new Zone(new Dimensions(50, 50, 50));
		Cell cell = zone.getCell(25, 25, 25);

		Actor actor = mock(Actor.class);
		Vision vision = mock(Vision.class);
		when(vision.getRadius()).thenReturn(5.0D);
		when(actor.getParent()).thenReturn(cell);
		when(actor.getVision()).thenReturn(vision);

		SightCalculator sightCalculator = new BasicSightCalculator();
		Collection<Cell> cells = sightCalculator.getVisibleCells(actor);

		Assert.assertEquals(cells.size(), 729, "729 Cells should be visible");
	}

	/**
	 * Test the getVisibleObjects method.
	 */
	@Test
	public void testGetVisibleObjects() {
		// Would be better to use mocks here, but it's a large collection so it's cleaner to just use the real objects
		Zone zone = new Zone(new Dimensions(50, 50, 50));
		Cell cell = zone.getCell(25, 25, 25);

		Actor actor = mock(Actor.class);
		Vision vision = mock(Vision.class);
		Actor visible = mock(Actor.class);
		when(vision.getRadius()).thenReturn(5.0D);
		when(actor.getParent()).thenReturn(cell);
		when(actor.getVision()).thenReturn(vision);

		cell.addChild(visible);
		cell.addChild(actor);

		SightCalculator sightCalculator = new BasicSightCalculator();
		Collection<WorldObject> visibleObjects = sightCalculator.getVisibleObjects(actor);

		Assert.assertEquals(visibleObjects.size(), 2, "One object should be visible, plus actor.");
	}

}
