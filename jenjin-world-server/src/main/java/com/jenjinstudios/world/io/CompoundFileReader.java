package com.jenjinstudios.world.io;

import com.jenjinstudios.world.NPC;
import com.jenjinstudios.world.World;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.security.NoSuchAlgorithmException;
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
	private NPCFileReader npcFileReader;

	/**
	 * Construct a new NPCFileReader for the given file.
	 * @param npcFile The file containing the NPC info.
	 * @throws java.io.IOException If there's an error reading the file.
	 * @throws javax.xml.parsers.ParserConfigurationException If there's an error parsing the XML.
	 * @throws org.xml.sax.SAXException If there's an error validating the XML.
	 * @throws java.security.NoSuchAlgorithmException If transform algorithms cannot be found.
	 * @throws javax.xml.transform.TransformerException If there is a Transformer Exception.
	 */
	public CompoundFileReader(File npcFile) throws IOException, ParserConfigurationException, SAXException, TransformerException, NoSuchAlgorithmException {
		this(new FileInputStream(npcFile));
	}

	/**
	 * Construct a new NPCFileReader for the given input stream.
	 * @param inputStream The stream containing the NPC XML.
	 * @throws javax.xml.parsers.ParserConfigurationException If there's an error parsing the XML.
	 * @throws java.io.IOException If there's an error reading the stream.
	 * @throws org.xml.sax.SAXException If there's an error validating the XML.
	 * @throws java.security.NoSuchAlgorithmException If transform algorithms cannot be found.
	 * @throws javax.xml.transform.TransformerException If there is a Transformer Exception.
	 */
	public CompoundFileReader(InputStream inputStream) throws ParserConfigurationException, IOException, SAXException, TransformerException, NoSuchAlgorithmException {
		InputStream worldFileStream = new NonClosableBufferedInputStream(inputStream);
		worldFileStream.mark(Integer.MAX_VALUE);
		WorldFileReader worldFileReader = new WorldFileReader(worldFileStream);
		world = worldFileReader.read();
		worldFileStream.reset();
		npcFileReader = new NPCFileReader(world, worldFileStream);
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
		public void close() throws IOException {
			super.reset();
		}
	}
}
