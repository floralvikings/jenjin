package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Point;
import com.jenjinstudios.world.object.WorldObject;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the Cell class.
 *
 * @author Caleb Brinkman
 */
public class CellTest
{
	/**
	 * Test the addChild method.
	 */
	@Test
	public void testAddChild() {
		Cell cell = new Cell(Point.getPoint((short) 0, (short) 0, (short) 0), null);

		WorldObject worldObject = Mockito.mock(WorldObject.class);
		cell.addChild(worldObject);

		Assert.assertTrue(cell.getChildren().contains(worldObject), "Cell should contain world object");
	}

	/**
	 * Test the removeChild method.
	 */
	@Test
	public void testRemoveChild() {
		Cell cell = new Cell(Point.getPoint((short) 0, (short) 0, (short) 0), null);

		WorldObject worldObject = Mockito.mock(WorldObject.class);
		cell.addChild(worldObject);
		cell.removeChild(worldObject);

		Assert.assertFalse(cell.getChildren().contains(worldObject), "Cell should contain world object");
	}
}
