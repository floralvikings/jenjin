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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The WorldServer class is responsible for updating a game world.
 * @author Caleb Brinkman
 */
public class WorldServer<U extends Player, T extends WorldServerMessageContext<U>> extends Server<U, T>
{
	private final ScheduledExecutorService worldUpdateExecutor = Executors.newSingleThreadScheduledExecutor();
	private final WorldUpdateTask worldUpdateTask;
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
	public WorldServer(ServerInit<T> init, Authenticator<U> authenticator, WorldDocumentReader reader)
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

		getConnectionPool().addUpdateTask(new ConnectionWorldUpdateTask<>());
		getConnectionPool().addShutdownTask(new ConnectionWorldShutdownTask<>(world));
		getConnectionPool().addConnectionAddedTask(connection -> {
			connection.getMessageContext().setWorld(world);
			connection.getMessageContext().setWorldChecksum(worldFileChecksum);
			connection.getMessageContext().setWorldBytes(worldFileBytes);
		});

		worldUpdateTask = new WorldUpdateTask(world);
	}

	public World getWorld() { return world; }

	@Override
	public void start() {
		super.start();
		worldUpdateExecutor.scheduleAtFixedRate(worldUpdateTask, 0, 1000 / getUps(), TimeUnit.MILLISECONDS);
	}

	@Override
	public void shutdown() throws IOException {
		super.shutdown();
		worldUpdateExecutor.shutdown();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Authenticator<Player> getAuthenticator() { return (Authenticator<Player>) super.getAuthenticator(); }

	public byte[] getWorldFileChecksum() { return worldFileChecksum; }

	public byte[] getWorldFileBytes() { return worldFileBytes; }

	private class WorldUpdateTask implements Runnable
	{
		private final World world;

		private WorldUpdateTask(World world)
		{
			this.world = world;
		}

		@Override
		public void run() {
			world.update();
		}
	}
}
