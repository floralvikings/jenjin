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
	 * @return A collection of retrieved objects. This collection will be empty if no results were found.
	 */
	List<T> lookup(Map<String, Object> where);

	/**
	 * Update the row matching the given conditions with the given data.
	 *
	 * @param where A key-value collection wherein the keys are the names of the columns, and the values are the values
	 * for which to update that row.
	 * @param row The new data to place in the row.
	 *
	 * @return Whether the table was updated.
	 */
	boolean update(Map<String, Object> where, T row);
}
