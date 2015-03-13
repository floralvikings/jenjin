package com.jenjinstudios.server.authentication;

/**
 * The {@code Authenticator} class is responsible for retrieving and updating users in the database specified by a
 * supplied JDBC {@code Connection}.
 */
public class BasicAuthenticator extends Authenticator<BasicUser>
{
	/**
	 * Construct a new Authenticator with the given database Connection and UserLookup.
	 *
	 * @param userLookup The UserLookup used to find and update users.
	 */
	public BasicAuthenticator(UserLookup<BasicUser> userLookup) {
		super(userLookup);
	}

}
