package com.jenjinstudios.server.database;

/**
 * Interface for classes used to retrieve data from a database.
 *
 * @author Caleb Brinkman
 */
public interface DatabaseLookup<T, R>
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
	default T lookup(String key) throws DatabaseException {
		R results = getDbResults(key);
		return create(results);
	}

	/**
	 * Query the database and return the results.  This method is responsible for any resource cleanup necessary after
	 * the query has been made.
	 *
	 * @param key The key used to look up the data.
	 *
	 * @return The results of the query.
	 *
	 * @throws DatabaseException If there's an exception when querying the database.
	 */
	R getDbResults(String key) throws DatabaseException;

	/**
	 * Create a new T given the database results.
	 *
	 * @param dbResults The results of a database query.
	 *
	 * @return The created T.
	 *
	 * @throws DatabaseException If the data in {@code dbResults} is unable to be deserialized into T.
	 */
	T create(R dbResults) throws DatabaseException;

}
