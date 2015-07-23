package com.jenjinstudios.server.authentication;

import com.jenjinstudios.server.database.DatabaseException;
import com.jenjinstudios.server.database.DatabaseLookup;

import java.util.Map;

/**
 * Used to lookup a user with the stored properties.
 *
 * @author Caleb Brinkman
 */
public class FullUserLookup<T extends User> implements DatabaseLookup<T, UserDbResult<T>>
{
    private final DatabaseLookup<T, ?> userLookup;
    private final DatabaseLookup<Map<String, String>, ?> propertiesLookup;
    private final UserFactory<T> userFactory;

    /** Used by Gson. */
    private FullUserLookup() { this(null, null, null); }

	/**
	 * Construct a new FullUserLookup that will use the given DatabaseLookup objects to retrieve data and build a user,
	 * including the user's custom properties.
	 *
	 * @param userLookup The DatabaseLookup that will return the user.
	 * @param propertiesLookup The DatabaseLookup that will return the properties.
	 * @param userFactory The factory used for creating users.
	 */
    public FullUserLookup(DatabaseLookup<T, ?> userLookup,
                          DatabaseLookup<Map<String, String>, ?> propertiesLookup,
                          UserFactory<T> userFactory)
    {
		this.userLookup = userLookup;
		this.propertiesLookup = propertiesLookup;
		this.userFactory = userFactory;
	}

	@Override
	public UserDbResult<T> getDbResults(String key) throws DatabaseException {
		T user = userLookup.lookup(key);
		Map<String, String> properties = propertiesLookup.lookup(key);

		return new UserDbResult<>(user, properties);
	}

	@Override
	public T create(UserDbResult<T> dbResults) throws DatabaseException {
		T user = dbResults.getUser();
		Map<String, String> properties = dbResults.getProperties();
		userFactory.populateUser(user, properties);
		return null;
	}
}
