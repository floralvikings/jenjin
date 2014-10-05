package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.util.WorldUtils;
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
		WorldObject obj0 = new WorldObject("Alice");
		WorldObject obj1 = new WorldObject("Bob");
		WorldObject obj2 = new WorldObject("Carol");
		World world = WorldUtils.createDefaultWorld();

		world.getWorldObjects().scheduleForAddition(obj0);
		world.getWorldObjects().scheduleForAddition(obj1);
		world.getWorldObjects().scheduleForAddition(obj2);
		world.update();

		Assert.assertEquals(obj2.getId(), 2);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testAddNullObject() {
		World world = WorldUtils.createDefaultWorld();
		world.getWorldObjects().scheduleForAddition(null);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testAddOccupiedID() {
		World world = WorldUtils.createDefaultWorld();
		world.getWorldObjects().scheduleForAddition(mock(WorldObject.class), 0);
		world.getWorldObjects().scheduleForAddition(mock(WorldObject.class), 0);
	}

	@Test
	public void testScheduleForRemoval() {
		WorldObject worldObject = new WorldObject("Bob");
		World world = WorldUtils.createDefaultWorld();
		world.getWorldObjects().scheduleForAddition(worldObject);
		world.update();
		world.getWorldObjects().scheduleForRemoval(worldObject.getId());
		world.update();
		Assert.assertEquals(world.getWorldObjects().getObjectCount(), 0);
	}

	@Test
	public void testUpdate() {
		WorldObject worldObject = mock(WorldObject.class);
		when(worldObject.getVector2D()).thenReturn(Vector2D.ORIGIN);
		World world = WorldUtils.createDefaultWorld();
		world.getWorldObjects().scheduleForAddition(worldObject);
		world.update();
		verify(worldObject, times(1)).preUpdate();
		verify(worldObject, times(1)).update();
		verify(worldObject, times(1)).postUpdate();
	}

	@Test
	public void testGetObject() {
		WorldObject obj0 = new WorldObject("Foo");
		WorldObject obj1 = new WorldObject("Bar");
		World world = WorldUtils.createDefaultWorld();
		world.getWorldObjects().scheduleForAddition(obj0, 0);
		world.getWorldObjects().scheduleForAddition(obj1, 1);
		world.update();
		WorldObject retrieved = world.getWorldObjects().getObject(0);
		Assert.assertEquals(retrieved, obj0);
	}
}
