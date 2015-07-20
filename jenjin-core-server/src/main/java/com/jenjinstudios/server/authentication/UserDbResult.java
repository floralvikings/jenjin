package com.jenjinstudios.server.authentication;

import java.util.Collections;
import java.util.Map;

/**
 * Very simple class that simply contains a freshly deserialized user and a map of values containing its properties.
 *
 * @author Caleb Brinkman
 */
public class UserDbResult<T extends User>
{
	private final T user;
	private final Map<String, String> properties;

	/**
	 * Construct a new UserDbResult with the given user and properties.
	 *
	 * @param user The user.
	 * @param properties The properties.
	 */
	public UserDbResult(T user, Map<String, String> properties) {
		this.user = user;
		this.properties = properties;
	}

	/**
	 * Get the user that has been deserialized without set properties.
	 *
	 * @return The user that has been deserialized without set properties.
	 */
	public T getUser() { return user; }

	/**
	 * Get the collection of properties to be set on the user.
	 *
	 * @return A Map containing string pairs of properties to be set on the user.
	 */
	public Map<String, String> getProperties() { return Collections.unmodifiableMap(properties); }
}
