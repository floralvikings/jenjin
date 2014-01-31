package test.jenjinstudios.world;

import com.jenjinstudios.world.Location;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test the Location class.
 * @author Caleb Brinkman
 */
public class LocationTest
{
	/** Test the coordinate values. */
	@Test
	public void testCoordinates() {
		Location loc = new Location(0, 1);
		Assert.assertEquals(0, loc.X_COORDINATE);
		Assert.assertEquals(1, loc.Y_COORDINATE);
		Assert.assertEquals(10, Location.SIZE);
	}
}
