package com.jenjinstudios.world.server.sql;

import com.jenjinstudios.server.net.User;
import com.jenjinstudios.server.sql.Authenticator;
import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.server.WorldClientHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles SQL stuff for a WorldServer.
 * @author Caleb Brinkman
 */
@SuppressWarnings("SpellCheckingInspection")
public class WorldAuthenticator extends Authenticator
{
	/** The column name of the X coordinate. */
	private static final String X_COORD = "xCoord";
	/** The column name of the Y coordinate. */
	private static final String Y_COORD = "yCoord";
	/** The column name of the zone ID. */
	private static final String ZONE_ID = "zoneID";
	/** The Logger used for this class. */
	private static final Logger LOGGER = Logger.getLogger(WorldAuthenticator.class.getName());

	/**
	 * Create a new SQLHandler with the given database information, and connect to the database.
	 */
	public WorldAuthenticator(Connection connection) { super(connection); }

	public void updatePlayer(WorldClientHandler worldClientHandler) {
		Actor player = worldClientHandler.getPlayer();
		User user = worldClientHandler.getUser();

		user.getProperties().put(X_COORD, player.getVector2D().getXCoordinate());
		user.getProperties().put(Y_COORD, player.getVector2D().getYCoordinate());
		user.getProperties().put(ZONE_ID, player.getZoneID());

		try
		{
			super.updateUserProperties(user);
		} catch (SQLException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to update player information in database!", e);
		}
	}
}
