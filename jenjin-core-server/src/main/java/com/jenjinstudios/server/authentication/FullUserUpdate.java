package com.jenjinstudios.server.authentication;

import com.jenjinstudios.server.database.DatabaseException;
import com.jenjinstudios.server.database.DatabaseUpdate;

import java.util.Map;

/**
 * Used to update both a user and the custom properties of that user in a database.
 *
 * @author Caleb Brinkman
 */
public abstract class FullUserUpdate<T extends User> implements DatabaseUpdate<T>
{
	private final DatabaseUpdate<T> userUpdate;
	private final DatabaseUpdate<Map<String, String>> propertiesUpdate;

    /** Used by Gson. */
    private FullUserUpdate() { this(null, null); }

	/**
	 * Construct a new FullUserUpdate instance that will use the supplied DatabaseUpdate instances to update users and
	 * their properties.
	 *
     * @param userUpdate The {@link DatabaseUpdate} that will be used to update the
     * user in the database.
     * @param propertiesUpdate The {@link DatabaseUpdate} that will be used to update
     * the user's custom properties in the database.
	 */
	protected FullUserUpdate(DatabaseUpdate<T> userUpdate, DatabaseUpdate<Map<String, String>> propertiesUpdate) {
		this.userUpdate = userUpdate;
		this.propertiesUpdate = propertiesUpdate;
	}

	@Override
	public boolean update(T object) throws DatabaseException {
		boolean changed = userUpdate.update(object);
		Map<String, String> map = getUserPropertiesMap(object);
		changed |= propertiesUpdate.update(map, object.getUsername());
		return changed;
	}

	/**
     * Build a {@link Map} of properties from the given user.  The keys of this map should be the names of
     * the
	 * properties (likely the same as the property name in the class) and the values should be String
	 * representations of
	 * the values of those properties.
	 *
	 * @param user The user for which to build the map.
	 *
	 * @return The constructed Map.
	 */
	protected abstract Map<String, String> getUserPropertiesMap(T user);
}
