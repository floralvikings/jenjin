package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Dimension2D;
import com.jenjinstudios.world.math.SightCalculator;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.object.Actor;
import com.jenjinstudios.world.object.WorldObject;
import com.jenjinstudios.world.util.WorldUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SightedObjectTest
{
	@Test
	public void testGetVisibleObjects() {
		World world = WorldUtils.createDefaultWorld();
		WorldObject visibleObject = new WorldObject("VisibleObject");
		Actor sightedObject = new Actor("SightedObject");
		world.getWorldObjects().add(visibleObject);
		world.getWorldObjects().add(sightedObject);

		world.update();
		world.update();
		Assert.assertTrue(sightedObject.getVision().getVisibleObjects()
			  .contains(visibleObject));
	}

	@Test
	public void testGetVisibleLocations() {
		Zone zone = new Zone(0, new Dimension2D(50, 50));
		World world = new World(zone);
		WorldObject sightedObject = new WorldObject("SightedObject");
		world.getWorldObjects().add(sightedObject);
		sightedObject.getGeometry2D().setPosition(new Vector2D(55, 55));

		world.update();
		world.update();

		Assert.assertEquals(SightCalculator.
			  getVisibleLocations(world, sightedObject).size(), 210);
	}

	@Test
	public void testGetNewlyVisibleObjects() {
		World world = WorldUtils.createDefaultWorld();
		WorldObject visibleObject = new WorldObject("VisibleObject");
		WorldObject sightedObject = new Actor("SightedObject");
		world.getWorldObjects().add(visibleObject);
		world.getWorldObjects().add(sightedObject);

		world.update();
		Assert.assertTrue(sightedObject.getVision().getNewlyVisibleObjects()
			  .contains(visibleObject));
	}

	@Test
	public void testGetNewlyInvisibleObjects() {
		World world = WorldUtils.createDefaultWorld();
		WorldObject visibleObject = new WorldObject("VisibleObject");
		WorldObject sightedObject = new Actor("SightedObject");
		world.getWorldObjects().add(visibleObject);
		world.getWorldObjects().add(sightedObject);

		world.update();
		world.getWorldObjects().remove(visibleObject.getIdentification().getId
			  ());
		world.update();

		Assert.assertTrue(sightedObject.getVision().getNewlyInvisibleObjects()
			  .contains(visibleObject));
	}
}
