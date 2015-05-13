package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.util.LocationUtils;
import com.jenjinstudios.world.util.WorldUtils;
import com.jenjinstudios.world.util.ZoneUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test WorldObject class.
 * @author Caleb Brinkman
 */
public class WorldObjectTest
{
	/** The WorldObject used for testing. */
	private WorldObject worldObject;
	/** The relativeAngle used for testing. */
	private float direction;
	/** The x coordinate used for testing. */
	private float xCoordinate;
	/** The y coordinate used for testing. */
	private float yCoordinate;
	/** The vector2D used for testing. */
	private Vector2D vector2D;
	/** The id used for testing. */
	private int id;
	/** The world used for testing. */
	private World world;

	/**
	 * Set up before each test.
	 * @throws Exception If there is an exception.
	 */
	@BeforeMethod
	public void setUp() throws Exception {
		worldObject = new WorldObject("Test Object");
		direction = 2.15f;
		xCoordinate = 5.20f;
		yCoordinate = 7.23f;
		vector2D = new Vector2D(xCoordinate, yCoordinate);
		id = 123;
		world = WorldUtils.createDefaultWorld();
		/* The zone used for testing. */
		worldObject.setPosition(vector2D);
		world.getWorldObjects().add(worldObject);
		world.update();
	}

	/**
	 * Test the relativeAngle methods.
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testSetDirection() throws Exception {
		worldObject.setOrientation(new Angle(direction));
		Assert.assertEquals(direction, worldObject.getOrientation().getAbsoluteAngle(), 0);
	}

	/**
	 * Test the coordinate methods.
	 * @throws Exception If there is an exception.
	 */
	@Test
	public void testSetCoordinates() throws Exception {
		worldObject.setPosition(vector2D);
		Assert.assertTrue(vector2D.equals(worldObject.getPosition()));

		worldObject.setPosition(new Vector2D(xCoordinate, yCoordinate));
		Assert.assertTrue(vector2D.equals(worldObject.getPosition()));
	}

	/**
	 * Test the getLocation method.
	 * @throws Exception If there is an exception.
	 */
	@Test
	public void testGetLocation() throws Exception {
		worldObject.setPosition(vector2D);
		Location loc = LocationUtils.getObjectLocation(worldObject);
		Assert.assertTrue(loc == ZoneUtils.getLocationForCoordinates(world, 0, vector2D));
	}

	/**
	 * Test the ID methods.
	 * @throws Exception If there is an exception.
	 */
	@Test
	public void testSetId() throws Exception {
		WorldObject worldObject1 = new WorldObject("Test Object");
		worldObject1.setId(id);
		Assert.assertEquals(id, worldObject1.getId());
	}

	/**
	 * Test the world methods.
	 * @throws Exception If there is an exception.
	 */
	@Test
	public void testSetWorld() throws Exception { Assert.assertTrue(world == worldObject.getWorld()); }
}
