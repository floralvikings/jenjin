package test.jenjinstudios.world;

import com.jenjinstudios.math.Vector2D;
import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.World;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Tests the World class.
 *
 * @author Caleb Brinkman
 */
public class WorldTest
{
	/**
	 * Test the getLocationArea() method.
	 *
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testGetLocationArea() throws Exception
	{
		World testWorld = new World();
		ArrayList<Location> testGrid = testWorld.getLocationArea(testWorld.getLocationForCoordinates(new Vector2D(0, 0)), 3);
		Assert.assertEquals(9, testGrid.size());

		testGrid = testWorld.getLocationArea(testWorld.getLocationForCoordinates(new Vector2D(50, 50)), 3);
		Assert.assertEquals(36, testGrid.size());

		testGrid = testWorld.getLocationArea(testWorld.getLocationForCoordinates(new Vector2D(50, 50)), 4);
		Assert.assertEquals(64, testGrid.size());
	}
}
