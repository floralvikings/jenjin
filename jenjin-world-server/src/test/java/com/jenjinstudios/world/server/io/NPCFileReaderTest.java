package com.jenjinstudios.world.server.io;

import com.jenjinstudios.world.server.NPC;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.server.io.NpcFileReader;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.List;

/**
 * Test the NPCFileReader class.
 * @author Caleb Brinkman
 */
public class NPCFileReaderTest
{
	/**
	 * Test the read functionality.
	 * @throws Exception If there's an exception.
	 */
	@Test
	public void testRead() throws Exception {
		World testWorld = new World();
		InputStream resourceAsStream = getClass().getResourceAsStream("/com/jenjinstudios/world/NPCFile01.xml");
		NpcFileReader npcFileReader = new NpcFileReader(testWorld, resourceAsStream);
		List<NPC> npcList = npcFileReader.read();
		for(NPC npc : npcList)
		{
			testWorld.addObject(npc);
		}
		Assert.assertEquals(1, testWorld.getObjectCount(), "World object count after reading NPCFile01.xml");
	}
}
