package com.jenjinstudios.world;

import com.jenjinstudios.world.object.WorldObject;
import com.jenjinstudios.world.util.WorldUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Caleb Brinkman
 */
public class WorldTest
{
	@Test
	public void testAddObject() {
		WorldObject obj0 = new WorldObject("Alice");
		WorldObject obj1 = new WorldObject("Bob");
		WorldObject obj2 = new WorldObject("Carol");
		World world = WorldUtils.createDefaultWorld();

		world.getWorldObjects().add(obj0);
		world.getWorldObjects().add(obj1);
		world.getWorldObjects().add(obj2);
		world.update();

		Assert.assertEquals(obj2.getId(), 2);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void testAddNullObject() {
		World world = WorldUtils.createDefaultWorld();
		world.getWorldObjects().add(null);
	}

	@Test
	public void testScheduleForRemoval() {
		WorldObject worldObject = new WorldObject("Bob");
		World world = WorldUtils.createDefaultWorld();
		world.getWorldObjects().add(worldObject);
		world.update();
		world.getWorldObjects().remove(worldObject.getId());
		world.update();
		Assert.assertEquals(world.getWorldObjects().size(), 0);
	}

	@Test
	public void testGetObject() {
		WorldObject obj0 = new WorldObject("Foo");
		WorldObject obj1 = new WorldObject("Bar");
		World world = WorldUtils.createDefaultWorld();
		world.getWorldObjects().set(0, obj0);
		world.getWorldObjects().set(1, obj1);
		world.update();
		WorldObject retrieved = world.getWorldObjects().get(0);
		Assert.assertEquals(retrieved, obj0);
	}
}
