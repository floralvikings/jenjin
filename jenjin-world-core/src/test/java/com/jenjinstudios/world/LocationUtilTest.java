package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.util.LocationUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class LocationUtilTest
{
	@Test
	public void testGetNorthEastCorner() {
		Location loc = new Location(5, 5);
		Vector2D actual = LocationUtils.getNorthEastCorner(loc);
		Vector2D expected = new Vector2D(59, 59);
		Assert.assertEquals(actual, expected);
	}

	@Test
	public void testGetNorthWestCorner() {
		Location loc = new Location(5, 5);
		Vector2D actual = LocationUtils.getNorthWestCorner(loc);
		Vector2D expected = new Vector2D(50, 59);
		Assert.assertEquals(actual, expected);
	}

	@Test
	public void testGetSouthEastCorner() {
		Location loc = new Location(5, 5);
		Vector2D actual = LocationUtils.getSouthEastCorner(loc);
		Vector2D expected = new Vector2D(59, 50);
		Assert.assertEquals(actual, expected);
	}

	@Test
	public void testGetSouthWestCorner() {
		Location loc = new Location(5, 5);
		Vector2D actual = LocationUtils.getSouthWestCorner(loc);
		Vector2D expected = new Vector2D(50, 50);
		Assert.assertEquals(actual, expected);
	}

	@Test
	public void testGetCenter() {
		Location loc = new Location(5, 5);
		Vector2D actual = LocationUtils.getCenter(loc);
		Vector2D expected = new Vector2D(55, 55);
		Assert.assertEquals(actual, expected);
	}
}
