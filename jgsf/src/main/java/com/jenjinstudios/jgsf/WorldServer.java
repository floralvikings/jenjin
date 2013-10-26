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
public class WorldServer extends SqlEnabledServer<WorldClientHandler>
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
	 * Construct a new WorldServer.
	 *
	 * @param sqlHandler The SQLHandler to be used by this server.
	 *
	 * @throws java.sql.SQLException If there's a SQLException.
	 */
	public WorldServer(WorldSQLHandler sqlHandler) throws SQLException
	{
		this(new World(), sqlHandler);
	}

	/**
	 * Construct a new SqlEnabledServer without a SQLHandler.
	 *
	 * @param world      The world to be used by this server.
	 * @param sqlHandler The SQLHandler to be used by this server.
	 *
	 * @throws java.sql.SQLException If there's a SQLException.
	 */
	public WorldServer(World world, WorldSQLHandler sqlHandler) throws SQLException
	{
		this(world, DEFAULT_PORT, sqlHandler);
	}

	/**
	 * Construct a new SqlEnabledServer without a SQLHandler.
	 *
	 * @param world      The world to be used by this server.
	 * @param port       The port number on which this server will listen.
	 * @param sqlHandler The SQLHandler to be used by this server.
	 *
	 * @throws java.sql.SQLException If there's a SQLException.
	 */
	public WorldServer(World world, int port, WorldSQLHandler sqlHandler) throws SQLException
	{
		this(world, DEFAULT_UPS, port, sqlHandler);
	}

	/**
	 * Construct a new SqlEnabledServer without a SQLHandler.
	 *
	 * @param world      The world to be used by this server.
	 * @param ups        The cycles per second at which this server will run.
	 * @param port       The port number on which this server will listen.
	 * @param sqlHandler The SQLHandler to be used by this server.
	 *
	 * @throws java.sql.SQLException If there's a SQLException.
	 */
	public WorldServer(World world, int ups, int port, WorldSQLHandler sqlHandler) throws SQLException
	{
		this(world, ups, port, WorldClientHandler.class, sqlHandler);
	}

	/**
	 * Construct a new SqlEnabledServer without a SQLHandler.
	 *
	 * @param worldToBeUsed The world to be used by this server.
	 * @param ups           The cycles per second at which this server will run.
	 * @param port          The port number on which this server will listen.
	 * @param wchClass      The class of WorldClientHandler to use.
	 * @param sqlHandler    The SQLHandler to be used by this server.
	 *
	 * @throws java.sql.SQLException If there's a SQLException.
	 */
	public WorldServer(World worldToBeUsed, int ups, int port, Class<? extends WorldClientHandler> wchClass, WorldSQLHandler sqlHandler) throws SQLException
	{
		super(ups, port, wchClass, sqlHandler);
		this.world = worldToBeUsed;
		setSQLHandler(sqlHandler);
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
	 * Set the SQLHandler.
	 *
	 * @param sqlHandler The WorldSQLHandler.
	 *
	 * @throws SQLException If there is a SQL exception.
	 */
	protected void setSQLHandler(WorldSQLHandler sqlHandler) throws SQLException
	{
		this.sqlHandler = sqlHandler;
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

	public void setSQLHandler(SQLHandler sqlHandler) throws SQLException
	{
		if (!(sqlHandler instanceof WorldSQLHandler))
			throw new IllegalArgumentException("SQL Handler is not instance of WorldSQLHandler");
		setSQLHandler((WorldSQLHandler) sqlHandler);
	}

	@Override
	public WorldSQLHandler getSqlHandler()
	{
		return sqlHandler;
	}
}
