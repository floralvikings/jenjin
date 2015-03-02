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
