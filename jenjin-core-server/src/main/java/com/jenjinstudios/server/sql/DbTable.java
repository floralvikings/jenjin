package com.jenjinstudios.server.sql;

/**
 * Interface to be implemented by classes that will make database lookups.
 *
 * @author Caleb Brinkman
 */
public interface DbTable<T>
{
	T lookup(String key);
}
