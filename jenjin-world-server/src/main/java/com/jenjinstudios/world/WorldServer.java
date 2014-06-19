package com.jenjinstudios.world;

import com.jenjinstudios.client.net.AuthServer;
import com.jenjinstudios.world.io.WorldFileReader;
import com.jenjinstudios.world.sql.WorldSQLHandler;

import java.io.IOException;

/**
 * The WorldServer class is responsible for updating a game world.
 * @author Caleb Brinkman
 */
public class WorldServer extends AuthServer<WorldClientHandler>
{
	/** The default updates-per-second for the world server. */
	public static final int DEFAULT_UPS = 50;
	/** The default port used for the world server. */
	public static final int DEFAULT_PORT = 51015;
	/** The world used by this server. */
	private final World world;
	/** The MD5 checksum for the world file. */
	private final byte[] worldFileChecksum;
	/** The bytes containing the world file. */
	private final byte[] worldFileBytes;

	/**
	 * Construct a new Server without a SQLHandler.
	 * @param worldFileReader The WorldFileReader used to read the world from a file.
	 * @param ups The cycles per second at which this server will run.
	 * @param port The port number on which this server will listen.
	 * @param wchClass The class of WorldClientHandler to use.
	 * @param sqlHandler The WorldSqlHandler used to communicate with the MySql Database.
	 * @throws java.io.IOException If there is an IO Error when initializing the server.
	 * @throws NoSuchMethodException If there is no appropriate constructor for the specified ClientHandler
	 * constructor.
	 */
	public WorldServer(WorldFileReader worldFileReader, int ups, int port, Class<? extends WorldClientHandler> wchClass,
					   WorldSQLHandler sqlHandler) throws IOException, NoSuchMethodException
	{
		super(ups, port, wchClass, sqlHandler);
		this.world = worldFileReader.read();
		worldFileBytes = worldFileReader.getWorldFileBytes();
		worldFileChecksum = worldFileReader.getWorldFileChecksum();
		addRepeatedTask(new Runnable()
		{
			@Override
			public void run() {
				world.update();
			}
		});
	}

	/**
	 * Get the world used by this server.
	 * @return The world used by this server.
	 */
	public World getWorld() { return world; }

	@Override
	public WorldSQLHandler getSqlHandler() { return (WorldSQLHandler) super.getSqlHandler(); }

	/**
	 * Get the world file checksum.
	 * @return The checksum for the world file.
	 */
	public byte[] getWorldFileChecksum() { return worldFileChecksum; }

	/**
	 * Get the bytes contained in the world file.
	 * @return The
	 */
	public byte[] getWorldFileBytes() { return worldFileBytes; }
}
