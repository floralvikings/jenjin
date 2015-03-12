package com.jenjinstudios.server.database;

import com.jenjinstudios.server.net.User;

/**
 * Interface for all database calsses used to lookup users.
 *
 * @author Caleb Brinkman
 */
public interface UserLookup
{
	/**
	 * Find the user with the given username, if it exists.
	 *
	 * @param username The username of the user to look for.
	 *
	 * @return The found User, or null if the user doesn't exist.
	 *
	 * @throws com.jenjinstudios.server.database.DbException If there is an error accessing the database.
	 */
	User findUser(String username) throws DbException;

	/**
	 * Update the given user in the databse.
	 *
	 * @param user The user to update.
	 *
	 * @return Whether an update was made.
	 *
	 * @throws DbException If there is an exception during the database update.
	 */
	boolean updateUser(User user) throws DbException;
}
