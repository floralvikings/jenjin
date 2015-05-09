package com.jenjinstudios.server.database;

/**
 * Interface for classes used to retrieve data from a database.
 *
 * @author Caleb Brinkman
 */
@FunctionalInterface
public interface DatabaseLookup<T>
{
	/**
	 * Lookup the object in the database with the given key.
	 *
	 * @param key The key to be used to look up the data.
	 *
	 * @return The retrieved object.
	 *
	 * @throws DatabaseException If there's an exception when interacting with the database.
	 */
	T lookup(String key) throws DatabaseException;

}
