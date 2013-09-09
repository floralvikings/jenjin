package test.jenjinstudios.world;

import com.jenjinstudios.math.Vector2D;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.WorldObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test WorldObject class.
 *
 * @author Caleb Brinkman
 */
public class WorldObjectTest
{
	/** The WorldObject used for testing. */
	private WorldObject worldObject;
	/** The direction used for testing. */
	private float direction;
	/** The x coordinate used for testing. */
	private float xCoordinate;
	/** The z coordinate used for testing. */
	private float zCoordinate;
	/** The vector2D used for testing. */
	private Vector2D vector2D;
	/** The id used for testing. */
	private int id;
	/** The world used for testing. */
	private World world;

	/**
	 * Set up before each test.
	 *
	 * @throws Exception If there is an exception.
	 */
	@Before
	public void setUp() throws Exception
	{
		worldObject = new WorldObject();
		direction = 2.15f;
		xCoordinate = 5.20f;
		zCoordinate = 7.23f;
		vector2D = new Vector2D(xCoordinate, zCoordinate);
		id = 123;
		world = new World();
		/* The zone used for testing. */
		worldObject.setVector2D(vector2D);
		world.addObject(worldObject);
	}

	/**
	 * Test the direction methods.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testSetDirection() throws Exception
	{
		worldObject.setDirection(direction);
		Assert.assertEquals(direction, worldObject.getDirection(), 0);
	}

	/**
	 * Test the coordinate methods.
	 *
	 * @throws Exception If there is an exception.
	 */
	@Test
	public void testSetCoordinates() throws Exception
	{
		worldObject.setVector2D(vector2D);
		Assert.assertTrue(vector2D.equals(worldObject.getVector2D()));

		worldObject.setVector2D(xCoordinate, zCoordinate);
		Assert.assertTrue(vector2D.equals(worldObject.getVector2D()));
	}

	/**
	 * Test the getLocation method.
	 *
	 * @throws Exception If there is an exception.
	 */
	@Test
	public void testGetLocation() throws Exception
	{
		worldObject.setVector2D(vector2D);
		Assert.assertTrue(worldObject.getLocation() == world.getLocationForCoordinates(vector2D));
	}

	/**
	 * Test the ID methods.
	 *
	 * @throws Exception If there is an exception.
	 */
	@Test
	public void testSetId() throws Exception
	{
		WorldObject worldObject1 = new WorldObject();
		worldObject1.setId(id);
		Assert.assertEquals(id, worldObject1.getId());
	}

	/**
	 * Test the world methods.
	 *
	 * @throws Exception If there is an exception.
	 */
	@Test
	public void testSetWorld() throws Exception
	{
		Assert.assertTrue(world == worldObject.getWorld());
	}
}
