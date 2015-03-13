package com.jenjinstudios.world.server.database;

import com.jenjinstudios.server.authentication.AbstractAuthenticator;
import com.jenjinstudios.world.server.Player;
import com.jenjinstudios.world.server.database.sql.PlayerTable;

import java.sql.Connection;

/**
 * Handles SQL stuff for a WorldServer.
 * @author Caleb Brinkman
 */
@SuppressWarnings("SpellCheckingInspection")
public class WorldAuthenticator extends AbstractAuthenticator<Player>
{

	/**
	 * Create a new SQLHandler with the given database information, and connect to the database.
	 * @param connection The database connection used by the authenticator.
	 */
	public WorldAuthenticator(Connection connection) {
		super(new PlayerTable(connection));
	}

}
