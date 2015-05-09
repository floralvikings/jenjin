package com.jenjinstudios.world.server.authentication;

import com.jenjinstudios.server.authentication.FullUserUpdate;
import com.jenjinstudios.server.database.DatabaseUpdate;
import com.jenjinstudios.world.server.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Used to update player objects in a database; if you are implementing a custom {@code Player} subclass with custom
 * properties, you should extend this class and override the {@code parseUserProperties} method, making sure to call the
 * method from the superclass at the beginning of your override.
 *
 * @author Caleb Brinkman
 */
public class FullPlayerUpdate<T extends Player> extends FullUserUpdate<T>
{
	private static final String X_COORD_KEY = "xCoord";
	private static final String Y_COORD_KEY = "yCoord";
	private static final String ZONE_ID_KEY = "zoneId";

	/**
	 * Construct a new FullPlayerUpdate instance that will use the supplied DatabaseUpdate instances to update users
	 * and
	 * their properties.
	 *
	 * @param userUpdate The {@link com.jenjinstudios.server.database.DatabaseUpdate} that will be used to update the
	 * user in the database.
	 * @param propertiesUpdate The {@link com.jenjinstudios.server.database.DatabaseUpdate} that will be used to update
	 */
	public FullPlayerUpdate(DatabaseUpdate<T> userUpdate, DatabaseUpdate<Map<String, String>> propertiesUpdate) {
		super(userUpdate, propertiesUpdate);
	}

	@Override
	protected Map<String, String> getUserPropertiesMap(T user) {
		Map<String, String> propertiesMap = new HashMap<>(3);
		propertiesMap.put(ZONE_ID_KEY, String.valueOf(user.getZoneID()));
		propertiesMap.put(X_COORD_KEY, String.valueOf(user.getVector2D().getXCoordinate()));
		propertiesMap.put(Y_COORD_KEY, String.valueOf(user.getVector2D().getYCoordinate()));
		return propertiesMap;
	}
}
