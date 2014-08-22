package com.jenjinstudios.world;

import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * @author Caleb Brinkman
 */
public class WorldTest
{
	@Test
	public void testAddObject() {
		WorldObject obj0 = mock(WorldObject.class);
		WorldObject obj1 = mock(WorldObject.class);
		WorldObject obj2 = new WorldObject("Bob");
		World world = new World();

		world.getWorldObjects().scheduleForAddition(obj0);
		world.getWorldObjects().scheduleForAddition(obj1);
		world.getWorldObjects().scheduleForAddition(obj2);
		world.update();

		Assert.assertEquals(obj2.getId(), 2);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testAddNullObject() {
		World world = new World();
		world.getWorldObjects().scheduleForAddition(null);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testAddOccupiedID() {
		World world = new World();
		world.getWorldObjects().scheduleForAddition(mock(WorldObject.class), 0);
		world.getWorldObjects().scheduleForAddition(mock(WorldObject.class), 0);
	}

	@Test
	public void testScheduleForRemoval() {
		WorldObject worldObject = new WorldObject("Bob");
		World world = new World();
		world.getWorldObjects().scheduleForAddition(worldObject);
		world.update();
		world.getWorldObjects().scheduleForRemoval(worldObject);
		world.update();
		Assert.assertEquals(world.getWorldObjects().getObjectCount(), 0);
	}

	@Test
	public void testUpdate() {
		WorldObject worldObject = mock(WorldObject.class);
		World world = new World();
		world.getWorldObjects().scheduleForAddition(worldObject);
		world.update();
		verify(worldObject, times(1)).setUp();
		verify(worldObject, times(1)).update();
		verify(worldObject, times(1)).reset();
	}

	@Test
	public void testGetObject() {
		WorldObject obj0 = new WorldObject("Foo");
		WorldObject obj1 = new WorldObject("Bar");
		World world = new World();
		world.getWorldObjects().scheduleForAddition(obj0, 0);
		world.getWorldObjects().scheduleForAddition(obj1, 1);
		world.update();
		WorldObject retrieved = world.getWorldObjects().getObject(0);
		Assert.assertEquals(retrieved, obj0);
	}
}
