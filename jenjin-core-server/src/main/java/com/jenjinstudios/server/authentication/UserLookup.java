package com.jenjinstudios.server.authentication;

import com.jenjinstudios.server.database.DatabaseLookup;

/**
 * Represents a database lookup that is designed to look up User objects.
 *
 * @author Caleb Brinkman
 */
public interface UserLookup<T extends User, R> extends DatabaseLookup<T, R>
{
	/**
	 * Get the UserFactory used to create and populate user objects.
	 *
	 * @return The UserFactory used to create and populate user objects.
	 */
	UserFactory<T> getUserFactory();
}
