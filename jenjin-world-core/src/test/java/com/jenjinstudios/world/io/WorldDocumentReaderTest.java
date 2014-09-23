package com.jenjinstudios.world.io;

import com.jenjinstudios.world.World;
import com.jenjinstudios.world.util.ZoneUtils;
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
				"       <location x=\"1\" y=\"1\">\n" +
				"			<properties>" +
				"				<entry>" +
				"					<key>walkable</key>" +
				"					<value>false</value>" +
				"				</entry>" +
				"			</properties>" +
				"		</location>\n" +
				"    </zone>\n" +
				"</world>";

	private static final String invalidWorldString =
		  "<world>ffffooooooooooobar\n" +
				"    <zone id=\"0\" xSize=\"15\" ySize=\"15\">\n" +
				"       <location x=\"1\" y=\"1\">\n" +
				"			<properties>" +
				"				<entry>" +
				"					<key>walkable</key>" +
				"					<value>false</value>" +
				"				</entry>" +
				"			</properties>" +
				"		</location>\n" +
				"    </zone>\n" +
				"</world>";

	@Test
	public void testReadValidDoc() throws Exception {
		byte[] worldStringBytes = validWorldString.getBytes(StandardCharsets.UTF_8);
		InputStream inputStream = new ByteArrayInputStream(worldStringBytes);
		WorldDocumentReader worldDocumentReader = new WorldDocumentReader(inputStream);
		World world = worldDocumentReader.read();
		String walkable = (String) ZoneUtils.getLocationOnGrid(world.getZone(0), 1, 1).getProperties().get("walkable");
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

	@Test(expectedExceptions = WorldDocumentException.class)
	public void testInvalidXml() throws Exception {
		byte[] worldStringBytes = invalidWorldString.getBytes(StandardCharsets.UTF_8);
		InputStream inputStream = new ByteArrayInputStream(worldStringBytes);
		WorldDocumentReader worldDocumentReader = new WorldDocumentReader(inputStream);
		worldDocumentReader.read();
	}

	@Test
	public void testGetWorldFileBytes() throws Exception {
		byte[] worldStringBytes = validWorldString.getBytes(StandardCharsets.UTF_8);
		InputStream inputStream = new ByteArrayInputStream(worldStringBytes);
		WorldDocumentReader worldDocumentReader = new WorldDocumentReader(inputStream);
		worldDocumentReader.read();

		Assert.assertEquals(worldDocumentReader.getWorldFileBytes(), worldStringBytes);
	}
}
