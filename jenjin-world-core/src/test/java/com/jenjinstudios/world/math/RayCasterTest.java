package com.jenjinstudios.world.math;

import com.jenjinstudios.world.Cell;
import com.jenjinstudios.world.Zone;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Tests the RayCaster class.
 *
 * @author Caleb Brinkman
 */
public class RayCasterTest
{
	/**
	 * Test the castRay method using a few known rays.
	 */
	@Test
	public void testCastRay() {
		Zone zone = new Zone(new Dimensions(20, 20, 20));
		RayCaster rayCaster = new RayCaster(zone);
		Vector start = new Vector(95, 95, 95);

		Collection<Cell> expectedNorthRay = new LinkedList<>();
		expectedNorthRay.add(zone.getCell((short) 9, (short) 9, (short) 9));
		expectedNorthRay.add(zone.getCell((short) 9, (short) 9, (short) 10));
		expectedNorthRay.add(zone.getCell((short) 9, (short) 9, (short) 11));
		expectedNorthRay.add(zone.getCell((short) 9, (short) 9, (short) 12));
		expectedNorthRay.add(zone.getCell((short) 9, (short) 9, (short) 13));
		expectedNorthRay.add(zone.getCell((short) 9, (short) 9, (short) 14));

		Queue<Cell> northRay = rayCaster.castRay(start, Orientation.NORTH, 5);
		Assert.assertEquals(northRay, expectedNorthRay, "Rays should be equal");

		Collection<Cell> expectedEastRay = new LinkedList<>();
		expectedEastRay.add(zone.getCell((short) 9, (short) 9, (short) 9));
		expectedEastRay.add(zone.getCell((short) 10, (short) 9, (short) 9));
		expectedEastRay.add(zone.getCell((short) 11, (short) 9, (short) 9));
		expectedEastRay.add(zone.getCell((short) 12, (short) 9, (short) 9));
		expectedEastRay.add(zone.getCell((short) 13, (short) 9, (short) 9));
		expectedEastRay.add(zone.getCell((short) 14, (short) 9, (short) 9));
		Queue<Cell> eastRay = rayCaster.castRay(start, Orientation.EAST, 5);
		Assert.assertEquals(eastRay, expectedEastRay, "Rays should be equal");
	}
}
