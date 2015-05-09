package com.jenjinstudios.server.authentication;

import com.jenjinstudios.server.database.DatabaseException;
import com.jenjinstudios.server.database.DatabaseLookup;

import java.util.Map;

/**
 * Used to lookup a user with the stored properties.
 *
 * @author Caleb Brinkman
 */
public abstract class FullUserLookup<T extends User> implements DatabaseLookup<T>
{
	private final DatabaseLookup<T> userLookup;
	private final DatabaseLookup<Map<String, String>> propertiesLookup;

	/**
	 * Construct a new FullUserLookup that will use the given DatabaseLookup objects to retrieve data and build a user,
	 * including the user's custom properties.
	 *
	 * @param userLookup The DatabaseLookup that will return the user.
	 * @param propertiesLookup The DatabaseLookup that will return the properties.
	 */
	protected FullUserLookup(DatabaseLookup<T> userLookup, DatabaseLookup<Map<String, String>> propertiesLookup) {
		this.userLookup = userLookup;
		this.propertiesLookup = propertiesLookup;
	}

	/**
	 * Retrieve a User from the database, including all custom properties; the first argument is the username, all
	 * other
	 * arguments are ignored.
	 *
	 * @param key The username.
	 *
	 * @return The retrieved User.
	 *
	 * @throws DatabaseException If there is an exception when retriving user data from the database.
	 */
	@Override
	public T lookup(String key) throws DatabaseException {
		Map<String, String> properties = propertiesLookup.lookup(key);
		T user = userLookup.lookup(key);

		parseUserProperties(properties, user);

		return user;
	}

	/**
	 * Using the supplied Map, parse and set properties on the supplied User.  This method is meant to be
	 * implemented in
	 * subclasses that require custom properties to be added to a user before the user is meant for use.
	 *
	 * @param properties The key value pairs of properties retrieved from a database, to be added to {@code user}.
	 * @param user The user to which the supplied properties should be added.
	 *
	 * @throws com.jenjinstudios.server.database.DatabaseException If there is an exception when parsing user
	 * properties.
	 */
	protected abstract void parseUserProperties(Map<String, String> properties, T user) throws DatabaseException;
}
