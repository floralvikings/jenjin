package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Dimension2D;
import com.jenjinstudios.world.util.ZoneUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Caleb Brinkman
 */
public class LocationTest
{
	@Test
	public void testGetAdjacentLocations() {
		Zone zone = new Zone(0, new Dimension2D(10, 10));
		Location loc = ZoneUtils.getLocationOnGrid(zone, 5, 5);
		List<Location> adjacentLocations = ZoneUtils.getAdjacentLocations(zone, loc);
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

	@Test
	public void testGetAdjacentWalkableLocations() {
		Map<String, String> props = new HashMap<>();
		props.put("walkable", "false");
		Location special = new Location(5, 6, props);
		Zone zone = new Zone(0, new Dimension2D(10, 10), special);
		Location loc = ZoneUtils.getLocationOnGrid(zone, 5, 5);
		List<Location> adjacentLocations = ZoneUtils.getAdjacentWalkableLocations(zone, loc);
		Location[] expectedLocationArray = {
			  new Location(5, 4),
			  new Location(6, 5),
			  new Location(4, 5),
			  new Location(6, 4),
			  new Location(4, 4)
		};
		List<Location> expectedLocations = Arrays.asList(expectedLocationArray);
		Assert.assertEquals(adjacentLocations, expectedLocations);
	}
}
