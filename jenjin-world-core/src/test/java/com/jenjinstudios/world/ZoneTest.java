package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Dimensions;
import org.testng.Assert;
import org.testng.annotations.Test;

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
}
