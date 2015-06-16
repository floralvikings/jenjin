package com.jenjinstudios.world;

import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * Test the world class.
 *
 * @author Caleb Brinkman
 */
public class WorldTest
{
	/**
	 * Test the one-time task functionality.
	 */
	@Test
	public void testScheduleOneTimeTask() {
		World world = new World();
		Runnable runnable = mock(Runnable.class);

		world.scheduleOneTimeTask(runnable);

		world.postUpdate();
		world.postUpdate();

		verify(runnable, times(1)).run();
	}

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
}
