package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Dimension2D;
import com.jenjinstudios.world.math.Vector2D;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Caleb Brinkman
 */
public class ZoneTest
{
	@Test
	public void testConstructor() {
		Map<String, Object> properties = new HashMap<>();
		properties.put("blocksVision", "true");
		Location specialLocation = new Location(10, 10, properties);

		Zone zone = new Zone(0, new Dimension2D(20, 20), specialLocation);

		Location actualLoc = zone.getLocationOnGrid(10, 10);
		Assert.assertEquals(actualLoc, specialLocation);
	}

	@Test
	public void testGetLocationOnGrid() {
		Zone zone = new Zone(0, new Dimension2D(20, 20));
		Location expected = new Location(10, 10);
		Location actual = zone.getLocationOnGrid(10, 10);
		Assert.assertEquals(actual, expected);
	}

	@Test
	public void testGetLocationForCoordinates() {
		Zone zone = new Zone(0, new Dimension2D(20, 20));
		Location expected = new Location(10, 10);
		Location actual = zone.getLocationForCoordinates(new Vector2D(100, 100));
		Assert.assertEquals(actual, expected);
	}
}
