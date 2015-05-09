package com.jenjinstudios.server.database;

/**
 * Interface for classes which will perform an update on a database.
 *
 * @author Caleb Brinkman
 */
@FunctionalInterface
public interface DatabaseUpdate<T>
{
	/**
	 * Used to update the representation of {@code object} in the database.  If the object doesn't already exist in the
	 * database, a {@code DatabaseException} will be thrown.
	 *
	 * @param object The object who's data should be updated.
	 *
	 * @return Whether any changes were made to the database.
	 *
	 * @throws DatabaseException If there is an exception when accessing the database.
	 */
	boolean update(T object) throws DatabaseException;

	/**
	 * Implemented when a class requires keys besides those contained in the object in order to properly update rows,
	 * such as for a collection.  The default implementation calls {@code update(T object)} with the {@code object}
	 * parameter and returns the result.
	 *
	 * @param object The object in the database to update.
	 * @param secondaryKeys Any secondary keys needed to update the object.
	 *
	 * @return Whether a change was made to the database.
	 *
	 * @throws DatabaseException If there is an exception when accessing the database.
	 */
	default boolean update(T object, String... secondaryKeys) throws DatabaseException { return update(object); }
}
