package com.jenjinstudios.world;

import com.jenjinstudios.world.ai.Pathfinder;
import com.jenjinstudios.world.math.Dimension2D;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Test the Location class.
 * @author Caleb Brinkman
 */
public class LocationTest
{

	/** Test the coordinate values. */
	@Test
	public void testCoordinates() {
		Location loc = new Location(0, 1);
		Assert.assertEquals(0, loc.X_COORDINATE);
		Assert.assertEquals(1, loc.Y_COORDINATE);
		Assert.assertEquals(10, Location.SIZE);
	}

	/** Test the adjacent properties of locations. */
	@SuppressWarnings("ConstantConditions")
	@Test
	public void testAdjacency() {
		// TODO rewrite this
	}

	/**
	 * Test the path finding functionality.
	 */
	@Test
	public void testFindPath() {
		int xSize = 100;
		int ySize = 100;
		Properties blocked = new Properties();
		blocked.setProperty("walkable", "false");
		Location blockedLocation01 = new Location(5, 2, blocked);
		Location blockedLocation02 = new Location(5, 3, blocked);
		Location blockedLocation03 = new Location(5, 4, blocked);
		Zone testZone = new Zone(0, new Dimension2D(xSize, ySize), blockedLocation01, blockedLocation02, blockedLocation03);

		Location start = testZone.getLocationOnGrid(3, 3);
		Location end = testZone.getLocationOnGrid(7, 3);
		LinkedList<Location> foundPath = Pathfinder.findPath(start, end);


		LinkedList<Location> correctPath = new LinkedList<>();
		correctPath.add(testZone.getLocationOnGrid(3, 3));
		correctPath.add(testZone.getLocationOnGrid(4, 4));
		correctPath.add(testZone.getLocationOnGrid(4, 5));
		correctPath.add(testZone.getLocationOnGrid(5, 5));
		correctPath.add(testZone.getLocationOnGrid(6, 5));
		correctPath.add(testZone.getLocationOnGrid(7, 4));
		correctPath.add(testZone.getLocationOnGrid(7, 3));


		Assert.assertEquals(correctPath, foundPath);
	}
}
