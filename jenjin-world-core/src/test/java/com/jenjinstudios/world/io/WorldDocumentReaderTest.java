package com.jenjinstudios.world.io;

import com.jenjinstudios.world.World;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Caleb Brinkman
 */
public class WorldDocumentReaderTest
{
	private static final String validWorldString =
			"<world>\n" +
					"    <zone id=\"0\" xSize=\"15\" ySize=\"15\">\n" +
					"       <location x=\"1\" y=\"1\" walkable=\"false\" />\n" +
					"    </zone>\n" +
					"</world>";

	@Test
	public void testReadValidDoc() throws Exception {
		byte[] worldStringBytes = validWorldString.getBytes(StandardCharsets.UTF_8);
		InputStream inputStream = new ByteArrayInputStream(worldStringBytes);
		WorldDocumentReader worldDocumentReader = new WorldDocumentReader(inputStream);
		World world = worldDocumentReader.read();
		String walkable = world.getZone(0).getLocationOnGrid(1, 1).getLocationProperties().getProperty("walkable");
		Assert.assertEquals(walkable, "false");
	}

	@Test
	public void testChecksum() throws Exception {
		byte[] worldStringBytes = validWorldString.getBytes(StandardCharsets.UTF_8);
		InputStream inputStream = new ByteArrayInputStream(worldStringBytes);
		WorldDocumentReader worldDocumentReader = new WorldDocumentReader(inputStream);
		worldDocumentReader.read();

		byte[] checksum = worldDocumentReader.getWorldFileChecksum();
		byte[] expected = ChecksumUtil.getMD5Checksum(worldStringBytes);
		Assert.assertEquals(checksum, expected);
	}
}
