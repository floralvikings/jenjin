package com.jenjinstudios.world.io;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.LocationProperties;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.math.Vector2D;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.InputStream;

/**
 * Used to test the WorldFileReader class.
 * @author Caleb Brinkman
 */
public class WorldDocumentReaderTest
{
	/**
	 * Test the read function.
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testRead() throws Exception {
		InputStream resourceAsStream = getClass().getResourceAsStream("/com/jenjinstudios/world/WorldFile01.xml");
		WorldDocumentReader testReader = new WorldDocumentReader(resourceAsStream);
		World world = testReader.read();
		Location testLocation = world.getLocationForCoordinates(0, new Vector2D(Location.SIZE, Location.SIZE));
		LocationProperties testProperties = testLocation.getLocationProperties();
		Assert.assertTrue("false".equals(testProperties.getProperty("walkable")));
	}
}
