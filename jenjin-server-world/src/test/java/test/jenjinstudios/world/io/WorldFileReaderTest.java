package test.jenjinstudios.world.io;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.LocationProperties;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.io.WorldFileReader;
import com.jenjinstudios.world.math.Vector2D;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

/**
 * Used to test the WorldFileReader class.
 * @author Caleb Brinkman */
public class WorldFileReaderTest
{
	/**
	 * Test the read function.
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testRead() throws Exception {
		InputStream resourceAsStream = getClass().getResourceAsStream("/test/jenjinstudios/world/WorldFile01.xml");
		WorldFileReader testReader = new WorldFileReader(resourceAsStream);
		World world = testReader.read();
		Location testLocation = world.getLocationForCoordinates(0, new Vector2D(Location.SIZE, Location.SIZE));
		LocationProperties testProperties = testLocation.getLocationProperties();
		Assert.assertTrue("false".equals(testProperties.getProperty("walkable")));
	}
}
