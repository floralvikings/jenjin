package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Dimensions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collection;

/**
 * Test the Zone class; specifically, test that it initializes properly.
 *
 * @author Caleb Brinkman
 */
public class ZoneTest
{
	/**
	 * Test the getCell method and ensure it properly populates cells.
	 */
	@Test
	public void testGetCell() {
		Zone zone = new Zone(new Dimensions(100, 100, 100));

		zone.getCell((short) 10, (short) 10, (short) 10);
		Assert.assertEquals(zone.populatedCellCount(), 27, "All adjacent cells should have been populated.");

		zone.getCell((short) 10, (short) 11, (short) 10);
		Assert.assertEquals(zone.populatedCellCount(), 27, "No further cells should have been populated.");

		zone.getCell((short) 10, (short) 12, (short) 10);
		Assert.assertEquals(zone.populatedCellCount(), 45, "18 further cells should have been populated.");
	}

	/**
	 * Test the getAdjacentCells method.
	 */
	@Test
	public void testGetAdjacentCells() {
		Zone zone = new Zone(new Dimensions(100, 100, 100));

		Cell center = zone.getCell(49, 49, 49);

		Collection<Cell> adjacents = zone.getAdjacentCells(center);

		Assert.assertEquals(adjacents.size(), 26, "Should be 3 cells cubed, minus center");

		for (Cell cell : adjacents) {
			int xDiff = Math.abs(center.getPoint().getXCoordinate() - cell.getPoint().getXCoordinate());
			int yDiff = Math.abs(center.getPoint().getYCoordinate() - cell.getPoint().getYCoordinate());
			int zDiff = Math.abs(center.getPoint().getZCoordinate() - cell.getPoint().getZCoordinate());

			Assert.assertTrue(xDiff <= 1, "X difference must be less than or equal to one");
			Assert.assertTrue(yDiff <= 1, "Y difference must be less than or equal to one");
			Assert.assertTrue(zDiff <= 1, "Z difference must be less than or equal to one");
		}
	}

	/**
	 * Test the areAdjacent method.
	 */
	@Test
	public void testAreAdjacent() {
		Zone zone = new Zone(new Dimensions(100, 100, 100));

		Cell center = zone.getCell(49, 49, 49);
		Cell adjacent = zone.getCell(48, 48, 48);
		Cell notAdjacent = zone.getCell(45, 45, 45);

		Assert.assertTrue(zone.areAdjacent(center, adjacent), "Cells should be adjacent");
		Assert.assertFalse(zone.areAdjacent(center, notAdjacent), "Cells should not be adjacent");
		Assert.assertFalse(zone.areAdjacent(center, null), "Cells should not be adjacent");
	}
}
