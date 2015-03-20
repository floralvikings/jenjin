package com.jenjinstudios.world.server.database;

import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.server.authentication.UserLookup;
import com.jenjinstudios.world.server.Player;
import com.jenjinstudios.world.server.sql.PlayerTable;

import java.sql.Connection;

/**
 * Handles SQL stuff for a WorldServer.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("SpellCheckingInspection")
public class WorldAuthenticator extends Authenticator<Player>
{

	/**
	 * Create a new SQLHandler with the given database information, and connect to the database.
	 *
	 * @param connection The database connection used by the authenticator.
	 */
	public WorldAuthenticator(Connection connection) {
		super(new PlayerTable(connection));
	}

	/**
	 * Construct a new WorldAuthenticator using the supplied UserLookup.
	 *
	 * @param userLookup The UserLookup used to find and update users.
	 */
	public WorldAuthenticator(UserLookup<Player> userLookup) {
		super(userLookup);
	}

}
