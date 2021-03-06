package com.jenjinstudios.world.server;

import com.jenjinstudios.server.net.AuthServer;
import com.jenjinstudios.server.net.ClientHandler;
import com.jenjinstudios.server.net.ServerInit;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.io.WorldDocumentReader;
import com.jenjinstudios.world.io.WorldDocumentWriter;
import com.jenjinstudios.world.server.sql.WorldAuthenticator;
import com.jenjinstudios.world.util.WorldUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * The WorldServer class is responsible for updating a game world.
 * @author Caleb Brinkman
 */
public class WorldServer extends AuthServer
{
	private final World world;
	private final byte[] worldFileChecksum;
	private final byte[] worldFileBytes;

	/**
	 * Construct a new Server without a SQLHandler.
	 * @param authenticator The WorldSqlHandler used to communicate with the MySql Database.
	 * @param reader The WorldFileReader used to read the world from a file.
	 * @throws java.io.IOException If there is an IO Error when initializing the server.
	 * @throws NoSuchMethodException If there is no appropriate constructor for the specified ClientHandler
	 * constructor.
	 */
	public WorldServer(ServerInit init, WorldAuthenticator authenticator, WorldDocumentReader reader)
	throws IOException, NoSuchMethodException
	{
		super(init, authenticator);
		if (reader != null)
		{
			this.world = reader.read();
		} else
		{
			this.world = WorldUtils.createDefaultWorld();
			WorldDocumentWriter writer = new WorldDocumentWriter(world);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			writer.write(bos);
			reader = new WorldDocumentReader(new ByteArrayInputStream(bos.toByteArray()));
			reader.read();
		}
		worldFileBytes = reader.getWorldFileBytes();
		worldFileChecksum = reader.getWorldFileChecksum();
		addRepeatedTask(world::update);
	}

	public World getWorld() { return world; }

	@Override
	public WorldAuthenticator getAuthenticator() { return (WorldAuthenticator) super.getAuthenticator(); }

	public byte[] getWorldFileChecksum() { return worldFileChecksum; }

	public byte[] getWorldFileBytes() { return worldFileBytes; }

	@Override
	public void removeClient(ClientHandler handler) {
		super.removeClient(handler);
		if (((WorldClientHandler) handler).getPlayer() != null)
			world.getWorldObjects().remove(((WorldClientHandler) handler).getPlayer().getId());
	}
}
