package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Dimension2D;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author Caleb Brinkman
 */
public class LocationTest
{
	@Test
	public void testGetAdjacentLocations() {
		Zone zone = new Zone(0, new Dimension2D(10, 10));
		Location loc = zone.getLocationOnGrid(5, 5);
		List<Location> adjacentLocations = loc.getAdjacentLocations();
		System.out.println(adjacentLocations);
		Location[] expectedLocationArray = {
			new Location(5, 6),
			new Location(5, 4),
			new Location(6, 5),
			new Location(4, 5),
			new Location(6, 6),
			new Location(4, 6),
			new Location(6, 4),
			new Location(4, 4)
		};
		List<Location> expectedLocations = Arrays.asList(expectedLocationArray);
		Assert.assertEquals(adjacentLocations, expectedLocations);
	}
}
