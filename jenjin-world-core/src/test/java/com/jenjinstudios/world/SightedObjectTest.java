package com.jenjinstudios.world;

import com.jenjinstudios.world.actor.Vision;
import com.jenjinstudios.world.math.Dimension2D;
import com.jenjinstudios.world.math.SightCalculator;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.util.WorldUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SightedObjectTest
{
	@Test
	public void testGetVisibleObjects() {
		World world = WorldUtils.createDefaultWorld();
		WorldObject visibleObject = new WorldObject("VisibleObject");
		WorldObject sightedObject = new WorldObject("SightedObject");
		sightedObject.addPreUpdateEvent(Vision.EVENT_NAME, new Vision());
		world.getWorldObjects().scheduleForAddition(visibleObject);
		world.getWorldObjects().scheduleForAddition(sightedObject);

		world.update();
		SightCalculator.updateVisibleObjects(world);
		world.update();
		Vision vision = (Vision) sightedObject.getPreUpdateEvent(Vision.EVENT_NAME);
		Assert.assertTrue(vision.getVisibleObjects().contains(visibleObject));
	}

	@Test
	public void testGetVisibleLocations() {
		Zone zone = new Zone(0, new Dimension2D(50, 50));
		World world = new World(zone);
		WorldObject sightedObject = new WorldObject("SightedObject");
		sightedObject.addPreUpdateEvent(Vision.EVENT_NAME, new Vision());
		world.getWorldObjects().scheduleForAddition(sightedObject);
		sightedObject.setVector2D(new Vector2D(55, 55));

		world.update();
		world.update();

		Assert.assertEquals(SightCalculator.getVisibleLocations(sightedObject).size(), 210);
	}

	@Test
	public void testGetNewlyVisibleObjects() {
		World world = WorldUtils.createDefaultWorld();
		WorldObject visibleObject = new WorldObject("VisibleObject");
		WorldObject sightedObject = new WorldObject("SightedObject");
		sightedObject.addPreUpdateEvent(Vision.EVENT_NAME, new Vision());
		world.getWorldObjects().scheduleForAddition(visibleObject);
		world.getWorldObjects().scheduleForAddition(sightedObject);

		SightCalculator.updateVisibleObjects(world);
		world.update();
		SightCalculator.updateVisibleObjects(world);
		world.update();
		Vision vision = (Vision) sightedObject.getPreUpdateEvent(Vision.EVENT_NAME);
		Assert.assertTrue(vision.getNewlyVisibleObjects().contains(visibleObject));
	}

	@Test
	public void testGetNewlyInvisibleObjects() {
		World world = WorldUtils.createDefaultWorld();
		WorldObject visibleObject = new WorldObject("VisibleObject");
		WorldObject sightedObject = new WorldObject("SightedObject");
		sightedObject.addPreUpdateEvent(Vision.EVENT_NAME, new Vision());
		world.getWorldObjects().scheduleForAddition(visibleObject);
		world.getWorldObjects().scheduleForAddition(sightedObject);

		SightCalculator.updateVisibleObjects(world);
		world.update();
		SightCalculator.updateVisibleObjects(world);
		world.update();

		world.getWorldObjects().scheduleForRemoval(visibleObject);

		world.update();
		SightCalculator.updateVisibleObjects(world);
		world.update();
		Vision vision = (Vision) sightedObject.getPreUpdateEvent(Vision.EVENT_NAME);
		Assert.assertTrue(vision.getNewlyInvisibleObjects().contains(visibleObject));
	}
}
