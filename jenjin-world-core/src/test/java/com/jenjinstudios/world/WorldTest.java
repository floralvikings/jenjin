package com.jenjinstudios.world;

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
}
