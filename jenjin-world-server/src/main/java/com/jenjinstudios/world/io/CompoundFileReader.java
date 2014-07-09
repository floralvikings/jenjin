package com.jenjinstudios.world.io;

import com.jenjinstudios.world.NPC;
import com.jenjinstudios.world.World;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * This class is used to read a World file which contains both World structure information and NPC information.
 * @author Caleb Brinkman
 */
public class CompoundFileReader
{
	/** The world being built from the XML. */
	private final World world;
	/** The NPCFileReader. */
	private NpcFileReader npcFileReader;

	/**
	 * Construct a new NPCFileReader for the given input stream.
	 * @param inputStream The stream containing the NPC XML.
	 * @throws javax.xml.parsers.ParserConfigurationException If there's an error parsing the XML.
	 * @throws java.io.IOException If there's an error reading the stream.
	 * @throws org.xml.sax.SAXException If there's an error validating the XML.
	 */
	public CompoundFileReader(InputStream inputStream) throws ParserConfigurationException, WorldDocumentException,
		  IOException, SAXException {
		InputStream worldFileStream = new NonClosableBufferedInputStream(inputStream);
		worldFileStream.mark(Integer.MAX_VALUE);
		WorldDocumentReader worldDocumentReader = new WorldDocumentReader(worldFileStream);
		world = worldDocumentReader.read();
		worldFileStream.reset();
		npcFileReader = new NpcFileReader(world, worldFileStream);
	}

	/**
	 * Read the XML document and return the correctly structured World containing all NPCs.
	 * @return The World represented by the XML with the NPCs already added.
	 */
	public World read() {
		List<NPC> npcs = npcFileReader.read();
		for (NPC npc : npcs)
		{
			world.addObject(npc);
		}
		return world;
	}

	/** A quick and dirty reusable input stream so the file can be read twice. */
	private class NonClosableBufferedInputStream extends BufferedInputStream
	{
		/**
		 * Construct a new NonClosableInputStream.
		 * @param in The input stream used to build this one.
		 */
		public NonClosableBufferedInputStream(InputStream in) {
			super(in);
			super.mark(Integer.MAX_VALUE);
		}

		@Override
		public void close() throws IOException { super.reset(); }
	}
}
