package test.jenjinstudios.world;

import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.World;
import org.testng.Assert;
import org.testng.annotations.Test;


import java.util.ArrayList;

/**
 * Tests the World class.
 * @author Caleb Brinkman
 */
public class WorldTest
{
	/**
	 * Test the getLocationArea() method.
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testGetLocationArea() throws Exception {
		World testWorld = new World();
		ArrayList<Location> testGrid = testWorld.getLocationArea(0, new Vector2D(0, 0), 3);
		Assert.assertEquals(9, testGrid.size());

		testGrid = testWorld.getLocationArea(0, new Vector2D(50, 50), 3);
		Assert.assertEquals(25, testGrid.size());

		testGrid = testWorld.getLocationArea(0, new Vector2D(50, 50), 4);
		Assert.assertEquals(49, testGrid.size());
	}
}
