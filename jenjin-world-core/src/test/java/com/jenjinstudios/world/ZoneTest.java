package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Dimension2D;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Properties;

/**
 * @author Caleb Brinkman
 */
public class ZoneTest
{
	@Test
	public void testConstructor() {
		Properties properties = new Properties();
		properties.setProperty("blocksVision", "true");
		Location specialLocation = new Location(10, 10, properties);

		Zone zone = new Zone(0, new Dimension2D(20, 20), specialLocation);

		Location actualLoc = zone.getLocationOnGrid(10, 10);
		Assert.assertEquals(actualLoc, specialLocation);
	}

	@Test
	public void testGetLocationOnGrid() {
		Zone zone = new Zone(0, new Dimension2D(20, 20));
		zone.getLocationOnGrid(10, 10);
		Location expected = new Location(10, 10);
		Location actual = zone.getLocationOnGrid(10, 10);
		Assert.assertEquals(actual, expected);
	}
}
