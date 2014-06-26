package com.jenjinstudios.world;

import com.jenjinstudios.server.net.AuthServer;
import com.jenjinstudios.server.net.ServerInit;
import com.jenjinstudios.world.io.WorldDocumentReader;
import com.jenjinstudios.world.sql.WorldAuthenticator;

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
	 * @param sqlHandler The WorldSqlHandler used to communicate with the MySql Database.
	 * @param worldDocumentReader The WorldFileReader used to read the world from a file.
	 * @throws java.io.IOException If there is an IO Error when initializing the server.
	 * @throws NoSuchMethodException If there is no appropriate constructor for the specified ClientHandler
	 * constructor.
	 */
	public WorldServer(ServerInit<WorldClientHandler> initInfo, WorldAuthenticator sqlHandler, WorldDocumentReader worldDocumentReader) throws IOException, NoSuchMethodException {
		super(initInfo, sqlHandler);
		this.world = worldDocumentReader.read();
		worldFileBytes = worldDocumentReader.getWorldFileBytes();
		worldFileChecksum = worldDocumentReader.getWorldFileChecksum();
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
	public WorldAuthenticator getAuthenticator() { return (WorldAuthenticator) super.getAuthenticator(); }

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
