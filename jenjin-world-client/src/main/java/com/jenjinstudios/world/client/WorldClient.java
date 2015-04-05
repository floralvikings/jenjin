package com.jenjinstudios.world.client;

import com.jenjinstudios.client.net.Client;
import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.core.io.MessageStreamPair;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.client.message.WorldClientMessageFactory;
import com.jenjinstudios.world.io.WorldDocumentException;
import com.jenjinstudios.world.io.WorldDocumentReader;
import com.jenjinstudios.world.io.WorldDocumentWriter;

import java.io.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The WorldClient class is used to connect to a WorldServer and stores information about the environment immediately
 * surrounding the player.
 *
 * @author Caleb Brinkman
 */
public class WorldClient<T extends WorldClientMessageContext> extends Client<T>
{
    private static final Logger LOGGER = Logger.getLogger(WorldClient.class.getName());
    private final WorldClientMessageFactory messageFactory;
    private final WorldFileTracker worldFileTracker;
	private final File worldFile;
	private WorldDocumentReader worldDocumentReader;
	private World world;

	public WorldClient(MessageStreamPair messageStreamPair, File worldFile, T context) throws
		  WorldDocumentException {
		super(messageStreamPair, context);
		this.worldFile = worldFile;
		this.messageFactory = new WorldClientMessageFactory();
		worldDocumentReader = getDocumentReaderFromFile();
		if (worldDocumentReader != null)
		{
			world = worldDocumentReader.read();
		}
		worldFileTracker = new WorldFileTracker();
		getMessageContext().setWorld(world);
		InputStream stream = getClass().getClassLoader().
			  getResourceAsStream("com/jenjinstudios/world/client/Messages.xml");
		MessageRegistry.getGlobalRegistry().register("World Client/Server Messages", stream);
	}

	public void writeWorldToFile() throws WorldDocumentException {
		createNewFile();
		byte[] bytes = getMessageContext().getWorldFileTracker().getBytes();
		World readWorld = null;
		if (bytes != null)
		{
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
			worldDocumentReader = new WorldDocumentReader(byteArrayInputStream);
			readWorld = worldDocumentReader.read();
		}
		writeWorldToFile(worldFile, readWorld);
	}

	private void createNewFile() throws WorldDocumentException {
		if (!tryCreateWorldFileDirectory() || !tryCreateWorldFile())
		{
			throw new WorldDocumentException("Unable to create new world file!");
		}
	}

	private boolean tryCreateWorldFile() throws WorldDocumentException {
		try
		{
			return worldFile.exists() || worldFile.createNewFile();
		} catch (IOException e)
		{
			throw new WorldDocumentException("Unable to create new file.", e);
		}
	}

	private boolean tryCreateWorldFileDirectory() {
		return worldFile.getParentFile().exists() || worldFile.getParentFile().mkdirs();
	}

	private static void writeWorldToFile(File worldFile, World world) throws WorldDocumentException {
		try (FileOutputStream worldOut = new FileOutputStream(worldFile))
		{
			WorldDocumentWriter worldDocumentWriter = new WorldDocumentWriter(world);
			worldDocumentWriter.write(worldOut);
		} catch (IOException ex)
		{
			throw new WorldDocumentException("Unable to write world file.", ex);
		} catch (NullPointerException ex)
		{
			throw new WorldDocumentException("Can't write world to file until world data has been received.");
		}
	}

	protected WorldDocumentReader getDocumentReaderFromFile() throws WorldDocumentException
	{
		WorldDocumentReader reader = null;
		if ((worldFile != null) && worldFile.exists())
		{
			try
			{
				FileInputStream inputStream = new FileInputStream(worldFile);
				reader = new WorldDocumentReader(inputStream);
				getMessageContext().getWorldFileTracker().setBytes(reader.getWorldFileBytes());
			} catch (FileNotFoundException e)
			{
				throw new WorldDocumentException("Couldn't find world file.", e);
			}
		}
		return reader;
	}

	public void waitForCheckSum() {
		while (getMessageContext().getWorldFileTracker().isWaitingForChecksum())
		{
			waitTenMillis();
		}
	}

	static void waitTenMillis() {
		try
		{
			Thread.sleep(10);
		} catch (InterruptedException e)
		{
			LOGGER.log(Level.WARNING, "Interrupted while waiting.");
		}
	}

	public WorldClientMessageFactory getMessageFactory() {return messageFactory; }

	public Actor getPlayer() { return getMessageContext().getPlayer(); }

	public World getWorld() { return world; }

	public void initializeWorldFromServer() throws WorldDocumentException {
		getMessageContext().getWorldFileTracker().setWaitingForChecksum(true);
		enqueueMessage(messageFactory.generateWorldChecksumRequest());
		LOGGER.log(Level.INFO, "Requested World Checksum.");
		waitForCheckSum();
		LOGGER.log(Level.INFO, "Received World Checksum.");

		if ((worldDocumentReader != null) &&
			  !Arrays.equals(getMessageContext().getWorldFileTracker().getChecksum(),
					worldDocumentReader.getWorldFileChecksum()))
		{
			getMessageContext().getWorldFileTracker().setWaitingForFile(true);
			enqueueMessage(messageFactory.generateWorldFileRequest());
			LOGGER.log(Level.INFO, "Requested World File.");
			while (getMessageContext().getWorldFileTracker().isWaitingForFile())
			{
				waitTenMillis();
			}
			LOGGER.log(Level.INFO, "Received World File.");
			writeWorldToFile();
		}

		worldDocumentReader = getDocumentReaderFromFile();
		world = worldDocumentReader.read();
		getMessageContext().setWorld(world);
	}

}
