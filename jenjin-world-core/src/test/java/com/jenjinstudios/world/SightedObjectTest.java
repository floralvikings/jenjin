package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Dimension2D;
import com.jenjinstudios.world.math.Vector2D;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Properties;

public class SightedObjectTest
{
	@Test
	public void testGetVisibleObjects() {
		World world = new World();
		WorldObject visibleObject = new WorldObject("VisibleObject");
		SightedObject sightedObject = new SightedObject("SightedObject");
		world.getWorldObjects().scheduleForAddition(visibleObject);
		world.getWorldObjects().scheduleForAddition(sightedObject);

		world.update();
		world.update();

		Assert.assertTrue(sightedObject.getVisibleObjects().containsKey(visibleObject.getId()));
	}

	@Test
	public void testGetVisibleLocations() {
		Properties props = new Properties();
		props.setProperty("blocksVision", "true");
		Location[] locations = new Location[]{
			  new Location(24, 23, props),
			  new Location(24, 27, props),
			  new Location(23, 26, props),
			  new Location(23, 24, props),
			  new Location(26, 23, props),
			  new Location(26, 27, props),
			  new Location(27, 24, props),
			  new Location(27, 26, props),
			  new Location(27, 27, props)
		};
		Zone zone = new Zone(0, new Dimension2D(50, 50), locations);
		World world = new World(zone);
		SightedObject sightedObject = new SightedObject("SightedObject");
		world.getWorldObjects().scheduleForAddition(sightedObject);
		sightedObject.setVector2D(new Vector2D(55, 55));

		world.update();
		world.update();

		Assert.assertEquals(sightedObject.getVisibleLocations().size(), 238);
	}

	@Test
	public void testGetNewlyVisibleObjects() {
		World world = new World();
		WorldObject visibleObject = new WorldObject("VisibleObject");
		SightedObject sightedObject = new SightedObject("SightedObject");
		world.getWorldObjects().scheduleForAddition(visibleObject);
		world.getWorldObjects().scheduleForAddition(sightedObject);

		world.update();

		Assert.assertTrue(sightedObject.getNewlyVisibleObjects().contains(visibleObject));
	}

	@Test
	public void testGetNewlyInvisibleObjects() {
		World world = new World();
		WorldObject visibleObject = new WorldObject("VisibleObject");
		SightedObject sightedObject = new SightedObject("SightedObject");
		world.getWorldObjects().scheduleForAddition(visibleObject);
		world.getWorldObjects().scheduleForAddition(sightedObject);

		world.update();
		world.update();

		world.getWorldObjects().scheduleForRemoval(visibleObject);

		world.update();

		Assert.assertTrue(sightedObject.getNewlyInvisibleObjects().contains(visibleObject));
	}
}
