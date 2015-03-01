package com.jenjinstudios.server.sql;

/**
 * @author Caleb Brinkman
 */
public interface DbTable<T>
{
	public T lookup(String key);
}
