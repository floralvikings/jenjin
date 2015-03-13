package com.jenjinstudios.world.server.database;

import com.jenjinstudios.server.authentication.Authenticator;
import com.jenjinstudios.world.server.database.sql.PlayerTable;

import java.sql.Connection;

/**
 * Handles SQL stuff for a WorldServer.
 * @author Caleb Brinkman
 */
@SuppressWarnings("SpellCheckingInspection")
public class WorldAuthenticator extends Authenticator
{

	/**
	 * Create a new SQLHandler with the given database information, and connect to the database.
	 */
	public WorldAuthenticator(Connection connection) {
		super(new PlayerTable(connection));
	}

}
