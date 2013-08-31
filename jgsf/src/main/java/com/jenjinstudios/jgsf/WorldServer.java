package com.jenjinstudios.jgsf;

import com.jenjinstudios.sql.SQLHandler;
import com.jenjinstudios.sql.WorldSQLHandler;
import com.jenjinstudios.world.World;

import java.sql.SQLException;

/**
 * The WorldServer class is responsible for updating a game world.
 *
 * @author Caleb Brinkman
 */
public class WorldServer extends Server<WorldClientHandler>
{
	/** The default updates-per-second for the world server. */
	public static final int DEFAULT_UPS = 50;
	/** The default port used for the world server. */
	public static final int DEFAULT_PORT = 51015;
	/** The world used by this server. */
	private final World world;
	/** The SQLHandler for this world server. */
	private WorldSQLHandler sqlHandler;

	/**
	 * Construct a new Server without a SQLHandler.
	 *
	 * @param worldToBeUsed The world to be used by this server.
	 * @param ups           The cycles per second at which this server will run.
	 * @param port          The port number on which this server will listen.
	 * @param wchClass      The class of WorldClientHandler to use.
	 */
	public WorldServer(World worldToBeUsed, int ups, int port, Class<? extends WorldClientHandler> wchClass)
	{
		super(ups, port, wchClass);
		this.world = worldToBeUsed;
		addRepeatedTask(new Runnable()
		{
			@Override
			public void run()
			{
				world.update();
			}
		});
	}

	/**
	 * Construct a new Server without a SQLHandler.
	 *
	 * @param world The world to be used by this server.
	 * @param ups   The cycles per second at which this server will run.
	 * @param port  The port number on which this server will listen.
	 */
	public WorldServer(World world, int ups, int port)
	{
		this(world, ups, port, WorldClientHandler.class);
	}

	/**
	 * Construct a new Server without a SQLHandler.
	 *
	 * @param world The world to be used by this server.
	 * @param port  The port number on which this server will listen.
	 */
	public WorldServer(World world, int port)
	{
		this(world, DEFAULT_UPS, port);
	}

	/**
	 * Construct a new Server without a SQLHandler.
	 *
	 * @param world The world to be used by this server.
	 */
	public WorldServer(World world)
	{
		this(world, DEFAULT_PORT);
	}

	/** Construct a new WorldServer. */
	public WorldServer()
	{
		this(new World());
	}

	/**
	 * Get the world used by this server.
	 *
	 * @return The world used by this server.
	 */
	public World getWorld()
	{
		return world;
	}

	@Override
	public WorldSQLHandler getSqlHandler()
	{
		return sqlHandler;
	}

	/**
	 * Set the SQLHandler.
	 *
	 * @param sqlHandler The WorldSQLHandler.
	 * @throws SQLException If there is a SQL exception.
	 */
	public void setSQLHandler(WorldSQLHandler sqlHandler) throws SQLException
	{
		super.setSQLHandler(sqlHandler);
		this.sqlHandler = sqlHandler;
	}

	public void setSQLHandler(SQLHandler sqlHandler) throws SQLException
	{
		if (!(sqlHandler instanceof WorldSQLHandler))
			throw new IllegalArgumentException("SQL Handler is not instance of WorldSQLHandler");
		setSQLHandler((WorldSQLHandler) sqlHandler);
	}
}
