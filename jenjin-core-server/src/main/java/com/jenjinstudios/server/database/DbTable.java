package com.jenjinstudios.server.database;

/**
 * Interface to be implemented by classes that will make database lookups.
 *
 * @author Caleb Brinkman
 */
public interface DbTable<T>
{
	/**
	 * Look up the object in the database with the given primary key.
	 *
	 * @param key The primary key used to make the lookup query.
	 *
	 * @return The object stored in the database with the given primary key.
	 */
	T lookup(String key);
}
