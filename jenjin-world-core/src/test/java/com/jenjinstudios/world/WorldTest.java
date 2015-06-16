package com.jenjinstudios.world;

import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the world class.
 *
 * @author Caleb Brinkman
 */
public class WorldTest
{

	/**
	 * Test the addZone method.
	 */
	@Test
	public void testAddZone() {
		World world = new World();

		Zone zone = mock(Zone.class);

		world.addZone(zone);

		Assert.assertTrue(world.getChildren().contains(zone), "World's children should contain the added zone.");
	}

	/**
	 * Test the getZone method.
	 */
	@Test
	public void testGetZone() {
		World world = new World();

		Zone zone = mock(Zone.class);
		when(zone.getId()).thenReturn("foo");

		world.addZone(zone);

		Assert.assertEquals(world.getZone("foo"), zone, "Zones should be equal.");
	}

	/**
	 * Test the getParent method.
	 */
	@Test
	public void testGetParent() {
		World world = new World();
		Assert.assertNull(world.getParent(), "World should have null parent.");
	}
}
