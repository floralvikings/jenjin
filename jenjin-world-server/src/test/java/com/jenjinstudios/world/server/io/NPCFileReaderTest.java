package com.jenjinstudios.world.server.io;

import com.jenjinstudios.world.World;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.server.NPC;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Caleb Brinkman
 */
public class NPCFileReaderTest
{
	private final String validNPCFileString = "" +
		  "<world>\n" +
		  "    <npc name=\"TestWanderer\" zoneID=\"0\" xCoordinate=\"50\" yCoordinate=\"50\">\n" +
		  "        <behaviors>\n" +
		  "            <behavior name=\"wanders\" value=\"true\" />\n" +
		  "        </behaviors>\n" +
		  "        <wander_targets>\n" +
		  "            <wander_target xLoc=\"0\" yLoc=\"3\"/>\n" +
		  "            <wander_target xLoc=\"1\" yLoc=\"6\"/>\n" +
		  "            <wander_target xLoc=\"4\" yLoc=\"1\"/>\n" +
		  "            <wander_target xLoc=\"0\" yLoc=\"0\"/>\n" +
		  "        </wander_targets>\n" +
		  "    </npc>\n" +
		  "</world>";

	private final String invalidNPCFileString = "" +
		  "<world><\n" +
		  "    <npc name=\"TestWanderer\" zoneID=\"0\" xCoordinate=\"50\" yCoordinate=\"50\">\n" +
		  "        <behaviors>\n" +
		  "            <behavior name=\"wanders\" value=\"true\" />\n" +
		  "        </behaviors>\n" +
		  "        <wander_targets>\n" +
		  "            <wander_target xLoc=\"0\" yLoc=\"3\"/>\n" +
		  "            <wander_target xLoc=\"1\" yLoc=\"6\"/>\n" +
		  "            <wander_target xLoc=\"4\" yLoc=\"1\"/>\n" +
		  "            <wander_target xLoc=\"0\" yLoc=\"0\"/>\n" +
		  "        </wander_targets>\n" +
		  "    </npc>\n" +
		  "</world>";

	@Test
	public void testReadValid() throws NPCDocumentException {
		World world = new World();
		byte[] npcFileBytes = validNPCFileString.getBytes(StandardCharsets.UTF_8);
		InputStream inputStream = new ByteArrayInputStream(npcFileBytes);
		NPCDocumentReader reader = new NPCDocumentReader(world, inputStream);
		List<NPC> npcList = reader.read();
		for (NPC npc : npcList)
		{
			world.addObject(npc);
		}

		NPC npc = (NPC) world.getObject(0);
		Assert.assertEquals(npc.getVector2D(), new Vector2D(50, 50));
	}

	@Test(expectedExceptions = NPCDocumentException.class)
	public void testReadInvalid() throws NPCDocumentException {
		World world = new World();
		byte[] npcFileBytes = invalidNPCFileString.getBytes(StandardCharsets.UTF_8);
		InputStream inputStream = new ByteArrayInputStream(npcFileBytes);
		NPCDocumentReader reader = new NPCDocumentReader(world, inputStream);
		reader.read();
	}
}
