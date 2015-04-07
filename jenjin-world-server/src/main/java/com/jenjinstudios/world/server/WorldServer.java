package com.jenjinstudios.world.server;

import com.jenjinstudios.core.io.MessageRegistry;
import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.net.Server;
import com.jenjinstudios.server.net.ServerInit;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.io.WorldDocumentReader;
import com.jenjinstudios.world.io.WorldDocumentWriter;
import com.jenjinstudios.world.util.WorldUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * The WorldServer class is responsible for updating a game world.
 * @author Caleb Brinkman
 */
public class WorldServer<T extends WorldClientHandler> extends Server<T>
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
	public WorldServer(ServerInit init, Authenticator<Player> authenticator, WorldDocumentReader reader)
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
		InputStream stream = getClass().getClassLoader().
			  getResourceAsStream("com/jenjinstudios/world/server/Messages.xml");
		MessageRegistry.getGlobalRegistry().register("World Client/Server Messages", stream);
	}

	public World getWorld() { return world; }

	@Override
	public void run() {
		super.run();
		getServerUpdateTask().addRepeatedTask(world::update);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Authenticator<Player> getAuthenticator() { return (Authenticator<Player>) super.getAuthenticator(); }

	public byte[] getWorldFileChecksum() { return worldFileChecksum; }

	public byte[] getWorldFileBytes() { return worldFileBytes; }

	@Override
	public void clientHandlerAdded(T h) {
		super.clientHandlerAdded(h);
		h.getMessageContext().setWorld(world);
		h.getMessageContext().setWorldChecksum(worldFileChecksum);
		h.getMessageContext().setWorldBytes(worldFileBytes);
	}

	@Override
	public void removeClient(T handler) {
		super.removeClient(handler);
		if (handler.getMessageContext().getUser() != null)
			world.getWorldObjects().remove(handler.getMessageContext().getUser().getId());
	}
}
