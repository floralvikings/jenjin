package test.jenjinstudios.jgsf.world;

import com.jenjinstudios.jgcf.world.math.Vector2D;
import com.jenjinstudios.jgsf.world.GameObject;
import com.jenjinstudios.jgsf.world.World;
import com.jenjinstudios.jgsf.world.Zone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test GameObject class.
 *
 * @author Caleb Brinkman
 */
public class GameObjectTest
{
	/** The GameObject used for testing. */
	private GameObject gameObject;
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
		gameObject = new GameObject();
		direction = 2.15f;
		xCoordinate = 5.20f;
		zCoordinate = 7.23f;
		vector2D = new Vector2D(xCoordinate, zCoordinate);
		id = 123;
		world = new World();
		/* The zone used for testing. */
		gameObject.setVector2D(vector2D);
		world.addObject(gameObject);
	}

	/**
	 * Test the direction methods.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testSetDirection() throws Exception
	{
		gameObject.setDirection(direction);
		Assert.assertEquals(direction, gameObject.getDirection(), 0);
	}

	/**
	 * Test the coordinate methods.
	 *
	 * @throws Exception If there is an exception.
	 */
	@Test
	public void testSetCoordinates() throws Exception
	{
		gameObject.setVector2D(vector2D);
		Assert.assertTrue(vector2D.equals(gameObject.getVector2D()));

		gameObject.setVector2D(xCoordinate, zCoordinate);
		Assert.assertTrue(vector2D.equals(gameObject.getVector2D()));
	}

	/**
	 * Test the zone methods.
	 *
	 * @throws Exception If there is an exception.
	 */
	@Test
	public void testSetZone() throws Exception
	{
		Zone newZone = new Zone(5, 7);
		gameObject.setZone(newZone);
		Assert.assertTrue(newZone == gameObject.getZone());
	}

	/**
	 * Test the getLocation method.
	 *
	 * @throws Exception If there is an exception.
	 */
	@Test
	public void testGetLocation() throws Exception
	{
		gameObject.setVector2D(vector2D);
		Assert.assertTrue(gameObject.getLocation() == world.getLocationForCoordinates(vector2D));
	}

	/**
	 * Test the ID methods.
	 *
	 * @throws Exception If there is an exception.
	 */
	@Test
	public void testSetId() throws Exception
	{
		GameObject gameObject1 = new GameObject();
		gameObject1.setId(id);
		Assert.assertEquals(id, gameObject1.getId());
	}

	/**
	 * Test the world methods.
	 *
	 * @throws Exception If there is an exception.
	 */
	@Test
	public void testSetWorld() throws Exception
	{
		Assert.assertTrue(world == gameObject.getWorld());
	}
}
