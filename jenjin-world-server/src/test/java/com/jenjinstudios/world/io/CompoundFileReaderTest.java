package com.jenjinstudios.world.io;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.LocationProperties;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.math.Vector2D;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.InputStream;

/**
 * Test the CompoundFileReader class.
 * @author Caleb Brinkman
 */
public class CompoundFileReaderTest
{
	/**
	 * Test the read functionality.
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testRead() throws Exception {
		InputStream resourceAsStream = getClass().getResourceAsStream("/com/jenjinstudios/world/CompoundFile01.xml");
		CompoundFileReader testReader = new CompoundFileReader(resourceAsStream);
		World world = testReader.read();

		Location testLocation = world.getLocationForCoordinates(0, new Vector2D(Location.SIZE, Location.SIZE));
		LocationProperties testProperties = testLocation.getLocationProperties();
		Assert.assertTrue("false".equals(testProperties.getProperty("walkable")));
		Assert.assertEquals(1, world.getObjectCount(), "World object count after reading NPCFile01.xml");
	}
}
