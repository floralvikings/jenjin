package test.jenjinstudios.world;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.World;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

/** @author Caleb Brinkman */
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
		ArrayList<Location> testGrid = testWorld.getLocationArea(testWorld.getLocationForCoordinates(0, 0), 3);
		Assert.assertEquals(9, testGrid.size());

		testGrid = testWorld.getLocationArea(testWorld.getLocationForCoordinates(50, 50), 3);
		Assert.assertEquals(36, testGrid.size());

		testGrid = testWorld.getLocationArea(testWorld.getLocationForCoordinates(50, 50), 4);
		Assert.assertEquals(64, testGrid.size());
	}
}
