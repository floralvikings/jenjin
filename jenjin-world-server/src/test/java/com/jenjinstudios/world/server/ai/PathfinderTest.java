package com.jenjinstudios.world.server.ai;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.Zone;
import com.jenjinstudios.world.math.Dimension2D;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Properties;

/**
 * @author Caleb Brinkman
 */
public class PathfinderTest
{
	@Test
	public void testFindPath() {
		Properties properties = new Properties();
		properties.setProperty("walkable", "false");
		Location[] obstacles = {
			  new Location(0, 8, properties), new Location(1, 8, properties), new Location(2, 8, properties),
			  new Location(3, 8, properties), new Location(4, 8, properties), new Location(5, 8, properties),
			  new Location(6, 8, properties), new Location(7, 8, properties), new Location(8, 8, properties),
			  new Location(8, 7, properties), new Location(8, 6, properties), new Location(8, 5, properties),
			  new Location(8, 4, properties), new Location(8, 3, properties), new Location(9, 3, properties)};

		LinkedList<Location> expectedPath = new LinkedList<>(Arrays.asList(
			  new Location(5, 5), new Location(5, 4), new Location(6, 3), new Location(7, 2), new Location(8, 2),
			  new Location(9, 2), new Location(10, 2), new Location(10, 3), new Location(10, 4), new Location(9, 5),
			  new Location(9, 6), new Location(9, 7), new Location(9, 8), new Location(9, 9), new Location(8, 10),
			  new Location(7, 11), new Location(6, 12), new Location(5, 13), new Location(5, 14),
			  new Location(5, 15)));


		Zone zone = new Zone(0, new Dimension2D(20, 20), obstacles);
		Location start = zone.getLocationOnGrid(5, 5);
		Location end = zone.getLocationOnGrid(5, 15);
		Pathfinder pathfinder = new Pathfinder(zone, start, end);
		LinkedList<Location> actualPath = pathfinder.findPath();
		Assert.assertEquals(actualPath, expectedPath);
	}
}
