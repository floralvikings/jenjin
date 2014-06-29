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

		world.addObject(obj0);
		world.addObject(obj1);
		world.addObject(obj2);

		Assert.assertEquals(obj2.getId(), 2);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testAddNullObject() {
		World world = new World();
		world.addObject(null);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testAddOccupiedID() {
		World world = new World();
		world.addObject(mock(WorldObject.class), 0);
		world.addObject(mock(WorldObject.class), 0);
	}

	@Test
	public void testRemoveObjectById() {
		World world = new World();
		world.addObject(mock(WorldObject.class), 0);
		world.removeObject(0);
		Assert.assertEquals(world.getObjectCount(), 0);
	}

	@Test
	public void testRemoveObject() {
		WorldObject worldObject = new WorldObject("Bob");
		World world = new World();
		world.addObject(worldObject);
		world.removeObject(worldObject);
		Assert.assertEquals(world.getObjectCount(), 0);
	}

	@Test
	public void testUpdate() {
		WorldObject worldObject = mock(WorldObject.class);
		World world = new World();
		world.addObject(worldObject);
		world.update();
		verify(worldObject, times(1)).setUp();
		verify(worldObject, times(1)).update();
		verify(worldObject, times(1)).reset();
	}

	@Test
	public void testGetObject() {
		WorldObject obj0 = mock(WorldObject.class);
		WorldObject obj1 = mock(WorldObject.class);
		World world = new World();
		world.addObject(obj0, 0);
		world.addObject(obj1, 1);
		WorldObject retrieved = world.getObject(0);
		Assert.assertEquals(retrieved, obj0);
	}

	@Test
	public void testUpdateTimes() {
		World world = new World();
		world.update();
		long startTime = world.getLastUpdateStarted();
		long completeTime = world.getLastUpdateCompleted();
		Assert.assertNotEquals(startTime, completeTime);
	}
}
