package test.jenjinstudios.world;

import com.jenjinstudios.world.Location;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test the Location class.
 *
 * @author Caleb Brinkman
 */
public class LocationTest
{
	/**
	 * Test the Get and Set methods for Location.Property
	 *
	 * @throws Exception If there is an exception.
	 */
	@Test
	public void testProperty() throws Exception
	{
		Location loc = new Location(0, 1);
		Assert.assertTrue(loc.getProperty() == Location.LocationProperty.OPEN);
		loc.setProperty(Location.LocationProperty.CLOSED);
		Assert.assertTrue(loc.getProperty() == Location.LocationProperty.CLOSED);
		loc.setProperty(Location.LocationProperty.OPEN);
		Assert.assertTrue(loc.getProperty() == Location.LocationProperty.OPEN);
	}

	/** Test the coordinate values. */
	@Test
	public void testCoordinates()
	{
		Location loc = new Location(0, 1);
		Assert.assertEquals(0, loc.X_COORDINATE);
		Assert.assertEquals(1, loc.Z_COORDINATE);
		Assert.assertEquals(10, Location.SIZE);
	}
}
