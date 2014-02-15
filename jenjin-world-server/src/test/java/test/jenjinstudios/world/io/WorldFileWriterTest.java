package test.jenjinstudios.world.io;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.LocationProperties;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.Zone;
import com.jenjinstudios.world.io.WorldFileReader;
import com.jenjinstudios.world.io.WorldFileWriter;
import com.jenjinstudios.world.math.Vector2D;
import org.testng.annotations.*;
import org.testng.Assert;

import java.io.File;
import java.util.TreeMap;

/**
 * Test the WorldFileWriter class.
 * @author Caleb Brinkman
 */
public class WorldFileWriterTest
{
	/** The file to be written to for testing purposes. */
	File worldFile;

	/**
	 * Set up the tests.
	 * @throws Exception If there's an exception.
	 */
	@BeforeMethod
	public void setUp() throws Exception {
		worldFile = new File("WorldFileWriterTest.xml");
		if(worldFile.exists() && worldFile.delete())
		{
			System.out.println("Deleting world test file before test.");
		}
	}

	/**
	 * Tear down the test.
	 * @throws Exception If there's an exception.
	 */
	@AfterMethod
	public void tearDown() throws Exception {
		if(worldFile.exists() && worldFile.delete())
		{
			System.out.println("Deleting world test file after test.");
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
		Location[] locArray = { testLocation };
		World writeWorld = new World(new Zone[] { new Zone(0, 5, 5, locArray )});

		WorldFileWriter worldFileWriter = new WorldFileWriter(writeWorld);
		worldFileWriter.write(worldFile);

		WorldFileReader worldFileReader = new WorldFileReader(worldFile);
		World readWorld = worldFileReader.read();


		testLocation = readWorld.getLocationForCoordinates(0, new Vector2D(Location.SIZE, Location.SIZE));
		Assert.assertTrue("false".equals(testLocation.getLocationProperties().getProperty("walkable")));
	}
}
