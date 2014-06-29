package com.jenjinstudios.world;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class WorldTest
{
	@Test
	public void testAddObject() {
		WorldObject obj0 = Mockito.mock(WorldObject.class);
		WorldObject obj1 = Mockito.mock(WorldObject.class);
		WorldObject obj2 = new WorldObject("Bob");
		World world = new World();

		world.addObject(obj0);
		world.addObject(obj1);
		world.addObject(obj2);

		Assert.assertEquals(obj2.getId(), 2);
	}
}
