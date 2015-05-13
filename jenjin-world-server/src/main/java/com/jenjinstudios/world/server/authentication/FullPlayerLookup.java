package com.jenjinstudios.world.server.authentication;

import com.jenjinstudios.server.authentication.FullUserLookup;
import com.jenjinstudios.server.database.DatabaseException;
import com.jenjinstudios.server.database.DatabaseLookup;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.server.Player;

import java.util.Map;

/**
 * Used to get player objects from a database; if you are implementing a custom {@code Player} subclass with custom
 * properties, you should extend this class and override the {@code parseUserProperties} method, making sure to call the
 * method from the superclass at the beginning of your override.
 *
 * @author Caleb Brinkman
 */
public class FullPlayerLookup<T extends Player> extends FullUserLookup<T>
{
	private static final String X_COORD_KEY = "xCoord";
	private static final String Y_COORD_KEY = "yCoord";
	private static final String ZONE_ID_KEY = "zoneId";

	/**
	 * Construct a new FullUserLookup that will use the given DatabaseLookup objects to retrieve data and build a user,
	 * including the user's custom properties.
	 *
	 * @param userLookup The DatabaseLookup that will return the user.
	 * @param propertiesLookup The DatabaseLookup that will return the properties.
	 */
	public FullPlayerLookup(DatabaseLookup<T> userLookup, DatabaseLookup<Map<String, String>> propertiesLookup) {
		super(userLookup, propertiesLookup);
	}

	@Override
	protected void parseUserProperties(Map<String, String> properties, T user) throws DatabaseException {
		String zoneIdString = properties.get(ZONE_ID_KEY);
		if (zoneIdString == null) {
			throw new DatabaseException("zoneId not present in user properties");
		}

		String xCoordString = properties.get(X_COORD_KEY);
		if (xCoordString == null) {
			throw new DatabaseException("xCoord not present in user properties");
		}

		String yCoordString = properties.get(Y_COORD_KEY);
		if (yCoordString == null) {
			throw new DatabaseException("yCoord not present in user properties");
		}

		int zoneId;
		double xCoord;
		double yCoord;
		try {
			zoneId = Integer.parseInt(zoneIdString);
			xCoord = Double.parseDouble(xCoordString);
			yCoord = Double.parseDouble(yCoordString);
		} catch (NumberFormatException ex) {
			throw new DatabaseException(ex);
		}

		user.setZoneID(zoneId);
		user.setPosition(new Vector2D(xCoord, yCoord));
	}
}
