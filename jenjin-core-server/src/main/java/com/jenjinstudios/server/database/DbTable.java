package com.jenjinstudios.server.database;

import java.util.List;
import java.util.Map;

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

	/**
	 * Return the rows from the database with column names matching the keys, and records of those columns matching the
	 * values.
	 *
	 * @param where A key-value collection wherein the keys are the names of the columns, and the values are the values
	 * for which to return that row.
	 *
	 * @return A collection of retrieved objects.
	 */
	List<T> lookup(Map<String, Object> where);

}
