package com.jenjinstudios.world.io;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.Zone;
import com.jenjinstudios.world.math.Dimension2D;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Caleb Brinkman
 */
public class WorldDocumentWriterTest
{
	private static final String validWorldString =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
					"<world>\n" +
					"    <zone id=\"0\" xSize=\"15\" ySize=\"15\">\n" +
					"        <location walkable=\"false\" x=\"1\" y=\"1\"/>\n" +
					"    </zone>\n" +
					"</world>\n";

	@Test
	public void testWriteValidData() throws Exception {
		World world = createWorld();
		WorldDocumentWriter writer = new WorldDocumentWriter(world);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		writer.write(outputStream);

		InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		String result = "";
		String line = bufferedReader.readLine();
		while (line != null)
		{
			result += line + "\n";
			line = bufferedReader.readLine();
		}
		Assert.assertEquals(result, validWorldString);
	}

	private World createWorld() {
		Map<String, Object> locationProperties = new HashMap<>();
		locationProperties.put("walkable", "false");
		Location location = new Location(1, 1, locationProperties);
		Zone[] zones = {new Zone(0, new Dimension2D(15, 15), location)};
		return new World(zones);
	}
}
