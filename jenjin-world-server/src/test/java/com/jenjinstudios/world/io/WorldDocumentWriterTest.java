package com.jenjinstudios.world.io;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.LocationProperties;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.Zone;
import com.jenjinstudios.world.math.Vector2D;
import org.testng.annotations.*;
import org.testng.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Test the WorldFileWriter class.
 * @author Caleb Brinkman
 */
public class WorldDocumentWriterTest
{
	/** The Logged for this test. */
	private static final Logger LOGGER = Logger.getLogger(WorldDocumentWriterTest.class.getName());
	/** The file to be written to for testing purposes. */
	File worldFile;

	/**
	 * Set up the tests.
	 * @throws Exception If there's an exception.
	 */
	@BeforeMethod
	public void setUp() throws Exception {
		worldFile = new File("WorldFileWriterTest.xml");
		if (worldFile.exists() && worldFile.delete())
		{
			LOGGER.log(Level.INFO, "Deleted test file.");
		}


	}

	/**
	 * Tear down the test.
	 * @throws Exception If there's an exception.
	 */
	@AfterMethod
	public void tearDown() throws Exception {
		if (worldFile.exists() && worldFile.delete())
		{
			LOGGER.log(Level.INFO, "Deleted test file.");
		}
	}

	/**
	 * Test the write functionality.
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testWrite() throws Exception {
		TreeMap<String, String> map = new TreeMap<>();
		map.put("walkable", "false");
		Location testLocation = new Location(1, 1, new LocationProperties(map));
		Location[] locArray = {testLocation};
		World writeWorld = new World(new Zone[]{new Zone(0, 5, 5, locArray)});

		OutputStream out = new FileOutputStream(worldFile);
		WorldDocumentWriter worldDocumentWriter = new WorldDocumentWriter(writeWorld);
		worldDocumentWriter.write(out);

		FileInputStream inputStream = new FileInputStream(worldFile);
		WorldDocumentReader worldDocumentReader = new WorldDocumentReader(inputStream);
		World readWorld = worldDocumentReader.read();


		testLocation = readWorld.getLocationForCoordinates(0, new Vector2D(Location.SIZE, Location.SIZE));
		Assert.assertTrue("false".equals(testLocation.getLocationProperties().getProperty("walkable")));
	}
}
