package com.jenjinstudios.world.io;

import com.google.gson.Gson;
import com.jenjinstudios.world.World;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Used to write world objects to file.
 * @author Caleb Brinkman
 */
public class WorldDocumentWriter
{
	/** The world to be written to file. */
	private final World world;

	/**
	 * Construct a new WorldFileWriter.
	 * @param world The world to be written to file.
	 */
	public WorldDocumentWriter(World world) { this.world = world; }

	public void write(OutputStream outputStream) throws WorldDocumentException {
		String json = new Gson().toJson(world);
		PrintWriter printWriter = new PrintWriter(outputStream, true);
		printWriter.write(json);
		printWriter.close();
	}

}
