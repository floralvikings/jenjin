package test.jenjinstudios.world;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.LocationProperties;
import com.jenjinstudios.world.Zone;
import com.jenjinstudios.world.ai.Pathfinder;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Test the Location class.
 * @author Caleb Brinkman
 */
public class LocationTest
{
	/** The logger for this class. */
	public static final Logger LOGGER = Logger.getLogger(LocationTest.class.getName());

	/** Test the coordinate values. */
	@Test
	public void testCoordinates() {
		Location loc = new Location(0, 1);
		Assert.assertEquals(0, loc.X_COORDINATE);
		Assert.assertEquals(1, loc.Y_COORDINATE);
		Assert.assertEquals(10, Location.SIZE);
	}

	/** Test the adjacent properties of locations. */
	@SuppressWarnings("ConstantConditions")
	@Test
	public void testAdjacency() {
		int xSize = 100;
		int ySize = 100;
		Zone testZone = new Zone(0, xSize, ySize, new Location[]{});

		for (int i = 0; i < 100; i++)
		{
			int randomX = (int) (Math.random() * xSize);
			int randomY = (int) (Math.random() * ySize);
			String messageString = "Testing adjacency at random location ({0}, {1}) during test number {2}";
			Object[] messageArgs = new Object[] { randomX, randomY, i };
			LOGGER.log(Level.FINE, messageString, messageArgs);
			Location randomLocation = testZone.getLocationOnGrid(randomX, randomY);
			Location northLocation = randomLocation.getAdjNorth();
			Location southLocation = randomLocation.getAdjSouth();
			Location eastLocation = randomLocation.getAdjEast();
			Location westLocation = randomLocation.getAdjWest();
			Location northEastLocation = randomLocation.getAdjNorthEast();
			Location northWestLocation = randomLocation.getAdjNorthWest();
			Location southEastLocation = randomLocation.getAdjSouthEast();
			Location southWestLocation = randomLocation.getAdjSouthWest();

			if (northLocation == null)
			{
				Assert.assertEquals(ySize - 1, randomY);
			} else
			{
				Assert.assertEquals(randomLocation, northLocation.getAdjSouth());
			}

			if (southLocation == null)
			{
				Assert.assertEquals(0, randomY);
			} else
			{
				Assert.assertEquals(randomLocation, southLocation.getAdjNorth());
			}

			if (eastLocation == null)
			{
				Assert.assertEquals(xSize - 1, randomX);
			} else
			{
				Assert.assertEquals(randomLocation, eastLocation.getAdjWest());
			}

			if (westLocation == null)
			{
				Assert.assertEquals(0, randomX);
			} else
			{
				Assert.assertEquals(randomLocation, westLocation.getAdjEast());
			}

			if(southEastLocation != null)
			{
				Assert.assertEquals(randomLocation, southEastLocation.getAdjNorthWest());
			}

			if(southWestLocation != null)
			{
				Assert.assertEquals(randomLocation, southWestLocation.getAdjNorthEast());
			}

			if(northWestLocation != null)
			{
				Assert.assertEquals(randomLocation, northWestLocation.getAdjSouthEast());
			}

			if(northEastLocation != null)
			{
				Assert.assertEquals(randomLocation, northEastLocation.getAdjSouthWest());
			}
		}
	}

	/**
	 * Test the path finding functionality.
	 */
	@Test
	public void testFindPath()
	{
		int xSize = 100;
		int ySize = 100;
		LocationProperties blocked = new LocationProperties();
		blocked.getProperties().put("walkable", "false");
		Location blockedLocation01 = new Location(5, 2, blocked);
		Location blockedLocation02 = new Location(5, 3, blocked);
		Location blockedLocation03 = new Location(5, 4, blocked);
		Zone testZone = new Zone(0, xSize, ySize, new Location[]{blockedLocation01, blockedLocation02, blockedLocation03});

		Location start = testZone.getLocationOnGrid(3, 3);
		Location end = testZone.getLocationOnGrid(7, 3);
		LinkedList<Location> foundPath = Pathfinder.findPath(start, end);


		LinkedList<Location> correctPath = new LinkedList<>();
		correctPath.add(testZone.getLocationOnGrid(3,3));
		correctPath.add(testZone.getLocationOnGrid(4,4));
		correctPath.add(testZone.getLocationOnGrid(4,5));
		correctPath.add(testZone.getLocationOnGrid(5,5));
		correctPath.add(testZone.getLocationOnGrid(6,5));
		correctPath.add(testZone.getLocationOnGrid(7,4));
		correctPath.add(testZone.getLocationOnGrid(7,3));


		Assert.assertEquals(correctPath, foundPath);
	}
}
