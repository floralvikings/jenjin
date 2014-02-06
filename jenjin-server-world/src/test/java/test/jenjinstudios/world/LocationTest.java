package test.jenjinstudios.world;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.Zone;
import org.junit.Assert;
import org.junit.Test;

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
		}
	}
}
