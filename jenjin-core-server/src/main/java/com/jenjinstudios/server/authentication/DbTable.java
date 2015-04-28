package com.jenjinstudios.server.authentication;

/**
 * Interface to be implemented by classes that will make database lookups.
 *
 * @author Caleb Brinkman
 */
public interface DbTable<T extends User>
{

	/**
	 * Return the user from the database with the given username.
	 *
	 * @param username The name of the user to return.
	 *
	 * @return The user; null if not found.
	 *
	 * @throws DbException If there is an exception during the database transaction.
	 */
	T lookup(String username) throws DbException;

	/**
	 * Update the user in the database.
	 *
	 * @param user The User to uodate in the database, including properties.
	 *
	 * @return Whether the table was updated.
	 *
	 * @throws DbException If there is an exception during the database transaction.
	 */
	boolean update(T user) throws DbException;
}
