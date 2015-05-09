package com.jenjinstudios.server.authentication;

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
}
