package com.jenjinstudios.server.authentication;

import java.util.Map;

/**
 * Used by a UserLookup to create user objects.
 *
 * @author Caleb Brinkman
 */
@FunctionalInterface
public interface UserFactory<T extends User>
{
	/**
	 * Create a user with the given username.
	 *
	 * @param username The username of the user to create.
	 *
	 * @return The created user.
	 */
	T createUser(String username);

	/**
	 * Crate a user with the given username and properties.  By default, returns the value of {@link
	 * #createUser(String)} and ignores the property map.
	 *
	 * @param user The user to be populated with the given data.
	 * @param properties The properties of the user.
	 */
	default void populateUser(T user, Map<String, String> properties) { }
}
